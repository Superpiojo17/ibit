package edu.ben.rate_review.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//import edu.ben.rate_review.models.Course;
import com.zaxxer.hikari.HikariDataSource;
import edu.ben.rate_review.models.Course;
import edu.ben.rate_review.models.ProfessorReview;
import edu.ben.rate_review.models.StudentInCourse;
import edu.ben.rate_review.models.User;

public class StudentInCourseDao extends BaseDao {

    private String STUDENTINCOURSES_TABLE = "student_in_course";

    public StudentInCourseDao(HikariDataSource db) {
        super(db);
    }

    private StudentInCourse mapRow(ResultSet rs) throws SQLException {
        UserDao uDao = new UserDao(this.db);
        CourseDao cDao = new CourseDao(this.db);
        // Create user object and pass to array
        StudentInCourse tmp = new StudentInCourse();
        tmp.setStudent_course_id(rs.getLong("student_course_id"));
        tmp.setCourse_id(rs.getLong("course_id"));
        tmp.setStudent_id(rs.getLong("student_id"));
        tmp.setCourse_reviewed(rs.getBoolean("course_reviewed"));
        tmp.setDisable_edit(rs.getBoolean("disable_edit"));
        tmp.setSemester_past(rs.getBoolean("semester_past"));

        Course course = cDao.findById(tmp.getCourse_id());

        if (course != null) {
            User professor = uDao.findById(course.getProfessor_id());
            if (professor != null) {
                tmp.setProfessor_first_name(professor.getFirst_name());
                tmp.setProfessor_last_name(professor.getLast_name());
            }
            tmp.setSemester(course.getSemester());
            tmp.setYear(course.getYear());
            tmp.setCourse_subject_number(course.getSubject() + course.getCourse_number());
        }
        return tmp;
    }

    /**
     * inserts a student into a course
     *
     * @param studentInCourse
     * @return
     */

