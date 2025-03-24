package com.wsclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Base64;

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
}
