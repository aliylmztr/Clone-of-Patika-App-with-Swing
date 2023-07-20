package com.patikadev.view;

import com.patikadev.helper.Helper;
import com.patikadev.helper.Item;
import com.patikadev.model.Course;
import com.patikadev.model.Operator;
import com.patikadev.model.Patika;
import com.patikadev.model.User;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class OperatorGUI extends JFrame {
    private JPanel wrapper;
    private JPanel pnl_top;
    private JLabel lbl_welcome;
    private JButton btn_logout;
    private JPanel pnl_bottom;
    private JTabbedPane tab_operator;
    private JPanel pnl_user_list;
    private JScrollPane scroll_user_list;
    private JTable table_user_list;
    private JPanel pnl_user_form;
    private JTextField field_user_fullname;
    private JTextField field_user_username;
    private JTextField field_user_password;
    private JComboBox combo_user_type;
    private JButton btn_user_add;
    private JTextField field_user_id;
    private JButton btn_user_delete;
    private JTextField field_search_name;
    private JButton btn_search;
    private JTextField field_search_username;
    private JComboBox combo_search_type;
    private JPanel pnl_path_list;
    private JTable table_path_list;
    private JScrollPane scroll_path_list;
    private JPanel pnl_path_add;
    private JTextField field_path_name;
    private JButton btn_path_add;
    private JPanel pnl_course_list;
    private JScrollPane scroll_course_list;
    private JTable table_course_list;
    private JPanel pnl_course_form;
    private JTextField field_course_name;
    private JTextField field_course_language;
    private JComboBox combo_course_path;
    private JComboBox combo_course_educator;
    private JButton btn_course_add;
    private JButton btn_course_delete;
    private JTextField field_course_id;
    private DefaultTableModel model_user_list;
    private Object[] row_user_list;
    private DefaultTableModel model_path_list;
    private Object[] row_path_list;
    private DefaultTableModel model_course_list;
    private Object[] row_course_list;
    private JPopupMenu pathPopupMenu;
    private final Operator operator;

    public OperatorGUI(Operator operator) {
        this.operator = operator;

        int guiWidth = 1000, guiHeight = 600;

        setContentPane(wrapper);
        setTitle(Helper.PROJECT_TITLE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(guiWidth, guiHeight);
        setLocation((Helper.SCREEN_WIDTH - guiWidth) / 2, (Helper.SCREEN_HEIGHT - guiHeight) / 2);
        setVisible(true);

        lbl_welcome.setText("Welcome, " + operator.getUsername() + " (Operator)");
        btn_logout.addActionListener(e -> {
            LoginGUI loginGUI = new LoginGUI();
            dispose();
        });

        // MODEL USER LIST
        model_user_list = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0) return false;
                return super.isCellEditable(row, column);
            }
        };
        Object[] col_user_list = {"ID", "Full Name", "User Name", "Password", "User Type"};
        model_user_list.setColumnIdentifiers(col_user_list);
        row_user_list = new Object[col_user_list.length];

        refreshUserModel();

        table_user_list.setModel(model_user_list);
        table_user_list.getTableHeader().setReorderingAllowed(false);

        table_user_list.getSelectionModel().addListSelectionListener(e -> {

            if (table_user_list.getSelectedRow() != -1) {
                String selectedRow = table_user_list.getValueAt(table_user_list.getSelectedRow(), 0).toString();
                field_user_id.setText(selectedRow);
            } else field_user_id.setText(null);
        });

        table_user_list.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int updatedRow = table_user_list.getSelectedRow();
                int userId = Integer.parseInt(table_user_list.getValueAt(updatedRow, 0).toString());
                String userFullname = table_user_list.getValueAt(updatedRow, 1).toString();
                String username = table_user_list.getValueAt(updatedRow, 2).toString();
                String userPassword = table_user_list.getValueAt(updatedRow, 3).toString();
                String userType = table_user_list.getValueAt(updatedRow, 4).toString();

                if (User.update(userId, userFullname, username, userPassword, userType)) {
                    Helper.showMessage("User information has been updated.", "UPDATE");
                } else {
                    Helper.showMessage("An error has occurred!", "Updating Error!");
                }
                refreshUserModel();
                refreshCourseModel();
            }
        });
        // ## MODEL USER LIST

        // USER ADD-DELETE-SEARCH ACTION LISTENERS
        btn_user_add.addActionListener(e -> {
            if (Helper.isFieldEmpty(field_user_fullname, field_user_username, field_user_password)) {
                Helper.showMessage("Please fill in all fields.", "ERROR!");
            } else {
                String fullname = field_user_fullname.getText();
                String username = field_user_username.getText();
                String password = field_user_password.getText();
                String userType = combo_user_type.getSelectedItem().toString();

                if (User.add(fullname, username, password, userType)) {
                    Helper.showMessage("New User \"" + fullname + "\" Added.", "Success");
                    refreshUserModel();
                    field_user_fullname.setText(null);
                    field_user_username.setText(null);
                    field_user_password.setText(null);

                    refreshCourseModel();
                }
            }
        });

        btn_user_delete.addActionListener(e -> {
            if (Integer.parseInt(field_user_id.getText()) == 1) {
                Helper.showMessage("You cannot delete a user with ID number 1!", "Error Message");
            } else {
                if (Helper.isFieldEmpty(field_user_id)) {
                    Helper.showMessage("Please enter a valid ID.", "ERROR!");
                } else {
                    if (Helper.confirm("User will be deleted! Are you sure?", "WARNING!")) {
                        if (User.delete(Integer.parseInt(field_user_id.getText()))) {
                            Helper.showMessage("User deleted.", "Operation Message");
                            refreshUserModel();
                            field_user_id.setText(null);

                            refreshCourseModel();
                        }
                    }
                }
            }
        });

        btn_search.addActionListener(e -> {
            String fullname = field_search_name.getText();
            String username = field_search_username.getText();
            String type = combo_search_type.getSelectedItem().toString();

            refreshUserModel(User.searchUserList(User.createSearchQuery(fullname, username, type)));
        });
        // ## USER ADD-DELETE-SEARCH ACTION LISTENERS

        // MODEL PATH LIST
        pathPopupMenu = new JPopupMenu();
        JMenuItem updateItem = new JMenuItem("Update");
        JMenuItem deleteItem = new JMenuItem("Delete");

        pathPopupMenu.add(updateItem);
        pathPopupMenu.add(deleteItem);
        table_path_list.setComponentPopupMenu(pathPopupMenu);

        updateItem.addActionListener(e -> {
            int selected_id = Integer.parseInt(table_path_list.getValueAt(table_path_list.getSelectedRow(), 0).toString());
            PathUpdateGUI pathUpdateGUI = new PathUpdateGUI(Patika.getPathByID(selected_id));

            pathUpdateGUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    refreshPathModel();
                    refreshCourseModel();
                }
            });
        });

        deleteItem.addActionListener(e -> {
            int selected_id = Integer.parseInt(table_path_list.getValueAt(table_path_list.getSelectedRow(), 0).toString());
            if (Helper.confirm("The chosen path will be deleted! Are you sure?", "WARNING!")) {
                if (Patika.delete(selected_id)) {
                    Helper.showMessage("Path has been deleted.", "Deleting Message");
                    refreshPathModel();
                    refreshCourseModel();
                }
            }
        });

        model_path_list = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0 || column == 1) return false;
                return super.isCellEditable(row, column);
            }
        };

        Object[] col_path_list = {"ID", "Path Name"};
        model_path_list.setColumnIdentifiers(col_path_list);
        row_path_list = new Object[col_path_list.length];

        refreshPathModel();

        table_path_list.setModel(model_path_list);
        table_path_list.getTableHeader().setReorderingAllowed(false);

        table_path_list.getColumnModel().getColumn(0).setMaxWidth(80);
        table_path_list.getColumnModel().getColumn(0).setMinWidth(25);
        // ## MODEL PATH LIST

        // PATH ADD-DELETE-SEARCH ACTION LISTENERS
        btn_path_add.addActionListener(e -> {
            if (Helper.isFieldEmpty(field_path_name)) {
                Helper.showMessage("Please fill in path name.", "Error Adding Path!");
            } else {
                String pathName = field_path_name.getText();

                if (Patika.add(pathName)) {
                    Helper.showMessage("A new path has been added.", "Path Added");
                    refreshPathModel();
                    field_path_name.setText(null);
                }
            }
        });

        table_path_list.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int r = table_path_list.rowAtPoint(e.getPoint());
                if (r >= 0 && r < table_path_list.getRowCount()) {
                    table_path_list.setRowSelectionInterval(r, r);
                } else {
                    table_path_list.clearSelection();
                }
                showPathPopup(e);
            }
        });
        // ## PATH ADD-DELETE-SEARCH ACTION LISTENERS

        // MODEL COURSE LIST
        model_course_list = new DefaultTableModel();
        Object[] col_course_list = {"ID", "Course Name", "Language", "Path Name", "Educator"};
        model_course_list.setColumnIdentifiers(col_course_list);
        row_course_list = new Object[col_course_list.length];

        refreshCourseModel();
        table_course_list.setModel(model_course_list);
        table_course_list.getTableHeader().setReorderingAllowed(false);

        table_course_list.getColumnModel().getColumn(0).setMaxWidth(80);
        table_course_list.getColumnModel().getColumn(0).setMinWidth(25);
        //## MODEL COURSE LIST

        // COURSE ADD-DELETE-SEARCH ACTION LISTENERS
        btn_course_add.addActionListener(e -> {

            if (combo_course_path.getItemCount() == 0) {
                Helper.showMessage("No course can be created without a path!", "No Paths Selected!");
            } else if (combo_course_educator.getItemCount() == 0) {
                Helper.showMessage("No course can be created without an educator!", "No Educator Selected!");
            } else {
                Item pathItem = (Item) combo_course_path.getSelectedItem();
                Item educatorItem = (Item) combo_course_educator.getSelectedItem();

                if (Helper.isFieldEmpty(field_course_name, field_course_language)) {
                    Helper.showMessage("Please fill in the empty fields.\n(course name and course language)", "Course Adding Error!");
                } else {
                    if (Course.add(field_course_name.getText(), field_course_language.getText(), ((Item) combo_course_educator.getSelectedItem()).getKey(), ((Item) combo_course_path.getSelectedItem()).getKey())) {
                        Helper.showMessage("A new course is added.", "Course Adding Message");
                        field_course_name.setText(null);
                        field_course_language.setText(null);
                        refreshCourseModel();
                    }
                }
            }
        });

        table_course_list.getSelectionModel().addListSelectionListener(e -> {

            if (table_course_list.getSelectedRow() != -1) {
                String selectedRow = table_course_list.getValueAt(table_course_list.getSelectedRow(), 0).toString();
                field_course_id.setText(selectedRow);
            } else {
                field_course_id.setText(null);
            }
        });

        btn_course_delete.addActionListener(e -> {
            if (!Helper.isFieldEmpty(field_course_id)) {
                if (Course.delete(Integer.parseInt(field_course_id.getText()))) {
                    Helper.showMessage("The course has been deleted!", "Deleting Message");
                    refreshCourseModel();
                }
            }
        });
        // ### COURSE ADD-DELETE-SEARCH ACTION LISTENERS ###
    }

    private void refreshUserModel() {
        DefaultTableModel clearModel = (DefaultTableModel) table_user_list.getModel();
        clearModel.setRowCount(0);

        for (User user : User.getList()) {
            int i = 0;
            row_user_list[i++] = user.getId();
            row_user_list[i++] = user.getFullname();
            row_user_list[i++] = user.getUsername();
            row_user_list[i++] = user.getPassword();
            row_user_list[i] = user.getType();

            model_user_list.addRow(row_user_list);
        }
    }

    private void refreshUserModel(ArrayList<User> filteredUsers) {
        DefaultTableModel clearModel = (DefaultTableModel) table_user_list.getModel();
        clearModel.setRowCount(0);
        int i;
        for (User user : filteredUsers) {
            i = 0;
            row_user_list[i++] = user.getId();
            row_user_list[i++] = user.getFullname();
            row_user_list[i++] = user.getUsername();
            row_user_list[i++] = user.getPassword();
            row_user_list[i] = user.getType();

            model_user_list.addRow(row_user_list);
        }
    }

    private void refreshPathModel() {
        DefaultTableModel clearModel = (DefaultTableModel) table_path_list.getModel();
        clearModel.setRowCount(0);
        int i;
        for (Patika patika : Patika.getList()) {
            i = 0;
            row_path_list[i++] = patika.getId();
            row_path_list[i] = patika.getName();

            model_path_list.addRow(row_path_list);
        }
        loadCourseComboPath();
        loadCourseComboEducator();
    }

    private void refreshCourseModel() {
        DefaultTableModel clearModel = (DefaultTableModel) table_course_list.getModel();
        clearModel.setRowCount(0);
        int i;
        for (Course course : Course.getList()) {
            i = 0;
            row_course_list[i++] = course.getCourseId();
            row_course_list[i++] = course.getCourseName();
            row_course_list[i++] = course.getCourseLanguage();
            row_course_list[i++] = course.getCoursePath().getName();
            row_course_list[i] = course.getCourseEducator().getFullname();

            model_course_list.addRow(row_course_list);
        }
        loadCourseComboPath();
        loadCourseComboEducator();
    }

    private void showPathPopup(MouseEvent e) {
        if (pathPopupMenu.isPopupTrigger(e)) {
            Point point = e.getPoint();
            int row = table_path_list.rowAtPoint(point);

            if (!table_path_list.isRowSelected(row)) {
                table_path_list.setRowSelectionInterval(row, row);
            }
            pathPopupMenu.show(table_path_list, e.getX(), e.getY());
        }
    }

    private void loadCourseComboPath() {
        combo_course_path.removeAllItems();

        for (Patika p : Patika.getList()) {
            combo_course_path.addItem(new Item(p.getId(), p.getName()));
        }
    }

    private void loadCourseComboEducator() {
        combo_course_educator.removeAllItems();

        for (User educator : User.getEducatorList()) {
            combo_course_educator.addItem(new Item(educator.getId(), educator.getFullname()));
        }
    }
}
