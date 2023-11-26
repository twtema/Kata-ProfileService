package org.kata.service.client_card.data;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.kata.dto.enums.DocumentType;

import java.text.SimpleDateFormat;
import java.util.Date;

@Builder
@Jacksonized
public class DocumentData {

    private DocumentType documentType;

    private String documentSerial;

    private Date expirationDate;

    @Override
    public String toString() {
        return String.format("%s: Serial: %s; Expiration date: %s",
                (documentType
                        .toString()
                        .replace("_", " ")),
                documentSerial,
                new SimpleDateFormat("dd/MM/yyyy").format(expirationDate));
    }
}
