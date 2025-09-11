package com.wsclient.api.services;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsclient.api.messages.response.WhatsAppErrorResponse;
import com.wsclient.core.exceptions.WhatsAppException;

public class MetaWebhookServiceImpl implements MetaWebhookService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private String graphUrl;

    @Override
    public void configure(String graphUrl) {
        this.graphUrl = graphUrl;
    }

    @Override
    public CompletableFuture<String> subscribeApp(
            String appId,
            String accessToken,
            String callbackUrl,
            String verifyToken) {

        return CompletableFuture.supplyAsync(() -> {
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                String url = graphUrl + "/" + appId + "/subscriptions";
                HttpPost post = new HttpPost(url);

                // Headers
                post.setHeader("Authorization", "Bearer " + accessToken);
                post.setHeader("Content-Type", "application/json");

                // Body
                Map<String, Object> data = Map.of(
                        "object", "whatsapp_business_account",
                        "callback_url", callbackUrl,
                        "verify_token", verifyToken,
                        "fields", "messages");

                String requestBody = OBJECT_MAPPER.writeValueAsString(data);
                post.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));

                try (CloseableHttpResponse response = client.execute(post)) {
                    String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                    throwIfErrorResponse(response.getStatusLine().getStatusCode(), responseBody);

                    return responseBody;
                }

            } catch (Exception e) {
                throw new RuntimeException("Error registrando webhook: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Checks the HTTP response for errors and throws a {@link WhatsAppException} if
     * an error is detected.
     *
     * @param statusCode   the HTTP status code
     * @param responseBody the response body as String
     * @throws WhatsAppException if the response indicates an error
     */
    private void throwIfErrorResponse(int statusCode, String responseBody) throws WhatsAppException {
        if (statusCode != 200) {
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
                throw new WhatsAppException(
                        "Failed to parse error response",
                        "ParsingError",
                        statusCode,
                        0,
                        null,
                        e);
            }
        }
    }
}