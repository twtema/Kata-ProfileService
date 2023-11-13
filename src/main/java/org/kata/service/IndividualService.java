package org.kata.service;

import org.kata.dto.IndividualDto;

public interface IndividualService {
    IndividualDto getIndividual(String icp);
    IndividualDto getIndividualByPhoneNumber(String phone);

    void createTestIndividual(int n);

    IndividualDto getIndividualByPhoneNumber(String phoneNumber);
}
