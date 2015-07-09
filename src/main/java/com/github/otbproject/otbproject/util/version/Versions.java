package com.github.otbproject.otbproject.util.version;

import com.github.otbproject.otbproject.App;

import java.io.*;
import java.util.Optional;

public class Versions {
    public static Optional<Version> readFromFile(String path) {
        return readFromFile(new File(path));
    }

    public static Optional<Version> readFromFile(File file) {
        if (!file.exists()) {
            return Optional.empty();
        }
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(file));
            String versionStr = fileReader.readLine();
            fileReader.close();
            return Version.parseAsOptional(versionStr);
        } catch (IOException | Version.ParseException e) {
            App.logger.catching(e);
            return Optional.empty();
        }
    }

    public static boolean writeToFile(String path, Version version) {
        return writeToFile(new File(path), version);
    }

    public static boolean writeToFile(File path, Version version) {
        PrintStream ps;
        try {
            ps = new PrintStream(path);
        } catch (FileNotFoundException e) {
            App.logger.catching(e);
            return false;
        }
        ps.println(version);
        ps.close();
        return true;
    }
}
