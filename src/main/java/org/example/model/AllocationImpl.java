package org.example.model;

import org.example.DTO.AllocationDTO;

public class AllocationImpl implements AllocationDTO {
    private final String employeeID;
    private final String courseInstanceID;
    private final String activityID;

    public AllocationImpl(String employeeID, String courseInstanceID, String activityID) {
        this.employeeID = employeeID;
        this.courseInstanceID = courseInstanceID;
        this.activityID = activityID;
    }

    public String getEmployeeID() { return employeeID; }

    public String getCourseInstanceID() { return courseInstanceID; }

    public String getActivityID() { return activityID; }
}

