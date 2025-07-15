package com.wsclient.api.messages.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Currency Object contains the following fields
 * 
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Currency {

    /**
     * <strong>Required.</strong>
     * <p>
     * The default text if localization fails.
     * </p>
     */
    @JsonProperty("fallback_value")
    private String fallbackValue;
    /**
     * <strong>Required.</strong>
     * <p>
     * The currency code as defined in
     * <a href=
     * "https://en.wikipedia.org/wiki/ISO_4217#Active_codes">ISO 4217</a>
     * </p>
     * 
     */
    private String code;
    /**
     * <strong>Required.</strong>
     * <p>
     * The amount multiplied by 1000.
     * </p>
     */
    @JsonProperty("amount_1000")
    private String amount1000;
}
