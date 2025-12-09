package org.example.startUp;

import org.example.controller.Controller;
import org.example.view.CmdLine;

public class Main {
    /*
    public static void main(String[] args) {
        try {
            new BlockingInterpreter(new Controller()).handleCmds();
        } catch(courseLayoutDBException cldbe) {
            System.out.println("Could not connect to Bank db.");
            cldbe.printStackTrace();
        }
    }
     */
    public static void main(String[] args) {
        try {
            Controller controller = new Controller();
            CmdLine cmdLoop = new CmdLine();
            cmdLoop.start(controller);
        } catch (Exception e) {
            System.out.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
