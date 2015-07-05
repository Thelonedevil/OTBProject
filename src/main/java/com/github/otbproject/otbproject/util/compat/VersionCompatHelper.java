package com.github.otbproject.otbproject.util.compat;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.config.Account;
import com.github.otbproject.otbproject.config.GeneralConfig;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.util.JsonHandler;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Optional;
import java.util.stream.Stream;

public class VersionCompatHelper {
    public static void fixCompatIssues(String oldVersion) {
        if (oldVersion == null) {
            return;
        }
        if (App.VERSION.startsWith("1.1") && oldVersion.startsWith("1.0")) {
            fix1_0To1_1();
        } else if (App.VERSION.startsWith("2.0") && oldVersion.startsWith("1.1")) {
            fix1_1To2_0();
        }
    }

    private static void fix1_0To1_1() {
        String oldAccountFilePath = FSUtil.configDir() + File.separator + "account.json";
        String twitchAccountFilePath = FSUtil.configDir() + File.separator + FSUtil.ConfigFileNames.ACCOUNT_TWITCH;
        File oldAcctFile = new File(oldAccountFilePath);
        File twitchAcctFile = new File(twitchAccountFilePath);

        if (oldAcctFile.exists() && !twitchAcctFile.exists()) {
            Optional<AccountOld> optional = JsonHandler.readValue(oldAccountFilePath, AccountOld.class);
            if (!optional.isPresent()) {
                return;
            }
            AccountOld accountOld = optional.get();
            Account accountNew = new Account();
            accountNew.setName(accountOld.getName());
            accountNew.setPasskey(accountOld.getOauth());
            JsonHandler.writeValue(twitchAccountFilePath, accountNew);
            // Not sure if need to recreate File for this
            if (new File(twitchAccountFilePath).exists() && !oldAcctFile.delete()) {
                App.logger.warn("Failed to delete old account file: " + oldAccountFilePath);
            }
        }
    }

    private static void fix1_1To2_0() {
        fixScripts();
        fixGeneralConfig();
    }

    private static void fixScripts() {
        // Delete scripts from base dir, because they will be unpacked into a subdirectory
        File scriptsDir = new File(FSUtil.scriptDir());
        File[] files = scriptsDir.listFiles();
        if (files == null) {
            return;
        }
        String newDir = FSUtil.commandScriptDir() + File.separator;
        Stream.of(files)
                .filter(File::isFile)
                .forEach(file -> file.renameTo(new File(newDir + file.getName())));
    }

    private static void fixGeneralConfig() {
        Optional<GeneralConfigOld> optional = JsonHandler.readValue((FSUtil.configDir() + File.separator + FSUtil.ConfigFileNames.GENERAL_CONFIG), GeneralConfigOld.class);
        if (!optional.isPresent()) {
            return;
        }
        GeneralConfigOld configOld = optional.get();
        GeneralConfig configNew = new GeneralConfig();

        configNew.setService(configOld.getServiceName());
        configNew.setPortNumber(configOld.getPortNumber());
        configNew.setIp_binding(configOld.getIp_binding());
        configNew.permanently_enabled_commands = configOld.permanently_enabled_commands;
        JsonHandler.writeValue((FSUtil.configDir() + File.separator + FSUtil.ConfigFileNames.GENERAL_CONFIG), configNew);
    }
}
