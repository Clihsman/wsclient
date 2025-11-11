package com.wsclient.api.services;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsclient.api.webhook.FBAccessToken;

public class FacebookAuthServiceImpl implements FacebookAuthService {

    private static final String GRAPH_API_URL = "https://graph.facebook.com/oauth/access_token";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Override
    public CompletableFuture<FBAccessToken> getAppAccessToken(String clientId, String clientSecret) {
        return CompletableFuture.supplyAsync(() -> {
            String url = String.format("%s?client_id=%s&client_secret=%s&grant_type=client_credentials",
                    GRAPH_API_URL, clientId, clientSecret);

            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                    CloseableHttpResponse response = httpClient.execute(new HttpGet(url))) {

                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != HttpStatus.SC_OK) {
                    String errorBody = response.getEntity() != null
                            ? EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)
                            : "No response body";
                    throw new CompletionException(
                            new RuntimeException("Failed to get access token. HTTP code: " + statusCode +
                                    " Body: " + errorBody));
                }

                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new CompletionException(new RuntimeException("Empty response from Facebook API"));
                }

                String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                return OBJECT_MAPPER.readValue(json, FBAccessToken.class);

            } catch (Exception e) {
                throw new CompletionException("Error fetching Facebook access token", e);
            }
        });
    }
}
