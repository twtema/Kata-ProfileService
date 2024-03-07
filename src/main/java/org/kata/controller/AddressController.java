package org.kata.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.kata.dto.AddressDto;
import org.kata.exception.AddressNotFoundException;
import org.kata.service.AddressService;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Address", description = "The address API")
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/address")
public class AddressController {
    private final AddressService addressService;

    @Operation(summary = "Get actual individual address")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The Address is found",
                    content = @Content(
                            mediaType = "Application/JSON",
                            schema = @Schema(implementation = AddressDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "The Address is NOT found",
                    content = @Content(
                            mediaType = "Application/JSON",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<AddressDto> getAddress(
            @RequestHeader(value = "conversationId", required = false) String conversationId,
            String id,
            @RequestParam(required = false) String type) {
        if (type == null) {
            return new ResponseEntity<>(addressService.getActualAddress(id, conversationId), HttpStatus.OK);
        }
        return new ResponseEntity<>(addressService.getActualAddress(id, type, conversationId), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AddressNotFoundException.class)
    public ErrorMessage getAddressHandler(AddressNotFoundException e) {
        return new ErrorMessage(e.getMessage());
    }
}