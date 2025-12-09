package org.example.model;

/**
 * Represents an allocation of a teacher to a course activity.
 * This is a domain model that encapsulates allocation data.
 */
public class Allocation {
    private final String employeeID;
    private final String courseInstanceID;
    private final String activityID;

    /**
     * Creates a new allocation.
     *
     * @param employeeID The employee (teacher) ID
     * @param courseInstanceID The course instance ID
     * @param activityID The activity ID
     */
    public Allocation(String employeeID, String courseInstanceID, String activityID) {
        this.employeeID = employeeID;
        this.courseInstanceID = courseInstanceID;
        this.activityID = activityID;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getCourseInstanceID() {
        return courseInstanceID;
    }

    public String getActivityID() {
        return activityID;
    }

    @Override
    public String toString() {
        return "Allocation{" +
                "employeeID='" + employeeID + '\'' +
                ", courseInstanceID='" + courseInstanceID + '\'' +
                ", activityID='" + activityID + '\'' +
                '}';
    }
}
