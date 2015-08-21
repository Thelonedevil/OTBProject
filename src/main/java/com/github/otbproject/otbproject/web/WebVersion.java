package com.github.otbproject.otbproject.web;

import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.util.version.Version;
import com.github.otbproject.otbproject.util.version.Versions;

import java.io.File;
import java.util.Optional;
import java.util.stream.Stream;

public class WebVersion {
    private static Version LATEST;
    private static Version CURRENT;

    static void updateCurrentToLatest() {
        CURRENT = LATEST;
    }

    public static Version current() {
        if (CURRENT == null) {
            getCurrent();
        }
        return CURRENT;
    }

    private static synchronized void getCurrent() {
        if (CURRENT == null) {
            CURRENT = lookupCurrent().orElse(Version.create(0, 0, Version.Type.RELEASE));
        }
    }

    public static Version latest() {
        if (LATEST == null) {
            getLatest();
        }
        return LATEST;
    }

    private static synchronized void getLatest() {
        if (LATEST == null) {
            LATEST = Version.parseAsOptional(Versions.lookupLatestGithubVersion("otbwebinterface"))
                    .orElse(Version.create(0, 0, Version.Type.RELEASE));
        }
    }

    private static Optional<Version> lookupCurrent() {
        return FSUtil.streamDirectory(new File(FSUtil.webDir()))
                .filter(File::isFile)
                .map(File::getName)
                .filter(s -> s.startsWith(WarDownload.WAR_PREFIX))
                .filter(s -> s.endsWith(WarDownload.WAR_EXT))
                .map(s -> {
                    String versionStr = s.substring(WarDownload.WAR_PREFIX.length(), (s.length() - WarDownload.WAR_EXT.length()));
                    return Version.parseAsOptional(versionStr).orElse(null);
                })
                .filter(version -> version != null)
                .max(Version::compareTo);
    }
}