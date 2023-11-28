package org.kata.controller;

import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.kata.dto.IndividualDto;
import org.kata.exception.IndividualNotFoundException;
import org.kata.service.IndividualQRCodeService;
import org.kata.service.IndividualService;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
@RequestMapping("v1/individual")
public class IndividualController {

    private final IndividualService individualService;
    private final IndividualQRCodeService individualQRCodeService;

    @GetMapping
    public ResponseEntity<IndividualDto> getIndividual(@RequestParam String icp) {
        return new ResponseEntity<>(individualService.getIndividual(icp), HttpStatus.OK);
    }

    @GetMapping(value = "/createQRCode", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generateQrCode(@RequestParam String icp) throws IOException, WriterException {
        IndividualDto individualDto = individualService.getIndividual(icp);

        return individualQRCodeService.generateQRCode(individualDto, 270, 270);
    }

    @PostMapping
    public String createTestIndividual(@RequestParam int n) {
        individualService.createTestIndividual(n);
        return "Success create " + n + "Individual, pls check Kafka and DB";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IndividualNotFoundException.class)
    public ErrorMessage getIndividualHandler(IndividualNotFoundException e) {
        return new ErrorMessage(e.getMessage());
    }

}
