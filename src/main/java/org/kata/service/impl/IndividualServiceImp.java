package org.kata.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.kata.config.UrlProperties;
import org.kata.dto.IndividualDto;
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

    //в ProfileService
// Должен быть post запрос с телом
//icporigin
//icpdedublication
//event_dedublication
//В сервисе если оба клиента найдены происходит лога проверки пользователей,
// если их ФИО и дата рождения совпадают, то клиенты объединяются в одного, второй удаляется
//
//Если у эталона не было документов, а у второго были,
// они переносятся, если документы совпадают ничего не происходит(все остальные поля аналогично)

    @Override
    public void deduplication(String icporigin,
                              String icpdedublication,
                              String event_dedublication) {

//   TODO
//    # Проверка наличия клиентов по указанным параметрам
//        client1 =
//        client2 =
//        # Проверка совпадения ФИО и даты рождения клиентов
//           Объединение клиентов в одного
//        merged client
//            # Удаление второго клиента
//        delete client(client2)
//        else:
//            # Логика для случая, когда ФИО и дата рождения не совпадают
//            ...
//    # Логика для случая, когда один из клиентов не найден
//    ...

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
