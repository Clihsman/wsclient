package com.wsclient.api.messages.request.interactive;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single product entry within a
 * {@link Interactive.InteractiveType#PRODUCT_LIST} section.
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InteractiveProductItem {

    /**
     * <strong>
     * Required.
     * </strong>
     *
     * <p>
     * Unique identifier of the product in the catalog connected to the
     * WhatsApp Business Account.
     * </p>
     */
    @JsonProperty("product_retailer_id")
    private String productRetailerId;
}
