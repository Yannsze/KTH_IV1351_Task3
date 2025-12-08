package org.example.model;

import org.example.DTO.TeachingActivityDTO;

public class TeachingActivityImpl implements TeachingActivityDTO {
    private final String activityName;

    public TeachingActivityImpl(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityName() {
        return activityName;
    }
}