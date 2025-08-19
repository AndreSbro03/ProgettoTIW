package it.polimi.tiw.servlets;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.Item;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDataDAO;
import it.polimi.tiw.generals.AuctionUtils;
import it.polimi.tiw.generals.LocalDateTimeAdapter;

@WebServlet("/get-user-data")
public class GetUserData extends HttpServlet {
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
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			res.getWriter().println("You must be logged");
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
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.getWriter().println("Server error");
			return;
		}
		
		/**
		 * Pass all the items to the sell-list page
		 */
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
		Map<String, Object> all = new HashMap<>();
		all.put("items", items);
		all.put("closedAuctions", closedAuctions);
		all.put("openAuctions", openAuctions);
		String allJson = gson.toJson(all);
		
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(allJson);
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
