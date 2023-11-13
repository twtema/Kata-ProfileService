package org.kata.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.kata.config.UrlProperties;
import org.kata.dto.*;
import org.kata.dto.enums.EventType;
import org.kata.exception.IndividualNotFoundException;
import org.kata.service.GenerateTestValue;
import org.kata.service.IndividualService;
import org.kata.service.KafkaMessageSender;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.stream.IntStream;

import static org.kata.dto.enums.EventType.DEDUPLICATION;

@Service
@Slf4j
public class IndividualServiceImp implements IndividualService {
    private final UrlProperties urlProperties;
    private final KafkaMessageSender kafkaMessageSender;
    private final GenerateTestValue generateTestValue;
    private final WebClient loaderWebClient;

    public IndividualServiceImp(UrlProperties urlProperties,
                                KafkaMessageSender kafkaMessageSender,
                                GenerateTestValue generateTestValue) {
        this.kafkaMessageSender = kafkaMessageSender;
        this.generateTestValue = generateTestValue;
        this.urlProperties = urlProperties;
        this.loaderWebClient = WebClient.create(urlProperties.getProfileLoaderBaseUrl());
    }

    public IndividualDto getIndividual(String icp) {
        return loaderWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(urlProperties.getProfileLoaderGetIndividual())
                        .queryParam("icp", icp)
                        .build())
                .retrieve()
                .onStatus(HttpStatus::isError, response ->
                        Mono.error(new IndividualNotFoundException(
                                "Individual with icp " + icp + " not found")
                        )
                )
                .bodyToMono(IndividualDto.class)
                .block();
    }

    /**
     * This method merges the data of two clients.
     *
     * @param icporigin        The identifier of the first client.
     * @param icpdedublication The identifier of the second client.
     * @param eventType        The identifier of the merge event.
     * @return An object with the data of the first client after the merge.
     */
    @Override
    public IndividualDto deduplication(String icporigin, String icpdedublication, EventType eventType) {
        // Retrieve data of the first client
        IndividualDto client1 = getIndividual(icporigin);

        // Retrieve data of the second client
        IndividualDto client2 = getIndividual(icpdedublication);

        if (eventType.equals(DEDUPLICATION)) {
            log.info("EventType -DEDUPLICATION");

            if (isIdentical(client1,client2)) {
                log.info("Сlient's identical");
                    // Create a new object for merging the client data
                    IndividualDto mergedDto = mergedIndividual(client1, client2);

                    // Delete old client records
                    deleteIndividual(icporigin);
                    deleteIndividual(icpdedublication);

                    // Create a new record with the merged client data
                    updateIndividual(mergedDto);

            } else {
                log.info("Сlient's not identical");
            }
        } else {
            log.info("EventsType is not DEDUPLICATION");
        }

        // Return an object with the data of the first client after the merge
        return getIndividual(icporigin);
    }

    /**
     * Checks if two individual clients are identical based on their personal information.
     *
     * @param client1 the first individual client
     * @param client2 the second individual client
     * @return true if the clients are identical, false otherwise
     */
    private boolean isIdentical(IndividualDto client1, IndividualDto client2) {
        IdenticalIndividualDto dto1 = getIdentical(client1);
        IdenticalIndividualDto dto2 = getIdentical(client2);
        return dto1.equals(dto2);
    }

    /**
     * Creates an instance of IdenticalIndividualDto based on the personal information of an individual client.
     *
     * @param client the individual client
     * @return an instance of IdenticalIndividualDto
     */
    private IdenticalIndividualDto getIdentical(IndividualDto client) {
        IdenticalIndividualDto dto = new IdenticalIndividualDto();
        dto.setName(client.getName());
        dto.setSurname(client.getSurname());
        dto.setPatronymic(client.getPatronymic());
        dto.setBirthDate(client.getBirthDate());
        return dto;
    }

    /**
     * This method is used to update client data.
     *
     * @param dto An object with the new client data.
     */
    protected void updateIndividual(IndividualDto dto) {
        loaderWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(urlProperties.getProfileLoaderPostIndividual())
                        .build())
                .body(Mono.just(dto), IndividualDto.class)
                .retrieve()
                .onStatus(HttpStatus::isError, response ->
                        Mono.error(new IndividualNotFoundException(
                                "Individual with icp " + dto.getIcp() + " not update")
                        ))
                .bodyToMono(IndividualDto.class)
                .block();
    }

    /**
     * This method is used to delete client data.
     *
     * @param icp The identifier of the client.
     */
    protected void deleteIndividual(String icp) {
        log.info("delete client, icp - " + icp);
        loaderWebClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(urlProperties.getProfileLoaderDeleteIndividual())
                        .queryParam("icp", icp)
                        .build())
                .retrieve()
                .onStatus(HttpStatus::isError, response ->
                        Mono.error(new IndividualNotFoundException(
                                "Individual with icp " + icp + " not found")
                        ))
                .bodyToMono(HttpStatus.class)
                .block();
    }

    /**
     * This method is used to merge the data of two clients.
     *
     * @param client1 An object with the data of the first client.
     * @param client2 An object with the data of the second client.
     * @return An object with the merged client data.
     */
    private IndividualDto mergedIndividual(IndividualDto client1, IndividualDto client2) {

        String icp = client1.getIcp();

        IndividualDto mergedDto = client1;

        // Merge the lists of client data
        mergedDto.getAvatar().addAll(client2.getAvatar());
        mergedDto.getDocuments().addAll(client2.getDocuments());
        mergedDto.getAddress().addAll(client2.getAddress());
        mergedDto.getContacts().addAll(client2.getContacts());

        mergedDto.setPlaceOfBirth(client1.getPlaceOfBirth());
        mergedDto.setBirthDate(client2.getBirthDate());

        log.info("client merged");

        // Remove duplicate data
        mergedDto.setAddress(mergedDto.getAddress().stream().distinct().toList());
        mergedDto.setAvatar(mergedDto.getAvatar().stream().distinct().toList());
        mergedDto.setDocuments(mergedDto.getDocuments().stream().distinct().toList());
        mergedDto.setContacts(mergedDto.getContacts().stream().distinct().toList());
        log.info("client removed duplicate data");

        return mergedDto;
    }

    public void createTestIndividual(int n) {
        IntStream.range(0, n)
                .forEach(i -> {
                    var dto = generateTestValue.generateRandomUser();
                    kafkaMessageSender.sendMessage(dto);
                    log.info("Create Individual with icp:{}", dto.getIcp());
                });
    }

}
