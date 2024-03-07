package org.kata.service;

import org.kata.dto.AddressDto;

public interface AddressService {
    AddressDto getActualAddress(String icp, String conversationId);
    AddressDto getActualAddress(String icp, String uuid, String conversationId);

}