    public StudentInCourse save(StudentInCourse studentInCourse) {
        final String sql = "INSERT INTO " + STUDENTINCOURSES_TABLE
                + "(course_id, student_id, course_reviewed, disable_edit, semester_past) Values(?,?,?,?,?)";
        try (Connection conn = this.db.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, studentInCourse.getCourse_id());
            ps.setLong(2, studentInCourse.getStudent_id());
            ps.setBoolean(3, studentInCourse.isCourse_reviewed());
            ps.setBoolean(4, studentInCourse.isDisable_edit());
            ps.setBoolean(5, studentInCourse.isSemester_past());
            ;
            ps.executeUpdate();
            ps.close();
            return studentInCourse;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * Removes recovery requests that have expired
     *
     * @param user
     * @return
     */
    public String removeFromCourse(long student_id, long course_id) {

        String sql = "DELETE FROM " + STUDENTINCOURSES_TABLE + " WHERE student_id = ? and course_id = ? LIMIT 1";

        try (Connection conn = this.db.getConnection()) {
            // Create Prepared Statement from query
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, student_id);
            ps.setLong(2, course_id);
            // Runs query
            ps.execute();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return " ";
    }

    /**
     * Inserts a student into a course
     *
     * @param studentInCourse
     * @return
     */
    public StudentInCourse enrollStudent(StudentInCourse studentInCourse) {
        final String sql = "INSERT INTO " + STUDENTINCOURSES_TABLE + "(course_id, student_id) Values(?,?)";
        try (Connection conn = this.db.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, studentInCourse.getCourse_id());
            ps.setLong(2, studentInCourse.getStudent_id());
            ps.executeUpdate();
            ps.close();
            return studentInCourse;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * gets all the students that havent reviewed a course
     *
     * @param user
     * @return
     */

    public List<StudentInCourse> allStudentCoursesNotReviewed(User user) {
        final String SELECT = "SELECT * FROM " + STUDENTINCOURSES_TABLE + " WHERE student_id = " + user.getId()
                + " AND course_reviewed = 0 AND semester_past = 0";
        List<StudentInCourse> studentInCourses = null;
        try (Connection conn = this.db.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(SELECT);
            studentInCourses = new ArrayList<StudentInCourse>();
            try {
                ResultSet rs = ps.executeQuery(SELECT);
                while (rs.next()) {
                    studentInCourses.add(mapRow(rs));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ps.close();
            return studentInCourses;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentInCourses;
    }

    public List<StudentInCourse> allStudentCoursesReviewed(User user) {
        final String SELECT = "SELECT * FROM " + STUDENTINCOURSES_TABLE + " WHERE student_id = " + user.getId()
                + " AND course_reviewed = 1";
        List<StudentInCourse> courses = null;
        try (Connection conn = this.db.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(SELECT);
            courses = new ArrayList<StudentInCourse>();
            try {
                ResultSet rs = ps.executeQuery(SELECT);
                while (rs.next()) {
                    courses.add(mapRow(rs));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ps.close();
            return courses;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public List<StudentInCourse> listAllCourses() {
        final String SELECT = "SELECT * FROM " + STUDENTINCOURSES_TABLE;

        List<StudentInCourse> courses = null;
        try (Connection conn = this.db.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(SELECT);
            courses = new ArrayList<StudentInCourse>();
            try {
                ResultSet rs = ps.executeQuery(SELECT);
                while (rs.next()) {
                    courses.add(mapRow(rs));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ps.close();
            return courses;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public StudentInCourse findByStudentCourseId(long student_course_id) {
        // Declare SQL template query
        StudentInCourse course = null;
        String sql = "SELECT * FROM " + STUDENTINCOURSES_TABLE + " WHERE student_course_id = ? LIMIT 1";
        try (Connection conn = this.db.getConnection()) {
            // Create Prepared Statement from query
            PreparedStatement q = conn.prepareStatement(sql);
            // Fill in the ? with the parameters you want
            q.setLong(1, student_course_id);

            // Runs query
            ResultSet rs = q.executeQuery();
            if (rs.next()) {
                course = mapRow(rs);
                q.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // If you don't find a model
        return course;

    }

    public void disableEditReview(StudentInCourse course) {
        // Declare SQL template query

        String sql = "UPDATE " + STUDENTINCOURSES_TABLE + " SET disable_edit = 1 WHERE student_course_id = ? LIMIT 1";
        try (Connection conn = this.db.getConnection()) {
            // Create Prepared Statement from query
            PreparedStatement ps = conn.prepareStatement(sql);
            // Fill in the ? with the parameters you want
            ps.setLong(1, course.getStudent_course_id());
            // Runs query
            ps.execute();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSemesterPast(StudentInCourse course) {
        // Declare SQL template query

        String sql = "UPDATE " + STUDENTINCOURSES_TABLE + " SET semester_past = 1 WHERE student_course_id = ? LIMIT 1";
        try (Connection conn = this.db.getConnection()) {
            // Create Prepared Statement from query
            PreparedStatement ps = conn.prepareStatement(sql);
            // Fill in the ? with the parameters you want
            ps.setLong(1, course.getStudent_course_id());
            // Runs query
            ps.execute();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCourseReviewed(ProfessorReview review) {
        // Declare SQL template query
        String sql = "UPDATE " + STUDENTINCOURSES_TABLE
                + " SET course_reviewed = 1 WHERE student_course_id = ? LIMIT 1";
        try (Connection conn = this.db.getConnection()) {
            // Create Prepared Statement from query
            PreparedStatement ps = conn.prepareStatement(sql);
            // Fill in the ? with the parameters you want
            ps.setLong(1, review.getStudent_course_id());
            // Runs query
            ps.execute();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCourseNotReviewed(ProfessorReview review) {
        // Declare SQL template query
        String sql = "UPDATE " + STUDENTINCOURSES_TABLE
                + " SET course_reviewed = 0 WHERE student_course_id = ? LIMIT 1";
        try (Connection conn = this.db.getConnection()) {
            // Create Prepared Statement from query
            PreparedStatement ps = conn.prepareStatement(sql);
            // Fill in the ? with the parameters you want
            ps.setLong(1, review.getStudent_course_id());
            // Runs query
            ps.execute();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks to see if a student is already enrolled in a course
     *
     * @param student
     * @return
     */
    public boolean isStudentInCourse(StudentInCourse student) {
        final String SELECT = "SELECT * FROM " + STUDENTINCOURSES_TABLE + " WHERE student_id = "
                + student.getStudent_id() + " AND course_id = " + student.getCourse_id();
        List<StudentInCourse> studentInCourses;
        try (Connection conn = this.db.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(SELECT);
            studentInCourses = new ArrayList<>();
            try {
                ResultSet rs = ps.executeQuery(SELECT);
                while (rs.next()) {
                    studentInCourses.add(mapRow(rs));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (studentInCourses.size() > 0) {
                ps.close();
                return true;
            } else {
                ps.close();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
