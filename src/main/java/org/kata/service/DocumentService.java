package org.kata.service;

import org.kata.dto.DocumentDto;

import java.util.List;

public interface DocumentService {
    List<DocumentDto> getActualDocuments(String icp);
}
