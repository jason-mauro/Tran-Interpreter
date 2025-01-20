// InterpreterWebSocketHandlerTest.java
package com.Tran;
import com.Tran.utils.InterpreterWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import static org.mockito.Mockito.*;

class InterpreterWebSocketHandlerTest {

    @Test
    void testSessionManagement() {
        InterpreterWebSocketHandler handler = new InterpreterWebSocketHandler();
        WebSocketSession mockSession = mock(WebSocketSession.class);

        // Test session connection
        when(mockSession.getId()).thenReturn("test-session-id");
        handler.afterConnectionEstablished(mockSession);

        // Verify session is stored
        WebSocketSession retrievedSession = handler.getSession("test-session-id");
        assert retrievedSession == mockSession;
    }

    @Test
    void testMessageSending() throws Exception {
        InterpreterWebSocketHandler handler = new InterpreterWebSocketHandler();
        WebSocketSession mockSession = mock(WebSocketSession.class);

        when(mockSession.getId()).thenReturn("test-session-id");
        when(mockSession.isOpen()).thenReturn(true);

        handler.afterConnectionEstablished(mockSession);
        handler.sendOutput("test-session-id", "Test message");

        verify(mockSession).sendMessage(any(TextMessage.class));
    }
}