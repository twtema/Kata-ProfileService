package org.kata.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.kata.dto.IndividualDto;
import org.kata.dto.enums.EventType;
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

    @Operation(summary = "Create random Individuals by n (count)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful Individuals creation",
                    content = @Content(
                            mediaType = "Application/JSON",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(
                            mediaType = "Application/JSON",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            )
    })
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


    @Operation(summary = "Dedublication, merge Individual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful deduplication"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/deduplication")
    public ResponseEntity<IndividualDto> dedublication(@RequestBody String icporigin,
                                                       @RequestBody String icpdedublication,
                                                       @RequestBody EventType eventType) {
        return new ResponseEntity<>(individualService.dedublication
                (icporigin, icpdedublication, eventType), HttpStatus.OK);
    }
}
