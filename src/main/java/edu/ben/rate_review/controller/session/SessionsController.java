package edu.ben.rate_review.controller.session;

import static spark.Spark.redirect;

import edu.ben.rate_review.daos.DaoManager;
import edu.ben.rate_review.daos.UserDao;
import edu.ben.rate_review.email.Email;
import edu.ben.rate_review.encryption.SecurePassword;
import edu.ben.rate_review.models.User;
//import edu.ben.rate_review.email;
//import edu.ben.rate_review.daos;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class SessionsController {

	public ModelAndView showRegister(Request req, Response res) {

		return new ModelAndView(null, "sessions/register.hbs");
	}

	public String register(Request req, Response res) {
		UserDao userDao = DaoManager.getInstance().getUserDao();
		// variables to keep track of empty field
		boolean emptyField = false;
		String emptyFieldName = "";

		// Checks to see if each field is filled out.
		if (req.queryParams("first_name").isEmpty()) {
			emptyFieldName = "First name";
			emptyField = true;
		} else if (req.queryParams("last_name").isEmpty()) {
			emptyFieldName = "Last name";
			emptyField = true;
		} else if (req.queryParams("email").isEmpty()) {
			emptyFieldName = "Email";
			emptyField = true;
		} else if (req.queryParams("password").isEmpty()) {
			emptyFieldName = "Password";
			emptyField = true;
		}

		// If a field is left empty, the user will be sent back to the
		// registration page. They will be told which was the first field they
		// left empty.
		if (emptyField) {
			// error message here - emptyFieldMessage(emptyFieldName)
			res.redirect("/register");
		}

		// Checks that the user's first and last name are valid. They must each
		// be at least 2 characters long and only contain letters.
		if (!validateName(req.queryParams("first_name"), req.queryParams("last_name"))) {
			// error message - "First and last name must be at least 2
			// characters long and only contain letters."
			res.redirect("/register");
		}
		// Checks that the password and verify password fields match.
		else if (!req.queryParams("password").equals(req.queryParams("verify_password"))) {
			// "Passwords do not match."
			res.redirect("/register");
		}
		// Checks that a benedictine email is being used to register.
		else if (!validateEmail(req.queryParams("email"))) {
			// "You must use a benedictine e-mail to register."
			res.redirect("/register");
		}
		// creates the user
		else {
			User user = userDao.findByEmail(req.queryParams("email"));
			// checks if email has already been registered
			if (user == null) {

				// will determine if account has confirmed registration
				boolean registrationConfirmed = false;
				// will determine whether or not account is currently active
				boolean accountActive = true;

				User tmp = createUser(req.queryParams("first_name"), req.queryParams("last_name"),
						req.queryParams("email"), req.queryParams("password"),
						Integer.parseInt(req.queryParams("role_id")), registrationConfirmed, accountActive);
				if (tmp != null) {
					res.redirect("/login");
				} else {
					res.redirect("/register");
				}

			} else {
				// "This email is already in use."
				res.redirect("/register");
			}

		}

		return "";
	}

	/**
	 * Creates the output the user sees when they did not fill in all their
	 * fields. It will tell them the name of the first field they failed to fill
	 * out.
	 * 
	 * @param field
	 * @return
	 */
	private String emptyFieldMessage(String field) {
		String firstEmptyField = "You are missing one or more fields starting with: ";
		return firstEmptyField + field;
	}

	/**
	 * Validates that the user's name is a valid length and only contains
	 * letters.
	 * 
	 * @param firstName
	 * @param lastName
	 * @return
	 */
	private boolean validateName(String firstName, String lastName) {

		// checks that first and last name are at least 2 characters long
		if (firstName.length() >= 2 && lastName.length() >= 2) {
			// concatenates name
			String fullName = firstName + lastName;
			// sends full name to character array
			char[] name = fullName.toCharArray();

			// checks that each character of name is a letter
			for (int i = 0; i < name.length; i++) {
				if (!Character.isAlphabetic(name[i])) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks that an e-mail address ends with a benedictine address
	 * 
	 * @param email
	 * @return
	 */
	private boolean validateEmail(String email) {
		if (email.length() > 8 && email.endsWith("@ben.edu")) {
			return true;
		}
		return false;
	}

	/**
	 * Method which creates the user
	 * 
	 * @param first_name
	 * @param last_name
	 * @param email
	 * @param password
	 * @param role_id
	 * @param confirmed
	 * @param active
	 */
	private static User createUser(String first_name, String last_name, String email, String password, int role,
			boolean confirmed, boolean active) {
		UserDao user = DaoManager.getInstance().getUserDao();
		User newUser = new User();
		newUser.setFirst_name(first_name);
		newUser.setLast_name(last_name);
		newUser.setEmail(email);
		newUser.setEncryptedPassword(password);
		newUser.setRole(role);
		newUser.setConfirmed(confirmed);
		newUser.setActive(active);

		User u = user.save(newUser);
		if (u != null) {
			confirmRegistration(newUser);
		}
		return u;
	}

	/**
	 * Method which will send an email to the registering user.
	 * 
	 * @param first_name
	 * @param email
	 * @param role_id
	 */
	private static void confirmRegistration(User user) {

		String accountType = "";

		if (user.getRole() == 1) {
			accountType = "ADMIN";
		} else if (user.getRole() == 2) {
			accountType = "PROFESSOR";
		} else if (user.getRole() == 3) {
			accountType = "TUTOR";
		} else {
			accountType = "STUDENT";
		}

		String subject = "Rate&Review Registration";
		String messageHeader = "Hello " + user.getFirst_name() + ",\n\n\n";
		String messageBody = "This message is to confirm that you have successfully signed up for Rate&Review as a "
				+ accountType + ". Please join us on the website to get started!";
		String messageFooter = "\n\n\nSincerely,\n\nThe Rate&Review Team";
		String message = messageHeader + messageBody + messageFooter;

		Email.deliverEmail(user.getFirst_name(), user.getEmail(), subject, message);

	}

	/**
	 * Getter for validateName
	 * 
	 * @param first
	 * @param last
	 * @return
	 */
	public boolean getValidateName(String first, String last) {
		return validateName(first, last);
	}

	/**
	 * Getter for validateEmail
	 * 
	 * @param first
	 * @param last
	 * @return
	 */
	public boolean getValidateEmail(String email) {
		return validateEmail(email);
	}
}
