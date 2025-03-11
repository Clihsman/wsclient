package com.wsclient.cloud.api.messages.request.contact;

public record ContactOrg(
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * Name of the contact's company.
         * </p>
         */
        String company,
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * Name of the contact's department.
         * </p>
         */
        String department,
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * The contact's business title.
         * </p>
         */
        String title) {
}
