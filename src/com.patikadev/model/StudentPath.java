package com.patikadev.model;

import com.patikadev.helper.DBConnector;
import com.patikadev.helper.Helper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class StudentPath {
    private int studentID;
    private int pathID;

    public StudentPath() {
    }

    public StudentPath(int studentID, int pathID) {
        this.studentID = studentID;
        this.pathID = pathID;
    }

    public int getPathID() {
        return pathID;
    }

    public static ArrayList<StudentPath> getSPListByStudentID(int student_id) {
        ArrayList<StudentPath> s_p_list = new ArrayList<>();

        String query = "SELECT * FROM " + Helper.DB_TABLE_NAME_STUDENT_PATH + " WHERE student_id = " + student_id;

        StudentPath studentPath = null;

        try {
            Statement statement = DBConnector.getInstance().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int pt_id = resultSet.getInt("path_id");

                studentPath = new StudentPath(student_id, pt_id);
                s_p_list.add(studentPath);
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return s_p_list;
    }

    public static boolean add(int student_id, int path_id) {
        String query = "INSERT INTO " + Helper.DB_TABLE_NAME_STUDENT_PATH + " (student_id, path_id) VALUES (?,?)";
        boolean result = false;

        try {
            PreparedStatement preparedStatement = DBConnector.getInstance().prepareStatement(query);
            preparedStatement.setInt(1, student_id);
            preparedStatement.setInt(2, path_id);

            result = (preparedStatement.executeUpdate() != -1);
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        if (!result) Helper.showMessage("Failed to register!", "Error");
        return result;
    }

    public static boolean isThereStudentPath(int student_id, int path_id) {
        String query = "SELECT * FROM " + Helper.DB_TABLE_NAME_STUDENT_PATH + " WHERE student_id = ? AND path_id = ?";
        boolean result = false;

        try {
            PreparedStatement preparedStatement = DBConnector.getInstance().prepareStatement(query);
            preparedStatement.setInt(1, student_id);
            preparedStatement.setInt(2, path_id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                result = true;
            }
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
}
