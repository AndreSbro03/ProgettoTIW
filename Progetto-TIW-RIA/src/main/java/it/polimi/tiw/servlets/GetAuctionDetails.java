package it.polimi.tiw.servlets;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.generals.AuctionState;
import it.polimi.tiw.generals.AuctionUtils;
import it.polimi.tiw.generals.LocalDateTimeAdapter;

/**
 * Servlet implementation class AuctionDetails
 */
@WebServlet("/get-auction-details")
public class GetAuctionDetails extends HttpServlet {
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
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.getWriter().println("Incorrect param values");
			return;
		}

		Auction auction = null;

		if (auctionId != null) {

			/**
			 * Get all the datas about the requested auction, even all the offers
			 */
			AuctionDAO ad = new AuctionDAO(connection);
			try {
				auction = ad.getAuctionFromId(auctionId);
			} catch (Exception e) {
				e.printStackTrace(); // Debug
				res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				res.getWriter().println("Server error");
				return;
			}

			if (auction != null) {

				/**
				 * The auction is finished
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
					auction.setOffers(ad.getAllAuctionOffers(auctionId));
				} catch (SQLException e) {
					res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					res.getWriter().println("Server error");
					return;
				}
			} else {
				// Item not found
				res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				res.getWriter().println("No auction with id:" + auctionId);
				return;
			}
		}

		/**
		 * Return the auction as json.
		 */
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
		String json = gson.toJson(auction);
		
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(json);

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
