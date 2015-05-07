package com.github.otbproject.otbproject.beam;

public class BeamChatUser {

    private int user_id;
    private String user_name;
    private String[] user_roles;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String[] getUser_roles() {
        return user_roles;
    }

    public void setUser_roles(String[] user_roles) {
        this.user_roles = user_roles;
    }
}
