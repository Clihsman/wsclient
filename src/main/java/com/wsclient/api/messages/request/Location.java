package com.wsclient.api.messages.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Location
 * 
 * @param longitude longitude
 * @param latitude  latitude
 * @param name      name
 * @param address   address
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {
        /**
         * <strong>
         * Required.
         * </strong>
         * 
         * <p>
         * The longitude of the location.
         * </p>
         */
        private String longitude;
        /**
         * <strong>
         * Required.
         * </strong>
         * 
         * <p>
         * The latitude of the location.
         * </p>
         */
        private String latitude;
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * The name of the location.
         * </p>
         */
        private String name;
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * The address of the location. This field is only displayed if name is present.
         * </p>
         */
        private String address;
}
