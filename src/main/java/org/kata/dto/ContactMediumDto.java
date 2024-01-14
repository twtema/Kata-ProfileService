package org.kata.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.kata.dto.enums.ContactMediumType;
import org.kata.dto.enums.ContactMediumUsage;


@Data
@Builder
@Jacksonized
public class ContactMediumDto {

    @Schema(description = "ICP", example = "1234567890")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String icp;

    @Schema(description = "Тип контакта", implementation = ContactMediumType.class, example = "EMAIL")
    private ContactMediumType type;

    @Schema(description = "Использование контакта", example = "BUSINESS")
    private ContactMediumUsage usage;

    @Schema(description = "Значение", example = "contact@gmail.com")
    private String value;
}