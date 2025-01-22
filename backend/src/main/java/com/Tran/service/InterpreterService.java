package com.Tran.service;

import com.Tran.interpreter.BuiltIns.ConsoleWrite;
import com.Tran.interpreter.Interpreter;
import com.Tran.lexer.Lexer;
import com.Tran.parser.AST.TranNode;
import com.Tran.parser.Parser;
import com.Tran.utils.Token;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InterpreterService {

    // Map to store active emitters and associated console writers for each client
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, ConsoleWrite> consoleWriters = new ConcurrentHashMap<>();

    /**
     * Creates a new console emitter for the client.
     * @param clientId The unique client identifier.
     * @return The SseEmitter instance for the client.
     */
    public SseEmitter createConsoleEmitter(String clientId) {
        SseEmitter emitter = getEmitter(clientId);

        // Store the emitter for the client
        emitters.put(clientId, emitter);

        // Create a new ConsoleWrite instance and associate it with the client
        ConsoleWrite consoleWrite = new ConsoleWrite() {{
            isVariadic = true;
            isShared = true;
            name = "write";
            sseEmitter = emitter;
        }};
        consoleWriters.put(clientId, consoleWrite);

        // Send an immediate response to confirm connection
        try {
            sendEvent(emitter, "CONNECTED", "SSE connection established");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return emitter;
    }

    private SseEmitter getEmitter(String clientId) {
        SseEmitter emitter = new SseEmitter(30000L); // Set timeout to 30 seconds

        // Cleanup logic on completion or timeout
        emitter.onCompletion(() -> {
            emitters.remove(clientId);
            consoleWriters.remove(clientId);
            System.out.println("SSE connection completed for client: " + clientId);
        });

        emitter.onTimeout(() -> {
            emitters.remove(clientId);
            consoleWriters.remove(clientId);
            System.out.println("SSE connection timed out for client: " + clientId);
        });
        return emitter;
    }

    /**
     * Sends an event to the client's SseEmitter.
     * @param emitter The SseEmitter instance.
     * @param eventName The name of the event.
     * @param eventData The event data to send.
     * @throws IOException If sending the event fails.
     */
    public void sendEvent(SseEmitter emitter, String eventName, Object eventData) throws IOException {
        emitter.send(SseEmitter.event()
                .name(eventName)
                .data(eventData)
                .id(String.valueOf(System.currentTimeMillis())));
    }

    /**
     * Executes the provided code for the client.
     * @param code The code to execute.
     * @param clientId The unique client identifier.
     */
    public void executeCode(String code, String clientId) {
        SseEmitter emitter = emitters.get(clientId);
        ConsoleWrite consoleWrite = consoleWriters.get(clientId);

        if (emitter == null || consoleWrite == null) {
            System.out.println("No emitter or console writer found for client: " + clientId);
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                sendEvent(emitter, "STARTED", "Interpreting initiated");

                // Create a new interpreter for this execution
                TranNode ast = new TranNode();
                Lexer lexer = new Lexer(code);
                List<Token> tokens = lexer.Lex();
                Parser parser = new Parser(ast, tokens);
                parser.Tran();

                Interpreter interpreter = new Interpreter(ast, consoleWrite);
                interpreter.start();

                sendEvent(emitter, "EXECUTION_COMPLETED", "Execution completed");
            } catch (Exception e) {
                try {
                    sendEvent(emitter, "CONSOLE_OUTPUT", e.toString());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }
}

