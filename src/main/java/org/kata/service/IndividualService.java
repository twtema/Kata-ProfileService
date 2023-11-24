package org.kata.service;

import org.kata.dto.IndividualDto;

public interface IndividualService {
    IndividualDto getIndividual(String icp);
    IndividualDto getIndividual(String icp, String uuid);

    void createTestIndividual(int n);
}
