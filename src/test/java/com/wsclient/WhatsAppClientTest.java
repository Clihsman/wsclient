package com.wsclient;

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
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.wsclient.cloud.api.exceptions.WhatsAppException;
import com.wsclient.cloud.api.messages.request.Text;
import com.wsclient.cloud.api.messages.request.interactive.Interactive;
import com.wsclient.cloud.api.messages.request.interactive.InteractiveAction;
import com.wsclient.cloud.api.messages.request.interactive.InteractiveBody;
import com.wsclient.cloud.api.messages.request.interactive.InteractiveButton;
import com.wsclient.cloud.api.messages.request.interactive.InteractiveButtonReply;
import com.wsclient.cloud.api.messages.request.interactive.Interactive.InteractiveType;
import com.wsclient.cloud.api.messages.response.WhatsAppResponse;
import com.wsclient.cloud.api.messages.service.WhatsAppClient;
import com.wsclient.cloud.api.messages.service.WhatsAppClientImpl;
import com.wsclient.cloud.api.messages.service.WhatsAppService;
import static com.wsclient.cloud.api.constants.WhatsAppConstants.*;

public class WhatsAppClientTest {

        @Mock
        private WhatsAppService whatsAppService;
        private WhatsAppClient whatsAppClient;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                whatsAppClient = new WhatsAppClientImpl(whatsAppService);
        }

        @Test
        public void testInvalidRecipientNumber() {
                // Arrange
                final String invalidTo = "3001111A"; // Contiene una letra, lo que lo hace inválido
                final Text exampleText = Text.builder().body("Example Body").build();

                // Act & Assert
                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendMessageAsync(invalidTo, exampleText).join());

                // Verifica que la excepción tenga el mensaje esperado
                Throwable cause = exception.getCause();
                assertNotNull(cause, "Exception cause should not be null");
                assertTrue(cause instanceof WhatsAppException, "Cause should be of type WhatsAppException");
                assertEquals("Invalid recipient number. The 'to' field must contain only digits.", cause.getMessage());
        }

        @Test
        public void testMessageExceedsMaxLength() {
                // Arrange
                final String validTo = "3001111222"; // Un número válido
                final String exampleBody = "A".repeat(1025); // Excede el límite de 1024 caracteres
                final Text exampleText = Text.builder().body(exampleBody).build();

                // Act & Assert
                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendMessageAsync(validTo, exampleText).join());

                // Verifica que la excepción no sea nula
                assertNotNull(exception.getCause(), "Exception cause should not be null");

                // Verifica que la excepción sea del tipo correcto
                Throwable cause = exception.getCause();
                assertTrue(cause instanceof WhatsAppException, "Cause should be of type WhatsAppException");

                // Verifica que el mensaje de error sea el esperado
                assertEquals(
                                String.format("Message exceeds max length of %d characters.",
                                                MESSAGE_MAX_TEXT),
                                cause.getMessage(),
                                "Error message should indicate text length limit exceeded");
        }

        @Test
        public void testMessageBelowMinLength() {
                // Arrange
                final String validTo = "3001111222"; // Un número válido
                final String exampleBody = ""; // Un mensaje vacío que viola la restricción de longitud mínima
                final Text exampleText = Text.builder().body(exampleBody).build();

                // Act & Assert
                CompletionException exception = assertThrows(
                                CompletionException.class,
                                () -> whatsAppClient.sendMessageAsync(validTo, exampleText).join());

                // Verifica que la excepción no sea nula
                Throwable cause = exception.getCause();
                assertNotNull(cause, "Exception cause should not be null");

                // Verifica que la excepción sea del tipo correcto
                assertInstanceOf(WhatsAppException.class, cause, "Cause should be of type WhatsAppException");

                // Verifica que el mensaje de error sea el esperado
                final String expectedMessage = String.format(
                                "Message is too short. Minimum length allowed is %d characters.",
                                MESSAGE_MIN_TEXT);

                assertEquals(expectedMessage, cause.getMessage(),
                                "Error message should indicate text length limit violated");
        }

        @Test
        void sendMessageAsync_ShouldReturnResponse_WhenRequestIsSuccessful()
                        throws IOException, InterruptedException, WhatsAppException {
                // Arrange: Datos válidos
                final String validPhoneNumber = "3001111222";
                final Text exampleText = Text.builder().body("Example Body").build();
                final WhatsAppResponse expectedResponse = new WhatsAppResponse(validPhoneNumber, null, null);

                Map<String, Object> requestData = Map.of(
                                "messaging_product", "whatsapp",
                                "to", validPhoneNumber,
                                "text", exampleText);

                when(whatsAppService.sendRequest(requestData)).thenReturn(expectedResponse);

                // Act: Llamar al método bajo prueba
                final WhatsAppResponse actualResponse = whatsAppClient.sendMessageAsync(validPhoneNumber, exampleText)
                                .join();

                // Assert: Validar que la respuesta es correcta
                assertEquals(expectedResponse, actualResponse);
                verify(whatsAppService, times(1)).sendRequest(requestData); // Se llamó una vez
        }

        @Test
        void sendMessageAsync_ShouldReturnResponse_WhenRequestIsSuccessful2()
                        throws IOException, InterruptedException, WhatsAppException {
                // Arrange: Datos válidos
                final String validPhoneNumber = "3001111222";

                final Interactive exampleInteractive = Interactive
                                .builder()
                                .type(InteractiveType.BUTTON)
                                .action(
                                                InteractiveAction
                                                                .builder()
                                                                .buttons(List.of(
                                                                                InteractiveButton.builder()
                                                                                                .reply(InteractiveButtonReply
                                                                                                                .builder()
                                                                                                                .id("1")
                                                                                                                .title("bt1")
                                                                                                                .build())
                                                                                                .build()))
                                                                .build())
                                .body(
                                                InteractiveBody
                                                                .builder()
                                                                .text("Example Text")
                                                                .build())
                                .build();

                final WhatsAppResponse expectedResponse = new WhatsAppResponse(validPhoneNumber, null, null);

                Map<String, Object> requestData = Map.of(
                                "messaging_product", "whatsapp",
                                "to", validPhoneNumber,
                                "type", "interactive",
                                "interactive", exampleInteractive);

                when(whatsAppService.sendRequest(requestData)).thenReturn(expectedResponse);

                // Act: Llamar al método bajo prueba
                final WhatsAppResponse actualResponse = whatsAppClient
                                .sendInteractiveAsync(validPhoneNumber, exampleInteractive)
                                .join();

                // Assert: Validar que la respuesta es correcta
                assertEquals(expectedResponse, actualResponse);
                verify(whatsAppService, times(1)).sendRequest(requestData); // Se llamó una vez
        }

        @Test
        void testConfigureWhatsAppApi_ValidInputs() {
                assertDoesNotThrow(() -> whatsAppClient.configureWhatsAppApi("https://api.whatsapp.com", "123456789",
                                "validToken"));
        }

}
