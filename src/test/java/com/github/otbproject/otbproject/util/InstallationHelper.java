package com.github.otbproject.otbproject.util;

import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.fs.Setup;

import java.io.IOException;

import static org.junit.Assert.fail;

public class InstallationHelper {
    public static void setupTestInstallation() {
        FSUtil.setBaseDirPath("target/installation");
        System.setProperty("OTBBASE", FSUtil.getBaseDir());
        System.setProperty("OTBCONF", FSUtil.logsDir());
        System.setProperty("OTBDEBUG", "true");
        try {
            Setup.setup();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
