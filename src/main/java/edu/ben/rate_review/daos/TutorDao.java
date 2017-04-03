package edu.ben.rate_review.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.ben.rate_review.models.Tutor;
import edu.ben.rate_review.models.TutorAppointment;
import edu.ben.rate_review.models.TutorForm;
import edu.ben.rate_review.models.User;

public class TutorDao implements Dao<Tutor> {

	String TUTOR_TABLE = "tutors";
	String USER_TABLE = "users";
	String APPOINTMENT_TABLE = "tutor_appointment";
	Connection conn = null;

	/**
	 * TutorDao connection
	 * 
	 * @param conn
	 */
	public TutorDao(Connection conn) {
		this.conn = conn;
	}

	private Tutor mapRow(ResultSet rs) throws SQLException {
		UserDao udao = new UserDao(conn);

		Tutor tmp = new Tutor();

		tmp.setId(rs.getLong("tutor_relationship_id"));
		tmp.setStudent_id(rs.getLong("user_id_student"));
		tmp.setProfessor_id(rs.getLong("user_id_professor"));
		tmp.setCourse_name(rs.getString("course_name"));

		tmp.setTutor_email(udao.findById(rs.getLong("user_id_student")).getEmail());
		tmp.setTutor_first_name(udao.findById(rs.getLong("user_id_student")).getFirst_name());
		tmp.setTutor_last_name(udao.findById(rs.getLong("user_id_student")).getLast_name());

		return tmp;
	}

