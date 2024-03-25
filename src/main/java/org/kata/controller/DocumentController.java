package org.kata.controller;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @GetMapping
    @Timed(value = "execution_time", description = "Get document")
    public ResponseEntity<List<DocumentDto>> getDocument(String id,
                                                         @RequestParam(required = false) String type) {
        if (type == null) {
            return new ResponseEntity<>(documentService.getAllDocuments(id), HttpStatus.OK);
        }
        return new ResponseEntity<>(documentService.getAllDocuments(id, type), HttpStatus.OK);
    }

    @Operation(summary = "Деактивация актуального документа",
               description = "Деактивирует актуальный документ если более новый есть в топике Kafka")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Successful Document creation",
                    content = @Content(
                            mediaType = "Application/JSON",
                            schema = @Schema(implementation = String.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/updateActualState")
    @Timed(value = "execution_time", description = "Create test document")
    public ResponseEntity<Void> createTestDocument(@Parameter(description = "Individual icp") @RequestParam String icp) {
        documentService.createTestDocument(icp);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DocumentsNotFoundException.class)
    @Timed(value = "execution_time", description = "Get document handler")
    public ErrorMessage getDocumentHandler(DocumentsNotFoundException e) {
        return new ErrorMessage(e.getMessage());
    }
}