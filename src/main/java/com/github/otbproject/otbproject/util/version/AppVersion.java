package com.github.otbproject.otbproject.util.version;


public class AppVersion {

    public String getVersionString() {
        return getClass().getPackage().getImplementationVersion();
    }

    public Version getVersion() {
        Version version;
        try {
            version = Version.parseVersion(getVersionString());
        } catch (Version.ParseException e) {
            e.printStackTrace(); // because logger not initialized, and should also never fail in actual execution
            version = new Version(0, 0, 0, Version.Type.RELEASE);
        }
        return version;
    }
}
