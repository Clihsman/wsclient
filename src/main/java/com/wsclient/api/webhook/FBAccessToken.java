package com.wsclient.api.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FBAccessToken(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") Long expiresIn) {

}
