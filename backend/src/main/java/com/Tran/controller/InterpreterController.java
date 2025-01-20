package com.Tran.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.Tran.service.InterpreterService;

@RestController
@RequestMapping("/api/interpreter")
public class InterpreterController {
    @Autowired
    private InterpreterService interpreterService;

    @PostMapping("/execute")
    public ResponseEntity<String> execute(@RequestBody String code,
                                          @RequestHeader("WebSocket-Session-Id") String sessionID) {
        interpreterService.executeCode(code, sessionID);
        return ResponseEntity.ok("Code execution starting...");
    }

}