	public Tutor save(Tutor tutor) {
		final String sql = "INSERT INTO " + TUTOR_TABLE
				+ "(user_id_student, user_id_professor, course_name) Values(?,?,?)";

		try {
			System.out.println("ADDED");
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setLong(1, tutor.getStudent_id());
			ps.setLong(2, tutor.getProfessor_id());
			ps.setString(3, tutor.getCourse_name());
			ps.executeUpdate();
			return tutor;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Stores a tutor appointment in the database
	 * 
	 * @param appointment
	 * @return
	 */
	public TutorAppointment saveTutorAppointment(TutorAppointment appointment) {
		final String sql = "INSERT INTO " + APPOINTMENT_TABLE + "(student_id, tutor_id, date, time, student_message, "
				+ "student_firstname, student_lastname, tutor_firstname, tutor_lastname, tutor_message, tutor_has_responded, appointment_status) Values(?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setLong(1, appointment.getStudent_id());
			ps.setLong(2, appointment.getTutor_id());
			ps.setString(3, appointment.getDate());
			ps.setString(4, appointment.getTime());
			ps.setString(5, appointment.getStudent_message());
			ps.setString(6, appointment.getStudent_firstname());
			ps.setString(7, appointment.getStudent_lastname());
			ps.setString(8, appointment.getTutor_firstname());
			ps.setString(9, appointment.getTutor_lastname());
			ps.setString(10, appointment.getTutor_message());
			ps.setBoolean(11, appointment.getTutor_has_responded());
			ps.setBoolean(12, appointment.getAppointment_status());
			ps.executeUpdate();
			return appointment;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Gets a list of appointments for a specific tutor
	 * 
	 * @param tutor_id
	 * @return
	 */
	public List<TutorAppointment> listAllTutorAppointments(Long tutor_id) {
		final String SELECT = "SELECT * FROM " + APPOINTMENT_TABLE + " WHERE tutor_id = " + tutor_id;

		List<TutorAppointment> appointments = null;
		try {
			PreparedStatement ps = conn.prepareStatement(SELECT);
			appointments = new ArrayList<TutorAppointment>();
			try {
				ResultSet rs = ps.executeQuery(SELECT);
				while (rs.next()) {
					appointments.add(appointmentMapRow(rs));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return appointments;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return appointments;
	}

	/**
	 * Builds a tutor object
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private TutorAppointment appointmentMapRow(ResultSet rs) throws SQLException {
		// create student course object
		TutorAppointment tmp = new TutorAppointment();
		tmp.setAppointment_id(rs.getLong("appointment_id"));
		tmp.setStudent_id(rs.getLong("student_id"));
		tmp.setTutor_id(rs.getLong("tutor_id"));
		tmp.setDate(rs.getString("date"));
		tmp.setTime(rs.getString("time"));
		tmp.setStudent_message(rs.getString("student_message"));
		tmp.setTutor_message(rs.getString("tutor_message"));
		tmp.setTutor_has_responded(rs.getBoolean("tutor_has_responded"));
		tmp.setAppointment_status(rs.getBoolean("appointment_status"));
		tmp.setStudent_firstname(rs.getString("student_firstname"));
		tmp.setStudent_lastname(rs.getString("student_lastname"));
		tmp.setTutor_firstname(rs.getString("tutor_firstname"));
		tmp.setTutor_lastname(rs.getString("tutor_lastname"));

		return tmp;
	}

	/**
	 * Finds an appointment by appointment ID
	 * 
	 * @param id
	 * @return
	 */
	public TutorAppointment findAppointmentByID(long id) {
		// Declare SQL template query
		String sql = "SELECT * FROM " + APPOINTMENT_TABLE + " WHERE appointment_id = ? LIMIT 1";
		try {
			// Create Prepared Statement from query
			PreparedStatement q = conn.prepareStatement(sql);
			q.setLong(1, id);

			ResultSet rs = q.executeQuery();
			if (rs.next()) {
				return appointmentMapRow(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Updates a tutor appointment with the tutor response
	 * 
	 * @param appointment
	 * @return
	 */
	public TutorAppointment updateTutorResponse(TutorAppointment appointment) {
		String sql = "UPDATE " + APPOINTMENT_TABLE + " SET tutor_message = ? WHERE appointment_id = ? LIMIT 1";

		try {
			// Create Prepared Statement from query
			PreparedStatement ps = conn.prepareStatement(sql);
			// Fill in the ? with the parameters you want
			ps.setString(1, appointment.getTutor_message());
			ps.setLong(2, appointment.getAppointment_id());
			// Runs query
			ps.execute();
			return appointment;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// If you don't find a model
		return null;
	}

	/**
	 * Allows a student to cancel a tutor appointment
	 * 
	 * @param appointment_id
	 */
	public void cancelTutorAppointment(long appointment_id) {
		String sql = "DELETE FROM " + APPOINTMENT_TABLE + " WHERE appointment_id = ? LIMIT 1";

		try {
			// Create Prepared Statement from query
			PreparedStatement ps = conn.prepareStatement(sql);
			// Fill in the ? with the parameters you want
			ps.setLong(1, appointment_id);
			// Runs query
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// If you don't find a model
	}

	/**
	 * Approves an appointment request
	 * 
	 * @param appointment
	 * @return
	 */
	public TutorAppointment setTutorResponded(TutorAppointment appointment) {
		String sql = "UPDATE " + APPOINTMENT_TABLE + " SET tutor_has_responded = 1 WHERE appointment_id = ? LIMIT 1";

		try {
			// Create Prepared Statement from query
			PreparedStatement ps = conn.prepareStatement(sql);
			// Fill in the ? with the parameters you want
			ps.setLong(1, appointment.getAppointment_id());
			// Runs query
			ps.execute();
			return appointment;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// If you don't find a model
		return null;
	}

	/**
	 * Denies an appointment request
	 * 
	 * @param appointment
	 * @return
	 */
	public TutorAppointment approveAppointment(TutorAppointment appointment) {
		String sql = "UPDATE " + APPOINTMENT_TABLE + " SET appointment_status = 1 WHERE appointment_id = ? LIMIT 1";

		try {
			// Create Prepared Statement from query
			PreparedStatement ps = conn.prepareStatement(sql);
			// Fill in the ? with the parameters you want
			ps.setLong(1, appointment.getAppointment_id());
			// Runs query
			ps.execute();
			return appointment;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// If you don't find a model
		return null;
	}

	/**
	 * Lists requests which have not been viewed by tutor
	 * 
	 * @param tutor_id
	 * @return
	 */
	public List<TutorAppointment> listAllUnviewedTutorAppointments(Long tutor_id) {
		final String SELECT = "SELECT * FROM " + APPOINTMENT_TABLE + " WHERE tutor_has_responded = 0 AND tutor_id = "
				+ tutor_id;

		List<TutorAppointment> appointments = null;
		try {
			PreparedStatement ps = conn.prepareStatement(SELECT);
			appointments = new ArrayList<TutorAppointment>();
			try {
				ResultSet rs = ps.executeQuery(SELECT);
				while (rs.next()) {
					appointments.add(appointmentMapRow(rs));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return appointments;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return appointments;
	}

	/**
	 * Lists all approved tutor appointments
	 * 
	 * @param tutor_id
	 * @return
	 */
	public List<TutorAppointment> listAllApprovedTutorAppointments(Long tutor_id) {
		final String SELECT = "SELECT * FROM " + APPOINTMENT_TABLE + " WHERE appointment_status = 1 AND tutor_id = "
				+ tutor_id;

		List<TutorAppointment> appointments = null;
		try {
			PreparedStatement ps = conn.prepareStatement(SELECT);
			appointments = new ArrayList<TutorAppointment>();
			try {
				ResultSet rs = ps.executeQuery(SELECT);
				while (rs.next()) {
					appointments.add(appointmentMapRow(rs));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return appointments;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return appointments;
	}

	/**
	 * Lists a student's upcoming appointments
	 * 
	 * @param student_id
	 * @return
	 */
	public List<TutorAppointment> listAllStudentAppointments(User user) {
		final String SELECT = "SELECT * FROM " + APPOINTMENT_TABLE + " WHERE student_id = " + user.getId();

		List<TutorAppointment> appointments = null;
		try {
			PreparedStatement ps = conn.prepareStatement(SELECT);
			appointments = new ArrayList<TutorAppointment>();
			try {
				ResultSet rs = ps.executeQuery(SELECT);
				while (rs.next()) {
					appointments.add(appointmentMapRow(rs));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return appointments;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return appointments;
	}

	/**
	 * 
	 * @return all users from the database.
	 */

	public List<Tutor> all(Long id) {
		final String SELECT = "SELECT * FROM " + TUTOR_TABLE + " WHERE user_id_professor = " + id;

		List<Tutor> tutors = null;
		try {
			PreparedStatement ps = conn.prepareStatement(SELECT);
			tutors = new ArrayList<Tutor>();
			try {
				ResultSet rs = ps.executeQuery(SELECT);
				while (rs.next()) {
					tutors.add(mapRow(rs));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return tutors;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tutors;
	}

	public List<Tutor> allbyDept(String department) {
		final String SELECT = "SELECT * FROM " + TUTOR_TABLE + " WHERE user_id_professor = " + department;

		List<Tutor> tutors = null;
		try {
			PreparedStatement ps = conn.prepareStatement(SELECT);
			tutors = new ArrayList<Tutor>();
			try {
				ResultSet rs = ps.executeQuery(SELECT);
				while (rs.next()) {
					tutors.add(mapRow(rs));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return tutors;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tutors;
	}

	/**
	 * Selects all tutors from the table
	 * 
	 * @param id
	 * @return
	 */
	public List<Tutor> listAllTutors() {
		final String SELECT = "SELECT * FROM " + TUTOR_TABLE + " ORDER BY user_id_student DESC";

		List<Tutor> tutors = null;
		try {
			PreparedStatement ps = conn.prepareStatement(SELECT);
			tutors = new ArrayList<Tutor>();
			try {
				ResultSet rs = ps.executeQuery(SELECT);
				while (rs.next()) {
					tutors.add(mapRow(rs));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return tutors;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tutors;
	}

	public Tutor findById(long id) {
		// Declare SQL template query
		String sql = "SELECT * FROM " + TUTOR_TABLE + " WHERE tutor_relationship_id = ? LIMIT 1";
		try {
			// Create Prepared Statement from query
			PreparedStatement q = conn.prepareStatement(sql);
			// Fill in the ? with the parameters you want
			q.setLong(1, id);

			// Run your shit
			ResultSet rs = q.executeQuery();
			if (rs.next()) {
				return mapRow(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// If you don't find a model
		return null;

	}

	public Tutor findByStudentId(long id) {
		// Declare SQL template query
		String sql = "SELECT * FROM " + TUTOR_TABLE + " WHERE user_id_student = ? LIMIT 1";
		try {
			// Create Prepared Statement from query
			PreparedStatement q = conn.prepareStatement(sql);
			// Fill in the ? with the parameters you want
			q.setLong(1, id);

			// Run your shit
			ResultSet rs = q.executeQuery();
			if (rs.next()) {
				return mapRow(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// If you don't find a model
		return null;

	}

	public Long getStudentId(long id) {
		// Declare SQL template query
		String sql = "SELECT USER_ID_STUDENT FROM " + TUTOR_TABLE + " WHERE tutor_relationship_id = ? LIMIT 1";
		try {
			// Create Prepared Statement from query
			PreparedStatement q = conn.prepareStatement(sql);
			// Fill in the ? with the parameters you want
			q.setLong(1, id);

			// Run your shit
			ResultSet rs = q.executeQuery();
			if (rs.next()) {
				return rs.getLong(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// If you don't find a model
		return (long) -1;

	}

	public String deleteTutor(long id) {

		String sql = "DELETE FROM " + TUTOR_TABLE + " WHERE tutor_relationship_id = ? LIMIT 1";

		try {
			// Create Prepared Statement from query
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setLong(1, id);
			// Runs query
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return " ";
	}

	public void changeTutorRole(Long id) {
		String sql = "UPDATE " + USER_TABLE + " SET role_id = ? WHERE user_id = ? LIMIT 1";

		try {
			// Create Prepared Statement from query
			PreparedStatement ps = conn.prepareStatement(sql);
			// Fill in the ? with the parameters you want
			ps.setInt(1, 4);
			ps.setLong(2, id);

			// Runs query
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// If you don't find a model
	}

	public TutorForm updateTutor(TutorForm tutor) {
		String sql = "UPDATE " + TUTOR_TABLE + " SET course_name = ? WHERE tutor_relationship_id = ? LIMIT 1";

		try {
			// Create Prepared Statement from query
			PreparedStatement ps = conn.prepareStatement(sql);
			// Fill in the ? with the parameters you want
			ps.setString(1, tutor.getCourse());
			ps.setLong(2, tutor.getId());

			// Runs query
			ps.execute();
			return tutor;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// If you don't find a model
		return null;
	}

	public TutorForm adminUpdateTutor(TutorForm tutor) {
		String sql = "UPDATE " + TUTOR_TABLE + " SET user_id_student = ? WHERE tutor_relationship_id = ? LIMIT 1";

		try {
			// Create Prepared Statement from query
			PreparedStatement ps = conn.prepareStatement(sql);
			// Fill in the ? with the parameters you want
			ps.setLong(1, tutor.getStudent_id());
			ps.setLong(2, tutor.getId());

			// Runs query
			ps.execute();
			return tutor;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// If you don't find a model
		return null;
	}

	@Override
	public Tutor find(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Tutor> all() {
		// TODO Auto-generated method stub
		return null;
	}

}
