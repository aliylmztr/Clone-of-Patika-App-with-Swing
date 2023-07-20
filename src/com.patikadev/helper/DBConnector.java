package com.patikadev.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
    private Connection connection = null;

    private Connection connectDB() {
        try {
            this.connection = DriverManager.getConnection(Helper.DB_URL, Helper.DB_USERNAME, Helper.DB_PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    public static Connection getInstance() {
        DBConnector dbConnector = new DBConnector();
        return dbConnector.connectDB();
    }
}
