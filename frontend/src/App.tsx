"use client";

import Editor from '@/components/Editor';
import { ThemeProvider } from './components/theme-provider';

function App() {
  return (
    <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
      <div className="w-full h-5 bg-primary"></div>
      <div className="flex flex-row justify-center items-center w-full h-[800px] px-[225px] py-10">
          <Editor />
      </div>
    </ThemeProvider>
  );
}

export default App;
