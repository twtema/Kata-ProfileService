package org.kata.controller;

import lombok.RequiredArgsConstructor;
import org.kata.dto.AddressDto;
import org.kata.exception.AddressNotFoundException;
import org.kata.service.AddressService;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/address")
public class AddressController {
    private final AddressService addressService;

    @GetMapping("/getActual")
    public ResponseEntity<AddressDto> getAddress(@RequestParam String icp) {
        return new ResponseEntity<>(addressService.getActualAddress(icp), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AddressNotFoundException.class)
    public ErrorMessage getAddressHandler(AddressNotFoundException e) {
        return new ErrorMessage(e.getMessage());
    }
}
