package org.example.model;

import org.example.DTO.*;
import org.example.integration.courseLayoutDAO;
import org.example.integration.courseLayoutDBException;

public class CourseLayoutService {
    private final courseLayoutDAO courseLayoutDAO;

    public CourseLayoutService(courseLayoutDAO courseLayoutDAO) {
        this.courseLayoutDAO = courseLayoutDAO;
    }

    public CostDTO calculateCostForCourse(PlannedActivityDTO plannedActivity, TeachingActivityDTO teachingActivity)
            throws courseLayoutDBException {

        return courseLayoutDAO.plannedActualCoststmt(plannedActivity, teachingActivity);
    }

    public void updateStudentCount(CourseInstanceDTO instance) throws courseLayoutDBException {
        courseLayoutDAO.updateStudentstmt(instance);
    }

    public void allocateTeacher(AllocationDTO allocation) throws courseLayoutDBException {
        courseLayoutDAO.allocateTeacherActivitystmt(allocation);
    }

    public void deallocateTeacher(AllocationDTO allocation) throws courseLayoutDBException {
        courseLayoutDAO.deallocatedTeacherActivitystmt(allocation);
    }

    public void createNewActivity(TeachingActivityDTO activity) throws courseLayoutDBException {
        courseLayoutDAO.insertNewTeachingActivitystmt(activity);
    }
}