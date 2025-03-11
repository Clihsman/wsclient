package com.wsclient.cloud.api.messages.request.contact;

import java.util.List;

public record Contact(
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * Specifies an array of address objects. For more information, see address
         * object.
         * </p>
         */
        List<ContactAddress> addresses,
        /**
         * <strong>
         * Optional.
         * <strong>
         * 
         * <p>
         * A YYYY-MM-DD formatted string.
         * </p>
         */
        String birthday,
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * Specifies an array of email objects. For more information, see emails object.
         * </p>
         */
        List<ContactEmail> emails,
        /**
         * <strong>
         * Required.
         * </strong>
         * 
         * <p>
         * Specifies the name object. For more information, see name object.
         * </p>
         */
        ContactName name,
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * Specifies the org object. For more information, see org object.
         * </p>
         */
        ContactOrg org,
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * Specifies an array of phone objects. For more information, see phone object.
         * </p>
         */
        List<ContactPhone> phones,
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * Specifies an array of url objects. For more information, see url object.
         * </p>
         */
        List<ContactUrl> urls) {
}