package com.github.otbproject.otbproject.users;

import com.github.otbproject.otbproject.database.DataTypes;
import com.github.otbproject.otbproject.database.TableFields;

import java.util.HashMap;

public class UserFields {
    public static final String NICK = "nick";
    public static final String USER_LEVEL = "userLevel";

    public static final String TABLE_NAME = "tblUsers";
    public static final String PRIMARY_KEY = NICK;

    public static TableFields getTableFields() {
        HashMap<String,String> userFields = new HashMap<>();
        userFields.put(NICK, DataTypes.STRING);
        userFields.put(USER_LEVEL, DataTypes.STRING);
        return new TableFields(userFields, PRIMARY_KEY);
    }
}
