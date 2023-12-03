package org.kata.service;

import org.kata.dto.IndividualDto;
import org.kata.dto.enums.EventType;

public interface IndividualService {
    IndividualDto getIndividual(String icp);

    void createTestIndividual(int n);

    IndividualDto dedublication(String icporigin,
                              String icpdedublication,
                              EventType eventType);



}
