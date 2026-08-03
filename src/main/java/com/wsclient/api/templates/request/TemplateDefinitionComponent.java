package com.wsclient.api.templates.request;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Describes a single component (header, body, footer, or buttons) of a
 * template being created or edited.
 * <p>
 * This is distinct from {@link com.wsclient.api.messages.request.Component},
 * which fills in dynamic parameter values on an already-approved template
 * when *sending* a message — the shapes are not interchangeable.
 * </p>
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateDefinitionComponent {

    /**
     * <strong>
     * Required.
     * </strong>
     * <p>
     * Values: {@code HEADER}, {@code BODY}, {@code FOOTER}, {@code BUTTONS}.
     * </p>
     */
    private String type;

    /**
     * <strong>
     * Optional, only applies to {@code HEADER}.
     * </strong>
     * <p>
     * Values: {@code TEXT}, {@code IMAGE}, {@code VIDEO}, {@code DOCUMENT},
     * {@code LOCATION}.
     * </p>
     */
    private String format;

    /**
     * <strong>
     * Required for {@code HEADER} (when {@code format} is {@code TEXT}) and
     * {@code BODY}/{@code FOOTER}.
     * </strong>
     * <p>
     * The component's text, which may contain numbered placeholders like
     * {@code {{1}}}.
     * </p>
     */
    private String text;

    /**
     * <strong>
     * Required when {@code text} contains a placeholder.
     * </strong>
     * <p>
     * Sample values Meta uses to validate and render a preview of the
     * template, keyed the way Meta expects for the given component type
     * (e.g. {@code {"body_text": [["John", "9AM"]]}}).
     * </p>
     */
    private Map<String, Object> example;

    /**
     * <strong>
     * Required when {@code type} is {@code BUTTONS}.
     * </strong>
     */
    private List<TemplateDefinitionButton> buttons;
}
