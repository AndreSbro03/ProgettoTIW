package it.polimi.tiw.servlets.user;

import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.*;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDataDAO;
import it.polimi.tiw.generals.AuctionUtils;

/**
 * Servlet implementation class SingUp
 */
@WebServlet("/singup")
@MultipartConfig
public class SingUp extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection connection;

	public void init() throws ServletException {
		connection = AuctionUtils.openDbConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		String name = req.getParameter("name");
		String surname = req.getParameter("surname");
		String address = req.getParameter("address");
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		String confirm_pw = req.getParameter("confirm-pw");


		/**
		 * Check every field is not null
		 */
		if(name == null || surname == null || address == null || username == null || password == null || confirm_pw == null) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.getWriter().println("All fields must be filled");
			return;
		}
		
		/**
		 * Check name all fields length (they should be equal the values in the db
		 */
		if(
				username.length() < 4 || username.length() > 32 ||
				name.length() < 1 || name.length() > 32 ||
				surname.length() < 1 || surname.length() > 32 ||
				address.length() < 4 || address.length() > 256 ||
				password.length() < 4 || password.length() > 32 
		) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.getWriter().println("Parameters too long or too short");
			return;
		}
		
		/**
		 * The passwords should be equals 
		 */
		if (!password.equals(confirm_pw)) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.getWriter().println("Passwords mismatch");
			return;
		}
		
		
		/**
		 * Add user to the db
		 */
		UserDataDAO udd = new UserDataDAO(connection);
		int uId = 0;
		try {
			uId = udd.addUser(username, password, name, surname, address);
		} catch (SQLException e) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.getWriter().println("Server error");
			return;
		}

		/**
		 * Save user in session
		 */
		User user = new User(uId, name, surname, username, address);
		req.getSession().setAttribute("user", user);
		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().println(user.getUsername());
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
