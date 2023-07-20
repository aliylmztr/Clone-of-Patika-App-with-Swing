package com.patikadev.model;

import com.patikadev.helper.DBConnector;
import com.patikadev.helper.Helper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Course {
    private int courseId;
    private int courseEducatorId;
    private User courseEducator;
    private int coursePathId;
    private Patika coursePatika;
    private String courseName;
    private String courseLanguage;

    public Course(int courseId, int courseEducatorId, int coursePathId, String courseName, String courseLanguage) {
        this.courseId = courseId;
        this.courseEducatorId = courseEducatorId;
        this.coursePathId = coursePathId;
        this.courseName = courseName;
        this.courseLanguage = courseLanguage;

        this.courseEducator = User.getUserByID(courseEducatorId);
        this.coursePatika = Patika.getPathByID(coursePathId);
    }

    public static ArrayList<Course> getList() {
        ArrayList<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM " + Helper.DB_TABLE_NAME_COURSE;

        Course course = null;
        try {
            Statement statement = DBConnector.getInstance().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int educatorId = resultSet.getInt("educator_id");
                int pathId = resultSet.getInt("path_id");
                String name = resultSet.getString("name");
                String lang = resultSet.getString("language");

                course = new Course(id, educatorId, pathId, name, lang);
                courses.add(course);
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return courses;
    }

    public static ArrayList<Course> getListByEducatorID(int educ_id) {
        ArrayList<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM " + Helper.DB_TABLE_NAME_COURSE + " WHERE educator_id = " + educ_id;

        Course course = null;
        try {
            Statement statement = DBConnector.getInstance().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int educatorId = resultSet.getInt("educator_id");
                int pathId = resultSet.getInt("path_id");
                String name = resultSet.getString("name");
                String lang = resultSet.getString("language");

                course = new Course(id, educatorId, pathId, name, lang);
                courses.add(course);
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return courses;
    }

    public static ArrayList<Course> getListByPathID(int path_id) {
        ArrayList<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM " + Helper.DB_TABLE_NAME_COURSE + " WHERE path_id = " + path_id;

        Course course = null;
        try {
            Statement statement = DBConnector.getInstance().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int educatorId = resultSet.getInt("educator_id");
                int pathId = resultSet.getInt("path_id");
                String name = resultSet.getString("name");
                String lang = resultSet.getString("language");

                course = new Course(id, educatorId, pathId, name, lang);
                courses.add(course);
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return courses;
    }

    public static Course getCourseByID(int id) {
        Course course = null;
        String query = "SELECT * FROM " + Helper.DB_TABLE_NAME_COURSE + " WHERE id = ?";
        try {
            PreparedStatement preparedStatement = DBConnector.getInstance().prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                course = new Course(
                        resultSet.getInt("id"),
                        resultSet.getInt("educator_id"),
                        resultSet.getInt("path_id"),
                        resultSet.getString("name"),
                        resultSet.getString("language")
                );
            }
            preparedStatement.close();
            resultSet.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return course;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getCourseEducatorId() {
        return courseEducatorId;
    }

    public void setCourseEducatorId(int courseEducatorId) {
        this.courseEducatorId = courseEducatorId;
    }

    public User getCourseEducator() {
        return courseEducator;
    }

    public void setCourseEducator(User courseEducator) {
        this.courseEducator = courseEducator;
    }

    public int getCoursePathId() {
        return coursePathId;
    }

    public void setCoursePathId(int coursePathId) {
        this.coursePathId = coursePathId;
    }

    public Patika getCoursePath() {
        return coursePatika;
    }

    public void setCoursePath(Patika coursePatika) {
        this.coursePatika = coursePatika;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseLanguage() {
        return courseLanguage;
    }

    public void setCourseLanguage(String courseLanguage) {
        this.courseLanguage = courseLanguage;
    }

    public static boolean add(String courseName, String courseLanguage, int courseEducatorId, int coursePathId) {

        String query = "INSERT INTO " + Helper.DB_TABLE_NAME_COURSE + " (name, language, educator_id, path_id) VALUES(?,?,?,?)";
        boolean result = false;
        try {
            PreparedStatement preparedStatement = DBConnector.getInstance().prepareStatement(query);
            preparedStatement.setString(1, courseName);
            preparedStatement.setString(2, courseLanguage);
            preparedStatement.setInt(3, courseEducatorId);
            preparedStatement.setInt(4, coursePathId);
            result = (preparedStatement.executeUpdate() != -1);
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if (!result) Helper.showMessage("Operation failed! There was an error adding a course.", "Error");
        return result;
    }

    public static boolean delete(int id) {
        String query = "DELETE FROM " + Helper.DB_TABLE_NAME_COURSE + " WHERE id = ?";
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
}
