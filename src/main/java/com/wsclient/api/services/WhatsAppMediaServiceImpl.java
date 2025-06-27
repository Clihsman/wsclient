package com.wsclient.api.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsclient.api.messages.response.MediaResponse;
import com.wsclient.api.messages.response.WhatsAppErrorResponse;
import com.wsclient.api.validators.ConfigValidator;
import com.wsclient.core.exceptions.WhatsAppException;

/**
 * Implementation of the {@link WhatsAppMediaService} interface that handles
 * media upload operations to the WhatsApp server.
 */
public class WhatsAppMediaServiceImpl implements WhatsAppMediaService {

    private String whatsappApiUrl;
    private String phoneNumberId;
    private String token;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * WhatsAppMediaServiceImpl
     */
    public WhatsAppMediaServiceImpl() {
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
    @Override
    public void configureWhatsAppApi(String whatsappApiUrl, String phoneNumberId, String token) {
        this.whatsappApiUrl = whatsappApiUrl;
        this.phoneNumberId = phoneNumberId;
        this.token = token;

        ConfigValidator.validateConfig(whatsappApiUrl, phoneNumberId, token);
    }

    /**
     * Uploads a media file to the server.
     *
     * @param media    the input stream representing the media content to upload.
     * @param fileName the name of the file to be used during upload.
     * @param type     the MIME type of the media (e.g., "image/jpeg", "video/mp4").
     * @return a {@link CompletableFuture} that will complete with the media ID or
     *         URL once the upload is successful.
     * @throws IOException if an I/O error occurs during the upload process.
     */
    @Override
    public CompletableFuture<String> uploadMedia(InputStream media, String fileName, String type) throws IOException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final HttpPost httpPost = createHttpPost();

                final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addPart("file", new InputStreamBody(media, ContentType.create(type), fileName));
                builder.addTextBody("type", type);
                final HttpEntity entity = builder.build();
                httpPost.setEntity(entity);

                sendRequest(httpPost);
                return "";
            } catch (Exception ex) {
                throw new CompletionException(ex);
            }
        });
    }

    /**
     * Uploads a media file from the specified file path to the server.
     *
     * @param filePath the full path to the media file on the local file system.
     * @param fileName the name to assign to the uploaded file.
     * @param type     the MIME type of the media (e.g., "image/png",
     *                 "application/pdf").
     * @return a {@link CompletableFuture} that will complete with the media ID or
     *         URL once the upload is successful.
     * @throws IOException if an I/O error occurs while reading the file or during
     *                     the upload process.
     */
    @Override
    public CompletableFuture<MediaResponse> uploadMedia(String filePath, String fileName, String type)
            throws IOException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final HttpPost httpPost = createHttpPost();

                final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                final File file = new File(filePath);
                builder.addPart("file", new FileBody(file, ContentType.create(type), fileName));
                builder.addTextBody("type", type);
                final HttpEntity entity = builder.build();
                httpPost.setEntity(entity);

                String responseBody = sendRequest(httpPost);
                return parseMediaResponse(responseBody);
            } catch (Exception ex) {
                throw new CompletionException(ex);
            }
        });
    }

    /**
     * Parses the response body from the media upload request and converts it into
     * a {@link MediaResponse} object.
     *
     * @param responseBody the JSON response body as a string.
     * @return a {@link MediaResponse} object containing the parsed data.
     * @throws CompletionException if there is an error during parsing.
     */
    private MediaResponse parseMediaResponse(String responseBody) {
        // Assuming the response body is a JSON string that can be deserialized into
        // MediaResponse
        try {
            return OBJECT_MAPPER.readValue(responseBody, MediaResponse.class);
        } catch (JsonProcessingException e) {
            throw new CompletionException("Failed to parse media response", e);
        }
    }

    /**
     * Creates and configures a new {@link HttpPost} request with the required
     * headers and body.
     *
     * @return a configured {@link HttpPost} instance ready to be executed.
     * @throws JsonProcessingException if there is an error while serializing the
     *                                 request body to JSON.
     */
    private HttpPost createHttpPost() throws JsonProcessingException {
        final HttpPost httppost = new HttpPost(String.format("%s/%s/media", whatsappApiUrl, phoneNumberId));
        httppost.addHeader("Authorization", String.format("Bearer %s", token));
        httppost.addHeader("Accept", "application/json");
        httppost.addHeader("Content-Type", "multipart/form-data");
        return httppost;
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
    private String sendRequest(HttpPost httpPost) throws IOException, ParseException, WhatsAppException {
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

        if (statusCode != 200) {
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
}
