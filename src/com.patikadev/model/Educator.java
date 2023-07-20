package com.patikadev.model;

import com.patikadev.helper.Helper;

public class Educator extends User {
    public Educator(int id, String fullname, String username, String password) {
        super(id, fullname, username, password, Helper.USER_TYPE_NAME_EDUCATOR);
    }
}
