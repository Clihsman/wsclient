package com.wsclient.cloud.api.messages.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.wsclient.cloud.api.exceptions.WhatsAppException;
import com.wsclient.cloud.api.messages.request.Template;
import com.wsclient.cloud.api.messages.request.Text;
import com.wsclient.cloud.api.messages.request.interactive.Interactive;
import com.wsclient.cloud.api.messages.response.WhatsAppResponse;

import lombok.RequiredArgsConstructor;

import static com.wsclient.cloud.api.validators.WhatsAppInputValidator.*;

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
@RequiredArgsConstructor
public class WhatsAppClientImpl implements WhatsAppClient {
    private final WhatsAppService whatsAppService;

    /**
     * Configures the WhatsApp API credentials and endpoint URL.
     * This method initializes the necessary parameters for interacting with the
     * WhatsApp API.
     *
     * @param whatsappApiUrl The base URL of the WhatsApp API.
     * @param phoneNumberId  The ID of the phone number associated with the WhatsApp
     *                       account.
     * @param token          The authentication token for API access.
     * @throws WhatsAppException
     */
    public void configureWhatsAppApi(String whatsappApiUrl, String phoneNumberId, String token) {
        whatsAppService.configureWhatsAppApi(whatsappApiUrl, phoneNumberId, token);
    }

    /**
     * Sends a WhatsApp text message to a specified recipient.
     *
     * @param to   The recipient's phone number in international format.
     * @param text The text message to send.
     * @return A WhatsAppResponse object containing the API response.
     */
    public CompletableFuture<WhatsAppResponse> sendMessageAsync(String to, Text text) {

        WhatsAppException exception = validateMessageInput(to, text);
        if (exception != null) {
            return CompletableFuture.failedFuture(exception);
        }

        Map<String, Object> data = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "text", text);

        return CompletableFuture.supplyAsync(() -> {
            try {
                return whatsAppService.sendRequest(data);
            } catch (Exception e) {
                throw new CompletionException(new WhatsAppException("Request failed", e));
            }
        });
    }

    /**
     * Sends a WhatsApp text message to a specified recipient.
     *
     * @param to   The recipient's phone number in international format.
     * @param text The text message to send.
     * @return A WhatsAppResponse object containing the API response.
     */
    public CompletableFuture<WhatsAppResponse> sendInteractiveAsync(String to, Interactive interactive) {
        WhatsAppException exception = validateInteractiveInput(to, interactive);
        if (exception != null) {
            return CompletableFuture.failedFuture(exception);
        }

        Map<String, Object> data = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "type", "interactive",
                "interactive", interactive);

        return CompletableFuture.supplyAsync(() -> {
            try {
                return whatsAppService.sendRequest(data);
            } catch (Exception e) {
                throw new CompletionException(new WhatsAppException("Request failed", e));
            }
        });
    }

    /**
     * Sends a WhatsApp template message to a specified recipient.
     *
     * @param to       The recipient's phone number in international format.
     * @param template The template object containing the message structure.
     * @return A WhatsAppResponse object containing the API response.
     */
    public CompletableFuture<WhatsAppResponse> sendTemplate(String to, Template template)
            throws IOException, InterruptedException, WhatsAppException {
        Map<String, Object> data = Map.of(
                "messaging_product", "whatsapp",
                "recipient_type", "individual",
                "to", to,
                "type", "template",
                "template", template);

        return CompletableFuture.supplyAsync(() -> {
            try {
                return whatsAppService.sendRequest(data);
            } catch (Exception e) {
                throw new CompletionException(new WhatsAppException("Request failed", e));
            }
        });
    }
}
