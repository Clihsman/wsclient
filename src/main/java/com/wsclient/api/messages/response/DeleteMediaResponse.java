package com.wsclient.api.messages.response;

/**
 * Represents the response returned by the WhatsApp Cloud API after deleting
 * a media resource ({@code DELETE /{media-id}}).
 *
 * @param success {@code true} if the media was successfully deleted.
 */
public record DeleteMediaResponse(boolean success) {
}
