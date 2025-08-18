package it.polimi.tiw.servlets.user;

import jakarta.servlet.ServletException;
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
 * Servlet implementation class LogIn
 */
@WebServlet("/login")
public class LogIn extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String formPath = "login.jsp";

	Connection connection;

	public void init() throws ServletException {
		connection = AuctionUtils.openDbConnection(getServletContext());
	}

	private void sendErrorMessage(HttpServletRequest req, HttpServletResponse res, String msg) throws IOException {
		HttpSession session = req.getSession(true);
		session.setAttribute("errorMsg", msg);
		res.sendRedirect(formPath);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
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
		if (username == null || password == null) {
			sendErrorMessage(req, res, "Fill all the forms");
			return;
		}

		res.setContentType("text/plain");
		
		UserDataDAO udd = new UserDataDAO(connection);
		User user;
		
		try {
			user = udd.logIn(username, password);
		} catch (RuntimeException e) {
			sendErrorMessage(req, res, e.getMessage());
			return;
		} catch (SQLException e1) {
			sendErrorMessage(req, res, "SQL error: " + e1.getMessage());
			return;
		}

		HttpSession session = req.getSession();
		session.setAttribute("user", user);
		
		/**
		 * Redirect to the sell management page 
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
