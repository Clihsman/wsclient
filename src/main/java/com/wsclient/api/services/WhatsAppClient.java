package com.wsclient.api.services;

import java.util.concurrent.CompletableFuture;

import com.wsclient.api.messages.request.Media;
import com.wsclient.api.messages.request.Template;
import com.wsclient.api.messages.request.Text;
import com.wsclient.api.messages.request.interactive.Interactive;
import com.wsclient.api.messages.response.WhatsAppResponse;

/**
 * A client for sending messages via WhatsApp's API.
 * <p>
 * This class provides methods to send text messages and interactive messages
 * using
 * WhatsApp's messaging service. It ensures input validation before sending
 * requests.
 * </p>
 * 
 * <h2>Example Usage:</h2>
 * 
 * <pre>
 * {@code
 * WhatsAppClient client = new WhatsAppClient();
 * Text textMessage = new Text("Hello, this is a test message!");
 * client.sendMessage("+1234567890", textMessage);
 * }
 * </pre>
 *
 * @author Clisman Isaac Iscala
 * @version 1.0
 * @since 2025-03-10
 */
public interface WhatsAppClient {

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
         * Sends a WhatsApp text message to a specified recipient.
         *
         * @param to   The recipient's phone number in international format.
         * @param text The text message to send.
         * @return A WhatsAppResponse object containing the API response.
         */
        public CompletableFuture<WhatsAppResponse> sendTextAsync(String to, Text text);

        /**
         * Sends a WhatsApp image message to a specified recipient.
         *
         * @param to    The recipient's phone number in international format.
         * @param image The image media object containing the image URL or ID, caption,
         *              and optional metadata.
         * @return A CompletableFuture that resolves to a WhatsAppResponse object
         *         containing the API response.
         */
        public CompletableFuture<WhatsAppResponse> sendImageAsync(String to, Media image);

        /**
         * Sends a WhatsApp video message asynchronously to a specified recipient.
         * <p>
         * This method sends a video message through the WhatsApp Cloud API using the
         * provided recipient number and {@link Media} object. The video can be
         * referenced either by its uploaded {@code id} or a publicly accessible
         * {@code link}. Optionally, a caption can be included with the video.
         * </p>
         *
         * <p>
         * <strong>Notes:</strong>
         * </p>
         * <ul>
         * <li>The recipient's phone number must be in international format and contain
         * only digits (e.g., "573001112233").</li>
         * <li>The {@link Media} object must include either a valid {@code id} or
         * {@code link}.</li>
         * <li>If a caption is included, its length must not exceed 1024
         * characters.</li>
         * </ul>
         *
         * @param to    The recipient's phone number in international format.
         * @param video The video media object containing the video URL or ID, caption,
         *              and optional metadata.
         * @return A {@link CompletableFuture} that resolves to a
         *         {@link WhatsAppResponse}
         *         containing the API response.
         * @throws IllegalArgumentException if validation of the recipient or video
         *                                  media fails.
         */
        public CompletableFuture<WhatsAppResponse> sendVideoAsync(String to, Media video);

        /**
         * Sends a WhatsApp audio message asynchronously to a specified recipient.
         * <p>
         * This method sends an audio message through the WhatsApp Cloud API using the
         * provided recipient number and {@link Media} object. The audio can be
         * referenced either by its uploaded {@code id} or a publicly accessible
         * {@code link}.
         * </p>
         *
         * <p>
         * <strong>Notes:</strong>
         * </p>
         * <ul>
         * <li>The recipient's phone number must be in international format and contain
         * only digits (e.g., "573001112233").</li>
         * <li>The {@link Media} object must include either a valid {@code id} or
         * {@code link}.</li>
         * <li>Audio messages do not support captions.</li>
         * </ul>
         *
         * @param to    The recipient's phone number in international format.
         * @param audio The audio media object containing the audio URL or ID and
         *              optional metadata.
         * @return A {@link CompletableFuture} that resolves to a
         *         {@link WhatsAppResponse} containing the API response.
         * @throws IllegalArgumentException if validation of the recipient or audio
         *                                  media fails.
         */
        public CompletableFuture<WhatsAppResponse> sendAudioAsync(String to, Media audio);

        /**
         * Sends a WhatsApp document message to a specified recipient.
         *
         * <p>
         * This method sends a document (such as a PDF, DOCX, or TXT file)
         * to the given phone number using the WhatsApp Cloud API. The {@link Media}
         * object must include either a valid {@code id} (from a previously uploaded
         * media) or a valid {@code link} (URL to the file). The {@code filename} field
         * is <b>required</b> for document messages, as specified by Meta’s API.
         * </p>
         *
         * @param to       The recipient's phone number in international format (e.g.,
         *                 "573001112233").
         * @param document The {@link Media} object containing the document details.
         *                 Must include either an {@code id} or a {@code link}, and a
         *                 {@code filename}.
         * @return A {@link CompletableFuture} resolving to a {@link WhatsAppResponse}
         *         object
         *         containing the API response.
         */
        public CompletableFuture<WhatsAppResponse> sendDocumentAsync(String to, Media document);

        /**
         * Sends a WhatsApp interactive message to a specified recipient.
         *
         * @param to          The recipient's phone number in international format.
         * @param interactive The interactive message to send.
         * @return A WhatsAppResponse object containing the API response.
         */
        public CompletableFuture<WhatsAppResponse> sendInteractiveAsync(String to, Interactive interactive);

        /**
         * Sends a WhatsApp template message to a specified recipient.
         *
         * @param to       The recipient's phone number in international format.
         * @param template The template object containing the message structure.
         * @return A WhatsAppResponse object containing the API response.
         */
        public CompletableFuture<WhatsAppResponse> sendTemplate(String to, Template template);

        /**
         * Sends a typing indicator for a WhatsApp message.
         * <p>
         * This method sends a request to the WhatsApp API to indicate that the system
         * is typing a response to a specific message. This improves the user experience
         * by informing the recipient that a reply is being prepared.
         * </p>
         *
         * @param messageId The unique identifier of the message related to the typing
         *                  indicator.
         * @return A {@link CompletableFuture} containing a {@link WhatsAppResponse}
         *         with the API's response.
         *         The future completes when the request is processed.
         */
        public CompletableFuture<WhatsAppResponse> typingIndicator(String messageId);

        /**
         * Marks a WhatsApp message as read.
         * <p>
         * This method sends a request to the WhatsApp API to update the status of a
         * message,
         * marking it as "read". This is useful for acknowledging received messages in
         * an
         * automated system.
         * </p>
         *
         * @param messageId The unique identifier of the message to be marked as read.
         * @return A {@link CompletableFuture} containing a {@link WhatsAppResponse}
         *         with the API's response.
         *         The future completes when the request is processed.
         */
        public CompletableFuture<WhatsAppResponse> markMessageAsRead(String messageId);
}
