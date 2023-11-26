package org.kata.service.client_card;
import org.kata.dto.IndividualDto;
import org.kata.service.client_card.mapper.AddressDataMapper;
import org.kata.service.client_card.mapper.ContactDataMapper;
import org.kata.service.client_card.mapper.DocumentDataMapper;
import org.mapstruct.factory.Mappers;
import java.text.SimpleDateFormat;
import java.util.*;


public class ClientCardDataParser {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private final DocumentDataMapper documentDataMapper;
    private final AddressDataMapper addressDataMapper;
    private final ContactDataMapper contactDataMapper;

    public ClientCardDataParser() {
        this.documentDataMapper = Mappers.getMapper(DocumentDataMapper.class);
        this.addressDataMapper = Mappers.getMapper(AddressDataMapper.class);
        this.contactDataMapper = Mappers.getMapper(ContactDataMapper.class);
    }

    public Map <String, String> parseMainData (IndividualDto ind) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("NAME", ind.getName());
        map.put("SURNAME", ind.getSurname());
        map.put("PATRONYMIC", ind.getPatronymic());
        map.put("GENDER", ind.getGender().name());
        map.put("BIRTHDATE", dateFormat.format(ind.getBirthDate()));
        map.put("COUNTRY", ind.getCountryOfBirth());
        return map;
    }
    public Map<String, List<String>> parseSubData(IndividualDto ind) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        addSubData("ADDRESSES", map, addressDataMapper.mapList(ind.getAddress()));
        addSubData("DOCUMENTS", map, documentDataMapper.mapList(ind.getDocuments()));
        addSubData("CONTACTS", map, contactDataMapper.mapList(ind.getContacts()));
        return map;
    }
    private <T> void addSubData(String key, Map<String, List<String>> map, List<T> list) {
        if (list.isEmpty()) {
            return;
        }
        map.put(key, list
                .stream()
                .map(T::toString)
                .toList());
    }


}
