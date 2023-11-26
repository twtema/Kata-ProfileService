package org.kata.service.client_card.data;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.kata.dto.enums.ContactMediumType;

@Builder
@Jacksonized
public class ContactData {

    private ContactMediumType type;

    private String value;

    @Override
    public String toString() {
        return String.format(
                "%s: %s",
                type, value);
    }
}
