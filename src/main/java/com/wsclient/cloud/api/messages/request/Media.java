package com.wsclient.cloud.api.messages.request;

import lombok.Builder;

/**
 * The Media Object consists of audio, document, image, sticker, and video
 * objects.
 * 
 * @param id       id
 * @param link     link
 * @param caption  caption
 * @param filename filename
 */
@Builder
public record Media(
        /**
         * <p>
         * Required when type is an image, audio, document, sticker, or video and you
         * are not using a link.
         * </p>
         * 
         * The media object ID. For more information, see Get Media ID.
         */
        Long id,
        /**
         * <p>
         * Required when type is audio, document, image, sticker, or video and you are
         * not using an uploaded media ID.
         * </p>
         * The protocol and URL of the media to be sent. Use only with HTTP/HTTPS URLs.
         */
        String link,
        /**
         * <strong>
         * Optional.
         * </strong>
         * <p>
         * Describes the specified image, document, or video. Do not use it with audio
         * or sticker media.
         * </p>
         */
        String caption,
        /**
         * <strong>
         * Optional.
         * </strong>
         * <p>
         * Describes the filename for the specific document. Use only with document
         * media.
         * </p>
         */
        String filename) {
}