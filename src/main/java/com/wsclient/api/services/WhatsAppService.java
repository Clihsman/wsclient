package com.wsclient.api.services;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;

import com.wsclient.api.messages.response.WhatsAppResponse;
import com.wsclient.api.messages.response.template.TemplatesResponse;
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
     * Configures the WhatsApp API credentials and endpoint information.
     *
     * <p>
     * This method sets up the necessary parameters required to interact with the
     * WhatsApp Business API. It must be called before making any API requests.
     * </p>
     *
     * @param whatsappApiUrl  The base URL of the WhatsApp Graph API (e.g.,
     *                        {@code https://graph.facebook.com/v19.0}).
     * @param phoneNumberId   The ID of the phone number associated with the
     *                        WhatsApp account.
     * @param businessAccount The WhatsApp Business Account ID (WABA ID) used for
     *                        managing templates and business data.
     * @param token           The authentication token (Bearer token) with the
     *                        required scopes for API access.
     *
     * @throws IllegalArgumentException If any of the input parameters are null,
     *                                  empty, or invalid.
     */
    public void configureWhatsAppApi(String whatsappApiUrl, String phoneNumberId, String businessAccount, String token);

    /**
     * Sends an HTTP request to the WhatsApp API with the provided request data.
     *
     * @param data The request payload as a key-value map.
     * @return A WhatsAppResponse object containing the API response.
     * @throws IOException          If an I/O error occurs during the HTTP request.
     * @throws InterruptedException If the operation is interrupted.
     * @throws WhatsAppException    If the API response contains an error.
     */
    public WhatsAppResponse sendRequest(Map<String, Object> data)
            throws IOException, InterruptedException, WhatsAppException;

    /**
     * Sends the given {@link HttpPost} request to the server.
     *
     * @param httpPost the HTTP POST request to be executed.
     * @throws IOException       if an I/O error occurs while sending the request or
     *                           receiving the response.
     * @throws WhatsAppException if the response indicates an error from the
     *                           WhatsApp server.
     * @throws ParseException    if there is an error parsing the response body.
     * @return the response body as a string.
     */
    public String sendRequest(HttpPost httpPost) throws IOException, WhatsAppException;

    /**
     * Retrieves the list of available WhatsApp message templates.
     *
     * <p>
     * This method contacts the WhatsApp Business API to fetch all registered
     * message templates associated with the account. Templates can be used to send
     * pre-approved messages to users, such as notifications, alerts, and updates.
     * </p>
     *
     * @return A {@link CompletableFuture} that resolves to a
     *         {@link TemplatesResponse}
     *         containing the list of available templates.
     */
    public CompletableFuture<TemplatesResponse> getTamplates();
}
