package it.polimi.tiw.servlets.user;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.*;

import it.polimi.tiw.dao.UserDataDAO;
import it.polimi.tiw.generals.AuctionUtils;

/**
 * Servlet implementation class SingUp
 */
@WebServlet("/singup")
public class SingUp extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String formPath = "signup.jsp";
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

		String name = req.getParameter("name");
		String surname = req.getParameter("surname");
		String address = req.getParameter("address");
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		String confirm_pw = req.getParameter("confirm-pw");

		res.setContentType("text/plain");

		if (!password.equals(confirm_pw)) {
			sendErrorMessage(req, res, "Password Mismatch");
			return;
		}
		
		/**
		 * TODO: Check data 
		 */
		
		/**
		 * Add user to the db
		 */
		UserDataDAO udd = new UserDataDAO(connection);
		try {
			udd.addUser(username, password, name, surname, address);
		} catch (Exception e) {
			sendErrorMessage(req, res, "SQL error: " + e.getMessage());
			return;
		}

		/**
		 * Redirect to the sell management page 
		 */
		res.sendRedirect("login.jsp");
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
