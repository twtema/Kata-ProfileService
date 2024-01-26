package org.kata.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.kata.config.UrlProperties;
import org.kata.dto.WalletDto;
import org.kata.exception.IndividualNotFoundException;
import org.kata.exception.WalletNotFoundException;
import org.kata.service.WalletService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final UrlProperties urlProperties;

    private final WebClient loaderWebClient;

    public WalletServiceImpl(UrlProperties urlProperties) {
        this.urlProperties = urlProperties;
        this.loaderWebClient = WebClient.create(urlProperties.getProfileLoaderBaseUrl());
    }

    @Override
    public List<WalletDto> getWallet(String icp) {
        if (icp != null) {
            return loaderWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(urlProperties.getProfileLoaderGetWallets())
                            .queryParam("id", icp)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatus::isError, response ->
                            Mono.error(new WalletNotFoundException(
                                    "Wallets with icp " + icp + " not found")
                            )
                    )
                    .bodyToMono(new ParameterizedTypeReference<List<WalletDto>>() {
                    })
                    .block();
        } else {
            throw new IndividualNotFoundException("Not found individual");
        }
    }
}
