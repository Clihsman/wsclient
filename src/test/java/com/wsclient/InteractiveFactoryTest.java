package com.wsclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.wsclient.api.messages.factory.InteractiveFactory;
import com.wsclient.api.messages.request.interactive.Interactive;
import com.wsclient.api.messages.request.interactive.Interactive.InteractiveType;
import com.wsclient.api.messages.request.interactive.InteractiveSection;

public class InteractiveFactoryTest {

    @Test
    void createButton_BuildsExpectedStructure() {
        Interactive interactive = InteractiveFactory.createButton()
                .text("Confirm?")
                .button("yes", "Yes")
                .button("no", "No")
                .build();

        assertEquals(InteractiveType.BUTTON, interactive.getType());
        assertEquals("Confirm?", interactive.getBody().getText());

        List<com.wsclient.api.messages.request.interactive.InteractiveButton> buttons = interactive.getAction()
                .getButtons();
        assertEquals(2, buttons.size());
        assertEquals("yes", buttons.get(0).getReply().getId());
        assertEquals("Yes", buttons.get(0).getReply().getTitle());
        assertEquals("no", buttons.get(1).getReply().getId());
        assertEquals("No", buttons.get(1).getReply().getTitle());
    }

    @Test
    void createList_WithMultipleSectionsAndRows_BuildsExpectedStructure() {
        Interactive interactive = InteractiveFactory.createList()
                .text("Choose an option:")
                .listButton("View options")
                .section("Main")
                .row("1", "Option A", "Description A")
                .row("2", "Option B")
                .section("Other")
                .row("3", "Option C")
                .build();

        assertEquals(InteractiveType.LIST, interactive.getType());
        assertEquals("Choose an option:", interactive.getBody().getText());
        assertEquals("View options", interactive.getAction().getButton());

        List<InteractiveSection> sections = interactive.getAction().getSections();
        assertEquals(2, sections.size());

        assertEquals("Main", sections.get(0).getTitle());
        assertEquals(2, sections.get(0).getRows().size());
        assertEquals("1", sections.get(0).getRows().get(0).getId());
        assertEquals("Option A", sections.get(0).getRows().get(0).getTitle());
        assertEquals("Description A", sections.get(0).getRows().get(0).getDescription());
        assertEquals("2", sections.get(0).getRows().get(1).getId());
        assertNull(sections.get(0).getRows().get(1).getDescription());

        assertEquals("Other", sections.get(1).getTitle());
        assertEquals(1, sections.get(1).getRows().size());
        assertEquals("3", sections.get(1).getRows().get(0).getId());
    }

    @Test
    void row_WithoutExplicitSection_CreatesDefaultSection() {
        Interactive interactive = InteractiveFactory.createList()
                .text("Choose:")
                .listButton("Open")
                .row("1", "Option A")
                .build();

        List<InteractiveSection> sections = interactive.getAction().getSections();
        assertEquals(1, sections.size());
        assertEquals("default", sections.get(0).getTitle());
        assertEquals(1, sections.get(0).getRows().size());
    }
}
