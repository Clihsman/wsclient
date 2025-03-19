package com.wsclient.api.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Specifies the product the user is requesting information about. For more
 * information, see Receive Response From Customers.
 * 
 * @param catalogId         catalogId
 * @param productRetailerId productRetailerId
 */
public record ReferredProduct(
                /**
                 * Unique identifier of the Meta catalog linked to the WhatsApp Business
                 * Account.
                 */
                @JsonProperty("catalog_id") String catalogId,
                /**
                 * Unique identifier of the product in a catalog.
                 */
                @JsonProperty("product_retailer_id") String productRetailerId) {

}
