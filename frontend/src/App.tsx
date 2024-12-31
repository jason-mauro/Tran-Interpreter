"use client";

import Editor from '@/components/Editor';
import { ThemeProvider } from './components/theme-provider';

function App() {
  return (
    <ThemeProvider defaultTheme="light" storageKey="vite-ui-theme">
      <div className = "w-full h-100vh">
        <div className="w-full h-[50px] bg-primary flex items-center p-2">
          <h1 className="text-background text-2xl font-sans">Tran Interpreter</h1>
        </div>
        <div className="flex flex-row justify-center items-center w-full h-[800px] py-10 drop-shadow rounded-[0.3rem]">
            <Editor />
        </div>
      </div>
    </ThemeProvider>
  );
}

export default App;
