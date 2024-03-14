package org.kata.controller;

import io.micrometer.core.annotation.Timed;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
    @Timed(value = "execution_time", description = "Get individual")
    public ResponseEntity<IndividualDto> getIndividual(String id,
                                                       @RequestParam(required = false) String type) {
        if (type == null) {
            return new ResponseEntity<>(individualService.getIndividual(id), HttpStatus.OK);
        }
        return new ResponseEntity<>(individualService.getIndividual(id, type), HttpStatus.OK);
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
    @Timed(value = "execution_time", description = "Get individual by icp or phone")
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

    @Timed(value = "execution_time", description = "Get individual by icp or phone_2")
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
    @Timed(value = "execution_time", description = "Create test individual")
    public String createTestIndividual(@RequestParam int n) {
        individualService.createTestIndividual(n);
        return "Success create " + n + "Individual, pls check Kafka and DB";
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IndividualNotFoundException.class)
    @Timed(value = "execution_time", description = "Get individual handler")
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
    @Timed(value = "execution_time", description = "Deduplication individual method")
    public ResponseEntity<IndividualDto> dedublication(@RequestBody String icporigin,
                                                       @RequestBody String icpdedublication,
                                                       @RequestBody EventType eventType) {
        return new ResponseEntity<>(individualService.dedublication
                (icporigin, icpdedublication, eventType), HttpStatus.OK);
    }
}
