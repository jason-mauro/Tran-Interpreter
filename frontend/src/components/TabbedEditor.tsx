import React, { useState, useEffect } from 'react';
import CodeEditor from './CodeEditor';
import TabBar from './TabBar';
import { Monaco } from '@monaco-editor/react';
import * as monacoEditor from 'monaco-editor';


type Tab = {
    id: number;
    name: string;
    content: string;
    viewState: monacoEditor.editor.IEditorViewState| null;
}

