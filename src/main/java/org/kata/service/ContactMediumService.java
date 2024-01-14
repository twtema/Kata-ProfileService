package org.kata.service;

import org.kata.dto.ContactMediumDto;

import java.util.List;

public interface ContactMediumService {
    List<ContactMediumDto> getActualContactMedium(String icp);

    List<ContactMediumDto> getActualContactMediumByType(String icp, String type);

    List<ContactMediumDto> getActualContactMediumByUsage(String icp, String usage);

    List<ContactMediumDto> getActualContactMediumByTypeAndUsage(String icp, String type, String usage);
}
