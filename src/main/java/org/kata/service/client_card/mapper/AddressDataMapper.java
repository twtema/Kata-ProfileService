package org.kata.service.client_card.mapper;

import org.kata.dto.AddressDto;
import org.kata.service.client_card.data.AddressData;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AddressDataMapper {
    AddressData map(AddressDto addressDto);
    List<AddressData> mapList(List<AddressDto> addressDtoList);
}
