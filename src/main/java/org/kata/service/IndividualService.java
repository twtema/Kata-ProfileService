package org.kata.service;

import org.kata.dto.IndividualDto;

public interface IndividualService {
    IndividualDto getIndividual(String icp);

    void createTestIndividual(int n);

    //в ProfileService
// Должен быть post запрос с телом
//icporigin
//icpdedublication
//event_dedublication
//В сервисе если оба клиента найдены происходит лога проверки пользователей,
// если их ФИО и дата рождения совпадают, то клиенты объединяются в одного, второй удаляется
//
//Если у эталона не было документов, а у второго были,
// они переносятся, если документы совпадают ничего не происходит(все остальные поля аналогично)
    public IndividualDto deduplication(String icporigin,
                              String icpdedublication,
                              String event_dedublication);
    IndividualDto updateIndividual(IndividualDto dto);


}
