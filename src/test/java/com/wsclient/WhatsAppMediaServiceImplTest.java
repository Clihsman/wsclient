package com.wsclient;

import com.wsclient.api.messages.response.DeleteMediaResponse;
import com.wsclient.api.messages.response.MediaInfoResponse;
import com.wsclient.api.messages.response.MediaResponse;
import com.wsclient.api.services.WhatsAppMediaServiceImpl;
import com.wsclient.api.services.WhatsAppService;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WhatsAppMediaServiceImplTest {

    private WhatsAppService mockService;
    private WhatsAppMediaServiceImpl mediaService;

    @BeforeEach
    void setUp() {
        mockService = mock(WhatsAppService.class);
        mediaService = new WhatsAppMediaServiceImpl(mockService);
        mediaService.configureWhatsAppApi("https://graph.facebook.com/v19.0", "123456789", "fake-token");
    }

    @Test
    void uploadMedia_withValidInputStream_shouldReturnMediaResponse() throws Exception {
        // Arrange
        String expectedResponse = "{\"id\": \"media-id\"}";
        when(mockService.sendRequest(any(HttpPost.class))).thenReturn(expectedResponse);

        ByteArrayInputStream inputStream = new ByteArrayInputStream("dummy-data".getBytes());
        String fileName = "test.jpg";
        String mimeType = "image/jpeg";

        // Act
        MediaResponse result = mediaService.uploadMedia(inputStream, fileName, mimeType).get();

        // Assert
        assertNotNull(result);
        assertEquals("media-id", result.id());
    }

    @Test
    void uploadMedia_withNullInputStream_shouldThrowException() {
        Exception exception = assertThrows(CompletionException.class, () -> {
            InputStream inputStream = null;
            mediaService.uploadMedia(inputStream, "name.jpg", "image/jpeg").join();
        });

        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Media input stream must not be null.", exception.getCause().getMessage());
    }

    @Test
    void uploadMedia_withInvalidFilePath_shouldThrowException() {
        Exception exception = assertThrows(CompletionException.class, () -> {
            mediaService.uploadMedia("invalid/path.jpg", "name.jpg", "image/jpeg").join();
        });

        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertTrue(exception.getCause().getMessage().contains("does not exist"));
    }

    @Test
    void uploadMedia_withValidFilePath_shouldReturnMediaResponse() throws Exception {
        // Arrange
        File tempFile = File.createTempFile("test", ".txt");
        tempFile.deleteOnExit();

        String expectedResponse = "{\"id\": \"media-id\"}";
        when(mockService.sendRequest(any(HttpPost.class))).thenReturn(expectedResponse);

        // Act
        MediaResponse result = mediaService.uploadMedia(tempFile.getAbsolutePath(), "file.txt", "text/plain").get();

        // Assert
        assertNotNull(result);
        assertEquals("media-id", result.id());
    }

    @Test
    void getMedia_withValidMediaId_shouldReturnMediaInfoResponse() throws Exception {
        String expectedResponse = "{\"messaging_product\":\"whatsapp\",\"url\":\"https://example.com/media-id\","
                + "\"mime_type\":\"image/jpeg\",\"sha256\":\"abc123\",\"file_size\":\"1024\",\"id\":\"media-id\"}";
        when(mockService.sendRequest(any(HttpGet.class))).thenReturn(expectedResponse);

        MediaInfoResponse result = mediaService.getMedia("media-id").get();

        assertNotNull(result);
        assertEquals("media-id", result.id());
        assertEquals("https://example.com/media-id", result.url());
        assertEquals("image/jpeg", result.mimeType());
    }

    @Test
    void getMedia_withNullMediaId_shouldThrowException() {
        Exception exception = assertThrows(CompletionException.class, () -> mediaService.getMedia(null).join());

        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Media ID must not be null or blank.", exception.getCause().getMessage());
    }

    @Test
    void deleteMedia_withValidMediaId_shouldReturnSuccess() throws Exception {
        when(mockService.sendRequest(any(HttpDelete.class))).thenReturn("{\"success\":true}");

        DeleteMediaResponse result = mediaService.deleteMedia("media-id").get();

        assertNotNull(result);
        assertTrue(result.success());
    }

    @Test
    void deleteMedia_withBlankMediaId_shouldThrowException() {
        Exception exception = assertThrows(CompletionException.class, () -> mediaService.deleteMedia("  ").join());

        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Media ID must not be null or blank.", exception.getCause().getMessage());
    }
}
