"use client";

import TabbedEditor from './components/TabbedEditor';
import { ThemeProvider } from './components/theme-provider';
import { useEffect, useState, useRef } from 'react';

import * as monacoEditor from 'monaco-editor';
import { File } from './types/types';


function App() {
  const [theme, setTheme] = useState<string>("GitHub Light");
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
  const handleThemeChange = (theme : string) => {
    setTheme(theme);
  }

  return (
    <ThemeProvider defaultTheme="light" storageKey="vite-ui-theme">
      <div className = "w-full h-100vh bg-background">
        <div className="w-full h-[50px] bg-primary flex items-center p-2">
          <h1 className="text-primary-foreground text-2xl font-sans">Tran Interpreter</h1>
        </div>
         <div className="flex justify-center items-center bg-background">
          <TabbedEditor
            handleKeybindChange={handleKeybindChange} 
            editorRef={editorRef}
            handleThemeChange={handleThemeChange}
            theme={theme}
            keybinds={keybinds}
            setFileName={setFileName}
            setFiles={setFiles}
            files={files}
            fileName={fileName}
            />
        </div>
      </div>
    </ThemeProvider>
  );
}

export default App;
