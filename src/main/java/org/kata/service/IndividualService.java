package org.kata.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.kata.dto.IndividualDto;

public interface IndividualService {
    IndividualDto getIndividual(String icp);

    void createTestIndividual(int n);

    PDDocument getClientCard(String icp);
}
