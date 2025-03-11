package com.wsclient.cloud.api.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Media Object is used for audio, images, documents, videos and stickers.
 */
public record Media(
        /**
         * <strong>
         * Added to Webhooks if it has been previously specified.
         * </strong>
         * <p>
         * The caption that describes the media.
         * <p>
         */
        String caption,
        /**
         * <strong>
         * Added to Webhooks for document messages.
         * </strong>
         * <p>
         * The media's filename on the sender's device.
         * </p>
         */
        String filename,
        /**
         * The ID of the media.
         */
        String id,
        /**
         * The mime type of the media.
         */
        @JsonProperty("mime_type") String mimeType,
        /**
         * The checksum of the media.
         */
        String sha256) {

}
