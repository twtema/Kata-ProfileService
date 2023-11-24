package org.kata.controller;

import com.google.zxing.WriterException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.kata.dto.IndividualDto;
import org.kata.exception.BadRequestException;
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


@Tag(name = "Individual", description = "The individual API")
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/individual")
public class IndividualController {

    private final IndividualService individualService;
    private final IndividualQRCodeService individualQRCodeService;

    @Operation(summary = "Get the Individual")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The Individual is found",
                    content = @Content(
                            mediaType = "Application/JSON",
                            schema = @Schema(implementation = IndividualDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "The Individual is NOT found",
                    content = @Content(
                            mediaType = "Application/JSON",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<IndividualDto> getIndividual(@RequestParam String icp, @RequestParam String uuid) {
        if (icp != null && uuid != null) {
            return new ResponseEntity<>(individualService.getIndividual(icp, uuid), HttpStatus.OK);
        } else if (icp != null) {
            return new ResponseEntity<>(individualService.getIndividual(icp), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @Operation(
            summary = "Get Individual by ICP or phone number",
            description = "Get Individual Entity by ICP or phone number"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The Individual is found",
                    content = @Content(
                            mediaType = "Application/JSON",
                            schema = @Schema(implementation = IndividualDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "The Individual is NOT found",
                    content = @Content(
                            mediaType = "Application/JSON",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            )
    })
    @GetMapping("/getIndividualByIcpOrPhone")
    public ResponseEntity<IndividualDto> getIndividualByIcpOrPhone(
            @RequestParam(required = false) @Parameter(description = "User icp") String icp,
            @RequestParam(required = false) @Parameter(description = "User phone") String phone
    ) {
        if ((StringUtils.isNotEmpty(icp) && StringUtils.isNotEmpty(phone)) ||
                (StringUtils.isEmpty(icp) && StringUtils.isEmpty(phone))) {
            throw new BadRequestException("Exactly one of the parameters (icp or phone) must be provided");
        }
        IndividualDto individual = icpOrPhone(icp, phone);
        if (individual == null) {
            throw new IndividualNotFoundException("Individual not found: icp = " + icp +
                    " phone = " + phone);
        }
        return ResponseEntity.ok(individual);
    }
    private IndividualDto icpOrPhone(String icp, String phone) {
        IndividualDto individual = null;
        if (StringUtils.isNotEmpty(phone)) {
            individual = individualService.getIndividualByPhoneNumber(phone);
        } else if (StringUtils.isNotEmpty(icp)) {
            individual = individualService.getIndividual(icp);
        }
        return individual;
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
