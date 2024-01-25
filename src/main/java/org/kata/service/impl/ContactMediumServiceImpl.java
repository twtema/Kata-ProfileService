package org.kata.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.kata.config.UrlProperties;
import org.kata.dto.ContactMediumDto;
import org.kata.dto.RequestContactMediumDto;
import org.kata.exception.ContactMediumNotFoundException;
import org.kata.exception.IndividualNotFoundException;
import org.kata.service.ContactMediumService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ContactMediumServiceImpl implements ContactMediumService {

    private final UrlProperties urlProperties;
    private final WebClient loaderWebClient;

    public ContactMediumServiceImpl(UrlProperties urlProperties) {
        this.urlProperties = urlProperties;
        this.loaderWebClient = WebClient.create(urlProperties.getProfileLoaderBaseUrl());
    }

    public List<ContactMediumDto> getActualContactMedium(RequestContactMediumDto dto) {
        if (dto.getIcp() != null) {
            return loaderWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(urlProperties.getProfileLoaderGetContactMedium())
                            .queryParam("icp", dto.getIcp())
                            .queryParamIfPresent("type", Optional.ofNullable(dto.getType()))
                            .queryParamIfPresent("usage", Optional.ofNullable(dto.getUsage()))
                            .build())
                    .retrieve()
                    .onStatus(HttpStatus::isError, response ->
                            Mono.error(new ContactMediumNotFoundException(
                                    "ContactMedium with parameters for icp " + dto.getIcp() + " not found")
                            )
                    )
                    .bodyToMono(new ParameterizedTypeReference<List<ContactMediumDto>>() {
                    })
                    .block();
        } else {
            throw new IndividualNotFoundException("Not found individual");
        }
    }
}
