package com.wsclient.cloud.api.messages.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * WhatsAppResponse
 * 
 * @param messagingProduct messagingProduct
 * @param contacts         contacts
 * @param messages         messages
 */
public record WhatsAppResponse(
                @JsonProperty("messaging_product") String messagingProduct,
                List<ContactData> contacts,
                List<MessageData> messages) {
}
