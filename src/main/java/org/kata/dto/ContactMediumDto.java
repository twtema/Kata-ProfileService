package org.kata.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.kata.dto.enums.ContactMediumType;


@Data
@Builder
@Jacksonized
public class ContactMediumDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String icp;

    private ContactMediumType type;

    private String value;
}
