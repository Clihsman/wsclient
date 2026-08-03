package com.wsclient.api.services;

import java.io.IOException;
import java.net.URLEncoder;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.wsclient.api.messages.response.WhatsAppErrorResponse;
import com.wsclient.api.webhook.FBAccessToken;
import com.wsclient.core.exceptions.WhatsAppException;

public class FacebookAuthServiceImpl implements FacebookAuthService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private String graphApiUrl = "https://graph.facebook.com/oauth/access_token";

    @Override
    public void configure(String graphApiUrl) {
        this.graphApiUrl = graphApiUrl;
    }

    @Override
    public CompletableFuture<FBAccessToken> getAppAccessToken(String clientId, String clientSecret) {
        return requestToken(String.format("client_id=%s&client_secret=%s&grant_type=client_credentials",
                clientId, clientSecret));
    }

    @Override
    public CompletableFuture<FBAccessToken> exchangeCodeForUserToken(String clientId, String clientSecret,
            String redirectUri, String code) {
        return requestToken(String.format("client_id=%s&client_secret=%s&redirect_uri=%s&code=%s",
                clientId, clientSecret,
                URLEncoder.encode(redirectUri, StandardCharsets.UTF_8),
                URLEncoder.encode(code, StandardCharsets.UTF_8)));
    }

    @Override
    public CompletableFuture<FBAccessToken> getLongLivedToken(String clientId, String clientSecret,
            String shortLivedToken) {
        return requestToken(String.format("grant_type=fb_exchange_token&client_id=%s&client_secret=%s"
                + "&fb_exchange_token=%s",
                clientId, clientSecret,
                URLEncoder.encode(shortLivedToken, StandardCharsets.UTF_8)));
    }

    /**
     * Executes a GET request against the configured Graph API OAuth endpoint
     * with the given query string and parses the response into an
     * {@link FBAccessToken}.
     *
     * @param queryParams the already-encoded query string (without the
     *                    leading {@code ?}) to append to {@link #graphApiUrl}.
     * @return a {@link CompletableFuture} that resolves to the parsed token
     *         response.
     */
    private CompletableFuture<FBAccessToken> requestToken(String queryParams) {
        return CompletableFuture.supplyAsync(() -> {
            String url = graphApiUrl + "?" + queryParams;

            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                    CloseableHttpResponse response = httpClient.execute(new HttpGet(url))) {

                HttpEntity entity = response.getEntity();
                String body = entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8) : null;

                throwIfErrorResponse(response.getStatusLine().getStatusCode(), body);

                if (body == null) {
                    throw new WhatsAppException("Empty response from Facebook API", "EmptyResponse", null, null,
                            null);
                }

                return OBJECT_MAPPER.readValue(body, FBAccessToken.class);

            } catch (IOException | WhatsAppException e) {
                throw new CompletionException("Failed to fetch Facebook access token", e);
            }
        });
    }

    /**
     * Checks the HTTP response for errors and throws a {@link WhatsAppException} if
     * an error is detected.
     *
     * @param statusCode   the HTTP status code
     * @param responseBody the response body as String, or {@code null} if the
     *                     response had no body
     * @throws WhatsAppException if the response indicates an error
     */
    private void throwIfErrorResponse(int statusCode, String responseBody) throws WhatsAppException {
        if (statusCode != HttpStatus.SC_OK) {
            if (responseBody == null) {
                throw new WhatsAppException("Facebook API returned an error with no response body",
                        "EmptyErrorResponse", statusCode, 0, null);
            }

            try {
                WhatsAppErrorResponse errorResponse = OBJECT_MAPPER.readValue(responseBody,
                        WhatsAppErrorResponse.class);

                throw new WhatsAppException(
                        errorResponse.error().message(),
                        errorResponse.error().type(),
                        errorResponse.error().code(),
                        errorResponse.error().errorSubcode(),
                        errorResponse.error().fbtraceId());

            } catch (JsonProcessingException e) {
                throw new WhatsAppException("Failed to parse error response", "ParsingError",
                        statusCode, 0, null, e);
            }
        }
    }
}
