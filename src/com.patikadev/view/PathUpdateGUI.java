package com.patikadev.view;

import com.patikadev.helper.Helper;
import com.patikadev.model.Patika;

import javax.swing.*;

public class PathUpdateGUI extends JFrame {
    private JPanel wrapper;
    private JTextField field_path_update_name;
    private JButton btn_path_update;
    private Patika patika;

    public PathUpdateGUI(Patika patika) {
        this.patika = patika;

        int guiWidth = 300, guiHeight = 180;

        setContentPane(wrapper);
        setTitle(Helper.PROJECT_TITLE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(guiWidth, guiHeight);
        setLocation((Helper.SCREEN_WIDTH - guiWidth) / 2, (Helper.SCREEN_HEIGHT - guiHeight) / 2);
        setVisible(true);
        field_path_update_name.setText(patika.getName());
        btn_path_update.addActionListener(e -> {
            if (Helper.isFieldEmpty(field_path_update_name)) {
                Helper.showMessage("Please fill in all fields.", "ERROR!");
            } else {
                if (Patika.update(patika.getId(), field_path_update_name.getText())) {
                    Helper.showMessage("The update process is complete.", "Update Message");
                }
                dispose();
            }
        });
    }
}
