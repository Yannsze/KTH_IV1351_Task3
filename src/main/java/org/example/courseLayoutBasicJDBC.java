package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class courseLayoutBasicJDBC {
    private void accessDB() {
        String url = "jdbc:postgresql://localhost:5432/courselayout";
        String user = "postgres";
        String password = "postgres";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("");
            System.out.println("Connected!");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new courseLayoutBasicJDBC().accessDB();
    }
}
