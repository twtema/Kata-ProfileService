package org.kata.service.client_card.data;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.kata.dto.enums.GenderType;

import java.util.Date;
@Builder
@Jacksonized
public class MainData {

    private String name;
    private String surname;
    private String patronymic;
    private GenderType gender;
    private String countryOfBirth;
    private Date birthDate;

    public String[] getData () {
        return new String[] {
                String.format("Name: %s", name),
                String.format("Surname: %s", surname),
                String.format("Patronymic: %s", patronymic),
                String.format("Gender: %s", gender),
                String.format("Place of Birth: %s", countryOfBirth),
                String.format("Date of birth: %s", birthDate),
        };
    }

}
