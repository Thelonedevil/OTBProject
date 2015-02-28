package com.github.otbproject.otbproject.util;

import com.github.otbproject.otbproject.config.*;
import com.github.otbproject.otbproject.fs.FSUtil;

import java.io.File;

public class ConfigFileGenerator {
    public static void generateAccountConfig() {
        Account account = DefaultConfigGenerator.createAccountConfig();
        JsonHandler.writeValue(FSUtil.defaultsDir() + File.separator + "account.json", account);
    }

    public static void generateBotConfig() {
        BotConfig botConfig = DefaultConfigGenerator.createBotConfig();
        JsonHandler.writeValue(FSUtil.defaultsDir() + File.separator + "bot-config.json", botConfig);
    }

    public static void generateChannelConfig() {
        ChannelConfig channelConfig = DefaultConfigGenerator.createChannelConfig();
        JsonHandler.writeValue(FSUtil.defaultsDir() + File.separator + "channel-config.json", channelConfig);
    }

    public static void generateGeneralConfig() {
        GeneralConfig generalConfig = DefaultConfigGenerator.createGeneralConfig();
        JsonHandler.writeValue(FSUtil.defaultsDir() + File.separator + "general-config.json", generalConfig);
    }
}
