package org.kata.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.kata.config.UrlProperties;
import org.kata.dto.ContactMediumDto;
import org.kata.dto.enums.ContactMediumType;
import org.kata.dto.enums.ContactMediumUsage;
import org.kata.exception.ContactMediumNotFoundException;
import org.kata.exception.IndividualNotFoundException;
import org.kata.service.ContactMediumService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
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

    public List<ContactMediumDto> getActualContactMedium(String icp) {
        if (icp != null) {
            return loaderWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(urlProperties.getProfileLoaderGetContactMedium())
                            .queryParam("id", icp)
                            .build())
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
    public List<ContactMediumDto> getActualContactMediumByType(String icp, String type) {
        List<String> allTypes = Arrays.stream(ContactMediumType.values())
                .map(Enum::toString)
                .toList();
        if (icp == null) {
            throw new IllegalArgumentException("Not found parameters");
        }
        if (allTypes.contains(type)) {
            return loaderWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(urlProperties.getProfileLoaderGetContactMedium())
                            .queryParam("id", icp)
                            .queryParam("type", type)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatus::isError, response ->
                            Mono.error(new ContactMediumNotFoundException(
                                    "ContactMedium with icp " + icp + " not found")
                            )
                    )
                    .bodyToMono(new ParameterizedTypeReference<List<ContactMediumDto>>() {
                    })
                    .block();
        } else if (type.isEmpty()) {
            return getActualContactMedium(icp);
        } else {
            throw new IllegalArgumentException("Invalid type");
        }
    }

    @Override
    public List<ContactMediumDto> getActualContactMediumByUsage(String icp, String usage) {
        List<String> allUsages = Arrays.stream(ContactMediumUsage.values())
                .map(Enum::toString)
                .toList();
        if (icp == null) {
            throw new IllegalArgumentException("Not found parameters");
        }
        if (allUsages.contains(usage)) {
            return loaderWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(urlProperties.getProfileLoaderGetContactMedium())
                            .queryParam("id", icp)
                            .queryParam("usage", usage)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatus::isError, response ->
                            Mono.error(new ContactMediumNotFoundException(
                                    "ContactMedium with icp " + icp + " not found")
                            )
                    )
                    .bodyToMono(new ParameterizedTypeReference<List<ContactMediumDto>>() {
                    })
                    .block();
        } else if (usage.isEmpty()) {
            return getActualContactMedium(icp);
        } else {
            throw new IllegalArgumentException("Invalid usage");
        }
    }

    @Override
    public List<ContactMediumDto> getActualContactMediumByTypeAndUsage(String icp, String type, String usage) {
        List<String> allTypes = Arrays.stream(ContactMediumType.values())
                .map(Enum::toString)
                .toList();
        List<String> allUsages = Arrays.stream(ContactMediumUsage.values())
                .map(Enum::toString)
                .toList();
        if (icp == null) {
            throw new IllegalArgumentException("Not found parameters");
        }
        if (!allTypes.contains(type)) {
            throw new IllegalArgumentException("Invalid type");
        }
        if (!allUsages.contains(usage)) {
            throw new IllegalArgumentException("Invalid usage");
        }
        if (allTypes.contains(type) && allUsages.contains(usage)) {
            return loaderWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(urlProperties.getProfileLoaderGetContactMedium())
                            .queryParam("id", icp)
                            .queryParam("type", type)
                            .queryParam("usage", usage)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatus::isError, response ->
                            Mono.error(new ContactMediumNotFoundException(
                                    "ContactMedium with icp " + icp + " not found")
                            )
                    )
                    .bodyToMono(new ParameterizedTypeReference<List<ContactMediumDto>>() {
                    })
                    .block();
        } else if (!type.isEmpty() && usage.isEmpty()) {
            return getActualContactMediumByType(icp, type);
        } else if (!usage.isEmpty() && type.isEmpty()) {
            return getActualContactMediumByUsage(icp, usage);
        } else {
            return getActualContactMedium(icp);
        }
    }
}
