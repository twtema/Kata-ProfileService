package org.kata.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.kata.config.UrlProperties;
import org.kata.dto.*;
import org.kata.exception.IndividualNotFoundException;
import org.kata.service.GenerateTestValue;
import org.kata.service.IndividualService;
import org.kata.service.KafkaMessageSender;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.stream.IntStream;

@Service
@Slf4j
public class IndividualServiceImp implements IndividualService {
    private final UrlProperties urlProperties;
    private final KafkaMessageSender kafkaMessageSender;
    private final GenerateTestValue generateTestValue;
    private final WebClient loaderWebClient;

    public IndividualServiceImp(UrlProperties urlProperties,
                                KafkaMessageSender kafkaMessageSender,
                                GenerateTestValue generateTestValue) {
        this.kafkaMessageSender = kafkaMessageSender;
        this.generateTestValue = generateTestValue;
        this.urlProperties = urlProperties;
        this.loaderWebClient = WebClient.create(urlProperties.getProfileLoaderBaseUrl());
    }

    public IndividualDto getIndividual(String icp) {
        return loaderWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(urlProperties.getProfileLoaderGetIndividual())
                        .queryParam("icp", icp)
                        .build())
                .retrieve()
                .onStatus(HttpStatus::isError, response ->
                        Mono.error(new IndividualNotFoundException(
                                "Individual with icp " + icp + " not found")
                        )
                )
                .bodyToMono(IndividualDto.class)
                .block();
    }


    @Override
    public IndividualDto deduplication(String icporigin,
                                       String icpdedublication,
                                       String event_dedublication) {

        IndividualDto client1 = getIndividual(icporigin);
        IndividualDto client2 = getIndividual(icpdedublication);
        // Проверка ФИО
        if (client1.getName().equals(client2.getName())
                && client1.getSurname().equals(client2.getSurname())
                && client1.getPatronymic().equals(client2.getPatronymic())) {
            log.info("Client's full name matches");
            // Проверка Дня рождения
            if (client1.getBirthDate().equals(client2.getBirthDate())) {
                log.info("Сlient's birthday coincides");
                // создаем Обьект  для слияния
                IndividualDto mergedDto = mergedIndividual(client1, client2);
                // Удаляем старые записи
                deleteIndividual(icporigin);
                deleteIndividual(icpdedublication);
                // Создаем новую
                updateIndividual(mergedDto);

            } else {
                log.info("Сlient's birthday not coincides");
            }
        } else {
            log.info("Client's full name does not match");
        }
        return getIndividual(icporigin);
    }

    public IndividualDto updateIndividual(IndividualDto dto) {
        return loaderWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(urlProperties.getProfileLoaderPostIndividual())
                        .build())
                .body(Mono.just(dto), IndividualDto.class)
                .retrieve()
                .onStatus(HttpStatus::isError, response ->
                        Mono.error(new IndividualNotFoundException(
                                "Individual with icp " + dto.getIcp() + " not update")
                        )
                )
                .bodyToMono(IndividualDto.class)
                .block();
    }


    private void deleteIndividual(String icp) {
        System.out.println(urlProperties.getProfileLoaderDeleteIndividual());
        loaderWebClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(urlProperties.getProfileLoaderDeleteIndividual())
                        .queryParam("icp", icp)
                        .build())
                .retrieve()
                .onStatus(HttpStatus::isError, response ->
                        Mono.error(new IndividualNotFoundException(
                                "Individual with icp " + icp + " not found")
                        )
                )
                .bodyToMono(HttpStatus.class)
                .block();
    }


    private IndividualDto mergedIndividual(IndividualDto client1, IndividualDto client2) {
        System.out.println(client1.toString());
        String icp = client1.getIcp();
        log.info("зашли в мердже");
        IndividualDto mergedDto = client1;
        // обьединяем списки
        mergedDto.getAvatar().addAll(client2.getAvatar());
        mergedDto.getDocuments().addAll(client2.getDocuments());
        mergedDto.getAddress().addAll(client2.getAddress());
        mergedDto.getContacts().addAll(client2.getContacts());
        mergedDto.setPlaceOfBirth(client1.getPlaceOfBirth());
        mergedDto.setBirthDate(client2.getBirthDate());

        // убираем копии
        mergedDto.setAddress(mergedDto.getAddress().stream().distinct().toList());
        mergedDto.setAvatar(mergedDto.getAvatar().stream().distinct().toList());
        mergedDto.setDocuments(mergedDto.getDocuments().stream().distinct().toList());
        mergedDto.setContacts(mergedDto.getContacts().stream().distinct().toList());
        System.out.println(mergedDto.toString());
        return mergedDto;
    }


    public void createTestIndividual(int n) {
        IntStream.range(0, n)
                .forEach(i -> {
                    var dto = generateTestValue.generateRandomUser();
                    kafkaMessageSender.sendMessage(dto);
                    log.info("Create Individual with icp:{}", dto.getIcp());
                });
    }

}
