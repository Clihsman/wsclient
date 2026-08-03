package com.wsclient.api.messages.factory;

import java.util.ArrayList;
import java.util.List;

import com.wsclient.api.messages.request.Component;
import com.wsclient.api.messages.request.Component.ComponentType;
import com.wsclient.api.messages.request.Currency;
import com.wsclient.api.messages.request.DateTime;
import com.wsclient.api.messages.request.Language;
import com.wsclient.api.messages.request.Media;
import com.wsclient.api.messages.request.Parameter;
import com.wsclient.api.messages.request.Parameter.ParameterType;
import com.wsclient.api.messages.request.Template;

/**
 * Factory class for building {@link Template} WhatsApp message objects
 * in a clean and fluent way.
 * <p>
 * Multiple calls that target the same component (e.g. several
 * {@link #bodyText(String)} calls) are grouped into a single
 * {@link Component}, whose {@code parameters} list is filled in the same
 * order the methods were called. Components are assembled in the fixed
 * order {@code HEADER}, {@code BODY}, {@code BUTTON} regardless of call
 * order, and a component is only included in the resulting {@link Template}
 * if at least one parameter was added to it.
 * <p>
 * Usage example:
 *
 * <pre>
 * Template template = TemplateFactory.create("order_confirmation", "es_CO")
 *         .headerText("Order confirmed")
 *         .bodyText("John Doe")
 *         .bodyText("order-123")
 *         .buttonText("order-123")
 *         .build();
 * </pre>
 *
 * <p>
 * <strong>Note:</strong> {@code buttonText(...)} appends to a single
 * {@code BUTTON} component's parameter list. The underlying {@link Component}
 * model does not carry a button {@code sub_type}/{@code index}, so this
 * factory cannot target a specific button when a template has more than one.
 * </p>
 */
public class TemplateFactory {

    private final String name;
    private final String languageCode;
    private String languagePolicy;

    private final List<Parameter> headerParameters = new ArrayList<>();
    private final List<Parameter> bodyParameters = new ArrayList<>();
    private final List<Parameter> buttonParameters = new ArrayList<>();

    private TemplateFactory(String name, String languageCode) {
        this.name = name;
        this.languageCode = languageCode;
    }

    /**
     * Creates a new factory for a template with the given name and language
     * code.
     *
     * @param name         the name of the template, as registered with Meta.
     * @param languageCode the language/locale code (e.g. {@code "en_US"}).
     * @return a new factory instance.
     */
    public static TemplateFactory create(String name, String languageCode) {
        return new TemplateFactory(name, languageCode);
    }

    /**
     * Overrides the language policy sent to WhatsApp. Optional — Meta defaults
     * to {@code "deterministic"} when this is not set.
     *
     * @param policy the language policy.
     * @return the current factory instance for method chaining.
     */
    public TemplateFactory languagePolicy(String policy) {
        this.languagePolicy = policy;
        return this;
    }

    /**
     * Adds a text parameter to the header component.
     *
     * @param text the header text.
     * @return the current factory instance.
     */
    public TemplateFactory headerText(String text) {
        headerParameters.add(Parameter.builder().type(ParameterType.TEXT).text(text).build());
        return this;
    }

    /**
     * Adds a currency parameter to the header component.
     *
     * @param currency the currency value.
     * @return the current factory instance.
     */
    public TemplateFactory headerCurrency(Currency currency) {
        headerParameters.add(Parameter.builder().type(ParameterType.CURRENCY).currency(currency).build());
        return this;
    }

    /**
     * Adds a date/time parameter to the header component.
     *
     * @param dateTime the date/time value.
     * @return the current factory instance.
     */
    public TemplateFactory headerDateTime(DateTime dateTime) {
        headerParameters.add(Parameter.builder().type(ParameterType.DATETIME).dateTime(dateTime).build());
        return this;
    }

    /**
     * Adds an image parameter to the header component.
     *
     * @param image the image media object.
     * @return the current factory instance.
     */
    public TemplateFactory headerImage(Media image) {
        headerParameters.add(Parameter.builder().type(ParameterType.IMAGE).image(image).build());
        return this;
    }

    /**
     * Adds a document parameter to the header component.
     *
     * @param document the document media object.
     * @return the current factory instance.
     */
    public TemplateFactory headerDocument(Media document) {
        headerParameters.add(Parameter.builder().type(ParameterType.DOCUMENT).document(document).build());
        return this;
    }

    /**
     * Adds a text parameter to the body component. Can be called multiple
     * times; each call appends another parameter to the same body component,
     * in order.
     *
     * @param text the body text.
     * @return the current factory instance.
     */
    public TemplateFactory bodyText(String text) {
        bodyParameters.add(Parameter.builder().type(ParameterType.TEXT).text(text).build());
        return this;
    }

    /**
     * Adds a currency parameter to the body component.
     *
     * @param currency the currency value.
     * @return the current factory instance.
     */
    public TemplateFactory bodyCurrency(Currency currency) {
        bodyParameters.add(Parameter.builder().type(ParameterType.CURRENCY).currency(currency).build());
        return this;
    }

    /**
     * Adds a date/time parameter to the body component.
     *
     * @param dateTime the date/time value.
     * @return the current factory instance.
     */
    public TemplateFactory bodyDateTime(DateTime dateTime) {
        bodyParameters.add(Parameter.builder().type(ParameterType.DATETIME).dateTime(dateTime).build());
        return this;
    }

    /**
     * Adds an image parameter to the body component.
     *
     * @param image the image media object.
     * @return the current factory instance.
     */
    public TemplateFactory bodyImage(Media image) {
        bodyParameters.add(Parameter.builder().type(ParameterType.IMAGE).image(image).build());
        return this;
    }

    /**
     * Adds a document parameter to the body component.
     *
     * @param document the document media object.
     * @return the current factory instance.
     */
    public TemplateFactory bodyDocument(Media document) {
        bodyParameters.add(Parameter.builder().type(ParameterType.DOCUMENT).document(document).build());
        return this;
    }

    /**
     * Adds a text parameter (e.g. a dynamic URL suffix or quick-reply payload)
     * to the button component.
     *
     * @param text the button parameter text.
     * @return the current factory instance.
     */
    public TemplateFactory buttonText(String text) {
        buttonParameters.add(Parameter.builder().type(ParameterType.TEXT).text(text).build());
        return this;
    }

    /**
     * Builds and returns a fully configured {@link Template} object.
     * <p>
     * This method does not validate the resulting object.
     *
     * @return the constructed {@link Template} message.
     */
    public Template build() {
        List<Component> components = new ArrayList<>();

        if (!headerParameters.isEmpty()) {
            components.add(Component.builder().type(ComponentType.HEADER).parameters(headerParameters).build());
        }
        if (!bodyParameters.isEmpty()) {
            components.add(Component.builder().type(ComponentType.BODY).parameters(bodyParameters).build());
        }
        if (!buttonParameters.isEmpty()) {
            components.add(Component.builder().type(ComponentType.BUTTON).parameters(buttonParameters).build());
        }

        return Template.builder()
                .name(name)
                .language(Language.builder().code(languageCode).policy(languagePolicy).build())
                .components(components.isEmpty() ? null : components)
                .build();
    }
}
