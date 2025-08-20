package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Auction;

import it.polimi.tiw.beans.Item;
import it.polimi.tiw.beans.User;

import java.sql.*;
import java.util.ArrayList;

public class UserDataDAO {
	private Connection connection;

	public UserDataDAO(Connection connection) {
		this.connection = connection;
	}

	public int addUser(String username, String password, String name, String surname, String address)
			throws SQLException {
		String query = "INSERT INTO user (username, password, name, surname, address) VALUES (?,?,?,?,?)";

		PreparedStatement statement = null;
		ResultSet rs = null;
		int key = 0;
		try {

			statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			statement.setString(1, username);
			statement.setString(2, password);
			statement.setString(3, name);
			statement.setString(4, surname);
			statement.setString(5, address);

			statement.executeUpdate();

			/**
			 * Retrieve the generated key
			 */
			rs = statement.getGeneratedKeys();
			if (rs.next()) {
				key = rs.getInt(1);
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (statement != null)
					statement.close();
				if (rs != null)
					rs.close();
			} catch (Exception e1) {
				throw e1;
			}
		}

		return key;
	}

	/**
	 * Return all the items that a user have loaded, if nothing is found return an
	 * empty list.
	 * 
	 * @param freeFromAuction if {@code true} only the items not in a auction will
	 *                        be returned.
	 * @return all the requested items.
	 * @exception SQLException if some errors occurred during the db connection.
	 */
	public ArrayList<Item> getUserItems(int userId, boolean freeFromAuction) throws SQLException {
		/**
		 * Get all the items associated with the user
		 */
		String query;
		if (freeFromAuction) {
			query = "SELECT * FROM item WHERE userID = " + userId + " AND auctionID IS NULL;";
		} else {
			query = "SELECT * FROM item WHERE userID = " + userId + ";";
		}

		PreparedStatement statement = null;
		ResultSet result = null;
		ArrayList<Item> items = new ArrayList<Item>();

		try {
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();

			while (result.next()) {
				int id = result.getInt("itemID");
				Integer auctionId = result.getInt("auctionID");
				String name = result.getString("name");
				String descr = result.getString("descr");
				String price = result.getString("price");

				try {
					items.add(new Item(id, auctionId, name, descr, price));
				} catch (Exception ign) {
					// Ignore the data must be already parsed if inside the db
				}
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (statement != null)
					statement.close();
			} catch (Exception e1) {
				throw e1;
			}
		}

		return items;
	}

	/**
	 * Return true if the item is a user item.
	 * 
	 * @param userId the user id
	 * @param itemId the item id
	 * @return true if the item is a user item.
	 * @throws SQLException
	 * @throws SQLException if some errors occurred during the db connection.
	 */
	public boolean isUserItem(int userId, int itemId) throws SQLException {
		String query = "SELECT COUNT(*) AS num FROM user AS u JOIN item AS i WHERE u.userID = ? AND i.itemID = ?;";

		PreparedStatement statement = null;
		ResultSet result = null;
		boolean out = false;

		try {
			statement = connection.prepareStatement(query);
			statement.setInt(1, userId);
			statement.setInt(2, itemId);
			result = statement.executeQuery();

			while (result.next()) {
				int num = result.getInt("num");
				out = num > 0;
			}
			if (statement != null)
				statement.close();
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (statement != null)
					statement.close();
			} catch (Exception e1) {
				throw e1;
			}
		}

		return out;
	}

	/**
	 * Get all user auction, if closed is false than only the open ones if true only
	 * the closed ones.
	 */
	public ArrayList<Auction> getUserAuctions(User user, boolean closed) throws SQLException {
		String query = "SELECT a.auctionID, init_price, min_incr, expiration, finished, "
				+ " itemID, i.name as item_name, i.descr as item_descr, i.price as item_price, "
				+ "lst_offerID, u.username AS buyer_name, o.price as offer_price "
				+ "FROM auction AS a LEFT JOIN item AS i ON i.auctionID = a.auctionID LEFT JOIN offer AS o ON a.lst_offerID = o.offerID LEFT JOIN user AS u ON o.userID = u.userID "
				+ "WHERE a.userID = ? AND a.finished = ? " + "ORDER BY a.expiration ASC;";

		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Auction> out = new ArrayList<Auction>();
		try {
			/*
			 * Execute the query
			 */
			ps = connection.prepareStatement(query);
			ps.setInt(1, user.getId());
			ps.setBoolean(2, closed);
			rs = ps.executeQuery();
			out = AuctionDAO.extractAuction(rs, user.getUsername());

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

	public User getUserFromUserName(String username) throws SQLException {
		/**
		 * Query to get the user data
		 */
		String query = "SELECT userID, name, surname, username, address FROM user WHERE username = ?";

		PreparedStatement statement = null;
		ResultSet results = null;
		User user = null;

		try {

			statement = connection.prepareStatement(query);
			statement.setString(1, username);
			results = statement.executeQuery();

			/**
			 * If there are no elements return an error else create an User obj and save it
			 * in the session.
			 */
			if (results.next()) {
				int id = results.getInt("userID");
				String resName = results.getString("name");
				String resSurname = results.getString("surname");
				String resUsername = results.getString("username");
				String resAddress = results.getString("address");
				user = new User(id, resName, resSurname, resUsername, resAddress);
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (statement != null)
					statement.close();
				if (results != null)
					results.close();
			} catch (Exception e1) {
				throw e1;
			}
		}
		return user;
	}

	public User logIn(String username, String password) throws SQLException, RuntimeException {

		/**
		 * Query to get the user data
		 */
		String query = "SELECT userID, name, surname, username, address FROM user WHERE username = ? AND password = ?;";

		PreparedStatement statement = null;
		ResultSet results = null;
		User user = null;

		try {

			statement = connection.prepareStatement(query);

			statement.setString(1, username);
			statement.setString(2, password);

			results = statement.executeQuery();

			/**
			 * If there are no elements return an error else create an User obj and save it
			 * in the session.
			 */
			if (results.next()) {
				int id = results.getInt("userID");
				String resName = results.getString("name");
				String resSurname = results.getString("surname");
				String resUsername = results.getString("username");
				String resAddress = results.getString("address");
				user = new User(id, resName, resSurname, resUsername, resAddress);
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (statement != null)
					statement.close();
				if (results != null)
					results.close();
			} catch (Exception e1) {
				throw e1;
			}
		}
		return user;
	}

}
