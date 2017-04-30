package edu.ben.rate_review.daos;

import java.net.*;
import java.sql.*;
import com.mysql.cj.jdbc.MysqlDataSource;
import javax.sql.DataSource;

public class DaoManager {
	private final String HOST = "jdbc:mysql://localhost:3306";
	private final String DATABASE_NAME = "rate";
	private final String USERNAME = "root";
	// private final String PASSWORD = "root"; // everyone else's local
	// private final String PASSWORD = "LiterallyAnything"; // Mike's local
	private final String PASSWORD = "root12"; // Server

	// Private
	private DataSource src;
	private Connection con;
	private static DaoManager instance = null;
	// private static Logger logger = LoggerFactory.getLogger(DaoManager.class);

	/**
	 * Constructor that will create the singleton instance if it doesn't exist
	 * already
	 *
	 * @throws Exception
	 */
	public DaoManager() throws Exception {
		if (instance == null) {
			try {
				MysqlDataSource mysql = new MysqlDataSource();
				mysql.setURL(HOST + "/" + DATABASE_NAME);
				mysql.setDatabaseName(DATABASE_NAME);
				mysql.setUser(USERNAME);
				mysql.setPassword(PASSWORD);
				this.src = mysql;
				instance = this;
			} catch (Exception e) {
				throw e;
			}
		}
	}

  private static Connection getConnection() throws URISyntaxException, SQLException {
    URI dbUri = new URI(System.getenv("CLEARDB_DATABASE_URL"));

    String username = dbUri.getUserInfo().split(":")[0];
    String password = dbUri.getUserInfo().split(":")[1];
    String dbUrl = "jdbc:mysql://" + dbUri.getHost() + dbUri.getPath();

    return DriverManager.getConnection(dbUrl, username, password);
  }

	/**
	 * gets instance of user dao
	 *
	 * @return
	 */
	public UserDao getUserDao() {
		try {
			return new UserDao(DaoManager.getConnection());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * gets instance of course dao
	 *
	 * @return
	 */
	public CourseDao getCourseDao() {
		try {
			return new CourseDao(this.src.getConnection());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * gets instance of student in course dao
	 *
	 * @return
	 */
	public StudentInCourseDao getStudentInCourseDao() {
		try {
			return new StudentInCourseDao(this.src.getConnection());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * gets instance of announcement dao
	 *
	 * @return
	 */
	public AnnouncementDao getAnnouncementDao() {
		try {
			return new AnnouncementDao(this.src.getConnection());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * gets instance of tutor dao
	 *
	 * @return
	 */
	public TutorDao getTutorDao() {
		try {
			return new TutorDao(this.src.getConnection());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * gets instance of professor review dao
	 *
	 * @return
	 */
	public ProfessorReviewDao getProfessorReviewDao() {
		try {
			return new ProfessorReviewDao(this.src.getConnection());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Used to return the singleton instance
	 *
	 * @return
	 */
	public static DaoManager getInstance() {
		return instance;
	}

	/**
	 * Retrieves a connection to the database
	 *
	 * @throws SQLException
	 */
	public void openConnection() throws SQLException {
		try {
			if (this.con == null || this.con.isClosed()) {
				this.con = src.getConnection();
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Closes a connection to the database
	 *
	 * @throws SQLException
	 */
	public void closeConnection() throws SQLException {
		try {
			if (this.con != null && !this.con.isClosed()) {
				this.con.close();
			}
		} catch (Exception e) {
			throw e;
		}
	}

}