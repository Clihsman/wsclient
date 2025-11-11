package com.wsclient.api.messages.response;

/**
 * Represents the response returned by the WhatsApp Business API after uploading
 * or retrieving a media resource.
 *
 * <p>
 * This record encapsulates the unique identifier assigned to a media file (such
 * as an image, video,
 * document, or audio) stored in the WhatsApp Cloud API. The {@code id} can be
 * used in subsequent requests
 * to reference, send, or delete the media.
 * </p>
 *
 * <h3>Example Usage:</h3>
 * 
 * <pre>{@code
 * MediaResponse response = new MediaResponse("1234567890");
 * System.out.println(response.id()); // Outputs: 1234567890
 * }</pre>
 *
 * @param id the unique identifier of the media resource in the WhatsApp
 *           Business API.
 */
public record MediaResponse(String id) {
}
