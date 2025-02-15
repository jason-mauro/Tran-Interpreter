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

    @GetMapping("/console/{clientId}")
    public SseEmitter console(@PathVariable String clientId) {
        return interpreterService.createConsoleEmitter(clientId);
    }

    @PostMapping("/execute/{clientId}")
    public void execute(@PathVariable String clientId, @RequestBody Map<String, String> payload) {
        String code = payload.get("code");
        interpreterService.executeCode(code, clientId);
    }

    @PostMapping("/execute/stop/{clientId}")
    public void stop(@PathVariable String clientId) {
        interpreterService.stopExecution(clientId);
    }

}
