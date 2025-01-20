package com.Tran.service;

import com.Tran.config.WebSocketConfig;
import com.Tran.interpreter.Interpreter;
import com.Tran.lexer.Lexer;
import com.Tran.parser.AST.TranNode;
import com.Tran.parser.Parser;
import com.Tran.utils.Token;
import com.Tran.utils.InterpreterWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Service
public class InterpreterService {
    @Autowired
    public InterpreterWebSocketHandler webSocketHandler;

    public void executeCode(String code, String sessionId) {
        WebSocketSession session = webSocketHandler.getSession(sessionId);
        try {
            TranNode ast = new TranNode();
            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.Lex();
            Parser parser = new Parser(ast, tokens);
            parser.Tran();
            Interpreter interpreter = new Interpreter(ast, session);
            interpreter.start();
        } catch (Exception e){
            // Send the error to the client
            webSocketHandler.sendOutput(sessionId, "Code Execution Failed: " + e.toString());
        }

    }
}
