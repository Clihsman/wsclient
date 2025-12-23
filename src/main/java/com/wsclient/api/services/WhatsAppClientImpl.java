package com.wsclient.api.services;

import static com.wsclient.api.validators.WhatsAppInputValidator.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.wsclient.api.messages.request.Media;
import com.wsclient.api.messages.request.Template;
import com.wsclient.api.messages.request.Text;
import com.wsclient.api.messages.request.interactive.Interactive;
import com.wsclient.api.messages.response.WhatsAppResponse;
import com.wsclient.api.validators.ConfigValidator;

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
 * client.sendMessage("1234567890", textMessage);
 * }
 * </pre>
 *
 * @author Clisman Isaac Iscala
 * @version 1.0
 * @since 2025-03-10
 */
public class WhatsAppClientImpl implements WhatsAppClient {
    private final WhatsAppService whatsAppService;

    /**
     * Constructor for {@code WhatsAppClientImpl}.
     * Initializes the client with the provided WhatsApp service.
     *
     * @param whatsAppService The service used to interact with the WhatsApp API.
     */
    public WhatsAppClientImpl(WhatsAppService whatsAppService) {
        this.whatsAppService = whatsAppService;
    }

    @Override
    public void configureWhatsAppApi(String whatsappApiUrl, String phoneNumberId, String token) {
        ConfigValidator.validateConfig(whatsappApiUrl, phoneNumberId, token);
        whatsAppService.configureWhatsAppApi(whatsappApiUrl, phoneNumberId, null, token);
    }

    @Override
    public CompletableFuture<WhatsAppResponse> sendTextAsync(String to, Text text) {
        final IllegalArgumentException exception = validateTextInput(to, text);
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
                throw new CompletionException(e);
            }
        });
    }

    @Override
    public CompletableFuture<WhatsAppResponse> sendInteractiveAsync(String to, Interactive interactive) {

        final IllegalArgumentException exception = validateInteractiveInput(to, interactive);
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
                throw new CompletionException(e);
            }
        });
    }

    @Override
    public CompletableFuture<WhatsAppResponse> sendTemplate(String to, Template template) {
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
                throw new CompletionException(e);
            }
        });
    }

    @Override
    public CompletableFuture<WhatsAppResponse> sendImageAsync(String to, Media image) {
        final IllegalArgumentException exception = validateImageInput(to, image);
        if (exception != null) {
            return CompletableFuture.failedFuture(exception);
        }

        Map<String, Object> data = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "type", "image",
                "image", image);

        return CompletableFuture.supplyAsync(() -> {
            try {
                return whatsAppService.sendRequest(data);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    @Override
    public CompletableFuture<WhatsAppResponse> sendVideoAsync(String to, Media video) {
        final IllegalArgumentException exception = validateVideoInput(to, video);

        if (exception != null) {
            return CompletableFuture.failedFuture(exception);
        }

        Map<String, Object> data = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "type", "video",
                "video", video);

        return CompletableFuture.supplyAsync(() -> {
            try {
                return whatsAppService.sendRequest(data);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    @Override
    public CompletableFuture<WhatsAppResponse> sendAudioAsync(String to, Media audio) {
        final IllegalArgumentException exception = validateAudioInput(to, audio);

        if (exception != null) {
            return CompletableFuture.failedFuture(exception);
        }

        Map<String, Object> data = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "type", "audio",
                "audio", audio);

        return CompletableFuture.supplyAsync(() -> {
            try {
                return whatsAppService.sendRequest(data);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    @Override
    public CompletableFuture<WhatsAppResponse> sendDocumentAsync(String to, Media document) {
        final IllegalArgumentException exception = validateDocumentInput(to, document);

        if (exception != null) {
            return CompletableFuture.failedFuture(exception);
        }

        Map<String, Object> data = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "type", "document",
                "document", document);

        return CompletableFuture.supplyAsync(() -> {
            try {
                return whatsAppService.sendRequest(data);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    @Override
    public CompletableFuture<WhatsAppResponse> typingIndicator(String messageId) {
        Map<String, Object> data = Map.of(
                "messaging_product", "whatsapp",
                "status", "read",
                "message_id", messageId,
                "typing_indicator", Map.of("type", "text"));

        return CompletableFuture.supplyAsync(() -> {
            try {
                return whatsAppService.sendRequest(data);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    @Override
    public CompletableFuture<WhatsAppResponse> markMessageAsRead(String messageId) {
        Map<String, Object> data = Map.of(
                "messaging_product", "whatsapp",
                "status", "read",
                "message_id", messageId);

        return CompletableFuture.supplyAsync(() -> {
            try {
                return whatsAppService.sendRequest(data);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }
}
