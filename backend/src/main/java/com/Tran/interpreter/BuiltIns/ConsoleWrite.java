package com.Tran.interpreter.BuiltIns;

import com.Tran.parser.AST.BuiltInMethodDeclarationNode;
import com.Tran.interpreter.DataTypes.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ConsoleWrite extends BuiltInMethodDeclarationNode {
    public SseEmitter sseEmitter;
    public List<String> console = new LinkedList<>();

    @Override
    public List<InterpreterDataType> Execute(List<InterpreterDataType> params) {
        StringBuilder sb = new StringBuilder();
        for (var i : params) {
            sb.append(i.toString());
            System.out.print(i.toString());
        }
        System.out.println();
        console.add(sb.toString());

        // Send output to the frontend
        if (sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event()
                        .name("CONSOLE_OUTPUT")
                        .data(sb.toString())
                        .id(String.valueOf(System.currentTimeMillis())));
            } catch (IOException e) {
                System.err.println("Failed to send console output");
                sseEmitter.completeWithError(e);
            }
        }
        return List.of();
    }
}

