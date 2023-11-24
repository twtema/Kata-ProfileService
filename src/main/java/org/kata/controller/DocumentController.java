package org.kata.controller;

import lombok.RequiredArgsConstructor;
import org.kata.dto.DocumentDto;
import org.kata.exception.DocumentsNotFoundException;
import org.kata.service.DocumentService;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/document")
public class DocumentController {
    private final DocumentService documentService;

    @GetMapping("/getActual")
    public ResponseEntity<List<DocumentDto>> getDocument(@RequestParam String icp, @RequestParam String uuid) {
        if (icp != null && uuid != null) {
            return new ResponseEntity<>(documentService.getActualDocument(icp, uuid), HttpStatus.OK);
        } else if (icp != null) {
            return new ResponseEntity<>(documentService.getActualDocument(icp), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DocumentsNotFoundException.class)
    public ErrorMessage getDocumentHandler(DocumentsNotFoundException e) {
        return new ErrorMessage(e.getMessage());
    }
}
