package org.real.view;

import org.real.controller.Controller;
import java.util.Scanner;

public class CmdLine {
    private Controller controller;
    private final Scanner scanner = new Scanner(System.in);

    public void start(Controller controller) {
        this.controller = controller;
        System.out.println("Welcome to Course Allocation Manageer (Task 3A)");

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
                    handleAddExercise();
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
        System.out.println("5. Add 'Exercise' Activity");
        System.out.println("6. Exit");
        System.out.print("Select an option: ");
    }

    private void handleComputeCost() {
        System.out.print("Enter Course Instance ID (e.g. AL7106ht25): ");
        String id = scanner.nextLine();
        System.out.println(controller.computeCost(id));
    }

    private void handleModifyStudents() {
        System.out.print("Enter Course Instance ID: ");
        String id = scanner.nextLine();
        System.out.println(controller.modifyStudentCount(id, 100));
    }

    private void handleAllocateTeacher() {
        System.out.print("Enter Teacher ID (int): ");
        int tId = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Activity ID (1=Lecture, 2=Lab, 3=Tutorial, 4=Seminar): ");
        int aId = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Course Instance ID: ");
        String iId = scanner.nextLine();
        System.out.print("Enter Hours: ");
        int hours = Integer.parseInt(scanner.nextLine());

        System.out.println(controller.allocateTeacher(tId, aId, iId, hours));
    }

    private void handleDeallocateTeacher() {
        System.out.print("Enter Teacher ID: ");
        int tId = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Activity ID: ");
        int aId = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Course Instance ID: ");
        String iId = scanner.nextLine();

        System.out.println(controller.deallocateTeacher(tId, aId, iId));
    }

    private void handleAddExercise() {
        System.out.print("Enter Course Instance ID to add Exercise to: ");
        String iId = scanner.nextLine();
        System.out.print("Enter Teacher ID to allocate: ");
        int tId = Integer.parseInt(scanner.nextLine());

        System.out.println(controller.addExerciseActivity(iId, tId));
    }
}
