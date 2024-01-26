package org.kata.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.kata.dto.WalletDto;
import org.kata.exception.WalletNotFoundException;
import org.kata.service.WalletService;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Wallet", description = "The wallet API")
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/wallet")
public class WalletController {

    private final WalletService walletService;

    @Operation(summary = "Получить Wallet по icp",
            description = "Возвращает DTO Wallet по ICP")
    @GetMapping
    public ResponseEntity<List<WalletDto>> getWallet(
            @Parameter(description = "ICP identifier", required = true)
            @RequestParam String id) {
        return new ResponseEntity<>(walletService.getWallet(id), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WalletNotFoundException.class)
    public ErrorMessage getWalletHandler(WalletNotFoundException e) {
        return new ErrorMessage(e.getMessage());
    }
}
