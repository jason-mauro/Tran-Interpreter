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
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        System.out.println("Starting test on port: " + port);

        // Create a WebSocket client
        StandardWebSocketClient client = new StandardWebSocketClient();
        List<String> receivedMessages = new ArrayList<>();
        CompletableFuture<Void> messageFuture = new CompletableFuture<>();

        // WebSocket handler to process received messages
        TextWebSocketHandler clientHandler = new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                System.out.println("Received WebSocket message: " + message.getPayload());
                receivedMessages.add(message.getPayload());

                // Complete the future once all expected messages are received
                if (receivedMessages.size() == 3) {
                    messageFuture.complete(null);
                }
            }
        };

        // WebSocket connection details
        String wsUrl = String.format("ws://localhost:%d/interpreter", port);
        URI uri = URI.create(wsUrl);
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

        // Connect to the WebSocket
        System.out.println("Connecting to WebSocket at: " + wsUrl);
        WebSocketSession session = client.execute(clientHandler, headers, uri).get(5, TimeUnit.SECONDS);

        try {
            // Assert that the session is open and log session details
            assertTrue(session.isOpen(), "WebSocket session should be open");
            System.out.println("WebSocket connected successfully. Session ID: " + session.getId());

            // Send test code for execution
            String testCode = """
            {
                "code": "class demo\n\tshared start()\n\t\tconsole.write(\"hello\")"
            }
            """;

            System.out.println("Sending test code to API...");
            mvc.perform(post("/api/interpreter/execute")
                            .content(testCode)
                            .header("WebSocket-Session-Id", session.getId())
                            .contentType("application/json"))
                    .andExpect(status().isOk());
            System.out.println("API request completed successfully.");

            // Wait for all expected messages
            System.out.println("Waiting for messages from WebSocket...");
            messageFuture.get(10, TimeUnit.SECONDS);

            // Assert the messages received are as expected
            assertEquals(3, receivedMessages.size(), "Should receive 3 messages");
            System.out.println("Messages received: " + receivedMessages);
            assertEquals("0", receivedMessages.get(0));
            assertEquals("1", receivedMessages.get(1));
            assertEquals("2", receivedMessages.get(2));

        } finally {
            // Close the WebSocket session
            System.out.println("Closing WebSocket session...");
            session.close();
            System.out.println("WebSocket session closed.");
        }
    }
}
