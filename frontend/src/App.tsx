"use client";

import CodeEditor from './components/CodeEditor';
import { ThemeProvider } from './components/theme-provider';
import { useEffect, useState, useRef } from 'react';
import ThemePicker from './components/ThemePicker'
import KeybindPicker from './components/KeybindPicker'
import * as monacoEditor from 'monaco-editor';
import TabBar from './components/TabBar';

interface File {
  name: string;
  content: string;
}

function App() {
  const [theme, setTheme] = useState<string>("Github Light");
  const [keybinds, setKeybinds] = useState<string>("Default");
  const editorRef = useRef<monacoEditor.editor.IStandaloneCodeEditor | null>(null);
  const [files, setFiles] = useState<Record<string, File>>({
    "demo.tran": {name: "demo.tran", content: `class demo\n\tshared start()\n\t\tconsole.write(\"Hello World\")`},
  })

  const [fileName, setFileName] = useState<string>("demo.tran");

  const file = files[fileName];

  useEffect(() => {
    editorRef?.current?.focus();
  }, [file.name]);


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
          <div className="flex flex-col w-[60%] bg-accent p-4 rounded-lg border border-input shadow-sm">
            <TabBar 
              setFiles={setFiles}
              setFileName={setFileName}/>
            <CodeEditor
              theme={theme}
              value={file.content}
              path={file.name}
              keybinds={keybinds}
              editorRef={editorRef}
            />
            <div className="flex flex-row py-1">
              <ThemePicker onThemeChange={handleThemeChange} />
              <KeybindPicker onKeybindChange={handleKeybindChange} />
            </div>
            
          </div>
        </div>
      </div>
    </ThemeProvider>
  );
}

export default App;
