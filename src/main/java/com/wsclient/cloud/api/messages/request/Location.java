package com.wsclient.cloud.api.messages.request;

/**
 * Location
 * 
 * @param longitude longitude
 * @param latitude  latitude
 * @param name      name
 * @param address   address
 */
public record Location(
        /**
         * <strong>
         * Required.
         * </strong>
         * 
         * <p>
         * The longitude of the location.
         * </p>
         */
        String longitude,
        /**
         * <strong>
         * Required.
         * </strong>
         * 
         * <p>
         * The latitude of the location.
         * </p>
         */
        String latitude,
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * The name of the location.
         * </p>
         */
        String name,
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * The address of the location. This field is only displayed if name is present.
         * </p>
         */
        String address) {
}
