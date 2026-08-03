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
     */
    public CompletableFuture<FBAccessToken> getAppAccessToken(String clientId, String clientSecret);

    /**
     * Exchanges an OAuth authorization code (obtained after a user completes
     * the Facebook Login / embedded signup flow) for a user access token.
     *
     * @param clientId     The App ID (Facebook Client ID).
     * @param clientSecret The App Secret (Facebook Client Secret).
     * @param redirectUri  The same redirect URI used in the authorization
     *                     request that produced {@code code}.
     * @param code         The authorization code returned to the redirect
     *                     URI.
     * @return A {@link CompletableFuture} that resolves to the user access
     *         token.
     */
    public CompletableFuture<FBAccessToken> exchangeCodeForUserToken(String clientId, String clientSecret,
            String redirectUri, String code);

    /**
     * Exchanges a short-lived user access token for a long-lived one
     * (typically valid for about 60 days).
     *
     * @param clientId        The App ID (Facebook Client ID).
     * @param clientSecret    The App Secret (Facebook Client Secret).
     * @param shortLivedToken The short-lived token to exchange, e.g. one
     *                        returned by
     *                        {@link #exchangeCodeForUserToken(String, String, String, String)}.
     * @return A {@link CompletableFuture} that resolves to the long-lived
     *         access token.
     */
    public CompletableFuture<FBAccessToken> getLongLivedToken(String clientId, String clientSecret,
            String shortLivedToken);
}
