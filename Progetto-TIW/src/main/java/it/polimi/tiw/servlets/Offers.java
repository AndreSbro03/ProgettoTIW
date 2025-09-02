package it.polimi.tiw.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.Offer;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.generals.AuctionState;
import it.polimi.tiw.generals.AuctionUtils;

/**
 * Servlet implementation class Offer
 */
@WebServlet("/offers")
public class Offers extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public void init() throws UnavailableException {
		connection = AuctionUtils.openDbConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		Integer auctionId;
		try {
			auctionId = Integer.parseInt(req.getParameter("auctionId"));
		} catch (NumberFormatException | NullPointerException e) {
			auctionId = null;
		}

		Auction auction = null;
		ArrayList<Offer> offers = null;
		req.setAttribute("isUserOwner", false);

		if (auctionId != null) {

			/**
			 * Get all the datas about the requested auction, even all the offers
			 */
			AuctionDAO ad = new AuctionDAO(connection);
			try {
				auction = ad.getAuctionFromId(auctionId);
			} catch (Exception e) {
				res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				res.getWriter().println("Server error");
				return;
			}

			if (auction != null) {

				/**
				 * The auction is finished?
				 */
				if (LocalDateTime.now().isAfter(auction.getDateTime())) {
					if (auction.getFinished())
						auction.setState(AuctionState.CLOSED);
					else
						auction.setState(AuctionState.EXPIRED);
				} else {
					auction.setState(AuctionState.OPEN);
				}

				/**
				 * Get auction offers
				 */
				try {
					offers = ad.getAllAuctionOffers(auctionId);
				} catch (SQLException e) {
					res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					res.getWriter().println("Server error");
					return;
				}
			}
		}

		/**
		 * Show the page with all the details
		 */
		String path = "/offer.jsp";
		req.setAttribute("auction", auction);
		req.setAttribute("offers", offers);
		RequestDispatcher dispatcher = req.getRequestDispatcher(path);
		dispatcher.forward(req, res);

	}

	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException ingore) {
		}
	}


}
