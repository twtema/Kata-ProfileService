package org.kata.service;

import org.kata.dto.WalletDto;

import java.util.List;

public interface WalletService {
    List<WalletDto> getWallets(String icp);
}
