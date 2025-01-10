import Editor, { loader } from '@monaco-editor/react';
import React, { useEffect } from 'react';
import * as monacoEditor from 'monaco-editor';
import { File } from '../types/types';

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
    const loadTheme = async () => {
      try {
        // Load the monaco editor
        const monaco = await loader.init();

        // Encode the theme name for JSON file
        const encodedThemeName = encodeURIComponent(theme);

        // Fetch the theme JSON
        const response = await fetch(`/themes/${encodedThemeName}.json`);
        if (!response.ok) {
          throw new Error(`Failed to fetch theme: ${response.statusText}`);
        }

        const themeData = await response.json();

        // Define and set the custom theme
        monaco.editor.defineTheme('x', themeData);
        monaco.editor.setTheme('x');
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
            height={"650px"}
            width={"100%"}
            language="python"
            theme={theme}
            path={files[fileName].id.toString()}
            defaultValue={value}
            onMount={(editor) => editorRef.current = editor}
            onChange={handleEditorChange}

        />
    );
};

export default CodeEditor;
