package org.kata.service;

import org.kata.dto.ContactMediumDto;
import org.kata.dto.RequestContactMediumDto;

import java.util.List;

public interface ContactMediumService {

    List<ContactMediumDto> getActualContactMedium(RequestContactMediumDto dto);

}
