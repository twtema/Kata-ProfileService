package org.kata.service.client_card.data;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public class AddressData {

    private String street;

    private String city;

    private String state;

    private String postCode;

    private String country;

    @Override
    public String toString() {
        return String.format(
                "%s, %s, %s, %s, %s",
                country, state, city, street, postCode);
    }
}
