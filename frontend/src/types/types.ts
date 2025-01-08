import * as monacoEditor from 'monaco-editor';

export interface File {
      id: number;
      name: string;
      content: string;
      active: boolean;
      viewState: monacoEditor.editor.IEditorViewState| null;
  }