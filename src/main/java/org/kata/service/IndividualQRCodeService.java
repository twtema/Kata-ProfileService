package org.kata.service;

import org.kata.dto.IndividualDto;

public interface IndividualQRCodeService {

    void generateQRCode(IndividualDto individualDto);
}
