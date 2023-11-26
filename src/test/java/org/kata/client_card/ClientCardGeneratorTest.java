package org.kata.client_card;

import org.junit.Test;
import org.kata.dto.*;
import org.kata.dto.enums.ContactMediumType;
import org.kata.dto.enums.DocumentType;
import org.kata.dto.enums.GenderType;
import org.kata.service.client_card.ClientCardCreator;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class ClientCardGeneratorTest {
    @Test
    public void test() throws IOException {
        ClientCardCreator clientCardCreator = new ClientCardCreator();

        var card = clientCardCreator.create(IndividualDto.builder()
                .name("Testname")
                .surname("Testsurname")
                .placeOfBirth("Testplaceofbirth")
                .countryOfBirth("Testcountry")
                .gender(GenderType.MALE)
                .patronymic("Testpatronymic")
                .birthDate(new Date())
                .address(List.of(AddressDto.builder()
                        .city("Testcity")
                        .country("Testcountry")
                        .state("Teststate")
                        .postCode("123456789")
                        .street("Teststreet")
                        .build()))
                .contacts(List.of(ContactMediumDto.builder()
                        .type(ContactMediumType.PHONE)
                        .value("+123-12-123-12-12")
                        .build()
                        ))
                .documents(List.of(DocumentDto.builder()
                        .documentType(DocumentType.RF_PASSPORT)
                        .documentNumber("12345678")
                        .documentSerial("87654321")
                        .expirationDate(new Date())
                        .build()))
                .avatar(List.of(AvatarDto.builder()
                        .imageData(extractImageBytes())
                        .build()))
                .build());
        card.save("src/test/resources/client_card/test.pdf");
        card.close();
    }

    private byte[] extractImageBytes() throws IOException {
        File imgPath = new File("src/test/resources/client_card/test.png");
        BufferedImage bufferedImage = ImageIO.read(imgPath);
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos );
        return baos.toByteArray();
    }


}
