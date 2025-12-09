package org.example.model;

/**
 * Represents a teaching activity type (e.g., Lecture, Exercise, Lab).
 * This is a domain model that encapsulates teaching activity data.
 */
public class TeachingActivity {
    private final String activityName;

    /**
     * Creates a new teaching activity.
     *
     * @param activityName The name of the activity (e.g., "Lecture", "Exercise")
     */
    public TeachingActivity(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityName() {
        return activityName;
    }

    @Override
    public String toString() {
        return "TeachingActivity{" +
                "activityName='" + activityName + '\'' +
                '}';
    }
}
