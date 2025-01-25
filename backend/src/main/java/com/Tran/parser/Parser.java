package com.Tran.parser;

import com.Tran.parser.AST.*;
import com.Tran.utils.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Parser {
    TokenManager tokenManager;
    TranNode top;

    public Parser(TranNode top, List<Token> tokens) {
        this.top = top;
        this.tokenManager = new TokenManager(tokens);
    }

    // Parse Classes and Interfaces in the program
    // Tran = { Interface } | { Class }
    public void Tran() throws SyntaxErrorException {
        SkipNewLine(); // Skip any empty lines in the beginning of the file
        while(!tokenManager.done()){
            var interfaceNode = Interface();
            if (interfaceNode.isPresent()) {
                for (var i: top.Interfaces) {
                    if (interfaceNode.get().name.equals(i.name)){
                        throw new RuntimeException("Interface: " + i.name + " already exists");
                    }
                }
                top.Interfaces.add(interfaceNode.get());
            }
            // Since one file can only have 1 class only need to try for it once
            var classNode = Class();
            if (classNode.isPresent()) {
                for (var c : top.Classes) {
                    if (classNode.get().name.equals(c.name)) {
                        throw new RuntimeException("Class: " + c.name + " already exists");
                    }
                }
                top.Classes.add(classNode.get());
            }
            if (!tokenManager.done() && interfaceNode.isEmpty() && classNode.isEmpty()) throw new SyntaxErrorException("Interface or Class Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        }


    }


    // Method to parse Classes
    // Class = "class" Identifier [ "implements" Identifier { "," Identifier } ] NEWLINE INDENT { Constructor NEWLINE | MethodDeclaration NEWLINE | Member NEWLINE } DEDENT
    private Optional<ClassNode> Class() throws SyntaxErrorException {
        if (tokenManager.matchAndRemove(Token.TokenTypes.CLASS).isEmpty()) return Optional.empty();
        ClassNode classNode = new ClassNode();
        Optional<Token> nameToken = tokenManager.matchAndRemove(Token.TokenTypes.WORD);
        if (nameToken.isEmpty()) throw new SyntaxErrorException("Identifier Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        nameToken.ifPresent(token -> classNode.name = token.getValue());
        if (tokenManager.matchAndRemove(Token.TokenTypes.IMPLEMENTS).isPresent()) {
            Optional<Token> name = tokenManager.matchAndRemove(Token.TokenTypes.WORD);
            name.ifPresent(token -> classNode.interfaces.add(token.getValue()));
            while(tokenManager.matchAndRemove(Token.TokenTypes.COMMA).isPresent()) {
                name = tokenManager.matchAndRemove(Token.TokenTypes.WORD);
                if (name.isEmpty()) throw new SyntaxErrorException("Identifier Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
                name.ifPresent(token -> classNode.interfaces.add(token.getValue()));
            }
        }
        RequireNewLine();
        if (tokenManager.matchAndRemove(Token.TokenTypes.INDENT).isEmpty()) throw new SyntaxErrorException("Indent Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        while(tokenManager.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty()) {
            Optional<ConstructorNode> cn = Constructor();
            cn.ifPresent(constructorNode -> classNode.constructors.add(constructorNode));
            Optional<List<MemberNode>> mn = Member();
            mn.ifPresent(memberNodes -> classNode.members.addAll(memberNodes));
            Optional<MethodDeclarationNode> mdn = MethodDeclaration();
            mdn.ifPresent(methodNode -> classNode.methods.add(methodNode));
            if (cn.isEmpty() && mn.isEmpty() && mdn.isEmpty()) throw new SyntaxErrorException("Unexpected Token (Constructor, Member, or MethodDeclaration expected)", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        }
        return Optional.of(classNode);
    }

    // Method to parse MemberNodes
    // Member = VariableDeclaration ({, Word}) NEWLINE | NEWLINE INDENT ["accessor:" NEWLINE Statements] ["mutator:" NEWLINE Statements] DEDENT
    private Optional<List<MemberNode>> Member() throws SyntaxErrorException {
        List<MemberNode> memberNodes = new ArrayList<>();
        MemberNode memberNode = new MemberNode();
        if (tokenManager.peek(0).isPresent() && tokenManager.peek(1).isPresent() && tokenManager.peek(2).isPresent() && tokenManager.peek(0).get().getType().equals(Token.TokenTypes.SHARED) && (!tokenManager.peek(1).get().getType().equals(Token.TokenTypes.WORD) || !tokenManager.peek(2).get().getType().equals(Token.TokenTypes.WORD))) return Optional.empty();
        if (tokenManager.matchAndRemove(Token.TokenTypes.SHARED).isPresent()) {
            memberNode.isShared = true;
        }
        var member = VariableDeclaration();
        if (member.isEmpty()) return Optional.empty(); // No variable Declaration
        member.ifPresent(variableDeclarationNode -> memberNode.declaration = variableDeclarationNode);
        if (tokenManager.peek(0).isPresent() && tokenManager.peek(0).get().getType().equals(Token.TokenTypes.COMMA)){
            // Add the processed member node and them add the rest of the declarations
            memberNodes.add(memberNode);
            while(tokenManager.matchAndRemove(Token.TokenTypes.COMMA).isPresent()) {
                boolean shared = memberNode.isShared;
                Optional<Token> name = tokenManager.matchAndRemove(Token.TokenTypes.WORD);
                if (name.isEmpty()) throw new SyntaxErrorException("Identifier Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
                name.ifPresent(token -> memberNodes.add(new MemberNode(){{this.isShared = shared; this.declaration = new VariableDeclarationNode(){{this.type = member.get().type; this.name = token.getValue();}};}}));
            }
            if (tokenManager.peek(0).isPresent() && !tokenManager.peek(0).get().getType().equals(Token.TokenTypes.DEDENT)) RequireNewLine();
            return Optional.of(memberNodes);
        }
        // Not at the end of a class must be a newline
        if (tokenManager.peek(0).isPresent() && !tokenManager.peek(0).get().getType().equals(Token.TokenTypes.DEDENT)) RequireNewLine();
        // No accessor or mutator so just return the member node
        if (tokenManager.matchAndRemove(Token.TokenTypes.INDENT).isEmpty()) {
            memberNodes.add(memberNode);
            return Optional.of(memberNodes);
        }
        // Check for the ACCESSOR AND MUTATORS
        if (tokenManager.matchAndRemove(Token.TokenTypes.ACCESSOR).isPresent()) {
            memberNode.accessor = processAccessorOrMutator();
            if (tokenManager.matchAndRemove(Token.TokenTypes.MUTATOR).isPresent()) {
                memberNode.mutator = processAccessorOrMutator();
            }
        } else if (tokenManager.matchAndRemove(Token.TokenTypes.MUTATOR).isPresent()){
            memberNode.mutator = processAccessorOrMutator();
            if (tokenManager.matchAndRemove(Token.TokenTypes.ACCESSOR).isPresent()) {
                memberNode.accessor = processAccessorOrMutator();
            }
        } else throw new SyntaxErrorException("Accessor or Mutator Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        if (tokenManager.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty()) throw new SyntaxErrorException("Invalid member statement (Dedent Expected)", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        memberNodes.add(memberNode);
        return Optional.of(memberNodes);
    }

    // Method to parse the statements for Accessors and Mutators
    Optional<List<StatementNode>> processAccessorOrMutator() throws SyntaxErrorException {
        if (tokenManager.matchAndRemove(Token.TokenTypes.COLON).isEmpty()) throw new SyntaxErrorException("':' Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        RequireNewLine();
        Optional<List<StatementNode>> statementNodes = Statements();
        if (statementNodes.isEmpty()) throw new SyntaxErrorException("Accessor Requires a statementBlock", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        return statementNodes;
    }

    // Constructor = "construct" "(" VariableDeclarations ")" NEWLINE MethodBody
    private Optional<ConstructorNode> Constructor() throws SyntaxErrorException {
        if (tokenManager.matchAndRemove(Token.TokenTypes.CONSTRUCT).isEmpty()) return Optional.empty();
        ConstructorNode constructorNode = new ConstructorNode();
        if (tokenManager.matchAndRemove(Token.TokenTypes.LPAREN).isEmpty()) throw new SyntaxErrorException("'(' Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        Optional<List<VariableDeclarationNode>> params = VariableDeclarations();
        params.ifPresent(variableDeclarationNodes -> constructorNode.parameters = variableDeclarationNodes); // Set the parameters if they are present
        if (tokenManager.matchAndRemove(Token.TokenTypes.RPAREN).isEmpty()) throw new SyntaxErrorException("')' Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        RequireNewLine();
        if (tokenManager.matchAndRemove(Token.TokenTypes.INDENT).isEmpty()) throw new SyntaxErrorException("Indent Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        while(tokenManager.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty()) {
            Optional<VariableDeclarationNode> vn = VariableDeclaration();
            vn.ifPresent(variableDeclarationNode -> constructorNode.locals.add(variableDeclarationNode));
            if (vn.isPresent()){
                if (tokenManager.peek(0).isPresent() && tokenManager.peek(0).get().getType().equals((Token.TokenTypes.ASSIGN))) {
                    var AssignmentNode = Assignment((new VariableReferenceNode(){{this.name = vn.get().name;}}));
                    if (AssignmentNode.isEmpty()){
                        throw new SyntaxErrorException("Assignment Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
                    }
                    constructorNode.statements.add(AssignmentNode.get());
                }
                // Check for list of declarations
                if (tokenManager.peek(0).isPresent() && tokenManager.peek(0).get().getType().equals(Token.TokenTypes.COMMA)) {
                    while(tokenManager.matchAndRemove(Token.TokenTypes.COMMA).isPresent()) {
                        Optional<Token> varNode = tokenManager.matchAndRemove(Token.TokenTypes.WORD);
                        if (varNode.isEmpty()) throw new SyntaxErrorException("Identifier Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
                        varNode.ifPresent(token -> constructorNode.locals.add(new VariableDeclarationNode(){{this.type = vn.get().type; this.name = token.getValue();}}));
                        if (tokenManager.peek(0).isPresent() && tokenManager.peek(0).get().getType().equals((Token.TokenTypes.ASSIGN))) {
                            var AssignmentNode = Assignment((new VariableReferenceNode(){{this.name = varNode.get().getValue();}}));
                            if (AssignmentNode.isEmpty()){
                                throw new SyntaxErrorException("Assignment Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
                            }
                            constructorNode.statements.add(AssignmentNode.get());
                        }
                    }
                }
            }
            if (vn.isPresent() && tokenManager.peek(0).isPresent() && !tokenManager.peek(0).get().getType().equals(Token.TokenTypes.DEDENT)) RequireNewLine();
            Optional<StatementNode> sn = Optional.empty();
            if (vn.isEmpty()){
                sn = Statement();
                sn.ifPresent(statementNode -> constructorNode.statements.add(statementNode));
                if (sn.isPresent() && (sn.get() instanceof AssignmentNode || sn.get() instanceof MethodCallStatementNode) && (tokenManager.peek(0).isPresent() && !tokenManager.peek(0).get().getType().equals(Token.TokenTypes.DEDENT))) RequireNewLine();
            }
            if (sn.isEmpty() && vn.isEmpty()) throw new SyntaxErrorException("Method Body requires a Variable Declaration or statement", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        }
        return Optional.of(constructorNode);
    }

    // MethodDeclaration = ["private"] ["shared"] MethodHeader NEWLINE MethodBody
    // MethodBody = INDENT { VariableDeclaration NEWLINE } {Statement} DEDENT
    private Optional<MethodDeclarationNode> MethodDeclaration() throws SyntaxErrorException {
        MethodDeclarationNode methodDeclarationNode = new MethodDeclarationNode();
        if (tokenManager.peek(0).isPresent() && !tokenManager.peek(0).get().getType().equals(Token.TokenTypes.PRIVATE) && !tokenManager.peek(0).get().getType().equals(Token.TokenTypes.SHARED) && !tokenManager.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.LPAREN) ) return Optional.empty();
        if (tokenManager.matchAndRemove(Token.TokenTypes.PRIVATE).isPresent()) methodDeclarationNode.isPrivate = true;
        else if (tokenManager.matchAndRemove(Token.TokenTypes.SHARED).isPresent()) methodDeclarationNode.isShared = true;
        Optional<MethodHeaderNode> methodHeader = MethodHeader();
        if (methodHeader.isEmpty()) throw new SyntaxErrorException("MethodHeader Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        // Set the values from the method header into the method declaration node
        methodDeclarationNode.name = methodHeader.get().name;
        methodDeclarationNode.parameters = methodHeader.get().parameters;
        methodDeclarationNode.returns = methodHeader.get().returns;
        RequireNewLine();
        if (tokenManager.matchAndRemove(Token.TokenTypes.INDENT).isEmpty()) throw new SyntaxErrorException("Indent Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        while(tokenManager.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty()) {
            Optional<VariableDeclarationNode> vn = VariableDeclaration();
            vn.ifPresent(variableDeclarationNode -> methodDeclarationNode.locals.add(variableDeclarationNode));
            if (vn.isPresent()){
                if (tokenManager.peek(0).isPresent() && tokenManager.peek(0).get().getType().equals((Token.TokenTypes.ASSIGN))) {
                    var AssignmentNode = Assignment((new VariableReferenceNode(){{this.name = vn.get().name;}}));
                    if (AssignmentNode.isEmpty()){
                        throw new SyntaxErrorException("Assignment Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
                    }
                    methodDeclarationNode.statements.add(AssignmentNode.get());
                }
                // Check for list of declarations
                if (tokenManager.peek(0).isPresent() && tokenManager.peek(0).get().getType().equals(Token.TokenTypes.COMMA)) {
                    while(tokenManager.matchAndRemove(Token.TokenTypes.COMMA).isPresent()) {
                        Optional<Token> varNode = tokenManager.matchAndRemove(Token.TokenTypes.WORD);
                        if (varNode.isEmpty()) throw new SyntaxErrorException("Identifier Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
                        varNode.ifPresent(token -> methodDeclarationNode.locals.add(new VariableDeclarationNode(){{this.type = vn.get().type; this.name = token.getValue();}}));
                        if (tokenManager.peek(0).isPresent() && tokenManager.peek(0).get().getType().equals((Token.TokenTypes.ASSIGN))) {
                            var AssignmentNode = Assignment((new VariableReferenceNode(){{this.name = varNode.get().getValue();}}));
                            if (AssignmentNode.isEmpty()){
                                throw new SyntaxErrorException("Assignment Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
                            }
                            methodDeclarationNode.statements.add(AssignmentNode.get());
                        }
                    }
                }
            }
            if (vn.isPresent() && tokenManager.peek(0).isPresent() && !tokenManager.peek(0).get().getType().equals(Token.TokenTypes.DEDENT)) RequireNewLine();
            Optional<StatementNode> sn = Optional.empty();
            if (vn.isEmpty()){
                sn = Statement();
                sn.ifPresent(statementNode -> methodDeclarationNode.statements.add(statementNode));
                if (sn.isPresent() && (sn.get() instanceof AssignmentNode || sn.get() instanceof MethodCallStatementNode) && (tokenManager.peek(0).isPresent() && !tokenManager.peek(0).get().getType().equals(Token.TokenTypes.DEDENT))) RequireNewLine();
            }
            if (sn.isEmpty() && vn.isEmpty()) throw new SyntaxErrorException("Method Body requires a Variable Declaration or statement", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        }
        return Optional.of(methodDeclarationNode);
    }

    // Statements = INDENT {Statement NEWLINE } DEDENT  (NEWLINE IS ONLY REQUIRED FOR ASSIGNMENT AND METHOD CALLS)
    private Optional<List<StatementNode>> Statements() throws SyntaxErrorException {
        if (tokenManager.matchAndRemove(Token.TokenTypes.INDENT).isEmpty()) return Optional.empty();
        List<StatementNode> statementNodes = new ArrayList<>();
        while(tokenManager.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty()) {
            Optional<StatementNode> sn = Statement();
            if (sn.isEmpty()) throw new SyntaxErrorException("Invalid Statement in Statement body", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
            sn.ifPresent(statementNodes::add);
            if ((sn.get() instanceof AssignmentNode || sn.get() instanceof MethodCallStatementNode) && (tokenManager.peek(0).isPresent() && !tokenManager.peek(0).get().getType().equals(Token.TokenTypes.DEDENT))) RequireNewLine();
        }
        return Optional.of(statementNodes);
    }

    // Statement = If | Loop | MethodCall | Assignment
    private Optional<StatementNode> Statement() throws SyntaxErrorException {
        var If = If();
        if (If.isPresent()) return Optional.of(If.get());
        var Loop = Loop(); // Try for normal loop without assignment
        if (Loop.isPresent()) return Optional.of(Loop.get());
        return disambiguate();
    }

    // If = "if" BoolExp NEWLINE Statements ["else" NEWLINE (Statement | Statements)] ASK IF STATEMENTS CAN BE EMPTY
    Optional<IfNode> If() throws SyntaxErrorException {
        if (tokenManager.matchAndRemove(Token.TokenTypes.IF).isEmpty()) return Optional.empty();
        IfNode ifNode = new IfNode();
        Optional<ExpressionNode> condition = BoolExpTerm();
        if (condition.isEmpty()) throw new SyntaxErrorException("BooleanExpression Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        if (!(condition.get() instanceof CompareNode) && !(condition.get() instanceof BooleanOpNode) && !(condition.get() instanceof BooleanLiteralNode) && !(condition.get() instanceof VariableReferenceNode) && !(condition.get() instanceof MethodCallExpressionNode)) throw new SyntaxErrorException("Invalid Boolean expression as If condition", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        ifNode.condition = condition.get();
        RequireNewLine();
        Optional<List<StatementNode>> statements = Statements();
        if (statements.isEmpty()) throw new SyntaxErrorException("If Requires a statementBlock", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        ifNode.statements = statements.get();
        if (tokenManager.matchAndRemove(Token.TokenTypes.ELSE).isEmpty()) {
            ifNode.elseStatement = Optional.empty();
            return Optional.of(ifNode);
        }
        // else If Statement on the same line as else (DO NOT BELIEVE THIS IS REQUIRED BUT IDK)
        if (tokenManager.peek(0).isPresent() && tokenManager.peek(0).get().getType().equals(Token.TokenTypes.IF)) {
            var statementNode = Statement();
            statementNode.ifPresent(node -> ifNode.elseStatement = Optional.of(new ElseNode(){{this.statements.add(node);}}));
        } else {
            RequireNewLine();
            var elseStatements = Statements();
            if (elseStatements.isEmpty()) throw new SyntaxErrorException("ELSE Requires a statementBlock", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
            ifNode.elseStatement = Optional.of(new ElseNode(){{this.statements = elseStatements.get();}});
        }
        return Optional.of(ifNode);
    }

    // Loop = "loop" [VariableReference "=" ] ( BoolExpTerm ) NEWLINE Statements
    Optional<LoopNode> Loop() throws SyntaxErrorException {
        LoopNode loopNode = new LoopNode();
        if (tokenManager.matchAndRemove(Token.TokenTypes.LOOP).isEmpty()) return Optional.empty();
        if (tokenManager.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.ASSIGN)) {
            var assignmentNode = VariableReference();
            if (assignmentNode.isEmpty()) throw new SyntaxErrorException("VariableReference Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
            tokenManager.matchAndRemove(Token.TokenTypes.ASSIGN);
            loopNode.assignment = assignmentNode;
        } else {
            loopNode.assignment = Optional.empty();
        }
        Optional<ExpressionNode> condition = BoolExpTerm();
        if (condition.isEmpty()) throw new SyntaxErrorException("BooleanExpression Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        if (!(condition.get() instanceof CompareNode) && !(condition.get() instanceof BooleanOpNode) && !(condition.get() instanceof BooleanLiteralNode) && !(condition.get() instanceof VariableReferenceNode) && !(condition.get() instanceof MethodCallExpressionNode)) throw new SyntaxErrorException("Invalid Boolean expression as loop condition", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        loopNode.expression = condition.get();
        RequireNewLine();
        Optional<List<StatementNode>> statements = Statements();
        if (statements.isEmpty()) throw new SyntaxErrorException("Loop must contain statementBlock", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        loopNode.statements = statements.get();
        return Optional.of(loopNode);
    }

    // PARSER 1 BELOW!

    // Interface = "interface" Identifier NEWLINE INDENT {MethodHeader NEWLINE } DEDENT
    private Optional<InterfaceNode> Interface() throws SyntaxErrorException {
        if (tokenManager.matchAndRemove(Token.TokenTypes.INTERFACE).isEmpty()) return Optional.empty();
        Optional<Token> nameToken = tokenManager.matchAndRemove(Token.TokenTypes.WORD);
        if (nameToken.isEmpty()) throw new SyntaxErrorException("Identifier Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        RequireNewLine();
        if (tokenManager.matchAndRemove(Token.TokenTypes.INDENT).isEmpty()) throw new SyntaxErrorException("Indent Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        List<MethodHeaderNode> methodNodes= new ArrayList<>();
        do {
            var method = MethodHeader();
            if (method.isEmpty()) throw new SyntaxErrorException("Method Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
            methodNodes.add(method.get());
            if (tokenManager.peek(0).isPresent() && !tokenManager.peek(0).get().getType().equals(Token.TokenTypes.DEDENT)) RequireNewLine(); // If there is no DEDENT after a method header it must be a NEWLINE
        } while (tokenManager.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty());
        return Optional.of(new InterfaceNode(){{this.name = nameToken.get().getValue(); this.methods = methodNodes;}});
    }

    // MethodHeader = Identifier "(" VariableDeclarations ")" [ ":" VariableDeclaration { "," VariableDeclaration }]
    private Optional<MethodHeaderNode> MethodHeader() throws SyntaxErrorException {
        Optional<Token> methodNameToken = tokenManager.matchAndRemove(Token.TokenTypes.WORD);
        if (methodNameToken.isEmpty()) return Optional.empty();
        MethodHeaderNode methodNode = new MethodHeaderNode();
        methodNode.name = methodNameToken.get().getValue();
        if (tokenManager.matchAndRemove(Token.TokenTypes.LPAREN).isEmpty()) throw new SyntaxErrorException("'(' Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        Optional<List<VariableDeclarationNode>> params = VariableDeclarations();
        params.ifPresent(variableDeclarationNodes -> methodNode.parameters = variableDeclarationNodes); // Set the parameters if they are present
        if (tokenManager.matchAndRemove(Token.TokenTypes.RPAREN).isEmpty()) throw new SyntaxErrorException("')' Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        if (tokenManager.matchAndRemove(Token.TokenTypes.COLON).isPresent()) {
            Optional<List<VariableDeclarationNode>> retVals = VariableDeclarations();
            if (retVals.isEmpty()) throw new SyntaxErrorException("VariableDeclarations Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
            methodNode.returns = retVals.get();
        }
        return Optional.of(methodNode);
    }

    // VariableDeclarations = [ VariableDeclaration ] | VariableDeclaration { "," VariableDeclaration }
    private Optional<List<VariableDeclarationNode>> VariableDeclarations() throws SyntaxErrorException {
        List<VariableDeclarationNode> vars = new ArrayList<>();
        var variable = VariableDeclaration();
        variable.ifPresent(vars::add);
        while(tokenManager.matchAndRemove(Token.TokenTypes.COMMA).isPresent()) {
            variable = VariableDeclaration();
            if (variable.isEmpty()) throw new SyntaxErrorException("Identifier Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
            vars.add(variable.get());
        }
        return vars.isEmpty() ? Optional.empty() : Optional.of(vars);
    }

    // VariableDeclaration = Identifier Identifier
    private Optional<VariableDeclarationNode> VariableDeclaration() throws SyntaxErrorException {
        if (!tokenManager.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.WORD)) return Optional.empty();
        var typeToken = tokenManager.matchAndRemove(Token.TokenTypes.WORD);
        if (typeToken.isEmpty()) return Optional.empty(); // Suppress errors
        var nameToken = tokenManager.matchAndRemove(Token.TokenTypes.WORD);
        if (nameToken.isEmpty()) throw new SyntaxErrorException("Identifier Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber()); // Suppress Errors
        return Optional.of(new VariableDeclarationNode(){{this.type = typeToken.get().getValue(); this.name = nameToken.get().getValue();}});
    }
    // Parser 3 Start

    // BoolExpTerm = BoolExpFactor {("and"|"or") BoolExpTerm} | "not" BoolExpTerm
    // BooleanOpNode or NotOpNode
    private Optional<ExpressionNode> BoolExpTerm() throws SyntaxErrorException {
        Optional<ExpressionNode> currentNode = this.AndTerm();
        if (currentNode.isPresent()){
            return getRightOfOrTerm(currentNode.get());
        }
        return Optional.empty();
    }

    // Parse consecutive AndTerms that are ORed together (order of operations (+/-))
    private Optional<ExpressionNode> getRightOfOrTerm(ExpressionNode leftNode) throws SyntaxErrorException {
        Optional<Token> ORToken = tokenManager.matchAndRemove(Token.TokenTypes.OR);
        if (ORToken.isEmpty()) return Optional.of(leftNode);
        Optional<ExpressionNode> rightNode = this.AndTerm();
        if (rightNode.isEmpty()) throw new SyntaxErrorException("Found && but not Boolean factor after", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        ExpressionNode expressionNode = new BooleanOpNode(){{this.left = leftNode; this.op = BooleanOperations.or; this.right = rightNode.get();}};
        return getRightOfOrTerm(expressionNode);
    }

    // Parse a BooleanTerm and consecutive AND terms if they are there (order of operations (Times/divide)
    private Optional<ExpressionNode> AndTerm() throws SyntaxErrorException {
        Optional<ExpressionNode> leftNode = BoolExpFactor();
        if (leftNode.isPresent()){
            return getRightOfAndTerm(leftNode.get());
        }
        return Optional.empty();
    }

    // Parse consecutive AND statements
    private Optional<ExpressionNode> getRightOfAndTerm(ExpressionNode leftNode) throws SyntaxErrorException {
        Optional<Token> ANDToken = tokenManager.matchAndRemove(Token.TokenTypes.AND);
        if (ANDToken.isEmpty()) return Optional.of(leftNode);
        Optional<ExpressionNode> rightNode = BoolExpFactor();
        if (rightNode.isEmpty()) throw new SyntaxErrorException("Found && but not Boolean factor after", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        ExpressionNode expressionNode = new BooleanOpNode(){{this.left = leftNode; this.op = BooleanOperations.and; this.right = rightNode.get();}};
        return getRightOfAndTerm(expressionNode);
    }


    // x = method(a && b, c) + 2
    // BoolExpFactor = "(" BoolExpTerm ")" | MethodCallExpression | (Expression ( "==" | "!=" | "<=" | ">=" | ">" | "<" ) Expression) | VariableReference
    // compareNode or variableReferenceNode or methodCallExpressionNode or BooleanLiteral or BoolExpTerm
    // TODO: Ask if this is correct or if I should check for what type of node expression it is and only allow certain ones (Most likely just check in interpreter)
    private Optional<ExpressionNode> BoolExpFactor() throws SyntaxErrorException {
        // Check for parentheses and call the boolExpTerm to get the boolean expression inside it
        if (tokenManager.matchAndRemove(Token.TokenTypes.LPAREN).isPresent()) {
            var boolExpTerm = BoolExpTerm();
            if (boolExpTerm.isEmpty()) throw new SyntaxErrorException("'(' Found but no Boolean Expression", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
            if (tokenManager.matchAndRemove(Token.TokenTypes.RPAREN).isEmpty()) throw new SyntaxErrorException("')' Expected here ", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
            return boolExpTerm;
        }
        // Process Not Op Nodes
        if (tokenManager.matchAndRemove(Token.TokenTypes.NOT).isPresent()) {
            var boolExpFactor = BoolExpFactor();
            if (boolExpFactor.isEmpty())
                throw new SyntaxErrorException("Boolean Expression Term Expected after NOT", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
            return Optional.of(new NotOpNode() {{
                this.left = boolExpFactor.get();
            }});
        }

        // Expression will return both a methodCallExpression or variable reference so just need to check for the compare operators
        var expressionNode = ExpressionForBoolExp();
        if (expressionNode.isEmpty()) return Optional.empty();
        CompareNode compareNode = new CompareNode();
        // Check for operators and if there are none return the expression Node
        if (tokenManager.matchAndRemove(Token.TokenTypes.EQUAL).isPresent()) {
            compareNode.op = CompareNode.CompareOperations.eq;
        } else if (tokenManager.matchAndRemove(Token.TokenTypes.NOTEQUAL).isPresent()) {
            compareNode.op = CompareNode.CompareOperations.ne;
        } else if (tokenManager.matchAndRemove(Token.TokenTypes.LESSTHANEQUAL).isPresent()) {
            compareNode.op = CompareNode.CompareOperations.le;
        } else if (tokenManager.matchAndRemove(Token.TokenTypes.GREATERTHANEQUAL).isPresent()) {
            compareNode.op = CompareNode.CompareOperations.ge;
        } else if (tokenManager.matchAndRemove(Token.TokenTypes.GREATERTHAN).isPresent()) {
            compareNode.op = CompareNode.CompareOperations.gt;
        } else if (tokenManager.matchAndRemove(Token.TokenTypes.LESSTHAN).isPresent()) {
            compareNode.op = CompareNode.CompareOperations.lt;
        } else {
            // Check for valid boolean expressions in the LOOP and IF parsing
            return expressionNode;
        }
        compareNode.left = expressionNode.get();
        var rightExpressionNode = ExpressionForBoolExp();
        if (rightExpressionNode.isEmpty()) throw new SyntaxErrorException("Expression Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        compareNode.right = rightExpressionNode.get();
        return Optional.of(compareNode);
    }

    // VariableReference = Identifier [. Identifier]
    private Optional<VariableReferenceNode> VariableReference() throws SyntaxErrorException {
        Optional<Token> nameToken = tokenManager.matchAndRemove(Token.TokenTypes.WORD);
        if (nameToken.isPresent()){
            return Optional.of(new VariableReferenceNode(){{this.name = nameToken.get().getValue();}});
        }
        return Optional.empty();
    }

    // TODO: Update this method with the new MethodCallExpression Node format for chain calls
    // TODO: LOOK AHEAD AND VERIFY THIS IS A METHOD CALL EXPRESSION BEFORE PARSING
    // MethodCallExpression = [Identifier "."] Identifier "(" [Expression {"," Expression }] ")"
    private Optional<ExpressionNode> MethodCallExpression() throws SyntaxErrorException {
        if (!tokenManager.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.DOT) && !tokenManager.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.LPAREN)) return Optional.empty();
        var nameToken = tokenManager.matchAndRemove(Token.TokenTypes.WORD);
        // Static Method
        if (tokenManager.matchAndRemove(Token.TokenTypes.LPAREN).isPresent()) {
            var mce = new MethodCallExpressionNode();
            mce.object = Optional.empty();
            mce.methodName = nameToken.get().getValue();
            var expression = Expression();
            if (expression.isEmpty()){
                if (tokenManager.matchAndRemove(Token.TokenTypes.RPAREN).isEmpty()){
                    throw new SyntaxErrorException("')' expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
                }
                mce.parameters = List.of();
                return ParseChainedMethodCallExpression(mce);
            }
            List<ExpressionNode> parameters = new ArrayList<>();
            parameters.add(expression.get());
            while(tokenManager.matchAndRemove(Token.TokenTypes.COMMA).isPresent()){
                var expressionNode = Expression();
                if (expressionNode.isEmpty()){
                    throw new SyntaxErrorException("Expression expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
                }
                parameters.add(expressionNode.get());
            }
            mce.parameters = parameters;
            if (tokenManager.matchAndRemove(Token.TokenTypes.RPAREN).isEmpty()){
                throw new SyntaxErrorException("')' expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
            }
            return ParseChainedMethodCallExpression(mce);
        }
        // Name.
        tokenManager.matchAndRemove(Token.TokenTypes.DOT);
        if (tokenManager.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.LPAREN)) {
            var mce = new MethodCallExpressionNode();
            mce.object = Optional.of(new VariableReferenceNode(){{this.name = nameToken.get().getValue();}});
            var methodName = tokenManager.matchAndRemove(Token.TokenTypes.WORD);
            mce.methodName = methodName.get().getValue();
            tokenManager.matchAndRemove(Token.TokenTypes.LPAREN);
            var expression = Expression();
            if (expression.isEmpty()){
                if (tokenManager.matchAndRemove(Token.TokenTypes.RPAREN).isEmpty()){
                    throw new SyntaxErrorException("')' expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
                }
                mce.parameters = List.of();
                return ParseChainedMethodCallExpression(mce);
            }
            List<ExpressionNode> parameters = new ArrayList<>();
            parameters.add(expression.get());
            while(tokenManager.matchAndRemove(Token.TokenTypes.COMMA).isPresent()){
                var expressionNode = Expression();
                if (expressionNode.isEmpty()){
                    throw new SyntaxErrorException("Expression expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
                }
                parameters.add(expressionNode.get());
            }
            mce.parameters = parameters;
            if (tokenManager.matchAndRemove(Token.TokenTypes.RPAREN).isEmpty()){
                throw new SyntaxErrorException("')' expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
            }
            return ParseChainedMethodCallExpression(mce);
        } else { // Name.name
            var memberName = tokenManager.matchAndRemove(Token.TokenTypes.WORD);
            if (memberName.isEmpty()) throw new SyntaxErrorException("identifier expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
            var memberExpression = new MemberExpressionNode(new VariableReferenceNode(){{this.name = nameToken.get().getValue();}},new VariableReferenceNode(){{this.name = memberName.get().getValue();}});
            return ParseChainedMethodCallExpression(memberExpression);
        }
    }



    private Optional<ExpressionNode> ParseChainedMethodCallExpression(ExpressionNode mce) throws SyntaxErrorException {
        if (tokenManager.matchAndRemove(Token.TokenTypes.DOT).isEmpty()) return Optional.of(mce);
        var nameToken = tokenManager.matchAndRemove(Token.TokenTypes.WORD);
        if (nameToken.isEmpty()) throw new SyntaxErrorException("identifier expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        if (tokenManager.matchAndRemove(Token.TokenTypes.LPAREN).isPresent()) {
            var methodCall = new MethodCallExpressionNode();
            methodCall.object = Optional.of(mce);
            methodCall.methodName = nameToken.get().getValue();
            var expression = Expression();
            if (expression.isEmpty()){
                if (tokenManager.matchAndRemove(Token.TokenTypes.RPAREN).isEmpty()){
                    throw new SyntaxErrorException("')' expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
                }
                methodCall.parameters = List.of();
                return ParseChainedMethodCallExpression(methodCall);
            }
            List<ExpressionNode> parameters = new ArrayList<>();
            parameters.add(expression.get());
            while(tokenManager.matchAndRemove(Token.TokenTypes.COMMA).isPresent()){
                var expressionNode = Expression();
                if (expressionNode.isEmpty()){
                    throw new SyntaxErrorException("Expression expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
                }
                parameters.add(expressionNode.get());
            }
            methodCall.parameters = parameters;
            if (tokenManager.matchAndRemove(Token.TokenTypes.RPAREN).isEmpty()){
                throw new SyntaxErrorException("')' expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
            }
            return ParseChainedMethodCallExpression(methodCall);
        } else {
            var memberExpression = new MemberExpressionNode(mce, new VariableReferenceNode(){{this.name = nameToken.get().getValue();}});
            return ParseChainedMethodCallExpression(memberExpression);
        }
    }

    private Optional<StatementNode> disambiguate() throws SyntaxErrorException {
        var expression = MethodCallExpression();
        if (expression.isPresent()) {
            if (expression.get() instanceof MethodCallExpressionNode) {
                if (!tokenManager.peek(0).get().getType().equals(Token.TokenTypes.DEDENT) && !tokenManager.peek(0).get().getType().equals(Token.TokenTypes.NEWLINE))
                    throw new SyntaxErrorException("NEWLINE expected after a method call expression", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
                return Optional.of(new MethodCallStatementNode((MethodCallExpressionNode) expression.get()));
            } else {
                if (tokenManager.peek(0).isPresent() && tokenManager.peek(0).get().getType().equals(Token.TokenTypes.COMMA)) {
                    return Optional.of(MethodCall(expression.get()).get());
                } else {
                    return Optional.of(Assignment((MemberExpressionNode) expression.get()).get());
                }
            }
        }
        // single variable reference (x = 5)
        var variableReferenceNode = VariableReference();
        if (variableReferenceNode.isEmpty()) return Optional.empty();
        // Increment
        if (tokenManager.nextTwoTokensMatch(Token.TokenTypes.PLUS, Token.TokenTypes.PLUS)){
            tokenManager.matchAndRemove(Token.TokenTypes.PLUS);
            tokenManager.matchAndRemove(Token.TokenTypes.PLUS);
            return Optional.of(new AssignmentNode(){{target = variableReferenceNode.get(); expression = new MathOpNode(){{left = variableReferenceNode.get(); op = MathOperations.add; right = new NumericLiteralNode(){{value = 1.0f;}};}} ;}});
        }
        // Decrement
        if (tokenManager.nextTwoTokensMatch(Token.TokenTypes.MINUS, Token.TokenTypes.MINUS)){
            tokenManager.matchAndRemove(Token.TokenTypes.MINUS);
            tokenManager.matchAndRemove(Token.TokenTypes.MINUS);
            return Optional.of(new AssignmentNode(){{target = variableReferenceNode.get(); expression = new MathOpNode(){{left = variableReferenceNode.get(); op = MathOperations.subtract; right = new NumericLiteralNode(){{value = 1.0f;}};}} ;}});
        }
        if (tokenManager.peek(0).isPresent() && tokenManager.peek(0).get().getType().equals(Token.TokenTypes.COMMA)) return Optional.of(MethodCall(variableReferenceNode.get()).get());
        if (tokenManager.peek(0).isPresent() && tokenManager.peek(0).get().getType().equals(Token.TokenTypes.ASSIGN)) return Optional.of(Assignment(variableReferenceNode.get()).get());
        return Optional.empty();
    }

    // MethodCall = [VariableReference { "," VariableReference } "=" MethodCallExpression
    // Note the variable reference passed in from disambiguate
    private Optional<MethodCallStatementNode> MethodCall(ExpressionNode referenceNode) throws SyntaxErrorException {
        if (referenceNode == null) throw new SyntaxErrorException("A Variable Reference is required to call MethodCall", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        List<ExpressionNode> retVals = new LinkedList<>();
        retVals.add(referenceNode);
        while(tokenManager.matchAndRemove(Token.TokenTypes.COMMA).isPresent()) {
            var memberExpression = MethodCallExpression();
            if (memberExpression.isPresent()) {
                if (memberExpression.get() instanceof MethodCallExpressionNode) {
                    throw new SyntaxErrorException("Cannot assign a return value from a method to a method call", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
                }
                retVals.add(memberExpression.get());
            } else {
                var variableReference = VariableReference();
                if (variableReference.isEmpty()) throw new SyntaxErrorException("Variable Reference Expected After Comma", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
                retVals.add(variableReference.get());
            }
        }
        if (tokenManager.matchAndRemove(Token.TokenTypes.ASSIGN).isEmpty()) throw new SyntaxErrorException("'=' Expected after list of VariableReferences in a methodCall", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        var methodCallExpression = MethodCallExpression();
        if (methodCallExpression.isEmpty()) throw new SyntaxErrorException("MethodCall expression expected in MethodCall Statement", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        if (methodCallExpression.get() instanceof MemberExpressionNode){
            throw new SyntaxErrorException("Invalid method call expression (Expression was a MemberCallExpression)", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        }
        if (methodCallExpression.get() instanceof MethodCallExpressionNode) {
            MethodCallStatementNode methodCallStatementNode = new MethodCallStatementNode((MethodCallExpressionNode)methodCallExpression.get());
            methodCallStatementNode.returnValues = retVals;
            return Optional.of(methodCallStatementNode);
        }
        throw new SyntaxErrorException("Method Call Requires a method call expression after '='. The provided was a MemberExpressionNode", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());

    }

    // Assignment = VariableReference "=" Expression
    private Optional<StatementNode> Assignment(ExpressionNode targetNode) throws SyntaxErrorException {
        if (targetNode == null) throw new SyntaxErrorException("A Variable Reference is required to call MethodCall", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        if (tokenManager.matchAndRemove(Token.TokenTypes.ASSIGN).isEmpty()) throw new SyntaxErrorException("'=' Expected in Assignment Statement", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        var expressionNode = Expression();
        if (expressionNode.isEmpty()) throw new SyntaxErrorException("Expression Expected in Assignment Statement", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        if (expressionNode.get() instanceof MethodCallExpressionNode){
            MethodCallStatementNode methodCallStatementNode = new MethodCallStatementNode((MethodCallExpressionNode) expressionNode.get());
            methodCallStatementNode.returnValues.add(targetNode);
            return Optional.of(methodCallStatementNode);
        }
        return Optional.of(new AssignmentNode(){{this.target = targetNode; expression = expressionNode.get();}});
    }

    // Expression = Term { ("+"|"-") Term }
    private Optional<ExpressionNode> Expression() throws SyntaxErrorException {
        if (ContainsBooleanOperator()){
            return BoolExpTerm();
        }
        var currentNode = this.Term();
        if (currentNode.isEmpty()) return Optional.empty();
        return getRightOfExpression(currentNode.get());
    }
    
    // Expression without the Boolean operator check so it can be called in BoolExpFactor
    private Optional<ExpressionNode> ExpressionForBoolExp() throws SyntaxErrorException {
        var currentNode = this.Term();
        if (currentNode.isEmpty()) return Optional.empty();
        return getRightOfExpression(currentNode.get());
    }

    // Method to recursively parse the right side of expression
    private Optional<ExpressionNode> getRightOfExpression(ExpressionNode leftNode) throws SyntaxErrorException {
        // Get operator for expression
        Optional<Token> opToken = tokenManager.matchAndRemove(Token.TokenTypes.PLUS);
        if (opToken.isEmpty()) opToken = tokenManager.matchAndRemove(Token.TokenTypes.MINUS);
        if (opToken.isEmpty()) return Optional.of(leftNode);
        // Get the right term and recursively parse the rest of the expression
        Optional<ExpressionNode> rightNode = this.Term();
        if (rightNode.isEmpty()) throw new SyntaxErrorException("Found a '+' or '-' but no term after", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        MathOpNode.MathOperations operator;
        if (opToken.get().getType().equals(Token.TokenTypes.PLUS)) {
            operator = MathOpNode.MathOperations.add;
        } else {
            operator = MathOpNode.MathOperations.subtract;
        }
        MathOpNode expressionNode = new MathOpNode(){{left = leftNode; right = rightNode.get(); op = operator;}};
        return getRightOfExpression(expressionNode);
    }

    // Term = Factor { ("*"|"/"|"%") Factor }
    private Optional<ExpressionNode> Term() throws SyntaxErrorException {
        var currentNode = this.Factor();
        if (currentNode.isEmpty()) return Optional.empty();
        return getRightOfTerm(currentNode.get());
    }

    // Method to recursively parse the right side of a Term
    private Optional<ExpressionNode> getRightOfTerm(ExpressionNode leftNode) throws SyntaxErrorException {
        // Get Operator for Term
        Optional<Token> opToken = tokenManager.matchAndRemove(Token.TokenTypes.TIMES);
        if (opToken.isEmpty()) opToken = tokenManager.matchAndRemove(Token.TokenTypes.DIVIDE);
        if (opToken.isEmpty())opToken = tokenManager.matchAndRemove(Token.TokenTypes.MODULO);
        if (opToken.isEmpty()) return Optional.of(leftNode);
        // Get the Factor on the right and recursively parse the rest of term
        Optional<ExpressionNode> rightNode = this.Factor();
        if (rightNode.isEmpty()) throw new SyntaxErrorException("Found a '+' or '-' but no term after", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        MathOpNode.MathOperations operator;
        if (opToken.get().getType().equals(Token.TokenTypes.TIMES)) {
            operator = MathOpNode.MathOperations.multiply;
        } else if (opToken.get().getType().equals(Token.TokenTypes.DIVIDE)){
            operator = MathOpNode.MathOperations.divide;
        } else {
            operator = MathOpNode.MathOperations.modulo;
        }
        MathOpNode expressionNode = new MathOpNode(){{left = leftNode; right = rightNode.get(); op = operator;}};
        return getRightOfTerm(expressionNode);
    }

    // TODO: DO SOME LOOK AHEAD FOR METHOD CALL EXPRESSIONS AND VARIABLE REFERENCES
    // Factor = NumberLiteral | VariableReference | "true" | "false" | StringLiteral | CharacterLiteral
    // | MethodCallExpression | "(" Expression ")" | "new" Identifier "(" [Expression {"," Expression }] ")"
    private Optional<ExpressionNode> Factor() throws SyntaxErrorException {
        // NumberLiteral
        // Check for negative number
        if (tokenManager.nextTwoTokensMatch(Token.TokenTypes.MINUS, Token.TokenTypes.NUMBER)){
            tokenManager.matchAndRemove(Token.TokenTypes.MINUS);
            var number = tokenManager.matchAndRemove(Token.TokenTypes.NUMBER);
            return Optional.of(new NumericLiteralNode(){{value = Float.parseFloat("-" + number.get().getValue());}});
        }
        var number = tokenManager.matchAndRemove(Token.TokenTypes.NUMBER);
        if (number.isPresent()) return Optional.of(new NumericLiteralNode(){{value = Float.parseFloat(number.get().getValue());}});
        // True or False
        if (tokenManager.matchAndRemove(Token.TokenTypes.TRUE).isPresent()) return Optional.of(new BooleanLiteralNode(true));
        if (tokenManager.matchAndRemove(Token.TokenTypes.FALSE).isPresent()) return Optional.of(new BooleanLiteralNode(false));
        // StringLiteral
        var stringLiteral = tokenManager.matchAndRemove(Token.TokenTypes.QUOTEDSTRING);
        if (stringLiteral.isPresent()) return Optional.of(new StringLiteralNode(){{value = stringLiteral.get().getValue();}});
        // CharacterLiteral
        var charLiteral = tokenManager.matchAndRemove(Token.TokenTypes.QUOTEDCHARACTER);
        if (charLiteral.isPresent()) return Optional.of(new CharLiteralNode(){{value = charLiteral.get().getValue().charAt(0);}});
        // MethodCallExpression
        var methodCallExpression = MethodCallExpression();
        if (methodCallExpression.isPresent()) return Optional.of(methodCallExpression.get());
        // Variable Reference
        var variableReference = VariableReference();
        if (variableReference.isPresent()) return Optional.of(variableReference.get());
        // Null reference
        var nullReference = tokenManager.matchAndRemove(Token.TokenTypes.NULL);
        if (nullReference.isPresent()) return Optional.of(new NullNode());
        // "(" Expression ")"
        if (tokenManager.matchAndRemove(Token.TokenTypes.LPAREN).isPresent()) {
            var expression = Expression();
            if (expression.isEmpty()) throw new SyntaxErrorException("'(' found but no expression", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
            if (tokenManager.matchAndRemove(Token.TokenTypes.RPAREN).isEmpty()) throw new SyntaxErrorException("')' Expected here ", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
            return expression;
        }
        // "new" Identifier "(" [Expression {"," Expression }] ")"
        if (tokenManager.matchAndRemove(Token.TokenTypes.NEW).isPresent()) {
            var classToken = tokenManager.matchAndRemove(Token.TokenTypes.WORD);
            if (classToken.isEmpty()) throw new SyntaxErrorException("Identifier Expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
            if (tokenManager.matchAndRemove(Token.TokenTypes.LPAREN).isEmpty()) throw new SyntaxErrorException("'(' Expected here ", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
            List<ExpressionNode> params = new ArrayList<>();
            var expressionNode = Expression();
            if (expressionNode.isPresent()) {
                params.add(expressionNode.get());
                while(tokenManager.matchAndRemove(Token.TokenTypes.COMMA).isPresent()){
                    var expression = Expression();
                    if (expression.isEmpty()) throw new SyntaxErrorException("Expression Expected after ',' in Method Call Parameters", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
                    params.add(expression.get());
                }
            }
            if (tokenManager.matchAndRemove(Token.TokenTypes.RPAREN).isEmpty()) throw new SyntaxErrorException("')' Expected here ", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
            return Optional.of(new NewNode(){{className = classToken.get().getValue(); parameters = params;}});
        }
        return Optional.empty();
    }

    // Method to look ahead and check if an expression is a boolean expression by checking for Tokens that are only contained in boolean expresions
    private boolean ContainsBooleanOperator() throws SyntaxErrorException {
        int i = 0;
        // Peek the token and check for boolean tokens until a newline or dedent
        while(tokenManager.peek(i).isPresent()){
            var tokenType = tokenManager.peek(i).get().getType();
            switch (tokenType) {
                case NEWLINE:
                case DEDENT:
                    return false;

                case EQUAL:
                case NOTEQUAL:
                case NOT:
                case AND:
                case OR:
                case LESSTHAN:
                case GREATERTHAN:
                case GREATERTHANEQUAL:
                case LESSTHANEQUAL:
                case TRUE:
                case FALSE:
                    return true;

                default:
                    break;
            }
            i++;
        }
        return false;
    }

    // Helper Method to require a NEWLINE and skip any consecutive newlines
    private void RequireNewLine() throws SyntaxErrorException {
        if (tokenManager.matchAndRemove(Token.TokenTypes.NEWLINE).isEmpty()) throw new SyntaxErrorException("NEWLINE expected", tokenManager.getCurrentLine(), tokenManager.getCurrentColumnNumber());
        SkipNewLine();
    }

    // Helper Method to skip any consecutive NEWLINES
    private void SkipNewLine() {
        Optional<Token> newline = tokenManager.matchAndRemove(Token.TokenTypes.NEWLINE);
        while (newline.isPresent()) {
            newline = tokenManager.matchAndRemove(Token.TokenTypes.NEWLINE);
        }
    }
}