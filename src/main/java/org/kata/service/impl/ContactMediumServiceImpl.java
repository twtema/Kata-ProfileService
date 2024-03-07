package org.kata.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.kata.config.UrlProperties;
import org.kata.dto.ContactMediumDto;
import org.kata.exception.ContactMediumNotFoundException;
import org.kata.exception.IndividualNotFoundException;
import org.kata.service.ContactMediumService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class ContactMediumServiceImpl implements ContactMediumService {
    private final UrlProperties urlProperties;
    private final WebClient loaderWebClient;

    public ContactMediumServiceImpl(UrlProperties urlProperties) {
        this.urlProperties = urlProperties;
        this.loaderWebClient = WebClient.create(urlProperties.getProfileLoaderBaseUrl());
    }

    public List<ContactMediumDto> getActualContactMedium(String icp, String conversationId) {
        if (icp != null) {
            return loaderWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(urlProperties.getProfileLoaderGetContactMedium())
                            .queryParam("id", icp)
                            .build())
                    .header("conversationId", conversationId)
                    .retrieve()
                    .onStatus(HttpStatus::isError, response ->
                            Mono.error(new ContactMediumNotFoundException(
                                    "ContactMedium with icp " + icp + " not found")
                            )
                    )
                    .bodyToMono(new ParameterizedTypeReference<List<ContactMediumDto>>() {
                    })
                    .block();
        } else {
            throw new IndividualNotFoundException("Not found individual");
        }
    }

    @Override
    public List<ContactMediumDto> getActualContactMedium(String icp, String uuid, String conversationId) {
        if (icp == null && uuid == null) {
            throw new IllegalArgumentException("Not found parameters");
        }
        if (uuid.equals("uuid")) {
            return loaderWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(urlProperties.getProfileLoaderGetContactMedium())
                            .queryParam("id", icp)
                            .queryParam("type", uuid)
                            .build())
                    .header("conversationId", conversationId)
                    .retrieve()
                    .onStatus(HttpStatus::isError, response ->
                            Mono.error(new ContactMediumNotFoundException(
                                    "ContactMedium with icp " + icp + " not found")
                            )
                    )
                    .bodyToMono(new ParameterizedTypeReference<List<ContactMediumDto>>() {
                    })
                    .block();
        } else if (uuid.isEmpty()) {
            return getActualContactMedium(icp, conversationId);
        } else {
            throw new IllegalArgumentException("Invalid type");
        }
    }
}
