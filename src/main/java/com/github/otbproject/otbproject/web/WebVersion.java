package com.github.otbproject.otbproject.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.util.JsonHandler;
import com.github.otbproject.otbproject.util.version.Version;
import com.github.otbproject.otbproject.util.version.Versions;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

public class WebVersion {
    private static Version LATEST;
    private static Version CURRENT;
    private static final String RATE_LIMIT_INFO_PATH = FSUtil.webDir() + File.separator + "rate-limit-info.json";
    private static final String CURRENT_VERSION_PATH = FSUtil.webDir() + File.separator + "VERSION";

    static void updateCurrentToLatest() {
        CURRENT = LATEST;
        Versions.writeToFile(CURRENT_VERSION_PATH, CURRENT);
    }

    public static Version current() {
        if (CURRENT == null) {
            getCurrent();
        }
        return CURRENT;
    }

    private static synchronized void getCurrent() {
        if (CURRENT == null) {
            CURRENT = Versions.readFromFile(CURRENT_VERSION_PATH).orElse(new Version(0, 0, Version.Type.RELEASE));
        }
    }

    public static Version latest() {
        if (LATEST == null) {
            getLatest();
        }
        return LATEST;
    }

    private static synchronized void getLatest() {
        if (LATEST == null) {
            LATEST = Version.parseAsOptional(lookupLatest()).orElse(new Version(0, 0, Version.Type.RELEASE));
        }
    }

    private static String lookupLatest() {
        if (tooFewRequestsRemaining()) {
            return null;
        }

        HttpClient httpClient = HttpClientBuilder.create().build();
        String url = "https://api.github.com/repos/otbproject/otbwebinterface/releases/latest";
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept", "application/vnd.github.v3+json");
        try {
            HttpResponse response = httpClient.execute(httpGet);

            // Get rate limiting info
            String infoString = response.toString();
            try {
                int xRateLimitRemaining = Integer.parseInt(infoString.split("X-RateLimit-Remaining: ")[1].split(",")[0]);
                long xRateLimitReset = Integer.parseInt(infoString.split("X-RateLimit-Reset: ")[1].split(",")[0]);
                JsonHandler.writeValue(RATE_LIMIT_INFO_PATH, new long[] { xRateLimitRemaining, xRateLimitReset });
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                App.logger.error("Error getting GitHub rate limit information");
                App.logger.catching(e);
            }

            // Parse response to get version
            JsonNode rootNode = JsonHandler.MAPPER.readTree(new BasicResponseHandler().handleResponse(response));
            return rootNode.path("tag_name").textValue();
        } catch (IOException e) {
            App.logger.catching(Level.WARN, e);
            return null;
        }
    }

    private static boolean tooFewRequestsRemaining() {
        Optional<Long[]> optional = JsonHandler.readValue(RATE_LIMIT_INFO_PATH, Long[].class);
        if (!optional.isPresent()) {
            return false;
        }
        Long[] info = optional.get();
        if (info.length != 2) {
            App.logger.warn("Unexpected array length when reading rate limiting information from file");
            return false;
        } else if ((info[0] == null) || info[1] == null) {
            App.logger.warn("Unexpected null values when reading rate limiting information from file");
            return false;
        }
        long remaining = info[0];
        long resetTime = info[1];

        return (remaining < 10) && (resetTime > Instant.now().getEpochSecond());
    }
}