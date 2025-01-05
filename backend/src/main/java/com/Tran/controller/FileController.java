package com.Tran.controller;

import com.Tran.service.InterpreterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class FileController {

    private final InterpreterService interpreter;

    @Autowired
    public FileController(InterpreterService interpreter) {
        this.interpreter = interpreter
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please upload a file");
            }

            if (!file.getOriginalFilename().endsWith(".txt") || !file.getOriginalFilename().endsWith(".tran")) {
                return ResponseEntity.badRequest().body("Only .txt or .tran files are allowed");
            }

            String result = interpreterService.interpretFile(file);
            return ResponseEntity.ok().body(result);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing file: " + e.getMessage());
        }
    }
}




}