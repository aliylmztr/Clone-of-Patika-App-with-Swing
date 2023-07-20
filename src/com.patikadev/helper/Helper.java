package com.patikadev.helper;

import javax.swing.*;
import java.awt.*;

public class Helper {

    public static final String PROJECT_TITLE = "PatikaDev";
    public static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    public static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    public static final String DB_URL = "jdbc:mysql://localhost/patikadev_clone";
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "mysql";
    public static final String DB_TABLE_NAME_USER = "user";
    public static final String DB_TABLE_NAME_PATH = "pathway";
    public static final String DB_TABLE_NAME_COURSE = "course";
    public static final String DB_TABLE_NAME_CONTENT = "content";
    public static final String DB_TABLE_NAME_STUDENT_PATH = "student_path";
    public static final String USER_TYPE_NAME_OPERATOR = "operator";
    public static final String USER_TYPE_NAME_EDUCATOR = "educator";
    public static final String USER_TYPE_NAME_STUDENT = "student";

    public static void setLayout() {
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if (info.getName().equalsIgnoreCase("windows")) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException |
                         InstantiationException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }
    }

    public static boolean isFieldEmpty(JTextField... fields) {
        for (JTextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(String message, String title) {
        return JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION) == 0;
    }
}
