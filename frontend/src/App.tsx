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
    "demo.tran": {id: 0, name: "demo.tran", content: `class demo\n\tshared start()\n\t\tconsole.write(\"Hello World\")`, viewState: null},
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

  const executeCode = async () => {
	const clientId = Date.now(); // Unique ID for this execution
	const code = files[fileName].content;
	
	// Close existing SSE connection if any
	if (eventSourceRef.current) {
	  eventSourceRef.current.close();
	}
  
	// Set up new SSE connection
	eventSourceRef.current = new EventSource(`http://localhost:8080/api/interpreter/console/${clientId}`);
  
	// Handle SSE events
	eventSourceRef.current.addEventListener('CONSOLE_OUTPUT', (event) => {
	  setOutput(prev => [...prev, event.data]); // Update output when a new event is received
	});
  
	eventSourceRef.current.onerror = (error) => {
	  console.error('SSE Error:', error);
	  eventSourceRef.current?.close();
	};
  
	// Wait for SSE connection to be established
	eventSourceRef.current.onopen = async () => {
	  console.log("SSE connection established");
	  console.log(code);

	  // Execute the code after SSE is confirmed
	  try {
		const response = await fetch(`http://localhost:8080/api/interpreter/execute/${clientId}`, {
		  method: 'POST',
		  headers: {
			'Content-Type': 'application/json',
		  },
		  body: JSON.stringify({ code: code }),
		});
  
		if (!response.ok) {
		  throw new Error('Failed to execute code');
		}
	  } catch (error) {
		console.error('Error executing code:', error);
	  }
	};
};


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
         <div className="flex justify-center items-center bg-background">
          
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
            />
			<Button onClick={executeCode}>Execute</Button>
          
        </div>
      </div>
      <Toaster />
    </ThemeProvider>
  );
}

export default App;
