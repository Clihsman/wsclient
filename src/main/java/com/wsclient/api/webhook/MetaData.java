package com.wsclient.api.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * MetaData
 * 
 * @param displayPhoneNumber displayPhoneNumber
 * @param phoneNumberId      phoneNumberId
 */
public record MetaData(
                /**
                 * The phone number of the business account that is receiving the Webhooks.
                 */
                @JsonProperty("display_phone_number") String displayPhoneNumber,
                /**
                 * The ID of the phone number receiving the Webhooks. You can use this
                 * <code>phone_number_id</code> to send messages back to customers.
                 */
                @JsonProperty("phone_number_id") String phoneNumberId) {
}
