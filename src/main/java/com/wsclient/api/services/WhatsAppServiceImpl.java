package com.wsclient.api.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import com.wsclient.api.messages.response.WhatsAppErrorResponse;
import com.wsclient.api.messages.response.WhatsAppResponse;
import com.wsclient.api.validators.ConfigValidator;
import com.wsclient.core.exceptions.WhatsAppException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of the {@link WhatsAppService} interface.
 * 
 * This class provides functionality for interacting with the WhatsApp API,
 * including:
 * <ul>
 * <li>Configuring API credentials.</li>
 * <li>Sending HTTP requests to the API.</li>
 * <li>Processing API responses.</li>
 * </ul>
 * 
 *
 * <p>
 * <b>Usage:</b> This class should be instantiated and configured before making
 * API requests.
 * </p>
 *
 * @author Clisman Isaac Iscala
 * @version 1.0
 * @since 2025-03-10
 */
public class WhatsAppServiceImpl implements WhatsAppService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(); 

    private String whatsappApiUrl;
    private String phoneNumberId;
    private String token;

    private final HttpClient httpClient;

    /**
     * Initializes a new instance of {@code WhatsAppServiceImpl}.
     * <p>
     * This constructor initializes an {@link HttpClient} instance for making HTTP
     * requests.
     * </p>
     */
    public WhatsAppServiceImpl() {
        httpClient = HttpClient.newHttpClient();
    }

    /**
     * Configures the WhatsApp API credentials and endpoint URL.
     * <p>
     * This method initializes the necessary parameters for interacting with the
     * WhatsApp API.
     * It also validates the configuration parameters using {@link ConfigValidator}.
     * </p>
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
     * <p>
     * This method creates an HTTP request, sends it, and processes the response.
     * </p>
     *
     * @param data The request payload as a key-value map.
     * @return A {@link WhatsAppResponse} object containing the API response.
     * @throws IOException          If an I/O error occurs during the HTTP request.
     * @throws InterruptedException If the operation is interrupted.
     * @throws WhatsAppException    If the API response indicates an error.
     */
    public WhatsAppResponse sendRequest(Map<String, Object> data)
            throws IOException, InterruptedException, WhatsAppException {
        HttpRequest httpRequest = createHttpRequest(data);
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return getWhatsAppResponse(response);
    }

    /**
     * Creates an HTTP request object with the given request body.
     * <p>
     * This method serializes the request body to JSON and constructs an
     * {@link HttpRequest}
     * with the appropriate headers and authentication token.
     * </p>
     *
     * @param body The request payload as a key-value map.
     * @return An {@link HttpRequest} object ready to be sent.
     * @throws JsonProcessingException If there is an error serializing the request
     *                                 body.
     */
    private HttpRequest createHttpRequest(Map<String, Object> body) throws JsonProcessingException {
        String bodyString = new ObjectMapper().writeValueAsString(body);

        return HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%s/messages", whatsappApiUrl, phoneNumberId)))
                .header("Content-Type", "application/json")
                .header("Authorization", String.format("Bearer %s", token))
                .POST(HttpRequest.BodyPublishers.ofString(bodyString))
                .build();
    }

    /**
     * Parses the response from the WhatsApp API and converts it into a
     * {@link WhatsAppResponse} object.
     * <p>
     * If the API response contains an error status code, a
     * {@link WhatsAppException} is thrown.
     * </p>
     *
     * @param response The HTTP response from the WhatsApp API.
     * @return A {@link WhatsAppResponse} object containing the API response data.
     * @throws JsonProcessingException If there is an error parsing the response
     *                                 JSON.
     * @throws WhatsAppException       If the response contains an error status
     *                                 code.
     * @throws JsonMappingException    If the response JSON structure is invalid.
     */
    private WhatsAppResponse getWhatsAppResponse(HttpResponse<String> response)
            throws WhatsAppException, JsonMappingException, JsonProcessingException {

        throwIfErrorResponse(response);

        final String body = response.body();
        return OBJECT_MAPPER.readValue(body, WhatsAppResponse.class);
    }

    /**
     * Checks the HTTP response for errors and throws a {@link WhatsAppException} if
     * an error is detected.
     *
     * <p>
     * This method verifies if the response status code is different from 200. If an
     * error is present,
     * it attempts to parse the error details from the response body and throws a
     * {@link WhatsAppException}
     * containing relevant error information. If the response body cannot be parsed,
     * a generic parsing
     * error exception is thrown.
     * </p>
     *
     * @param response the HTTP response to check.
     * @throws WhatsAppException if the response contains an error, including
     *                           parsing failures.
     */
    private void throwIfErrorResponse(HttpResponse<String> response) throws WhatsAppException {
        int statusCode = response.statusCode();

        if (statusCode != 200) {
            try {
                WhatsAppErrorResponse whatsAppErrorResponse = OBJECT_MAPPER.readValue(response.body(), WhatsAppErrorResponse.class);
                throw new WhatsAppException(
                        whatsAppErrorResponse.error().message(),
                        whatsAppErrorResponse.error().type(),
                        whatsAppErrorResponse.error().code(),
                        whatsAppErrorResponse.error().errorSubcode(),
                        whatsAppErrorResponse.error().fbtraceId());
            } catch (JsonProcessingException e) {
                throw new WhatsAppException("Failed to parse error response", "ParsingError",
                        statusCode, 0, null, e);
            }
        }
    }
}
