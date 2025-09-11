package com.wsclient.api.services;

import java.util.concurrent.CompletableFuture;

/**
 * Service interface for managing the integration of WhatsApp Webhooks
 * with Meta's Graph API.
 * <p>
 * This service is responsible for configuring the connection to the
 * Graph API and subscribing an application to receive WhatsApp-related
 * webhook events.
 * </p>
 */
public interface MetaWebhookService {

    /**
     * Configures the Meta Graph API base URL.
     * <p>
     * This method should be called before invoking any other operation
     * to ensure that the service points to the correct Graph API endpoint.
     * </p>
     *
     * @param graphUrl The base URL of the Meta Graph API (e.g.,
     *                 {@code https://graph.facebook.com/v20.0}).
     */
    public void configure(String graphUrl);

    /**
     * Subscribes a Meta application to WhatsApp webhook events.
     * <p>
     * This registers the application with Meta’s Graph API to start
     * receiving events (such as incoming messages) from a WhatsApp
     * Business Account. The provided callback URL will be invoked by
     * Meta when events occur.
     * </p>
     *
     * @param appId       The ID of the Meta application to subscribe.
     * @param accessToken The access token for authenticating with the Graph API.
     * @param callbackUrl The public URL where webhook events should be delivered.
     * @param verifyToken A token used to validate webhook verification requests.
     * @return A {@link CompletableFuture} that completes when the subscription
     *         process has finished. The future will be completed exceptionally
     *         if the subscription fails.
     */
    public CompletableFuture<String> subscribeApp(
            String appId,
            String accessToken,
            String callbackUrl,
            String verifyToken);
}
