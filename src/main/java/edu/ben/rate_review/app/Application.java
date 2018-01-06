package edu.ben.rate_review.app;

import static spark.Spark.*;

import edu.ben.rate_review.controller.home.*;
import spark.Session;
import spark.template.handlebars.HandlebarsTemplateEngine;

public class Application {

	// static variables to control email functionality
	public static boolean ALLOW_EMAIL = true;
	// SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
	// "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"
	// match up paths
	public static String HOME_PATH = "/";
	public static String NOTFOUND_HOME_PATH = "/notfound";
	public static String CONTACTUS_PATH = "contactus";
	public static String PORTFOLIO_PATH = "portfolio";
	public static String SERVICES_PATH = "services";
	public static String SUPPORT_PATH = "support";
	

	public static void main(String[] args) throws Exception {

		port(getEnvironmentPort());

		// Configure your Asset folder so that your JS, CSS, Images are
		// available from the server endpoint
		staticFiles.location("/public");
		configRoutes();
	}

	static int getEnvironmentPort() {
		ProcessBuilder processBuilder = new ProcessBuilder();
		if (processBuilder.environment().get("PORT") != null) {
			return Integer.parseInt(processBuilder.environment().get("PORT"));
		}
		// return default port if heroku-port isn't set (i.e. on localhost)
		return 4567;

	}

	/**
	 * Configures the routes based on URL path
	 */
	private static void configRoutes() {

		// Filter that checks things right away before every request
		before("/*", (request, response) -> {
			// create the session and assign it to a variable
			Session session = request.session(true);

		});

		after((request, response) -> {
			response.header("Content-Encoding", "gzip");
		});

		exception(Exception.class, (exception, request, response) -> {
			exception.printStackTrace();
		});

		notFound((req, res) -> {
			res.type("application/json");

			// page not found - kicks out to home
			res.redirect(HOME_PATH);
			halt();

			return "{\"message\":\"Custom 404\"}";
		});

		// path for the home page
		get(CONTACTUS_PATH, HomeController::showHomePage, new HandlebarsTemplateEngine());
		get(HOME_PATH, HomeController::showHomePage, new HandlebarsTemplateEngine());
		get(PORTFOLIO_PATH, HomeController::showHomePage, new HandlebarsTemplateEngine());
		get(SERVICES_PATH, HomeController::showHomePage, new HandlebarsTemplateEngine());
		get(SUPPORT_PATH, HomeController::showHomePage, new HandlebarsTemplateEngine());
		get(NOTFOUND_HOME_PATH, HomeController::showHomePage, new HandlebarsTemplateEngine());
		post(HOME_PATH, HomeController::contact);


	}
}