package org.example.model;

import org.example.DTO.CourseInstanceDTO;

public class CourseInstanceImpl implements CourseInstanceDTO {
    private final int courseStudents;
    private final String courseLayoutID;
    private final String courseInstanceID;

    public CourseInstanceImpl(String courseInstanceID, String courseLayoutID, int courseStudents) {
        this.courseInstanceID = courseInstanceID;
        this.courseLayoutID = courseLayoutID;
        this.courseStudents = courseStudents;
    }

    public int getCourseStudents() { return courseStudents; }

    public String getCourseLayoutID() { return courseLayoutID; }

    public String getCourseInstanceID() { return courseInstanceID; }
}

