package com.Tran.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.Tran.service.InterpreterService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
@RequestMapping("/api/interpreter")
public class InterpreterController {
    @Autowired
    private InterpreterService interpreterService;

    @GetMapping("/console")
    public SseEmitter console(@RequestParam String clientId) {
        return interpreterService.createConsoleEmitter(clientId);
    }

    @PostMapping("/execute")
    public void execute(@RequestParam String clientId, @RequestBody Map<String, String> payload) {
        String code = payload.get("code");
        interpreterService.executeCode(code, clientId);
    }


}
