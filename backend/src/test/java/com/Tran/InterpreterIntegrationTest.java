package com.Tran;

import com.Tran.service.InterpreterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class InterpreterServiceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private InterpreterService interpreterService;

    @Test
    void testInterpreterServiceExecutionFlow() throws Exception {
        // Step 1: Set up WebSocket client and handler
        StandardWebSocketClient client = new StandardWebSocketClient();
        List<String> receivedMessages = new ArrayList<>();
        CompletableFuture<Void> messageFuture = new CompletableFuture<>();

        TextWebSocketHandler clientHandler = new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                System.out.println("Received WebSocket message: " + message.getPayload());
                receivedMessages.add(message.getPayload());
                // Complete the future when 3 messages are received (expecting 3 outputs from console.write)
                if (receivedMessages.size() == 3) {
                    messageFuture.complete(null);
                }
            }
        };

        // Step 2: Establish WebSocket connection
        String wsUrl = String.format("ws://localhost:%d/interpreter", port);
        URI uri = URI.create(wsUrl);
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        WebSocketSession session = client.execute(clientHandler, headers, uri).get(5, TimeUnit.SECONDS);

        System.out.println("WebSocket session established: " + session.getId());

        try {
            // Step 3: Execute test code via REST API
            String testCode = """
                x = 0
                while x < 3:
                    console.write(x)
                    x = x + 1
                """;

            // Ensure the WebSocket session ID is passed correctly
            mvc.perform(post("/api/interpreter/execute")
                            .content(testCode)
                            .header("WebSocket-Session-Id", session.getId()) // Ensure this matches the WebSocket session ID
                            .contentType("application/json"))
                    .andExpect(status().isOk());

            // Step 4: Wait for messages with timeout (ensuring all expected messages are received)
            messageFuture.get(10, TimeUnit.SECONDS);

            // Step 5: Verify received messages
            assertEquals(3, receivedMessages.size(), "Should receive 3 messages");
            assertEquals("0", receivedMessages.get(0)); // First output should be "0"
            assertEquals("1", receivedMessages.get(1)); // Second output should be "1"
            assertEquals("2", receivedMessages.get(2)); // Third output should be "2"

        } finally {
            // Step 6: Clean up
            session.close();
            System.out.println("WebSocket session closed.");
        }
    }
}
