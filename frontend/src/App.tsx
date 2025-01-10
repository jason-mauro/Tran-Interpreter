"use client";

import TabbedEditor from './components/TabbedEditor';
import { useEffect, useState, useRef } from 'react';
import { Toaster } from '@/components/ui/toaster'
import * as monacoEditor from 'monaco-editor';
import { File } from './types/types';
import { ThemeProvider } from '@/components/theme-provider';
import { ModeToggle } from '@/components/mode-toggle';

function App() {
  const [editorTheme, setEditorTheme] = useState<string>("GitHub Light");
  const [keybinds, setKeybinds] = useState<string>("Default");
  const editorRef = useRef<monacoEditor.editor.IStandaloneCodeEditor | null>(null);
  const [files, setFiles] = useState<Record<string, File>>({
    "demo.tran": {id: 0, name: "demo.tran", content: `class demo\n\tshared start()\n\t\tconsole.write(\"Hello World\")`, viewState: null},
    "demo2.tran": {id: 1, name: "demo2.tran", content: `class demo2\n\tshared start()\n\t\tconsole.write(\"Hello World\")`, viewState: null}
  })

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

  return (
    <ThemeProvider defaultTheme="system" storageKey="vite-ui-theme">
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
        </div>
      </div>
      <Toaster />
    </ThemeProvider>
  );
}

export default App;
