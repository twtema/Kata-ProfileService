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
        if (icp != null) {
            return loaderWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(urlProperties.getProfileLoaderGetIndividual())
                            .queryParam("id", icp)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatus::isError, response ->
                            Mono.error(new IndividualNotFoundException(
                                    "Individual with icp " + icp + " not found")
                            )
                    )
                    .bodyToMono(IndividualDto.class)
                    .block();

        } else {
            throw new IllegalArgumentException("ERROR");
        }
    }

    @Override
    public IndividualDto getIndividual(String icp, String type) {
        if (icp == null && type == null) {
            throw new IllegalArgumentException("Not found parameters");
        }
        if (type.equals("uuid")) {
            return loaderWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(urlProperties.getProfileLoaderGetIndividual())
                            .queryParam("id", icp)
                            .queryParam("type", type)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatus::isError, response ->
                            Mono.error(new IndividualNotFoundException(
                                    "Individual with icp " + icp + " not found")
                            )
                    )
                    .bodyToMono(IndividualDto.class)
                    .block();
        } else if (type.isEmpty()) {
            return getIndividual(icp);
        } else {
            throw new IllegalArgumentException("Invalid type");
        }
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


