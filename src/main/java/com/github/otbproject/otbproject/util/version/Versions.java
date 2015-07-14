package com.github.otbproject.otbproject.util.version;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.util.JsonHandler;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.time.Instant;
import java.util.Optional;

public class Versions {
    private static final String RATE_LIMIT_INFO_PATH = FSUtil.dataDir() + File.separator + "rate-limit-info.json";

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

    public static String lookupLatestGithubVersion(String otbRepo) {
        if (tooFewRequestsRemaining()) {
            return null;
        }

        HttpClient httpClient = HttpClientBuilder.create().build();
        String url = "https://api.github.com/repos/otbproject/" + otbRepo + "/releases/latest";
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
