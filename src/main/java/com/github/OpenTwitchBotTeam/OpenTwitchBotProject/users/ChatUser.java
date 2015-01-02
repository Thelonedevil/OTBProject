package com.github.OpenTwitchBotTeam.OpenTwitchBotProject.users;


public class ChatUser {

    public static String getUserLevel(String nick) {
        return ""; // TODO fix
    }

    private final String userName;
    private UserLevel userLevel;

    public ChatUser(String userName, UserLevel userLevel) {
        this.userName = userName;
        this.userLevel = userLevel;
    }

    public ChatUser(String userName) {
        this(userName, UserLevel.DEFAULT);
    }

    public String getUserName() {
        return userName;
    }

    public UserLevel getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(UserLevel userLevel) {
        this.userLevel = userLevel;
    }
}
