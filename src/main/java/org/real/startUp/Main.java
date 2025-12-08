package org.real.startUp;

import org.real.controller.Controller;
import org.real.view.CmdLine;

public class Main {
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
