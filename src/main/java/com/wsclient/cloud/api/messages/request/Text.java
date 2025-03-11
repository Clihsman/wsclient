package com.wsclient.cloud.api.messages.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

/**
 * A Text Object consists of the following fields and formatting options
 */
@Builder
public record Text(
        /**
         * <p>
         * Required for text messages.
         * The text of the text message that can contain URLs and supports formatting.
         * To view available formatting options, see Text Object Formatting Options.
         * </p>
         * <br>
         * <p>
         * If you include URLs in your text and want to include a preview box in text
         * messages ("preview_url": true), ensure it starts with http:// or https://.
         * You must include a hostname, since IP addresses are not matched.
         * </p>
         * <br>
         * Maximum length: 4096 characters.
         */
        String body,
        /**
         * <strong>
         * Optional.
         * </strong>
         * <p>
         * By default, WhatsApp recognizes URLs and makes them clickable, but you can
         * also include a preview box with more information about the link. Set this
         * field to true if you want to include a URL preview box.
         * </p>
         * <p>
         * The majority of the time when you send a URL, whether with a preview or not,
         * the receiver of the message will see a URL that they can click on.
         * </p>
         * <p>
         * URL previews are only rendered after one of the following has occurred:
         * The business has sent a message template to the user.
         * The user initiates a conversation with a "click to chat" link.
         * The user adds the business phone number to their address book and initiates a
         * conversation.
         * </p>
         * 
         * Default: false
         * 
         */
        @JsonProperty("preview_url") Boolean previewUrl) {
}
