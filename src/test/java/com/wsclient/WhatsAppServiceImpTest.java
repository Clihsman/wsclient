package com.wsclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.wsclient.cloud.api.services.WhatsAppService;
import com.wsclient.cloud.api.services.WhatsAppServiceImpl;

public class WhatsAppServiceImpTest {

    private WhatsAppService whatsAppService = new WhatsAppServiceImpl();

    @Test
    void testConfigureWhatsAppApi_NullApiUrl() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> whatsAppService.configureWhatsAppApi(null, "123456789", "validToken"));
        assertEquals("WhatsApp API URL cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testConfigureWhatsAppApi_EmptyApiUrl() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> whatsAppService.configureWhatsAppApi("", "123456789", "validToken"));
        assertEquals("WhatsApp API URL cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testConfigureWhatsAppApi_NullPhoneNumberId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> whatsAppService
                .configureWhatsAppApi("https://api.whatsapp.com", null, "validToken"));
        assertEquals("Phone number ID cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testConfigureWhatsAppApi_EmptyPhoneNumberId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> whatsAppService
                .configureWhatsAppApi("https://api.whatsapp.com", "", "validToken"));
        assertEquals("Phone number ID cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testConfigureWhatsAppApi_InvalidPhoneNumberId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> whatsAppService
                .configureWhatsAppApi("https://api.whatsapp.com", "123ABC456", "validToken"));
        assertEquals("Phone number ID must contain only digits.", exception.getMessage());
    }

    @Test
    void testConfigureWhatsAppApi_NullToken() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> whatsAppService
                .configureWhatsAppApi("https://api.whatsapp.com", "123456789", null));
        assertEquals("Token cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testConfigureWhatsAppApi_EmptyToken() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> whatsAppService.configureWhatsAppApi("https://api.whatsapp.com", "123456789", ""));
        assertEquals("Token cannot be null or empty.", exception.getMessage());
    }
}
