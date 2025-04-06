

const DocsPage = () => {
    return (
        <div className="flex justify-center my-10 no-scrollbar">
        <div className="text-primary-background w-full max-w-4xl px-4 flex flex-col">
            <h1 className="scroll-m-20 text-4xl font-bold tracking-tighter lg:text-5xl">
                Tran Language Definition
            </h1>
            <h2 className="mt-10 scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight transition-colors first:mt-0">
                Introduction
            </h2>
            <p className="leading-7 [&:not(:first-child)]:mt-6">
                Tran is an object-oriented language created by Professor Michael Phipps at UAlbany. It is similar to other languages with a few differences:
            </p>
            <ol type="1" className="list-decimal pl-6 mt-6 block">
                <li>Tran methods can have multiple return types</li>
                <li>Tran uses indentation for blocks, like Python</li>
                <li>Tran members are private and <strong>need</strong> accessors or mutators to be accessed</li>
                <li>Tran uses the keyword <strong>shared</strong> to represent static method and members</li>
                <li>Tran methods are public unless specified with the <strong>private</strong> keyword</li>
                <li>There is one type of loop (<strong>loop</strong>) instead of for, while, do-while</li>
            </ol>
            <h3 className="mt-10 scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight transition-colors first:mt-0">
                Types
            </h3>
            <ul className="my-6 ml-6 list-disc [&>li]:mt-2">
                <li><strong>number</strong> (floating point) - primitive type like Java</li>
                <li><strong>string</strong> (an arbitrarily large string of characters) - class, like Java</li>
                <li><strong>character</strong> (a single number/letter/symbol) - primitive type, like Java</li>
                <li><strong>boolean</strong> (built-in class) - see below</li>
            </ul>
            <h3 className="mt-10 scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight transition-colors first:mt-0">
                Classes
            </h3>
            <p className="leading-7 [&:not(:first-child)]:mt-6">
                Classes in Tran are defined using the <strong>class</strong> keyword followed by the class name. The class body can contain fields, methods, constructors, and other member declarations. You can only have multiple classes per file, but no inner classes.
            </p>
            <h3 className="mt-10 scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight transition-colors first:mt-0">
                Blocks
            </h3>
            <p className="leading-7 [&:not(:first-child)]:mt-6">
                Blocks are one or more statements that are run consecutively. Blocks of code are identified by indentation one level deeper (further right) than that of the enclosing owner. To mark the end of a block, “dedent” one level. Indents on empty lines or ones with only comments are not counted.
            </p>
            <p className="leading-7 [&:not(:first-child)]:mt-6">
                <strong>Example:</strong>
            </p>
            <pre className="bg-secondary p-4 rounded-md">
                <code className="text-secondary-foreground">
                    class Example1<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;number x<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;string y<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;construct() {'{called when someone uses “new” }'}<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;x = 0<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;y = ""<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;doSomeWork()<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;x=10<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;loop x.times()<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;console.print(“In The Block”)<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;console.print(“Out of the block”)
                </code>
            </pre>

            <h3 className="mt-10 scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight transition-colors first:mt-0">
                New
            </h3>
            <p className="leading-7 [&:not(:first-child)]:mt-6">
                In Tran, the new keyword creates an object of the specified class. It allocates the memory for the object and creates a reference to the object. The reference can then be used to access properties and methods.
            </p>
            <p className="leading-7 [&:not(:first-child)]:mt-6">
                <strong>Example:</strong>
            </p>
            <pre className="bg-secondary p-4 rounded-md">
                <code className="text-secondary-foreground">
                    Student instanceOfStudent<br />
                    instanceOfStudent = new Student ()
                </code>
            </pre>

            <h3 className="mt-10 scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight transition-colors first:mt-0">
                Constructors
            </h3>
            <p className="leading-7 [&:not(:first-child)]:mt-6">
                A constructor is a special method used to initialize objects and is called when an instance of a class has been created. They are defined using the keyword construct(). Constructors can be overloaded, providing the ability to initialize these objects with different parameters.
            </p>
            <p className="leading-7 [&:not(:first-child)]:mt-6">
                <strong>Example:</strong>
            </p>
            <pre className="bg-secondary p-4 rounded-md">
                <code className="text-secondary-foreground">
                    class Example2<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;number x<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;string y<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;construct() {'{called when someone uses “new” }'}<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;x = 0<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;y = ""<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;construct(number n) {'{An example of an overloaded constructor}'}<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;x = n<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;y = ""
                </code>
            </pre>

            <h3 className="mt-10 scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight transition-colors first:mt-0">
                Comments
            </h3>
            <p className="leading-7 [&:not(:first-child)]:mt-6">
                Comments start with {'{and end with}'}. Comments can span multiple lines. Comments can be nested.
            </p>
            <pre className="bg-secondary p-4 rounded-md">
                <code className="text-secondary-foreground">
                    {'{This is a comment}'}<br />
                    {'{This is {also} a comment}'}<br />
                    {'{This'} <br />
                    is a <br />
                    comment that<br />
                    spans multiple lines{'}'}
                </code>
            </pre>

            <h3 className="mt-10 scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight transition-colors first:mt-0">
                Statement Types
            </h3>
            <p className="leading-7 [&:not(:first-child)]:mt-6">
                Variables can be assigned on the same line they are declared or after declaration. Tran allows multiple variables of the same type to be declared in the same line as well, supporting initialization at declaration.
            </p>
            <pre className="bg-secondary p-4 rounded-md">
                <code className="text-secondary-foreground">
                    string s<br />
                    s = "agc"<br />
                    number x<br />
                    x = 4656<br />
                    number k,e,n,x<br />
                    k = 10.132023<br />
                    e = 4.122024<br />
                    n = 7.052024<br />
                    x = 7.232021<br />
                    number d = 42<br />
                    number a = 10.132023, b = 4.122024, c = 7.052024
                </code>
            </pre>

            <h3 className="mt-10 scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight transition-colors first:mt-0">
                If Statements
            </h3>
            <p className="leading-7 [&:not(:first-child)]:mt-6">
                In Tran, an if statement works almost exactly like that in Java. The only difference lies in the syntax, in Java, the condition is surrounded by parentheses, whereas in Tran, it is not. Tran also has else and else if statements that work like those in Java.
            </p>
            <pre className="bg-secondary p-4 rounded-md">
                <code className="text-secondary-foreground">
                    number n<br />
                    n = 200<br />
                    if n {'>'} 100<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;n = n -1<br />
                    else if n {'<'} 50<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;n=n+1<br />
                    else<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;console.print (“Not in either case!”)
                </code>
            </pre>

            <h3 className="mt-10 scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight transition-colors first:mt-0">
                Loops
            </h3>
            <p className="leading-7 [&:not(:first-child)]:mt-6">
                In Tran, the loop construct replaces traditional for, while, and do-while loops. The condition can either be an iterator or a boolean. When using an iterator, the loop will continue until the iterator reaches its end, effectively evaluating to false. For Boolean conditions, the loop will run until the condition evaluates to false.
            </p>
            <pre className="bg-secondary p-4 rounded-md">
                <code className="text-secondary-foreground">
                    boolean keepGoing = boolean.true<br />
                    loop keepGoing<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;if n {'>'}= 65<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;keepGoing = boolean.false<br />
                    number x = 10<br />
                    loop x.times()<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;console.print (“Hello”)<br />
                    number temp<br />
                    number x = 18<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;loop temp = x.times() {"{the loop is being assigned to the variable 'temp'}"}<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;console.print (temp)
                </code>
            </pre>

            <h3 className="mt-10 scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight transition-colors first:mt-0">
                Method Overview
            </h3>
            <p className="leading-7 [&:not(:first-child)]:mt-6">
                Methods are made of the following components:
            </p>
            <ul className="my-6 ml-6 list-disc [&>li]:mt-2">
                <li><strong>Optional Access Modifier:</strong> shared (works like static in Java), private (restricts access to the method or variable)</li>
                <li><strong>Name:</strong> A descriptive name for the method</li>
                <li><strong>Parameters (optional):</strong> Input values specified inside parentheses, separated by commas</li>
                <li><strong>Return Values (optional):</strong> Return values are specified after the colon in the method header</li>
                <li><strong>Body:</strong> Contains all the code within one indentation level</li>
            </ul>

            <h3 className="mt-10 scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight transition-colors first:mt-0">
                Method Calls
            </h3>
            <p className="leading-7 [&:not(:first-child)]:mt-6">
                A method call involves executing a predefined block of code by its name and passing in any required arguments. When a method is called, it can return multiple types or no return type at all.
            </p>
            <pre className="bg-secondary p-4 rounded-md">
                <code className="text-secondary-foreground">
                    shared main()<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;printNumbers()<br/>
                    &nbsp;&nbsp;&nbsp;&nbsp;x,y,z = getNumbers()
                </code>
            </pre>

            <h3 className="mt-10 scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight transition-colors first:mt-0">
                Built-in Objects
            </h3>
            <p className="leading-7 [&:not(:first-child)]:mt-6">
                Methods Available:
            </p>
            <ul className="my-6 ml-6 list-disc [&>li]:mt-2">
                <li><strong>times():</strong> Available on number objects. It returns an iterator that generates a sequence from 1 to the specified number.</li>
            </ul>
            <pre className="bg-secondary p-4 rounded-md">
                <code className="text-secondary-foreground">
                    number n<br />
                    n=10<br />
                    loop n.times()<br />
                    &nbsp;&nbsp;&nbsp;&nbsp;console.print(“This is iteration number” + n)
                </code>
            </pre>
            <ul className="my-6 ml-6 list-disc [&>li]:mt-2">
                <li><strong>console</strong> is a class with a shared method<br />print(params...)</li>
                <li>console.print outputs to the console</li>
                <li>boolean is an included class</li>
            </ul>
            <h3 className="mt-10 scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight transition-colors first:mt-0">
            Boolean Class and Operations
            </h3>
                <p className="leading-7 [&:not(:first-child)]:mt-6">
                    The Boolean class defines the fundamental logical operations in Tran. It includes predefined constants for <code>true</code> and <code>false</code> and methods for logical operations such as <code>not</code>, <code>and</code>, and <code>or</code>.
                </p>
                <pre className="bg-secondary p-4 rounded-md">
                    <code className="text-secondary-foreground">
                        class boolean<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;shared boolean true<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;accessor: {'{ makes this publicly readable }'}<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;value = true<br />

                        &nbsp;&nbsp;&nbsp;&nbsp;shared boolean false<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;accessor: {'{ makes this publicly readable }'}<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;value = false<br />

                        &nbsp;&nbsp;&nbsp;&nbsp;shared not(boolean value) : boolean result<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if value == true<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;result = false<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;else<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;result = true<br />

                        &nbsp;&nbsp;&nbsp;&nbsp;shared and (boolean a, boolean b) : boolean result<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if a == false<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;result = false<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;else<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if b == false<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;result = false<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;else<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;result = true<br />

                        &nbsp;&nbsp;&nbsp;&nbsp;shared or (boolean a, boolean b) : boolean result<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if a == true<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;result = true<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;else<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if b == true<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;result = true<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;else<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;result = false<br />
                    </code>
                </pre>

                <h3 className="mt-10 scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight transition-colors first:mt-0">
                    Interfaces
                </h3>
                <p className="leading-7 [&:not(:first-child)]:mt-6">
                    In Tran, an interface defines a set of methods and members that a class must implement. Interfaces do not impose any inheritance rules, and a class can implement as many interfaces as needed. Interfaces provide a blueprint for classes without defining how methods should be implemented.
                </p>
                <pre className="bg-secondary p-4 rounded-md">
                    <code className="text-secondary-foreground">
                        interface someName<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;square() : number s<br />

                        class TranExample implements someName<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;shared start()<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;number x = 10<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;number y = square(x)<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;console.print(y)<br />

                        &nbsp;&nbsp;&nbsp;&nbsp;square(number x) : number s {'{The method we defined in the interface is used here!}'}<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;s = x * x<br />

                        class UseTranExample<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;someMethod()<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;someName t = new TranExample() {'{t is an instance of someName}'}<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;t.square(20)<br />
                    </code>
                </pre>

                <h3 className="mt-10 scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight transition-colors first:mt-0">
                    Operators and Comparisons
                </h3>
                <p className="leading-7 [&:not(:first-child)]:mt-6">
                    Tran provides several operators for mathematical and logical operations. The order of operations follows the typical rules found in many languages, with parentheses having the highest precedence.
                </p>
                <ul className="my-6 ml-6 list-disc [&>li]:mt-2">
                    <li><strong>Number Operators:</strong> <code>+</code>, <code>-</code>, <code>*</code>, <code>/</code>, <code>%</code>. These operators follow the order of operations: parentheses {'>'} multiplication, division, and modulo {'>'} addition and subtraction. All of these operations are left-associative.</li>
                    <li><strong>Character Operators:</strong> Characters do not support operators in Tran.</li>
                    <li><strong>String Operators:</strong> <code>+</code> is used for concatenation. This operator is left-associative.</li>
                    <li><strong>Comparison Operators:</strong> The comparison operators in Tran are: <code>==</code> (equals), <code>!=</code> (not equal), <code>{'<'}</code> (less than), <code>{'<'}=</code> (less than or equal to), <code>{'>'}</code> (greater than), <code>{'>'}=</code> (greater than or equal to). Comparisons can only be made between values of the same type, and these operators are also left-associative.</li>
                </ul>
                <pre className="bg-secondary p-4 rounded-md">
                    <code className="text-secondary-foreground">
                        number a = 5<br />
                        number b = 10<br />
                        if a == b<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;console.print("Numbers are equal")<br />
                        if a != b<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;console.print("Numbers are not equal")<br />
                        if a {'<'} b<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;console.print("a is less than b")<br />
                    </code>
                </pre>

                <h3 className="mt-10 scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight transition-colors first:mt-0">
                    Example Program
                </h3>
                <p className="leading-7 [&:not(:first-child)]:mt-6">
                    Here's an example program that demonstrates some of the concepts in Tran, including variables, operators, and method calls.
                </p>
                <pre className="bg-secondary p-4 rounded-md">
                    <code className="text-secondary-foreground">
                        shared main()<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;number a = 5<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;number b = 10<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;boolean result = not(a == b)<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;if result<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;console.print("a is not equal to b")<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;else<br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;console.print("a is equal to b")<br />
                    </code>
                </pre>
        </div>
        </div>
    )
}

export default DocsPage;
