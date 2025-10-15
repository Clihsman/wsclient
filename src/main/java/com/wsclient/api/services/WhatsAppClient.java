package com.wsclient.api.services;

import java.util.concurrent.CompletableFuture;

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
        public CompletableFuture<WhatsAppResponse> sendMessageAsync(String to, Text text);

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
