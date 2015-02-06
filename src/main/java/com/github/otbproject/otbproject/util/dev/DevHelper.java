package com.github.otbproject.otbproject.util.dev;

import com.github.otbproject.otbproject.fs.Setup;
import com.github.otbproject.otbproject.util.ConfigFileGenerator;

import java.io.IOException;

public class DevHelper {
    public static void run(String[] args) {
        try {
            Setup.setup();
            Setup.setupChannel("the_lone_devil");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ConfigFileGenerator.generateAccountConfig();
        ConfigFileGenerator.generateBotConfig();
        ConfigFileGenerator.generateChannelConfig();
        ConfigFileGenerator.generateGeneralConfig();
    }
}
