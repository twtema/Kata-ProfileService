package org.kata.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.kata.config.UrlProperties;
import org.kata.dto.IdenticalIndividualDto;
import org.kata.dto.IndividualDto;
import org.kata.dto.enums.EventType;
import org.kata.exception.IndividualMergeException;
import org.kata.exception.IndividualNotFoundException;
import org.kata.service.GenerateTestValue;
import org.kata.service.IndividualService;
import org.kata.service.KafkaMessageSender;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;
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

        if (icp != null) {
            return loaderWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(urlProperties.getProfileLoaderGetIndividual())
                            .queryParam("icp", icp)
                            .build())
                    .header("icp", icp)
                    .retrieve()
                    .onStatus(HttpStatus::isError, response ->
                            Mono.error(new IndividualNotFoundException(
                                    "Individual with icp " + icp + " not found")
                            )
                    )
                    .bodyToMono(IndividualDto.class)
                    .block();

        } else {
            throw new IllegalArgumentException("ERROR");
        }
    }

    @Override
    public IndividualDto getIndividual(String icp, String type) {
        if (icp == null && type == null) {
            throw new IllegalArgumentException("Not found parameters");
        }
        if (type.equals("uuid")) {
            return loaderWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(urlProperties.getProfileLoaderGetIndividual())
                            .queryParam("icp", icp)
                            .queryParam("type", type)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatus::isError, response ->
                            Mono.error(new IndividualNotFoundException(
                                    "Individual with icp " + icp + " not found")
                            )
                    )
                    .bodyToMono(IndividualDto.class)
                    .block();
        } else if (type.isEmpty()) {
            return getIndividual(icp);
        } else {
            throw new IllegalArgumentException("Invalid type");
        }
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
    public IndividualDto dedublication(String icporigin, String icpdedublication, EventType eventType) {
        // Получаем данные первого клиента
        IndividualDto original = getIndividual(icporigin);

        // Получаем данные второго клиента
        IndividualDto duplicate = getIndividual(icpdedublication);

        // Проверяем, является ли eventType DEDUPLICATION
        if (eventType == EventType.DEDUPLICATION) {
            log.info("Событие - ДЕДУПЛИКАЦИЯ");

            // Проверяем существование обоих клиентов
            if (original != null && duplicate != null) {

                // Проверяем, не являются ли оба клиента архивными
                if (!original.isArchived() && !duplicate.isArchived()) {

                    // Проверяем, идентичны ли клиенты
                    if (isIdentical(original, duplicate)) {
                        log.info("Клиенты идентичны");

                        // Удаляем дублирующиеся данные у второго клиента
                        removeDuplicateData(duplicate);

                        // Архивируем второго клиента
                        duplicate.setArchived(true);
                        duplicate.setStatus("archiveClient");

                        // Связываем первого клиента с архивным дубликатом
                        duplicate.setLinkedClientUUID(original.getUuid());

                        // Обновляем базу данных
                        updateIndividual(duplicate);
                        updateIndividual(original);

                        log.info("Дедубликация успешно завершена");

                        // Возвращаем обновленные данные первого клиента
                        return original;
                    } else {
                        log.info("Клиенты не идентичны");
                        // Клиенты не идентичны, дедубликация невозможна
                        throw new IllegalStateException("Дедубликация невозможна: Клиенты не идентичны.");
                    }
                } else {
                    log.info("Один или оба клиента уже архивированы");
                    // Один или оба клиента уже архивированы, дедубликация невозможна
                    throw new IllegalStateException("Дедубликация невозможна: Один или оба клиента уже архивированы.");
                }
            } else {
                log.info("Один или оба клиента не найдены");
                // Один или оба клиента не найдены, дедубликация невозможна
                throw new IllegalStateException("Дедубликация невозможна: Один или оба клиента не найдены.");
            }
        } else {
            log.info("Событие не является ДЕДУПЛИКАЦИЕЙ");
            // Событие не является DEDUPLICATION, дедубликация невозможна
            throw new IllegalArgumentException("Дедубликация невозможна: Событие не является ДЕДУПЛИКАЦИЕЙ.");
        }
    }

    /**
     * Удаляет дублирующиеся данные у индивидуального клиента.
     *
     * @param individualDto индивидуальный клиент
     */
    private void removeDuplicateData(IndividualDto individualDto) {
        // Удаляем дублирующиеся поля данных

        individualDto.setAddress(individualDto.getAddress().stream().distinct().toList());
        individualDto.setAvatar(individualDto.getAvatar().stream().distinct().toList());
        individualDto.setDocuments(individualDto.getDocuments().stream().distinct().toList());
        individualDto.setContacts(individualDto.getContacts().stream().distinct().toList());

        log.info("Дублирующиеся данные удалены у клиента");
    }

    /**
     * Удаляет дубликаты из списка.
     *
     * @param list список для удаления дубликатов
     * @return список без дубликатов
     */
    private List<String> removeDuplicates(List<String> list) {
        return list.stream().distinct().collect(Collectors.toList());
    }

    /**
     * Проверяет, являются ли два индивидуальных клиента идентичными на основе их персональной информации.
     *
     * @param original   первый индивидуальный клиент
     * @param duplicate второй индивидуальный клиент
     * @return true, если клиенты идентичны, иначе false
     */
    private boolean isIdentical(IndividualDto original, IndividualDto duplicate) {
        return getIdentical(original).equals(getIdentical(duplicate));
    }
    private IdenticalIndividualDto getIdentical(IndividualDto individualDto) {
        IdenticalIndividualDto dto = new IdenticalIndividualDto();
        dto.setName(individualDto.getName());
        dto.setSurname(individualDto.getSurname());
        dto.setPatronymic(individualDto.getPatronymic());
        dto.setBirthDate(individualDto.getBirthDate());
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
                                printException( dto.getIcp()) + " not update")
                        ))
                .bodyToMono(IndividualDto.class)
                .block();
    }


    @Override
    public IndividualDto getIndividualByPhoneNumber(String phone) {
        return loaderWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(urlProperties.getProfileLoaderGetIndividualByPhoneNumber())
                        .queryParam("phone", phone)
                        .build())
                .retrieve()
                .onStatus(HttpStatus::isError, response ->
                        Mono.error(new IndividualNotFoundException(
                                "Individual with phone number " + phone + " not found")
                        )
                )
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
                                printException(icp) + " not found")
                        ))
                .bodyToMono(HttpStatus.class)
                .block();
    }

    /**
     * This method is used to merge the data of two clients.
     *
     * @param original An object with the data of the first client.
     * @param dedublication An object with the data of the second client.
     * @return An object with the merged client data.
     */
    private IndividualDto mergedIndividual(IndividualDto original, IndividualDto dedublication) {


        try {
            // Merge the lists of client data
            original.getAvatar().addAll(dedublication.getAvatar());
            original.getDocuments().addAll(dedublication.getDocuments());
            original.getAddress().addAll(dedublication.getAddress());
            original.getContacts().addAll(dedublication.getContacts());

            original.setBirthDate(dedublication.getBirthDate());

            log.info("client merged");

        } catch (NullPointerException e) {

            throw new IndividualMergeException(printException(original.getIcp()) + " not merged");
        }
        return original;
    }

    public void createTestIndividual(int n) {
        IntStream.range(0, n)
                .forEach(i -> {
                    var dto = generateTestValue.generateRandomUser();
                    kafkaMessageSender.sendMessage(dto);
                    log.info("Create Individual with icp:{}", dto.getIcp());
                });
    }

    private String printException(String icp) {
        return "Individual with icp " + icp;
    }

}
