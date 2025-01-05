"use client";

import CodeEditor from './components/CodeEditor';
import { ThemeProvider } from './components/theme-provider';
import { useState } from 'react';


function App() {

  const [code, setCode] = useState("hello world");

  



  return (
    <ThemeProvider defaultTheme="light" storageKey="vite-ui-theme">
      <div className = "w-full h-100vh">
        <div className="w-full h-[50px] bg-primary flex items-center p-2">
          <h1 className="text-background text-2xl font-sans">Tran Interpreter</h1>
        </div>
        <div className="flex flex-row justify-center items-center h-[800px] drop-shadow rounded-[0.3rem] mt-10" >
            <CodeEditor
                theme={'vs-dark'}
                code={code}
                setCode={setCode}
            />
        </div>
      </div>
    </ThemeProvider>
  );
}

export default App;
