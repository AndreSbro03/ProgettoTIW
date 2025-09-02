package it.polimi.tiw.servlets;

import jakarta.servlet.ServletException;

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
public class ConfirmOffer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection connection;

	public void init() throws ServletException {
		connection = AuctionUtils.openDbConnection(getServletContext());
	}
	
	private void sendErrorMessage(HttpServletRequest req, HttpServletResponse res, String msg) throws IOException {
		HttpSession session = req.getSession(true);
		session.setAttribute("errorMsg", msg);
		String aId = req.getParameter("auctionId");
		res.sendRedirect("auction-details?auctionId="+aId);
	}
       
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		/**
		 * First check if there is an user logged in. If not redirect to the login page.
		 */
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			res.sendRedirect("login.jsp");
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
		Float price = null;
		Integer auctionId = null;
		try {
			price = Float.parseFloat(p);
			auctionId = Integer.parseInt(aId);
		} catch (Exception e) {
			sendErrorMessage(req, res, "Input not valid " + price + ", " + auctionId);
			return;
		}
	
		
		/**
		 * Get auction data
		 */
		AuctionDAO adao = new AuctionDAO(connection);
		Auction auction = null;
		try {
			auction = adao.getAuctionFromId(auctionId);
		} catch (Exception e) {
			sendErrorMessage(req, res, "SQL error:" + e.getMessage());
			return;
		}
		
		if(auction == null) {
			sendErrorMessage(req, res, "No such auction");
			return;
		}
		
		
		/**
		 * Check if the user is the auction owner
		 */
		if(user.getUsername().equals(auction.getUsername())) {
			sendErrorMessage(req, res, "You can't put an offer on your auction");
			return;
		}
		
		/**
		 * Check if the auction is finished or expired
		 */
		if(auction.getFinished()) {
			sendErrorMessage(req, res, "Auction already finished");
			return;
		}
		if(LocalDateTime.now().isAfter(auction.getDateTime())) {
			try {
				adao.markAuctionAsFinished(auction.getId());
			} catch (SQLException e) {
				sendErrorMessage(req, res, "SQL error:" + e.getMessage());
				return;
			}
			sendErrorMessage(req, res, "Auction already finished");
			return;
		}
		
		/**
		 * Check if the price is valid
		 */
		float currPrice = (auction.getLstOffer() == null) ? auction.getInitPrice() : auction.getLstOffer().getPrice();
		if(price < currPrice + auction.getMinIncr()) {
			sendErrorMessage(req, res, "Price not valid");
			return;
		}
		
		/**
		 * If everything is good add the offer to the db
		 */
		try {
			adao.offerToAuction(user.getId(), auction.getId(), price);
		} catch (SQLException e) {
			sendErrorMessage(req, res, "SQL error:" + e.getMessage());
			return;
		}
		
		res.sendRedirect("offers?auctionId="+aId);
		
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
