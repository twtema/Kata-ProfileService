package org.kata.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.kata.dto.DocumentDto;
import org.kata.exception.DocumentsNotFoundException;
import org.kata.service.DocumentService;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Document", description = "The document API")
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/document")
public class DocumentController {
    private final DocumentService documentService;

    @Operation(summary = "Get all Individual Documents")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of Documents is found",
                    content = @Content(
                            mediaType = "Application/JSON",
                            array = @ArraySchema(schema = @Schema(implementation = DocumentDto.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Documents is NOT found",
                    content = @Content(
                            mediaType = "Application/JSON",
                            schema = @Schema(implementation = ErrorMessage.class)
                    )
            )
    })
    @GetMapping("/getActual")
    public ResponseEntity<List<DocumentDto>> getDocument(@RequestParam(required = false) String id,
                                                         @RequestParam(required = false) String type) {
        if (id != null && type != null) {
            return new ResponseEntity<>(documentService.getActualDocument(id, type), HttpStatus.OK);
        } else if (id != null) {
            return new ResponseEntity<>(documentService.getActualDocument(id), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DocumentsNotFoundException.class)
    public ErrorMessage getDocumentHandler(DocumentsNotFoundException e) {
        return new ErrorMessage(e.getMessage());
    }
}