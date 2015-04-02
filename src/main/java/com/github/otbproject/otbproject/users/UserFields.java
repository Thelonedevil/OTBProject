package com.github.otbproject.otbproject.users;

import com.github.otbproject.otbproject.database.DataTypes;

import java.util.HashMap;

public class UserFields {
    public static final String NICK = "nick";
    public static final String USER_LEVEL = "userLevel";

    public static final String TABLE_NAME = "tblUsers";

    public static HashMap<String,String> getTableHashMap() {
        HashMap<String,String> userFields = new HashMap<>();
        userFields.put(NICK, DataTypes.STRING);
        userFields.put(USER_LEVEL, DataTypes.STRING);
        return userFields;
    }
}
