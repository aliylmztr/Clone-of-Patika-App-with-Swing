package com.patikadev.model;

import com.patikadev.helper.DBConnector;
import com.patikadev.helper.Helper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class User {
    private int id;
    private String fullname;
    private String username;
    private String password;
    private String type;

    public User() {
    }

    public User(int id, String fullname, String username, String password, String type) {
        this.id = id;
        this.fullname = fullname;
        this.username = username;
        this.password = password;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static ArrayList<User> getList() {
        ArrayList<User> users = new ArrayList<>();

        String query = "SELECT * FROM " + Helper.DB_TABLE_NAME_USER;
        User user;

        try {
            Statement statement = DBConnector.getInstance().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setFullname(resultSet.getString("fullname"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setType(resultSet.getString("type"));

                users.add(user);
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public static ArrayList<User> getEducatorList() {
        ArrayList<User> users = new ArrayList<>();

        String query = "SELECT * FROM " + Helper.DB_TABLE_NAME_USER + " WHERE type = 'educator'";
        User user;

        try {
            Statement statement = DBConnector.getInstance().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setFullname(resultSet.getString("fullname"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setType(resultSet.getString("type"));

                users.add(user);
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public static boolean add(String fullname, String username, String password, String type) {

        if (fetchByUsername(username) != null) {
            Helper.showMessage("Username : \"" + username + "\" is already exist.", "Username Error!");
            return false;
        }

        String query = "INSERT INTO " + Helper.DB_TABLE_NAME_USER + " (fullname, username, password, type) VALUES (?,?,?,?)";
        boolean result = false;

        try {
            PreparedStatement preparedStatement = DBConnector.getInstance().prepareStatement(query);
            preparedStatement.setString(1, fullname);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, type);
            result = (preparedStatement.executeUpdate() != -1);
            preparedStatement.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        if (!result) Helper.showMessage("Operation failed! There was an error adding a user.", "Error");

        return result;
    }

    public static boolean delete(int id) {
        String query = "DELETE FROM user WHERE id = ?";

        if (id == 1) {
            Helper.showMessage("You cannot delete a user with ID number 1!", "Error Message");
            return false;
        }
        boolean result = false;

        if (getUserByID(id).getType().equalsIgnoreCase("educator")) {
            ArrayList<Course> educatorCoursesList = Course.getListByEducatorID(id);

            if (educatorCoursesList.size() > 0) {
                StringBuilder message = new StringBuilder("Attention!\nRelated courses will be deleted along with the educator.");
                for (Course c : educatorCoursesList) {
                    message.append("\n").append(c.getCourseName());
                }
                message.append("\nDo you approve?");

                if (Helper.confirm(message.toString(), "Confirm Message!")) {
                    for (Course c : educatorCoursesList) {
                        Course.delete(c.getCourseId());
                    }
                } else {
                    Helper.showMessage("The transaction was canceled.", "Cancel Message");
                    return false;
                }
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

        if (!result) Helper.showMessage("User not found.", "Delete Operation Error!");

        return result;
    }

    public static boolean update(int id, String fullname, String username, String password, String type) {
        if (id == 1) {
            Helper.showMessage("You cannot update a user with ID number 1!", "Error Message");
            return false;
        }

        if (fullname.isBlank() || username.isBlank() || password.isBlank()) {
            Helper.showMessage("Fields can not be empty!", "Error!");
            return false;
        }
        boolean result = false;

        if (type.equalsIgnoreCase("OPERATOR")) type = "operator";
        else if (type.equalsIgnoreCase("EDUCATOR")) type = "educator";
        else if (type.equalsIgnoreCase("STUDENT")) type = "student";
        else {
            Helper.showMessage("Type must be \"operator\", \"educator\" or \"student\" !", "Type Error!");
        }

        User selectedUser = getUserByID(id);

        User checkOther = fetchByUsername(username);
        if (checkOther != null) {
            if (checkOther.getId() != selectedUser.getId()) {
                Helper.showMessage("This username already exist!", "Updating Error!");
                return false;
            }
        }

        String query = "UPDATE " + Helper.DB_TABLE_NAME_USER + " SET fullname = ?, username = ?, password = ?, type = ? WHERE id = ?";
        try {
            PreparedStatement preparedStatement = DBConnector.getInstance().prepareStatement(query);
            preparedStatement.setString(1, fullname);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, type);
            preparedStatement.setInt(5, id);

            result = (preparedStatement.executeUpdate() > 0);
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public static ArrayList<User> searchUserList(String query) {
        ArrayList<User> users = new ArrayList<>();

        User user;

        try {
            Statement statement = DBConnector.getInstance().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setFullname(resultSet.getString("fullname"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setType(resultSet.getString("type"));

                users.add(user);
            }

            statement.close();
            resultSet.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users;
    }

    public static User fetchByUsername(String username) {
        User user = null;
        String query = "SELECT * FROM " + Helper.DB_TABLE_NAME_USER + " WHERE username = ?";

        try {
            PreparedStatement preparedStatement = DBConnector.getInstance().prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setFullname(resultSet.getString("fullname"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setType(resultSet.getString("type"));
            }
            preparedStatement.close();
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public static User getUserByID(int id) {
        User user = null;
        String query = "SELECT * FROM " + Helper.DB_TABLE_NAME_USER + " WHERE id = ?";
        try {
            PreparedStatement preparedStatement = DBConnector.getInstance().prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setFullname(resultSet.getString("fullname"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setType(resultSet.getString("type"));
            }
            preparedStatement.close();
            resultSet.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return user;
    }

    public static String createSearchQuery(String fullname, String username, String type) {
        String query = "SELECT * FROM " + Helper.DB_TABLE_NAME_USER + " WHERE fullname LIKE '%{{fullname}}%' AND  username LIKE '%{{username}}%'";
        query = query.replace("{{fullname}}", fullname);
        query = query.replace("{{username}}", username);

        if (!type.trim().isBlank()) {
            query += " AND type LIKE '{{type}}'";
            query = query.replace("{{type}}", type);
        }
        return query;
    }
}
