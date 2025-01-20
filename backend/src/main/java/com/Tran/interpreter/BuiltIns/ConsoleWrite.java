package com.Tran.interpreter.BuiltIns;

import com.Tran.parser.AST.BuiltInMethodDeclarationNode;
import com.Tran.interpreter.DataTypes.*;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ConsoleWrite extends BuiltInMethodDeclarationNode {
    public WebSocketSession session;
    public LinkedList<String> console = new LinkedList<>();
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
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(sb.toString()));
            } catch (IOException e) {
                System.err.println("Failed to send output to frontend: " + e.getMessage());
            }
        }
        return List.of();
    }
}

