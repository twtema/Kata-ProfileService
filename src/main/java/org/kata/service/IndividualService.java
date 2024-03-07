package org.kata.service;

import org.kata.dto.IndividualDto;
import org.kata.dto.enums.EventType;

public interface IndividualService {
    IndividualDto getIndividual(String icp, String conversationId);

    IndividualDto getIndividual(String icp, String uuid, String conversationId);

    IndividualDto getIndividualByPhoneNumber(String phone, String conversationId);

    void createTestIndividual(int n);

    IndividualDto dedublication(String icporigin,
                                String icpdedublication,
                                EventType eventType,
                                String conversationId);
}
