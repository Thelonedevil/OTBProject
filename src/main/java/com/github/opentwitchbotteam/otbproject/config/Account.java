package com.github.opentwitchbotteam.otbproject.config;

public class Account implements IConfig {
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
}
