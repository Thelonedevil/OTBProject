package com.github.otbproject.otbproject.util.version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Optional;

public class Version implements Comparable<Version> {
    public final int major;
    public final int minor;
    public final int patch;
    private final boolean hasPatch;
    public final Type type;

    private Version(int major, int minor, int patch, boolean hasPatch, Type type) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.hasPatch = hasPatch;
        this.type = type;
    }

    public static Version create(int major, int minor, Type type) throws IllegalArgumentException {
        valueCheck(major, minor);
        typeCheck(type);
        return new Version(major, minor, 0, false, type);
    }

    public static Version create(int major, int minor, int patch, Type type) throws IllegalArgumentException {
        valueCheck(major, minor, patch);
        typeCheck(type);
        return new Version(major, minor, patch, true, type);
    }

    public boolean hasPatch() {
        return hasPatch;
    }

    public static Version parseVersion(String versionString) throws ParseException {
        if (versionString == null) {
            throw new ParseException(null);
        }

        String[] split = versionString.split("\\.");
        if ((split.length != 2) && (split.length != 3)) {
            throw new ParseException(versionString);
        }

        try {
            int major = Integer.parseInt(split[0]);
            int minor;
            int patch;
            boolean hasPatch;
            Type type;

            if (split.length == 2) {
                split = split[1].split("-", 2);
                minor = Integer.parseInt(split[0]);
                patch = 0;
                hasPatch = false;
            } else /* length == 3 */ {
                minor = Integer.parseInt(split[1]);
                split = split[2].split("-", 2);
                patch = Integer.parseInt(split[0]);
                hasPatch = true;
            }
            if (split.length == 1) {
                type = Type.RELEASE;
            } else {
                type = Type.valueOf(split[1].toUpperCase());
            }

            if ((major < 0) || (minor < 0) || (patch < 0)) {
                throw new ParseException(versionString);
            }

            return new Version(major, minor, patch, hasPatch, type);
        } catch (IllegalArgumentException e) {
            throw new ParseException(versionString, e);
        }
    }

    public static Optional<Version> parseAsOptional(String versionString) {
        try {
            return Optional.of(parseVersion(versionString));
        } catch (ParseException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return major + "." + minor + (hasPatch ? ("." + patch) : "")
                + ((type == Type.RELEASE) ? "" : ("-" + type.name()));
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(major)
                .append(minor)
                .append(patch)
                .append(type)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Version)) {
            return false;
        }
        Version rhs = (Version) obj;
        return new EqualsBuilder()
                .append(major, rhs.major)
                .append(minor, rhs.minor)
                .append(patch, rhs.patch)
                .append(type, rhs.type)
                .isEquals();
    }

    // x.x sorts less than x.x.0
    @Override
    public int compareTo(Version o) {
        return (major != o.major) ? Integer.compare(major, o.major)
                : ((minor != o.minor) ? Integer.compare(minor, o.minor)
                : ((patch != o.patch) ? Integer.compare(patch, o.patch)
                : ((hasPatch != o.hasPatch) ? Boolean.compare(hasPatch, o.hasPatch)
                : type.compareTo(o.type))));
    }

    public Checker checker() {
        return new Checker(this);
    }

    private static void valueCheck(int... values) throws IllegalArgumentException {
        for (int value : values) {
            if (value < 0) {
                throw new IllegalArgumentException("Versions cannot have negative values");
            }
        }
    }

    private static void typeCheck(Type type) throws IllegalArgumentException {
        if (type == null) {
            throw new IllegalArgumentException("Version type cannot be null");
        }
    }

    public enum Type {
        SNAPSHOT, UNSTABLE, RELEASE
    }

    public static class ParseException extends IllegalArgumentException {
        private ParseException(String versionString) {
            super("Invalid version string: " + versionString);
        }

        private ParseException(String versionString, Throwable cause) {
            super(("Invalid version string: " + versionString), cause);
        }
    }

    public static class Checker {
        private final Version version;
        private int major = -1;
        private int minor = -1;
        private int patch = -1;
        private Type type = null;

        private Checker(Version version) {
            this.version = version;
        }

        public Checker major(int value) throws IllegalArgumentException {
            valueCheck(value);
            major = value;
            return this;
        }

        public Checker minor(int value) throws IllegalArgumentException {
            valueCheck(value);
            minor = value;
            return this;
        }

        public Checker patch(int value) throws IllegalArgumentException {
            valueCheck(value);
            patch = value;
            return this;
        }

        public Checker type(Type type) {
            typeCheck(type);
            this.type = type;
            return this;
        }

        public boolean isVersion() {
            return ((major < 0) || (major == version.major))
                    && ((minor < 0) || (minor == version.minor))
                    && ((patch < 0) || (patch == version.patch))
                    && ((type == null) || (type == version.type));
        }
    }
}
