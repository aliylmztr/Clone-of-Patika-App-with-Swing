package com.patikadev.model;

import com.patikadev.helper.DBConnector;
import com.patikadev.helper.Helper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Patika {
    private int id;
    private String name;

    public Patika() {
    }

    public Patika(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static ArrayList<Patika> getList() {
        ArrayList<Patika> patikas = new ArrayList<>();

        String query = "SELECT * FROM " + Helper.DB_TABLE_NAME_PATH;
        Patika patika;

        try {
            Statement statement = DBConnector.getInstance().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                patika = new Patika();
                patika.setId(resultSet.getInt("id"));
                patika.setName(resultSet.getString("name"));

                patikas.add(patika);
            }

            statement.close();
            resultSet.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return patikas;
    }

    public static boolean add(String pathName) {
        String query = "INSERT INTO " + Helper.DB_TABLE_NAME_PATH + " (name) VALUES (?)";
        boolean result = false;

        try {
            PreparedStatement preparedStatement = DBConnector.getInstance().prepareStatement(query);
            preparedStatement.setString(1, pathName);
            result = (preparedStatement.executeUpdate() != -1);
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        if (!result) Helper.showMessage("Operation failed! There was an error adding a path.", "Error");
        return result;
    }

    public static boolean update(int id, String name) {
        String query = "UPDATE " + Helper.DB_TABLE_NAME_PATH + " SET name = ? WHERE id = ?";
        boolean result = false;
        try {
            PreparedStatement preparedStatement = DBConnector.getInstance().prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, id);
            result = (preparedStatement.executeUpdate() > 0);
            preparedStatement.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if (!result)
            Helper.showMessage("Operation failed! There was an error updating the path name.", "Updating Error");
        return result;
    }

    public static boolean delete(int id) {
        String query = "DELETE FROM " + Helper.DB_TABLE_NAME_PATH + " WHERE id = ?";
        boolean result = false;

        ArrayList<Course> pathCourseList = Course.getListByPathID(id);
        if (pathCourseList.size() > 0) {
            StringBuilder message = new StringBuilder("Attention!\nRelated courses will be deleted along with the path.");
            for (Course c : pathCourseList) {
                message.append("\n").append(c.getCourseName());
            }
            message.append("\nDo you approve?");

            if (Helper.confirm(message.toString(), "Confirm Message!")) {
                for (Course c : pathCourseList) {
                    Course.delete(c.getCourseId());
                }
            } else {
                Helper.showMessage("The transaction was canceled.", "Cancel Message");
                return false;
            }
        }

        try {
            PreparedStatement preparedStatement = DBConnector.getInstance().prepareStatement(query);
            preparedStatement.setInt(1, id);
            result = (preparedStatement.executeUpdate() > 0);
            preparedStatement.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public static Patika getPathByID(int id) {
        Patika patika = null;
        String query = "SELECT * FROM " + Helper.DB_TABLE_NAME_PATH + " WHERE id = ?";
        try {
            PreparedStatement preparedStatement = DBConnector.getInstance().prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                patika = new Patika();
                patika.setId(resultSet.getInt("id"));
                patika.setName(resultSet.getString("name"));
            }
            preparedStatement.close();
            resultSet.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return patika;
    }
}
