package org.example.controller;

import org.example.integration.courseLayoutDAO;
import org.example.integration.courseLayoutDBException;

// Domain models
import org.example.model.Allocation;
import org.example.model.Cost;
import org.example.model.CourseInstance;
import org.example.model.PlannedActivity;
import org.example.model.TeachingActivity;

// Service layer
import org.example.model.CourseLayoutService;

/**
 * Controller layer - handles user requests from the view.
 * Delegates business logic to the service layer.
 * Follows MVC architecture: View → Controller → Service → DAO
 */
public class Controller {
    private final CourseLayoutService service;

    /**
     * Creates a new controller and initializes the service layer.
     *
     * @throws courseLayoutDBException if database connection fails
     */
    public Controller() throws courseLayoutDBException {
        courseLayoutDAO dao = new courseLayoutDAO();
        this.service = new CourseLayoutService(dao);
    }

    /**
     * Gets the planned and actual cost for a course.
     *
     * @param instanceId The course instance ID
     * @param studyYear The study year
     * @return Cost object with planned and actual costs
     * @throws courseLayoutDBException if operation fails
     */
    public Cost getCourseCost(int instanceId, String studyYear) throws courseLayoutDBException {
        PlannedActivity planned = new PlannedActivity(instanceId, studyYear);
        TeachingActivity teaching = new TeachingActivity("Lecture"); // Default activity type
        return service.calculateCostForCourse(planned, teaching);
    }

    /**
     * Updates the student count for a course instance.
     *
     * @param instanceId The course instance ID
     * @param layoutId The course layout ID
     * @param numStudents The new number of students
     * @throws courseLayoutDBException if operation fails
     */
    public void updateStudentCount(String instanceId, String layoutId, int numStudents) 
            throws courseLayoutDBException {
        CourseInstance instance = new CourseInstance(instanceId, layoutId, numStudents);
        service.updateStudentCount(instance);
    }

    /**
     * Allocates a teacher to a course activity.
     *
     * @param empId The employee (teacher) ID
     * @param courseInstanceId The course instance ID
     * @param activityId The activity ID
     * @throws courseLayoutDBException if operation fails
     */
    public void allocateTeacher(String empId, String courseInstanceId, String activityId) 
            throws courseLayoutDBException {
        Allocation allocation = new Allocation(empId, courseInstanceId, activityId);
        service.allocateTeacher(allocation);
    }

    /**
     * Deallocates a teacher from a course activity.
     *
     * @param empId The employee (teacher) ID
     * @param courseInstanceId The course instance ID
     * @param activityId The activity ID
     * @throws courseLayoutDBException if operation fails
     */
    public void deallocateTeacher(String empId, String courseInstanceId, String activityId) 
            throws courseLayoutDBException {
        Allocation allocation = new Allocation(empId, courseInstanceId, activityId);
        service.deallocateTeacher(allocation);
    }

    /**
     * Creates a new teaching activity type.
     *
     * @param name The name of the activity (e.g., "Exercise", "Lab")
     * @throws courseLayoutDBException if operation fails
     */
    public void createTeachingActivity(String name) throws courseLayoutDBException {
        TeachingActivity activity = new TeachingActivity(name);
        service.createNewActivity(activity);
    }
}
