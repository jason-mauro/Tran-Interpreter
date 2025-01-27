import React from 'react';
import CodeEditor from './CodeEditor';
import TabBar from './TabBar';
import ThemePicker from './ThemePicker';
import { MousePointer } from 'lucide-react';
import * as monacoEditor from 'monaco-editor';
import { File } from '../types/types';
import { Button } from '@/components/ui/button';
import RunContext from './RunContext';

interface TabbedEditorProps {
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
}

const EmptyState = () => {
  return (
    <div className="flex flex-col items-center justify-center h-[650px] relative">
      <div className="absolute top-[-25px] right-[20px]">
        <MousePointer 
          className="w-12 h-12 text-primary transform scale-x-[-1]" 
        />
      </div>
      <div className="text-center space-y-2">
        <h3 className="text-2xl font-semibold text-primary">No Files Open</h3>
        <p className="text-muted-foreground">
          Click the plus icon in the top right to create a new file
        </p>
      </div>
    </div>
  );
};

const TabbedEditor: React.FC<TabbedEditorProps> = ({ 
    editorRef, 
    handleThemeChange, 
    theme, 
    keybinds, 
    setFileName, 
    setFiles, 
    files, 
    fileName,
    setOutput,
    eventSourceRef
}) => {
  const [currentId, setCurrentId] = React.useState<number>(0);
	const [context, setContext] = React.useState<string>("current");
  const [running, setRunning] = React.useState<boolean>(false);

	// TODO: Add delimiters to files to track errors for each file $name$ and create new token for them in the lexer for better errors
	const executeCode = async () => {
    setRunning(true);
    const clientId = Date.now(); // Get a unique ID for each code execution
    setCurrentId(clientId);
    const code = context === "current" ? "##" + fileName + "##" + files[fileName].content 
      : "##" + fileName + "##" + [files[fileName].content, ...Object.entries(files)
          .filter(([key]) => key !== fileName)
          .map(([key, file]) => "##" + key + "##" + file.content)]
          .join('\n');
    
    // Close existing SSE connections
    if (eventSourceRef.current) {
      eventSourceRef.current.close();
    }
    
    // Set up the SSE connection for the console
    eventSourceRef.current = new EventSource(`https:localhost:8080/api/interpreter/console/${clientId}`);
    
    // Handle SSE events
    eventSourceRef.current.addEventListener('CONSOLE_OUTPUT', (event) => {
      setOutput(prev => {
        const newOutput = [...prev, event.data];
        return newOutput.slice(-100); // Limit to last 100 lines
      });
    });
    
    eventSourceRef.current.onerror = (error) => {
      console.error('SSE Error:', error);
      setOutput(prev => {
        const newOutput = [...prev, "** Time limit exceeded **"];
        return newOutput.slice(-100); // Limit to last 100 lines
      });
      eventSourceRef.current?.close();
    };
    
    // Wait for SSE connection to be established before executing
    eventSourceRef.current.onopen = async () => {
      console.log("SSE connection established");
      eventSourceRef.current?.addEventListener('EXECUTION_COMPLETED', () => {
        eventSourceRef.current?.close();
        setRunning(false);
      });
   
      // Execute the code
      try {
        const response = await fetch(`https://localhost:8080/api/interpreter/execute/${clientId}`, {
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

   const stopRunning = async () => {
    try {
      const response = await fetch(`https:localhost:8080/api/interpreter/execute/stop/${currentId}`, {
        method: 'POST',
      });
  
      if (!response.ok) {
        throw new Error('Failed to stop code execution');
      }
    } catch (error) {
      console.error('Error stopping execution:', error);
    }
    
    eventSourceRef.current?.close();
    setRunning(false);
  };
    return (
        <div className="flex flex-col w-[70%] bg-accent p-4 rounded-lg border border-input shadow-sm">
            <TabBar 
                files={files}
                setFileName={setFileName}
                setFiles={setFiles}
                fileName={fileName}
                editorRef={editorRef}
            />
            {fileName === "" ? (
                <EmptyState />
            ) : (
                <CodeEditor
                    theme={theme}
                    value={files[fileName].content}
                    fileName={fileName}
                    keybinds={keybinds}
                    editorRef={editorRef}
                    setFiles={setFiles}
                    files={files}
                />
            )}
            <div className="flex flex-row items-end py-1 overflow-auto">
                <ThemePicker onThemeChange={handleThemeChange} />
                
				<RunContext setContext={setContext}/>
				<Button onClick={running ? stopRunning : executeCode} className = "ml-[5px]">{running ? "Stop" : "Run"}</Button>
            </div>        
        </div>
    );
};

export default TabbedEditor;