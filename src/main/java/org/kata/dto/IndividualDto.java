package org.kata.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.kata.dto.enums.GenderType;

import java.util.Date;
import java.util.List;

@Data
@Builder
@Jacksonized
public class IndividualDto {

    @Schema(description = "ICP", example = "1234567890")
    private String icp;

    @Schema(description = "Имя", example = "Иван")
    private String name;

    @Schema(description = "Фамилия", example = "Иванов")
    private String surname;

    @Schema(description = "Отчество", example = "Иванович")
    private String patronymic;

    @Schema(description = "Полное имя", example = "Иванов Иван Иванович")
    private String fullName;

    @Schema(description = "Пол")
    private GenderType gender;

    @Schema(description = "Место рождения", example = "Москва")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String placeOfBirth;

    @Schema(description = "Страна рождения", example = "Россия")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String countryOfBirth;

    @Schema(description = "Дата рождения", example = "1990-01-01")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date birthDate;

    @Schema(description = "Документы")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<DocumentDto> documents;

    @Schema(description = "Контакты")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ContactMediumDto> contacts;

    @Schema(description = "Адреса")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AddressDto> address;

    @Schema(description = "Аватары")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AvatarDto> avatar;
}