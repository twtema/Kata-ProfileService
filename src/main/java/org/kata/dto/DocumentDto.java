package org.kata.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.kata.dto.enums.DocumentType;

import java.util.Date;

@Data
@Builder
@Jacksonized
public class DocumentDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String icp;

    private DocumentType documentType;

    private String documentNumber;

    private String documentSerial;

    private Date issueDate;

    private Date expirationDate;
}
