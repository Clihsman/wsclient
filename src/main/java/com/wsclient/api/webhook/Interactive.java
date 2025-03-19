package com.wsclient.api.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Interactive
 * 
 * @param type        type
 * @param buttonReply buttonReply
 * @param listReply   listReply
 */
public record Interactive(
        /**
         * Contains the type of interactive object. Supported options are:
         * <li>
         * <code>button_reply</code>: for responses of Reply Buttons.
         * </li>
         * <li1>
         * <code>list_reply</code>: for responses to List Messages and other interactive
         * objects.
         * </li>
         */
        InteractiveType type,
        /**
         * <strong>
         * Used on Webhooks related to Reply Buttons.
         * </strong>
         * <p>
         * Contains a button reply object.
         * <p>
         */
        @JsonProperty("button_reply") ButtonReply buttonReply,
        /**
         * <strong>
         * Used on Webhooks related to List Messages
         * </strong>
         * <p>
         * Contains a list reply object.
         * </p>
         */
        @JsonProperty("list_reply") ListReply listReply) {
    /**
     * Contains the type of interactive object.
     */
    public enum InteractiveType {
        /**
         * for responses of Reply Buttons.
         */
        BUTTON_REPLY("button_reply"),
        /**
         * for responses to List Messages and other interactive objects.
         */
        LIST_REPLY("list_reply");

        private final String value;

        InteractiveType(String value) {
            this.value = value;
        }

        /**
         * getValue
         * 
         * @return value
         */
        @JsonValue
        public String getValue() {
            return value;
        }

    }
}
