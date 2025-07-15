package com.wsclient.api.messages.request.interactive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a row in an interactive section for WhatsApp messages.
 * This record is used to define selectable options within a section.
 * 
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InteractiveSectionRow {
        /**
         * Unique identifier for the row (Maximum length: 200
         */
        private String id;

        /**
         * Display title of the row (Maximum length: 24 characters)
         */
        private String title;

        /**
         * Additional description for the row (Maximum length: 72
         * characters).
         */
        private String description;
}
