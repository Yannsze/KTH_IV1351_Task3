package org.example.model;

/**
 * Represents a teaching activity type (e.g., Lecture, Exercise, Lab).
 * This is a domain model that encapsulates teaching activity data.
 */
public class TeachingActivity {
    private final String activityName;
    private double factor;

    /**
     * Creates a new teaching activity.
     *
     * @param activityName The name of the activity (e.g., "Lecture", "Exercise")
     */
    public TeachingActivity(String activityName, double factor) {
        this.activityName = activityName;
        this.factor = factor;
    }

    public String getActivityName() {
        return activityName;
    }

    public double getFactor() {
        return factor;
    }

    @Override
    public String toString() {
        return "TeachingActivity{" +
                "activityName='" + activityName + '\'' +
                '}';
    }
}
