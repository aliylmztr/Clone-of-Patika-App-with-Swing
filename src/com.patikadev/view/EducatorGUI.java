package com.patikadev.view;

import com.patikadev.helper.Helper;
import com.patikadev.model.Content;
import com.patikadev.model.Course;
import com.patikadev.model.Educator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class EducatorGUI extends JFrame {
    private final Educator educator;
    private JPanel wrapper;
    private JPanel pnl_top;
    private JPanel pnl_bottom;
    private JLabel lbl_welcome;
    private JButton btn_logout;
    private JTable table_courses;
    private DefaultTableModel model_course_list;
    private Object[] row_course_list;
    private JTable table_contents;
    private DefaultTableModel model_content_list;
    private Object[] row_content_list;
    private JButton btn_content_add;
    private JButton btn_content_delete;
    private JButton btn_content_update;
    private JTextField field_content_title;
    private JTextField field_content_description;
    private JTextField field_content_link;
    private JTextField field_course_id;
    private JTextField field_content_id;
    private JPanel pnl_operations;

    public EducatorGUI(Educator educator) {
        this.educator = educator;

        int guiWidth = 1000, guiHeight = 600;

        setContentPane(wrapper);
        setTitle(Helper.PROJECT_TITLE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(guiWidth, guiHeight);
        setLocation((Helper.SCREEN_WIDTH - guiWidth) / 2, (Helper.SCREEN_HEIGHT - guiHeight) / 2);
        setVisible(true);

        lbl_welcome.setText("Welcome, " + educator.getUsername() + " (Educator)");
        btn_logout.addActionListener(e -> {
            LoginGUI loginGUI = new LoginGUI();
            dispose();
        });

        // MODEL COURSE LIST
        model_course_list = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        Object[] col_course_list = {"ID", "Course Name"};
        model_course_list.setColumnIdentifiers(col_course_list);
        row_course_list = new Object[col_course_list.length];

        refreshCourseModel();

        table_courses.setModel(model_course_list);
        table_courses.getTableHeader().setReorderingAllowed(false);
        table_courses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table_courses.getColumnModel().getColumn(0).setMaxWidth(80);
        table_courses.getColumnModel().getColumn(0).setMinWidth(25);

        // ### MODEL EDUCATOR COURSE LIST ###
        // EDUCATOR COURSE SELECT-ADD-DELETE ACTION LISTENERS
        table_courses.getSelectionModel().addListSelectionListener(e -> {
            if (table_courses.getSelectedRow() != -1) {
                int selectedRow = table_courses.getSelectedRow();
                int courseId = Integer.parseInt(table_courses.getValueAt(selectedRow, 0).toString());
                refreshContentModelBySelectedCourseID(courseId);
                refreshTextOfFields();
            }
        });
        // ### EDUCATOR COURSE SELECT-ADD-DELETE ACTION LISTENERS ###

        // MODEL EDUCATOR CONTENT LIST
        model_content_list = new DefaultTableModel();
        Object[] col_content_list = {"ID", "Content Name"};
        model_content_list.setColumnIdentifiers(col_content_list);
        row_content_list = new Object[col_content_list.length];

        table_contents.setModel(model_content_list);
        table_contents.getTableHeader().setReorderingAllowed(false);

        table_contents.getColumnModel().getColumn(0).setMaxWidth(80);
        table_contents.getColumnModel().getColumn(0).setMinWidth(25);
        // ### MODEL EDUCATOR CONTENT LIST ###

        // EDUCATOR CONTENT ADD-DELETE ACTION LISTENERS
        table_contents.getSelectionModel().addListSelectionListener(e -> {
            if (table_contents.getSelectedRow() != -1) {
                refreshTextOfFields();
            }
        });


        btn_content_delete.addActionListener(e -> {
            if (!Helper.isFieldEmpty(field_content_id)) {
                if (Helper.confirm("Content will be deleted!\n\nDo you agree?", "Attention!")) {
                    if (Content.delete(Integer.parseInt(field_content_id.getText()))) {
                        Helper.showMessage("The content has been deleted.", "Deleting Message");
                        refreshContentModelBySelectedCourseID(Integer.parseInt(field_course_id.getText()));
                        refreshTextOfFields();
                    }
                }
            }
        });

        btn_content_update.addActionListener(e -> {
            if (Helper.isFieldEmpty(field_content_id, field_content_title, field_content_description, field_content_link)) {
                Helper.showMessage("Please fill in the empty fields", "Content Updating Error!");
            } else {
                if (Content.update(Integer.parseInt(field_content_id.getText()), field_content_title.getText(), field_content_description.getText(), field_content_link.getText())) {
                    Helper.showMessage("Content has been updated.", "Updating Message");
                    refreshContentModelBySelectedCourseID(Integer.parseInt(field_course_id.getText()));
                    refreshTextOfFields();
                }
            }
        });

        btn_content_add.addActionListener(e -> {
            if (Helper.isFieldEmpty(field_content_title, field_content_description, field_content_link, field_course_id)) {
                Helper.showMessage("Please fill in the empty fields\n(selected course id, title, description, and link)", "Content Adding Error!");
            } else {
                String title = field_content_title.getText();
                String description = field_content_description.getText();
                String link = field_content_link.getText();
                int courseId = Integer.parseInt(field_course_id.getText());

                Content content = Content.getContentByTitle(title);
                if (content == null) {
                    if (Content.add(title, description, link, courseId)) {
                        Helper.showMessage("A new content is added.", "Content Adding Message");
                        refreshContentModelBySelectedCourseID(courseId);
                        refreshTextOfFields();
                    }
                } else Helper.showMessage("Content with the same title already exists!", "Repetitive Title Error!");
            }
        });
        // ### EDUCATOR COURSE SELECT-ADD-DELETE ACTION LISTENERS ###
    }

    private void refreshCourseModel() {
        DefaultTableModel clearModel = (DefaultTableModel) table_courses.getModel();
        clearModel.setRowCount(0);
        for (Course course : Course.getListByEducatorID(educator.getId())) {
            int i = 0;
            row_course_list[i++] = course.getCourseId();
            row_course_list[i] = course.getCourseName();

            model_course_list.addRow(row_course_list);
        }
    }

    private void refreshContentModelBySelectedCourseID(int selectedCourseID) {
        DefaultTableModel clearModel = (DefaultTableModel) table_contents.getModel();
        clearModel.setRowCount(0);
        for (Content content : Content.getContentListByCourseID(selectedCourseID)) {
            int i = 0;
            row_content_list[i++] = content.getId();
            row_content_list[i] = content.getTitle();

            model_content_list.addRow(row_content_list);
        }
    }

    private void refreshTextOfFields() {
        if (table_courses.getSelectedRow() != -1) {
            int selectedRow = table_courses.getSelectedRow();
            field_course_id.setText(table_courses.getValueAt(selectedRow, 0).toString());
        } else field_course_id.setText(null);

        if (table_contents.getSelectedRow() != -1) {
            int selectedRow = table_contents.getSelectedRow();
            field_content_id.setText(table_contents.getValueAt(selectedRow, 0).toString());

            Content content = Content.getContentByID(Integer.parseInt(table_contents.getValueAt(selectedRow, 0).toString()));
            field_content_id.setText(String.valueOf(content.getId()));
            field_content_title.setText(content.getTitle());
            field_content_description.setText(content.getDescription());
            field_content_link.setText(content.getLink());
        } else {
            field_content_id.setText(null);
            field_content_title.setText(null);
            field_content_description.setText(null);
            field_content_link.setText(null);
        }
    }
}
