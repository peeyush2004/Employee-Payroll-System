package com.payroll.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static String jdbcURL = "jdbc:mysql://localhost:3306/payroll_db";
    private static String jdbcUsername = "root";
    private static String jdbcPassword = "root0501";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    jdbcURL, jdbcUsername, jdbcPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}