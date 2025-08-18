package it.polimi.tiw.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.generals.AuctionUtils;
import it.polimi.tiw.generals.LocalDateTimeAdapter;

@WebServlet("/get-auctions")
public class GetAuctions extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection connection;
	
	public void init() throws ServletException {
		connection = AuctionUtils.openDbConnection(getServletContext());
	}
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		/**
		 * Get all auctions
		 */
		AuctionDAO ad = new AuctionDAO(connection);
		ArrayList<Auction> auctions = new ArrayList<Auction>();
		try {
			auctions = ad.getAllAuction();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
		String json = gson.toJson(auctions);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		
	}
	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}

}
