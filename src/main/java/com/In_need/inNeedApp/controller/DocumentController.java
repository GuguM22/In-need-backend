package com.In_need.inNeedApp.controller;

import com.In_need.inNeedApp.model.Documents;
import com.In_need.inNeedApp.repository.DocumentRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
@RequestMapping("/documents")
public class DocumentController {
    private final DocumentRepository documentsRepository;

    public DocumentController(DocumentRepository documentsRepository) {
        this.documentsRepository = documentsRepository;
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        Documents document = documentsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF) // assuming all docs are PDFs
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"")
                .body(document.getData());
    }

}
