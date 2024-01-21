package org.kata.util;


import org.kata.dto.ContactMediumDto;
import org.kata.dto.enums.ContactMediumType;


import java.util.List;

public class PhoneNumberValidator {

    public static void validatePhoneNumbers(List<ContactMediumDto> contacts) {
        for (ContactMediumDto contact : contacts) {
            if (ContactMediumType.PHONE.equals(contact.getType())) {
                if (!isValidPhoneNumber(contact.getValue())) {
                    throw new RuntimeException("Invalid phone number");
                }
            }
        }
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && (phoneNumber.matches("^\\+7|^8\\d{10}$"));
    }
}