package com.wsclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletionException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;
import com.wsclient.api.services.FacebookAuthService;
import com.wsclient.api.services.FacebookAuthServiceImpl;
import com.wsclient.api.webhook.FBAccessToken;

/**
 * These tests exercise {@link FacebookAuthServiceImpl} against a local
 * {@link HttpServer} (built into the JDK) instead of mocking Apache
 * HttpClient, since the class builds its own {@code CloseableHttpClient}
 * internally rather than delegating to an injectable {@code WhatsAppService}.
 */
public class FacebookAuthServiceImplTest {

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
    void getAppAccessToken_ShouldReturnParsedToken_WhenRequestSucceeds() throws Exception {
        String baseUrl = startServer(200, "{\"access_token\":\"abc123\",\"token_type\":\"bearer\"}");

        FacebookAuthService authService = new FacebookAuthServiceImpl();
        authService.configure(baseUrl);

        FBAccessToken token = authService.getAppAccessToken("client-id", "client-secret").join();

        assertEquals("abc123", token.accessToken());
        assertEquals("bearer", token.tokenType());
    }

    @Test
    void getAppAccessToken_ShouldThrow_WhenApiReturnsError() throws Exception {
        String baseUrl = startServer(400, "{\"error\":{\"message\":\"Invalid client secret\"}}");

        FacebookAuthService authService = new FacebookAuthServiceImpl();
        authService.configure(baseUrl);

        CompletionException exception = assertThrows(CompletionException.class,
                () -> authService.getAppAccessToken("client-id", "wrong-secret").join());

        // Note: unlike the rest of the library, FacebookAuthServiceImpl currently
        // wraps API errors in a generic RuntimeException rather than
        // WhatsAppException. This test documents that existing behavior: the
        // outer CompletionException's cause is itself a CompletionException
        // wrapping the actual RuntimeException with the error details.
        Throwable innerCompletion = exception.getCause();
        assertInstanceOf(CompletionException.class, innerCompletion);
        assertInstanceOf(RuntimeException.class, innerCompletion.getCause());
        assertTrue(innerCompletion.getCause().getMessage().contains("Failed to get access token"));
    }
}
