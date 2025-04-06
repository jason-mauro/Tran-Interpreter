declare module 'monaco-emacs' {
    export function emacsInit(editor: monaco.editor.IStandaloneCodeEditor): void;
    export function removeEmacsMode(editor: monaco.editor.IStandaloneCodeEditor): void;
  }
  