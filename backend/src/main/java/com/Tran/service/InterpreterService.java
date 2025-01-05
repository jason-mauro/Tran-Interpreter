package com.Tran.service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class InterpreterService {

    @Async
    public void interpretFileWithStreaming(MultipartFile file, SseEmitter emitter) {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                // TODO: Replace with your actual interpreter logic
                String interpretedResult = interpretLine(line);

                // Send the interpreted result to the client
                emitter.send(interpretedResult);

                // Simulate some processing time (remove in production)
                Thread.sleep(100);
            }

            // Signal the end of processing
            emitter.complete();

        } catch (Exception e) {
            emitter.completeWithError(e);
        }
    }

    private String interpretLine(String line) {
        // TODO: Implement your interpreter logic here
        return "Interpreted: " + line;
    }
}