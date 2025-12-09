package org.example.model;

/**
 * Represents a course instance with student enrollment information.
 * This is a domain model that encapsulates course instance data.
 */
public class CourseInstance {
    private final String courseInstanceID;
    private final int courseLayoutID;
    private final int numStudents;

    /**
     * Creates a new course instance.
     *
     * @param courseInstanceID The unique course instance ID
     * @param courseLayoutID The course layout ID (foreign key)
     * @param numStudents The number of students enrolled
     */
    public CourseInstance(int courseLayoutID, int numStudents, String courseInstanceID) {
        this.courseInstanceID = courseInstanceID;
        this.courseLayoutID = courseLayoutID;
        this.numStudents = numStudents;
    }

    public String getCourseInstanceID() {
        return courseInstanceID;
    }

    public int getCourseLayoutID() {
        return courseLayoutID;
    }

    public int getNumStudents() {
        return numStudents;
    }

    @Override
    public String toString() {
        return "CourseInstance{" +
                "courseInstanceID='" + courseInstanceID + '\'' +
                ", courseLayoutID='" + courseLayoutID + '\'' +
                ", numStudents=" + numStudents +
                '}';
    }
}
