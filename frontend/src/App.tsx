"use client";

import TabbedEditor from './components/TabbedEditor';
import { useEffect, useState, useRef } from 'react';
import { Toaster } from '@/components/ui/toaster'
import * as monacoEditor from 'monaco-editor';
import { File } from './types/types';
import { ThemeProvider } from '@/components/theme-provider';
import { ModeToggle } from '@/components/mode-toggle';
import { Button } from '@/components/ui/button';

function App() {
  const [editorTheme, setEditorTheme] = useState<string>("GitHub Light");
  const [keybinds, setKeybinds] = useState<string>("Default");
  const editorRef = useRef<monacoEditor.editor.IStandaloneCodeEditor | null>(null);
  const [files, setFiles] = useState<Record<string, File>>({
    "demo.tran": {id: 0, name: "demo.tran", 
      content: `class demo
	shared fib(number n) : number x
		if n <= 1
			x = n
		else
			x = fib(n-1) + fib(n-2)

	shared start()
		console.write(fib(20))`, viewState: null},
    "demo2.tran": {id: 1, name: "demo2.tran", content: `class demo2\n\tshared start()\n\t\tconsole.write(\"Hello World\")`, viewState: null}
  })
  const [output, setOutput] = useState<string[]>([]);
  const eventSourceRef = useRef<EventSource | null>(null);

  const [fileName, setFileName] = useState<string>("demo.tran");

  const file = files[fileName];

  useEffect(() => {
    editorRef?.current?.focus();
  }, [fileName, file]);

  const handleKeybindChange = (keybinds : string ) => {
    setKeybinds(keybinds);
  }
  const handleThemeChange = (newTheme: string) => {
    setEditorTheme(newTheme);
  }

  
  useEffect(() => {
	console.log(output);
  }, [output]);

  return (
    <ThemeProvider defaultTheme="light" storageKey="vite-ui-theme">
      <div className = "w-full h-screen bg-background">
      <div className="w-full h-[50px] bg-primary flex justify-between items-center p-2">
        <h1 className="text-primary-foreground text-2xl font-sans">Tran Interpreter</h1>
        <ModeToggle />
      </div>
         <div className="flex flex-col  justify-center items-center bg-background mt-2">
          
          <TabbedEditor
            handleKeybindChange={handleKeybindChange} 
            editorRef={editorRef}
            handleThemeChange={handleThemeChange}
            theme={editorTheme}
            keybinds={keybinds}
            setFileName={setFileName}
            setFiles={setFiles}
            files={files}
            fileName={fileName}
            eventSourceRef={eventSourceRef}
            setOutput={setOutput}
            />

          <div className="flex flex-col w-[70%] bg-accent p-4 rounded-lg border border-input shadow-sm mt-2">
            <div className="flex items-center justify-between">
              <h3 className="text-lg text-accent-foreground">Console Output</h3>
              <Button onClick={() => setOutput([])} variant="outline">Clear</Button>
            </div>
            <div className="h-[200px] overflow-y-auto mt-2 border border-input rounded-lg bg-background shadow-sm ">
              {output.map((line, index) => (
                <p key={index} className={`text-sm pl-2 ${
                  line?.includes("SyntaxErrorException") ? "text-red-500" : ""
                }`}>{line}</p>

              ))}
            </div>
          </div>
		
        </div>
      </div>
      <Toaster />
    </ThemeProvider>
  );
}

export default App;