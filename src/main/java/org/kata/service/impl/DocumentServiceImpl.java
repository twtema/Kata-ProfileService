package org.kata.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.kata.config.UrlProperties;
import org.kata.dto.DocumentDto;
import org.kata.exception.DocumentsNotFoundException;
import org.kata.service.DocumentService;
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
    private final WebClient loaderWebClient;

    public DocumentServiceImpl(UrlProperties urlProperties) {
        this.urlProperties = urlProperties;
        this.loaderWebClient = WebClient.create(urlProperties.getProfileLoaderBaseUrl());
    }

    public List<DocumentDto> getActualDocuments(String icp) {
        return loaderWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(urlProperties.getProfileLoaderGetDocuments())
                        .queryParam("icp", icp)
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
    }
}
