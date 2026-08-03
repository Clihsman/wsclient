package com.wsclient.api.messages.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the response returned by the WhatsApp Cloud API when retrieving
 * the details of a previously uploaded media resource ({@code GET
 * /{media-id}}).
 *
 * @param messagingProduct The messaging product, always "whatsapp".
 * @param url              A temporary URL to download the media file. Expires
 *                         after a short period and requires the same access
 *                         token used to request it.
 * @param mimeType         The MIME type of the media.
 * @param sha256           The SHA256 checksum of the media.
 * @param fileSize         The file size, in bytes.
 * @param id               The media's unique identifier.
 */
public record MediaInfoResponse(
        @JsonProperty("messaging_product") String messagingProduct,
        String url,
        @JsonProperty("mime_type") String mimeType,
        String sha256,
        @JsonProperty("file_size") String fileSize,
        String id) {
}
