package com.wsclient.cloud.api.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Context(
        /**
         * <strong>
         * Added to Webhooks if message was forwarded.
         * </strong>
         * <p>
         * Set to <code>true</code> if the received message has been forwarded.
         * </p>
         */
        Boolean forwarded,
        /**
         * <strong>
         * Added to Webhooks if message has been frequently forwarded.
         * </strong>
         * <p>
         * Set to <code>true</code> if the received message has been forwarded more than
         * five times.
         * </p>
         */
        @JsonProperty("frequently_forwarded") Boolean frequentlyForwarded,
        /**
         * <strong>
         * Added to Webhooks if message is an inbound reply to a sent message.
         * </strong>
         * <p>
         * The WhatsApp ID of the sender of the sent message.
         * </p>
         */
        String from,
        /**
         * <strong>
         * Optional
         * </strong>
         * <p>
         * The message ID for the sent message for an inbound reply.
         * </p>
         */
        String id,
        /**
         * <strong>
         * Required for Product Enquiry Messages.
         * </strong>
         * <p>
         * Specifies the product the user is requesting information about. For more
         * information, see Receive Response From Customers.
         * </p>
         * <p>
         * The referred_product object contains the following fields:
         * </p>
         * <li>
         * - <code>catalog_id</code>: Unique identifier of the Meta catalog linked to the WhatsApp
         * Business Account.
         * </li>
         * <li>
         * - <code>product_retailer_id</code>: Unique identifier of the product in a catalog.
         * </li>
         */
        ReferredProduct referredProduct
) {

}
