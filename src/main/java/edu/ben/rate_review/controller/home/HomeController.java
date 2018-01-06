package edu.ben.rate_review.controller.home;

import java.util.HashMap;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import edu.ben.rate_review.app.Application;
import edu.ben.rate_review.email.SendGridManager;

public class HomeController {

	public static ModelAndView showHomePage(Request req, Response res) throws Exception {
		// Just a hash to pass data from the servlet to the page
		HashMap<String, Object> model = new HashMap<>();

		return new ModelAndView(model, "home/home.hbs");
	}
	public static ModelAndView showContactUsPage(Request req, Response res) throws Exception {
		// Just a hash to pass data from the servlet to the page
		HashMap<String, Object> model = new HashMap<>();

		return new ModelAndView(model, "home/contactus.hbs");
	}

	public static ModelAndView showPortfolioPage(Request req, Response res) throws Exception {
		// Just a hash to pass data from the servlet to the page
		HashMap<String, Object> model = new HashMap<>();

		return new ModelAndView(model, "home/portfolio.hbs");
	}

	public static ModelAndView showServicesPage(Request req, Response res) throws Exception {
		// Just a hash to pass data from the servlet to the page
		HashMap<String, Object> model = new HashMap<>();

		return new ModelAndView(model, "home/services.hbs");
	}

	public static ModelAndView showSupportPage(Request req, Response res) throws Exception {
		// Just a hash to pass data from the servlet to the page
		HashMap<String, Object> model = new HashMap<>();

		return new ModelAndView(model, "home/support.hbs");
	}


	public static ModelAndView contact(Request req, Response res) {
		HashMap<String, Object> model = new HashMap<>();

		String name = req.queryParams("name");
		String email = req.queryParams("email");
		String message = req.queryParams("message");
		String subject = "New Notification From Markotic Sports";
		String header = "Sent by:\n " + name + " from " + email;

		String messageBody = "<p>" + message + "</p>";
		String finalMessage = header + messageBody;

		HashMap<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("subject", subject);
		params.put("email", email);
		params.put("to", "antemarkotic@yahoo.com");
		params.put("message", finalMessage);
		if (Application.ALLOW_EMAIL) {
			SendGridManager.getInstance().send(params);
		}
		res.redirect(Application.HOME_PATH);
		return new ModelAndView(model, "home/home.hbs");

	}

}
