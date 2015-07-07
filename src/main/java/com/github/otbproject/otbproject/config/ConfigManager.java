package com.github.otbproject.otbproject.config;

public class ConfigManager {
    private Account account;
    private GeneralConfig generalConfig;
    private BotConfig botConfig;
    private WebConfig webConfig;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public GeneralConfig getGeneralConfig() {
        return generalConfig;
    }

    public void setGeneralConfig(GeneralConfig generalConfig) {
        this.generalConfig = generalConfig;
    }

    public BotConfig getBotConfig() {
        return botConfig;
    }

    public void setBotConfig(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    public WebConfig getWebConfig() {
        return webConfig;
    }

    public void setWebConfig(WebConfig webConfig) {
        this.webConfig = webConfig;
    }
}
