package com.github.otbproject.otbproject.config;

public class Account {
    private String name;
    private String oauth;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOauth() {
        return oauth;
    }

    public void setOauth(String oauth) {
        this.oauth = oauth;
    }

    public Account getCopy() {
        Account copy = new Account();

        copy.name = this.name;
        copy.oauth = this.oauth;

        return copy;
    }
}
