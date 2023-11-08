package org.kata.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class AddressDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "ICP", example = "1234567890")
    private String icp;

    @Schema(description = "Улица", example = "Петрозаводская")
    private String street;

    @Schema(description = "Город", example = "Москва")
    private String city;

    @Schema(description = "Область", example = "Московская")
    private String state;

    @Schema(description = "Почтовый код", example = "123457890")
    private String postCode;

    @Schema(description = "Страна", example = "Россия")
    private String country;
}