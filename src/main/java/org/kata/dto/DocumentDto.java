package org.kata.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.kata.dto.enums.DocumentType;

import java.util.Date;

@Data
@Builder
@Jacksonized
public class DocumentDto {

    @Schema(description = "ICP", example = "1234567890")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String icp;

    @Schema(description = "Тип документа")
    private DocumentType documentType;

    @Schema(description = "Номер документа", example = "1234567890")
    private String documentNumber;

    @Schema(description = "Серийный номер документа", example = "1234567890")
    private String documentSerial;

    @Schema(description = "Дата создания документа", example = "2020-01-01")
    private Date issueDate;

    @Schema(description = "Дата окончания срока документа", example = "2030-04-10")
    private Date expirationDate;
}