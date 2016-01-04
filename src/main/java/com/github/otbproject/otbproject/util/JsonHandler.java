package com.github.otbproject.otbproject.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.util.version.Version;
import com.google.common.collect.ImmutableList;
import com.google.gson.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonHandler {
    public static final Gson GSON;
    public static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    static {
        GSON = new GsonBuilder()
                .registerTypeAdapter(Version.class, new Version.Deserializer())
                .registerTypeAdapter(ImmutableList.class, new ImmutableListDeserializer())
                .create();
    }

    private JsonHandler() {
    }

    // Returns empty Optional if can't read object
    public static <T> Optional<T> readValue(String path, Class<T> className) {
        try {
            T t = MAPPER.readValue(new File(path), className);
            Set<ConstraintViolation<T>> violations = VALIDATOR.validate(t);
            if (!violations.isEmpty()) {
                App.logger.warn("Missing required field(s) in file: " + path);
                App.logger.warn("Missing fields: " + getConstraintViolationsString(violations));
                return Optional.empty();
            }
            return Optional.ofNullable(t);
        } catch (FileNotFoundException e) {
            App.logger.warn("File not found to parse as JSON: " + path);
        } catch (IOException e) {
            App.logger.catching(e);
        }
        return Optional.empty();
    }

    // Logs exception if can't write object
    public static <T> boolean writeValue(String path, T object) {
        try {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(new File(path), object);
            return true;
        } catch (IOException e) {
            App.logger.catching(e);
            return false;
        }
    }

    public static <T> Optional<T> gsonFromJson(String json, Class<T> tClass) {
        try {
            T t = GSON.fromJson(json, tClass);
            Set<ConstraintViolation<T>> violations = VALIDATOR.validate(t);
            if (!violations.isEmpty()) {
                App.logger.warn("Missing required field(s) in JSON: " + getConstraintViolationsString(violations));
                return Optional.empty();
            }
            return Optional.ofNullable(t);
        } catch (JsonSyntaxException e) {
            App.logger.catching(e);
            return Optional.empty();
        }
    }

    private static <T> String getConstraintViolationsString(Set<ConstraintViolation<T>> violations) {
        return violations.stream()
                .map(violation -> "{class: '" + violation.getRootBeanClass().getCanonicalName()
                        + "', field: '" + violation.getPropertyPath() + "'}")
                .collect(Collectors.joining(", ", "[", "]"));
    }

    // TODO: 1/4/16 Check that works with generics
    private static class ImmutableListDeserializer implements JsonDeserializer<ImmutableList<?>> {
        @Override
        public ImmutableList<?> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            List<?> list = context.deserialize(json, ArrayList.class);
            return ImmutableList.copyOf(list);
        }
    }
}
