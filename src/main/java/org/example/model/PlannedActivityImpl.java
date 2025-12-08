package org.example.model;

import org.example.DTO.PlannedActivityDTO;

public class PlannedActivityImpl implements PlannedActivityDTO {
    private final int courseInstanceId;
    private final String studyYear;

    public PlannedActivityImpl(int courseInstanceId, String studyYear) {
        this.courseInstanceId = courseInstanceId;
        this.studyYear = studyYear;
    }

    public int getCourseInstanceId() {
        return courseInstanceId;
    }

    public String getStudyYear() {
        return studyYear;
    }
}

