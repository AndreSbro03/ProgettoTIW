package it.polimi.tiw.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/images/*")
public class ItemImage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final static String BASE_DIR = System.getProperty("catalina.base") + File.separator + "uploads";
	private final static String DEFAULT_IMAGE = "assets/image_not_aviable.png";

	public static void saveImage(ServletContext sc, Part imagePart, int id) throws IOException {
		File uploadDir = new File(BASE_DIR);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}

		String fileName = id + ".jpg";
		File file = new File(uploadDir, fileName);

		if (imagePart != null) {
			try (InputStream fileContent = imagePart.getInputStream()) {
				Files.copy(fileContent, file.toPath());
				System.out.println("File saved correctly!");
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

	}

	public static String getImageUrl(Integer id) {
		if (id == null)
			return DEFAULT_IMAGE;
		File imgFile = new File(BASE_DIR, id + ".jpg");
		if (imgFile.exists()) {
			return "images/" + id + ".jpg";
		} else {
			return DEFAULT_IMAGE;
		}
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String fileName = req.getPathInfo().substring(1);
		File file = new File(BASE_DIR, fileName);

		if (!file.exists()) {
			file = new File(getServletContext().getRealPath(DEFAULT_IMAGE));
		}

		if (!file.exists()) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		resp.setContentType("image/jpeg");
		resp.setContentLengthLong(file.length());

		try (FileInputStream fis = new FileInputStream(file); ServletOutputStream os = resp.getOutputStream()) {
			fis.transferTo(os);
		}
	}
}
