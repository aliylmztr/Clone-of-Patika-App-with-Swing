package com.patikadev.view;

import com.patikadev.helper.Helper;
import com.patikadev.helper.Item;
import com.patikadev.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StudentGUI extends JFrame {
    private final Student student;
    private JPanel wrapper;
    private JPanel pnl_top;
    private JPanel pnl_bottom;
    private JButton btn_logout;
    private JLabel lbl_welcome;
    private JTabbedPane tabbedPane1;
    private JTable table_student_all_pathways;
    private DefaultTableModel model_all_path_list;
    private Object[] row_all_path_list;
    private JButton button_student_path_register;
    private JButton button_student_path_unregister;
    private JComboBox combo_student_my_pathways;
    private JTable table_student_contents;
    private DefaultTableModel model_content_list;
    private Object[] row_content_list;
    private JTable table_student_courses;
    private DefaultTableModel model_course_list;
    private Object[] row_course_list;

    public StudentGUI(Student student) {
        this.student = student;

        int guiWidth = 1000, guiHeight = 600;

        setContentPane(wrapper);
        setTitle(Helper.PROJECT_TITLE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(guiWidth, guiHeight);
        setLocation((Helper.SCREEN_WIDTH - guiWidth) / 2, (Helper.SCREEN_HEIGHT - guiHeight) / 2);
        setVisible(true);

        lbl_welcome.setText("Welcome, " + student.getUsername() + " (Student)");
        btn_logout.addActionListener(e -> {
            LoginGUI loginGUI = new LoginGUI();
            dispose();
        });

        // MODEL ALL PATHWAYS
        model_all_path_list = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        Object[] col_all_path_list = {"ID", "Path Name"};
        model_all_path_list.setColumnIdentifiers(col_all_path_list);
        row_all_path_list = new Object[col_all_path_list.length];

        refreshAllPathsModel();

        table_student_all_pathways.setModel(model_all_path_list);
        table_student_all_pathways.getTableHeader().setReorderingAllowed(false);
        table_student_all_pathways.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table_student_all_pathways.getColumnModel().getColumn(0).setMaxWidth(80);
        table_student_all_pathways.getColumnModel().getColumn(0).setMinWidth(25);

        table_student_all_pathways.getSelectionModel().addListSelectionListener(e -> {
            if (table_student_all_pathways.getSelectedRow() != -1) {

                int path_id = Integer.parseInt(table_student_all_pathways.getValueAt(table_student_all_pathways.getSelectedRow(), 0).toString());
                if (StudentPath.isThereStudentPath(this.student.getId(), path_id)) {
                    button_student_path_register.setEnabled(false);
                    button_student_path_unregister.setEnabled(true);
                } else {
                    button_student_path_register.setEnabled(true);
                    button_student_path_unregister.setEnabled(false);
                }
            } else {
                button_student_path_register.setEnabled(false);
                button_student_path_unregister.setEnabled(false);
            }
        });

        button_student_path_register.addActionListener(e -> {
            button_student_path_register.setEnabled(false);
            if (StudentPath.add(this.student.getId(), Integer.parseInt(table_student_all_pathways.getValueAt(table_student_all_pathways.getSelectedRow(), 0).toString()))) {
                Helper.showMessage("Registration successful!", "Registration Message");
                loadStudentComboPaths();
                button_student_path_unregister.setEnabled(true);
            }
        });
        // ### MODEL ALL PATHWAYS ###

        // MODEL STUDENT MY PATHWAYS
        loadStudentComboPaths();

        combo_student_my_pathways.addActionListener(e -> {
            if (combo_student_my_pathways.getItemCount() > 0) {
                refreshStudentCoursesModelBySelectedPath(((Item) combo_student_my_pathways.getSelectedItem()).getKey());
                refreshStudentContentsModelBySelectedCourse(-1);
            }
        });

        // Courses table
        model_course_list = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        Object[] col_course_list = {"ID", "Course Name"};
        model_course_list.setColumnIdentifiers(col_course_list);
        row_course_list = new Object[col_course_list.length];

        if (combo_student_my_pathways.getItemCount() > 0) {
            refreshStudentCoursesModelBySelectedPath(((Item) combo_student_my_pathways.getSelectedItem()).getKey());
        }

        table_student_courses.setModel(model_course_list);
        table_student_courses.getTableHeader().setReorderingAllowed(false);
        table_student_courses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table_student_courses.getColumnModel().getColumn(0).setMaxWidth(80);
        table_student_courses.getColumnModel().getColumn(0).setMinWidth(25);

        table_student_courses.getSelectionModel().addListSelectionListener(e -> {
            if (table_student_courses.getSelectedRow() != -1) {
                int selectedRow = table_student_courses.getSelectedRow();
                int courseId = Integer.parseInt(table_student_courses.getValueAt(selectedRow, 0).toString());
                refreshStudentContentsModelBySelectedCourse(courseId);
            }
        });

        // Contents Table
        model_content_list = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        Object[] col_content_list = {"Title", "Description", "Youtube Link"};
        model_content_list.setColumnIdentifiers(col_content_list);
        row_content_list = new Object[col_content_list.length];

        table_student_contents.setModel(model_content_list);
        table_student_contents.getTableHeader().setReorderingAllowed(false);
        table_student_contents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // ### MODEL STUDENT MY PATHWAYS ###
    }

    private void refreshAllPathsModel() {
        DefaultTableModel clearModel = (DefaultTableModel) table_student_all_pathways.getModel();
        clearModel.setRowCount(0);
        int i;
        for (Patika patika : Patika.getList()) {
            i = 0;
            row_all_path_list[i++] = patika.getId();
            row_all_path_list[i] = patika.getName();

            model_all_path_list.addRow(row_all_path_list);
        }
    }

    private void loadStudentComboPaths() {
        combo_student_my_pathways.removeAllItems();
        for (StudentPath sp : StudentPath.getSPListByStudentID(this.student.getId())) {
            combo_student_my_pathways.addItem(new Item(sp.getPathID(), Patika.getPathByID(sp.getPathID()).getName()));
        }
    }

    private void refreshStudentCoursesModelBySelectedPath(int selectedPathId) {
        DefaultTableModel clearModel = (DefaultTableModel) table_student_courses.getModel();
        clearModel.setRowCount(0);
        int i;
        for (Course c : Course.getListByPathID(selectedPathId)) {
            i = 0;
            row_course_list[i++] = c.getCourseId();
            row_course_list[i] = c.getCourseName();

            model_course_list.addRow(row_course_list);
        }
    }

    private void refreshStudentContentsModelBySelectedCourse(int selectedCourseId) {
        DefaultTableModel clearModel = (DefaultTableModel) table_student_contents.getModel();
        clearModel.setRowCount(0);
        int i;
        for (Content content : Content.getContentListByCourseID(selectedCourseId)) {
            i = 0;
            row_content_list[i++] = content.getTitle();
            row_content_list[i++] = content.getDescription();
            row_content_list[i] = content.getLink();

            model_content_list.addRow(row_content_list);
        }
    }
}
