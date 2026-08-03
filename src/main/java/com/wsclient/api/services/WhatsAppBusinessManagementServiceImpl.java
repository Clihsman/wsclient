package com.wsclient.api.services;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsclient.api.messages.response.PhoneNumberResponse;
import com.wsclient.api.messages.response.PhoneNumbersListResponse;
import com.wsclient.api.messages.response.QrCodeResponse;
import com.wsclient.api.messages.response.QrCodesListResponse;
import com.wsclient.api.messages.response.SuccessResponse;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link WhatsAppBusinessManagementService}.
 * <p>
 * Delegates HTTP execution and error handling to the injected
 * {@link WhatsAppService} (its {@code sendRequest(HttpGet/HttpPost/HttpDelete)}
 * overloads), the same way {@link WhatsAppMediaServiceImpl} does, since these
 * endpoints operate on phone number/WABA IDs rather than the single
 * {@code phoneNumberId} the generic {@code sendRequest(Map, path)} overload
 * is tied to.
 * </p>
 */
@RequiredArgsConstructor
public class WhatsAppBusinessManagementServiceImpl implements WhatsAppBusinessManagementService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private String whatsappApiUrl;
    private String businessAccount;
    private String token;

    private final WhatsAppService whatsAppService;

    @Override
    public void configure(String whatsappApiUrl, String businessAccount, String token) {
        this.whatsappApiUrl = whatsappApiUrl;
        this.businessAccount = businessAccount;
        this.token = token;
    }

    @Override
    public CompletableFuture<PhoneNumbersListResponse> listPhoneNumbers() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpGet httpGet = new HttpGet(String.format("%s/%s/phone_numbers", whatsappApiUrl, businessAccount));
                httpGet.addHeader("Authorization", String.format("Bearer %s", token));

                String responseBody = whatsAppService.sendRequest(httpGet);
                return OBJECT_MAPPER.readValue(responseBody, PhoneNumbersListResponse.class);
            } catch (Exception e) {
                throw new CompletionException("Failed to list phone numbers", e);
            }
        });
    }

    @Override
    public CompletableFuture<PhoneNumberResponse> getPhoneNumber(String phoneNumberId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateId(phoneNumberId, "Phone number ID");

                HttpGet httpGet = new HttpGet(String.format("%s/%s", whatsappApiUrl, phoneNumberId));
                httpGet.addHeader("Authorization", String.format("Bearer %s", token));

                String responseBody = whatsAppService.sendRequest(httpGet);
                return OBJECT_MAPPER.readValue(responseBody, PhoneNumberResponse.class);
            } catch (Exception e) {
                throw new CompletionException("Failed to get phone number", e);
            }
        });
    }

    @Override
    public CompletableFuture<SuccessResponse> registerPhoneNumber(String phoneNumberId, String pin) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateId(phoneNumberId, "Phone number ID");

                Map<String, Object> body = Map.of(
                        "messaging_product", "whatsapp",
                        "pin", pin);

                String responseBody = post(String.format("%s/%s/register", whatsappApiUrl, phoneNumberId), body);
                return OBJECT_MAPPER.readValue(responseBody, SuccessResponse.class);
            } catch (Exception e) {
                throw new CompletionException("Failed to register phone number", e);
            }
        });
    }

    @Override
    public CompletableFuture<SuccessResponse> requestVerificationCode(String phoneNumberId, String codeMethod,
            String locale) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateId(phoneNumberId, "Phone number ID");

                Map<String, Object> body = Map.of(
                        "code_method", codeMethod,
                        "locale", locale);

                String responseBody = post(String.format("%s/%s/request_code", whatsappApiUrl, phoneNumberId), body);
                return OBJECT_MAPPER.readValue(responseBody, SuccessResponse.class);
            } catch (Exception e) {
                throw new CompletionException("Failed to request verification code", e);
            }
        });
    }

    @Override
    public CompletableFuture<SuccessResponse> verifyCode(String phoneNumberId, String code) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateId(phoneNumberId, "Phone number ID");

                Map<String, Object> body = Map.of("code", code);

                String responseBody = post(String.format("%s/%s/verify_code", whatsappApiUrl, phoneNumberId), body);
                return OBJECT_MAPPER.readValue(responseBody, SuccessResponse.class);
            } catch (Exception e) {
                throw new CompletionException("Failed to verify code", e);
            }
        });
    }

    @Override
    public CompletableFuture<QrCodeResponse> createQrCode(String phoneNumberId, String prefilledMessage) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateId(phoneNumberId, "Phone number ID");

                Map<String, Object> body = Map.of(
                        "prefilled_message", prefilledMessage,
                        "generate_qr_image", "PNG");

                String responseBody = post(String.format("%s/%s/message_qrdls", whatsappApiUrl, phoneNumberId), body);
                return OBJECT_MAPPER.readValue(responseBody, QrCodeResponse.class);
            } catch (Exception e) {
                throw new CompletionException("Failed to create QR code", e);
            }
        });
    }

    @Override
    public CompletableFuture<QrCodesListResponse> listQrCodes(String phoneNumberId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateId(phoneNumberId, "Phone number ID");

                HttpGet httpGet = new HttpGet(
                        String.format("%s/%s/message_qrdls", whatsappApiUrl, phoneNumberId));
                httpGet.addHeader("Authorization", String.format("Bearer %s", token));

                String responseBody = whatsAppService.sendRequest(httpGet);
                return OBJECT_MAPPER.readValue(responseBody, QrCodesListResponse.class);
            } catch (Exception e) {
                throw new CompletionException("Failed to list QR codes", e);
            }
        });
    }

    @Override
    public CompletableFuture<QrCodeResponse> getQrCode(String phoneNumberId, String qrCodeId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateId(phoneNumberId, "Phone number ID");
                validateId(qrCodeId, "QR code ID");

                HttpGet httpGet = new HttpGet(
                        String.format("%s/%s/message_qrdls/%s", whatsappApiUrl, phoneNumberId, qrCodeId));
                httpGet.addHeader("Authorization", String.format("Bearer %s", token));

                String responseBody = whatsAppService.sendRequest(httpGet);
                return OBJECT_MAPPER.readValue(responseBody, QrCodeResponse.class);
            } catch (Exception e) {
                throw new CompletionException("Failed to get QR code", e);
            }
        });
    }

    @Override
    public CompletableFuture<QrCodeResponse> updateQrCode(String phoneNumberId, String qrCodeId,
            String prefilledMessage) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateId(phoneNumberId, "Phone number ID");
                validateId(qrCodeId, "QR code ID");

                Map<String, Object> body = Map.of("prefilled_message", prefilledMessage);

                String responseBody = post(
                        String.format("%s/%s/message_qrdls/%s", whatsappApiUrl, phoneNumberId, qrCodeId), body);
                return OBJECT_MAPPER.readValue(responseBody, QrCodeResponse.class);
            } catch (Exception e) {
                throw new CompletionException("Failed to update QR code", e);
            }
        });
    }

    @Override
    public CompletableFuture<SuccessResponse> deleteQrCode(String phoneNumberId, String qrCodeId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateId(phoneNumberId, "Phone number ID");
                validateId(qrCodeId, "QR code ID");

                HttpDelete httpDelete = new HttpDelete(
                        String.format("%s/%s/message_qrdls/%s", whatsappApiUrl, phoneNumberId, qrCodeId));
                httpDelete.addHeader("Authorization", String.format("Bearer %s", token));

                String responseBody = whatsAppService.sendRequest(httpDelete);
                return OBJECT_MAPPER.readValue(responseBody, SuccessResponse.class);
            } catch (Exception e) {
                throw new CompletionException("Failed to delete QR code", e);
            }
        });
    }

    /**
     * Executes a JSON POST request against the given URL and returns the raw
     * response body.
     *
     * @param url  the fully-qualified URL to POST to.
     * @param body the request payload, serialized as JSON.
     * @return the response body as a string.
     * @throws Exception if the request fails (I/O error or
     *                    {@link com.wsclient.core.exceptions.WhatsAppException}).
     */
    private String post(String url, Map<String, Object> body) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Authorization", String.format("Bearer %s", token));
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(OBJECT_MAPPER.writeValueAsString(body), StandardCharsets.UTF_8));

        return whatsAppService.sendRequest(httpPost);
    }

    /**
     * Validates that an ID parameter was provided.
     *
     * @param id    the ID to validate.
     * @param label the human-readable name of the ID, used in the error
     *              message.
     * @throws IllegalArgumentException if {@code id} is {@code null} or
     *                                  blank.
     */
    private void validateId(String id, String label) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(label + " must not be null or blank.");
        }
    }
}
