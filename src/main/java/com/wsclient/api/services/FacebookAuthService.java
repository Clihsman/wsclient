package com.wsclient.api.services;

import java.util.concurrent.CompletableFuture;

import com.wsclient.api.webhook.FBAccessToken;

public interface FacebookAuthService {
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
