package org.example.model;

/**
 * Represents a planned activity for a course instance.
 * This is a domain model that encapsulates planned activity data.
 */
public class PlannedActivity {
    private final String courseInstanceId;
    private final String studyYear;

    /**
     * Creates a new planned activity.
     *
     * @param courseInstanceId The course instance ID
     * @param studyYear The study year (e.g., "2024/2025")
     */
    public PlannedActivity(String courseInstanceId, String studyYear) {
        this.courseInstanceId = courseInstanceId;
        this.studyYear = studyYear;
    }

    public String getCourseInstanceId() {
        return courseInstanceId;
    }

    public String getStudyYear() {
        return studyYear;
    }

    @Override
    public String toString() {
        return "PlannedActivity{" +
                "courseInstanceId='" + courseInstanceId + '\'' +
                ", studyYear='" + studyYear + '\'' +
                '}';
    }
}
