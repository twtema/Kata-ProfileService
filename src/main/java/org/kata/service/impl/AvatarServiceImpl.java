package org.kata.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.kata.config.UrlProperties;
import org.kata.dto.AvatarDto;
import org.kata.exception.AvatarNotFoundException;
import org.kata.exception.IndividualNotFoundException;
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
        if (icp != null) {
            return loaderWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(urlProperties.getProfileLoaderGetAvatar())
                            .queryParam("id", icp)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatus::isError, response ->
                            Mono.error(new AvatarNotFoundException(
                                    "Address with icp " + icp + " not found")
                            )
                    )
                    .bodyToMono(AvatarDto.class)
                    .block();
        } else {
            throw new IndividualNotFoundException("Not found individual");
        }
    }

    @Override
    public AvatarDto getActualAvatar(String icp, String type) {
        if (icp == null && type == null) {
            throw new IllegalArgumentException("Not found parameters");
        }
        if (type.equals("uuid")) {
            return loaderWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(urlProperties.getProfileLoaderGetAvatar())
                            .queryParam("id", icp)
                            .queryParam("type", type)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatus::isError, response ->
                            Mono.error(new AvatarNotFoundException(
                                    "Address with icp " + icp + " not found")
                            )

                    )
                    .bodyToMono(AvatarDto.class)
                    .block();
        } else if (type.isEmpty()) {
            return getActualAvatar(icp);
        } else {
            throw new IllegalArgumentException("Invalid type");
        }
    }
}
