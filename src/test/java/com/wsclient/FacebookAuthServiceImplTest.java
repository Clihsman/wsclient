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
import com.wsclient.api.services.FacebookAuthService;
import com.wsclient.api.services.FacebookAuthServiceImpl;
import com.wsclient.api.webhook.FBAccessToken;
import com.wsclient.core.exceptions.WhatsAppException;

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
    void getAppAccessToken_ShouldThrowWhatsAppException_WhenApiReturnsError() throws Exception {
        String errorJson = "{\"error\":{\"message\":\"Invalid client secret\",\"type\":\"OAuthException\","
                + "\"code\":190,\"error_subcode\":0,\"fbtrace_id\":\"trace-2\"}}";
        String baseUrl = startServer(400, errorJson);

        FacebookAuthService authService = new FacebookAuthServiceImpl();
        authService.configure(baseUrl);

        CompletionException exception = assertThrows(CompletionException.class,
                () -> authService.getAppAccessToken("client-id", "wrong-secret").join());

        Throwable cause = exception.getCause();
        assertInstanceOf(WhatsAppException.class, cause);
        WhatsAppException whatsAppException = (WhatsAppException) cause;
        assertEquals("Invalid client secret", whatsAppException.getMessage());
        assertEquals("OAuthException", whatsAppException.getType());
        assertEquals(190, whatsAppException.getCode());
        assertEquals("trace-2", whatsAppException.getFbtraceId());
    }

    @Test
    void getAppAccessToken_ShouldThrowWhatsAppException_WhenErrorResponseIsNotJson() throws Exception {
        String baseUrl = startServer(500, "Internal Server Error");

        FacebookAuthService authService = new FacebookAuthServiceImpl();
        authService.configure(baseUrl);

        CompletionException exception = assertThrows(CompletionException.class,
                () -> authService.getAppAccessToken("client-id", "wrong-secret").join());

        assertInstanceOf(WhatsAppException.class, exception.getCause());
    }

    @Test
    void exchangeCodeForUserToken_ShouldReturnParsedToken_WhenRequestSucceeds() throws Exception {
        String baseUrl = startServer(200, "{\"access_token\":\"user-token\",\"token_type\":\"bearer\"}");

        FacebookAuthService authService = new FacebookAuthServiceImpl();
        authService.configure(baseUrl);

        FBAccessToken token = authService.exchangeCodeForUserToken(
                "client-id", "client-secret", "https://example.com/callback", "auth-code").join();

        assertEquals("user-token", token.accessToken());
        assertEquals("bearer", token.tokenType());
    }

    @Test
    void getLongLivedToken_ShouldReturnParsedTokenWithExpiry_WhenRequestSucceeds() throws Exception {
        String baseUrl = startServer(200,
                "{\"access_token\":\"long-lived-token\",\"token_type\":\"bearer\",\"expires_in\":5184000}");

        FacebookAuthService authService = new FacebookAuthServiceImpl();
        authService.configure(baseUrl);

        FBAccessToken token = authService.getLongLivedToken("client-id", "client-secret", "short-lived-token")
                .join();

        assertEquals("long-lived-token", token.accessToken());
        assertEquals(5184000L, token.expiresIn());
    }

    @Test
    void getLongLivedToken_ShouldThrowWhatsAppException_WhenApiReturnsError() throws Exception {
        String errorJson = "{\"error\":{\"message\":\"Invalid token\",\"type\":\"OAuthException\","
                + "\"code\":190,\"error_subcode\":0,\"fbtrace_id\":\"trace-4\"}}";
        String baseUrl = startServer(400, errorJson);

        FacebookAuthService authService = new FacebookAuthServiceImpl();
        authService.configure(baseUrl);

        CompletionException exception = assertThrows(CompletionException.class,
                () -> authService.getLongLivedToken("client-id", "client-secret", "expired-token").join());

        assertInstanceOf(WhatsAppException.class, exception.getCause());
    }
}
