package com.wsclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletionException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;
import com.wsclient.api.services.MetaWebhookService;
import com.wsclient.api.services.MetaWebhookServiceImpl;
import com.wsclient.core.exceptions.WhatsAppException;

/**
 * These tests exercise {@link MetaWebhookServiceImpl} against a local
 * {@link HttpServer} (built into the JDK) instead of mocking Apache
 * HttpClient, since the class builds its own {@code CloseableHttpClient}
 * internally rather than delegating to an injectable {@code WhatsAppService}.
 */
public class MetaWebhookServiceImplTest {

    private HttpServer server;

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    private String startServer(int statusCode, String responseBody) throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/", exchange -> {
            byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();
        return "http://localhost:" + server.getAddress().getPort();
    }

    @Test
    void subscribeApp_ShouldReturnResponseBody_WhenRequestSucceeds() throws Exception {
        String baseUrl = startServer(200, "{\"success\":true}");

        MetaWebhookService webhookService = new MetaWebhookServiceImpl();
        webhookService.configure(baseUrl);

        String response = webhookService.subscribeApp(
                "APP_ID", "token", "https://example.com/callback", "verify-token").join();

        assertEquals("{\"success\":true}", response);
    }

    @Test
    void subscribeApp_ShouldThrowWhatsAppException_WhenApiReturnsError() throws Exception {
        String errorJson = "{\"error\":{\"message\":\"Invalid OAuth access token\","
                + "\"type\":\"OAuthException\",\"code\":190,\"error_subcode\":0,\"fbtrace_id\":\"trace-1\"}}";
        String baseUrl = startServer(400, errorJson);

        MetaWebhookService webhookService = new MetaWebhookServiceImpl();
        webhookService.configure(baseUrl);

        CompletionException exception = assertThrows(CompletionException.class,
                () -> webhookService.subscribeApp(
                        "APP_ID", "token", "https://example.com/callback", "verify-token").join());

        Throwable cause = exception.getCause();
        assertInstanceOf(WhatsAppException.class, cause);
        WhatsAppException whatsAppException = (WhatsAppException) cause;
        assertEquals("Invalid OAuth access token", whatsAppException.getMessage());
        assertEquals("OAuthException", whatsAppException.getType());
        assertEquals(190, whatsAppException.getCode());
        assertEquals("trace-1", whatsAppException.getFbtraceId());
    }
}
