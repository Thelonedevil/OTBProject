package com.github.otbproject.otbproject.serviceapi;

import com.github.otbproject.otbproject.App;
import com.google.common.util.concurrent.Uninterruptibles;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ApiRequest {
    public static String sendRequest(String request) {
        String url = "https://api.twitch.tv/kraken/" + request;
        App.logger.info("Sent request: " + url);

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Accept", "application/vnd.twitchtv.v3+json");
            HttpResponse response = client.execute(httpGet);
            String responseStr = new BasicResponseHandler().handleResponse(response);
            App.logger.debug(responseStr);
            return responseStr;
        } catch (HttpResponseException e) {
            App.logger.info("Request failed.");
        } catch (IOException e) {
            App.logger.catching(e);
        }
        return null;
    }

    public static String attemptRequest(String request, int attempts, int millisecondsBetweenAttempts) {
        for (int i = 0; i < attempts; i++) {
            String response = ApiRequest.sendRequest(request);
            if (response == null) {
                Uninterruptibles.sleepUninterruptibly(millisecondsBetweenAttempts, TimeUnit.MILLISECONDS);
            } else {
                return response;
            }
        }
        return null;
    }
}
