package org.kata.service;

import org.kata.dto.AvatarDto;

public interface AvatarService {
    AvatarDto getActualAvatar(String icp);
}
