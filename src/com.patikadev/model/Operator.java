package com.patikadev.model;

import com.patikadev.helper.Helper;

public class Operator extends User {
    public Operator(int id, String fullname, String username, String password) {
        super(id, fullname, username, password, Helper.USER_TYPE_NAME_OPERATOR);
    }
}
