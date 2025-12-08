package org.example.controller;

import org.example.DTO.*;
import org.example.integration.courseLayoutDAO;
import org.example.integration.courseLayoutDBException;
import org.example.model.*;

public class Controller {
    private CourseLayoutService courseLayoutService;

    public Controller() throws courseLayoutDBException {
        this.courseLayoutService = new CourseLayoutService(new courseLayoutDAO());
    }

    public CostImplementation getCourseCost(int instanceId, String studyYear) throws courseLayoutDBException {
        PlannedActivityDTO planned = new PlannedActivityImpl(instanceId, studyYear);
        TeachingActivityDTO teaching = new TeachingActivityImpl("Lecture"); // example
        return courseLayoutService.calculateCostForCourse(planned, teaching);
    }

    // Update students
    public void updateStudentCount(String instanceId, String layoutId, int numStudents) throws courseLayoutDBException {
        CourseInstanceDTO instance = new CourseInstanceImpl(instanceId, layoutId, numStudents);
        courseLayoutService.updateStudentCount(instance);
    }

    // Allocate teacher
    public void allocateTeacher(String empId, String courseInstanceId, String activityId) throws courseLayoutDBException {
        AllocationDTO allocation = new AllocationImpl(empId, courseInstanceId, activityId);
        courseLayoutService.allocateTeacher(allocation);
    }

    // Deallocating the teacher
    public void deallocateTeacher(String empId, String courseInstanceId, String activityId) throws courseLayoutDBException {
        AllocationDTO allocation = new AllocationImpl(empId, courseInstanceId, activityId);
        courseLayoutService.deallocateTeacher(allocation);
    }

    // Adding new activity
    public void createTeachingActivity(String name) throws courseLayoutDBException {
        TeachingActivityDTO activity = new TeachingActivityImpl(name);
        courseLayoutService.createNewActivity(activity);
    }
}