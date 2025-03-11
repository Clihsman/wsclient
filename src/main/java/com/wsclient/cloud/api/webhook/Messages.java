package com.wsclient.cloud.api.webhook;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The messages array of objects is nested within the Value object and is
 * triggered when a customer updates their profile information or a customer
 * sends a message to the business that is subscribed to the Webhook.
 */
public record Messages(
        /**
         * The customer's phone number.
         */
        String from,
        /**
         * The unique identifier of incoming message, you can use messages endpoint to
         * mark it as read.
         */
        String id,
        /**
         * The timestamp when a customer sends a message.
         */
        Long timestamp,
        /**
         * <p>
         * The type of message being received.
         * </p>
         * Supported values are:
         * <ul>
         * <li><code>text</code>: for text messages.</li>
         * 
         * <li><code>image</code>: for image (media) messages.</li>
         * 
         * <li><code>interactive</code>: for interactive messages.</li>
         * 
         * <li><code>document</code>: for document (media) messages.</li>
         * 
         * <li><code>audio</code>: for audio and voice (media) messages.</li>
         * 
         * <li><code>sticker</code>: for sticker messages.</li>
         * 
         * <li><code>order</code>: for when a customer has placed an order.</li>
         * 
         * <li><code>video</code>: for video (media) messages.</li>
         * 
         * <li><code>button</code>: for responses to interactive message templates.</li>
         * 
         * <li><code>contacts</code>: for contact messages.</li>
         * 
         * <li><code>location</code>: for location messages.</li>
         * 
         * <li><code>unknown</code>: for unknown messages.</li>
         * 
         * <li><code>system</code>: for user number change messages.</li>
         * </ul>
         * 
         * change messages.
         */
        MessagesType type,
        /**
         * Added to Webhook if message is forwarded or an inbound reply.
         * A context object.
         */
        Context context,
        /**
         * Added to Webhook if show_security_notifications is enabled in application
         * settings.
         * An identity object.
         */
        Identity identity,
        /**
         * <strong>
         * Added to Webhook if type is <code>text</code>.
         * </strong>
         * <p>
         * A text object.
         * </p>
         */
        Text text,
        /**
         * Added to Webhook if type is audio (including voice messages).
         * A media object with the audio information.
         * 
         */
        Media audio,
        /**
         * <strong>
         * Added to Webhook if type is image.
         * </strong>
         * <p>
         * A media object with the image information.
         * </p>
         */
        Media image,
        /**
         * <strong>
         * Added to Webhook if type is sticker.
         * </strong>
         * <p>
         * A media object with the sticker information.
         * </p>
         */
        Media sticker,
        /**
         * <strong>
         * Added to Webhook if type is document.
         * </strong>
         * <p>
         * A media object with the document information.
         * </p>
         */
        Media document,
        /**
         * <strong>
         * Added to Webhook if type is video.
         * </strong>
         * <p>
         * A media object with the video information.
         * </p>
         */
        Media video,
        /**
         * <strong>
         * Added to Webhook if type is <code>interactive</code>.
         * </strong>
         * <p>
         * When a customer has interacted with your message, an interactive object is
         * included in the <strong>`Messages`</strong> object.
         * </p>
         */
        Interactive interactive,
        /**
         * <strong>
         * Added to Webhook if type is system.
         * </strong>
         * <p>
         * A system message object.
         * </p>
         */
        SystemMessage system,
        /**
         * <strong>
         * Added to Webhook if type is <code>button</code>.
         * </strong>
         * <p>
         * A button message object.
         * This field is used when the Webhook notifies you that a user clicked on a
         * quick reply button.
         * </p>
         */
        Button button,
        /**
         * <strong>
         * Added to Webhook if the message is coming from a user that clicked an ad that
         * <code>is Click To WhatsApp</code>.
         * </strong>
         * <p>
         * A referral object. This is how the referral object works:
         * </p>
         * 
         * <li>
         * 1. A user clicks on an ad with the Click to WhatsApp call-to-action.
         * </li>
         * 
         * </li>
         * 2. User is redirected to WhatsApp and sends a message to the advertising
         * business.
         * </li>
         * 
         * <li>
         * 3. User sends a message to the business. Be aware that users may elect to
         * remove their referral data.
         * </li>
         * 
         * <li>
         * 4. The advertising business gets an inbound message notification including
         * the referral object, which provides additional context on the ad that
         * triggered the message. Knowing all this information, the business can
         * appropriately reply to the user message.
         * </li>
         */
        Referral referral

) {
    /**
     * The type of message being received.
     */
    public enum MessagesType {
        /**
         * for text messages
         */
        TEXT("text"),
        /**
         * for image (media)
         */
        IMAGE("image"),
        /**
         * for interactive messages.
         */
        INTERACTIVE("interactive"),
        /**
         * for document (media) messages.
         */
        DOCUMENT("document"),
        /**
         * for audio and voice (media) messages.
         */
        AUDIO("audio"),
        /**
         * for sticker messages.
         */
        STICKER("sticker"),
        /**
         * for when a customer has placed an order.
         */
        ORDER("order"),
        /**
         * for video (media) messages.
         */
        VIDEO("video"),
        /**
         * for responses to interactive message templates.
         */
        BUTTON("button"),
        /**
         * for contact messages.
         */
        CONTACTS("contacts"),
        /**
         * for location messages.
         */
        LOCATION("location"),
        /**
         * for unknown messages.
         */
        UNKNOWN("unknown"),
        /**
         * for user number change messages.
         */
        SYSTEM("system");

        private final String value;

        MessagesType(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }
}
