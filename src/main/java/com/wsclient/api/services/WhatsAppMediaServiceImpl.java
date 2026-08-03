package com.wsclient.api.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsclient.api.messages.response.DeleteMediaResponse;
import com.wsclient.api.messages.response.MediaInfoResponse;
import com.wsclient.api.messages.response.MediaResponse;
import com.wsclient.api.validators.ConfigValidator;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of the {@link WhatsAppMediaService} interface that handles
 * media upload operations to the WhatsApp server.
 */
@RequiredArgsConstructor
public class WhatsAppMediaServiceImpl implements WhatsAppMediaService {

    private String whatsappApiUrl;
    private String phoneNumberId;
    private String token;

    private final WhatsAppService whatsAppService;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    /**
     * WhatsAppMediaServiceImpl
     * 
     * /**
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
        whatsAppService.configureWhatsAppApi(whatsappApiUrl, phoneNumberId, null, token);
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
    public CompletableFuture<MediaResponse> uploadMedia(InputStream media, String fileName, String type)
            throws IOException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateInput(media, fileName, type);

                final HttpPost httpPost = createHttpPost();
                final HttpEntity entity = createEntity(media, fileName, type);
                httpPost.setEntity(entity);

                String responseBody = whatsAppService.sendRequest(httpPost);
                return parseMediaResponse(responseBody);
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
                validateInput(filePath, fileName, type);
                final HttpPost httpPost = createHttpPost();

                final File file = new File(filePath);
                FileInputStream fileInputStream = new FileInputStream(file);
                final HttpEntity entity = createEntity(fileInputStream, type, fileName);

                httpPost.setEntity(entity);
                String responseBody = whatsAppService.sendRequest(httpPost);
                return parseMediaResponse(responseBody);
            } catch (Exception ex) {
                throw new CompletionException(ex);
            }
        });
    }

    /**
     * Retrieves the details (including a temporary download URL) of a
     * previously uploaded media resource.
     *
     * @param mediaId the ID of the media resource to look up.
     * @return a {@link CompletableFuture} that will complete with the media's
     *         details once the request is successful.
     * @throws IOException if an I/O error occurs during the request.
     */
    @Override
    public CompletableFuture<MediaInfoResponse> getMedia(String mediaId) throws IOException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateMediaId(mediaId);

                final HttpGet httpGet = new HttpGet(String.format("%s/%s", whatsappApiUrl, mediaId));
                httpGet.addHeader("Authorization", String.format("Bearer %s", token));

                String responseBody = whatsAppService.sendRequest(httpGet);
                return OBJECT_MAPPER.readValue(responseBody, MediaInfoResponse.class);
            } catch (Exception ex) {
                throw new CompletionException(ex);
            }
        });
    }

    /**
     * Deletes a previously uploaded media resource from the server.
     *
     * @param mediaId the ID of the media resource to delete.
     * @return a {@link CompletableFuture} that will complete with the deletion
     *         result once the request is successful.
     * @throws IOException if an I/O error occurs during the request.
     */
    @Override
    public CompletableFuture<DeleteMediaResponse> deleteMedia(String mediaId) throws IOException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateMediaId(mediaId);

                final HttpDelete httpDelete = new HttpDelete(String.format("%s/%s", whatsappApiUrl, mediaId));
                httpDelete.addHeader("Authorization", String.format("Bearer %s", token));

                String responseBody = whatsAppService.sendRequest(httpDelete);
                return OBJECT_MAPPER.readValue(responseBody, DeleteMediaResponse.class);
            } catch (Exception ex) {
                throw new CompletionException(ex);
            }
        });
    }

    /**
     * Validates that a media ID was provided.
     *
     * @param mediaId the media ID to validate.
     * @throws IllegalArgumentException if {@code mediaId} is {@code null} or
     *                                  blank.
     */
    private void validateMediaId(String mediaId) {
        if (mediaId == null || mediaId.isBlank()) {
            throw new IllegalArgumentException("Media ID must not be null or blank.");
        }
    }

    /**
     * Builds a multipart HTTP entity containing the media file and type metadata.
     * <p>
     * This entity is suitable for uploading media to the WhatsApp API,
     * using the standard 'file' part for binary content and a 'type'
     * part for the MIME type.
     * </p>
     *
     * @param inputStream the InputStream of the media content; must not be
     *                    {@code null}.
     * @param type        the MIME type of the media (e.g. "image/jpeg"); must not
     *                    be {@code null} or blank.
     * @param fileName    the name to assign to the uploaded file; must not be
     *                    {@code null} or blank.
     * @return a configured {@link HttpEntity} ready to be sent in a
     *         multipart/form-data request.
     */
    private HttpEntity createEntity(InputStream inputStream, String type, String fileName) {
        return MultipartEntityBuilder.create()
                .addPart("file", new InputStreamBody(inputStream, ContentType.create(type), fileName))
                .addTextBody("type", type, ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8))
                .build();
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
     * Validates the input parameters for a media upload operation.
     *
     * @param media    the media input stream; must not be {@code null}.
     * @param fileName the name of the file; must not be {@code null} or blank.
     * @param type     the MIME type of the media; must not be {@code null} or
     *                 blank.
     * @throws IllegalArgumentException if any parameter is invalid.
     */
    private void validateInput(InputStream media, String fileName, String type) {
        if (media == null) {
            throw new IllegalArgumentException("Media input stream must not be null.");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name must not be null or blank.");
        }
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Media type must not be null or blank.");
        }
    }

    /**
     * Validates the input parameters for a media upload operation using a file
     * path.
     *
     * @param filePath the full path to the media file; must not be {@code null} or
     *                 blank.
     * @param fileName the name to assign to the uploaded file; must not be
     *                 {@code null} or blank.
     * @param type     the MIME type of the media (e.g., "image/png"); must not be
     *                 {@code null} or blank.
     * @throws IllegalArgumentException if any parameter is invalid.
     */
    private void validateInput(String filePath, String fileName, String type) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("File path must not be null or blank.");
        }

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name must not be null or blank.");
        }

        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Media type must not be null or blank.");
        }

        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("File does not exist or is not a valid file: " + filePath);
        }
    }

}
