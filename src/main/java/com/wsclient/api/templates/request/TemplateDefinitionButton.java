package com.wsclient.api.templates.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Describes a single button within the {@code BUTTONS} component of a
 * template being created or edited (as opposed to
 * {@link com.wsclient.api.messages.request.ButtonParameter}, which fills in a
 * dynamic value on an already-approved template when *sending* a message).
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateDefinitionButton {

    /**
     * <strong>
     * Required.
     * </strong>
     * <p>
     * The type of button. Common values: {@code QUICK_REPLY}, {@code URL},
     * {@code PHONE_NUMBER}, {@code OTP}.
     * </p>
     */
    private String type;

    /**
     * <strong>
     * Required.
     * </strong>
     * <p>
     * The button's label text.
     * </p>
     */
    private String text;

    /**
     * <strong>
     * Required for {@code URL} buttons.
     * </strong>
     * <p>
     * The URL to open, which may contain a {@code {{1}}} placeholder for a
     * dynamic suffix.
     * </p>
     */
    private String url;

    /**
     * <strong>
     * Required for {@code PHONE_NUMBER} buttons.
     * </strong>
     */
    @JsonProperty("phone_number")
    private String phoneNumber;

    /**
     * <strong>
     * Required when the button's text or URL contains a placeholder.
     * </strong>
     * <p>
     * Example values Meta uses to validate the template, e.g.
     * {@code ["https://example.com/orders/860198"]} for a URL button.
     * </p>
     */
    private List<String> example;
}
