package com.wsclient.api.templates.response;

/**
 * Response returned by the WhatsApp Cloud API after creating a new message
 * template.
 *
 * @param id       The newly created template's unique identifier.
 * @param status   The template's initial review status (e.g.
 *                 {@code "PENDING"}).
 * @param category The template's category, which Meta may have reclassified
 *                 during review.
 */
public record CreateTemplateResponse(String id, String status, String category) {
}
