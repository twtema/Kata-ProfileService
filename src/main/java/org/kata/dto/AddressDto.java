package org.kata.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class AddressDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String icp;

    private String street;

    private String city;

    private String state;

    private String postCode;

    private String country;

}
