package com.github.otbproject.otbproject.util.version;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.util.JsonHandler;
import com.google.common.collect.ImmutableList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Collectors;

public class AppVersion {
    private static final Version CURRENT = getCurrentVersion();

    private AppVersion() {}

    private static class ReleaseDataHolder {
        private static final ImmutableList<ReleaseData> RELEASE_DATA;
        private static final Version LATEST_VERSION;
        
        static {
            RELEASE_DATA = fetchReleaseData();
            LATEST_VERSION = RELEASE_DATA.stream()
                    .map(ReleaseData::getVersion)
                    .max(Version::compareTo)
                    .orElse(Version.create(0, 0, 0, Version.Type.RELEASE));
        }
        
        private static ImmutableList<ReleaseData> fetchReleaseData() {
            try {
                URL url = new URL("https://otbproject.github.io/release-data/app");
                try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    String json = in.lines().collect(Collectors.joining("\n"));
                    Optional<ReleaseData[]> optional = JsonHandler.gsonFromJson(json, ReleaseData[].class);
                    if (optional.isPresent()) {
                        return ImmutableList.copyOf(optional.get());
                    }
                }
            } catch (IOException e) {
                App.logger.catching(e);
            }
            return ImmutableList.of();
        }
    }

    private static String getVersionString() {
        return AppVersion.class.getPackage().getImplementationVersion();
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
        return ReleaseDataHolder.LATEST_VERSION;
    }

    public static ImmutableList<ReleaseData> getReleaseData() {
        return ReleaseDataHolder.RELEASE_DATA;
    }
}
