package com.wsclient.api.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

/**
 * Defines the contract for handling media-related operations
 * for WhatsApp, such as uploading media files.
 */
public interface WhatsAppMediaService {

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
    public void configureWhatsAppApi(String whatsappApiUrl, String phoneNumberId, String token);

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
    CompletableFuture<String> uploadMedia(InputStream media, String fileName, String type) throws IOException;

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
    CompletableFuture<String> uploadMedia(String filePath, String fileName, String type) throws IOException;
}
