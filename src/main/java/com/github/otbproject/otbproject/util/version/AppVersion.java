package com.github.otbproject.otbproject.util.version;

public class AppVersion {
    private static Version LATEST;
    private static final Version CURRENT;
    private static final AppVersion dummyInstance;

    static {
        dummyInstance = new AppVersion();
        CURRENT = getCurrentVersion();
    }

    private static String getVersionString() {
        return dummyInstance.getClass().getPackage().getImplementationVersion();
    }

    public static Version current() {
        return CURRENT;
    }

    private static Version getCurrentVersion() {
        Version version;
        try {
            version = Version.parseVersion(getVersionString());
        } catch (Version.ParseException e) {
            e.printStackTrace(); // because logger not initialized, and should also never fail in actual execution
            version = Version.create(0, 0, 0, Version.Type.RELEASE);
        }
        return version;
    }

    public static Version latest() {
        if (LATEST == null) {
            getLatest();
        }
        return LATEST;
    }

    private static synchronized void getLatest() {
        if (LATEST == null) {
            LATEST = Version.parseAsOptional(Versions.lookupLatestGithubVersion("otbproject"))
                    .orElse(Version.create(0, 0, 0, Version.Type.RELEASE));
        }
    }
}
