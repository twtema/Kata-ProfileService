package org.kata.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.kata.config.UrlProperties;
import org.kata.dto.AvatarDto;
import org.kata.exception.AvatarNotFoundException;
import org.kata.service.AvatarService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AvatarServiceImpl implements AvatarService {
    private final UrlProperties urlProperties;
    private final WebClient loaderWebClient;

    public AvatarServiceImpl(UrlProperties urlProperties) {
        this.urlProperties = urlProperties;
        this.loaderWebClient = WebClient.create(urlProperties.getProfileLoaderBaseUrl());
    }

    public AvatarDto getActualAvatar(String icp) {
        return loaderWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(urlProperties.getProfileLoaderGetAvatar())
                        .queryParam("icp", icp)
                        .build())
                .retrieve()
                .onStatus(HttpStatus::isError, response ->
                        Mono.error(new AvatarNotFoundException(
                                "Avatar with icp " + icp + " not found")
                        )
                )
                .bodyToMono(AvatarDto.class)
                .block();
    }
}
