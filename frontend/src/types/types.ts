import * as monacoEditor from 'monaco-editor';

export interface File {
      id: number;
      name: string;
      content: string;
      viewState: monacoEditor.editor.ICodeEditorViewState| null;
  }