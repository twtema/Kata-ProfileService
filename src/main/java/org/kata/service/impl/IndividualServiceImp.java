package org.kata.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.extern.slf4j.Slf4j;
import org.kata.config.UrlProperties;
import org.kata.dto.*;
import org.kata.dto.enums.GenderType;
import org.kata.exception.DocumentsNotFoundException;
import org.kata.exception.IndividualNotFoundException;
import org.kata.service.GenerateTestValue;
import org.kata.service.IndividualService;
import org.kata.service.KafkaMessageSender;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
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
    public void deduplication(String icporigin,
                              String icpdedublication,
                              String event_dedublication) {

        IndividualDto client1 = getIndividual(icporigin);
        IndividualDto client2 = getIndividual(icpdedublication);
        if (client1.getFullName().equals(client2.getFullName())) {
            log.info("Client's full name matches");
            if (client1.getBirthDate().equals(client2.getBirthDate())) {
                log.info("Сlient's birthday coincides");
                mergedIndividual(client1, client2);
               // deleteIndividual(icpdedublication);

            } else {
                log.info("Сlient's birthday not coincides");
            }
        } else {
            log.info("Client's full name does not match");
        }
    }

    private void deleteIndividual(String icp) {
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
                );
    }


    private IndividualDto mergedIndividual(IndividualDto client1, IndividualDto client2) {
        System.out.println(client1.toString());
        String icp = client1.getIcp();
        log.info("зашли в мердже");
        client1.getAvatar().addAll(client2.getAvatar());
        client1.getDocuments().addAll(client2.getDocuments());
        client1.getAddress().addAll(client2.getAddress());
        client1.getContacts().addAll(client2.getContacts());
        client1.setIcp(icp);
        client1.setPlaceOfBirth(client1.getPlaceOfBirth());
        client1.setBirthDate(client2.getBirthDate());
        client1.getFullName();
        System.out.println(client1.toString());
            loaderWebClient.put()
                    .uri(uriBuilder -> uriBuilder
                            .path(urlProperties.getProfileLoaderUpdateIndividual())
                            .queryParam("icp", client1.getIcp())
                            .build())
                    .body(Mono.just(client1), IndividualDto.class)
                    .retrieve()
                    .onStatus(HttpStatus::isError, response ->
                            Mono.error(new DocumentsNotFoundException(
                                    "Documents with icp " + client1.getIcp() + " not update")
                            )
                    )
                    .bodyToMono(new ParameterizedTypeReference<List<DocumentDto>>() {
                    })
                    .block();
            return getIndividual(client1.getIcp());
//        }
//        return loaderWebClient.post()
//                .uri(uriBuilder -> uriBuilder
//                        .path(urlProperties.getProfileLoaderPostIndividual())
//                        .queryParam("dto", client1)
//                        .build())
//                .retrieve()
//                .onStatus(HttpStatus::isError, response ->
//                        Mono.error(new IndividualNotFoundException(
//                                "Individual with icp " + icp + " not found")
//                        )
//                )
//                .bodyToMono(IndividualDto.class)
//                .block();
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
