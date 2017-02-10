package edu.ben.rate_review.controllers.user;

import java.util.HashMap;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class AdminDashboardController {
	public ModelAndView showAdminDashboardPage(Request req, Response res) {
		// Just a hash to pass data from the servlet to the page
		HashMap<String, Object> model = new HashMap<>();
		// Tell the server to render the index page with the data in the model
		return new ModelAndView(model, "users/admindashboard.hbs");
	}
	
	public ModelAndView showAllUsersPage(Request req, Response res) {
		// Just a hash to pass data from the servlet to the page
		HashMap<String, Object> model = new HashMap<>();
		// Tell the server to render the index page with the data in the model
		return new ModelAndView(model, "users/allusers.hbs");
	}

}
