package org.example.controller;

import org.example.DTO.CostDTO;
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
     * @param studyYear  The study year
     * @return Cost object with planned and actual costs
     * @throws courseLayoutDBException if operation fails
     */
    public CostDTO getCourseCost(int instanceId, int studyYear) throws courseLayoutDBException {
        PlannedActivity planned = new PlannedActivity(instanceId, studyYear);
        TeachingActivity teaching = new TeachingActivity("Lecture", 3.6); // Default activity type

        Cost cost = service.calculateCostForCourse(planned, teaching);
        return new CostDTO(cost.getPlannedCost(), cost.getActualCost());
    }

    public void updateStudentCount(int courseLayoutID, int numStudents, int courseInstanceID)
            throws courseLayoutDBException {
        CourseInstance instance = new CourseInstance(courseLayoutID, numStudents, courseInstanceID);
        service.updateStudentCount(instance);
    }

    /**
     * Allocates a teacher to a course activity.
     *
     * @param empId            The employee (teacher) ID
     * @param courseInstanceId The course instance ID
     * @param activityId       The activity ID
     * @throws courseLayoutDBException if operation fails
     */
    public void allocateTeacher(int empId, int courseInstanceId, int activityId, double allocatedHours)
            throws courseLayoutDBException {
        Allocation allocation = new Allocation(empId, courseInstanceId, activityId, allocatedHours);
        service.allocateTeacher(allocation);
    }

    /**
     * Deallocates a teacher from a course activity.
     *
     * @param empId              The employee (teacher) ID
     * @param courseInstanceId   The course instance ID
     * @param teachingActivityId The activity ID
     * @throws courseLayoutDBException if operation fails
     */
    public void deallocateTeacher(int empId, int courseInstanceId, int teachingActivityId)
            throws courseLayoutDBException {
        Allocation allocation = new Allocation(empId, courseInstanceId, teachingActivityId, 0.0);
        service.deallocateTeacher(allocation);
    }

    /**
     * Creates a new teaching activity type.
     *
     * @throws courseLayoutDBException if operation fails
     */
    public void createTeachingActivity(String name, double factor, int courseInstanceId, int plannedHours)
            throws courseLayoutDBException {
        TeachingActivity activity = new TeachingActivity(name, factor);
        service.createNewActivity(activity, courseInstanceId, plannedHours);
    }
}
