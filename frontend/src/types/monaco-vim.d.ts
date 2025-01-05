declare module 'monaco-vim' {
    export function initVimMode(editor: monaco.editor.IStandaloneCodeEditor, element: HTMLElement): void;
    export function removeVimMode(editor: monaco.editor.IStandaloneCodeEditor): void;
  }
  