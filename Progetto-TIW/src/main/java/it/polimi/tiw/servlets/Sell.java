package it.polimi.tiw.servlets;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.Item;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDataDAO;
import it.polimi.tiw.generals.AuctionUtils;

@WebServlet("/sell")
public class Sell extends HttpServlet {
	private static final long serialVersionUID = 1L;

	Connection connection;

	public void init() throws ServletException {
		connection = AuctionUtils.openDbConnection(getServletContext());
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		/**
		 * Get session user, if not present redirect to login
		 */
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			res.sendRedirect("login.jsp");
			return;
		}

		/**
		 * Get all the data associated with the user
		 */
		UserDataDAO udd = new UserDataDAO(connection);
		ArrayList<Item> items;
		ArrayList<Auction> closedAuctions;
		ArrayList<Auction> openAuctions;
		try {
			/**
			 * If this functions don't find anything they return an empty list.
			 */
			items = udd.getUserItems(user.getId(), true);
			closedAuctions = udd.getUserAuctions(user, true);
			openAuctions = udd.getUserAuctions(user, false);
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		
		/**
		 * Pass all the items to the sell-list page
		 */
		res.setContentType("text/plain");
		String path = "/sell-list.jsp";
		req.setAttribute("items", items);
		req.setAttribute("closedAuctions", closedAuctions);
		req.setAttribute("openAuctions", openAuctions);
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
