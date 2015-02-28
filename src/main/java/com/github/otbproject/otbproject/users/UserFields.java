package com.github.otbproject.otbproject.users;

import java.util.HashMap;
import java.util.HashSet;

public class UserFields {
    public static final String NICK = "nick";
    public static final String USER_LEVEL = "userLevel";

    public static final String TABLE_NAME = "tblUsers";

    public static HashSet<String> getTableHashSet()
    {
        HashSet <String> userFields = new HashSet<>();
        userFields.add(NICK);
        userFields.add(USER_LEVEL);

        return userFields;
    }
}
