package com.github.otbproject.otbproject.util.version;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotNull;

public class ReleaseData {
    @NotNull
    private final Version version;
    @NotNull
    @SerializedName("download_url")
    private final String downloadUrl;
    @NotNull
    private final String sha256;
    @NotNull
    private final String sha1;
    @NotNull
    private final ImmutableList<String> changes;

    private ReleaseData() {
        version = null;
        downloadUrl = null;
        sha256 = null;
        sha1 = null;
        changes = null;
    }

    public ReleaseData(String downloadUrl, Version version, String sha256, String sha1, ImmutableList<String> changes) {
        this.downloadUrl = downloadUrl;
        this.version = version;
        this.sha256 = sha256;
        this.sha1 = sha1;
        this.changes = changes;
    }

    public Version getVersion() {
        return version;
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ReleaseData)) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        ReleaseData rhs = (ReleaseData) obj;
        return new EqualsBuilder()
                .append(version, rhs.version)
                .append(downloadUrl, rhs.downloadUrl)
                .append(sha256, rhs.sha256)
                .append(sha1, rhs.sha1)
                .append(changes, rhs.changes)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(version)
                .append(downloadUrl)
                .append(sha256)
                .append(sha1)
                .append(changes)
                .toHashCode();
    }
}
