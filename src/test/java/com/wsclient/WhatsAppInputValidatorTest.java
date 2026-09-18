package com.wsclient;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.wsclient.api.messages.request.Text;
import com.wsclient.api.validators.WhatsAppInputValidator;

class WhatsAppInputValidatorTest {

    @Test
    @DisplayName("validateTextInput - accepts a plain-digit phone number")
    void validateTextInput_acceptsDigitOnlyRecipient() {
        assertNull(WhatsAppInputValidator.validateTextInput("573001112233",
                Text.builder().body("hola").build()));
    }

    @Test
    @DisplayName("validateTextInput - accepts a business-scoped id (contacto nuevo, coexistencia)")
    void validateTextInput_acceptsBusinessScopedId() {
        assertNull(WhatsAppInputValidator.validateTextInput("CO.27917137097968166",
                Text.builder().body("hola").build()));
    }

    @Test
    @DisplayName("validateTextInput - rejects garbage that isn't a phone number or a business-scoped id")
    void validateTextInput_rejectsGarbageRecipient() {
        assertNotNull(WhatsAppInputValidator.validateTextInput("not-a-recipient",
                Text.builder().body("hola").build()));
    }
}
