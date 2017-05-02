package edu.ben.rate_review.models;

import edu.ben.rate_review.daos.DaoManager;
import edu.ben.rate_review.daos.UserDao;

public class TutorForm {
	private long id;
	private long course_id;
	private Long student_id;
	private Long professor_id;
	private String tutor_first_name;
	private String tutor_last_name;
	private String tutor_email;
	private String course_name;

	public TutorForm() {
		super();
	}

	public TutorForm(Tutor tutor) {
		this.setId(tutor.getId());
		this.setCourse_id(tutor.getCourse_id());
		this.setProfessor_id(tutor.getProfessor_id());
		this.setStudent_id(tutor.getStudent_id());
		this.setTutor_email(tutor.getTutor_email());
		this.setTutor_first_name(tutor.getTutor_first_name());
		this.setTutor_last_name(tutor.getTutor_last_name());
		this.setCourse_name(tutor.getCourse_name());
	}

	public Tutor build() {
		Tutor tutor = new Tutor();

		return tutor;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getStudent_id() {
		return student_id;
	}

	public void setStudent_id(Long student_id) {
		this.student_id = student_id;
	}

	public Long getProfessor_id() {
		return professor_id;
	}

	public void setProfessor_id(Long professor_id) {
		this.professor_id = professor_id;
	}

	public String getTutor_first_name() {
		return tutor_first_name;
	}

	public void setTutor_first_name(String tutor_first_name) {
		this.tutor_first_name = tutor_first_name;
	}

	public String getTutor_last_name() {
		return tutor_last_name;
	}

	public void setTutor_last_name(String tutor_last_name) {
		this.tutor_last_name = tutor_last_name;
	}

	public String getTutor_email() {
		return tutor_email;
	}

	public void setTutor_email(String tutor_email) {
		this.tutor_email = tutor_email;
	}

	public String getSubject() {
		DaoManager dao = DaoManager.getInstance();
		UserDao ud = dao.getUserDao();
		User user = ud.findById(professor_id);
		return user.getMajor();
	}

	public String getProfessor_name() {
		DaoManager dao = DaoManager.getInstance();
		UserDao ud = dao.getUserDao();
		User user = ud.findById(professor_id);
		String professor_name = user.getFirst_name() + ", " + user.getLast_name();
		return professor_name;
	}

	public long getCourse_id() {
		return course_id;
	}

	public void setCourse_id(long course_id) {
		this.course_id = course_id;
	}

	public String getCourse_name() {
		return course_name;
	}

	public void setCourse_name(String course_name) {
		this.course_name = course_name;
	}

}
