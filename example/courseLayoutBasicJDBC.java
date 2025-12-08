package org.example;

import javax.xml.transform.Result;
import java.sql.*;


/* TO BE DELETED */


// TASKS
// compute teaching cost
// modify the course instance (insert, update)
// allocate & deallocate teaching loads (update / delete?)
// add new teaching activity "exercise" (insert)

public class courseLayoutBasicJDBC {
    private static final String TABLE_NAME = "teaching_activity";

    private void accessDB() {
        String url = "jdbc:postgresql://localhost:5432/courselayout"; // modify localhost & password
        String user = "postgres";
        String password = "postgres";

        try {
            Class.forName("org.postgresql.Driver"); // loads driver
            Connection connection = DriverManager.getConnection(url, user, password); // connects to URL
            System.out.println("Connected!");

            Statement stmt = connection.createStatement(); // used to send query to database

            stmt.executeUpdate("");

            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Checks if table exists
    private boolean tableExists(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tableMetaData = metaData.getTables(null, null, null, null);
        while (tableMetaData.next()) {
            String tableName = tableMetaData.getString(3);
            if (tableName.equalsIgnoreCase(TABLE_NAME)) {
                return true;
            }
        }
        return false;
    }

    private void listAllRows(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet table = stmt.executeQuery("query");
        while(table.next()) {
            System.out.println();
        }
    }

    private void prepareStatements(Connection connection) throws SQLException {

    }

    public static void main(String[] args) {
        new courseLayoutBasicJDBC().accessDB();
    }
}
