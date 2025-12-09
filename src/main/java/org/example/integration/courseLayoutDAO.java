package org.example.integration;

import org.example.model.Allocation;
import org.example.model.CourseInstance;
import org.example.model.PlannedActivity;
import org.example.model.TeachingActivity;
import org.example.model.Cost;

import java.sql.*;

public class courseLayoutDAO {
    private static final String SALARY_HISTORY_TABLE = "salary_history";
    private static final String COURSE_INSTANCE_TABLE_NAME = "course_instance";
    private static final String COURSE_LAYOUT_NAME = "course_layout_id";
    private static final String COURSE_INSTANCE_ID_NAME = "instance_id";
    private static final String STUDENT_COLUMN_NAME = "num_students";
    private static final String EMPLOYEE_CROSS_REFERENCE_TABLE_NAME = "employee_planned_activity";
    private static final String EMPLOYEE_ID_NAME = "employee_id";
    private static final String PLANNED_ACTIVITY_ID_NAME = "activity_id";
    private static final String TEACHING_TABLE_NAME = "teaching_activity";

    private Connection connection;
    private PreparedStatement plannedTeachingCoststmt;
    private PreparedStatement actualTeachingCoststmt;
    private PreparedStatement updateStudentCountsstmt;
    private PreparedStatement insertNewTeachingActivitystmt;
    private PreparedStatement teacherAllocateActivitystmt;
    private PreparedStatement teacherDeallocateActivitystmt;

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
        plannedTeachingCoststmt = connection.prepareStatement(
                "WITH avg_salary AS (" +
                        "    SELECT AVG(sh.salary_amount) AS avg_sal" +
                        "    FROM " + SALARY_HISTORY_TABLE + " sh" +
                        ")" +
                        " SELECT SUM(avg_salary.avg_sal / (pa.planned_hours * ta.factor)) AS planned_cost" +
                        " FROM " + EMPLOYEE_CROSS_REFERENCE_TABLE_NAME + " epa" +
                        " JOIN planned_activity pa ON epa.course_instance_id = pa.course_instance_id " +
                        " JOIN " + TEACHING_TABLE_NAME + " ta ON epa.teaching_activity_id = ta.teaching_activity_id" +
                        " JOIN " + COURSE_INSTANCE_TABLE_NAME + " ci ON epa.course_instance_id = ci.course_instance_id" +
                        " CROSS JOIN avg_salary" +
                        " WHERE ci.course_instance_id = ?" +
                        " AND ci.study_year = ?;"
        );

        actualTeachingCoststmt = connection.prepareStatement(
                "SELECT SUM(sh.salary_amount / (epa.actual_allocated_hours * ta.factor)) AS actual_cost" +
                        " FROM " + EMPLOYEE_CROSS_REFERENCE_TABLE_NAME+ " epa" +
                        " JOIN " + TEACHING_TABLE_NAME + " ta ON epa.teaching_activity_id = ta.teaching_activity_id" +
                        " JOIN " + COURSE_INSTANCE_TABLE_NAME + " ci ON epa.course_instance_id = ci.course_instance_id" +
                        " JOIN " + SALARY_HISTORY_TABLE + " sh ON sh.employee_id = epa.employee_id" +
                        "   AND sh.valid_from = (" +
                        "       SELECT MAX(valid_from)" +
                        "       FROM salary_history" +
                        "       WHERE employee_id = epa.employee_id" +
                        "       AND valid_from <= ci.start_date" +
                        "   )" +
                        " WHERE ci.course_instance_id = ?" +
                        " AND ci.study_year = ?;"
        );

        updateStudentCountsstmt = connection.prepareStatement(
                "UPDATE " + COURSE_INSTANCE_TABLE_NAME + " SET " +
                        COURSE_LAYOUT_NAME + " = ?, " +
                        STUDENT_COLUMN_NAME + " = ? " +
                        "WHERE " + COURSE_INSTANCE_ID_NAME + " = ?"
        );

        teacherAllocateActivitystmt = connection.prepareStatement(
                "INSERT INTO " + EMPLOYEE_CROSS_REFERENCE_TABLE_NAME + " (" +
                        EMPLOYEE_ID_NAME + ", " + COURSE_INSTANCE_ID_NAME + ", " +
                        PLANNED_ACTIVITY_ID_NAME + ") " +
                        "VALUES (?, ?, ?)"
        );

