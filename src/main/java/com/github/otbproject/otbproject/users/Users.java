package com.github.otbproject.otbproject.users;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.database.DatabaseWrapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Users {

    public static User get(DatabaseWrapper db, String userNick) {
        User user = new User();
        if (db.exists(UserFields.TABLE_NAME, userNick, UserFields.NICK)) {
            ResultSet rs = db.getRecord(UserFields.TABLE_NAME, userNick, UserFields.NICK);
            try {
                user.setNick(rs.getString(UserFields.NICK));
                user.setUserLevel(UserLevel.valueOf(rs.getString(UserFields.USER_LEVEL)));
            } catch (SQLException e) {
                App.logger.catching(e);
            } finally {
                try {
                    rs.close();
                } catch (SQLException e) {
                    App.logger.catching(e);
                }
            }
        }
        return user;
    }

    public static ArrayList<String> getUsers(DatabaseWrapper db) {
        return db.getRecordsList(UserFields.TABLE_NAME, UserFields.NICK);
    }

    public static boolean update(DatabaseWrapper db, HashMap map) {
        return db.updateRecord(UserFields.TABLE_NAME, (String) map.get(UserFields.NICK), UserFields.NICK, map);
    }

    public static boolean exists(DatabaseWrapper db, String userName) {
        return db.exists(UserFields.TABLE_NAME, userName, UserFields.NICK);
    }

    public static boolean add(DatabaseWrapper db, HashMap map) {
        return db.insertRecord(UserFields.TABLE_NAME, (String) map.get(UserFields.NICK), UserFields.NICK, map);
    }

    public static boolean remove(DatabaseWrapper db, String userName) {
        return db.removeRecord(UserFields.TABLE_NAME, userName, UserFields.NICK);
    }

    public static boolean addUserFromObj(DatabaseWrapper db, User user) {
        HashMap<String, String> map = new HashMap<String, String>();

        map.put(UserFields.NICK, user.getNick());
        map.put(UserFields.USER_LEVEL, user.getUserLevel().name());

        if (exists(db, user.getNick())) {
            return update(db, map);
        } else {
            return add(db, map);
        }
    }
}
