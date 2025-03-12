package com.wsclient.cloud.api.messages.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import com.wsclient.cloud.api.exceptions.WhatsAppException;
import com.wsclient.cloud.api.messages.response.WhatsAppResponse;
import com.wsclient.cloud.api.validators.ConfigValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * WhatsAppServiceImpl
 */
public class WhatsAppServiceImpl implements WhatsAppService {
    private String whatsappApiUrl;
    private String phoneNumberId;
    private String token;

    private final HttpClient httpClient;

    /**
     * WhatsAppServiceImplcls
     */
    public WhatsAppServiceImpl() {
        httpClient = HttpClient.newHttpClient();
    }

    /**
     * Configures the WhatsApp API credentials and endpoint URL.
     * This method initializes the necessary parameters for interacting with the
     * WhatsApp API.
     *
     * @param whatsappApiUrl The base URL of the WhatsApp API.
     * @param phoneNumberId  The ID of the phone number associated with the WhatsApp
     *                       account.
     * @param token          The authentication token for API access.
     */
    public void configureWhatsAppApi(String whatsappApiUrl, String phoneNumberId, String token) {
        this.whatsappApiUrl = whatsappApiUrl;
        this.phoneNumberId = phoneNumberId;
        this.token = token;

        ConfigValidator.validateConfig(whatsappApiUrl, phoneNumberId, token);
    }

    /**
     * Sends an HTTP request to the WhatsApp API with the provided request data.
     *
     * @param data The request payload as a key-value map.
     * @return A WhatsAppResponse object containing the API response.
     * @throws IOException          If an I/O error occurs during the HTTP request.
     * @throws InterruptedException If the operation is interrupted.
     */
    public WhatsAppResponse sendRequest(Map<String, Object> data)
            throws IOException, InterruptedException, WhatsAppException {
        HttpRequest httpRequest = createHttpRequest(data);
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return getWhatsAppResponse(response);
    }

    /**
     * Creates an HTTP request object with the given request body.
     *
     * @param body The request payload as a key-value map.
     * @return An HttpRequest object ready to be sent.
     * @throws JsonProcessingException If there is an error serializing the request
     *                                 body.
     */
    private HttpRequest createHttpRequest(Map<String, Object> body) throws JsonProcessingException {
        String bodyString = new ObjectMapper().writeValueAsString(body);

        return HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%s/messages", whatsappApiUrl, phoneNumberId)))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(bodyString))
                .build();
    }

    /**
     * Parses the response from the WhatsApp API and converts it into a
     * WhatsAppResponse object.
     *
     * @param response The HTTP response from the WhatsApp API.
     * @return A WhatsAppResponse object containing the API response data.
     * @throws JsonProcessingException If there is an error parsing the response
     *                                 JSON.
     * @throws WhatsAppException       If the response contains an error status
     *                                 code.
     * @throws JsonMappingException
     */
    private WhatsAppResponse getWhatsAppResponse(HttpResponse<String> response)
            throws WhatsAppException, JsonMappingException, JsonProcessingException {
        if (response.statusCode() != 200) {
            throw new WhatsAppException("WhatsApp API error: " + response.body());
        }
        return new ObjectMapper().readValue(response.body(), WhatsAppResponse.class);
    }
}
