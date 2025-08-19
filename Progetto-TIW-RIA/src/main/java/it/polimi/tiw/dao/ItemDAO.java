package it.polimi.tiw.dao;

import java.sql.*;
import java.util.List;

public class ItemDAO {
	private Connection connection;

	public ItemDAO(Connection connection) {
		this.connection = connection;
	}

	public int addItem(int userId, String name, String descr, float price) throws SQLException {
		String query = "INSERT INTO astemi.item (userID, name, descr, price) VALUES (?, ?, ?, ?)";
		PreparedStatement statement = null;
		ResultSet generatedKeys = null;
		int key = 0;
		try {
			statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			statement.setInt(1, userId);
			statement.setString(2, name);
			statement.setString(3, descr);
			statement.setFloat(4, price);
			statement.executeUpdate();

			/**
			 * Retrieve the generated key
			 */
			generatedKeys = statement.getGeneratedKeys();
			if (generatedKeys.next()) {
				key = generatedKeys.getInt(1);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (statement != null)
					statement.close();
				if (generatedKeys != null)
					generatedKeys.close();
			} catch (Exception e1) {
				throw e1;
			}
		}
		return key;
	}

	public float getItemsPrice(List<Integer> ids) throws SQLException {
		/**
		 * Create the query filling ? for each id.
		 */
		StringBuilder query = new StringBuilder("SELECT SUM(price) as tot FROM item WHERE itemID IN (");
		for (int i = 0; i < ids.size(); i++) {
			query.append("?");
			if (i < ids.size() - 1)
				query.append(", ");
		}
		query.append(")");

		PreparedStatement ps = null;
		ResultSet rs = null;
		float out = 0.f;

		try {
			/**
			 * Prepare the statement and fill it with real ids.
			 */
			ps = connection.prepareStatement(query.toString());
			for (int i = 0; i < ids.size(); i++) {
				ps.setInt(i + 1, ids.get(i));
			}

			/**
			 * Execute the query and return the first result, since is a sum it will be the
			 * only one.
			 */
			rs = ps.executeQuery();
			if (rs.next())
				out = rs.getInt("tot");

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

	public void addItemsToAuction(int auctionID, List<Integer> ids) throws SQLException {
		/**
		 * Create the query filling ? for each id.
		 */
		StringBuilder query = new StringBuilder("UPDATE item SET auctionID = ? WHERE itemID IN (");
		for (int i = 0; i < ids.size(); i++) {
			query.append("?");
			if (i < ids.size() - 1)
				query.append(", ");
		}
		query.append(")");

		PreparedStatement ps = null;

		try {
			/**
			 * Prepare the statement and fill it with real ids.
			 */
			ps = connection.prepareStatement(query.toString());
			ps.setInt(1, auctionID);
			for (int i = 0; i < ids.size(); i++) {
				ps.setInt(i + 2, ids.get(i));
			}

			/**
			 * Execute the query and return the first result, since is a sum it will be the
			 * only one.
			 */
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
}