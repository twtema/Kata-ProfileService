package org.kata.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("url-properties")
@Getter
@Setter
public class UrlProperties {

    private String profileLoaderBaseUrl;

    private String profileLoaderGetIndividual;
    private String profileLoaderGetActualDocuments;
    private String profileLoaderGetNotActualDocuments;
    private String profileLoaderGetContactMedium;
    private String profileLoaderGetAvatar;
    private String profileLoaderGetAddress;


}
