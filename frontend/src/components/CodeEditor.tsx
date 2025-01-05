import Editor, { Monaco } from '@monaco-editor/react';



interface EditorProps {
    code: string;
    setCode: React.Dispatch<React.SetStateAction<string>>;
    theme: string;
}

const CodeEditor: React.FC<EditorProps> = ({code, setCode, theme }) => {
    
    return (
        <Editor
            height={"800px"}
            width={"66.66%"}
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
