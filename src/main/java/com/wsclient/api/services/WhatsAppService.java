package com.wsclient.api.services;

import java.io.IOException;
import java.util.Map;

import com.wsclient.api.messages.response.WhatsAppResponse;
import com.wsclient.core.exceptions.WhatsAppException;

/**
 * Service interface for handling WhatsApp-related operations.
 * <p>
 * This interface defines the contract for implementing services that interact
 * with the WhatsApp API, such as sending messages, retrieving conversations,
 * and processing received messages.
 * </p>
 *
 * <p>
 * Implementations of this interface should handle the necessary API calls and
 * data processing
 * to facilitate seamless communication with WhatsApp.
 * </p>
 *
 * <p>
 * <b>Usage:</b> A class implementing this interface should provide concrete
 * implementations
 * for WhatsApp messaging functionalities.
 * </p>
 *
 * @author Clisman Isaac Iscala
 * @version 1.0
 * @since 2025-03-10
 */
public interface WhatsAppService {

    /**
     * Configures the WhatsApp API credentials and endpoint URL.
     * This method initializes the necessary parameters for interacting with the
     * WhatsApp API.
     *
     * @param whatsappApiUrl The base URL of the WhatsApp API.
     * @param phoneNumberId  The ID of the phone number associated with the WhatsApp
     *                       account.
     * @param token          The authentication token for API access.
     * @throws IllegalArgumentException If any of the input parameters are invalid.
     */
    public void configureWhatsAppApi(String whatsappApiUrl, String phoneNumberId, String token);

    /**
     * Sends an HTTP request to the WhatsApp API with the provided request data.
     *
     * @param data The request payload as a key-value map.
     * @return A WhatsAppResponse object containing the API response.
     * @throws IOException          If an I/O error occurs during the HTTP request.
     * @throws InterruptedException If the operation is interrupted.
     * @throws WhatsAppException    If the API response contains an error.
     */
    WhatsAppResponse sendRequest(Map<String, Object> data) throws IOException, InterruptedException, WhatsAppException;
}
