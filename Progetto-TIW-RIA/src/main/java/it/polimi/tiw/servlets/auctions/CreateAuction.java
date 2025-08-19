package it.polimi.tiw.servlets.auctions;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.polimi.tiw.beans.Item;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDataDAO;
import it.polimi.tiw.generals.AuctionUtils;

/**
 * Servlet implementation class CreateAuction
 */
@WebServlet("/create-auction")
public class CreateAuction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String formPath = "create-auction.jsp";
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
			res.sendRedirect("login.jsp");
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
			e.printStackTrace();
		}
		
		/**
		 * Pass all the items to the sell-list page
		 */
		res.setContentType("text/plain");
		req.setAttribute("items", items);
		RequestDispatcher dispatcher = req.getRequestDispatcher(formPath);
		dispatcher.forward(req, res);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
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
