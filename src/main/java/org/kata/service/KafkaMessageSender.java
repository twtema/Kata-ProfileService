package org.kata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kata.dto.IndividualDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageSender {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.create}")
    private String kafkaTopic;

    @Value("${messageId}")
    private String messageId;

    public void sendMessage(IndividualDto dto) {
        kafkaTemplate.send(kafkaTopic, messageId, dto);
        log.info("Message send to topic:{}", kafkaTopic);
    }
}
