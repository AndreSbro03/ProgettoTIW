package it.polimi.tiw.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.generals.AuctionUtils;

/**
 * Servlet implementation class ConfirmOffer
 */
@WebServlet("/confirm-offer")
@MultipartConfig
public class ConfirmOffer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection connection;

	public void init() throws ServletException {
		connection = AuctionUtils.openDbConnection(getServletContext());
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		/**
		 * First check if there is an user logged in. If not redirect to the login page.
		 */
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			res.getWriter().println("You must be logged");
			return;
		}
		
		/**
		 * Get all the parameters passed in the form
		 */
		String p = req.getParameter("import");
		String aId = req.getParameter("auctionId");
		
		/**
		 * Check if the input is valid
		 */
		Float price;
		Integer auctionId;
		try {
			price = Float.parseFloat(p);
			auctionId = Integer.parseInt(aId);
		} catch (Exception e) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.getWriter().println("Incorrect param values");
			return;
		}
	
		
		/**
		 * Get auction data
		 */
		AuctionDAO adao = new AuctionDAO(connection);
		Auction auction;
		try {
			auction = adao.getAuctionFromId(auctionId);
		} catch (Exception e) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.getWriter().println("Server error");
			return;
		}
		
		if(auction == null) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.getWriter().println("Auction not found");
			return;
		}
		
		/**
		 * Check if the user is the auction owner
		 */
		if(user.getUsername().equals(auction.getUsername())) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.getWriter().println("You can't put an offer on your auction");
			return;
		}
		
		/**
		 * Check if the auction is finished or expired
		 */
		if(auction.getFinished()) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.getWriter().println("Auction already ended");
			return;
		}
		if(LocalDateTime.now().isAfter(auction.getDateTime())) {
			try {
				adao.markAuctionAsFinished(auction.getId());
			} catch (SQLException e) {
				res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				res.getWriter().println("Server error");
				return;
			}
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.getWriter().println("Auction already ended");
			return;
		}
		
		/**
		 * Check if the price is valid
		 */
		float currPrice = (auction.getLstOffer() == null) ? auction.getInitPrice() : auction.getLstOffer().getPrice();
		if(price < currPrice + auction.getMinIncr()) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.getWriter().println("Offer should be bigger than current price + minimum increment");
			return;
		}
		
		/**
		 * If everything is good add the offer to the db
		 */
		try {
			adao.offerToAuction(user.getId(), auction.getId(), price);
		} catch (SQLException e) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.getWriter().println("Server error");
			return;
		}
		
		res.setStatus(HttpServletResponse.SC_OK);
		res.getWriter().println("Item added successfully");
		return;
		
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
