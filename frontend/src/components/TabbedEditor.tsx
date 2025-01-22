import React from 'react';
import CodeEditor from './CodeEditor';
import TabBar from './TabBar';
import ThemePicker from './ThemePicker';
import KeybindPicker from './KeybindPicker';
import { MousePointer } from 'lucide-react';
import * as monacoEditor from 'monaco-editor';
import { File } from '../types/types';

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
    handleKeybindChange, 
    editorRef, 
    handleThemeChange, 
    theme, 
    keybinds, 
    setFileName, 
    setFiles, 
    files, 
    fileName
}) => {
    return (
        <div className="flex flex-col w-[60%] bg-accent p-4 rounded-lg border border-input shadow-sm">
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
            <div className="flex flex-row py-1">
                <ThemePicker onThemeChange={handleThemeChange} />
                <KeybindPicker onKeybindChange={handleKeybindChange} />
            </div>        
        </div>
    );
};

export default TabbedEditor;