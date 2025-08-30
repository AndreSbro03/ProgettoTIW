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
 * Servlet implementation class CloseAuction
 */
@WebServlet("/close-auction")
public class CloseAuction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	public void init() throws ServletException {
		connection = AuctionUtils.openDbConnection(getServletContext());
	}
       
	private void sendErrorMessage(HttpServletRequest req, HttpServletResponse res, String msg, int auctionId) throws IOException {
		HttpSession session = req.getSession(true);
		session.setAttribute("errorMsg", msg);
		res.sendRedirect("auction-details?auctionId=" + auctionId);
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
		String aId = req.getParameter("auctionId");
		Integer auctionId = null;
		try {
			auctionId = Integer.parseInt(aId);
		} catch (Exception e) {
			sendErrorMessage(req, res, "Input not valid", 0);
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
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.getWriter().println("Server error");
			return;
		}
		
		if(auction == null) {
			sendErrorMessage(req, res, "No such auction", 0);
			return;
		}
		
		/**
		 * Check if the user is the owner of the auction
		 */
		if(!user.getUsername().equals(auction.getUsername())) {
			sendErrorMessage(req, res, "This is not your auction", auction.getId());
			return;
		}
		
		/**
		 * The auction must be expired
		 */
		if(!LocalDateTime.now().isAfter(auction.getDateTime())) {
			sendErrorMessage(req, res, "The auction must be expired", auction.getId());
			return;
		}
		
		/**
		 * The auction must not be already closed
		 */
		if(auction.getFinished()) {
			sendErrorMessage(req, res, "The auction is alredy closed", auction.getId());
			return;
		}
		
		try {
			adao.markAuctionAsFinished(auction.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		res.sendRedirect("auction-details?auctionId=" + auction.getId());
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
