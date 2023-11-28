package org.kata.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.kata.config.UrlProperties;
import org.kata.dto.DocumentDto;
import org.kata.exception.DocumentsNotFoundException;
import org.kata.exception.IndividualNotFoundException;
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

    @Override
    public List<DocumentDto> getActualDocument(String icp) {
        if (icp != null) {
            return loaderWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(urlProperties.getProfileLoaderGetDocument())
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
    public List<DocumentDto> getActualDocument(String icp, String type) {
        if (icp == null && type == null) {
            throw new IllegalArgumentException("Not found parameters");
        }
        if (type.equals("uuid")) {
            return loaderWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(urlProperties.getProfileLoaderGetDocument())
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
            return getActualDocument(icp);
        } else {
            throw new IllegalArgumentException("Invalid type");
        }
    }
}
