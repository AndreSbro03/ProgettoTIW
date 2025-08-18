package it.polimi.tiw.generals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.ServletContext;
import jakarta.servlet.UnavailableException;

public class AuctionUtils {
	
	static public Connection openDbConnection(ServletContext context) throws UnavailableException {
		Connection connection;
		try {
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load db driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't connect");
		}
		return connection;
	}
	
	static public String getDateTimeFormat(LocalDateTime date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		return date.format(formatter);
	}
	
	static public String diffTime(LocalDateTime to) {
		return diffTime(LocalDateTime.now(), to);
	}
	
	static public String diffTime(LocalDateTime from, LocalDateTime to) {
        Duration duration = Duration.between(from, to);

        long days = duration.toDays();
        duration = duration.minusDays(days);

        long hours = duration.toHours();
        duration = duration.minusHours(hours);

        long minutes = duration.toMinutes();
        duration = duration.minusMinutes(minutes);

        long seconds = duration.getSeconds();

        return String.format("%d days, %d:%d:%d",
                days, hours, minutes, seconds);
    }
	
}
