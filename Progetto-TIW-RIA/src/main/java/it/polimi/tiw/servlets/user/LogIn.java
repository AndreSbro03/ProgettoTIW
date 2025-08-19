package it.polimi.tiw.servlets.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;


import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDataDAO;
import it.polimi.tiw.generals.AuctionUtils;

/**
 * Servlet implementation class LogIn
 */
@WebServlet("/login")
@MultipartConfig
public class LogIn extends HttpServlet {
	private static final long serialVersionUID = 1L;

	Connection connection;

	public void init() throws ServletException {
		connection = AuctionUtils.openDbConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		/**
		 * Get user name and password form the form fields
		 */
		String username = req.getParameter("username");
		String password = req.getParameter("password");

		/**
		 * Check that they are both not null
		 */
		if (username == null || password == null || username.isEmpty() || password.isEmpty() ) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.getWriter().println("Fill all fields");
			return;
		}

		UserDataDAO udd = new UserDataDAO(connection);
		User user = null;
		try {
			user = udd.logIn(username, password);
		} catch (SQLException e1) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.getWriter().println("Server error");
			return;
		}

		/**
		 * If the user exists, add info to the session and go to home page, otherwise
		 * return an error status code and message
		 */
		if (user == null) {
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			res.getWriter().println("Incorrect credentials");
		} else {
			/**
			 * Save user in session
			 */
			req.getSession().setAttribute("user", user);
			res.setStatus(HttpServletResponse.SC_OK);
			res.setContentType("application/json");
			res.setCharacterEncoding("UTF-8");
			res.getWriter().println(user);
		}

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
