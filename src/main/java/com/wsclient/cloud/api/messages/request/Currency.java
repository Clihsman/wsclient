package com.wsclient.cloud.api.messages.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

/**
 * The Currency Object contains the following fields
 */
@Builder
public record Currency(
        /**
         * <strong>Required.</strong>
         * <p>
         * The default text if localization fails.
         * </p>
         */
        @JsonProperty("fallback_value") String fallbackValue,
        /**
         * <strong>Required.</strong>
         * <p>
         * The currency code as defined in
         * <a href=
         * "https://en.wikipedia.org/wiki/ISO_4217#Active_codes">ISO 4217</a>
         * </p>
         * 
         */
        String code,
        /**
         * <strong>Required.</strong>
         * <p>
         * The amount multiplied by 1000.
         * </p>
         */
        @JsonProperty("amount_1000") String amount1000) {
}
