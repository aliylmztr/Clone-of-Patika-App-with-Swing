package com.patikadev.model;

import com.patikadev.helper.DBConnector;
import com.patikadev.helper.Helper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Content {
    private int id;
    private String title;
    private String description;
    private String link;
    private int contentCourseID;
    private Course contentCourse;

    public Content(String title, String description, String link, int contentCourseID) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.contentCourseID = contentCourseID;

        contentCourse = Course.getCourseByID(contentCourseID);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getContentCourseID() {
        return contentCourseID;
    }

    public void setContentCourseID(int contentCourseID) {
        this.contentCourseID = contentCourseID;
    }

    public Course getContentCourse() {
        return contentCourse;
    }

    public void setContentCourse(Course contentCourse) {
        this.contentCourse = contentCourse;
    }

    public static Content getContentByID(int id) {
        Content content = null;
        String query = "SELECT * FROM " + Helper.DB_TABLE_NAME_CONTENT + " WHERE id = ?";
        try {
            PreparedStatement preparedStatement = DBConnector.getInstance().prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                content = new Content(
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getString("link"),
                        resultSet.getInt("course_id")
                );
                content.setId(resultSet.getInt("id"));
            }
            preparedStatement.close();
            resultSet.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return content;
    }

    public static Content getContentByTitle(String title) {
        Content content = null;
        String query = "SELECT * FROM " + Helper.DB_TABLE_NAME_CONTENT + " WHERE title = ?";
        try {
            PreparedStatement preparedStatement = DBConnector.getInstance().prepareStatement(query);
            preparedStatement.setString(1, title);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                content = new Content(
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getString("link"),
                        resultSet.getInt("course_id")
                );
                content.setId(resultSet.getInt("id"));
            }
            preparedStatement.close();
            resultSet.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return content;
    }

    public static ArrayList<Content> getContentListByCourseID(int contentCourseID) {
        ArrayList<Content> contents = new ArrayList<>();
        String query = "SELECT * FROM " + Helper.DB_TABLE_NAME_CONTENT + " WHERE course_id = " + contentCourseID;

        Content content = null;
        try {
            Statement statement = DBConnector.getInstance().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String link = resultSet.getString("link");
                int courseID = resultSet.getInt("course_id");

                content = new Content(title, description, link, courseID);
                content.setId(id);
                contents.add(content);
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return contents;
    }

    public static boolean add(String title, String description, String link, int courseID) {

        String query = "INSERT INTO " + Helper.DB_TABLE_NAME_CONTENT + " (title, description, link, course_id) VALUES(?,?,?,?)";
        boolean result = false;
        try {
            PreparedStatement preparedStatement = DBConnector.getInstance().prepareStatement(query);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, description);
            preparedStatement.setString(3, link);
            preparedStatement.setInt(4, courseID);
            result = (preparedStatement.executeUpdate() != -1);
            preparedStatement.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if (!result) Helper.showMessage("Operation failed! There was an error adding a new content.", "Error");
        return result;
    }

    public static boolean delete(int id) {
        String query = "DELETE FROM " + Helper.DB_TABLE_NAME_CONTENT + " WHERE id = ?";
        boolean result = false;
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

    public static boolean update(int id, String title, String description, String link) {
        String query = "UPDATE " + Helper.DB_TABLE_NAME_CONTENT + " SET title = ?, description = ?, link = ? WHERE id = ?";
        boolean result = false;
        try {
            PreparedStatement preparedStatement = DBConnector.getInstance().prepareStatement(query);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, description);
            preparedStatement.setString(3, link);
            preparedStatement.setInt(4, id);

            result = (preparedStatement.executeUpdate() > 0);
            preparedStatement.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if (!result)
            Helper.showMessage("Operation failed! There was an error updating the content.", "Updating Error");
        return result;
    }
}
