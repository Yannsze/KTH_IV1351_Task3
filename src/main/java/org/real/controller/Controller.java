package org.real.controller;

import org.real.integration.courseLayoutDAO;
import java.sql.SQLException;

public class Controller {
    private final courseLayoutDAO dao;

    public Controller() throws SQLException {
        this.dao = new courseLayoutDAO();
    }

    public String computeCost(String instanceId) {
        try {
            return dao.computeCost(instanceId);
        } catch (SQLException e) {
            return "Error calculating cost: " + e.getMessage();
        }
    }

    public String modifyStudentCount(String instanceId, int increase) {
        try {
            dao.modifyStudentCount(instanceId, increase);
            return "Student count updated successfully.";
        } catch (SQLException e) {
            return "Error updating students: " + e.getMessage();
        }
    }

    public String allocateTeacher(int teacherId, int activityId, String instanceId, int hours) {
        try {
            dao.allocateTeacher(teacherId, activityId, instanceId, hours);
            return "Teacher allocated successfully.";
        } catch (SQLException e) {
            return "Error allocating teacher: " + e.getMessage();
        }
    }

    public String deallocateTeacher(int teacherId, int activityId, String instanceId) {
        try {
            dao.deallocateTeacher(teacherId, activityId, instanceId);
            return "Teacher deallocated successfully.";
        } catch (SQLException e) {
            return "Error deallocating teacher: " + e.getMessage();
        }
    }

    public String addExerciseActivity(String instanceId, int teacherId) {
        try {
            dao.addExerciseActivity(instanceId, teacherId);
            return "Exercise activity added and teacher allocated.";
        } catch (SQLException e) {
            return "Error adding activity: " + e.getMessage();
        }
    }
}
