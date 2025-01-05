import Editor, { loader, Monaco } from '@monaco-editor/react';
import { useEffect } from 'react';






interface EditorProps {
    code: string;
    setCode: React.Dispatch<React.SetStateAction<string>>;
    theme: string;
    keybinds: string;
}




const CodeEditor: React.FC<EditorProps> = ({code, setCode, theme, keybinds }) => {

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
            value={code}
            theme={theme}
            className="text-3xl w-2/3 h-full"
            defaultValue="{ Write code here }"
            onChange={(value) => setCode(String(value))}
            beforeMount={(monaco: Monaco) => {
                monaco.editor.defineTheme("myCustomTheme", {
                  base: "vs-dark",
                  inherit: true,
                  rules: [
                    { token: "comment", foreground: "ffa500", fontStyle: "italic" },
                    { token: "keyword", foreground: "00ff00" },
                    { token: "string", foreground: "ff0000" },
                  ],
                  colors: {
                    "editor.background": "#1e1e1e",
                    "editor.foreground": "#d4d4d4",
                    "editorCursor.foreground": "#ffffff",
                  },
                });
              }}
        />
    );
};

export default CodeEditor;
