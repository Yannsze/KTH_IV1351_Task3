package org.example.model;

/**
 * Represents a planned activity for a course instance.
 * This is a domain model that encapsulates planned activity data.
 */
public class PlannedActivity {
    private final int courseInstanceId;
    private final int studyYear;

    /**
     * Creates a new planned activity.
     *
     * @param courseInstanceId The course instance ID (integer primary key)
     * @param studyYear The study year (e.g., "2024/2025")
     */
    public PlannedActivity(int courseInstanceId, int studyYear) {
        this.courseInstanceId = courseInstanceId;
        this.studyYear = studyYear;
    }

    public int getCourseInstanceId() {
        return courseInstanceId;
    }

    public int getStudyYear() {
        return studyYear;
    }

    @Override
    public String toString() {
        return "PlannedActivity{" +
                "courseInstanceId=" + courseInstanceId +
                ", studyYear='" + studyYear + '\'' +
                '}';
    }
}
