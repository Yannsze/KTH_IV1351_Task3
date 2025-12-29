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
    private static final String COURSE_INSTANCE_ID_NAME = "course_instance_id";
    private static final String STUDENT_COLUMN_NAME = "num_students";
    private static final String EMPLOYEE_CROSS_REFERENCE_TABLE_NAME = "employee_planned_activity";
    private static final String EMPLOYEE_ID_NAME = "employee_id";
    private static final String TEACHING_TABLE_NAME = "teaching_activity";
    private static final String TEACHING_TABLE_ID = "teaching_activity_id";
    private static final String AVG_SALARY = "avg_salary";

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

    // The procedure of connecting to the database (Postgres, in Yannsze's
    // localhost)
    private void connectToCourseLayoutDB() throws ClassNotFoundException, SQLException {
        String url = "jdbc:postgresql://localhost:5432/courselayout"; // modify localhost & password
        String user = "postgres";
        String password = "postgres";

        connection = DriverManager.getConnection(url, user, password);
        System.out.println("Connected!");

        connection.setAutoCommit(false);
    }

    // course instance id is an int
    private void prepareStatements() throws SQLException {
        plannedTeachingCoststmt = connection.prepareStatement(
                "WITH " + AVG_SALARY + " AS (" +
                        "    SELECT AVG(sh.salary_amount) AS avg_sal" +
                        "    FROM " + SALARY_HISTORY_TABLE + " sh" +
                        ")" +
                        " SELECT ROUND(CAST(SUM(avg_salary.avg_sal / NULLIF(pa.planned_hours * ta.factor, 0.0)) AS NUMERIC), 2)AS planned_cost"
                        +
                        " FROM " + EMPLOYEE_CROSS_REFERENCE_TABLE_NAME + " epa" +
                        " JOIN planned_activity pa ON epa.course_instance_id = CAST(pa.course_instance_id AS INTEGER) "
                        +
                        " JOIN " + TEACHING_TABLE_NAME + " ta ON epa.teaching_activity_id = ta.teaching_activity_id" +
                        " JOIN " + COURSE_INSTANCE_TABLE_NAME + " ci ON epa.course_instance_id = ci.course_instance_id"
                        +
                        " CROSS JOIN " + AVG_SALARY +
                        " WHERE ci.course_instance_id = ?" +
                        " AND ci.study_year = ?;");

        actualTeachingCoststmt = connection.prepareStatement(
                "WITH " + AVG_SALARY + " AS (" +
                        "    SELECT AVG(sh.salary_amount) AS avg_sal" +
                        "    FROM " + SALARY_HISTORY_TABLE + " sh" +
                        ")" +
                        " SELECT ROUND(CAST(SUM(avg_salary.avg_sal / NULLIF(epa.actual_allocated_hours * ta.factor, 0.0)) AS NUMERIC), 2)AS actual_cost"
                        +
                        " FROM " + EMPLOYEE_CROSS_REFERENCE_TABLE_NAME + " epa" +
                        " JOIN planned_activity pa ON epa.course_instance_id = CAST(pa.course_instance_id AS INTEGER) "
                        +
                        " JOIN " + TEACHING_TABLE_NAME + " ta ON epa.teaching_activity_id = ta.teaching_activity_id" +
                        " JOIN " + COURSE_INSTANCE_TABLE_NAME + " ci ON epa.course_instance_id = ci.course_instance_id"
                        +
                        " CROSS JOIN " + AVG_SALARY +
                        " WHERE ci.course_instance_id = ?" +
                        " AND ci.study_year = ?;");

        updateStudentCountsstmt = connection.prepareStatement(
                // int, int, varchar (string)
                "UPDATE " + COURSE_INSTANCE_TABLE_NAME + " SET " +
                        COURSE_LAYOUT_NAME + " = ?, " +
                        STUDENT_COLUMN_NAME + " = ? " +
                        "WHERE " + COURSE_INSTANCE_ID_NAME + " = ?;");

        teacherAllocateActivitystmt = connection.prepareStatement(
                // int, int, int
                "INSERT INTO " + EMPLOYEE_CROSS_REFERENCE_TABLE_NAME + " (" +
                        EMPLOYEE_ID_NAME + ", " + COURSE_INSTANCE_ID_NAME + ", " +
                        TEACHING_TABLE_ID + ", actual_allocated_hours) " +
                        "VALUES (?, ?, ?, ?);");

        teacherDeallocateActivitystmt = connection.prepareStatement(
                // int, int, int
                "DELETE FROM " + EMPLOYEE_CROSS_REFERENCE_TABLE_NAME +
                        " WHERE " + EMPLOYEE_ID_NAME + " = ? AND " +
                        COURSE_INSTANCE_ID_NAME + " = ? AND " +
                        TEACHING_TABLE_ID + " = ?;");

        insertNewTeachingActivitystmt = connection.prepareStatement(
                // string, double, int, int
                // "INSERT INTO " + TEACHING_TABLE_NAME + " (activity_name, factor) VALUES (?,
                // ?) RETURNING " + TEACHING_TABLE_ID
                "WITH new_activity AS (" +
                        " INSERT INTO " + TEACHING_TABLE_NAME + " (activity_name, factor) " +
                        " VALUES (?, ?) " +
                        " RETURNING " + TEACHING_TABLE_ID + "), " +
                        "assigned_course AS (" +
                        " INSERT INTO planned_activity(" + TEACHING_TABLE_ID + ", " + COURSE_INSTANCE_ID_NAME
                        + ", planned_hours) " +
                        " SELECT new_activity." + TEACHING_TABLE_ID + ", ?, ? " +
                        " FROM new_activity " +
                        " RETURNING " + TEACHING_TABLE_ID + ", course_instance_id" +
                        ") " +
                        "INSERT INTO employee_planned_activity(employee_id, " + COURSE_INSTANCE_ID_NAME + ", "
                        + TEACHING_TABLE_ID + ", actual_allocated_hours) " +
                        " SELECT ?, assigned_course.course_instance_id, assigned_course.teaching_activity_id, 0 " + // Default
                                                                                                                    // 0
                                                                                                                    // hours
                                                                                                                    // for
                                                                                                                    // initial
                                                                                                                    // allocation
                        " FROM assigned_course;");
    }

    public Cost plannedActualCosts(PlannedActivity plannedActivity, TeachingActivity teachingActivity)
            throws courseLayoutDBException {
        String failureMsg = "Could not get planned and actual cost: " + plannedActivity + ", " + teachingActivity;
        try {
            plannedTeachingCoststmt.setInt(1, plannedActivity.getCourseInstanceId());
            plannedTeachingCoststmt.setInt(2, plannedActivity.getStudyYear());
            ResultSet rsPlanned = plannedTeachingCoststmt.executeQuery(); // use executeQuery, not executeUpdate
            double plannedCost = 0;
            if (rsPlanned.next()) {
                plannedCost = rsPlanned.getDouble("planned_cost");
            } else {
                handleException(failureMsg, null); // no rows returned
            }
            rsPlanned.close();

            actualTeachingCoststmt.setInt(1, plannedActivity.getCourseInstanceId());
            actualTeachingCoststmt.setInt(2, plannedActivity.getStudyYear());

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
            updateStudentCountsstmt.setInt(1, courseInstance.getCourseLayoutID());
            updateStudentCountsstmt.setInt(2, courseInstance.getNumStudents());
            updateStudentCountsstmt.setInt(3, courseInstance.getCourseInstanceID());
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
            teacherAllocateActivitystmt.setInt(1, allocation.getEmployeeID());
            teacherAllocateActivitystmt.setInt(2, allocation.getCourseInstanceID());
            teacherAllocateActivitystmt.setInt(3, allocation.getTeachingActivityID());
            teacherAllocateActivitystmt.setDouble(4, allocation.getAllocatedHours());
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
            teacherDeallocateActivitystmt.setInt(1, allocation.getEmployeeID());
            teacherDeallocateActivitystmt.setInt(2, allocation.getCourseInstanceID());
            teacherDeallocateActivitystmt.setInt(3, allocation.getTeachingActivityID());

            int teacherActivityUpdate = teacherDeallocateActivitystmt.executeUpdate();
            if (teacherActivityUpdate != 1) {
                handleException(failureMsg, null);
            }

            connection.commit();

        } catch (SQLException e) {
            handleException(failureMsg, e); // if >4 courses → trigger throws here
        }
    }

    // Insert
    public void insertNewTeachingActivity(TeachingActivity teachingActivity, int courseInstanceId, int plannedHours)
            throws courseLayoutDBException {
        String failureMsg = "Could not insert teaching activity: " + teachingActivity;
        try {
            // 1. Check if activity exists
            int activityId = -1;
            PreparedStatement checkStmt = connection.prepareStatement(
                    "SELECT " + TEACHING_TABLE_ID + " FROM " + TEACHING_TABLE_NAME + " WHERE activity_name = ?");
            checkStmt.setString(1, teachingActivity.getActivityName());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                activityId = rs.getInt(TEACHING_TABLE_ID);
            }
            rs.close();

            // 2. If not exists, insert it
            if (activityId == -1) {
                // We use a simple insert here instead of the complex CTE since we broke it down
                PreparedStatement insertActivityStmt = connection.prepareStatement(
                        "INSERT INTO " + TEACHING_TABLE_NAME + " (activity_name, factor) VALUES (?, ?) RETURNING "
                                + TEACHING_TABLE_ID);
                insertActivityStmt.setString(1, teachingActivity.getActivityName());
                insertActivityStmt.setDouble(2, teachingActivity.getFactor());
                ResultSet rsInsert = insertActivityStmt.executeQuery();
                if (rsInsert.next()) {
                    activityId = rsInsert.getInt(TEACHING_TABLE_ID);
                }
                rsInsert.close();
            }

            if (activityId == -1) {
                throw new SQLException("Failed to obtain activity ID.");
            }

            // 3. Insert into planned_activity for this course
            PreparedStatement planStmt = connection.prepareStatement(
                    "INSERT INTO planned_activity (" + TEACHING_TABLE_ID + ", " + COURSE_INSTANCE_ID_NAME
                            + ", planned_hours) VALUES (?, ?, ?)");
            planStmt.setInt(1, activityId);
            planStmt.setInt(2, courseInstanceId);
            planStmt.setInt(3, plannedHours);
            planStmt.executeUpdate();

            // 4. Allocate to default employee (ID 1)
            PreparedStatement allocStmt = connection.prepareStatement(
                    "INSERT INTO employee_planned_activity (employee_id, " + COURSE_INSTANCE_ID_NAME + ", "
                            + TEACHING_TABLE_ID + ", actual_allocated_hours) VALUES (?, ?, ?, ?)");
            allocStmt.setInt(1, 1); // Default Employee 1
            allocStmt.setInt(2, courseInstanceId);
            allocStmt.setInt(3, activityId);
            allocStmt.setDouble(4, 0); // Default 0 hours
            allocStmt.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
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
