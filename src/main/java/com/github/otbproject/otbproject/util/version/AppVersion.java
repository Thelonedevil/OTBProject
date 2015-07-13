package com.github.otbproject.otbproject.util.version;

public class AppVersion {
    private static Version LATEST;

    public String getVersionString() {
        return getClass().getPackage().getImplementationVersion();
    }

    public Version getCurrentVersion() {
        Version version;
        try {
            version = Version.parseVersion(getVersionString());
        } catch (Version.ParseException e) {
            e.printStackTrace(); // because logger not initialized, and should also never fail in actual execution
            version = Version.create(0, 0, 0, Version.Type.RELEASE);
        }
        return version;
    }

    public Version latest() {
        if (LATEST == null) {
            getLatest();
        }
        return LATEST;
    }

    private synchronized void getLatest() {
        if (LATEST == null) {
            LATEST = Version.parseAsOptional(Versions.lookupLatestGithubVersion("otbproject"))
                    .orElse(Version.create(0, 0, 0, Version.Type.RELEASE));
        }
    }
}
