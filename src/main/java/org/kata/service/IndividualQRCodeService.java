package org.kata.service;

import com.google.zxing.WriterException;
import org.kata.dto.IndividualDto;

import java.io.IOException;

public interface IndividualQRCodeService {

    public byte[] generateQRCode(IndividualDto individualDto, int width, int height) throws WriterException, IOException;
}