        teacherDeallocateActivitystmt = connection.prepareStatement(
                "DELETE FROM " + EMPLOYEE_CROSS_REFERENCE_TABLE_NAME +
                        "WHERE " + EMPLOYEE_ID_NAME + " = ? AND " +
                        COURSE_INSTANCE_ID_NAME + " = ? AND " +
                        PLANNED_ACTIVITY_ID_NAME + " = ?"
        );

        insertNewTeachingActivitystmt = connection.prepareStatement(
                "INSERT INTO " + TEACHING_TABLE_NAME + " (activity_name) VALUES (?) RETURNING " +  PLANNED_ACTIVITY_ID_NAME
        );
    }

    public Cost plannedActualCosts(PlannedActivity plannedActivity, TeachingActivity teachingActivity) throws courseLayoutDBException {
        String failureMsg = "Could not get planned and actual cost: " + plannedActivity + ", " + teachingActivity ;
        try {
            plannedTeachingCoststmt.setString(1, plannedActivity.getCourseInstanceId());
            plannedTeachingCoststmt.setString(2, plannedActivity.getStudyYear());
            ResultSet rsPlanned = plannedTeachingCoststmt.executeQuery(); // use executeQuery, not executeUpdate
            double plannedCost = 0;
            if (rsPlanned.next()) {
                plannedCost = rsPlanned.getDouble("planned_cost");
            } else {
                handleException(failureMsg, null); // no rows returned
            }
            rsPlanned.close();

            actualTeachingCoststmt.setString(1, plannedActivity.getCourseInstanceId());
            actualTeachingCoststmt.setString(2, plannedActivity.getStudyYear());

            ResultSet rsActual = actualTeachingCoststmt.executeQuery();
            double actualCost = 0;
            if (rsActual.next()) {
                actualCost = rsActual.getDouble("actual_cost");
            } else {
                handleException(failureMsg, null);
            }
            rsActual.close();

            connection.commit();
            return new Cost(plannedCost, actualCost);
        } catch (SQLException e) {
            handleException(failureMsg, e);
            return null; // required by Java
        }
    }

    public void updateStudent(CourseInstance courseInstance) throws courseLayoutDBException {
        String failureMsg = "Could not update the course instance: " + courseInstance;
        try {
            updateStudentCountsstmt.setString(1, courseInstance.getCourseLayoutID());
            updateStudentCountsstmt.setInt(2, courseInstance.getNumStudents());
            updateStudentCountsstmt.setString(3, courseInstance.getCourseInstanceID());
            int numStudentsUpdate = updateStudentCountsstmt.executeUpdate();

            if (numStudentsUpdate != 1) {
                handleException(failureMsg, null);
            }

            connection.commit();

        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
    }

    // Insert
    public void allocateTeacherActivity(Allocation allocation) throws courseLayoutDBException {
        String failureMsg = "Could not allocate teaching activity: " + allocation;
        try {
            teacherAllocateActivitystmt.setString(1, allocation.getEmployeeID());
            teacherAllocateActivitystmt.setString(2, allocation.getCourseInstanceID());
            teacherAllocateActivitystmt.setString(3, allocation.getActivityID());
            int teacherActivityUpdate = teacherAllocateActivitystmt.executeUpdate();

            if (teacherActivityUpdate != 1) {
                handleException(failureMsg, null);
            }

            connection.commit();

        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
    }

    // Delete
    public void deallocatedTeacherActivity(Allocation allocation) throws courseLayoutDBException {
        String failureMsg = "Could not deallocated activity: " + allocation;
        try {
            teacherDeallocateActivitystmt.setString(1, allocation.getEmployeeID());
            teacherDeallocateActivitystmt.setString(2, allocation.getCourseInstanceID());
            teacherDeallocateActivitystmt.setString(3, allocation.getActivityID());

            int teacherActivityUpdate = teacherDeallocateActivitystmt.executeUpdate();
            if (teacherActivityUpdate != 1) {
                handleException(failureMsg, null);
            }

            connection.commit();

        } catch (SQLException e) {
            handleException(failureMsg, e);   // if >4 courses → trigger throws here
        }
    }

    // Insert
    public void insertNewTeachingActivity(TeachingActivity teachingActivity) throws courseLayoutDBException {
        String failureMsg = "Could not insert teaching activity: " + teachingActivity;
        try {
            insertNewTeachingActivitystmt.setString(1, teachingActivity.getActivityName());

            int insertTeachingActivity = insertNewTeachingActivitystmt.executeUpdate();
            if (insertTeachingActivity != 1) {
                handleException(failureMsg, null);
            }

            connection.commit();
        }
        catch (SQLException e) {
            handleException(failureMsg, e);
        }
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
