package com.wsclient.api.services;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wsclient.api.messages.response.WhatsAppResponse;
import com.wsclient.api.messages.response.template.TemplatesResponse;
import com.wsclient.api.templates.request.CreateTemplateRequest;
import com.wsclient.api.templates.request.EditTemplateRequest;
import com.wsclient.api.templates.response.CreateTemplateResponse;
import com.wsclient.api.templates.response.TemplateActionResponse;
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
        public void configureWhatsAppApi(String whatsappApiUrl, String phoneNumberId, String businessAccount,
                        String token);

        /**
         * Sends an HTTP request to the WhatsApp API with the provided request data.
         *
         * @param data The request payload as a key-value map.
         * @param path
         *             The relative API path of the WhatsApp Cloud API resource to be
         *             called.
         * @return A WhatsAppResponse object containing the API response.
         * @throws IOException          If an I/O error occurs during the HTTP request.
         * @throws InterruptedException If the operation is interrupted.
         * @throws WhatsAppException    If the API response contains an error.
         */
        public WhatsAppResponse sendRequest(Map<String, Object> data, String path)
                        throws IOException, InterruptedException, WhatsAppException;

        /**
         * Sends an HTTP request to the WhatsApp Cloud API using the provided JSON
         * payload.
         * <p>
         * This method executes a synchronous HTTP request to the configured WhatsApp
         * endpoint and returns the parsed API response. It is used internally by
         * higher-
         * level client methods to interact with WhatsApp resources such as messages,
         * business profiles, and QR codes.
         * </p>
         *
         * @param data
         *             The request payload represented as a JSON object. The structure
         *             of this
         *             object must conform to the requirements of the target WhatsApp
         *             API
         *             endpoint.
         *
         * @param path
         *             The relative API path of the WhatsApp Cloud API resource to be
         *             called.
         * @return A {@link WhatsAppResponse} containing the response returned by the
         *         WhatsApp Cloud API.
         *
         * @throws IOException
         *                              If an I/O error occurs while sending or
         *                              receiving the HTTP request.
         *
         * @throws InterruptedException
         *                              If the HTTP request is interrupted during
         *                              execution.
         *
         * @throws WhatsAppException
         *                              If the WhatsApp API returns an error response or
         *                              a non-success status.
         */
        public WhatsAppResponse sendRequest(ObjectNode data, String path)
                        throws IOException, InterruptedException, WhatsAppException;

        /**
         * Sends the given {@link HttpPost} request to the server.
         *
         * @param httpPost the HTTP POST request to be executed.
         * @param path
         *                 The relative API path of the WhatsApp Cloud API resource to
         *                 be called.
         * @throws IOException       if an I/O error occurs while sending the request or
         *                           receiving the response.
         * @throws WhatsAppException if the response indicates an error from the
         *                           WhatsApp server.
         * @throws ParseException    if there is an error parsing the response body.
         * @return the response body as a string.
         */
        public String sendRequest(HttpPost httpPost) throws IOException, WhatsAppException;

        /**
         * Sends an HTTP GET request to the WhatsApp Cloud API using the provided
         * request.
         * <p>
         * This method executes a synchronous {@link HttpGet} request against the
         * WhatsApp Cloud API. The provided {@code path} is used to build the final
         * endpoint URL relative to the configured base Graph API URL and version.
         * </p>
         *
         * <p>
         * This method is intended for low-level API interactions and is typically
         * invoked internally by higher-level client operations.
         * </p>
         *
         * @param httpGet
         *                The pre-configured {@link HttpGet} request instance to be
         *                executed.
         *
         * @param path
         *                The relative WhatsApp Cloud API endpoint path associated with
         *                the
         *                request (for example, {@code "/whatsapp_business_profile"}).
         *
         * @return The raw response body returned by the WhatsApp Cloud API as a
         *         {@link String}.
         *
         * @throws IOException
         *                           If an I/O error occurs while sending or receiving
         *                           the HTTP request.
         *
         * @throws WhatsAppException
         *                           If the WhatsApp Cloud API returns an error response
         *                           or a non-success
         *                           HTTP status.
         */
        public String sendRequest(HttpGet HttpGet) throws IOException, WhatsAppException;

        /**
         * Sends an HTTP DELETE request to the WhatsApp Cloud API using the provided
         * request.
         * <p>
         * This method executes a synchronous {@link HttpDelete} request against the
         * WhatsApp Cloud API. It is intended for low-level API interactions, such as
         * deleting an uploaded media resource, and is typically invoked internally by
         * higher-level client operations.
         * </p>
         *
         * @param httpDelete
         *                   The pre-configured {@link HttpDelete} request instance to
         *                   be executed.
         *
         * @return The raw response body returned by the WhatsApp Cloud API as a
         *         {@link String}.
         *
         * @throws IOException
         *                           If an I/O error occurs while sending or receiving
         *                           the HTTP request.
         *
         * @throws WhatsAppException
         *                           If the WhatsApp Cloud API returns an error response
         *                           or a non-success HTTP status.
         */
        public String sendRequest(HttpDelete httpDelete) throws IOException, WhatsAppException;

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

        /**
         * Creates a new WhatsApp message template on the configured WhatsApp
         * Business Account.
         *
         * @param request The template definition (name, category, language,
         *                components) to create.
         * @return A {@link CompletableFuture} that resolves to a
         *         {@link CreateTemplateResponse} with the new template's ID and
         *         initial review status.
         */
        public CompletableFuture<CreateTemplateResponse> createTemplate(CreateTemplateRequest request);

        /**
         * Edits an existing WhatsApp message template.
         * <p>
         * Editing a template resets its approval status; only {@code category}
         * and {@code components} can be changed.
         * </p>
         *
         * @param templateId The ID of the template to edit (as returned by
         *                   {@link #createTemplate(CreateTemplateRequest)} or
         *                   {@link #getTamplates()}).
         * @param request    The fields to update.
         * @return A {@link CompletableFuture} that resolves to a
         *         {@link TemplateActionResponse} once the edit is processed.
         */
        public CompletableFuture<TemplateActionResponse> editTemplate(String templateId, EditTemplateRequest request);

        /**
         * Deletes a WhatsApp message template by name.
         * <p>
         * Deleting by name removes every language version of the template.
         * </p>
         *
         * @param templateName The name of the template to delete.
         * @return A {@link CompletableFuture} that resolves to a
         *         {@link TemplateActionResponse} once the deletion is processed.
         */
        public CompletableFuture<TemplateActionResponse> deleteTemplate(String templateName);

        /**
         * Returns the WhatsApp Phone Number ID associated with the current client
         * configuration.
         * <p>
         * The Phone Number ID uniquely identifies a WhatsApp-enabled phone number
         * within the WhatsApp Cloud API. It is required to perform operations such as
         * sending messages, managing the business profile, and marking messages as
         * read.
         * </p>
         *
         * @return The Phone Number ID configured for this client.
         */
        public String getPhoneNumberId();

        /**
         * Returns the WhatsApp Business Account (WABA) ID associated with the current
         * client configuration.
         * <p>
         * The Business Account ID uniquely identifies the WhatsApp Business Account
         * within Meta's platform. It is commonly used to manage phone numbers,
         * templates, and other business-level resources.
         * </p>
         *
         * @return The WhatsApp Business Account ID configured for this client.
         */
        public String getBusinessAccount();

        /**
         * Returns the base WhatsApp Cloud API URL used by this client.
         * <p>
         * This URL represents the root endpoint for all WhatsApp Cloud API requests
         * and is combined with the configured API version and relative endpoint paths
         * to build the final request URLs.
         * </p>
         *
         * <p>
         * Example value:
         * {@code https://graph.facebook.com}
         * </p>
         *
         * @return The base WhatsApp Cloud API URL.
         */
        public String getWhatsappApiUrl();
}