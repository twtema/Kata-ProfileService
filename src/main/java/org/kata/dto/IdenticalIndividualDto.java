package org.kata.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@EqualsAndHashCode
public class IdenticalIndividualDto {

    @Schema(description = "Имя", example = "Иван")
    private String name;

    @Schema(description = "Фамилия", example = "Иванов")
    private String surname;

    @Schema(description = "Отчество", example = "Иванович")
    private String patronymic;

    @Schema(description = "Дата рождения", example = "1990-01-01")
    private Date birthDate;


}
