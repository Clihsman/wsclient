package com.wsclient.api.messages.request.interactive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a row in an interactive section for WhatsApp messages.
 * This record is used to define selectable options within a section.
 * 
 * @param id          Unique identifier for the row (Maximum length: 200
 *                    characters).
 * @param title       Display title of the row (Maximum length: 24 characters).
 * @param description Additional description for the row (Maximum length: 72
 *                    characters).
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InteractiveSectionRow {
        private String id;
        private String title;
        private String description;
}
