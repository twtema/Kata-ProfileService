package org.kata.service.client_card.mapper;

import org.kata.dto.ContactMediumDto;
import org.kata.service.client_card.data.ContactData;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ContactDataMapper {
    ContactData map(ContactMediumDto contactMediumDto);
    List<ContactData> mapList(List<ContactMediumDto> contactMediumDtoList);
}
