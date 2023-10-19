package org.kata.service;

import com.github.javafaker.Faker;
import org.kata.dto.AddressDto;
import org.kata.dto.ContactMediumDto;
import org.kata.dto.DocumentDto;
import org.kata.dto.IndividualDto;
import org.kata.dto.enums.ContactMediumType;
import org.kata.dto.enums.DocumentType;
import org.kata.dto.enums.GenderType;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class GenerateTestValue {

    private final Faker faker = new Faker();

    public IndividualDto generateRandomUser() {
        String icp = faker.idNumber().valid();
        return IndividualDto.builder()
                .icp(icp)
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .patronymic(faker.name().firstName())
                .fullName(faker.name().fullName())
                .gender(getRandomGender())
                .placeOfBirth(faker.address().city())
                .countryOfBirth(faker.address().country())
                .birthDate(faker.date().birthday())
                .documents(generateRandomDocuments(icp))
                .contacts(generateRandomContacts(icp))
                .address(generateRandomAddresses(icp))
                .build();
    }

    private GenderType getRandomGender() {
        Random random = new Random();
        int index = random.nextInt(GenderType.values().length);
        return GenderType.values()[index];
    }

    private List<DocumentDto> generateRandomDocuments(String icp) {
        var doc1 = generateRandomDocument(icp);
        var doc2 = generateRandomDocument(icp);
        return List.of(doc1, doc2);
    }

    private DocumentDto generateRandomDocument(String icp) {
        DocumentType documentType = getRandomDocumentType();
        String documentNumber = faker.number().digits(8);
        String documentSerial = faker.number().digits(4);
        Date issueDate = faker.date().birthday();
        Date expirationDate = faker.date().future(10, TimeUnit.DAYS);

        return DocumentDto.builder()
                .icp(icp)
                .documentType(documentType)
                .documentNumber(documentNumber)
                .documentSerial(documentSerial)
                .issueDate(issueDate)
                .expirationDate(expirationDate)
                .build();
    }

    private DocumentType getRandomDocumentType() {
        DocumentType[] documentTypes = DocumentType.values();
        int randomIndex = faker.random().nextInt(documentTypes.length);
        return documentTypes[randomIndex];
    }

    private List<ContactMediumDto> generateRandomContacts(String icp) {
        return List.of(generateRandomContactMedium(icp));
    }

    private ContactMediumDto generateRandomContactMedium(String icp) {
        ContactMediumType type = getRandomContactMediumType();
        String value = generateContactMediumValue(type);

        return ContactMediumDto.builder()
                .icp(icp)
                .type(type)
                .value(value)
                .build();
    }

    private ContactMediumType getRandomContactMediumType() {
        ContactMediumType[] contactMediumTypes = ContactMediumType.values();
        int randomIndex = faker.random().nextInt(contactMediumTypes.length);
        return contactMediumTypes[randomIndex];
    }

    private String generateContactMediumValue(ContactMediumType type) {
        if (type == ContactMediumType.EMAIL) {
            return faker.internet().emailAddress();
        } else if (type == ContactMediumType.PHONE) {
            return faker.phoneNumber().phoneNumber();
        }
        return null;
    }

    private List<AddressDto> generateRandomAddresses(String icp) {
        return List.of(AddressDto.builder()
                .icp(icp)
                .street(faker.address().streetAddress())
                .city(faker.address().city())
                .state(faker.address().state())
                .postCode(faker.address().zipCode())
                .country(faker.address().country())
                .build());
    }

}
