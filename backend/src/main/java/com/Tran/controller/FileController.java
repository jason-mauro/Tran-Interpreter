package com.Tran.controller;

import com.Tran.service.InterpreterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class FileController {

    private final InterpreterService interpreterService;

    @Autowired
    public FileController(InterpreterService interpreterService) {
        this.interpreterService = interpreterService;
    }

    @PostMapping(value = "/upload/stream", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SseEmitter uploadAndStream(@RequestParam("file") MultipartFile file) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // Process the file asynchronously
        interpreterService.interpretFileWithStreaming(file, emitter);

        return emitter;
    }
}