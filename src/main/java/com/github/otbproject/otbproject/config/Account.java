package com.github.otbproject.otbproject.config;

public class Account {
    private String name = "your_name_here";
    private String passkey = "your_passkey_here";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasskey() {
        return passkey;
    }

    public void setPasskey(String passkey) {
        this.passkey = passkey;
    }
}
