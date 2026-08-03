package com.wsclient.api.services;

import java.util.concurrent.CompletableFuture;

import com.wsclient.api.messages.response.PhoneNumberResponse;
import com.wsclient.api.messages.response.PhoneNumbersListResponse;
import com.wsclient.api.messages.response.QrCodeResponse;
import com.wsclient.api.messages.response.QrCodesListResponse;
import com.wsclient.api.messages.response.SuccessResponse;

/**
 * Service interface for managing a WhatsApp Business Account's phone
 * numbers and message shortlinks (QR codes) via the WhatsApp Business
 * Management API.
 */
public interface WhatsAppBusinessManagementService {

    /**
     * Configures the API credentials and endpoint used by this service.
     *
     * @param whatsappApiUrl  The base URL of the WhatsApp Graph API.
     * @param businessAccount The WhatsApp Business Account (WABA) ID.
     * @param token           The authentication token for API access.
     */
    public void configure(String whatsappApiUrl, String businessAccount, String token);

    /**
     * Lists the phone numbers registered on the configured WhatsApp Business
     * Account.
     *
     * @return A {@link CompletableFuture} that resolves to a
     *         {@link PhoneNumbersListResponse}.
     */
    public CompletableFuture<PhoneNumbersListResponse> listPhoneNumbers();

    /**
     * Retrieves the details of a specific phone number.
     *
     * @param phoneNumberId The ID of the phone number to look up.
     * @return A {@link CompletableFuture} that resolves to a
     *         {@link PhoneNumberResponse}.
     */
    public CompletableFuture<PhoneNumberResponse> getPhoneNumber(String phoneNumberId);

    /**
     * Registers a phone number for use with the WhatsApp Cloud API.
     *
     * @param phoneNumberId The ID of the phone number to register.
     * @param pin           The 6-digit PIN for two-step verification.
     * @return A {@link CompletableFuture} that resolves to a
     *         {@link SuccessResponse}.
     */
    public CompletableFuture<SuccessResponse> registerPhoneNumber(String phoneNumberId, String pin);

    /**
     * Requests a verification code for a phone number.
     *
     * @param phoneNumberId The ID of the phone number to verify.
     * @param codeMethod    The delivery method, e.g. {@code "SMS"} or
     *                      {@code "VOICE"}.
     * @param locale        The locale to use for the verification message
     *                      (e.g. {@code "en_US"}).
     * @return A {@link CompletableFuture} that resolves to a
     *         {@link SuccessResponse}.
     */
    public CompletableFuture<SuccessResponse> requestVerificationCode(String phoneNumberId, String codeMethod,
            String locale);

    /**
     * Verifies a phone number using the code received via
     * {@link #requestVerificationCode(String, String, String)}.
     *
     * @param phoneNumberId The ID of the phone number being verified.
     * @param code          The verification code received by the phone
     *                      number.
     * @return A {@link CompletableFuture} that resolves to a
     *         {@link SuccessResponse}.
     */
    public CompletableFuture<SuccessResponse> verifyCode(String phoneNumberId, String code);

    /**
     * Creates a new message shortlink (QR code) for a phone number.
     *
     * @param phoneNumberId    The ID of the phone number the code belongs to.
     * @param prefilledMessage The message pre-filled when a customer scans
     *                         the code or opens its deep link.
     * @return A {@link CompletableFuture} that resolves to the created
     *         {@link QrCodeResponse}.
     */
    public CompletableFuture<QrCodeResponse> createQrCode(String phoneNumberId, String prefilledMessage);

    /**
     * Lists the QR codes created for a phone number.
     *
     * @param phoneNumberId The ID of the phone number.
     * @return A {@link CompletableFuture} that resolves to a
     *         {@link QrCodesListResponse}.
     */
    public CompletableFuture<QrCodesListResponse> listQrCodes(String phoneNumberId);

    /**
     * Retrieves the details of a specific QR code.
     *
     * @param phoneNumberId The ID of the phone number the code belongs to.
     * @param qrCodeId      The ID of the QR code to look up.
     * @return A {@link CompletableFuture} that resolves to a
     *         {@link QrCodeResponse}.
     */
    public CompletableFuture<QrCodeResponse> getQrCode(String phoneNumberId, String qrCodeId);

    /**
     * Updates the pre-filled message of an existing QR code.
     *
     * @param phoneNumberId    The ID of the phone number the code belongs to.
     * @param qrCodeId         The ID of the QR code to update.
     * @param prefilledMessage The new pre-filled message.
     * @return A {@link CompletableFuture} that resolves to the updated
     *         {@link QrCodeResponse}.
     */
    public CompletableFuture<QrCodeResponse> updateQrCode(String phoneNumberId, String qrCodeId,
            String prefilledMessage);

    /**
     * Deletes a QR code.
     *
     * @param phoneNumberId The ID of the phone number the code belongs to.
     * @param qrCodeId      The ID of the QR code to delete.
     * @return A {@link CompletableFuture} that resolves to a
     *         {@link SuccessResponse}.
     */
    public CompletableFuture<SuccessResponse> deleteQrCode(String phoneNumberId, String qrCodeId);
}
