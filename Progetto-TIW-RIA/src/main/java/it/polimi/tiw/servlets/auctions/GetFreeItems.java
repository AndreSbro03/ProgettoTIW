package it.polimi.tiw.servlets.auctions;

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
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Item;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDataDAO;
import it.polimi.tiw.generals.AuctionUtils;
import it.polimi.tiw.generals.LocalDateTimeAdapter;

/**
 * Servlet implementation class CreateAuction
 */
@WebServlet("/get-free-items")
public class GetFreeItems extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection connection;

	public void init() throws ServletException {
		connection = AuctionUtils.openDbConnection(getServletContext());
	}
       
    
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
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
		 * Get all the items associated with the user that are not in an auction yet
		 */
		UserDataDAO udd = new UserDataDAO(connection);
		ArrayList<Item> items = null;
		try {
			items = udd.getUserItems(user.getId(), true);
		} catch (SQLException e) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.getWriter().println("Server error");
			return;
		}
		
		/**
		 * Pass all the items to the sell-list page
		 */
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
		String json = gson.toJson(items);
		
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(json);
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
