package org.example.view;

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
        String instanceId = readString("Enter Course Instance ID: ");
        String studyYear = readString("Enter Study Year: ");

        Cost cost = controller.getCourseCost(instanceId, studyYear);
        System.out.println("Planned Cost: " + cost.getPlannedCost() + " | Actual Cost: " + cost.getActualCost());
    }

    private void handleModifyStudents() throws courseLayoutDBException {
        String instanceId = readString("Enter Course Instance ID: ");
        String layoutId = readString("Enter Course Layout ID: ");
        int increment = 100;

        controller.updateStudentCount(instanceId, layoutId, increment);
        System.out.println("Student count increased by " + increment);
    }

    private void handleAllocateTeacher() throws courseLayoutDBException {
        String empId = readString("Enter Teacher ID: ");
        String courseInstanceId = readString("Enter Course Instance ID: ");
        String activityId = readString("Enter Activity ID: ");

        controller.allocateTeacher(empId, courseInstanceId, activityId);
        System.out.println("Teacher allocated successfully.");
    }

    private void handleDeallocateTeacher() throws courseLayoutDBException {
        String empId = readString("Enter Teacher ID: ");
        String courseInstanceId = readString("Enter Course Instance ID: ");
        String activityId = readString("Enter Activity ID: ");

        controller.deallocateTeacher(empId, courseInstanceId, activityId);
        System.out.println("Teacher deallocated successfully.");
    }

    private void handleAddActivity() throws courseLayoutDBException {
        String name = "Exercise"; // fixed name for this assignment
        controller.createTeachingActivity(name);
        System.out.println("New 'Exercise' activity added successfully.");
    }

    // Helper methods to read input
    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}