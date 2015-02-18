package com.github.otbproject.otbproject.config;

public class ConfigValidator {
    public static Account validateAccount(Account account) {
        Account defaultAccount = DefaultConfigGenerator.createAccountConfig();

        if (account == null) {
            return defaultAccount;
        }

        Account validatedAccount = account.getCopy();

        if (validatedAccount.getName() == null) {
            validatedAccount.setName(defaultAccount.getName());
        }

        if (validatedAccount.getOauth() == null) {
            validatedAccount.setName(defaultAccount.getOauth());
        }

        return validatedAccount;
    }

    public static GeneralConfig validateGeneralConfig(GeneralConfig config) {
        GeneralConfig defaultConfig = DefaultConfigGenerator.createGeneralConfig();

        if (config == null) {
            return defaultConfig;
        }

        GeneralConfig validatedConfig = config.getCopy();

        if (validatedConfig.getIp_binding() == null) {
            validatedConfig.setIp_binding(defaultConfig.getIp_binding());
        }

        GeneralConfigHelper.initialize(validatedConfig);

        return validatedConfig;
    }

    public static BotConfig validateBotConfig(BotConfig config) {
        BotConfig defaultConfig = DefaultConfigGenerator.createBotConfig();

        if (config == null) {
            return defaultConfig;
        }

        BotConfig validatedConfig = config.getCopy();

        if (validatedConfig.getChannelJoinSetting() == null) {
            validatedConfig.setChannelJoinSetting(defaultConfig.getChannelJoinSetting());
        }

        BotConfigHelper.initialize(validatedConfig);

        return validatedConfig;
    }

    public static ChannelConfig validateChannelConfig(ChannelConfig config) {
        ChannelConfig defaultConfig = DefaultConfigGenerator.createChannelConfig();

        if (config == null) {
            return defaultConfig;
        }

        ChannelConfig validatedConfig = config.getCopy();

        if (validatedConfig.getCommandCooldown() == null) {
            validatedConfig.setCommandCooldown(defaultConfig.getCommandCooldown());
        }

        if (validatedConfig.userCooldowns.getUl_regular() == null) {
            validatedConfig.userCooldowns.setUl_regular(defaultConfig.userCooldowns.getUl_regular());
        }

        if (validatedConfig.userCooldowns.getUl_subscriber() == null) {
            validatedConfig.userCooldowns.setUl_subscriber(defaultConfig.userCooldowns.getUl_subscriber());
        }

        if (validatedConfig.userCooldowns.getUl_default() == null) {
            validatedConfig.userCooldowns.setUl_default(defaultConfig.userCooldowns.getUl_default());
        }

        return validatedConfig;
    }
}
