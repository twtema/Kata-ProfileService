package org.kata.service;

import org.kata.dto.IndividualDto;

public interface IndividualService {
    IndividualDto getIndividual(String icp);

    void createTestIndividual(int n);
}
