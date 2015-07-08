package com.github.otbproject.otbproject.util;

import com.github.otbproject.otbproject.App;

public class AppVersion {

    public String getVersionString() {
        return getClass().getPackage().getImplementationVersion();
    }

    public Version getVersion() {
        Version version;
        try {
            version = Version.parseVersion(getVersionString());
        } catch (Version.ParseException e) {
            App.logger.catching(e);
            version = new Version(0, 0, 0, Version.Type.RELEASE);
        }
        return version;
    }
}
