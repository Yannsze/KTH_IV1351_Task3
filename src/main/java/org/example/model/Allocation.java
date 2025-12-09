package org.example.model;

/**
 * Represents an allocation of a teacher to a course activity.
 * This is a domain model that encapsulates allocation data.
 */
public class Allocation {
    private final int employeeID;
    private final int courseInstanceID;
    private final int teachingactivityID;

    /**
     * Creates a new allocation.
     *
     * @param employeeID The employee (teacher) ID
     * @param courseInstanceID The course instance ID
     * @param teachingactivityID The activity ID
     */
    public Allocation(int employeeID, int courseInstanceID, int teachingactivityID) {
        this.employeeID = employeeID;
        this.courseInstanceID = courseInstanceID;
        this.teachingactivityID = teachingactivityID;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public int getCourseInstanceID() {
        return courseInstanceID;
    }

    public int getTeachingActivityID() {
        return teachingactivityID;
    }

    @Override
    public String toString() {
        return "Allocation{" +
                "employeeID='" + employeeID + '\'' +
                ", courseInstanceID='" + courseInstanceID + '\'' +
                ", activityID='" + teachingactivityID + '\'' +
                '}';
    }
}
