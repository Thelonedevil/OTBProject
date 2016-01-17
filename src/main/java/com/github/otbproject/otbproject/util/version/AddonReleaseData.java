package com.github.otbproject.otbproject.util.version;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.util.JsonHandler;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Collectors;

public class AddonReleaseData {
    @NotNull
    private final Version version;
    @NotNull
    @SerializedName("minimum_app_version")
    private final Version minimumAppVersion;
    @NotNull
    @SerializedName("download_url")
    private final String downloadUrl;
    @NotNull
    private final String sha256;
    @NotNull
    private final String sha1;
    @NotNull
    private final ImmutableList<String> changes;

    public AddonReleaseData(ImmutableList<String> changes, Version version, Version minimumAppVersion, String downloadUrl, String sha256, String sha1) {
        this.changes = changes;
        this.version = version;
        this.minimumAppVersion = minimumAppVersion;
        this.downloadUrl = downloadUrl;
        this.sha256 = sha256;
        this.sha1 = sha1;
    }

    public Version getVersion() {
        return version;
    }

    public Version getMinimumAppVersion() {
        return minimumAppVersion;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getSha256() {
        return sha256;
    }

    public String getSha1() {
        return sha1;
    }

    public ImmutableList<String> getChanges() {
        return changes;
    }

    /**
     * Gets an {@link ImmutableList} of {@link AddonReleaseData} from a URL
     * which returning a JSON representation of an array of {@code AddonReleaseData}.
     *
     * <p>For security reasons, the URL MUST use HTTPS.
     *
     * <p>If there is a problem deserializing the JSON, or the JSON represents an
     * empty array, an empty {@code ImmutableList} is returned.
     *
     * @param httpsReleaseDataUrl an HTTPS URL from which to download release data
     * @return an ImmutableList of the release data
     * @throws IllegalArgumentException if the URL does not use HTTPS
     */
    public static ImmutableList<AddonReleaseData> fetchData(String httpsReleaseDataUrl) throws IllegalArgumentException {
        if (!httpsReleaseDataUrl.startsWith("https://")) {
            throw new IllegalArgumentException("URL must start with 'https://'");
        }

        try {
            URL url = new URL(httpsReleaseDataUrl);
            try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String json = in.lines().collect(Collectors.joining("\n"));
                Optional<AddonReleaseData[]> optional = JsonHandler.gsonFromJson(json, AddonReleaseData[].class);
                if (optional.isPresent()) {
                    return ImmutableList.copyOf(optional.get());
                }
            }
        } catch (IOException | JsonSyntaxException e) {
            App.logger.catching(e);
        }
        return ImmutableList.of();
    }
}
