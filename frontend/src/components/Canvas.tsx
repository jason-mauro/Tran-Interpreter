import React, { useState, useRef, useEffect } from "react";
import MonacoEditor from "@monaco-editor/react";

type Tab = {
  id: number;
  title: string; // Tab title
  path?: string; // Optional file path
  content: string; // Editor content
  viewState: monaco.editor.ICodeEditorViewState | null; // Saved Monaco view state
};

const MultiTabEditor: React.FC = () => {
  const [tabs, setTabs] = useState<Tab[]>([
    { id: 1, title: "File 1", content: "Initial content for File 1", viewState: null },
    { id: 2, title: "File 2", content: "Initial content for File 2", viewState: null },
  ]);
  const [activeTabId, setActiveTabId] = useState(1); // Currently active tab ID
  const editorRef = useRef<monaco.editor.IStandaloneCodeEditor | null>(null);

  // Get the active tab
  const activeTab = tabs.find((tab) => tab.id === activeTabId);

  // Save editor instance
  const handleEditorDidMount = (editor: monaco.editor.IStandaloneCodeEditor) => {
    editorRef.current = editor;
  };

  // Handle tab switching
  const switchTab = (tabId: number) => {
    if (!editorRef.current) return;

    setTabs((prevTabs) =>
      prevTabs.map((tab) => {
        if (tab.id === activeTabId) {
          // Save the current tab's content and view state
          return {
            ...tab,
            content: editorRef.current?.getValue() || "",
            viewState: editorRef.current?.saveViewState(),
          };
        }
        return tab;
      })
    );

    // Switch to the new tab
    const newTab = tabs.find((tab) => tab.id === tabId);
    if (newTab) {
      editorRef.current.setValue(newTab.content); // Load the new tab's content
      if (newTab.viewState) {
        editorRef.current.restoreViewState(newTab.viewState); // Restore the new tab's view state
      }
      editorRef.current.focus();
      setActiveTabId(tabId);
    }
  };

  // Add a new tab
  const addTab = () => {
    const newTab: Tab = {
      id: tabs.length + 1,
      title: `File ${tabs.length + 1}`,
      content: "",
      viewState: null,
    };
    setTabs([...tabs, newTab]);
    switchTab(newTab.id);
  };

  // Close a tab
  const closeTab = (tabId: number) => {
    const updatedTabs = tabs.filter((tab) => tab.id !== tabId);

    if (tabId === activeTabId && updatedTabs.length > 0) {
      // Switch to another tab if the active one is closed
      switchTab(updatedTabs[0].id);
    }

    setTabs(updatedTabs);
  };

  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100vh" }}>
      {/* Tab bar */}
      <div style={{ display: "flex", backgroundColor: "#eee", padding: "5px" }}>
        {tabs.map((tab) => (
          <div
            key={tab.id}
            onClick={() => switchTab(tab.id)}
            style={{
              padding: "5px 10px",
              marginRight: "5px",
              cursor: "pointer",
              backgroundColor: tab.id === activeTabId ? "#ddd" : "#fff",
              border: tab.id === activeTabId ? "1px solid #ccc" : "1px solid transparent",
              borderRadius: "5px",
            }}
          >
            {tab.title}{" "}
            <span onClick={(e) => { e.stopPropagation(); closeTab(tab.id); }}>✖</span>
          </div>
        ))}
        <button onClick={addTab} style={{ marginLeft: "auto", padding: "5px" }}>
          + Add Tab
        </button>
      </div>

      {/* Editor */}
      <div style={{ flexGrow: 1 }}>
        {activeTab && (
          <MonacoEditor
            height="100%"
            defaultLanguage="javascript"
            value={activeTab.content}
            onMount={handleEditorDidMount}
          />
        )}
      </div>
    </div>
  );
};

export default MultiTabEditor;
