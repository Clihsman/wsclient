package com.wsclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletionException;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.wsclient.api.messages.response.PhoneNumberResponse;
import com.wsclient.api.messages.response.PhoneNumbersListResponse;
import com.wsclient.api.messages.response.QrCodeResponse;
import com.wsclient.api.messages.response.QrCodesListResponse;
import com.wsclient.api.messages.response.SuccessResponse;
import com.wsclient.api.services.WhatsAppBusinessManagementService;
import com.wsclient.api.services.WhatsAppBusinessManagementServiceImpl;
import com.wsclient.api.services.WhatsAppService;

public class WhatsAppBusinessManagementServiceImplTest {

    private WhatsAppService mockService;
    private WhatsAppBusinessManagementService managementService;

    @BeforeEach
    void setUp() {
        mockService = mock(WhatsAppService.class);
        managementService = new WhatsAppBusinessManagementServiceImpl(mockService);
        managementService.configure("https://graph.facebook.com/v20.0", "waba-id", "fake-token");
    }

    @Test
    void listPhoneNumbers_shouldReturnParsedResponse() throws Exception {
        when(mockService.sendRequest(any(HttpGet.class)))
                .thenReturn("{\"data\":[{\"id\":\"phone-1\",\"display_phone_number\":\"+15551234567\"}]}");

        PhoneNumbersListResponse response = managementService.listPhoneNumbers().get();

        assertEquals(1, response.data().size());
        assertEquals("phone-1", response.data().get(0).id());
    }

    @Test
    void getPhoneNumber_shouldReturnParsedResponse() throws Exception {
        when(mockService.sendRequest(any(HttpGet.class)))
                .thenReturn("{\"id\":\"phone-1\",\"verified_name\":\"My Business\",\"quality_rating\":\"GREEN\"}");

        PhoneNumberResponse response = managementService.getPhoneNumber("phone-1").get();

        assertEquals("phone-1", response.id());
        assertEquals("My Business", response.verifiedName());
        assertEquals("GREEN", response.qualityRating());
    }

    @Test
    void getPhoneNumber_withNullId_shouldThrow() {
        CompletionException exception = assertThrows(CompletionException.class,
                () -> managementService.getPhoneNumber(null).join());

        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Phone number ID must not be null or blank.", exception.getCause().getMessage());
    }

    @Test
    void registerPhoneNumber_shouldReturnSuccess() throws Exception {
        when(mockService.sendRequest(any(HttpPost.class))).thenReturn("{\"success\":true}");

        SuccessResponse response = managementService.registerPhoneNumber("phone-1", "123456").get();

        assertTrue(response.success());
    }

    @Test
    void requestVerificationCode_shouldReturnSuccess() throws Exception {
        when(mockService.sendRequest(any(HttpPost.class))).thenReturn("{\"success\":true}");

        SuccessResponse response = managementService.requestVerificationCode("phone-1", "SMS", "en_US").get();

        assertTrue(response.success());
    }

    @Test
    void verifyCode_shouldReturnSuccess() throws Exception {
        when(mockService.sendRequest(any(HttpPost.class))).thenReturn("{\"success\":true}");

        SuccessResponse response = managementService.verifyCode("phone-1", "000000").get();

        assertTrue(response.success());
    }

    @Test
    void createQrCode_shouldReturnParsedResponse() throws Exception {
        when(mockService.sendRequest(any(HttpPost.class))).thenReturn(
                "{\"code\":\"qr-1\",\"prefilled_message\":\"Hi!\",\"deep_link_url\":\"https://wa.me/message/qr-1\"}");

        QrCodeResponse response = managementService.createQrCode("phone-1", "Hi!").get();

        assertNotNull(response);
        assertEquals("qr-1", response.code());
        assertEquals("Hi!", response.prefilledMessage());
    }

    @Test
    void listQrCodes_shouldReturnParsedResponse() throws Exception {
        when(mockService.sendRequest(any(HttpGet.class)))
                .thenReturn("{\"data\":[{\"code\":\"qr-1\"},{\"code\":\"qr-2\"}]}");

        QrCodesListResponse response = managementService.listQrCodes("phone-1").get();

        assertEquals(2, response.data().size());
    }

    @Test
    void getQrCode_shouldReturnParsedResponse() throws Exception {
        when(mockService.sendRequest(any(HttpGet.class))).thenReturn("{\"code\":\"qr-1\",\"prefilled_message\":\"Hi!\"}");

        QrCodeResponse response = managementService.getQrCode("phone-1", "qr-1").get();

        assertEquals("qr-1", response.code());
    }

    @Test
    void updateQrCode_shouldReturnParsedResponse() throws Exception {
        when(mockService.sendRequest(any(HttpPost.class)))
                .thenReturn("{\"code\":\"qr-1\",\"prefilled_message\":\"Updated\"}");

        QrCodeResponse response = managementService.updateQrCode("phone-1", "qr-1", "Updated").get();

        assertEquals("Updated", response.prefilledMessage());
    }

    @Test
    void deleteQrCode_shouldReturnSuccess() throws Exception {
        when(mockService.sendRequest(any(HttpDelete.class))).thenReturn("{\"success\":true}");

        SuccessResponse response = managementService.deleteQrCode("phone-1", "qr-1").get();

        assertTrue(response.success());
    }

    @Test
    void deleteQrCode_withBlankQrCodeId_shouldThrow() {
        CompletionException exception = assertThrows(CompletionException.class,
                () -> managementService.deleteQrCode("phone-1", "  ").join());

        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("QR code ID must not be null or blank.", exception.getCause().getMessage());
    }
}
