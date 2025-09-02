package it.polimi.tiw.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.Item;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.generals.AuctionUtils;
import it.polimi.tiw.generals.LocalDateTimeAdapter;

@WebServlet("/get-auctions")
@MultipartConfig
public class GetAuctions extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection connection;
	
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
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String word = request.getParameter("key-word");
		
		/**
		 * Get all auctions
		 */
		AuctionDAO ad = new AuctionDAO(connection);
		ArrayList<Auction> auctions = null;
		ArrayList<Auction> out = null;
		try {
			auctions = ad.getAllAuction();
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Server error");
			return;
		}
		
		if(word != null) {
			out = filterAuction(auctions, word);
		} else {
			out = auctions;
		}
		
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
		String json = gson.toJson(out);
		
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
