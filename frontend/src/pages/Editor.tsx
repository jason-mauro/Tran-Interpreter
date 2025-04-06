import React from 'react';
import { Button } from '@/components/ui/button';
import TabbedEditor from '@/components/TabbedEditor';
import * as monacoEditor from 'monaco-editor';
import { File } from '../types/types';

interface EditorPageProps {
    handleKeybindChange: (keybinds: string) => void
    editorRef: React.MutableRefObject<monacoEditor.editor.IStandaloneCodeEditor | null>
    handleThemeChange: (theme: string) => void
    theme: string;
    keybinds: string;
    setFileName: React.Dispatch<React.SetStateAction<string>>;
    setFiles: React.Dispatch<React.SetStateAction<Record<string, File>>>
    files: Record<string, File>;
    fileName: string;
    setOutput: React.Dispatch<React.SetStateAction<string[]>>;
    eventSourceRef: React.MutableRefObject<EventSource | null>; 
    output: string[];
}

const EditorPage: React.FC<EditorPageProps> = ({
  handleKeybindChange,
  editorRef,
  handleThemeChange,
  theme: editorTheme,
  keybinds,
  setFileName,
  setFiles,
  files,
  fileName,
  eventSourceRef,
  setOutput,
  output
}) => {
  const outputContainerRef = React.useRef<HTMLDivElement>(null);

  return (
    <div className="flex flex-col justify-center pt-2 items-center items-gap-2 w-full">
      
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
            <Button 
              onClick={() => setOutput([])} 
              variant="outline"
            >
              Clear
            </Button>
          </div>
          
          <div 
            ref={outputContainerRef}
            className="h-[200px] overflow-y-auto mt-2 border border-input rounded-lg bg-background shadow-sm"
          >
            {output.map((line, index) => (
              <p 
                key={index} 
                className={`text-sm pl-2 ${
                  line?.includes("SyntaxErrorException") || line?.includes("TranRuntimeException") 
                    ? "text-destructive" 
                    : ""
                }`}
              >
                {line}
              </p>
            ))}
          </div>
        
      </div>
    </div>
  );
};

export default EditorPage;