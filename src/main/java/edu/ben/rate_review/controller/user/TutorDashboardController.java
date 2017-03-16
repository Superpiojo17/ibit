package edu.ben.rate_review.controller.user;

import java.util.HashMap;
import java.util.List;

import edu.ben.rate_review.app.Application;
import edu.ben.rate_review.authorization.AuthException;
import edu.ben.rate_review.daos.AnnouncementDao;
import edu.ben.rate_review.daos.DaoManager;
import edu.ben.rate_review.daos.TutorDao;
import edu.ben.rate_review.models.Announcement;
import edu.ben.rate_review.models.TutorAppointment;
import edu.ben.rate_review.models.User;
import edu.ben.rate_review.policy.AuthPolicyManager;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Session;

public class TutorDashboardController {

	public ModelAndView showTutorDashboardPage(Request req, Response res) throws AuthException {
		// Just a hash to pass data from the servlet to the page
		HashMap<String, Object> model = new HashMap<>();

		Session session = req.session();
		User u = (User) session.attribute("current_user");
		AuthPolicyManager.getInstance().getUserPolicy().showTutorDashboardPage();

		model.put("current_user", u);

		DaoManager adao = DaoManager.getInstance();
		AnnouncementDao ad = adao.getAnnouncementDao();
		List<Announcement> announcements = ad.all();
		model.put("announcements", announcements);

		TutorDao tDao = adao.getTutorDao();
		List<TutorAppointment> appointments = tDao.listAllTutorAppointments(u.getId());

		model.put("appointments", appointments);

		// Tell the server to render the index page with the data in the model
		return new ModelAndView(model, "users/tutorDashboard.hbs");
	}

	/**
	 * updates a tutor appointment object with the tutor's response
	 * 
	 * @param req
	 * @param res
	 * @return
	 */
	public String replyToRequest(Request req, Response res) {

		TutorDao tDao = DaoManager.getInstance().getTutorDao();
		long id = Long.parseLong(req.queryParams("appointment_id"));

		if (id > 0) {
			TutorAppointment appointment = tDao.findAppointmentByID(id);
			appointment.setTutor_message(req.queryParams("tutor_message"));
			tDao.updateTutorResponse(appointment);
			tDao.approveAppointment(appointment);
		} else {
			id *= -1;
			TutorAppointment appointment = tDao.findAppointmentByID(id);
			appointment.setTutor_message(req.queryParams("tutor_message"));
			tDao.updateTutorResponse(appointment);
			tDao.denyAppointment(appointment);
		}

		res.redirect(Application.TUTORDASHBOARD_PATH);
		return "";
	}

}
