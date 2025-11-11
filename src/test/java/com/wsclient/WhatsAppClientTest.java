package com.wsclient;

import static com.wsclient.api.constants.WhatsAppConstants.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.wsclient.api.messages.factory.InteractiveFactory;
import com.wsclient.api.messages.request.Text;
import com.wsclient.api.messages.request.interactive.Interactive;
import com.wsclient.api.messages.response.WhatsAppResponse;
import com.wsclient.api.services.WhatsAppClient;
import com.wsclient.api.services.WhatsAppClientImpl;
import com.wsclient.api.services.WhatsAppService;
import com.wsclient.core.exceptions.WhatsAppException;

public class WhatsAppClientTest {

        @Mock
        private WhatsAppService whatsAppService;
        private WhatsAppClient whatsAppClient;
 
        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                whatsAppClient = new WhatsAppClientImpl(whatsAppService);
        }

        // ===========================
        // TEXT MESSAGE VALIDATIONS
        // ===========================

        @Test
        public void testInvalidRecipientNumber() {
                final String invalidTo = "3001111A";
                final Text exampleText = Text.builder().body("Example Body").build();

                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendTextAsync(invalidTo, exampleText).join());

                Throwable cause = exception.getCause();
                assertNotNull(cause);
                assertInstanceOf(IllegalArgumentException.class, cause);
                assertEquals("Invalid recipient number. The 'to' field must contain only digits.", cause.getMessage());
        }

        @Test
        public void testMessageExceedsMaxLength() {
                final String validTo = "3001111222";
                final String exampleBody = "A".repeat(4097);
                final Text exampleText = Text.builder().body(exampleBody).build();

                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendTextAsync(validTo, exampleText).join());

                Throwable cause = exception.getCause();
                assertNotNull(cause);
                assertInstanceOf(IllegalArgumentException.class, cause);
                assertEquals(
                                String.format("Message exceeds max length of %d characters.", MESSAGE_MAX_TEXT),
                                cause.getMessage());
        }

        @Test
        public void testMessageBelowMinLength() {
                final String validTo = "3001111222";
                final Text exampleText = Text.builder().body("").build();

                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendTextAsync(validTo, exampleText).join());

                Throwable cause = exception.getCause();
                assertNotNull(cause);
                assertInstanceOf(IllegalArgumentException.class, cause);
                assertEquals(
                                String.format("Message is too short. Minimum length allowed is %d characters.",
                                                MESSAGE_MIN_TEXT),
                                cause.getMessage());
        }

        @Test
        void sendMessageAsync_ShouldReturnResponse_WhenRequestIsSuccessful()
                        throws IOException, InterruptedException, WhatsAppException {
                final String validPhoneNumber = "3001111222";
                final Text exampleText = Text.builder().body("Example Body").build();
                final WhatsAppResponse expectedResponse = new WhatsAppResponse(validPhoneNumber, null, null);

                Map<String, Object> requestData = Map.of(
                                "messaging_product", "whatsapp",
                                "to", validPhoneNumber,
                                "text", exampleText);

                when(whatsAppService.sendRequest(requestData)).thenReturn(expectedResponse);

                final WhatsAppResponse actualResponse = whatsAppClient.sendTextAsync(validPhoneNumber, exampleText)
                                .join();

                assertEquals(expectedResponse, actualResponse);
                verify(whatsAppService, times(1)).sendRequest(requestData);
        }

        // ===========================
        // INTERACTIVE MESSAGES (BUTTONS)
        // ===========================

        @Test
        void testInteractiveButton_MissingText_ShouldThrow() {
                final String validTo = "3001111222";
                final Interactive invalidInteractive = InteractiveFactory.createButton()
                                .button("1", "Option 1")
                                .build();

                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendInteractiveAsync(validTo, invalidInteractive).join());

                Throwable cause = exception.getCause();
                assertInstanceOf(IllegalArgumentException.class, cause);
                assertEquals("Interactive body text is required and cannot be empty.", cause.getMessage());
        }

        @Test
        void testInteractiveButton_NoButtons_ShouldThrow() {
                final String validTo = "3001111222";
                final Interactive invalidInteractive = InteractiveFactory.createButton()
                                .text("Confirm your choice")
                                .build();

                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendInteractiveAsync(validTo, invalidInteractive).join());

                Throwable cause = exception.getCause();
                assertInstanceOf(IllegalArgumentException.class, cause);
                assertEquals("Button interactive must contain at least one button.", cause.getMessage());
        }

        @Test
        void testInteractiveButton_DuplicateIds_ShouldThrow() {
                final String validTo = "3001111222";
                final Interactive duplicateButtons = InteractiveFactory.createButton()
                                .text("Choose one")
                                .button("1", "Option A")
                                .button("1", "Option B") // Duplicate ID
                                .build();

                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendInteractiveAsync(validTo, duplicateButtons).join());

                Throwable cause = exception.getCause();
                assertInstanceOf(IllegalArgumentException.class, cause);
                assertTrue(cause.getMessage().contains("Duplicate button ID"));
        }

        @Test
        void sendInteractiveButton_ShouldReturnResponse_WhenValid()
                        throws IOException, InterruptedException, WhatsAppException {
                final String validPhoneNumber = "3001111222";

                final Interactive exampleInteractive = InteractiveFactory.createButton()
                                .text("Example Text")
                                .button("1", "Yes")
                                .button("2", "No")
                                .build();

                final WhatsAppResponse expectedResponse = new WhatsAppResponse(validPhoneNumber, null, null);

                Map<String, Object> requestData = Map.of(
                                "messaging_product", "whatsapp",
                                "to", validPhoneNumber,
                                "type", "interactive",
                                "interactive", exampleInteractive);

                when(whatsAppService.sendRequest(requestData)).thenReturn(expectedResponse);

                final WhatsAppResponse actualResponse = whatsAppClient
                                .sendInteractiveAsync(validPhoneNumber, exampleInteractive).join();

                assertEquals(expectedResponse, actualResponse);
                verify(whatsAppService, times(1)).sendRequest(requestData);
        }

        // ===========================
        // INTERACTIVE MESSAGES (LISTS)
        // ===========================

        @Test
        void testInteractiveList_MissingButtonTitle_ShouldThrow() {
                final String validTo = "3001111222";
                final Interactive invalidList = InteractiveFactory.createList()
                                .text("Choose an option:")
                                .section("Main")
                                .row("1", "Option A")
                                .build(); // Missing listButton()

                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendInteractiveAsync(validTo, invalidList).join());

                Throwable cause = exception.getCause();
                assertInstanceOf(IllegalArgumentException.class, cause);
                assertEquals("List interactive must have a list button title.", cause.getMessage());
        }

        @Test
        void testInteractiveList_NoSections_ShouldThrow() {
                final String validTo = "3001111222";
                final Interactive invalidList = InteractiveFactory.createList()
                                .text("Choose an option:")
                                .listButton("Options")
                                .build();

                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendInteractiveAsync(validTo, invalidList).join());

                Throwable cause = exception.getCause();
                assertInstanceOf(IllegalArgumentException.class, cause);
                assertEquals("List interactive must contain at least one section.", cause.getMessage());
        }

        @Test
        void testInteractiveList_DuplicateRowIds_ShouldThrow() {
                final String validTo = "3001111222";
                final Interactive invalidList = InteractiveFactory.createList()
                                .text("Choose an option:")
                                .listButton("Options")
                                .section("Main")
                                .row("1", "Option A")
                                .row("1", "Option B") // Duplicate ID
                                .build();

                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendInteractiveAsync(validTo, invalidList).join());

                Throwable cause = exception.getCause();
                assertInstanceOf(IllegalArgumentException.class, cause);
                assertTrue(cause.getMessage().contains("Duplicate row ID"));
        }

        @Test
        void sendInteractiveList_ShouldReturnResponse_WhenValid()
                        throws IOException, InterruptedException, WhatsAppException {
                final String validPhoneNumber = "3001111222";

                final Interactive exampleInteractive = InteractiveFactory.createList()
                                .text("Choose an option:")
                                .listButton("View options")
                                .section("Main")
                                .row("1", "Option 1", "Desc 1")
                                .row("2", "Option 2")
                                .build();

                final WhatsAppResponse expectedResponse = new WhatsAppResponse(validPhoneNumber, null, null);

                Map<String, Object> requestData = Map.of(
                                "messaging_product", "whatsapp",
                                "to", validPhoneNumber,
                                "type", "interactive",
                                "interactive", exampleInteractive);

                when(whatsAppService.sendRequest(requestData)).thenReturn(expectedResponse);

                final WhatsAppResponse actualResponse = whatsAppClient
                                .sendInteractiveAsync(validPhoneNumber, exampleInteractive).join();

                assertEquals(expectedResponse, actualResponse);
                verify(whatsAppService, times(1)).sendRequest(requestData);
        }

        // ===========================
        // CONFIGURATION
        // ===========================

        @Test
        void testConfigureWhatsAppApi_ValidInputs() {
                assertDoesNotThrow(() -> whatsAppClient.configureWhatsAppApi("https://api.whatsapp.com", "123456789",
                                "validToken"));
        }

        @Test
        void testConfigureWhatsAppApi_InvalidInputs_ShouldThrow() {
                assertThrows(IllegalArgumentException.class,
                                () -> whatsAppClient.configureWhatsAppApi("", "123456789", "validToken"));

                assertThrows(IllegalArgumentException.class, () -> whatsAppClient
                                .configureWhatsAppApi("https://api.whatsapp.com", "", "validToken"));

                assertThrows(IllegalArgumentException.class,
                                () -> whatsAppClient.configureWhatsAppApi("https://api.whatsapp.com", "123456789", ""));
        }
}