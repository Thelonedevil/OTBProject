package com.github.otbproject.otbproject.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.otbproject.otbproject.App;

import java.io.File;
import java.io.IOException;

public class JsonHandler {
    private static ObjectMapper mapper= new ObjectMapper();

    // Returns null if can't read object
    public static <T> T readValue(String path, Class<T> className) {
        try {
            return mapper.readValue(new File(path), className);
        } catch (IOException e) {
            App.logger.catching(e);
        }
        return null;
    }

    // Logs exception if can't write object
    public static <T> void writeValue(String path, T object) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(path), object);
        } catch (IOException e) {
            App.logger.catching(e);
        }
    }
}
