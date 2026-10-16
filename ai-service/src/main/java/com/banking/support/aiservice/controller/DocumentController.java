package com.banking.support.aiservice.controller;

import com.banking.support.aiservice.service.DocumentExtractionService;
import com.banking.support.aiservice.service.DocumentIndexingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentExtractionService extractionService;
    private final DocumentIndexingService indexingService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) throws Exception {
        Path temp = Files.createTempFile("upload-", ".pdf");
        file.transferTo(temp.toFile());

        String text = extractionService.extractText(temp.toFile());
        int chunks = indexingService.indexDocument(text, file.getOriginalFilename());

        Files.deleteIfExists(temp);

        return ResponseEntity.ok(Map.of(
                "fileName", file.getOriginalFilename(),
                "chunks", chunks,
                "status", "SUCCESS"));
    }
}