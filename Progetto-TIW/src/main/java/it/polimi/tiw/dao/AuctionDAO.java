package it.polimi.tiw.dao;

import java.sql.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;

import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.Item;
import it.polimi.tiw.beans.Offer;

public class AuctionDAO {
	private Connection connection;

	public AuctionDAO(Connection connection) {
		this.connection = connection;
	}

	public int addAuction(int userId, float initPrice, int minIncr, LocalDateTime ldt) throws SQLException {
		String query = "INSERT INTO auction (userID, init_price, min_incr, expiration) VALUES (?,?,?,?)";
		PreparedStatement ps = null;
		ResultSet rs = null;
		int auctionId = 0; 

		try {
			ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, userId);
			ps.setFloat(2, initPrice);
			ps.setInt(3, minIncr);
			ps.setTimestamp(4, Timestamp.valueOf(ldt));
			ps.executeUpdate();

			rs = ps.getGeneratedKeys();
			if(rs.next()) auctionId = rs.getInt(1);

		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e1) {
				throw e1;
			}
		}
	
		return auctionId;
	}

	/**
	 * Extract auction data from ResultSet. If the user name is provided that one
	 * will be use in all auction. Can be used if the query is used to retrieve all
	 * user auction.
	 * 
	 * @throws SQLException if something goes wrong with the query
	 */
	public static ArrayList<Auction> extractAuction(ResultSet rs, String username) throws SQLException {

		LinkedHashMap<Integer, Auction> map = new LinkedHashMap<Integer, Auction>();

		/**
		 * Iter all the results
		 */
		while (rs.next()) {
			int auctionId = rs.getInt("auctionID");
			Auction auction = map.get(auctionId);

			/**
			 * If it is the first time we find an auction create it and add it to the map.
			 */
			if (auction == null) {
				float initPrice = rs.getFloat("init_price");
				int minIncr = rs.getInt("min_incr");
				boolean finished = rs.getBoolean("finished");
				LocalDateTime expiration = rs.getTimestamp("expiration").toLocalDateTime();

				auction = new Auction(auctionId, initPrice, minIncr, finished, expiration);
				if (username == null)
					auction.setUsername(rs.getString("vendor_username"));
				else
					auction.setUsername(username);

				if (rs.getInt("lst_offerID") != 0) {
					Offer o = new Offer();
					o.setPrice(rs.getFloat("offer_price"));
					o.setUsername(rs.getString("buyer_name"));
					auction.setLstOffer(o);
				}

				map.put(auctionId, auction);
			}

			/**
			 * Add the item
			 */
			Item item = null;
			try {
				item = new Item(rs.getInt("itemID"), auctionId, rs.getString("item_name"), rs.getString("item_descr"),
						rs.getString("item_price"));
			} catch (Exception e) {
				// error can't happen here
				e.printStackTrace();
			}

			assert (item != null);
			auction.addItem(item);
		}

		return new ArrayList<>(map.values());

	}

	/**
	 * Return all the non finished auction.
	 * 
	 * @throws SQLException if something goes wrong with the query
	 */
	public ArrayList<Auction> getAllAuction() throws SQLException {
		String query = "SELECT a.auctionID, a.init_price, a.min_incr, a.expiration, a.finished, "
				+ "		u1.userID AS vendorID, u1.username AS vendor_username, "
				+ "        i.itemID, i.price AS item_price, i.name AS item_name, i.descr AS item_descr, "
				+ "        a.lst_offerID, o.price AS offer_price, u2.username AS buyer_name "
				+ "FROM auction AS a JOIN user AS u1 ON a.userID = u1.userID "
				+ "LEFT JOIN item AS i ON i.auctionID = a.auctionID LEFT JOIN offer AS o ON o.offerID = a.lst_offerID "
				+ "LEFT JOIN user AS u2 ON o.userID = u2.userID " 
				+ "WHERE a.finished = false AND a.expiration > CURRENT_TIMESTAMP "
				+ "ORDER BY a.expiration DESC;";

		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Auction> out = null;

		try {
			/*
			 * Execute the query
			 */
			ps = connection.prepareStatement(query);
			rs = ps.executeQuery();
			out = extractAuction(rs, null);

		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e1) {
				throw e1;
			}
		}

		return out;
	}

	/**
	 * Return the auction associated with the id.
	 * 
	 * @param auctionId the id of the desired auction.
	 * @return null if no there is no auction with the id requested.
	 * @throws Exception f something goes wrong with the query.
	 */
	public Auction getAuctionFromId(int auctionId) throws Exception {
		String query = "SELECT a.auctionID, a.init_price, a.min_incr, a.expiration, a.finished, "
				+ "		u1.userID AS vendorID, u1.username AS vendor_username, "
				+ "        i.itemID, i.price AS item_price, i.name AS item_name, i.descr AS item_descr, "
				+ "        a.lst_offerID, o.price AS offer_price, u2.username AS buyer_name "
				+ "FROM auction AS a JOIN user AS u1 ON a.userID = u1.userID "
				+ "JOIN item AS i ON a.auctionID = i.auctionID LEFT JOIN offer AS o ON o.offerID = a.lst_offerID "
				+ "LEFT JOIN user AS u2 ON o.userID = u2.userID " + "WHERE a.auctionID = ? " + "ORDER BY a.auctionID;";

		PreparedStatement ps = null;
		ResultSet rs = null;
		Auction out = null;

		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, auctionId);
			rs = ps.executeQuery();

			ArrayList<Auction> as = extractAuction(rs, null);
			try {
				out = as.getFirst();
			} catch (NoSuchElementException ign) {
				// ignore
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e1) {
				throw e1;
			}
		}

		return out;
	}

	public void offerToAuction(int userId, int auctionId, float price) throws SQLException {
		String query = "INSERT INTO offer (userID, auctionID, price) VALUES (?, ?, ?);";

		PreparedStatement ps = null;

		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, userId);
			ps.setInt(2, auctionId);
			ps.setFloat(3, price);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e1) {
				throw e1;
			}
		}
	}
	
	public void markAuctionAsFinished(int auctionId) throws SQLException {
		String query = "UPDATE auction SET finished = true WHERE auctionID = ?;";
		PreparedStatement ps = null;

		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, auctionId);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (ps != null)
				ps.close();
		}

	}
	
	public ArrayList<Offer> getAllAuctionOffers(int auctionId) throws SQLException {
		String query = "SELECT price, username, offer_time "
				+ "FROM offer AS o LEFT JOIN user AS u ON o.userID = u.userID "
				+ "WHERE auctionID = ? "
				+ "ORDER BY offer_time DESC, price DESC;";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Offer> offers = new ArrayList<Offer>();
		
		try {
			ps = connection.prepareStatement(query);
			ps.setInt(1, auctionId);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				Offer o = new Offer();
				o.setPrice(rs.getFloat("price"));
				o.setUsername(rs.getString("username"));
				LocalDateTime ldt = rs.getTimestamp("offer_time").toLocalDateTime();
				o.setDateTime(ldt);
				offers.add(o);
			}
			
		} catch (SQLException e) {
			throw e;
		} finally {
			if(ps != null) ps.close();
			if(rs != null) rs.close();
		}
		
		return offers;
		
	}
	
	public ArrayList<Auction> getAucitonWonBy(String username) throws SQLException{
		String query = "SELECT a.auctionID, a.init_price, a.min_incr, a.expiration, a.finished, "
				+ "						u1.userID AS vendorID, u1.username AS vendor_username, "
				+ "				        i.itemID, i.price AS item_price, i.name AS item_name, i.descr AS item_descr, "
				+ "				        a.lst_offerID, o.price AS offer_price, u2.username AS buyer_name "
				+ "FROM auction as a JOIN user AS u1 ON a.userID = u1.userID JOIN offer AS o ON a.lst_offerID = o.offerID JOIN user AS u2 ON o.userID = u2.userID JOIN item AS i ON i.auctionID = a.auctionID "
				+ "WHERE u2.username = ? AND a.finished = true;";

		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Auction> out = null;

		try {
			/*
			 * Execute the query
			 */
			ps = connection.prepareStatement(query);
			ps.setString(1, username);
			rs = ps.executeQuery();
			out = extractAuction(rs, null);

		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e1) {
				throw e1;
			}
		}

		return out;
	}

}