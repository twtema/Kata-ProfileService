package org.kata.controller;

import lombok.RequiredArgsConstructor;
import org.kata.dto.IndividualDto;
import org.kata.exception.IndividualNotFoundException;
import org.kata.service.IndividualService;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("v1/individual")
public class IndividualController {

    private final IndividualService individualService;

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
