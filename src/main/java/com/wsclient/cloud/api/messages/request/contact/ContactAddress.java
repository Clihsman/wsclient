package com.wsclient.cloud.api.messages.request.contact;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ContactAddress
 * 
 * @param street      street
 * @param city        city
 * @param state       state
 * @param zip         zip
 * @param country     country
 * @param countryCode countryCode
 * @param type        type
 */
public record ContactAddress(
                /**
                 * <strong>
                 * Optional.
                 * </strong>
                 * 
                 * <p>
                 * Steet number and name.
                 * </p>
                 */
                String street,
                /**
                 * <strong>
                 * Optional.
                 * </strong>
                 * 
                 * <p>
                 * The name of the city.
                 * </p>
                 */
                String city,
                /**
                 * <strong>
                 * Optional.
                 * </strong>
                 * 
                 * <p>
                 * The abbreviation name of the state.
                 * </p>
                 */
                String state,
                /**
                 * <strong>
                 * Optional.
                 * </strong>
                 * 
                 * <p>
                 * The ZIP code.
                 * </p>
                 */
                String zip,
                /**
                 * <strong>
                 * Optional.
                 * </strong>
                 * 
                 * <p>
                 * The full name of the country.
                 * </p>
                 */
                String country,
                /**
                 * <strong>
                 * Optional.
                 * </strong>
                 * 
                 * <p>
                 * The <code>two-letter</code> country abbreviation.
                 * </p>
                 */
                @JsonProperty("country_code") String countryCode,
                /**
                 * <strong>
                 * Optional.
                 * </strong>
                 * 
                 * <p>
                 * Standard values: <code>HOME</code>, <code>WORK</code>.
                 * </p>
                 */
                ContactStandardType type) {
}