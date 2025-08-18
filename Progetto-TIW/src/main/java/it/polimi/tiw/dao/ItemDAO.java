package it.polimi.tiw.dao;
import java.sql.*;
import java.util.List;

public class ItemDAO  {
	private Connection connection;
	
	public ItemDAO(Connection connection) {
		this.connection = connection;
	}
	
	public float getItemsPrice(List<Integer> ids) throws SQLException {
		/**
		 * Create the query filling ? for each id.
		 */
		StringBuilder query = new StringBuilder("SELECT SUM(price) as tot FROM item WHERE itemID IN (");
		for (int i = 0; i < ids.size(); i++) {
		    query.append("?");
		    if (i < ids.size() - 1) query.append(", ");
		}
		query.append(")");
		
		/**
		 * Prepare the statement and fill it with real ids.
		 */
		PreparedStatement ps = connection.prepareStatement(query.toString());
	    for (int i = 0; i < ids.size(); i++) {
	        ps.setInt(i + 1, ids.get(i));
	    }
	    
	    /**
	     * Execute the query and return the first result, since is a sum it will be the only one.
	     */
		ResultSet rs = ps.executeQuery();
		while(rs.next()) return rs.getInt("tot");
		throw new RuntimeException("Item not found");
	}
	
	public void addItemsToAuction(int auctionID, List<Integer> ids) throws SQLException {
		/**
		 * Create the query filling ? for each id.
		 */
		StringBuilder query = new StringBuilder("UPDATE item SET auctionID = ? WHERE itemID IN (");
		for (int i = 0; i < ids.size(); i++) {
		    query.append("?");
		    if (i < ids.size() - 1) query.append(", ");
		}
		query.append(")");
		
		/**
		 * Prepare the statement and fill it with real ids.
		 */
		PreparedStatement ps = connection.prepareStatement(query.toString());
		ps.setInt(1, auctionID);
	    for (int i = 0; i < ids.size(); i++) {
	        ps.setInt(i + 2, ids.get(i));
	    }
	    
	    /**
	     * Execute the query and return the first result, since is a sum it will be the only one.
	     */
		ps.executeUpdate();
		ps.close();
	}
	
}