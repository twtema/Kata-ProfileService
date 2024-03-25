package org.kata.controller;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.kata.dto.ContactMediumDto;
import org.kata.exception.ContactMediumNotFoundException;
import org.kata.service.ContactMediumService;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Contact Medium", description = "The contact medium API")
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/contactMedium")
public class ContactMediumController {

    private final ContactMediumService contactMediumService;

    @Operation(summary = "Get List of contacts")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Contacts is found",
                    content = @Content(
                            mediaType = "Application/JSON",
                            array = @ArraySchema(schema = @Schema(implementation = ContactMediumDto.class))
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
    @GetMapping
    @Timed(value = "execution_time", description = "Get contact medium")
    public ResponseEntity<List<ContactMediumDto>> getContactMedium(String id,
                                                                   @RequestParam(required = false) String type) {
        if (type == null) {
            return new ResponseEntity<>(contactMediumService.getActualContactMedium(id), HttpStatus.OK);
        }
        return new ResponseEntity<>(contactMediumService.getActualContactMedium(id, type), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ContactMediumNotFoundException.class)
    @Timed(value = "execution_time", description = "Get contact medium handler")
    public ErrorMessage getContactMediumHandler(ContactMediumNotFoundException e) {
        return new ErrorMessage(e.getMessage());
    }
}