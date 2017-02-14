package edu.ben.rate_review.controller.home;

import java.util.HashMap;

//import java.util.ArrayList;
//import java.util.HashMap;
import edu.ben.rate_review.daos.DaoManager;
import edu.ben.rate_review.daos.UserDao;
import edu.ben.rate_review.encryption.SecurePassword;
import edu.ben.rate_review.models.User;
import edu.ben.rate_review.policy.AuthPolicyManager;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Session;

/**
 * Login controller
 * 
 * @author Mike
 * @version 2-2-2017
 */
public class LogInController {

	/**
	 * Show log in page
	 */
	public ModelAndView showLoginPage(Request req, Response res) {
		// Just a hash to pass data from the servlet to the page
		HashMap<String, Object> model = new HashMap<>();
		// Tell the server to render the index page with the data in the model
		return new ModelAndView(model, "sessions/login.hbs");
	}

	/**
	 * Login method. Confirms fields are properly filled out and user conditions
	 * are met to allow login such as accurate email and password, and an
	 * account which is confirmed and activated. Depending on role type of the
	 * account, the user will be directed to the appropriate dashboard.
	 * 
	 * @param req
	 * @param res
	 * @return
	 */
	public String login(Request req, Response res) {
		// checks the email and password fields are filled out
		if (!req.queryParams("email").isEmpty() && !req.queryParams("password").isEmpty()) {
			// checks that the login credentials match a registered, confirmed,
			// and active account
			if (confirmRegistered(req.queryParams("email"), req.queryParams("password"))
					&& accountConfirmed(req.queryParams("email")) && accountActive(req.queryParams("email"))) {
				// determines role of user
				int role = getAccountType(req.queryParams("email"), req.queryParams("password"));
				// directs user to correct dashboard
				Session session = req.session();
				UserDao user = DaoManager.getInstance().getUserDao();
				User u = user.findByEmail(req.queryParams("email"));
				
				session.attribute("current_user", u);
				System.out.println(u.getEmail());
//				session.attribute()
				if (role == 4) {
					res.redirect("/studentdashboard");
				} else if (role == 3) {
					res.redirect("/tutordashboard");
				} else if (role == 2) {
					res.redirect("/facultydashboard");
				} else if (role == 1) {
					
					res.redirect("/admindashboard");
					
				}
			} else {
				// if email is not found in the system, outputs message
				res.redirect("/login");
				// "Incorrect E-mail or Password. Please try again."
			}
		} else {
			res.redirect("/login");
		}
		return "";
	}

	/**
	 * While attempting to log-in, this method checks that the account the user
	 * is logging in with is currently activated.
	 * 
	 * @param email
	 * @return
	 */
	public static boolean accountActive(String email) {
		UserDao userDao = DaoManager.getInstance().getUserDao();
		User user = new User();
		user = userDao.findByEmail(email);

		if (user != null && user.isActive()) {
			return true;
		}
		return false;
	}

	/**
	 * While attempting to log-in, this method checks that the account the user
	 * is logging in with has been confirmed.
	 * 
	 * @param email
	 * @return
	 */
	public static boolean accountConfirmed(String email) {
		UserDao userDao = DaoManager.getInstance().getUserDao();
		User user = new User();
		user = userDao.findByEmail(email);

		if (user != null && user.isConfirmed()) {
			return true;
		}
		return false;
	}

	/**
	 * Checks that the account logging in is registered for the site
	 * 
	 * @param email
	 * @param password
	 * @return
	 */
	public static boolean confirmRegistered(String email, String password) {
		UserDao userDao = DaoManager.getInstance().getUserDao();
		User u = new User();
		u = userDao.findByEmail(email);

		if (u != null) {
			if (SecurePassword.getCheckPassword(password, u.getEncryptedPassword())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks that the account logging in is registered for the site
	 * 
	 * @param email
	 * @param password
	 * @return
	 */
	public static int getAccountType(String email, String password) {
		UserDao userDao = DaoManager.getInstance().getUserDao();
		User u = new User();
		u = userDao.findByEmail(email);
		
		return u.getRole();
	}

}
