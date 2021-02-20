/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Scanner;
import java.sql.*;
import java.util.regex.*;

/**
 *
 * @author kishan
 */
public class Database {

    public static String username;
    public static String password;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        username = args[0];
        password = args[1];
        System.out.println("Please type in your SQL query, after you have finish, please type \"exit\" on a new line.");

        /**
         * Watch for SQL issued errors
         */
        try {
            /**
             * Try/close on the open resources
             */
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3308/cw2?useSSL=false", username, password)) {
                Scanner inputScanner = new Scanner(System.in);
                StringBuilder sb = new StringBuilder();

                /**
                 * Grab users input irrespective of carriage returns
                 */
                while (inputScanner.hasNextLine()) {
                    String input = inputScanner.nextLine();
                    if ("exit".equals(input)) {
                        break;
                    } else {
                        sb.append(" ").append(input); // fix whitespace
                    }
                }
                /**
                 * Finalise the builder
                 */
                String finalQuery = sb.toString();
                Pattern insertPattern = Pattern.compile("(insert|delete|update|select)", Pattern.CASE_INSENSITIVE);
                Matcher insertMatcher = insertPattern.matcher(finalQuery);
                boolean matchFound = insertMatcher.find();
                Statement stmt = con.createStatement();
                String updateType = insertMatcher.group().toLowerCase();
                if (matchFound) {
                    switch (updateType) {
                        case "insert":
                        case "update":
                        case "delete":
                            int successfulUpdate = stmt.executeUpdate(finalQuery);
                            if (successfulUpdate == 1) {
                                System.out.println(updateType.toUpperCase() + " Successful");
                            }

                        case "select":
                            ResultSet rs = stmt.executeQuery(finalQuery);
                            ResultSetMetaData rsmd = rs.getMetaData();
                            int columnsNumber = rsmd.getColumnCount();
                            while (rs.next()) {
                                for (int i = 1; i <= columnsNumber; i++) {
                                    if (i > 1) {
                                        System.out.print(",  ");
                                    }
                                    String columnValue = rs.getString(i);
                                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
                                }
                                System.out.println("");
                            }

                        default:
                            main(new String[]{args[0], args[1]});

                    }
                }

            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

}