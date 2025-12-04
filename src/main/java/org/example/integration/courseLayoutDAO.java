package org.example.integration;

import org.example.model.AllocationDTO;
import org.example.model.CourseInstanceDTO;

import java.sql.*;

public class courseLayoutDAO {
    private static final String COURSE_INSTANCE_TABLE_NAME = "course_instance";
    private static final String COURSE_LAYOUT_NAME = "course_layout_id";
    private static final String COURSE_INSTANCE_ID_NAME = "instance_id";
    private static final String STUDENT_COLUMN_NAME = "num_students";
    private static final String EMPLOYEE_CROSS_REFERENCE_TABLE_NAME = "employee_planned_activity";
    private static final String EMPLOYEE_ID_NAME = "employee_id";
    private static final String PLANNED_ACTIVITY_ID_NAME = "activity_id";
    private static final String TEACHING_TABLE_NAME = "teaching_activity";


    private Connection connection;
    private PreparedStatement getCourseCost;
    private PreparedStatement updateStudentCounts;
    private PreparedStatement insertNewTeachingActivity;
    private PreparedStatement teacherAllocateActivity;
    private PreparedStatement teacherDeallocateActivity;

    // Construct new DAO objects connected to the database
    public courseLayoutDAO() throws courseLayoutDBException {
        try {
            connectToCourseLayoutDB();
            prepareStatements();
        } catch (ClassNotFoundException | SQLException exception) {
            throw new courseLayoutDBException("Could not connect to datasource.", exception);
        }
    }

    // The procedure of connecting to the database (Postgres, in Yannsze's localhost)
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
                "UPDATE " + COURSE_INSTANCE_TABLE_NAME + " SET " +
                        COURSE_LAYOUT_NAME + " = ?, " +
                        STUDENT_COLUMN_NAME + " = ? " +
                        "WHERE " + COURSE_INSTANCE_ID_NAME + " = ?"
        );

        teacherAllocateActivity = connection.prepareStatement(
                "INSERT INTO " + EMPLOYEE_CROSS_REFERENCE_TABLE_NAME + " (" +
                        EMPLOYEE_ID_NAME + ", " + COURSE_INSTANCE_ID_NAME + ", " +
                        PLANNED_ACTIVITY_ID_NAME + ") " +
                        "VALUES (?, ?, ?)"
        );

        teacherDeallocateActivity = connection.prepareStatement(
                "DELETE FROM " + EMPLOYEE_CROSS_REFERENCE_TABLE_NAME +
                        "WHERE " + EMPLOYEE_ID_NAME + " = ? AND " +
                        COURSE_INSTANCE_ID_NAME + " = ? AND " +
                        PLANNED_ACTIVITY_ID_NAME + " = ?"
        );

        insertNewTeachingActivity = connection.prepareStatement(
                "INSERT INTO " + TEACHING_TABLE_NAME + " (activity_name) VALUES (?) RETURNING " +  PLANNED_ACTIVITY_ID_NAME
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

    // Insert
    public void allocateTeacherActivitystmt(AllocationDTO allocation) throws courseLayoutDBException {
        String failureMsg = "Could not allocate teaching activity: " + allocation;
        try {
            teacherAllocateActivity.setString(1, allocation.getEmployeeID());
            teacherAllocateActivity.setString(2, allocation.getCourseInstanceID());
            teacherAllocateActivity.setString(3, allocation.getActivityID());
            int teacherActivityUpdate = teacherAllocateActivity.executeUpdate();

            if (teacherActivityUpdate != 1) {
                handleException(failureMsg, null);
            }

            connection.commit();
            System.out.print("Teaching activity allocated!");
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
    }

    // Delete
    public void deallocatedTeacherActivitystmt(AllocationDTO allocation) throws courseLayoutDBException {
        String failureMsg = "Could not deallocated activity: " + allocation;
        try {
            teacherDeallocateActivity.setString(1, allocation.getEmployeeID());
            teacherDeallocateActivity.setString(2, allocation.getCourseInstanceID());
            teacherDeallocateActivity.setString(3, allocation.getActivityID());

            int teacherActivityUpdate = teacherDeallocateActivity.executeUpdate();
            if (teacherActivityUpdate != 1) {
                handleException(failureMsg, null);
            }

            connection.commit();
            System.out.println("Teacher deallocated successfully!");

        } catch (SQLException e) {
            handleException(failureMsg, e);   // if >4 courses → trigger throws here
        }
    }

    // Insert
    public void insertNewTeachingActivitystmt() throws courseLayoutDBException {

    }







    // Exceptions handler
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
