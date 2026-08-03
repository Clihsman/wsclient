package com.wsclient.api.templates.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for editing an existing WhatsApp message template, via
 * {@code POST /{template-id}}.
 * <p>
 * Both fields are optional individually, but at least one should be set —
 * Meta only lets you edit a template's {@code category} and/or
 * {@code components} (name and language cannot be changed after creation).
 * </p>
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditTemplateRequest {

    /**
     * <strong>
     * Optional.
     * </strong>
     * <p>
     * Values: {@code AUTHENTICATION}, {@code MARKETING}, {@code UTILITY}.
     * </p>
     */
    private String category;

    /**
     * <strong>
     * Optional.
     * </strong>
     * <p>
     * The updated components (header/body/footer/buttons). Editing a
     * template resets its approval status.
     * </p>
     */
    private List<TemplateDefinitionComponent> components;
}
