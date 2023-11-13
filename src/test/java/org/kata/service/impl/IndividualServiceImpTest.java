package org.kata.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.kata.config.UrlProperties;
import org.kata.dto.IndividualDto;
import org.kata.dto.enums.EventType;
import org.kata.exception.IndividualNotFoundException;
import org.kata.service.GenerateTestValue;
import org.kata.service.KafkaMessageSender;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Component
@ConfigurationProperties("url-properties")
@Getter
@Setter
@Slf4j
public class IndividualServiceImpTest {
    UrlProperties urlProperties = new UrlProperties();
    KafkaMessageSender kafkaMessageSender;
    GenerateTestValue generateTestValue;

    @Test
    public void TestDeduplication() throws IOException {

        urlProperties.setProfileLoaderBaseUrl("http://localhost:8081/");
        urlProperties.setProfileLoaderPostIndividual("v1/individual");
        urlProperties.setProfileLoaderDeleteIndividual("v1/individual/delete");
        urlProperties.setProfileLoaderGetIndividual("v1/individual");

        IndividualServiceImpTest r = new IndividualServiceImpTest();


        IndividualServiceImp individualService = new IndividualServiceImp(urlProperties, kafkaMessageSender, generateTestValue);
        ObjectMapper mapper = new ObjectMapper();


        // создание 1 клиента
        File file1 = new File("src/test/java/org/kata/service/impl/ind1.json");
        String content = FileUtils.readFileToString(file1, "UTF-8");
        IndividualDto dto1 = mapper.readValue(content, IndividualDto.class);
        log.info("Cчитали с файла клиент 1 - " + dto1);
        individualService.updateIndividual(dto1);
        Assert.assertEquals("Valentin11", individualService.getIndividual("100").getName());
        Assert.assertThrows(IndividualNotFoundException.class, () -> individualService.getIndividual("200"));

        // создание 2 клиента
        File file2 = new File("src/test/java/org/kata/service/impl/ind2.json");
        content = FileUtils.readFileToString(file2, "UTF-8");
        IndividualDto dto2 = mapper.readValue(content, IndividualDto.class);
        log.info("Cчитали с файла клиент 2- " + dto2);
        individualService.updateIndividual(dto2);
        Assert.assertEquals("Valentin11", individualService.getIndividual("200").getName());
        Assert.assertThrows(IndividualNotFoundException.class, () -> individualService
                .getIndividual("300"));

        // слияние клиентов
        Assert.assertEquals("Bobby",
                individualService.deduplication("100", "200", EventType.DEDUPLICATION)
                        .getPatronymic());
        log.info("Cделали слияние - " + individualService.getIndividual("100"));

        Assert.assertThrows(IndividualNotFoundException.class, () -> individualService.getIndividual("200"));
        // удаляем смерженный обьект
        individualService.deleteIndividual("100");
    }
}