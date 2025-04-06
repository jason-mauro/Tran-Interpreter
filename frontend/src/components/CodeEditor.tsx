import Editor, { loader } from '@monaco-editor/react';
import React, { useEffect } from 'react';
import * as monacoEditor from 'monaco-editor';
import { File } from '../types/types';
import { tran } from '../TranLanguageConfig'

interface EditorProps {
    fileName: string;
    value: string
    theme: string;
    keybinds: string;
    editorRef: React.MutableRefObject<monacoEditor.editor.IStandaloneCodeEditor | null>
    setFiles: React.Dispatch<React.SetStateAction<Record<string, File>>>;
    files: Record<string, File>;
}

const CodeEditor: React.FC<EditorProps> = ({fileName, value, theme, editorRef, setFiles , files}) => {

  useEffect(() => {
    monacoEditor.languages.register({ id: 'tran' });
    monacoEditor.languages.setMonarchTokensProvider('tran', tran as monacoEditor.languages.IMonarchLanguage);
  }, []);

  
  useEffect(() => {
    const loadTheme = async () => {
      try {
        // Load monaco editor and register custom language
        const monaco = await loader.init();
        monaco.languages.register({ id: 'tran' });
        monaco.languages.setMonarchTokensProvider('tran', tran as monacoEditor.languages.IMonarchLanguage);
  
        // Fetch the theme JSON
        const response = await fetch(`/themes/${theme}.json`);
        if (!response.ok) {
          throw new Error(`Failed to fetch theme: ${response.statusText}`);
        }
  
        const themeData = await response.json();
  
        // Define and set the theme without modifying token colors
        monaco.editor.defineTheme('customTheme', themeData);
        monaco.editor.setTheme('customTheme');
      } catch (error) {
        console.error("Failed to load theme:", error);
      }
    };
  
    loadTheme();
  }, [theme, editorRef]);

  const restoreEditorState = () => {
    if (editorRef.current && files[fileName].viewState) {
      const editor = editorRef.current;
      editor.restoreViewState(files[fileName].viewState);
    }
  }

  useEffect(() => {
    restoreEditorState();
  }, []);
  

  const handleEditorChange = (value: string | undefined) => {
    // No content change return
    if (!value) return;
    // Update the content for the current File
    setFiles(current => ({
      ...current,
      [fileName]: {
        ...current[fileName],
        content: value
      }
    }));
  };

  
    
    return (
        <Editor
            height={"450px"}
            width={"100%"}
            language="tran"
            theme={theme}
            path={files[fileName].id.toString()}
            defaultValue={value}
            onMount={(editor) => editorRef.current = editor}
            onChange={handleEditorChange}

        />
    );
};

export default CodeEditor;
