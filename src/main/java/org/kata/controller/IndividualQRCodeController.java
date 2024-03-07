package org.kata.controller;

import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.kata.dto.IndividualDto;
import org.kata.service.IndividualQRCodeService;
import org.kata.service.IndividualService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
@RequestMapping("v1/individual")
public class IndividualQRCodeController {
    private final IndividualService individualService;
    private final IndividualQRCodeService individualQRCodeService;



    @GetMapping(value = "/createQRCode", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generateQrCode(
            @RequestHeader(value = "conversationId", required = false) String conversationId,
            @RequestParam String icp) throws IOException, WriterException {
        IndividualDto individualDto = individualService.getIndividual(icp, conversationId);

        return individualQRCodeService.generateQRCode(individualDto, 270, 270);
    }
}
