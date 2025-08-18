package it.polimi.tiw.servlets;

import jakarta.servlet.RequestDispatcher;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.*;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.generals.AuctionUtils;

@WebServlet("/buy")
public class Buy extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	public void init() throws ServletException {
		connection = AuctionUtils.openDbConnection(getServletContext());
	}
	
	private boolean isAuctionValid(Auction auctions, String word){
		for(Item i : auctions.getItems()) {
			if(i.getName().toLowerCase().contains(word.toLowerCase())) return true;
			if(i.getDescr().toLowerCase().contains(word.toLowerCase())) return true;
		}
		return false;
	}
	
	private ArrayList<Auction> filterAuction(List<Auction> auctions, String word) {
		
		ArrayList<Auction> out = new ArrayList<Auction>(); 
		for(Auction a : auctions) {
			if(isAuctionValid(a, word)) {
				out.add(a);
			}
		}
		return out;
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String word = req.getParameter("key-word");
		
		/**
		 * Get all auctions
		 */
		AuctionDAO ad = new AuctionDAO(connection);
		ArrayList<Auction> auctions = new ArrayList<Auction>();
		ArrayList<Auction> out = null;
		try {
			auctions = ad.getAllAuction();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(word != null) {
			out = filterAuction(auctions, word);
		} else {
			out = auctions;
		}
		
		res.setContentType("text/plain");
		String path = "/buylist.jsp";
		req.setAttribute("number", out.size());
		req.setAttribute("auctions", out);
		RequestDispatcher dispatcher = req.getRequestDispatcher(path);
		dispatcher.forward(req, res);
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
