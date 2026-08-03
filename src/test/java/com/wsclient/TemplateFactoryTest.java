package com.wsclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsclient.api.messages.factory.TemplateFactory;
import com.wsclient.api.messages.request.Component;
import com.wsclient.api.messages.request.DateTime;
import com.wsclient.api.messages.request.Template;

public class TemplateFactoryTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Test
    void build_GroupsParametersByComponent_InFixedOrder() {
        Template template = TemplateFactory.create("order_confirmation", "es_CO")
                .buttonText("order-123") // called out of order on purpose
                .bodyText("John Doe")
                .bodyText("order-123")
                .headerText("Order confirmed")
                .build();

        assertEquals("order_confirmation", template.getName());
        assertEquals("es_CO", template.getLanguage().getCode());
        assertEquals(3, template.getComponents().size());

        Component header = template.getComponents().get(0);
        Component body = template.getComponents().get(1);
        Component button = template.getComponents().get(2);

        assertEquals(Component.ComponentType.HEADER, header.getType());
        assertEquals(1, header.getParameters().size());

        assertEquals(Component.ComponentType.BODY, body.getType());
        assertEquals(2, body.getParameters().size());
        assertEquals("John Doe", body.getParameters().get(0).getText());
        assertEquals("order-123", body.getParameters().get(1).getText());

        assertEquals(Component.ComponentType.BUTTON, button.getType());
        assertEquals(1, button.getParameters().size());
    }

    @Test
    void build_OmitsComponentsWithNoParameters() {
        Template template = TemplateFactory.create("simple_template", "en_US")
                .bodyText("Hello")
                .build();

        assertEquals(1, template.getComponents().size());
        assertEquals(Component.ComponentType.BODY, template.getComponents().get(0).getType());
    }

    @Test
    void componentParameters_SerializeAsJsonArray() throws Exception {
        Template template = TemplateFactory.create("order_confirmation", "es_CO")
                .bodyText("John Doe")
                .build();

        String json = OBJECT_MAPPER.writeValueAsString(template.getComponents().get(0));

        assertTrue(json.contains("\"parameters\":["), "parameters must serialize as a JSON array: " + json);
        assertFalse(json.contains("\"parameters\":{"), "parameters must not serialize as a JSON object: " + json);
    }

    @Test
    void dateTime_SerializesYearField_NotYaer() throws Exception {
        DateTime dateTime = DateTime.builder()
                .fallbackValue("2026-01-01")
                .year(2026)
                .build();

        String json = OBJECT_MAPPER.writeValueAsString(dateTime);

        assertTrue(json.contains("\"year\":2026"), "expected \"year\" field in JSON: " + json);
        assertFalse(json.contains("yaer"), "the \"yaer\" typo must not appear in JSON: " + json);
    }
}
