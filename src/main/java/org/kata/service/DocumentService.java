package org.kata.service;

import org.kata.dto.DocumentDto;

import java.util.List;

public interface DocumentService {
    List<DocumentDto> getAllDocuments(String icp, String conversationId);
    List<DocumentDto> getAllDocuments(String icp, String uuid, String conversationId);
    void createTestDocument(String icp, String conversationId);
}
