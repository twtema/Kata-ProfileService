package org.kata.service.client_card.mapper;

import org.kata.dto.DocumentDto;
import org.kata.service.client_card.data.DocumentData;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DocumentDataMapper {
    DocumentData map(DocumentDto documentDto);
    List<DocumentData> mapList(List<DocumentDto> documentDtoList);
}
