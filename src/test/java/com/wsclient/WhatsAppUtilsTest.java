package com.wsclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;

import com.wsclient.common.utils.WhatsAppUtils;

public class WhatsAppUtilsTest {
    @Test
    void testExtractMessageId_ValidId() {
        String base64Encoded = Base64.getEncoder().encodeToString("12345".getBytes());
        String validMessageId = "wamid." + base64Encoded;

        String extracted = WhatsAppUtils.extractMessageId(validMessageId);
        assertEquals("12345", extracted);
    }

    @Test
    void testExtractMessageId_InvalidFormat() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> WhatsAppUtils.extractMessageId("invalid_format"));
        assertEquals("Invalid message ID format. Expected format: wamid.<base64-encoded-id>", exception.getMessage());
    }

    @Test
    void testExtractMessageId_InvalidBase64() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> WhatsAppUtils.extractMessageId("wamid.!@#$$%"));
        assertEquals("Failed to decode base64 message ID.", exception.getMessage());
    }

    private static String computeSignature(String payload, String appSecret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return "sha256=" + HexFormat.of().formatHex(digest);
    }

    @Test
    void testVerifyWebhookSignature_ValidSignature() throws Exception {
        String payload = "{\"field\":\"value\"}";
        String appSecret = "my-app-secret";
        String signature = computeSignature(payload, appSecret);

        assertTrue(WhatsAppUtils.verifyWebhookSignature(payload, signature, appSecret));
    }

    @Test
    void testVerifyWebhookSignature_InvalidSignature() {
        String payload = "{\"field\":\"value\"}";
        String appSecret = "my-app-secret";

        assertFalse(WhatsAppUtils.verifyWebhookSignature(payload, "sha256=" + "0".repeat(64), appSecret));
    }

    @Test
    void testVerifyWebhookSignature_MissingPrefix() {
        String payload = "{\"field\":\"value\"}";
        String appSecret = "my-app-secret";

        assertFalse(WhatsAppUtils.verifyWebhookSignature(payload, "0".repeat(64), appSecret));
    }

    @Test
    void testVerifyWebhookSignature_NullSignatureHeader() {
        assertFalse(WhatsAppUtils.verifyWebhookSignature("{}", null, "my-app-secret"));
    }

    @Test
    void testVerifyWebhookSignature_NullPayload_ShouldThrow() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> WhatsAppUtils.verifyWebhookSignature(null, "sha256=abc", "my-app-secret"));
        assertEquals("Payload and app secret cannot be null.", exception.getMessage());
    }

    @Test
    void testVerifyWebhookSignature_NullAppSecret_ShouldThrow() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> WhatsAppUtils.verifyWebhookSignature("{}", "sha256=abc", null));
        assertEquals("Payload and app secret cannot be null.", exception.getMessage());
    }
}
