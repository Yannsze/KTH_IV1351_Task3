package org.example.view;

import org.example.DTO.CostDTO;
import org.example.controller.Controller;
import org.example.integration.courseLayoutDBException;
import org.example.model.Cost;
import java.util.Scanner;

public class CmdLine {
    private Controller controller;
    private final Scanner scanner = new Scanner(System.in);

    public void start(Controller controller) throws courseLayoutDBException {
        this.controller = controller;
        System.out.println("Welcome to Course Allocation Manager");

        while (true) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleComputeCost();
                    break;
                case "2":
                    handleModifyStudents();
                    break;
                case "3":
                    handleAllocateTeacher();
                    break;
                case "4":
                    handleDeallocateTeacher();
                    break;
                case "5":
                    handleAddActivity();
                    break;
                case "6":
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    private void printMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Compute Teaching Cost");
        System.out.println("2. Modify Student Count (+100)");
        System.out.println("3. Allocate Teacher");
        System.out.println("4. Deallocate Teacher");
        System.out.println("5. Add Activity (for e.g. Exercise)");
        System.out.println("6. Exit");
        System.out.print("Select an option: ");
    }

    private void handleComputeCost() throws courseLayoutDBException {
        try {
            String instanceIdStr = readString("Enter Course Instance ID: ");
            int instanceId = Integer.parseInt(instanceIdStr);
            String studyYearStr = readString("Enter Study Year: ");
            int studyYear = Integer.parseInt(studyYearStr);

            CostDTO cost = controller.getCourseCost(instanceId, studyYear);
            System.out.println("Planned Cost: " + cost.getPlannedCost() + " | Actual Cost: " + cost.getActualCost());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter an integer value.");
        }
    }

    private void handleModifyStudents() throws courseLayoutDBException {
        try {
            String courseLayoutIDstr = readString("Enter Course Layout ID: ");
            int courseLayoutID = Integer.parseInt(courseLayoutIDstr);
            String numStudentsStr = readString("Enter increased number of student in total: ");
            int numStudents = Integer.parseInt(numStudentsStr);
            String courseInstanceIDStr = readString("Enter Course Instance ID: ");
            int courseInstanceID = Integer.parseInt(courseInstanceIDStr);

            controller.updateStudentCount(courseLayoutID, numStudents, courseInstanceID);
            System.out.println("Student is now " + numStudents);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter an integer value.");
        }
    }

    private void handleAllocateTeacher() throws courseLayoutDBException {
        try {
            String empIdStr = readString("Enter Teacher ID: ");
            int empId = Integer.parseInt(empIdStr);
            String courseInstanceIdStr = readString("Enter Course Instance ID: ");
            int courseInstanceId = Integer.parseInt(courseInstanceIdStr);
            String teachingActivityIdStr = readString("Enter Teaching Activity ID: ");
            int teachingActivityId = Integer.parseInt(teachingActivityIdStr);
            String allocatedHoursStr = readString("Enter Allocated Hours: ");
            double allocatedHours = Double.parseDouble(allocatedHoursStr);

            controller.allocateTeacher(empId, courseInstanceId, teachingActivityId, allocatedHours);
            System.out.println("Teacher allocated successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter an integer value.");
        }
    }

    private void handleDeallocateTeacher() throws courseLayoutDBException {
        try {
            String empIdStr = readString("Enter Teacher ID: ");
            int empId = Integer.parseInt(empIdStr);
            String courseInstanceIdStr = readString("Enter Course Instance ID: ");
            int courseInstanceId = Integer.parseInt(courseInstanceIdStr);
            String teachingActivityIdStr = readString("Enter Teaching Activity ID: ");
            int teachingActivityId = Integer.parseInt(teachingActivityIdStr);

            controller.deallocateTeacher(empId, courseInstanceId, teachingActivityId);
            System.out.println("Teacher deallocated successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter an integer value.");
        }
    }

    private void handleAddActivity() throws courseLayoutDBException {
        try {
            String name = "Exercise"; // fixed name for this assignment
            controller.createTeachingActivity(name, 1.0);
            System.out.println("New activity added successfully.");
        } catch (courseLayoutDBException e) {
            System.out.print("The Activity has already added!");
        }

    }

    // Helper methods to read input
    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}