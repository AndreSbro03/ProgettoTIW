package it.polimi.tiw.servlets.auctions;

import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;

import it.polimi.tiw.beans.Item;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.generals.AuctionUtils;
import it.polimi.tiw.servlets.ItemImage;

/**
 * Servlet implementation class AddItem
 */
@WebServlet("/add-item")
@MultipartConfig
public class AddItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String formPath = "add-item.jsp";
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/**
		 * First check if there is an user logged in. If not redirect to the login page.
		 */
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			response.sendRedirect("login.jsp");
			return;
		}

		/**
		 * Get all the parameters passed in the form
		 */
		String name = request.getParameter("name");
		String descr = request.getParameter("description");
		Part imagePart = request.getPart("image");
		String price = request.getParameter("price");

		/**
		 * Create the item so that the constructor can check the validity of the fields
		 */
		try {
			Item.isValid(name, descr, price);
		} catch (Exception e) {
			sendErrorMessage(request, response, e.getMessage());
			return;
		}
		
		// We first check the parameter needed is present
		if (imagePart == null || imagePart.getSize() <= 0) {
			sendErrorMessage(request, response, "Missing file in request!");
			return;
		}

		// We then check the parameter is valid (in this case right format)
		String contentType = imagePart.getContentType();

		if (!contentType.startsWith("image")) {
			sendErrorMessage(request, response, "File format not permitted");
			return;
		}

		/**
		 * Insert the item into the db
		 */
		String query = "INSERT INTO astemi.item (userID, name, descr, price) VALUES (?, ?, ?, ?)";
		PreparedStatement statement = null;
		ResultSet generatedKeys = null;
		try {
			statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			statement.setInt(1, user.getId());
			statement.setString(2, name);
			statement.setString(3, descr);
			statement.setFloat(4, Float.parseFloat(price));
			statement.executeUpdate();
			
			/**
			 * Retrieve the generated key
			 */
			generatedKeys = statement.getGeneratedKeys();
			int itemId = -1;
			if (generatedKeys.next()) {
				itemId = generatedKeys.getInt(1);
			}
			
			/**
			 * Save the image passed with the id assigned
			 */
			ItemImage.saveImage(getServletContext(), imagePart, itemId);
			
		} catch (SQLException e) {
			sendErrorMessage(request, response, "SQL error: " + e.getMessage());
			return;
		} catch (RuntimeException e2) {
			sendErrorMessage(request, response, e2.getMessage());
			return;
		} finally {
			try {
				if(statement != null) statement.close();
				if(generatedKeys != null) generatedKeys.close();
			} catch (Exception e1) {
				sendErrorMessage(request, response, "SQL STMT ERROR: " + e1.getMessage());
				return;
			}
		}

		response.sendRedirect("sell");
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
