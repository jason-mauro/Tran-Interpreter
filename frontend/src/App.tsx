"use client";

import { useEffect, useState, useRef } from 'react';
import { Toaster } from '@/components/ui/toaster';
import * as monacoEditor from 'monaco-editor';
import { File } from './types/types';
import { ThemeProvider } from '@/components/theme-provider';
import { ModeToggle } from '@/components/mode-toggle';
import EditorPage from './pages/Editor';
import DocsPage from './pages/Docs';
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Link
} from "react-router-dom";

function App() {
  const [editorTheme, setEditorTheme] = useState<string>("GitHub Light");
  const [keybinds, setKeybinds] = useState<string>("Default");
  const editorRef = useRef<monacoEditor.editor.IStandaloneCodeEditor | null>(null);
  const [files, setFiles] = useState<Record<string, File>>({
    "demo.tran": {
      id: 0,
      name: "demo.tran",
      content: `class demo\n\tshared start()\n\t\tconsole.print(\"Hello World\")`,
      viewState: null
    },
    "demo2.tran": {
      id: 1,
      name: "demo2.tran", 
      content: `class demo2
	shared fib(number n) : number x
		if n <= 1
			x = n
		else
			x = fib(n-1) + fib(n-2)

	shared start()
		console.print(fib(20))`,
      viewState: null
    }
  });
  const [output, setOutput] = useState<string[]>([]);
  const eventSourceRef = useRef<EventSource | null>(null);
  const [fileName, setFileName] = useState<string>("demo.tran");
  const outputContainerRef = useRef<HTMLDivElement>(null);

  const file = files[fileName];

  useEffect(() => {
    if (outputContainerRef.current) {
      outputContainerRef.current.scrollTop = outputContainerRef.current.scrollHeight;
    }
  }, [output]);

  useEffect(() => {
    editorRef?.current?.focus();
  }, [fileName, file]);

  const handleKeybindChange = (keybinds: string) => {
    setKeybinds(keybinds);
  };

  const handleThemeChange = (newTheme: string) => {
    setEditorTheme(newTheme);
  };

  return (
    <ThemeProvider defaultTheme="light" storageKey="vite-ui-theme">
      <Router>
        <div className="w-full h-screen bg-background no-scrollbar overflow-y-auto">
          <div className="w-full h-[50px] bg-primary flex items-baseline p-2">
            <div className="flex items-baseline gap-5">
              <Link to="/">
                <h1 className="text-primary-foreground text-2xl font-sans leading-none">Tran Interpreter</h1>
              </Link>
              <Link to="/docs">
                <h2 className="text-primary-foreground font-sans underline text-xl">Docs</h2>
              </Link>
            </div>
            <div className="ml-auto">
              <ModeToggle />
            </div>
          </div>
              <Routes>
                <Route 
                  path="/" 
                  element={
                    <EditorPage
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
                      output={output}
                    />
                  }
                />
                <Route
                  path="/docs"
                  element={
                    <DocsPage></DocsPage>
                  }
                />
              </Routes>
        </div>
        <Toaster />
      </Router>
    </ThemeProvider>
  );
}

export default App;