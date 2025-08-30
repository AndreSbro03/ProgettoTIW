package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.generals.AuctionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class CloseAuction
 */
@WebServlet("/close-auction")
@MultipartConfig
public class CloseAuction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

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
		String aId = req.getParameter("auctionId");
		Integer auctionId = null;
		try {
			auctionId = Integer.parseInt(aId);
		} catch (Exception e) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.getWriter().println("Input not valid");
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

		if (auction == null) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.getWriter().println("No such auction");
			return;
		}

		/**
		 * Check if the user is the owner of the auction
		 */
		if (!user.getUsername().equals(auction.getUsername())) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.getWriter().println("This is not your auction");
			return;
		}

		/**
		 * The auction must be expired
		 */
		if (!LocalDateTime.now().isAfter(auction.getDateTime())) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.getWriter().println("The auction must be expired");
			return;
		}

		/**
		 * The auction must not be already closed
		 */
		if (auction.getFinished()) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.getWriter().println("The auction is alredy closed");
			return;
		}

		try {
			adao.markAuctionAsFinished(auction.getId());
		} catch (SQLException e) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.getWriter().println("Server error");
			return;
		}

		res.setStatus(HttpServletResponse.SC_OK);
		res.getWriter().println("Auction closed successfully");
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