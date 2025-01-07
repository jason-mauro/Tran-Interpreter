import Editor, { loader } from '@monaco-editor/react';
import React, { useEffect } from 'react';
import * as monacoEditor from 'monaco-editor';

interface EditorProps {
    path: string;
    value: string
    theme: string;
    keybinds: string;
    editorRef: React.MutableRefObject<monacoEditor.editor.IStandaloneCodeEditor | null>
}

const CodeEditor: React.FC<EditorProps> = ({path, value, theme, keybinds, editorRef }) => {

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
  }, [theme]);
    
    
    return (
        <Editor
            height={"650px"}
            width={"100%"}
            language="python"
            theme={theme}
            path={path}
            defaultValue={value}
            onMount={(editor) => editorRef.current = editor}
        />
    );
};

export default CodeEditor;
