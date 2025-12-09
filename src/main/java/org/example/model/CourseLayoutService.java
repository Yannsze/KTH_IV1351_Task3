package org.example.model;

import org.example.integration.courseLayoutDAO;
import org.example.integration.courseLayoutDBException;

/**
 * Service layer containing business logic for course layout operations.
 * This layer sits between the Controller and DAO, handling validation and orchestration.
 */
public class CourseLayoutService {
    private final courseLayoutDAO courseLayoutDAO;

    public CourseLayoutService(courseLayoutDAO courseLayoutDAO) {
        this.courseLayoutDAO = courseLayoutDAO;
    }

    /**
     * Calculates both planned and actual costs for a course.
     *
     * @param plannedActivity The planned activity details
     * @param teachingActivity The teaching activity type
     * @return Cost object with planned and actual costs
     * @throws courseLayoutDBException if database operation fails
     */
    public Cost calculateCostForCourse(PlannedActivity plannedActivity, TeachingActivity teachingActivity)
            throws courseLayoutDBException {
        // Validation
        if (plannedActivity == null || teachingActivity == null) {
            throw new IllegalArgumentException("Planned activity and teaching activity cannot be null");
        }
        
        return courseLayoutDAO.plannedActualCosts(plannedActivity, teachingActivity);
    }

    /**
     * Updates the student count for a course instance.
     *
     * @param instance The course instance with updated student count
     * @throws courseLayoutDBException if database operation fails
     */
    public void updateStudentCount(CourseInstance instance) throws courseLayoutDBException {
        // Validation
        if (instance == null) {
            throw new IllegalArgumentException("Course instance cannot be null");
        }
        if (instance.getNumStudents() < 0) {
            throw new IllegalArgumentException("Number of students cannot be negative");
        }
        
        courseLayoutDAO.updateStudent(instance);
    }

    /**
     * Allocates a teacher to a course activity.
     *
     * @param allocation The allocation details
     * @throws courseLayoutDBException if database operation fails
     */
    public void allocateTeacher(Allocation allocation) throws courseLayoutDBException {
        // Validation
        if (allocation == null) {
            throw new IllegalArgumentException("Allocation cannot be null");
        }
        if (allocation.getEmployeeID() == 0) {
            throw new IllegalArgumentException("Employee ID cannot be empty");
        }
        if (allocation.getCourseInstanceID() == 0) {
            throw new IllegalArgumentException("Course instance ID cannot be empty");
        }
        if (allocation.getTeachingActivityID() == 0) {
            throw new IllegalArgumentException("Activity ID cannot be empty");
        }
        
        courseLayoutDAO.allocateTeacherActivity(allocation);
    }

    /**
     * Deallocates a teacher from a course activity.
     *
     * @param allocation The allocation details to remove
     * @throws courseLayoutDBException if database operation fails
     */
    public void deallocateTeacher(Allocation allocation) throws courseLayoutDBException {
        // Validation
        if (allocation == null) {
            throw new IllegalArgumentException("Allocation cannot be null");
        }
        
        courseLayoutDAO.deallocatedTeacherActivity(allocation);
    }

    /**
     * Creates a new teaching activity type.
     *
     * @param activity The teaching activity to create
     * @throws courseLayoutDBException if database operation fails
     */
    public void createNewActivity(TeachingActivity activity) throws courseLayoutDBException {
        // Validation
        if (activity == null) {
            throw new IllegalArgumentException("Teaching activity cannot be null");
        }
        if (activity.getActivityName() == null || activity.getActivityName().trim().isEmpty()) {
            throw new IllegalArgumentException("Activity name cannot be empty");
        }
        
        courseLayoutDAO.insertNewTeachingActivity(activity);
    }
}