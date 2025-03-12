package com.wsclient.cloud.api.messages.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ContactData
 * 
 * @param input input
 * @param waId  waId
 */
public record ContactData(
                String input,

                @JsonProperty("wa_id") String waId) {
}