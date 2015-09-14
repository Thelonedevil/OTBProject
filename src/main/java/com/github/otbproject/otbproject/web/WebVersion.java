package com.github.otbproject.otbproject.web;

import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.util.version.Version;
import com.github.otbproject.otbproject.util.version.Versions;

import java.io.File;
import java.util.Optional;

public class WebVersion {
    private static class LatestVersionHolder {
        private static final Version FIELD = Version.parseAsOptional(Versions.lookupLatestGithubVersion("otbwebinterface"))
                .orElse(Version.create(0, 0, Version.Type.RELEASE));
    }

    public static Version latest() {
        return LatestVersionHolder.FIELD;
    }

    static Optional<Version> lookupCurrent() {
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