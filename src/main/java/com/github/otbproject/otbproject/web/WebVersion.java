package com.github.otbproject.otbproject.web;

import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.util.version.AddonReleaseData;
import com.github.otbproject.otbproject.util.version.Version;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.Optional;

public class WebVersion {
    private static class ReleaseDataHolder {
        private static final ImmutableList<AddonReleaseData> RELEASE_DATA;
        private static final Optional<AddonReleaseData> LATEST_RELEASE_DATA_OPTIONAL;
        private static final Version LATEST_VERSION;
        private static final Version REQUIRED_APP_VERSION;

        static {
            RELEASE_DATA = AddonReleaseData.fetchData("https://otbproject.github.io/release-data/web-interface");

            LATEST_RELEASE_DATA_OPTIONAL = RELEASE_DATA.stream()
                    .max((o1, o2) -> o1.getVersion().compareTo(o2.getVersion()));

            if (LATEST_RELEASE_DATA_OPTIONAL.isPresent()) {
                AddonReleaseData latestReleaseData = LATEST_RELEASE_DATA_OPTIONAL.get();
                LATEST_VERSION = latestReleaseData.getVersion();
                REQUIRED_APP_VERSION = latestReleaseData.getMinimumAppVersion();
            } else {
                LATEST_VERSION = Version.create(0, 0, Version.Type.RELEASE);
                REQUIRED_APP_VERSION = Version.create(0, 0, 0, Version.Type.RELEASE);
            }
        }
    }

    private WebVersion() {}

    public static Version latest() {
        return ReleaseDataHolder.LATEST_VERSION;
    }

    public static Version requiredAppVersionForLatest() {
        return ReleaseDataHolder.REQUIRED_APP_VERSION;
    }

    public static ImmutableList<AddonReleaseData> getReleaseData() {
        return ReleaseDataHolder.RELEASE_DATA;
    }

    public static Optional<AddonReleaseData> getLatestReleaseData() {
        return ReleaseDataHolder.LATEST_RELEASE_DATA_OPTIONAL;
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