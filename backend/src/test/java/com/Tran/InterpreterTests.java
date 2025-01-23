package com.Tran;

import com.Tran.parser.Parser;
import com.Tran.parser.AST.*;
import com.Tran.interpreter.*;
import com.Tran.interpreter.BuiltIns.ConsoleWrite;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.Tran.lexer.*;

import java.util.List;

public class InterpreterTests {
    @Test
    public void SimpleAdd() {
        String program = """
                class demo
                	fib(number n): number x
                		if n <= 1 and n == 5
                			x = n
                		else
                			x = fib(n - 1) + fib(n-2)
                
                	shared start()
                		console.write(fib(10))
                """;

        var tranNode = run(program);
        var c = getConsole(tranNode);
        Assertions.assertEquals(1,c.size());
        Assertions.assertEquals("12.0",c.getFirst());
    }

    @Test
    public void SimpleAddInstantiate() {
        String program = """
                class demotwo
                    shared start()
                        console.write("Hello Nick")""";
        var tranNode = run(program);


    }

    @Test
    public void SimpleAddInstantiateAndPrint() {
        String program = """
                class SimpleAdd
                    number x
                    number y
                    
                    construct()
                        x = 6
                        y = 6
                        
                    add()
                        number z
                        z = x + y 
                        console.write(z)
                        
                    shared start()
                        SimpleAdd t
                        t = new SimpleAdd()
                        t.add()
                        
                """;
        var tranNode = run(program);
        var c = getConsole(tranNode);
        Assertions.assertEquals(1,c.size());
        Assertions.assertEquals("12.0",c.getFirst());
    }

    @Test
    public void Loop1() {
        String program = "class LoopOne\n" +
                "    shared start()\n" +
                "        boolean keepGoing\n" +
                "        number n\n" +
                "        n = 0\n" +
                "        keepGoing = true\n" +
                "        loop keepGoing\n" +
                "            if n >= 15\n" +
                "                keepGoing = false\n" +
                "            else\n" +
                "                n = n + 1\n" +
                "                console.write(n)\n";
        var tranNode = run(program);
        var c = getConsole(tranNode);
        Assertions.assertEquals(15,c.size());
        Assertions.assertEquals("1.0",c.getFirst());
        Assertions.assertEquals("15.0",c.getLast());
    }

    @Test
    public void student() {
        String program = "class student\n" +
                "    number gradea\n" +
                "    number gradeb\n" +
                "    number gradec\n" +
                "    string firstname\n" +
                "    string lastname\n" +
                "    \n" +
                "    construct (string fname, string lname, number ga, number gb, number gc)\n" +
                "        firstname = fname\n" +
                "        lastname = lname\n" +
                "        gradea = ga\n" +
                "        gradeb = gb\n" +
                "        gradec = gc\n" +
                "    \n" +
                "    getAverage() : number avg \n" +
                "        avg = (gradea + gradeb + gradec)/3\n" +
                "    \n" +
                "    print() \n" +
                "        console.write(firstname, \" \", lastname, \" \", getAverage())\n" +
                "    \n" +
                "    shared start()\n" +
                "        student sa\n" +
                "        student sb\n" +
                "        student sc\n" +
                "        student sd\n" +
                "        sa = new student(\"michael\",\"phipps\",100,99,98)\n" +
                "        sb = new student(\"tom\",\"johnson\",80,75,83)\n" +
                "        sc = new student(\"bart\",\"simpson\",32,25,33)\n" +
                "        sd = new student(\"Domenic\",\"Riccio\",12,24,0)\n" +
                "        sa.print()\n" +
                "        sb.print()\n" +
                "        sc.print()\n" +
                "        sd.print()\n";
        var tranNode = run(program);
        var c = getConsole(tranNode);
        Assertions.assertEquals(4,c.size());
        Assertions.assertEquals("michael phipps 99.0",c.getFirst());
        //Assertions.assertEquals("bart simpson 30.0",c.getLast());
    }

    private static List<String> getConsole(TranNode tn) {
        for (var c : tn.Classes)
            if (c.name.equals("console")) {
                for (var m : c.methods)  {
                    if (m.name.equals("write")) {
                        return ((ConsoleWrite)m).console;
                    }
                }
            }
        throw new RuntimeException("Unable to find console");
    }

    private static TranNode run(String program) {
        var l  = new Lexer(program);
        try {
            var tokens = l.Lex();
            var tran = new TranNode();
            var p = new Parser(tran,tokens);
            p.Tran();
            System.out.println(tran.toString());
            var i = new Interpreter(tran, new ConsoleWrite(){{this.isShared = true; this.isVariadic = true; this.name = "write";}});
            i.start();
            return tran;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
