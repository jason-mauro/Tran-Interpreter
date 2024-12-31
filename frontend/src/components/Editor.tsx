import React, { useState, useRef, useEffect } from 'react';
import AceEditor from 'react-ace';


// Import required modes and themes
import 'ace-builds/src-noconflict/mode-java';
import 'ace-builds/src-noconflict/theme-github';
import 'ace-builds/src-noconflict/theme-github_dark';
import 'ace-builds/src-noconflict/theme-gruvbox_dark_hard';
import 'ace-builds/src-noconflict/theme-kr_theme';
import 'ace-builds/src-noconflict/theme-github_light_default';



const MultiTabEditor = () => {
  const [tabs, setTabs] = useState([
    { id: 1, name: 'main.tran', content: 'console.log("Hello, Tab 1!");' },
    { id: 2, name: 'Untitled(1)', content: 'console.log("Hello, Tab 2!");' },
  ]);
  const [activeTab, setActiveTab] = useState(1);
  const newTabRef = useRef<HTMLInputElement | null>(null);
  const [editingTabId, setEditingTabId] = useState<number | null>(null);
  const inputRef = useRef<{ [key: number]: HTMLInputElement | null }>({});


  const handleEditorChange = (newContent: string) => {
    setTabs((prevTabs) =>
      prevTabs.map((tab) =>
        tab.id === activeTab ? { ...tab, content: newContent } : tab
      )
    );
  };

  useEffect(() => {
    if (newTabRef.current) {
      newTabRef.current.focus();
    }
  }, [tabs]);

  const addTab = () => {
    const newTabId = Math.max(...tabs.map(tab => tab.id)) + 1;
    const defaultName = `Untiltled(${newTabId})`;
    
    setTabs([...tabs, { id: newTabId, name: defaultName, content: '' }]);
    setActiveTab(newTabId);
    setEditingTabId(newTabId);
  };

  const removeTab = (id: number) => {
    const confirmRemove = window.confirm('Do you want to remove this tab?');
    if (confirmRemove) {
      setTabs((prevTabs) => prevTabs.filter((tab) => tab.id !== id));
      if (activeTab === id) {
        const remainingTabs = tabs.filter(tab => tab.id !== id);
        setActiveTab(remainingTabs[0]?.id || 1);
      }
    }
  };

  const handleTabClick = (id: number) => {
    setActiveTab(id);
  };

  const handleTabNameChange = (id: number, newName: string) => {
    setTabs((prevTabs) =>
      prevTabs.map((tab) =>
        tab.id === id ? { ...tab, name: newName} : tab
      )
    );
  };

  const handleRenameKeyPress = (event: React.KeyboardEvent<HTMLInputElement>, tabId: number) => {
    if (event.key === 'Enter') {
      const newName = (event.target as HTMLInputElement).value;
      if (!newName) {
        handleTabNameChange(tabId, `Untitled(${tabId})`);
      }
      inputRef.current[tabId]?.blur();
      setEditingTabId(null);
    }
  };

  const handleBlur = (tabId: number) => {
    const tab = tabs.find(t => t.id === tabId);
    if (tab && !tab.name.trim()) {
      handleTabNameChange(tabId, `Untitled(${tabId})`);
    } else if (tab){
        handleTabNameChange(tabId, tab.name.replace(/\s/g, ""))
    }
    setEditingTabId(null);
  };


  return (
    <div className="w-2/3 h-full">
      <div className="flex items-center rounded-t-[0.3rem] bg-secondary m-0 p-0 overflow-x-auto whitespace-nowrap scrollbar-thin scrollbar-thumb-gray-500 scrollbar-track-gray-100 hover:scrollbar-thumb-gray-700">
        {tabs.map((tab) => (
          <div
            key={tab.id}
            className={`flex items-center ${
              activeTab === tab.id ? 'bg-primary rounded-t-[0.3rem]' : 'bg-secondary '
            }  flex justify-center border-background w-[160px] h-[35px] cursor-pointer`}
            onClick={() => handleTabClick(tab.id)}
          >
            <input
              type="text"
              value={tab.name}
              onChange={(e) => handleTabNameChange(tab.id, e.target.value)}
              onKeyDown={(e) => handleRenameKeyPress(e, tab.id)}
              onFocus={() => setEditingTabId(tab.id)}
              onBlur={() => handleBlur(tab.id)}
              onMouseDown={(e) => e.preventDefault()}
              onDoubleClick={() => inputRef.current[tab.id]?.focus()}
              ref={(el) => {
                inputRef.current[tab.id] = el;
                if (editingTabId === tab.id) {
                  newTabRef.current = el;
                }
              }}
              className={`bg-transparent border-none font-bold w-[120px] text-center ${
                editingTabId === tab.id ? 'cursor-text' : 'cursor-pointer'
              }`}
            />
            <button
              onClick={(e) => {
                e.stopPropagation();
                removeTab(tab.id);
              }}
              className="ml-1.5 text-white border-none rounded-full cursor-pointer text-center"
            >
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 384 512"><path d="M376.6 84.5c11.3-13.6 9.5-33.8-4.1-45.1s-33.8-9.5-45.1 4.1L192 206 56.6 43.5C45.3 29.9 25.1 28.1 11.5 39.4S-3.9 70.9 7.4 84.5L150.3 256 7.4 427.5c-11.3 13.6-9.5 33.8 4.1 45.1s33.8 9.5 45.1-4.1L192 306 327.4 468.5c11.3 13.6 31.5 15.4 45.1 4.1s15.4-31.5 4.1-45.1L233.7 256 376.6 84.5z"/></svg>
            </button>
          </div>
        ))}
        <button
          onClick={addTab}
          className="h-[35px] w-[35px] ml-[2px]  bg-primary text-white border-background rounded-[0.3rem] cursor-pointer"
        >
          +
        </button>
      </div>

      {tabs.map(
        (tab) =>
          tab.id === activeTab && (
            <div key={tab.id} className="w-full h-full" >
              <AceEditor
                mode="java"
                theme="github_light_default"
                name={`EDITOR_${tab.id}`}
                width="100%"
                height="100%"
                value={tab.content}
                onChange={handleEditorChange}
                setOptions={{ 
                    fontSize: 18,
                    printMargin: false,
                    fontFamily: 'Monaspace'
                 }}
                style={{paddingTop: '100px'}}
              />
            </div>
          )
      )}
    </div>
  );
};


export default MultiTabEditor;