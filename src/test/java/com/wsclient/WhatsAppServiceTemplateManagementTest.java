package com.wsclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletionException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;
import com.wsclient.api.services.WhatsAppService;
import com.wsclient.api.services.WhatsAppServiceImpl;
import com.wsclient.api.templates.request.CreateTemplateRequest;
import com.wsclient.api.templates.request.EditTemplateRequest;
import com.wsclient.api.templates.request.TemplateDefinitionComponent;
import com.wsclient.api.templates.response.CreateTemplateResponse;
import com.wsclient.api.templates.response.TemplateActionResponse;
import com.wsclient.core.exceptions.WhatsAppException;

/**
 * These tests exercise {@link WhatsAppServiceImpl}'s template management
 * methods (create/edit/delete) against a local {@link HttpServer} (built
 * into the JDK) rather than mocking Apache HttpClient, since these methods
 * build their own {@code CloseableHttpClient} via {@code sendRequest}
 * internally.
 */
public class WhatsAppServiceTemplateManagementTest {

    private HttpServer server;
    private WhatsAppService service;

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
        String baseUrl = "http://localhost:" + server.getAddress().getPort();

        service = new WhatsAppServiceImpl();
        service.configureWhatsAppApi(baseUrl, "123456789", "waba-id", "fake-token");
        return baseUrl;
    }

    @Test
    void createTemplate_ShouldReturnParsedResponse_WhenSuccessful() throws Exception {
        startServer(200, "{\"id\":\"template-id\",\"status\":\"PENDING\",\"category\":\"UTILITY\"}");

        CreateTemplateRequest request = CreateTemplateRequest.builder()
                .name("order_confirmation")
                .category("UTILITY")
                .language("en_US")
                .components(List.of(TemplateDefinitionComponent.builder()
                        .type("BODY")
                        .text("Your order {{1}} is confirmed.")
                        .build()))
                .build();

        CreateTemplateResponse response = service.createTemplate(request).join();

        assertEquals("template-id", response.id());
        assertEquals("PENDING", response.status());
        assertEquals("UTILITY", response.category());
    }

    @Test
    void createTemplate_ShouldThrowWhatsAppException_WhenApiReturnsError() throws Exception {
        String errorJson = "{\"error\":{\"message\":\"Template name already exists\",\"type\":\"OAuthException\","
                + "\"code\":100,\"error_subcode\":0,\"fbtrace_id\":\"trace-3\"}}";
        startServer(400, errorJson);

        CreateTemplateRequest request = CreateTemplateRequest.builder()
                .name("order_confirmation")
                .category("UTILITY")
                .language("en_US")
                .components(List.of())
                .build();

        CompletionException exception = assertThrows(CompletionException.class,
                () -> service.createTemplate(request).join());

        assertInstanceOf(WhatsAppException.class, exception.getCause());
        assertEquals("Template name already exists", exception.getCause().getMessage());
    }

    @Test
    void editTemplate_ShouldReturnSuccess_WhenSuccessful() throws Exception {
        startServer(200, "{\"success\":true}");

        EditTemplateRequest request = EditTemplateRequest.builder()
                .category("MARKETING")
                .build();

        TemplateActionResponse response = service.editTemplate("template-id", request).join();

        assertTrue(response.success());
    }

    @Test
    void deleteTemplate_ShouldReturnSuccess_WhenSuccessful() throws Exception {
        startServer(200, "{\"success\":true}");

        TemplateActionResponse response = service.deleteTemplate("order_confirmation").join();

        assertTrue(response.success());
    }
}
