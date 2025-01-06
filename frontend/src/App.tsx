"use client";

import CodeEditor from './components/CodeEditor';
import { ThemeProvider } from './components/theme-provider';
import { useState } from 'react';
import ThemePicker from './components/ThemePicker'
import KeybindPicker from './components/KeybindPicker'

interface File {
  name: string;
  value: string;
}

function App() {
  const [theme, setTheme] = useState<string>("Github Light");
  const [keybinds, setKeybinds] = useState<string>("Default");
  const [files, setFiles] = useState<Record<string, File>>({
    "demo.tran": {name: "demo.tran", value: `class demo\tshared start()\t\tconsole.write(\"Hello World\")`,}
  })

  const [fileName, setFileName] = useState<string>("demo.tran");

  const file = files[fileName];


  const handleKeybindChange = (keybinds : string ) => {
    setKeybinds(keybinds);
  }
  const handleThemeChange = (theme : string) => {
    setTheme(theme);
  }




  return (
    <ThemeProvider defaultTheme="light" storageKey="vite-ui-theme">
      <div className = "w-full h-100vh">
        <div className="w-full h-[50px] bg-primary flex items-center p-2">
          <h1 className="text-background text-2xl font-sans">Tran Interpreter</h1>
        </div>
         <div className="flex justify-center items-center">
          <div className="flex flex-col w-[60%] bg-secondary p-4 rounded-lg shadow-lg">
            <CodeEditor
              theme={theme}
              value={file.value}
              path={file.name}
              keybinds={keybinds}
              
            />
            <div className="flex flex-row">
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
