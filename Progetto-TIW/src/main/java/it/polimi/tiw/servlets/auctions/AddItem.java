package it.polimi.tiw.servlets.auctions;

import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;

import it.polimi.tiw.beans.Item;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.ItemDAO;
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
		String price = request.getParameter("price");

		Part imagePart = null;
		try {
			imagePart = request.getPart("image");
		} catch (IllegalStateException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Server error");
			return;
		}
		
		/**
		 * Create the item so that the constructor can check the validity of the fields
		 */
		float fprice = 0.f;
		try {
			fprice = Item.isValid(name, descr, price);
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
		ItemDAO idao = new ItemDAO(connection);
		int itemId = 0;
		
		try {
			connection.setAutoCommit(false);
			itemId = idao.addItem(user.getId(), name, descr, fprice);
			/**
			 * Save the image passed with the id assigned
			 */
			ItemImage.saveImage(getServletContext(), imagePart, itemId);
			connection.commit();
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				//ignore
			}
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Server error");
			return;
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				// ignore
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
