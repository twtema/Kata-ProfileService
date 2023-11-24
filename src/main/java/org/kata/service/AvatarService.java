package org.kata.service;

import org.kata.dto.AddressDto;
import org.kata.dto.AvatarDto;

public interface AvatarService {
    AvatarDto getActualAvatar(String icp);
    AvatarDto getActualAvatar(String icp, String uuid);
}
