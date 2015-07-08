package com.github.otbproject.otbproject.util;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Version implements Comparable<Version> {
    public final int major;
    public final int minor;
    public final int patch;
    private final boolean hasPatch;
    public final Type type;

    public Version(int major, int minor, Type type) {
        this(major, minor, 0, false, type);
    }

    public Version(int major, int minor, int patch, Type type) {
        this(major, minor, patch, true, type);
    }

    private Version(int major, int minor, int patch, boolean hasPatch, Type type) {
        typeCheck(type);
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.hasPatch = hasPatch;
        this.type = type;
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
            type = Type.valueOf(split[1].toUpperCase());

            if ((major < 0) || (minor < 0) || (patch < 0)) {
                throw new ParseException(versionString);
            }

            return new Version(major, minor, patch, hasPatch, type);
        } catch (IllegalArgumentException e) {
            throw new ParseException(versionString, e);
        }
    }

    @Override
    public String toString() {
        return major + "." + minor +
                (hasPatch ? ("." + patch) : "") +
                (type == Type.RELEASE ? "" : ("-" + type.name()) );
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

    @Override
    public int compareTo(Version o) {
        return (major != o.major) ? Integer.compare(major, o.major)
                : ((minor != o.minor) ? Integer.compare(minor, o.minor)
                : Integer.compare(patch, o.patch));
    }

    public Checker checker() {
        return new Checker(this);
    }

    private static void typeCheck(Type type) throws IllegalArgumentException {
        if (type == null) {
            throw new IllegalArgumentException("Version type cannot be null");
        }
    }

    public enum Type {
        RELEASE, SNAPSHOT, UNSTABLE
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

        private static void valueCheck(int value) throws IllegalArgumentException {
            if (value < 0) {
                throw new IllegalArgumentException("Versions cannot have negative values");
            }
        }

        public Checker type(Type type) {
            typeCheck(type);
            this.type = type;
            return this;
        }

        public boolean isVersion() {
            return  (   (major >= 0) && (major == version.major)    ) &&
                    (   (minor >= 0) && (minor == version.minor)    ) &&
                    (   (patch >= 0) && (patch == version.patch)    ) &&
                    (   (type != null) && (type == version.type)    );
        }
    }
}
