package com.wsclient.api.services;

import java.util.concurrent.CompletableFuture;

import com.wsclient.api.webhook.FBAccessToken;

public interface FacebookAuthService {

    /**
     * Configures the base Facebook Graph API OAuth endpoint used to request
     * access tokens.
     * <p>
     * Optional — if never called, the service defaults to Meta's production
     * endpoint ({@code https://graph.facebook.com/oauth/access_token}).
     * </p>
     *
     * @param graphApiUrl The full URL of the Graph API OAuth access token
     *                    endpoint.
     */
    public void configure(String graphApiUrl);

    /**
     * Requests an App Access Token from Facebook Graph API.
     *
     * @param clientId     The App ID (Facebook Client ID).
     * @param clientSecret The App Secret (Facebook Client Secret).
     * @return FacebookAccessTokenResponse containing the access_token and
     *         token_type.
     * @throws Exception if the request fails.
     */
    public CompletableFuture<FBAccessToken> getAppAccessToken(String clientId, String clientSecret);
}
