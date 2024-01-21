package org.kata.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.kata.config.UrlProperties;
import org.kata.dto.DocumentDto;
import org.kata.exception.DocumentsNotFoundException;
import org.kata.exception.IndividualNotFoundException;
import org.kata.service.DocumentService;
import org.kata.service.GenerateTestValue;
import org.kata.service.KafkaMessageSender;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class DocumentServiceImpl implements DocumentService {
    private final UrlProperties urlProperties;
    private final KafkaMessageSender kafkaMessageSender;
    private final GenerateTestValue generateTestValue;
    private final WebClient loaderWebClient;

    public DocumentServiceImpl(UrlProperties urlProperties, KafkaMessageSender kafkaMessageSender, GenerateTestValue generateTestValue) {
        this.urlProperties = urlProperties;
        this.loaderWebClient = WebClient.create(urlProperties.getProfileLoaderBaseUrl());
        this.kafkaMessageSender = kafkaMessageSender;
        this.generateTestValue = generateTestValue;
    }

    @Override
    public List<DocumentDto> getAllDocuments(String icp) {
        if (icp != null) {
            return loaderWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(urlProperties.getProfileLoaderGetAllDocuments())
                            .queryParam("id", icp)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatus::isError, response ->
                            Mono.error(new DocumentsNotFoundException(
                                    "Documents with icp " + icp + " not found")
                            )
                    )
                    .bodyToMono(new ParameterizedTypeReference<List<DocumentDto>>() {
                    })
                    .block();
        } else {
            throw new IndividualNotFoundException("Not found individual");
        }
    }
    @Override
    public List<DocumentDto> getAllDocuments(String icp, String type) {
        if (icp == null && type == null) {
            throw new IllegalArgumentException("Not found parameters");
        }
        if (type.equals("uuid")) {
            return loaderWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(urlProperties.getProfileLoaderGetAllDocuments())
                            .queryParam("id", icp)
                            .queryParam("type", type)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatus::isError, response ->
                            Mono.error(new DocumentsNotFoundException(
                                    "Documents with icp " + icp + " not found")
                            )
                    )
                    .bodyToMono(new ParameterizedTypeReference<List<DocumentDto>>() {
                    })
                    .block();
        } else if (type.isEmpty()) {
            return getAllDocuments(icp);
        } else {
            throw new IllegalArgumentException("Invalid type");
        }
    }

    @Override
    public void createTestDocument(String icp) {
        DocumentDto dto = generateTestValue.generateRandomUser().getDocuments().get(0);
        dto.setIcp(icp);
        kafkaMessageSender.sendMessage(dto);
        log.info("Create Document with icp:{}", dto.getIcp());
    }
}
