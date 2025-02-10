package com.Tran.interpreter;
import com.Tran.parser.AST.*;
import com.Tran.utils.*;
import com.Tran.interpreter.BuiltIns.*;
import com.Tran.interpreter.DataTypes.*;
import com.Tran.utils.InterruptedException;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Interpreter {
    private AtomicBoolean stopRunning;
    private TranNode top;
    private HashMap<String, MethodDeclarationNode> BuiltInMethods = new HashMap<>();


    /** Constructor - get the interpreter ready to run. Set members from parameters and "prepare" the class.
     *
     * Store the tran node.
     * Add any built-in methods to the AST
     * @param top - the head of the AST
     */
    public Interpreter(TranNode top, ConsoleWrite consoleWrite, AtomicBoolean stopRunning) {
        // Populate the members
        this.top = top;
        // Add built-in class and methods for Console
        ClassNode console = new ClassNode();
        console.name = "console";
        console.methods.add(consoleWrite);
        top.Classes.add(console);
        // Iterator interface
        top.Interfaces.add(new InterfaceNode(){{this.name = "iterator"; this.methods.add(new MethodHeaderNode(){{this.name = "getNext"; this.returns.add(new VariableDeclarationNode(){{this.name = "b"; this.type = "boolean";}}); this.returns.add(new VariableDeclarationNode(){{this.name = "i"; this.type = "number";}});}});}});
        BuiltInMethods.put("clone", new MethodDeclarationNode(){{this.name = "clone";}});
        // Add the built-in NumberIterator Class
        top.Classes.add(getNumberIterator());
        // Add the boolean built-in class
        top.Classes.add(Boolean());
        this.stopRunning = stopRunning;
    }

    /**
     * Send a signal to stop executing the code
     * @param stopRunning signal to stop running
     */
    public void setStopRunning(AtomicBoolean stopRunning) {
        this.stopRunning = stopRunning;
    }

    private ClassNode Boolean(){
        ClassNode bool = new ClassNode();
        bool.name = "boolean";
        // True and False
        MemberNode trueNode = new MemberNode(){{this.isShared = true; this.declaration = new VariableDeclarationNode(){{this.type = "boolean"; this.name = "true";}};}};
        trueNode.accessor = Optional.of(new ArrayList<>());
        trueNode.accessor.get().add(new AssignmentNode(){{this.target = new VariableReferenceNode(){{this.name = "value";}}; this.expression = new BooleanLiteralNode(true);}});
        MemberNode falseNode = new MemberNode(){{this.isShared = true; this.declaration = new VariableDeclarationNode(){{this.type = "boolean"; this.name = "false";}};}};
        falseNode.accessor = Optional.of(new ArrayList<>());
        falseNode.accessor.get().add(new AssignmentNode(){{this.target = new VariableReferenceNode(){{this.name = "value";}}; this.expression = new BooleanLiteralNode(false);}});
        bool.members.add(trueNode);
        bool.members.add(falseNode);
        // NOT
        MethodDeclarationNode notMethod = new MethodDeclarationNode(){{this.name = "not"; isShared = true;}};
        notMethod.returns.add(new VariableDeclarationNode(){{this.name = "result"; this.type = "boolean";}});
        notMethod.parameters.add(new VariableDeclarationNode(){{this.name = "value"; this.type = "boolean";}});
        IfNode notIf = new IfNode(){{
            this.statements = new ArrayList<>();
            this.condition = new CompareNode(){{
                this.left = new VariableReferenceNode(){{
                    this.name = "value";}};
                    this.op = CompareOperations.eq;
                    this.right = new BooleanLiteralNode(true);
            }};
        }};
        notIf.statements.add(new AssignmentNode(){{this.target = new VariableReferenceNode(){{this.name = "result";}}; this.expression = new BooleanLiteralNode(false);}});
        notIf.elseStatement = Optional.of(new ElseNode(){{
            this.statements = new ArrayList<>();
            this.statements.add(new AssignmentNode(){{
                this.target = new VariableReferenceNode(){{
                    this.name = "result";
                }};
                this.expression = new BooleanLiteralNode(true);
            }});
        }});
        notMethod.statements.add(notIf);
        // AND
        MethodDeclarationNode andMethod = new MethodDeclarationNode(){{this.name = "and"; isShared = true;}};
        andMethod.returns.add(new VariableDeclarationNode(){{this.name = "result"; this.type = "boolean";}});
        andMethod.parameters.add(new VariableDeclarationNode(){{this.name = "a"; this.type = "boolean";}});
        andMethod.parameters.add(new VariableDeclarationNode(){{this.name = "b"; this.type = "boolean";}});
        IfNode andIf = new IfNode(){{
            this.statements = new ArrayList<>();
            this.condition = new CompareNode(){{
                this.left = new VariableReferenceNode(){{
                    this.name = "a";}};
                    this.op = CompareOperations.eq;
                    this.right = new BooleanLiteralNode(false);
                }};
        }};
        andIf.statements.add(new AssignmentNode(){{this.target = new VariableReferenceNode(){{this.name = "result";}}; this.expression = new BooleanLiteralNode(false);}});
        andIf.elseStatement = Optional.of(new ElseNode() {{
            this.statements = new ArrayList<>();
            this.statements.add(new IfNode() {{
                this.statements = new ArrayList<>();
                this.condition = new CompareNode() {{
                    this.left = new VariableReferenceNode() {{
                        this.name = "b";
                    }};
                    this.op = CompareOperations.eq;
                    this.right = new BooleanLiteralNode(false);
                }};
                this.statements.add(new AssignmentNode() {{
                    this.target = new VariableReferenceNode() {{
                        this.name = "result";
                    }};
                    this.expression = new BooleanLiteralNode(false);
                }});
                this.elseStatement = Optional.of(new ElseNode() {{
                    this.statements = new ArrayList<>();
                    this.statements.add(new AssignmentNode() {{
                        this.target = new VariableReferenceNode() {{
                            this.name = "result";
                        }};
                        this.expression = new BooleanLiteralNode(true);
                    }});
                }});
            }});
        }});
        andMethod.statements.add(andIf);

        // OR
        MethodDeclarationNode orMethod = new MethodDeclarationNode(){{this.name = "or"; isShared = true;}};
        orMethod.returns.add(new VariableDeclarationNode(){{this.name = "result"; this.type = "boolean";}});
        orMethod.parameters.add(new VariableDeclarationNode(){{this.name = "a"; this.type = "boolean";}});
        orMethod.parameters.add(new VariableDeclarationNode(){{this.name = "b"; this.type = "boolean";}});
        IfNode orIf = new IfNode(){{
            this.statements = new ArrayList<>();
            this.condition = new CompareNode(){{
                this.left = new VariableReferenceNode(){{
                    this.name = "a";}};
                this.op = CompareOperations.eq;
                this.right = new BooleanLiteralNode(true);
            }};
        }};
        orIf.statements.add(new AssignmentNode(){{this.target = new VariableReferenceNode(){{this.name = "result";}}; this.expression = new BooleanLiteralNode(true);}});
        orIf.elseStatement = Optional.of(new ElseNode() {{
            this.statements = new ArrayList<>();
            this.statements.add(new IfNode() {{
                this.statements = new ArrayList<>();
                this.condition = new CompareNode() {{
                    this.left = new VariableReferenceNode() {{
                        this.name = "b";
                    }};
                    this.op = CompareOperations.eq;
                    this.right = new BooleanLiteralNode(true);
                }};
                this.statements.add(new AssignmentNode() {{
                    this.target = new VariableReferenceNode() {{
                        this.name = "result";
                    }};
                    this.expression = new BooleanLiteralNode(true);
                }});
                this.elseStatement = Optional.of(new ElseNode() {{
                    this.statements = new ArrayList<>();
                    this.statements.add(new AssignmentNode() {{
                        this.target = new VariableReferenceNode() {{
                            this.name = "result";
                        }};
                        this.expression = new BooleanLiteralNode(false);
                    }});
                }});
            }});
        }});
        orMethod.statements.add(orIf);
        bool.methods.add(orMethod);
        bool.methods.add(notMethod);
        bool.methods.add(andMethod);
        return bool;

    }
    private ClassNode getNumberIterator(){
        ClassNode NumberIterator = new ClassNode();
        NumberIterator.name = "NumberIterator";
        NumberIterator.interfaces.add("iterator");
        NumberIterator.members.add(new MemberNode(){{this.declaration = new VariableDeclarationNode(){{this.name = "maxValue"; this.type = "number";}};}});
        NumberIterator.members.add(new MemberNode(){{this.declaration = new VariableDeclarationNode(){{this.name = "currentValue"; this.type = "number";}};}});
        ConstructorNode constructor = new ConstructorNode();
        constructor.parameters.add(new VariableDeclarationNode(){{this.name = "n"; this.type = "number";}});
        constructor.statements.add(new AssignmentNode(){{this.target = new VariableReferenceNode(){{this.name = "maxValue";}}; this.expression = new VariableReferenceNode(){{this.name = "n";}};}});
        constructor.statements.add(new AssignmentNode(){{this.target = new VariableReferenceNode(){{this.name = "currentValue";}}; this.expression = new NumericLiteralNode(){{this.value = 0;}};}});
        NumberIterator.constructors.add(constructor);
        MethodDeclarationNode getNext = new MethodDeclarationNode(){{this.name = "getNext"; this.returns.add(new VariableDeclarationNode(){{this.name = "b"; this.type = "boolean";}}); this.returns.add(new VariableDeclarationNode(){{this.name = "i"; this.type = "number";}});}};
        getNext.statements.add(new AssignmentNode(){{this.target = new VariableReferenceNode(){{this.name = "b";}}; this.expression = new CompareNode(){{this.left = new VariableReferenceNode(){{this.name = ("currentValue");}}; this.op = CompareOperations.lt; this.right = new VariableReferenceNode(){{this.name = "maxValue";}};}};}});
        getNext.statements.add(new AssignmentNode(){{this.target = new VariableReferenceNode(){{this.name = "currentValue";}}; this.expression = new MathOpNode(){{this.left = new VariableReferenceNode(){{this.name = "currentValue";}}; this.right = new NumericLiteralNode(){{this.value = 1;}}; this.op = MathOperations.add;}};}});
        getNext.statements.add(new AssignmentNode(){{this.target = new VariableReferenceNode(){{this.name ="i";}}; this.expression = new VariableReferenceNode(){{this.name ="currentValue";}};}});
        NumberIterator.methods.add(getNext);
        return NumberIterator;
    }


    /**
     * This method performs basic semantic analysis by walking through the AST and populating the hashmaps for the variables in classes
     */
    private void SemanticAnalysis(TranNode top) throws Exception {
        for (var i : top.Interfaces){
            for (var c : top.Classes){
                if (c.name.equals(i.name)){
                    throw new TranRuntimeException(String.format("Class: %s can not have the same name as interface: %s", c.name, i.name));
                }
            }
        }
        for (var c : top.Classes){
            List<String> members = new ArrayList<>();
            for (var m : c.members){
                if (members.contains(m.declaration.name)){
                    throw new Exception(String.format("Variable '%s' is already defined in scope", m.declaration.name));
                } else {
                    members.add(m.declaration.name);
                    if (m.isShared){
                        c.sharedMemberMap.put(m.declaration.name, instantiate(m.declaration.type));
                        c.sharedMemberMap.get(m.declaration.name).Initialize();
                    }
                }
                if (m.accessor.isPresent()){
                    var methodNode = new MethodDeclarationNode();
                    methodNode.name = m.declaration.name;
                    if (m.isShared){
                        methodNode.isShared = true;
                    }
                    methodNode.returns.add(new VariableDeclarationNode(){{this.name = "value"; this.type = m.declaration.type;}});
                    methodNode.statements = m.accessor.get();
                    c.accessors.put(m.declaration.name, methodNode);
                }
                if (m.mutator.isPresent()){
                    var methodNode = new MethodDeclarationNode();
                    methodNode.name = m.declaration.name;
                    if (m.isShared){
                        methodNode.isShared = true;
                    }
                    methodNode.parameters.add(new VariableDeclarationNode(){{this.name = "value"; this.type = m.declaration.type;}});
                    methodNode.statements = m.mutator.get();
                    c.mutators.put(m.declaration.name, methodNode);
                }
            }

            // Walk through the methods and populate the locals map
            for (var m : c.methods){
                if (c.methodMap.containsKey(m.name)){
                    c.methodMap.get(m.name).add(m);
                } else {
                    List<MethodDeclarationNode> methods = new ArrayList<>();
                    methods.add(m);
                    c.methodMap.put(m.name, methods);
                }
            }
            for (var m : c.constructors){
                for (var l : m.locals){
                    if (m.localMap.containsKey(l.name)){
                        throw new TranRuntimeException(String.format("Variable '%s' is already defined in method scope", l.name));
                    } else {
                        m.localMap.put(l.name, instantiate(l.type));
                    }
                }
                for (var l : m.parameters){
                    if (m.localMap.containsKey(l.name)){
                        throw new TranRuntimeException(String.format("Variable '%s' is already defined in method scope", l.name));
                    } else {
                        m.localMap.put(l.name, instantiate(l.type));
                    }
                }

            }
        }
    }

    /**
     * This is the public interface to the interpreter. After parsing, we will create an interpreter and call start to
     * start interpreting the code.
     *
     * Search the classes in Tran for a method that is "isShared", named "start", that is not private and has no parameters
     * Call "InterpretMethodCall" on that method, then return.
     * Throw an exception if no such method exists.
     */
    public void start() throws Exception {
        // Populate the hash maps for the symbol table
        SemanticAnalysis(top);
         //Search through classes in top and find a method called start which isShared.
         //Create the methods to be Interpreter Data Types
        for (var c : top.Classes){
            for (var m : c.methods){
                if (m.isShared && m.name.equals("start") && m.parameters.isEmpty()){
                    interpretMethodCall(Optional.of(new ObjectIDT(c){{this.isSharedMethodCall = true;}}), m, new ArrayList<>());
                    return;
                }
            }
        }
        throw new TranRuntimeException("Shared Method start() was not found. Make sure to include a shared start() in order to run a program!");
    }




    //              Running Methods
    /**
     * Find the method (local to this class, shared (like Java's system.out.print), or a method on another class)
     * Evaluate the parameters to have a list of values
     * Use interpretMethodCall() to actually run the method.1
     * Call GetParameters() to get the parameter value list
     * Find the method. This is tricky - there are several cases:
     * someLocalMethod() - has NO object name. Look in "object"
     * console.write() - the objectName is a CLASS and the method is shared
     * bestStudent.getGPA() - the objectName is a local or a member
     * Once you find the method, call InterpretMethodCall() on it. Return the list that it returns.
     * Throw an exception if we can't find a match.
     * @param object - the object we are inside right now (might be empty) this is for method calls with the
     * @param locals - the current local variables
     * @param mc - the method call
     * @return - the return values
     */
    private List<InterpreterDataType> findMethodForMethodCallAndRunIt(Optional<ObjectIDT> object, HashMap<String, InterpreterDataType> locals, MethodCallStatementNode mc) {
        // Stop Execution if user sends signal
        if (stopRunning.get()){
            throw new InterruptedException("** Program Execution Interrupted **");
        }
        List<InterpreterDataType> parameters = getParameters(object, locals, mc);
        if (mc.object.isPresent()){
            if (mc.object.get() instanceof VariableReferenceNode){
                var classNode = getClassByName(((VariableReferenceNode) mc.object.get()).name);
                // Class Name is present
                if (classNode.isPresent()){
                    if (classNode.get().methodMap.containsKey(mc.methodName)) {
                        var methods = classNode.get().methodMap.get(mc.methodName);
                        for (var m : methods) {
                            if (doesMatch(m, mc, parameters)) {
                                if (m.isShared) {
                                    return interpretMethodCall(Optional.of(new ObjectIDT(classNode.get()){{this.isSharedMethodCall = true;}}), m, parameters);
                                }
                            }
                        }
                        throw new TranRuntimeException("MethodCall on a Class was called, but the method was not shared");
                    }
                    throw new TranRuntimeException("MethodCall on a Class was called, but the method could not be found: " + mc.methodName);
                }
            }
            InterpreterDataType callerObject = evaluate(locals, object, mc.object.get());
            if (!(callerObject instanceof ReferenceIDT)){
                throw new TranRuntimeException("Trying to call a method on an IDT on a primitive type");
            }
            if ((((ReferenceIDT) callerObject).refersTo.get().astNode == null)){
                throw new TranRuntimeException("Trying to call method on a uninitialized object");
            }
            if (((ReferenceIDT) callerObject).refersTo.get().astNode.methodMap.containsKey(mc.methodName)){
                var methods = ((ReferenceIDT) callerObject).refersTo.get().astNode.methodMap.get(mc.methodName);
                for (var m : methods) {
                    if (doesMatch(m, mc, parameters)) {
                        if (m.isPrivate){
                            throw new TranRuntimeException("Method was called on a private function which can not be accessed by an instance of the class");
                        }
                        if (m.isShared){
                            return interpretMethodCall(Optional.of(new ObjectIDT(((ReferenceIDT) callerObject).refersTo.get().astNode){{this.isSharedMethodCall = true;}}), m, parameters);
                        }
                        return interpretMethodCall(((ReferenceIDT) callerObject).refersTo, m, parameters);
                    }
                }
            }
            if (BuiltInMethods.containsKey(mc.methodName)){
                if (doesMatch(BuiltInMethods.get(mc.methodName), mc, parameters)) {
                    return interpretMethodCall(((ReferenceIDT) callerObject).refersTo, BuiltInMethods.get(mc.methodName), parameters);
                }
            }
            throw new TranRuntimeException("Method was called on an reference, but the method does not exist");
        } else {
            // If we are in a current object as no classname or reference name was specified
            if (object.isEmpty()){
                throw new TranRuntimeException("Current Object is not defined and no methods can be checked");
            }
            return interpretMethodCall(object, getMethodFromObject(object.get(), mc, parameters), parameters);
        }
    }

    /**
     * Run a "prepared" method (found, parameters evaluated)
     * This is split from findMethodForMethodCallAndRunIt() because there are a few cases where we don't need to do the finding:
     * in start() and dealing with loops with iterator objects, for example.
     *
     * Check to see if "m" is a built-in. If so, call Execute() on it and return
     * Make local variables, per "m"
     * If the number of passed in values doesn't match m's "expectations", throw
     * Add the parameters by name to locals.
     * Call InterpretStatementBlock
     * Build the return list - find the names from "m", then get the values for those names and add them to the list.
     * @param object - The object this method is being called on (might be empty for shared)
     * @param m - Which method is being called
     * @param values - The values to be passed in
     * @return the returned values from the method
     */
    private List<InterpreterDataType> interpretMethodCall(Optional<ObjectIDT> object, MethodDeclarationNode m, List<InterpreterDataType> values) {
        // Stop Execution if user sends signal
        if (stopRunning.get()){
            throw new InterruptedException("** Program Execution Interrupted **");
        }
        // Only built in method implemented for now is console.write
        if (m instanceof ConsoleWrite){
            return ((ConsoleWrite) m).Execute(values);
        }
        var retVal = new LinkedList<InterpreterDataType>();
        if (m.returns.isEmpty() && m.parameters.isEmpty() && m.statements.isEmpty() && m.name.equals("clone")){
            // Create a deep copy of the object
            if (object.isEmpty()){
                throw new TranRuntimeException("Clone was called with no object");
            }
            var newObject = new ObjectIDT(object.get().astNode);
            newObject.members.putAll(object.get().members);
            retVal.add(newObject);
            return retVal;

        }

        HashMap<String, InterpreterDataType> methodLocals = new HashMap<>();

        for (var l : m.locals){
            if (methodLocals.containsKey(l.name)){
                throw new TranRuntimeException(String.format("Variable '%s' is already defined in method scope", l.name));
            } else {
                methodLocals.put(l.name, instantiate(l.type));
            }
        }
        for (var l : m.parameters){
            if (methodLocals.containsKey(l.name)){
                throw new TranRuntimeException(String.format("Variable '%s' is already defined in method scope", l.name));
            } else {
                methodLocals.put(l.name, instantiate(l.type));
            }
        }
        for (var l : m.returns){
            if (methodLocals.containsKey(l.name)){
                throw new TranRuntimeException(String.format("Variable '%s' is already defined in method scope", l.name));
            } else {
                methodLocals.put(l.name, instantiate(l.type));
            }
        }

        // Deep Copy each one for the maps
        for (int i = 0; i < values.size(); i++){
            methodLocals.get(m.parameters.get(i).name).Assign(values.get(i));
        }

        interpretStatementBlock(object, m.statements, methodLocals);

        for(var x : m.returns){
            if(!methodLocals.get(x.name).isInitialized()){
                throw new TranRuntimeException("Method has a return variable of " + x.name + " but was not assigned during the method call");
            }
            retVal.add(methodLocals.get(x.name));
        }
        return retVal;
    }

    //              Running Constructors

    /**
     * This is a special case of the code for methods. Just different enough to make it worthwhile to split it out.
     *
     * Call GetParameters() to populate a list of IDT's
     * Call GetClassByName() to find the class for the constructor
     * If we didn't find the class, throw an exception
     * Find a constructor that is a good match - use DoesConstructorMatch()
     * Call InterpretConstructorCall() on the good match
     * @param callerObj - the object that we are inside when we called the constructor
     * @param locals - the current local variables (used to fill parameters)
     * @param cn  - the newNode that contains the constructor call
     * @param newOne - the object that we just created that we are calling the constructor for
     */
    private void findConstructorAndRunIt(Optional<ObjectIDT> callerObj, HashMap<String, InterpreterDataType> locals, NewNode cn, ObjectIDT newOne) {
        // Stop Execution if user sends signal
        if (stopRunning.get()){
            throw new InterruptedException("** Program Execution Interrupted **");
        }
        List<InterpreterDataType> parameters = new LinkedList<>();
        for (var x : cn.parameters){
            parameters.add(evaluate(locals, callerObj, x));
        }
        var classNode = getClassByName(cn.className);
        if (classNode.isEmpty()){
            throw new TranRuntimeException("Class not found: " + cn.className);
        }
        for (var c : classNode.get().constructors){
            if (doesConstructorMatch(c, parameters)){
                interpretConstructorCall(newOne, c, parameters);
            }
        }
    }

    /**
     * Similar to interpretMethodCall, but "just different enough" - for example, constructors don't return anything.
     *
     * Creates local variables (as defined by the ConstructorNode), calls Instantiate() to do the creation
     * Checks to ensure that the right number of parameters were passed in, if not throw.
     * Adds the parameters (with the names from the ConstructorNode) to the locals.
     * Calls InterpretStatementBlock
     * @param object - the object that we allocated
     * @param c - which constructor is being called
     * @param values - the parameter values being passed to the constructor
     */
    private void interpretConstructorCall(ObjectIDT object, ConstructorNode c, List<InterpreterDataType> values) {
        // Stop Execution if user sends signal
        if (stopRunning.get()){
            throw new InterruptedException("** Program Execution Interrupted **");
        }
        for (int i = 0; i < values.size(); i++){
            c.localMap.get(c.parameters.get(i).name).Assign(values.get(i));
        }
        interpretStatementBlock(Optional.of(object), c.statements, c.localMap);
    }

    //              Running Instructions
    /**
     * Given a block (which could be from a method or an "if" or "loop" block, run each statement).
     * Blocks, by definition, do ever statement, so iterating over the statements makes sense.
     *
     * For each statement in statements:
     * check the type:
     *      For AssignmentNode, FindVariable() to get the target. Evaluate() the expression. Call Assign() on the target with the result of Evaluate()
     *      For MethodCallStatementNode, call doMethodCall(). Loop over the returned values and copy the into our local variables
     *      For LoopNode - there are 2 kinds.
     *          Setup:
     *          If this is a Loop over an iterator (an Object node whose class has "iterator" as an interface)
     *              Find the "getNext()" method; throw an exception if there isn't one
     *          Loop:
     *          While we are not done:
     *              if this is a boolean loop, Evaluate() to get true or false.
     *              if this is an iterator, call "getNext()" - it has 2 return values. The first is a boolean (was there another?), the second is a value
     *              If the loop has an assignment variable, populate it: for boolean loops, the true/false. For iterators, the "second value"
     *              If our answer from above is "true", InterpretStatementBlock() on the body of the loop.
     *       For If - Evaluate() the condition. If true, InterpretStatementBlock() on the if's statements. If not AND there is an else, InterpretStatementBlock on the else body.
     * @param object - the object that this statement block belongs to (used to get member variables and any members without an object)
     * @param statements - the statements to run
     * @param locals - the local variables
     */
    private void interpretStatementBlock(Optional<ObjectIDT> object, List<StatementNode> statements, HashMap<String, InterpreterDataType> locals) {
        // Stop Execution if user sends signal
        if (stopRunning.get()){
            throw new InterruptedException("** Program Execution Interrupted **");
        }
        for (var s : statements){
            if (s instanceof AssignmentNode){
                if (((AssignmentNode) s).target instanceof VariableReferenceNode){
                    InterpreterDataType variable = findVariable((VariableReferenceNode) ((AssignmentNode) s).target, locals, object);
                    InterpreterDataType expressionValue = evaluate(locals, object, ((AssignmentNode) s).expression);
                    variable.Assign(expressionValue);
                } else if (((AssignmentNode) s).target instanceof MemberExpressionNode){
                    setMember(((MemberExpressionNode) ((AssignmentNode) s).target), locals, object, evaluate(locals, object, ((AssignmentNode) s).expression));
                }
            } else if (s instanceof MethodCallStatementNode){
                // If the method call expression contains an object pass that into the object
                var retVals = findMethodForMethodCallAndRunIt(object, locals, (MethodCallStatementNode) s);
                // Only return the number of return values called in the statement (x,y statement returns where function returns x,y,z  will return the values into x,y and ignore z)
                for (int i =0; i < ((MethodCallStatementNode) s).returnValues.size(); i++){
                    if (((MethodCallStatementNode)s).returnValues.get(i) instanceof VariableReferenceNode){
                        findVariable((VariableReferenceNode) ((MethodCallStatementNode) s).returnValues.get(i), locals, object).Assign(retVals.get(i));
                    } else {
                        setMember((MemberExpressionNode) ((MethodCallStatementNode)s).returnValues.get(i), locals, object, retVals.get(i));
                    }
                }
            } else if (s instanceof IfNode){
                var conditionValue = evaluate(locals, object, ((IfNode) s).condition);
                if (!(conditionValue instanceof BooleanIDT)){
                    throw new TranRuntimeException("If condition expression is not a boolean");
                }
                if (((BooleanIDT) conditionValue).Value){
                    interpretStatementBlock(object, ((IfNode) s).statements, locals);
                } else {
                    ((IfNode) s).elseStatement.ifPresent(elseNode -> interpretStatementBlock(object, elseNode.statements, locals));
                }
            } else if (s instanceof LoopNode){

                // .times() iterator
                if (((LoopNode) s).expression instanceof MethodCallExpressionNode && ((MethodCallExpressionNode) ((LoopNode) s).expression).object.isPresent() && ((MethodCallExpressionNode) ((LoopNode) s).expression).methodName.equals("times")){
                    InterpreterDataType iteratedObject;
                    if (((MethodCallExpressionNode) ((LoopNode) s).expression).object.get() instanceof VariableReferenceNode) {
                        iteratedObject = findVariable((VariableReferenceNode) ((MethodCallExpressionNode) ((LoopNode) s).expression).object.get(), locals, object);
                    } else {
                        iteratedObject = findMember((MemberExpressionNode) ((MethodCallExpressionNode) ((LoopNode) s).expression).object.get(), locals, object);
                    }
                    if (!iteratedObject.isInitialized()){
                        throw new TranRuntimeException("Trying to use iterator on an object that was not initialized");
                    }

                    if (iteratedObject instanceof NumberIDT){
                        ClassNode NumberIterator = getClassByName("NumberIterator").get();
                        ObjectIDT iterator = new ObjectIDT(NumberIterator);
                        // Create new iterator object
                        List<ExpressionNode> constructorParameter =  new ArrayList<>();
                        constructorParameter.add(new NumericLiteralNode(){{this.value  = ((NumberIDT) iteratedObject).Value;}});
                        for (var m : NumberIterator.members){
                            iterator.members.put(m.declaration.name, instantiate(m.declaration.type));
                        }
                        // Initialize the iterator object
                        findConstructorAndRunIt(Optional.of(iterator), locals, new NewNode(){{this.className = "NumberIterator"; this.parameters = constructorParameter;}}, iterator);
                        List<InterpreterDataType> retVals = interpretMethodCall(Optional.of(iterator), NumberIterator.methods.getFirst(), List.of());
                        while(((BooleanIDT) retVals.getFirst()).Value){
                            if (((LoopNode) s).assignment.isPresent()){
                                findVariable(((LoopNode) s).assignment.get(), locals, object).Assign(retVals.get(1));
                            }
                            interpretStatementBlock(object, ((LoopNode) s).statements, locals);
                            retVals = interpretMethodCall(Optional.of(iterator), NumberIterator.methods.getFirst(), List.of());
                        }

                    } else if (iteratedObject instanceof ReferenceIDT && ((ReferenceIDT) iteratedObject).refersTo.get().astNode.interfaces.contains("iterator")){

                    } else {
                        throw new TranRuntimeException("Trying to use iterator on an object that does not implement the iterator interface");
                    }
                } else { // Boolean Expression
                    var condition = evaluate(locals, object, ((LoopNode) s).expression);
                    if (!(condition instanceof BooleanIDT)){
                        throw new TranRuntimeException("Loop expression is not a boolean");
                    }
                    while(((BooleanIDT) condition).Value){
                        interpretStatementBlock(object, ((LoopNode) s).statements, locals);
                        condition = evaluate(locals, object, ((LoopNode) s).expression);
                    }
                }
            }
        }
    }

    /**
     *  evaluate() processes everything that is an expression - math, variables, boolean expressions.
     *  There is a good bit of recursion in here, since math and comparisons have left and right sides that need to be evaluated.
     *
     * See the How To Write an Interpreter document for examples
     * For each possible ExpressionNode, do the work to resolve it:
     * BooleanLiteralNode - create a new BooleanLiteralNode with the same value
     *      - Same for all of the basic data types
     * BooleanOpNode - Evaluate() left and right, then perform either and/or on the results.
     * CompareNode - Evaluate() both sides. Do good comparison for each data type
     * MathOpNode - Evaluate() both sides. If they are both numbers, do the math using the built-in operators. Also handle String + String as concatenation (like Java)
     * MethodCallExpression - call doMethodCall() and return the first value
     * VariableReferenceNode - call findVariable()
     * @param locals the local variables
     * @param object - the current object we are running
     * @param expression - some expression to evaluate
     * @return a value
     */
    private InterpreterDataType evaluate(HashMap<String, InterpreterDataType> locals, Optional<ObjectIDT> object, ExpressionNode expression){
        // Stop Execution if user sends signal
        if (stopRunning.get()){
            throw new InterruptedException("** Program Execution Interrupted **");
        }
        if (expression instanceof NumericLiteralNode){
            return new NumberIDT(((NumericLiteralNode) expression).value);
        } else if (expression instanceof StringLiteralNode){
            return new StringIDT(((StringLiteralNode) expression).value);
        } else if (expression instanceof BooleanLiteralNode){
            return new BooleanIDT(((BooleanLiteralNode) expression).value);
        } else if (expression instanceof CharLiteralNode){
            return new CharIDT(((CharLiteralNode) expression).value);
        } else if (expression instanceof VariableReferenceNode){
            return findVariable(((VariableReferenceNode) expression), locals, object);
        } else if (expression instanceof NullNode) {
            return new ObjectIDT(null);
        } else if (expression instanceof MemberExpressionNode){
            return findMember((MemberExpressionNode) expression, locals, object);
        } else if (expression instanceof MathOpNode){
            InterpreterDataType left = evaluate(locals, object, ((MathOpNode) expression).left);
            InterpreterDataType right = evaluate(locals, object, ((MathOpNode) expression).right);
            if (left instanceof BooleanIDT || right instanceof BooleanIDT){
                throw new TranRuntimeException("Invalid Arithmetic Expression with Booleans");
            }
            if ((left instanceof StringIDT || right instanceof StringIDT) && !((MathOpNode) expression).op.equals(MathOpNode.MathOperations.add)){
                throw new TranRuntimeException("Only String concatenation is allowed for strings");
            }
            // String + String = String
            if (left instanceof StringIDT && right instanceof StringIDT){
                return new StringIDT(((StringIDT) left).Value + ((StringIDT) right).Value);
            }
            // String + Char = String
            if (left instanceof StringIDT && right instanceof CharIDT){
                return new StringIDT(((StringIDT) left).Value + ((CharIDT) right).Value);
            }
            // Char + String = String
            if (left instanceof CharIDT && right instanceof StringIDT){
                return new StringIDT(((CharIDT) left).Value + ((StringIDT) right).Value);
            }
            if (left instanceof StringIDT && right instanceof NumberIDT){
                return new StringIDT(((StringIDT) left).Value + ((NumberIDT) right).Value);
            }
            // Number Operations
            if (left instanceof NumberIDT && right instanceof NumberIDT){
                switch(((MathOpNode) expression).op){
                    case add -> {
                        return new NumberIDT(((NumberIDT) left).Value + ((NumberIDT) right).Value);
                    }
                    case subtract -> {
                        return new NumberIDT(((NumberIDT) left).Value - ((NumberIDT) right).Value);
                    }
                    case multiply -> {
                        return new NumberIDT(((NumberIDT) left).Value * ((NumberIDT) right).Value);
                    }
                    case divide -> {
                        return new NumberIDT(((NumberIDT) left).Value / ((NumberIDT) right).Value);
                    }
                    case modulo -> {
                        return new NumberIDT(((NumberIDT) left).Value % ((NumberIDT) right).Value);
                    }
                }
            }
            throw new TranRuntimeException("Invalid Arithmetic Expression " + expression);
        } else if (expression instanceof NotOpNode){
            // Process NotOpNodes
            InterpreterDataType value = evaluate(locals, object, expression);
            if (value instanceof BooleanIDT){
                return new BooleanIDT(!((BooleanIDT) value).Value);
            }
        } else if (expression instanceof BooleanOpNode){
            InterpreterDataType left = evaluate(locals, object, ((BooleanOpNode) expression).left);
            InterpreterDataType right = evaluate(locals, object, ((BooleanOpNode) expression).right);
            if (left instanceof BooleanIDT && right instanceof BooleanIDT){
                if (((BooleanOpNode) expression).op.equals(BooleanOpNode.BooleanOperations.or)){
                    return new BooleanIDT(((BooleanIDT) left).Value || ((BooleanIDT) right).Value);
                } else {
                    return new BooleanIDT(((BooleanIDT) left).Value && ((BooleanIDT) right).Value);
                }
            }
            throw new TranRuntimeException("Invalid Boolean Comparison on types");


        } else if (expression instanceof MethodCallExpressionNode){
            List<InterpreterDataType> retVals = findMethodForMethodCallAndRunIt(object, locals, new MethodCallStatementNode((MethodCallExpressionNode) expression));
            if (retVals.isEmpty()){
                throw new TranRuntimeException("void method has no value and can not be assigned to a variable");
            }
            return retVals.getFirst();
        } else if (expression instanceof CompareNode){ // Comparisons are only valid for String, Number, and Characters
            InterpreterDataType left = evaluate(locals, object, ((CompareNode) expression).left);
            InterpreterDataType right = evaluate(locals, object, ((CompareNode) expression).right);
            // Number comparisons
            if (left instanceof NumberIDT && right instanceof NumberIDT){
                 switch(((CompareNode) expression).op){
                     case eq -> {
                         return new BooleanIDT(((NumberIDT) left).Value == ((NumberIDT) right).Value);
                     }
                     case lt -> {
                         return new BooleanIDT(((NumberIDT) left).Value < ((NumberIDT) right).Value);
                     }
                     case gt -> {
                         return new BooleanIDT(((NumberIDT) left).Value > ((NumberIDT) right).Value);
                     }
                     case ge -> {
                         return new BooleanIDT(((NumberIDT) left).Value >= ((NumberIDT) right).Value);
                     }
                     case le -> {
                         return new BooleanIDT(((NumberIDT) left).Value <= ((NumberIDT) right).Value);
                     }
                     case ne -> {
                         return new BooleanIDT(((NumberIDT) left).Value != ((NumberIDT) right).Value);
                     }
                 }
            }
            // Character comparisons
            if (left instanceof CharIDT && right instanceof CharIDT){
                switch(((CompareNode) expression).op){
                    case eq -> {
                        return new BooleanIDT(((CharIDT) left).Value == ((CharIDT) right).Value);
                    }
                    case lt -> {
                        return new BooleanIDT(((CharIDT) left).Value < ((CharIDT) right).Value);
                    }
                    case gt -> {
                        return new BooleanIDT(((CharIDT) left).Value > ((CharIDT) right).Value);
                    }
                    case ge -> {
                        return new BooleanIDT(((CharIDT) left).Value >= ((CharIDT) right).Value);
                    }
                    case le -> {
                        return new BooleanIDT(((CharIDT) left).Value <= ((CharIDT) right).Value);
                    }
                    case ne -> {
                        return new BooleanIDT(((CharIDT) left).Value != ((CharIDT) right).Value);
                    }
                }
            }
            // Add functionality for null comparisons
            if (left instanceof ReferenceIDT && right instanceof ReferenceIDT){
                switch(((CompareNode) expression).op) {
                    case eq -> {
                        return new BooleanIDT(((ReferenceIDT) left).refersTo.get().equals(((ReferenceIDT) right).refersTo.get()));
                    }
                    case ne -> {
                        return new BooleanIDT(!((ReferenceIDT) left).refersTo.get().equals(((ReferenceIDT) right).refersTo.get()));
                    }
                    default -> {
                        return new BooleanIDT(false);
                    }
                }
            }
            if (left instanceof ObjectIDT && right instanceof ReferenceIDT){
                switch(((CompareNode) expression).op) {
                    case eq -> {

                    }
                    case ne -> {

                    }
                    default -> {
                        return new BooleanIDT(false);
                    }
                }
            }
            if (left instanceof ReferenceIDT && right instanceof ObjectIDT){
                switch(((CompareNode) expression).op) {
                    case eq -> {
                        return new BooleanIDT(((ReferenceIDT) left).refersTo.get().astNode == null);
                    }
                    case ne -> {

                    }
                    default -> {
                        return new BooleanIDT(false);
                    }
                }
            }
            // String comparisons
            if (left instanceof StringIDT && right instanceof StringIDT){
                int value = ((StringIDT) left).Value.compareTo(((StringIDT) right).Value);
                switch(((CompareNode) expression).op){
                    case eq -> {
                        return value == 0 ? new BooleanIDT(true) : new BooleanIDT(false);
                    }
                    case lt -> {
                        return value < 0 ? new BooleanIDT(true) : new BooleanIDT(false);
                    }
                    case gt -> {
                        return value > 0 ? new BooleanIDT(true) : new BooleanIDT(false);
                    }
                    case ge -> {
                        return value >= 0 ? new BooleanIDT(true) : new BooleanIDT(false);
                    }
                    case le -> {
                        return value <=0 ? new BooleanIDT(true) : new BooleanIDT(false);
                    }
                    case ne -> {
                        return value != 0 ? new BooleanIDT(true) : new BooleanIDT(false);
                    }
                }
            }
            throw new TranRuntimeException("Invalid Comparison of different or invalid types");
        } else if (expression instanceof NewNode){
            // the object is the object that the reference refers to
            var classNode = getClassByName(((NewNode) expression).className);
            if (classNode.isEmpty()){
                throw new TranRuntimeException("Class not found: " + ((NewNode) expression).className);
            }
            ObjectIDT newObject = new ObjectIDT(classNode.get());
            newObject.members = new HashMap<>();
            for (var m : classNode.get().members){
                if (!m.isShared){
                    newObject.members.put(m.declaration.name, instantiate(m.declaration.type));
                }
            }
            findConstructorAndRunIt(Optional.of(newObject), locals, (NewNode) expression, newObject);
            return newObject;
        }
        throw new TranRuntimeException("Invalid Expression");
    }

    //              Utility Methods

    /**
     * Used when trying to find a match to a method call. Given a method declaration, does it match this methoc call?
     * We double check with the parameters, too, although in theory JUST checking the declaration to the call should be enough.
     *
     * Match names, parameter counts (both declared count vs method call and declared count vs value list), return counts.
     * If all of those match, consider the types (use TypeMatchToIDT).
     * If everything is OK, return true, else return false.
     * Note - if m is a built-in and isVariadic is true, skip all of the parameter validation.
     * @param m - the method declaration we are considering
     * @param mc - the method call we are trying to match
     * @param parameters - the parameter values for this method call
     * @return does this method match the method call?
     */
    private boolean doesMatch(MethodDeclarationNode m, MethodCallStatementNode mc, List<InterpreterDataType> parameters) {
        if (m.name.equals(mc.methodName) && m instanceof BuiltInMethodDeclarationNode && ((BuiltInMethodDeclarationNode) m).isVariadic){
            return true;
        }
        if (m.name.equals(mc.methodName) && mc.parameters.size() == m.parameters.size()){
            for (int i = 0; i < mc.parameters.size(); i++){
                // Compare the types of the parameters
                if (!typeMatchToIDT(m.parameters.get(i).type, parameters.get(i))){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Very similar to DoesMatch() except simpler - there are no return values, the name will always match.
     * @param c - a particular constructor
     * @param parameters - the parameter values
     * @return does this constructor match the method call?
     */
    private boolean doesConstructorMatch(ConstructorNode c, List<InterpreterDataType> parameters) {
        if (c.parameters.size() == parameters.size()){
            for (int i = 0; i < c.parameters.size(); i++){
                if (!typeMatchToIDT(c.parameters.get(i).type, parameters.get(i))){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Used when we call a method to get the list of values for the parameters.
     *
     * for each parameter in the method call, call Evaluate() on the parameter to get an IDT and add it to a list
     * @param object - the current object
     * @param locals - the local variables
     * @param mc - a method call
     * @return the list of method values
     */
    private List<InterpreterDataType> getParameters(Optional<ObjectIDT> object, HashMap<String,InterpreterDataType> locals, MethodCallStatementNode mc) {
        List<InterpreterDataType> parameters = new LinkedList<>();
        for (var x : mc.parameters){
            parameters.add(evaluate(locals, object, x));
        }
        return parameters;
    }

    /**
     * Used when we have an IDT and we want to see if it matches a type definition
     * Commonly, when someone is making a function call - do the parameter values match the method declaration?
     *
     * If the IDT is a simple type (boolean, number, etc) - does the string type match the name of that IDT ("boolean", etc)
     * If the IDT is an object, check to see if the name matches OR the class has an interface that matches
     * If the IDT is a reference, check the inner (refered to) type
     * @param type the name of a data type (parameter to a method)
     * @param idt the IDT someone is trying to pass to this method
     * @return is this OK?
     */
    private boolean typeMatchToIDT(String type, InterpreterDataType idt) {
        if (idt instanceof BooleanIDT){
            return type.equals("boolean");
        } else if (idt instanceof StringIDT){
            return type.equals("string");
        } else if (idt instanceof CharIDT){
            return type.equals("character");
        } else if (idt instanceof NumberIDT){
            return type.equals("number");
        } else if (idt instanceof ObjectIDT){
            if(type.equals(((ObjectIDT) idt).astNode.name)) {
                return true;
            } else {
                for (var i : ((ObjectIDT) idt).astNode.interfaces){
                    if (i.equals(type)) {
                        return true;
                    }
                }
                return false;
            }
        } else {
            if(type.equals(((ReferenceIDT) idt).refersTo.get().astNode.name)) {
                return true;
            } else {
                for (var i : ((ReferenceIDT) idt).refersTo.get().astNode.interfaces){
                    if (i.equals(type)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    /**
     * Find a method in an object that is the right match for a method call (same name, parameters match, etc. Uses doesMatch() to do most of the work)
     *
     * Given a method call, we want to loop over the methods for that class, looking for a method that matches (use DoesMatch) or throw
     * @param object - an object that we want to find a method on
     * @param mc - the method call
     * @param parameters - the parameter value list
     * @return a method or throws an exception
     */
    private MethodDeclarationNode getMethodFromObject(ObjectIDT object, MethodCallStatementNode mc, List<InterpreterDataType> parameters) {
        if (!object.astNode.methodMap.containsKey(mc.methodName)){
            throw new TranRuntimeException("Method was called but the method was not found in the current object");
        }
        var methods = object.astNode.methodMap.get(mc.methodName);
        for (var m : methods) {
            if (doesMatch(m, mc, parameters)) {
                if (object.isSharedMethodCall && !m.isShared){
                    throw new TranRuntimeException("Trying to call a non-shared method from a class in start() without an instance of the class");
                }
                return m;
            }
        }
        if (BuiltInMethods.containsKey(mc.methodName)){
            return BuiltInMethods.get(mc.methodName);
        }
        throw new TranRuntimeException("Unable to resolve method call " + mc);
    }

    /**
     * Find a class, given the name. Just loops over the TranNode's classes member, matching by name.
     *
     * Loop over each class in the top node, comparing names to find a match.
     * @param name Name of the class to find
     * @return either a class node or empty if that class doesn't exist
     */
    private Optional<ClassNode> getClassByName(String name) {
        for (var x : top.Classes){
            if (x.name.equals(name)) {
                return Optional.of(x);
            }
        }
        return Optional.empty();
    }

    /**
     * Given an execution environment (the current object, the current local variables), find a variable by name.
     *
     * @param name  - the variable that we are looking for
     * @param locals - the current method's local variables
     * @param object - the current object (so we can find members)
     * @return the IDT that we are looking for or throw an exception
     */
    private InterpreterDataType findVariable(VariableReferenceNode name, HashMap<String,InterpreterDataType> locals, Optional<ObjectIDT> object) {

        // Walk through the list to find the last varibale in the reference. This should only matter when finding variables for a mutator or accessor such as car.engine.part.number which would return the final IDT of the reference
        if (locals.containsKey(name.name)) {
            return locals.get(name.name);
        }
        if (object.isPresent()){
            ObjectIDT objectID = object.get();
            if (objectID.members.containsKey(name.name)){
                return objectID.members.get(name.name);
            } else if (objectID.astNode.sharedMemberMap.containsKey(name.name)){
                return objectID.astNode.sharedMemberMap.get(name.name);
            }
        }

        var classNode = getClassByName(name.name);
        if (classNode.isPresent()){
            return new ObjectIDT(classNode.get()){{this.isSharedMethodCall = true;}};
        }
        throw new TranRuntimeException("Unable to find variable '" + name + "'");
    }

    private InterpreterDataType findMember(MemberExpressionNode member, HashMap<String,InterpreterDataType> locals, Optional<ObjectIDT> object) {
        InterpreterDataType callerObject = evaluate(locals, object, member.object);

        // The caller is a variable reference node and I need to check if the node is a class which can be used with findClassByName

        //
        if (!(callerObject instanceof ReferenceIDT)){
            if (callerObject instanceof ObjectIDT){
                String memberName = ((VariableReferenceNode) member.property).name;
                if (!((ObjectIDT) callerObject).astNode.accessors.containsKey(memberName)){
                    throw new TranRuntimeException("Unable to find accessor: " + memberName);
                } else if (!((ObjectIDT) callerObject).astNode.accessors.get(memberName).isShared) {
                    throw new TranRuntimeException("Trying to access member using class name, but the member is not shared");
                }
                return interpretMethodCall(Optional.of((ObjectIDT) callerObject), ((ObjectIDT) callerObject).astNode.accessors.get(memberName), List.of()).getFirst();

            }
            throw new TranRuntimeException("Trying to access a member on an IDT that is not a reference (Not an object)");
        }
        ObjectIDT actualObject = ((ReferenceIDT) callerObject).refersTo.get();
        String memberName = ((VariableReferenceNode) member.property).name;

        if (!((ReferenceIDT) callerObject).refersTo.get().astNode.accessors.containsKey(memberName)){
            throw new TranRuntimeException("Member :" + ((VariableReferenceNode)member.property).name + "Does not have a valid accessor and therefore cannot be accessed");
        }
        return interpretMethodCall(((ReferenceIDT) callerObject).refersTo, ((ReferenceIDT) callerObject).refersTo.get().astNode.accessors.get(memberName), List.of()).getFirst();
    }

    private void setMember(MemberExpressionNode member, HashMap<String,InterpreterDataType> locals, Optional<ObjectIDT> object, InterpreterDataType value) {

        InterpreterDataType callerObject = evaluate(locals, object, member.object);

        if (!(callerObject instanceof ReferenceIDT)){
            if (!(callerObject instanceof ReferenceIDT)){
                if (callerObject instanceof ObjectIDT){
                    String memberName = ((VariableReferenceNode) member.property).name;
                    if (!((ObjectIDT) callerObject).astNode.mutators.containsKey(memberName)){
                        throw new TranRuntimeException("Unable to find mutator: " + memberName);
                    } else if (!((ObjectIDT) callerObject).astNode.mutators.get(memberName).isShared) {
                        throw new TranRuntimeException("Trying to access member using class name, but the member is not shared");
                    }
                    List<InterpreterDataType> parameter = new LinkedList<>();
                    parameter.add(value);
                    interpretMethodCall(Optional.of((ObjectIDT) callerObject), ((ObjectIDT) callerObject).astNode.mutators.get(memberName), parameter);

                }
                throw new TranRuntimeException("Trying to access a member on an IDT that is not a reference (Not an object)");
            }
            throw new TranRuntimeException("Trying to access a member on an IDT that is not a reference (Not an object)");
        }
        ObjectIDT actualObject = ((ReferenceIDT) callerObject).refersTo.get();
        String memberName = ((VariableReferenceNode) member.property).name;
        if (!((ReferenceIDT) callerObject).refersTo.get().astNode.mutators.containsKey(memberName)){
            throw new TranRuntimeException("Member :" + ((VariableReferenceNode)member.property).name + "Does not have a valid mutator and therefore it's value can not be changed.");
        }
        List<InterpreterDataType> parameter = new LinkedList<>();
        parameter.add(value);
        interpretMethodCall(((ReferenceIDT) callerObject).refersTo, ((ReferenceIDT) callerObject).refersTo.get().astNode.mutators.get(memberName), parameter);
    }

    /**
     * Given a string (the type name), make an IDT for it.
     *
     * @param type The name of the type (string, number, boolean, character). Defaults to ReferenceIDT if not one of those.
     * @return an IDT with default values (0 for number, "" for string, false for boolean, ' ' for character)
     */
    private InterpreterDataType instantiate(String type) {
        switch (type) {
            case "number":
                return new NumberIDT(0);
            case "string":
                return new StringIDT("");
            case "boolean":
                return new BooleanIDT(false);
            case "character":
                return new CharIDT('\0');
            default:
                var classNode = getClassByName(type);
                if (classNode.isEmpty()) {
                    for (var i : top.Interfaces){
                        if (type.equals(i.name)){
                            List<ClassNode> classes = new LinkedList<>();
                            for (var c : top.Classes) {
                                if (c.interfaces.contains(i.name)) {
                                    classes.add(c);
                                }
                            }
                            return new ReferenceIDT(){{this.validAstNodes = classes; this.refersTo = Optional.of(new ObjectIDT(null));}};

                        }
                    }
                    throw new TranRuntimeException(String.format("Unable to find the class that '%s' is type of", type));
                }
                // Sets the class that the variable references to. Members will be set in the constructor call
                return new ReferenceIDT(){{this.refersTo = Optional.of(new ObjectIDT(null)); this.validAstNodes.add(classNode.get());}};
        }
    }
}