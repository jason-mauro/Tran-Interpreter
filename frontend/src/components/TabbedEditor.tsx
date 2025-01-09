import React from 'react';
import CodeEditor from './CodeEditor';
import TabBar from './TabBar';
import * as monacoEditor from 'monaco-editor';
import ThemePicker from './ThemePicker';
import KeybindPicker from './KeybindPicker';
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

// DESTROY AND MAKE NEW EDITOR EACH TIME THE TAB CHANGES AS THAT WILL HELP WITH THE MEMORY BLOAT AND MORE ! WHICH IS GOOD TO DO



const TabbedEditor: React.FC<TabbedEditorProps> = ({ handleKeybindChange, editorRef, handleThemeChange, theme, keybinds, setFileName, setFiles, files, fileName}) => {







    return (
        <div className="flex flex-col w-[60%] bg-accent p-4 rounded-lg border border-input shadow-sm">
            <TabBar 
                files={files}
                setFileName={setFileName}
                setFiles={setFiles}
                fileName={fileName}
                editorRef={editorRef}
            />
            <CodeEditor
                theme={theme}
                value={files[fileName].content}
                fileName={fileName}
                keybinds={keybinds}
                editorRef={editorRef}
                setFiles={setFiles}
                files={files}
            />
            <div className="flex flex-row py-1">
                <ThemePicker onThemeChange={handleThemeChange} />
                <KeybindPicker onKeybindChange={handleKeybindChange} />
            </div>        
        </div>
    )
}

export default TabbedEditor