package com.github.otbproject.otbproject.config;

public class DefaultConfigGenerator {

    public static Account createAccountConfig() {
        Account account = new Account();
        account.setName("your_name_here");
        account.setOauth("oauth:some_characters_here");

        return account;
    }

    public static BotConfig createBotConfig() {
        BotConfig botConfig = new BotConfig();
        botConfig.setChannelJoinSetting(ChannelJoinSetting.NONE);
        botConfig.setMessageSendDelayInMilliseconds(1600);
        BotConfigHelper.initialize(botConfig);

        return botConfig;
    }

    public static ChannelConfig createChannelConfig() {
        ChannelConfig channelConfig = new ChannelConfig();
        channelConfig.setCommandCooldown(10);
        channelConfig.userCooldowns = channelConfig.new UserCooldowns();
        channelConfig.userCooldowns.setUl_internal(0);
        channelConfig.userCooldowns.setUl_broadcaster(0);
        channelConfig.userCooldowns.setUl_super_moderator(0);
        channelConfig.userCooldowns.setUl_moderator(0);
        channelConfig.userCooldowns.setUl_regular(15);
        channelConfig.userCooldowns.setUl_subscriber(30);
        channelConfig.userCooldowns.setUl_default(30);
        channelConfig.setDebug(false);
        channelConfig.setEnabled(true);

        return channelConfig;
    }

    public static GeneralConfig createGeneralConfig() {
        GeneralConfig generalConfig = new GeneralConfig();
        generalConfig.setPortNumber(80);
        generalConfig.setIp_binding("0.0.0.0");
        GeneralConfigHelper.initialize(generalConfig);

        return generalConfig;
    }
}
