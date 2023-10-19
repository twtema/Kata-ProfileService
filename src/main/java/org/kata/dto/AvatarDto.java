package org.kata.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class AvatarDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String icp;

    private String filename;

    private byte[] imageData;
}
