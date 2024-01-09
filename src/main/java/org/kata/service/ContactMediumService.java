package org.kata.service;

import org.kata.dto.ContactMediumDto;

import java.util.List;

public interface ContactMediumService {
    List<ContactMediumDto> getActualContactMedium(String icp);
    List<ContactMediumDto> getActualContactMedium(String icp, String uuid);

}
