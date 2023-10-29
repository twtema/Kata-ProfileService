package org.kata.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.kata.dto.enums.ContactMediumType;


@Data
@Builder
@Jacksonized
public class ContactMediumDto {

    @Schema(description = "ICP", example = "1234567890")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String icp;

    @Schema(description = "Контакт")
    private ContactMediumType type;

    @Schema(description = "Значение", example = "contact@gmail.com")
    private String value;
}