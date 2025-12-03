package org.example.integration;

import org.example.model.CourseInstanceDTO;
import java.sql.*;

public class courseLayoutDAO {
    private static final String STUDENT_TABLE_NAME = "course_instance";
    private static final String STUDENT_COURSE_LAYOUT_NAME = "course_layout_id";
    private static final String STUDENT_COURSE_INSTANCE_NAME = "instance_id";
    private static final String STUDENT_COLUMN_NAME = "num_students";

    private Connection connection;
    private PreparedStatement getCourseCost;
    private PreparedStatement updateStudentCounts;
    private PreparedStatement insertTeachingActivity;
    private PreparedStatement allocateTeacher;
    private PreparedStatement teacherLoadCount;
    private PreparedStatement teacherAllocateActivity;
    private PreparedStatement deallocatedTeacher;

    // Construct new DAO objects connected to the database
    public courseLayoutDAO() throws courseLayoutDBException {
        try {
            connectToCourseLayoutDB();
            prepareStatements();
        } catch (ClassNotFoundException | SQLException exception) {
            throw new courseLayoutDBException("Could not connect to datasource.", exception);
        }
    }

    private void connectToCourseLayoutDB() throws ClassNotFoundException, SQLException {
        String url = "jdbc:postgresql://localhost:5432/courselayout"; // modify localhost & password
        String user = "postgres";
        String password = "postgres";

        connection = DriverManager.getConnection(url, user, password);
        System.out.println("Connected!");

        connection.setAutoCommit(false);
    }

    private void prepareStatements() throws SQLException {
        updateStudentCounts = connection.prepareStatement(
                "UPDATE " + STUDENT_TABLE_NAME + " SET " +
                        STUDENT_COURSE_LAYOUT_NAME + " = ?, " +
                        STUDENT_COLUMN_NAME + " = ? " +
                        "WHERE " + STUDENT_COURSE_INSTANCE_NAME + " = ?"
        );

        teacherAllocateActivity = connection.prepareStatement(
                "INSERT INTO "
        );
    }

    public void updateStudentstmt(CourseInstanceDTO courseInstance) throws courseLayoutDBException {
        String failureMsg = "Could not update the course instance: " + courseInstance;
        try {
            updateStudentCounts.setString(1, courseInstance.getCourseLayoutID());
            updateStudentCounts.setInt(2, courseInstance.getCourseStudents());
            updateStudentCounts.setString(3, courseInstance.getCourseInstanceID());
            int numStudentsUpdate = updateStudentCounts.executeUpdate();

            if (numStudentsUpdate != 1) {
                handleException(failureMsg, null);
            }

            connection.commit();
            System.out.print("Student count updated!");
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
    }

    public void allocateTeacherActivity() {

    }

    private void handleException(String failureMsg, Exception cause) throws courseLayoutDBException {
        String completeFailureMsg = failureMsg;
        try {
            connection.rollback();
        } catch (SQLException rollbackExc) {
            completeFailureMsg = completeFailureMsg +
                    ". Also failed to rollback transaction because of: " + rollbackExc.getMessage();
        }

        if (cause != null) {
            throw new courseLayoutDBException(failureMsg, cause);
        } else {
            throw new courseLayoutDBException(failureMsg);
        }
    }

    // Throw exception if unable to commit
    public void commit() throws courseLayoutDBException {
        try {
            connection.commit();
        } catch (SQLException e) {
            handleException("Failed to commit", e);
        }
    }
}
