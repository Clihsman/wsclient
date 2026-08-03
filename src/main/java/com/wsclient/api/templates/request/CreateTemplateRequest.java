package com.wsclient.api.templates.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for creating a new WhatsApp message template, via
 * {@code POST /{waba-id}/message_templates}.
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTemplateRequest {

    /**
     * <strong>
     * Required.
     * </strong>
     * <p>
     * The template's name. Must be unique within the WhatsApp Business
     * Account, lowercase, and use only letters, numbers, and underscores.
     * </p>
     */
    private String name;

    /**
     * <strong>
     * Required.
     * </strong>
     * <p>
     * Values: {@code AUTHENTICATION}, {@code MARKETING}, {@code UTILITY}.
     * </p>
     */
    private String category;

    /**
     * <strong>
     * Required.
     * </strong>
     * <p>
     * The template's language/locale code (e.g. {@code "en_US"}).
     * </p>
     */
    private String language;

    /**
     * <strong>
     * Required.
     * </strong>
     * <p>
     * The template's components (header/body/footer/buttons).
     * </p>
     */
    private List<TemplateDefinitionComponent> components;

    /**
     * <strong>
     * Optional.
     * </strong>
     * <p>
     * Whether Meta is allowed to reclassify the template's category during
     * review.
     * </p>
     */
    @JsonProperty("allow_category_change")
    private Boolean allowCategoryChange;
}
