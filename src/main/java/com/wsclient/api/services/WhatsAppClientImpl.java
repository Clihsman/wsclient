package com.wsclient.api.services;

import static com.wsclient.api.validators.WhatsAppInputValidator.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.http.client.methods.HttpGet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;

import com.wsclient.api.business.request.BusinessProfile;
import com.wsclient.api.business.response.BusinessProfileResponse;
import com.wsclient.api.messages.request.Location;
import com.wsclient.api.messages.request.Media;
import com.wsclient.api.messages.request.Reaction;
import com.wsclient.api.messages.request.Template;
import com.wsclient.api.messages.request.Text;
import com.wsclient.api.messages.request.contact.Contact;
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
        return sendTextAsync(to, text, null);
    }

    @Override
    public CompletableFuture<WhatsAppResponse> sendTextAsync(String to, Text text, String replyToMessageId) {
        final IllegalArgumentException exception = validateTextInput(to, text);
        if (exception != null) {
            return CompletableFuture.failedFuture(exception);
        }

        Map<String, Object> data = new java.util.LinkedHashMap<>();
        data.put("messaging_product", "whatsapp");
        // Un Business-Scoped User ID (contacto nuevo, ver isBusinessScopedId) va
        // en 'recipient', nunca en 'to' — mandarlo en 'to' pasa la validacion
        // pero Meta lo rechaza en la entrega con "131026 Message undeliverable"
        // al no poder resolverlo como telefono.
        if (isBusinessScopedId(to)) {
            data.put("recipient", to);
        } else {
            data.put("to", to);
        }
        if (replyToMessageId != null && !replyToMessageId.isBlank()) {
            data.put("context", Map.of("message_id", replyToMessageId));
        }
        data.put("text", text);

        return CompletableFuture.supplyAsync(() -> {
            try {
                return whatsAppService.sendRequest(data, "messages");
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    @Override
    public CompletableFuture<WhatsAppResponse> sendStickerAsync(String to, Media sticker) {
        final IllegalArgumentException exception = validateStickerInput(to, sticker);
        if (exception != null) {
            return CompletableFuture.failedFuture(exception);
        }

        Map<String, Object> data = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "type", "sticker",
                "sticker", sticker);

        return CompletableFuture.supplyAsync(() -> {
            try {
                return whatsAppService.sendRequest(data, "messages");
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    @Override
    public CompletableFuture<WhatsAppResponse> sendLocationAsync(String to, Location location) {
        final IllegalArgumentException exception = validateLocationInput(to, location);
        if (exception != null) {
            return CompletableFuture.failedFuture(exception);
        }

        Map<String, Object> data = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "type", "location",
                "location", location);

        return CompletableFuture.supplyAsync(() -> {
            try {
                return whatsAppService.sendRequest(data, "messages");
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    @Override
    public CompletableFuture<WhatsAppResponse> sendReactionAsync(String to, Reaction reaction) {
        final IllegalArgumentException exception = validateReactionInput(to, reaction);
        if (exception != null) {
            return CompletableFuture.failedFuture(exception);
        }

        Map<String, Object> data = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "type", "reaction",
                "reaction", reaction);

        return CompletableFuture.supplyAsync(() -> {
            try {
                return whatsAppService.sendRequest(data, "messages");
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    @Override
    public CompletableFuture<WhatsAppResponse> sendContactsAsync(String to, List<Contact> contacts) {
        final IllegalArgumentException exception = validateContactsInput(to, contacts);
        if (exception != null) {
            return CompletableFuture.failedFuture(exception);
        }

        Map<String, Object> data = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "type", "contacts",
                "contacts", contacts);

        return CompletableFuture.supplyAsync(() -> {
            try {
                return whatsAppService.sendRequest(data, "messages");
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
                return whatsAppService.sendRequest(data, "messages");
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    @Override
    public CompletableFuture<WhatsAppResponse> sendTemplate(String to, Template template) {
        final IllegalArgumentException exception = validateTemplateInput(to, template);
        if (exception != null) {
            return CompletableFuture.failedFuture(exception);
        }

        Map<String, Object> data = Map.of(
                "messaging_product", "whatsapp",
                "recipient_type", "individual",
                "to", to,
                "type", "template",
                "template", template);

        return CompletableFuture.supplyAsync(() -> {
            try {
                return whatsAppService.sendRequest(data, "messages");
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
                return whatsAppService.sendRequest(data, "messages");
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
                return whatsAppService.sendRequest(data, "messages");
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
                return whatsAppService.sendRequest(data, "messages");
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
                return whatsAppService.sendRequest(data, "messages");
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
                return whatsAppService.sendRequest(data, "messages");
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
                return whatsAppService.sendRequest(data, "messages");
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    @Override
    public CompletableFuture<BusinessProfileResponse> getBusinessProfile() {

        final HttpGet httpGet = new HttpGet(
                String.format(
                        "%s/whatsapp_business_profile?fields=%s",
                        whatsAppService.getWhatsappApiUrl(),
                        "about,address,description,email,profile_picture_url,websites,vertical"));

        return CompletableFuture.supplyAsync(() -> {
            try {
                final String json = whatsAppService.sendRequest(httpGet);

                ObjectMapper mapper = new ObjectMapper()
                        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                        .configure(
                                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                                false);

                JsonNode root = mapper.readTree(json);

                JsonNode dataNode = root.path("data");
                if (!dataNode.isArray() || dataNode.isEmpty()) {
                    throw new IllegalStateException("WhatsApp API returned empty data array");
                }

                JsonNode businessProfileNode = dataNode.get(0).path("business_profile");

                if (!businessProfileNode.isObject()) {
                    throw new IllegalStateException("Missing business_profile object in response");
                }

                return mapper.treeToValue(
                        businessProfileNode,
                        BusinessProfileResponse.class);

            } catch (Exception e) {
                throw new CompletionException("Failed to get business profile", e);
            }
        });
    }

    @Override
    public CompletableFuture<WhatsAppResponse> updateBusinessProfile(BusinessProfile profile) {

        if (profile == null) {
            throw new IllegalArgumentException("BusinessProfile must not be null");
        }

        ObjectMapper mapper = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // business_profile object
        ObjectNode businessProfileNode = mapper.valueToTree(profile);
        businessProfileNode.put("messaging_product", "whatsapp");
        businessProfileNode.put("id", whatsAppService.getPhoneNumberId());

        // data[0]
        ObjectNode dataItem = mapper.createObjectNode();
        dataItem.set("business_profile", businessProfileNode);
        dataItem.put("id", whatsAppService.getPhoneNumberId());

        // root
        ObjectNode root = mapper.createObjectNode();
        root.set("data", mapper.createArrayNode().add(dataItem));

        return CompletableFuture.supplyAsync(() -> {
            try {
                return whatsAppService.sendRequest(root, "whatsapp_business_profile");
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }
}
