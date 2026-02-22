package com.wsclient.api.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.wsclient.api.messages.response.WhatsAppErrorResponse;
import com.wsclient.api.messages.response.WhatsAppResponse;
import com.wsclient.api.messages.response.template.TemplatesResponse;
import com.wsclient.api.validators.ConfigValidator;
import com.wsclient.core.exceptions.WhatsAppException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private String whatsappApiUrl;
    private String phoneNumberId;
    private String businessAccount;
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
    @Override
    public void configureWhatsAppApi(String whatsappApiUrl, String phoneNumberId, String businessAccount,
            String token) {
        this.whatsappApiUrl = whatsappApiUrl;
        this.phoneNumberId = phoneNumberId;
        this.businessAccount = Optional.ofNullable(businessAccount).orElse(this.businessAccount);
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
    @Override
    public WhatsAppResponse sendRequest(Map<String, Object> data, String path)
            throws IOException, InterruptedException, WhatsAppException {
        HttpRequest httpRequest = createHttpRequest(data, path);
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return getWhatsAppResponse(response);
    }

    @Override
    public WhatsAppResponse sendRequest(ObjectNode data, String path)
            throws IOException, InterruptedException, WhatsAppException {
        HttpRequest httpRequest = createHttpRequest(data, path);
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return getWhatsAppResponse(response);
    }

    /**
     * Sends the given {@link HttpPost} request to the server.
     *
     * @param httpPost the HTTP POST request to be executed.
     * @throws IOException       if an I/O error occurs while sending the request or
     *                           receiving the response.
     * @throws WhatsAppException if the response indicates an error from the
     *                           WhatsApp server.
     * @throws ParseException    if there is an error parsing the response body.
     * @return the response body as a string.
     */
    @Override
    public String sendRequest(HttpPost httpPost) throws IOException, ParseException, WhatsAppException {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .build()) {

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {

                HttpEntity responseEntity = response.getEntity();

                if (responseEntity == null) {
                    throw new IOException("No response received from the server.");
                }

                throwIfErrorResponse(response);

                String body = EntityUtils.toString(responseEntity);
                EntityUtils.consume(responseEntity);

                return body;
            }
        }
    }

    @Override
    public String sendRequest(HttpGet HttpGet) throws IOException, WhatsAppException {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .build()) {

            try (CloseableHttpResponse response = httpClient.execute(HttpGet)) {

                HttpEntity responseEntity = response.getEntity();

                if (responseEntity == null) {
                    throw new IOException("No response received from the server.");
                }

                throwIfErrorResponse(response);

                String body = EntityUtils.toString(responseEntity);
                EntityUtils.consume(responseEntity);

                return body;
            }
        }
    }

    /**
     * Retrieves the list of available WhatsApp message templates.
     *
     * <p>
     * This method contacts the WhatsApp Business API to fetch all registered
     * message templates associated with the account. Templates can be used to send
     * pre-approved messages to users, such as notifications, alerts, and updates.
     * </p>
     *
     * @return A {@link CompletableFuture} that resolves to a
     *         {@link TemplatesResponse}
     *         containing the list of available templates.
     */
    @Override
    public CompletableFuture<TemplatesResponse> getTamplates() {
        return CompletableFuture.supplyAsync(() -> {
            Objects.nonNull(businessAccount);

            String url = String.format("%s/%s/message_templates?access_token=%s",
                    whatsappApiUrl,
                    businessAccount,
                    token);

            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Accept", "application/json");

            try (CloseableHttpClient client = HttpClientBuilder.create().build();
                    CloseableHttpResponse response = client.execute(httpGet)) {

                HttpEntity responseEntity = response.getEntity();
                if (responseEntity == null) {
                    throw new IOException("No response received from the WhatsApp API.");
                }

                throwIfErrorResponse(response);

                String body = EntityUtils.toString(responseEntity);
                EntityUtils.consume(responseEntity);

                System.out.println(body);

                return OBJECT_MAPPER.readValue(body, TemplatesResponse.class);

            } catch (IOException | ParseException | WhatsAppException e) {
                throw new CompletionException("Failed to fetch WhatsApp templates", e);
            }
        });
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
    private HttpRequest createHttpRequest(Object body, String path) throws JsonProcessingException {
        String bodyString = OBJECT_MAPPER.writeValueAsString(body);

        return HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%s/%s", whatsappApiUrl, phoneNumberId, path)))
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

        if (statusCode != HttpStatus.SC_OK) {
            try {
                WhatsAppErrorResponse whatsAppErrorResponse = OBJECT_MAPPER.readValue(response.body(),
                        WhatsAppErrorResponse.class);
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
     * @throws IOException       if an I/O error occurs while reading the response
     *                           body.
     * @throws ParseException    if there is an error parsing the response body.
     * @see WhatsAppException
     */
    private void throwIfErrorResponse(CloseableHttpResponse response)
            throws WhatsAppException, ParseException, IOException {
        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != HttpStatus.SC_OK) {
            try {
                final HttpEntity responseEntity = response.getEntity();
                String body = EntityUtils.toString(responseEntity);
                WhatsAppErrorResponse whatsAppErrorResponse = OBJECT_MAPPER.readValue(body,
                        WhatsAppErrorResponse.class);

                EntityUtils.consume(responseEntity);

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

    @Override
    public String getPhoneNumberId() {
        return phoneNumberId;
    }

    @Override
    public String getBusinessAccount() {
        return businessAccount;
    }

    @Override
    public String getWhatsappApiUrl() {
        return whatsappApiUrl;
    }

}
