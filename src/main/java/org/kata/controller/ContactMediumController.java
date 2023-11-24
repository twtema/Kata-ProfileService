package org.kata.controller;

import lombok.RequiredArgsConstructor;
import org.kata.dto.ContactMediumDto;
import org.kata.exception.ContactMediumNotFoundException;
import org.kata.service.ContactMediumService;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/contactMedium")
public class ContactMediumController {

    private final ContactMediumService contactMediumService;

    @GetMapping("/getActual")
    public ResponseEntity<List<ContactMediumDto>> getContactMedium(@RequestParam String icp, @RequestParam String uuid) {
        if (icp != null && uuid != null) {
            return new ResponseEntity<>(contactMediumService.getActualContactMedium(icp, uuid), HttpStatus.OK);
        } else if (icp != null) {
            return new ResponseEntity<>(contactMediumService.getActualContactMedium(icp), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ContactMediumNotFoundException.class)
    public ErrorMessage getContactMediumHandler(ContactMediumNotFoundException e) {
        return new ErrorMessage(e.getMessage());
    }

}
