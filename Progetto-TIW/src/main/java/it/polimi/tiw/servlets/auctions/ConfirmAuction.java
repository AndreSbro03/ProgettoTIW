package it.polimi.tiw.servlets.auctions;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.dao.ItemDAO;
import it.polimi.tiw.dao.UserDataDAO;
import it.polimi.tiw.generals.AuctionUtils;

@WebServlet("/confirm-auction")
public class ConfirmAuction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection connection;

	public void init() throws ServletException {
		connection = AuctionUtils.openDbConnection(getServletContext());
	}

	private void sendErrorMessage(HttpServletRequest req, HttpServletResponse res, String msg) throws IOException {
		 HttpSession session = req.getSession(true);
		 session.setAttribute("errorMsg", msg); 
		 res.sendRedirect("create-auction");
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
		 * Get the form parameters
		 */
		String[] ids = req.getParameterValues("item_ids");
		String min_increment = req.getParameter("min_incr");
		String date = req.getParameter("date");
		String time = req.getParameter("time");

		if (ids == null || min_increment == null || date == null || time == null) {
			sendErrorMessage(req, res, "All fileds must be filled");
			return;
		}


		UserDataDAO udd = new UserDataDAO(connection);

		/**
		 * Check that all the ids are user items and collect the price.
		 */
		ArrayList<Integer> itemIds = new ArrayList<Integer>();
		
		for (String id : ids) {
			Integer itemId;
			try {
				itemId = Integer.parseInt(id);
			} catch (NullPointerException | NumberFormatException e) {
				sendErrorMessage(req, res, "Id not valid");
				return;
			}
			try {
				/**
				 * If one of the ids cannot be parsed or if one of them is not a current user
				 * property return an error
				 */
				Boolean isUserItem = udd.isUserItem(user.getId(), itemId);

				if (itemId == null || !isUserItem) {
					sendErrorMessage(req, res, "Id not valid");
					return;
				}
				itemIds.add(itemId);
			} catch (SQLException e) {
				res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				res.getWriter().println("Server error");
				return;
			}
		}

		/**
		 * Sum the prices
		 */
		ItemDAO idao = new ItemDAO(connection);
		float price = 0;
		try {
			price = idao.getItemsPrice(itemIds);
		} catch (SQLException e) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.getWriter().println("Server error");
			return;
		}

		/**
		 * Now all the ids must be valid check the rest of the data
		 */
		Integer min_incr;
		try {
			min_incr = Integer.parseInt(min_increment);
		} catch (NullPointerException | NumberFormatException e) {
			sendErrorMessage(req, res, "Invalid min incr");
			return;
		}
		
		if (min_incr == null || min_incr < 1) {
			sendErrorMessage(req, res, "Minimum increment must be grater than 1");
			return;
		}

		/**
		 * Check if the date is in the past and convert the fields into one
		 */
		LocalDateTime dateTime;
		try {
			/**
			 * Parse local date and time
			 */
			dateTime = LocalDateTime.of(LocalDate.parse(date), LocalTime.parse(time));
			/**
			 * Check if the dateTime is in the future
			 */
			LocalDateTime now = LocalDateTime.now();
			if (!dateTime.isAfter(now)) {
				sendErrorMessage(req, res, "Date time must be in the future");
				return;
			}
		} catch (DateTimeParseException e) {
			sendErrorMessage(req, res, "Date time format not valid");
			return;
		}

		/**
		 * Add the auction, to ensure the result we use the commit db property.
		 */
		AuctionDAO ad = new AuctionDAO(connection);
		try {
			connection.setAutoCommit(false);
			int auctionID = ad.addAuction(user.getId(), price, min_incr, dateTime);
			idao.addItemsToAuction(auctionID, itemIds);
			connection.commit();
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				// ingore
			}
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.getWriter().println("Server error");
			return;
		}

		/**
		 * If nothing failed
		 */
		res.sendRedirect("sell");
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
