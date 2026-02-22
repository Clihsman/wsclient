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
import com.wsclient.api.messages.request.Media;
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
        void testInvalidRecipientNumber() {
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
        void testMessageExceedsMaxLength() {
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
        void testMessageBelowMinLength() {
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

                when(whatsAppService.sendRequest(requestData, "messages")).thenReturn(expectedResponse);

                final WhatsAppResponse actualResponse = whatsAppClient.sendTextAsync(validPhoneNumber, exampleText)
                                .join();

                assertEquals(expectedResponse, actualResponse);
                verify(whatsAppService, times(1)).sendRequest(requestData, "messages");
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
        void testSendInteractiveButton_ShouldReturnResponse_WhenValid()
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

                when(whatsAppService.sendRequest(requestData, "messages")).thenReturn(expectedResponse);

                final WhatsAppResponse actualResponse = whatsAppClient
                                .sendInteractiveAsync(validPhoneNumber, exampleInteractive).join();

                assertEquals(expectedResponse, actualResponse);
                verify(whatsAppService, times(1)).sendRequest(requestData, "messages");
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
        void testSendInteractiveList_ShouldReturnResponse_WhenValid()
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

                when(whatsAppService.sendRequest(requestData, "messages")).thenReturn(expectedResponse);

                final WhatsAppResponse actualResponse = whatsAppClient
                                .sendInteractiveAsync(validPhoneNumber, exampleInteractive).join();

                assertEquals(expectedResponse, actualResponse);
                verify(whatsAppService, times(1)).sendRequest(requestData, "messages");
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

        // ===========================
        // MEDIA
        // ===========================

        @Test
        void sendImageAsync_ShouldReturnResponse_WhenRequestIsSuccessful()
                        throws IOException, InterruptedException, WhatsAppException {

                // Arrange
                final String validTo = "3001111222";
                final Media validImage = Media.builder()
                                .link("https://example.com/image.jpg")
                                .caption("Example caption")
                                .build();

                final WhatsAppResponse expectedResponse = new WhatsAppResponse(validTo, null, null);

                Map<String, Object> expectedData = Map.of(
                                "messaging_product", "whatsapp",
                                "to", validTo,
                                "type", "image",
                                "image", validImage);

                when(whatsAppService.sendRequest(expectedData, "messages")).thenReturn(expectedResponse);

                // Act
                final WhatsAppResponse actualResponse = whatsAppClient.sendImageAsync(validTo, validImage).join();

                // Assert
                assertNotNull(actualResponse);
                assertEquals(expectedResponse, actualResponse);
                verify(whatsAppService, times(1)).sendRequest(expectedData, "messages");
        }

        @Test
        void sendImageAsync_ShouldThrowException_WhenRecipientNumberIsInvalid() {

                // Arrange
                final String invalidTo = "30011A22"; // Contiene letra
                final Media validImage = Media.builder()
                                .link("https://example.com/image.jpg")
                                .build();

                // Act & Assert
                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendImageAsync(invalidTo, validImage).join());

                Throwable cause = exception.getCause();
                assertInstanceOf(IllegalArgumentException.class, cause);
                assertEquals("Invalid recipient number. The 'to' field must contain only digits.", cause.getMessage());
        }

        @Test
        void sendImageAsync_ShouldThrowException_WhenMediaIsNull() {

                // Arrange
                final String validTo = "3001111222";

                // Act & Assert
                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendImageAsync(validTo, null).join());

                Throwable cause = exception.getCause();
                assertInstanceOf(IllegalArgumentException.class, cause);
                assertEquals("Media object cannot be null.", cause.getMessage());
        }

        @Test
        void sendImageAsync_ShouldThrowException_WhenImageLinkIsMissing() {

                // Arrange
                final String validTo = "3001111222";
                final Media invalidImage = Media.builder()
                                .link("") // Vacío
                                .build();

                // Act & Assert
                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendImageAsync(validTo, invalidImage).join());

                Throwable cause = exception.getCause();
                assertInstanceOf(IllegalArgumentException.class, cause);
                assertEquals("Media must have either an 'id' or a 'link' defined.", cause.getMessage());
        }

        @Test
        void sendImageAsync_ShouldWrapException_WhenWhatsAppServiceFails()
                        throws IOException, InterruptedException, WhatsAppException {

                // Arrange
                final String validTo = "3001111222";
                final Media validImage = Media.builder()
                                .link("https://example.com/image.jpg")
                                .build();

                Map<String, Object> expectedData = Map.of(
                                "messaging_product", "whatsapp",
                                "to", validTo,
                                "type", "image",
                                "image", validImage);

                when(whatsAppService.sendRequest(expectedData, "messages"))
                                .thenThrow(new WhatsAppException("Service unavailable", validTo, null, null, validTo));

                // Act & Assert
                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendImageAsync(validTo, validImage).join());

                Throwable cause = exception.getCause();
                assertInstanceOf(WhatsAppException.class, cause);
                assertEquals("Service unavailable", cause.getMessage());
        }

        @Test
        void testSendDocumentAsync_ShouldFail_WhenRecipientIsNull() {
                Media document = Media.builder().id("123").filename("test.pdf").build();

                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendDocumentAsync(null, document).join());

                assertInstanceOf(IllegalArgumentException.class, exception.getCause());
                assertEquals("Recipient number cannot be null.", exception.getCause().getMessage());
        }

        @Test
        void testSendDocumentAsync_ShouldFail_WhenRecipientHasInvalidCharacters() {
                Media document = Media.builder().id("123").filename("file.pdf").build();

                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendDocumentAsync("30011A222", document).join());

                assertInstanceOf(IllegalArgumentException.class, exception.getCause());
                assertEquals("Invalid recipient number. The 'to' field must contain only digits.",
                                exception.getCause().getMessage());
        }

        @Test
        void testSendDocumentAsync_ShouldFail_WhenDocumentIsNull() {
                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendDocumentAsync("573001112233", null).join());

                assertInstanceOf(IllegalArgumentException.class, exception.getCause());
                assertEquals("Media object cannot be null.", exception.getCause().getMessage());
        }

        @Test
        void testSendDocumentAsync_ShouldFail_WhenNoIdOrLinkProvided() {
                Media document = Media.builder().filename("file.pdf").build();

                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendDocumentAsync("573001112233", document).join());

                assertInstanceOf(IllegalArgumentException.class, exception.getCause());
                assertEquals("Media must have either an 'id' or a 'link' defined.",
                                exception.getCause().getMessage());
        }

        @Test
        void testSendDocumentAsync_ShouldFail_WhenLinkIsInvalid() {
                Media document = Media.builder()
                                .link("ftp://invalid-link.com/document.pdf")
                                .filename("doc.pdf")
                                .build();

                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendDocumentAsync("573001112233", document).join());

                assertInstanceOf(IllegalArgumentException.class, exception.getCause());
                assertEquals("Invalid media link. Only HTTP/HTTPS URLs are allowed.",
                                exception.getCause().getMessage());
        }

        @Test
        void testSendDocumentAsync_ShouldFail_WhenFilenameIsMissing() {
                Media document = Media.builder()
                                .id("123456")
                                .build();

                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendDocumentAsync("573001112233", document).join());

                assertInstanceOf(IllegalArgumentException.class, exception.getCause());
                assertEquals("Filename is required for document messages.", exception.getCause().getMessage());
        }

        @Test
        void testSendDocumentAsync_ShouldFail_WhenCaptionTooLong() {
                String longCaption = "A".repeat(1025);
                Media document = Media.builder()
                                .id("123")
                                .filename("file.pdf")
                                .caption(longCaption)
                                .build();

                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendDocumentAsync("573001112233", document).join());

                assertInstanceOf(IllegalArgumentException.class, exception.getCause());
                assertEquals("Caption exceeds maximum length of 1024 characters.", exception.getCause().getMessage());
        }

        @Test
        void testSendDocumentAsync_ShouldReturnResponse_WhenRequestIsSuccessful()
                        throws IOException, InterruptedException, WhatsAppException {
                final String validTo = "573001112233";

                Media document = Media.builder()
                                .link("https://example.com/doc.pdf")
                                .filename("doc.pdf")
                                .caption("Example Document")
                                .build();

                WhatsAppResponse expectedResponse = new WhatsAppResponse(validTo, null, null);

                Map<String, Object> requestData = Map.of(
                                "messaging_product", "whatsapp",
                                "to", validTo,
                                "type", "document",
                                "document", document);

                when(whatsAppService.sendRequest(requestData, "messages")).thenReturn(expectedResponse);

                WhatsAppResponse actualResponse = whatsAppClient.sendDocumentAsync(validTo, document).join();

                assertEquals(expectedResponse, actualResponse);
                verify(whatsAppService, times(1)).sendRequest(requestData, "messages");
        }

        @Test
        void sendVideoAsync_ShouldFail_WhenRecipientIsNull() {
                Media video = Media.builder().link("https://example.com/video.mp4").build();

                CompletionException ex = assertThrows(CompletionException.class,
                                () -> whatsAppClient.sendVideoAsync(null, video).join());

                assertTrue(ex.getCause() instanceof IllegalArgumentException);
                assertEquals("Recipient number cannot be null.", ex.getCause().getMessage());
        }

        @Test
        void sendVideoAsync_ShouldFail_WhenRecipientIsInvalid() {
                Media video = Media.builder().link("https://example.com/video.mp4").build();

                CompletionException ex = assertThrows(CompletionException.class,
                                () -> whatsAppClient.sendVideoAsync("30011A223", video).join());

                assertTrue(ex.getCause() instanceof IllegalArgumentException);
                assertEquals("Invalid recipient number. The 'to' field must contain only digits.",
                                ex.getCause().getMessage());
        }

        @Test
        void sendVideoAsync_ShouldFail_WhenMediaIsNull() {
                CompletionException ex = assertThrows(CompletionException.class,
                                () -> whatsAppClient.sendVideoAsync("573001112233", null).join());

                assertTrue(ex.getCause() instanceof IllegalArgumentException);
                assertEquals("Media object cannot be null.", ex.getCause().getMessage());
        }

        @Test
        void sendVideoAsync_ShouldFail_WhenNoIdOrLinkProvided() {
                Media video = Media.builder().build();

                CompletionException ex = assertThrows(CompletionException.class,
                                () -> whatsAppClient.sendVideoAsync("573001112233", video).join());

                assertTrue(ex.getCause() instanceof IllegalArgumentException);
                assertEquals("Media must have either an 'id' or a 'link' defined.", ex.getCause().getMessage());
        }

        @Test
        void sendVideoAsync_ShouldFail_WhenLinkIsInvalid() {
                Media video = Media.builder().link("ftp://example.com/file.mp4").build();

                CompletionException ex = assertThrows(CompletionException.class,
                                () -> whatsAppClient.sendVideoAsync("573001112233", video).join());

                assertTrue(ex.getCause() instanceof IllegalArgumentException);
                assertEquals("Invalid media link. Only HTTP/HTTPS URLs are allowed.", ex.getCause().getMessage());
        }

        @Test
        void sendVideoAsync_ShouldFail_WhenCaptionTooLong() {
                Media video = Media.builder()
                                .link("https://example.com/video.mp4")
                                .caption("A".repeat(1025))
                                .build();

                CompletionException ex = assertThrows(CompletionException.class,
                                () -> whatsAppClient.sendVideoAsync("573001112233", video).join());

                assertTrue(ex.getCause() instanceof IllegalArgumentException);
                assertEquals("Caption exceeds maximum length of 1024 characters.", ex.getCause().getMessage());
        }

        @Test
        void sendVideoAsync_ShouldFail_WhenFilenameProvided() {
                Media video = Media.builder()
                                .link("https://example.com/video.mp4")
                                .filename("video.mp4")
                                .build();

                CompletionException ex = assertThrows(CompletionException.class,
                                () -> whatsAppClient.sendVideoAsync("573001112233", video).join());

                assertTrue(ex.getCause() instanceof IllegalArgumentException);
                assertEquals("Filename is not allowed for video messages.", ex.getCause().getMessage());
        }

        @Test
        void sendVideoAsync_ShouldReturnResponse_WhenRequestIsSuccessful()
                        throws Exception {
                final String validTo = "573001112233";
                final Media video = Media.builder()
                                .link("https://example.com/video.mp4")
                                .caption("Sample video caption")
                                .build();

                final WhatsAppResponse expectedResponse = new WhatsAppResponse(validTo, null, null);

                Map<String, Object> expectedData = Map.of(
                                "messaging_product", "whatsapp",
                                "to", validTo,
                                "type", "video",
                                "video", video);

                when(whatsAppService.sendRequest(expectedData, "messages")).thenReturn(expectedResponse);

                WhatsAppResponse actualResponse = whatsAppClient.sendVideoAsync(validTo, video).join();

                assertEquals(expectedResponse, actualResponse);
                verify(whatsAppService, times(1)).sendRequest(expectedData, "messages");
        }

        @Test
        void sendAudioAsync_ShouldFail_WhenRecipientIsNull() {
                Media video = Media.builder().link("https://example.com/audio.mp3").build();

                CompletionException ex = assertThrows(CompletionException.class,
                                () -> whatsAppClient.sendAudioAsync(null, video).join());

                assertTrue(ex.getCause() instanceof IllegalArgumentException);
                assertEquals("Recipient number cannot be null.", ex.getCause().getMessage());
        }

        @Test
        void sendAudioAsync_ShouldFail_WhenRecipientIsInvalid() {
                Media video = Media.builder().link("https://example.com/audio.mp3").build();

                CompletionException ex = assertThrows(CompletionException.class,
                                () -> whatsAppClient.sendAudioAsync("30011A223", video).join());

                assertTrue(ex.getCause() instanceof IllegalArgumentException);
                assertEquals("Invalid recipient number. The 'to' field must contain only digits.",
                                ex.getCause().getMessage());
        }

        @Test
        void sendAudioAsync_ShouldFail_WhenMediaIsNull() {
                CompletionException ex = assertThrows(CompletionException.class,
                                () -> whatsAppClient.sendAudioAsync("573001112233", null).join());

                assertTrue(ex.getCause() instanceof IllegalArgumentException);
                assertEquals("Media object cannot be null.", ex.getCause().getMessage());
        }

        @Test
        void sendAudioAsync_ShouldFail_WhenNoIdOrLinkProvided() {
                Media video = Media.builder().build();

                CompletionException ex = assertThrows(CompletionException.class,
                                () -> whatsAppClient.sendAudioAsync("573001112233", video).join());

                assertTrue(ex.getCause() instanceof IllegalArgumentException);
                assertEquals("Media must have either an 'id' or a 'link' defined.", ex.getCause().getMessage());
        }

        @Test
        void sendAudioAsync_ShouldFail_WhenLinkIsInvalid() {
                Media video = Media.builder().link("ftp://example.com/audio.mp3").build();

                CompletionException ex = assertThrows(CompletionException.class,
                                () -> whatsAppClient.sendAudioAsync("573001112233", video).join());

                assertTrue(ex.getCause() instanceof IllegalArgumentException);
                assertEquals("Invalid media link. Only HTTP/HTTPS URLs are allowed.", ex.getCause().getMessage());
        }

        @Test
        void sendAudioAsync_ShouldFail_WhenCaptionTooLong() {
                Media video = Media.builder()
                                .link("https://example.com/audio.mp3")
                                .caption("A".repeat(1025))
                                .build();

                CompletionException ex = assertThrows(CompletionException.class,
                                () -> whatsAppClient.sendAudioAsync("573001112233", video).join());

                assertTrue(ex.getCause() instanceof IllegalArgumentException);
                assertEquals("Caption exceeds maximum length of 1024 characters.", ex.getCause().getMessage());
        }

        @Test
        void sendAudioAsync_ShouldFail_WhenFilenameProvided() {
                Media video = Media.builder()
                                .link("https://example.com/audio.mp3")
                                .filename("video.mp4")
                                .build();

                CompletionException ex = assertThrows(CompletionException.class,
                                () -> whatsAppClient.sendAudioAsync("573001112233", video).join());

                assertTrue(ex.getCause() instanceof IllegalArgumentException);
                assertEquals("Filename is not allowed for video messages.", ex.getCause().getMessage());
        }

        @Test
        void sendAudioAsync_ShouldReturnResponse_WhenRequestIsSuccessful()
                        throws Exception {
                final String validTo = "573001112233";
                final Media audio = Media.builder()
                                .link("https://example.com/audio.mp3")
                                .caption("Sample audio caption")
                                .build();

                final WhatsAppResponse expectedResponse = new WhatsAppResponse(validTo, null, null);

                Map<String, Object> expectedData = Map.of(
                                "messaging_product", "whatsapp",
                                "to", validTo,
                                "type", "audio",
                                "audio", audio);

                when(whatsAppService.sendRequest(expectedData, "messages")).thenReturn(expectedResponse);

                WhatsAppResponse actualResponse = whatsAppClient.sendAudioAsync(validTo, audio).join();

                assertEquals(expectedResponse, actualResponse);
                verify(whatsAppService, times(1)).sendRequest(expectedData, "messages");
        }
}