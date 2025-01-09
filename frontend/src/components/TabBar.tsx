"use client"

import React, { useState, useRef, useEffect } from 'react'
import { X, Plus } from 'lucide-react'
import { File } from '../types/types'
import * as monacoEditor from 'monaco-editor'
import { useToast } from '@/hooks/use-toast';
import { ToastAction } from '@/components/ui/toast';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"

interface TabProps {
    files: Record<string, File>
    setFiles: React.Dispatch<React.SetStateAction<Record<string, File>>>
    setFileName: React.Dispatch<React.SetStateAction<string>>
    fileName: string;
    editorRef: React.MutableRefObject<monacoEditor.editor.IStandaloneCodeEditor | null>
}

const TabBar: React.FC<TabProps> = ({ files, setFiles, setFileName, fileName, editorRef}) => {
  const { toast } = useToast();
  const [nextId, setNextId] = useState(Object.keys(files).length);
  const [tabs, setTabs] = useState<string[]>(Object.keys(files));
  const [showScrollbar, setShowScrollbar] = useState(false)
  const [isHovering, setIsHovering] = useState(false)
  const scrollContainerRef = useRef<HTMLDivElement>(null)
  const scrollbarRef = useRef<HTMLDivElement>(null)
  const [editingTabName, setEditingTabName] = useState<string | null>(null)
  const newTabInputRef = useRef<HTMLInputElement | null>(null)


  useEffect(() => {
    const checkForOverflow = () => {
      if (scrollContainerRef.current) {
        const hasOverflow = scrollContainerRef.current.scrollWidth > scrollContainerRef.current.clientWidth
        setShowScrollbar(hasOverflow)
      }
    }

    checkForOverflow()
    window.addEventListener('resize', checkForOverflow)

    return () => {
      window.removeEventListener('resize', checkForOverflow)
    }
  }, [files])

  const handleScroll = () => {
    if (scrollContainerRef.current && scrollbarRef.current) {
      const { scrollLeft, scrollWidth, clientWidth } = scrollContainerRef.current;
      const maxScroll = scrollWidth - clientWidth;
      
      if (scrollLeft < 0) {
        scrollContainerRef.current.scrollLeft = 0;
      } else if (scrollLeft > maxScroll) {
        scrollContainerRef.current.scrollLeft = maxScroll;
      }

      const scrollPercentage = scrollLeft / maxScroll;
      scrollbarRef.current.scrollLeft = scrollPercentage * (scrollbarRef.current.scrollWidth - scrollbarRef.current.clientWidth);
    }
  };

  const handleScrollbarScroll = () => {
    if (scrollContainerRef.current && scrollbarRef.current) {
      const scrollPercentage = scrollbarRef.current.scrollLeft / (scrollbarRef.current.scrollWidth - scrollbarRef.current.clientWidth)
      scrollContainerRef.current.scrollLeft = scrollPercentage * (scrollContainerRef.current.scrollWidth - scrollContainerRef.current.clientWidth)
    }
  }



  const handleWheel = (e: React.WheelEvent<HTMLDivElement>) => {
    e.preventDefault();
    if (scrollContainerRef.current) {
      const { scrollLeft, scrollWidth, clientWidth } = scrollContainerRef.current;
      const maxScroll = scrollWidth - clientWidth;
      const newScrollLeft = Math.max(0, Math.min(maxScroll, scrollLeft + e.deltaY));

      scrollContainerRef.current.scrollLeft = newScrollLeft;
      handleScroll();
    }
  };

  useEffect(() => {
    const preventDefaultScroll = (e: WheelEvent) => {
      if (scrollContainerRef.current?.contains(e.target as Node)) {
        e.preventDefault();
      }
    };

    window.addEventListener('wheel', preventDefaultScroll, { passive: false });

    return () => {
      window.removeEventListener('wheel', preventDefaultScroll);
    };
  }, []);

  // activateTab
  const switchTab = (tab: string) => {
    if (tab === fileName || tab === editingTabName) return;
    
    // Save current editor's view state before switching
    if (editorRef.current) {
      const currentViewState = editorRef.current.saveViewState();
      setFiles(prevFiles => ({
        ...prevFiles,
        [fileName]: {
          ...prevFiles[fileName],
          viewState: currentViewState
        }
      }));
    }
  
    // Switch to new tab
    setFileName(tab);
  
    // Restore view state after switch
    if (editorRef.current && files[tab].viewState) {
      // Use setTimeout to ensure editor is ready
      setTimeout(() => {
        if (editorRef.current) {
          editorRef.current.restoreViewState(files[tab].viewState!);
        }
      }, 0);
    }
  };

  
  // handleTabRename

  

  // setEditingTabID

  const handleTabRename = (oldName: string, newName: string) => {
    // If the new name is the same as the old name
    if (newName === oldName){
      setEditingTabName(null);
      return;
    } else if (tabs.includes(newName) || newName === '') {
      if (newName === '') {
        toast({
          variant: 'destructive',
          title: 'A file name must be provided',
          description: 'Please provide a name for the file',
          action: <ToastAction altText="Rename">Rename</ToastAction> 
        });
      } else {
        toast({
          variant: 'destructive',
          title: `File: "${newName}" already exists`,
          description: 'Please provide a unique name for the file',
          action: <ToastAction altText="Rename">Rename</ToastAction> 
        });
      }
      setTimeout(() => {
        if (newTabInputRef.current) {
          newTabInputRef.current.focus();
          newTabInputRef.current.select();
        }
      }, 0);
      return;
    }

    setFiles((prevFiles) => {
      const newFiles = { ...prevFiles };
      newFiles[newName] = newFiles[oldName];
      delete newFiles[oldName];
      return newFiles;
    });
    setFileName(newName)
    setTabs((prevTabs) => {
      const newTabs = [...prevTabs];
      newTabs[newTabs.indexOf(oldName)] = newName;
      return newTabs;
    });
  }

  // removeTab
  const removeTab = (tab: string, event: React.MouseEvent) => {
    event.stopPropagation();
    // Rest of existing removeTab code...
    const tabIndex = tabs.indexOf(tab);
    const newTabs = tabs.filter((t) => t !== tab);
    setTabs(newTabs);
    setFiles((prevFiles) => {
      const newFiles = { ...prevFiles };
      delete newFiles[tab];
      return newFiles;
    });
    const newIndex = Math.min(tabIndex, newTabs.length - 1);
    setFileName(newTabs[newIndex]);
  }

  const addTab = () => {
    const newTabName = `Untitled-${nextId}`;
    setNextId((prevId) => prevId + 1);
    setTabs((prevTabs) => [...prevTabs, newTabName]);
    setFiles((prevFiles) => ({
      ...prevFiles,
      [newTabName]: {
        id: nextId,
        name: newTabName,
        content: '',
        viewState: null,
      },
    }));
    setEditingTabName(newTabName);
    setFileName(newTabName);

    setTimeout(() => {
      if (newTabInputRef.current) {
        newTabInputRef.current.focus();
        newTabInputRef.current.select();
      }
    }, 0);
  };
  

  return (
    <div 
      className="flex items-center h-9 bg-accent mb-2 relative"
      onMouseEnter={() => setIsHovering(true)}
      onMouseLeave={() => setIsHovering(false)}
    >
      <div
        ref={scrollContainerRef}
        className="flex-1 flex overflow-x-auto items-center relative group scroll-smooth"
        style={{
          scrollbarWidth: 'none',
          msOverflowStyle: 'none',
        }}
        onScroll={handleScroll}
        onWheel={handleWheel}
      >
        {tabs.map((tab) => (
          <div
            key={files[tab].id}
            onClick={() => switchTab(tab)}
            className={`
              flex items-center justify-between min-w-[120px] max-w-[200px] h-8 px-3
              cursor-pointer select-none
              border-t border-r border-l
              ${
                tab === fileName 
                  ? 'bg-card border-ring rounded-lg border'
                  : 'bg-card rounded-lg border border-input'
              }
            `}
          >
            {tab === fileName && tab === editingTabName ? (
                <input 
                ref={newTabInputRef}
                className="flex-1 text-sm text-card-foreground truncate w-[80%] bg-card" 
                defaultValue={tab}
                onBlur={(e) => {
                    handleTabRename(tab, e.target.value);
                }}
                onKeyDown={(e) => {
                    if (e.key === 'Enter') {
                        e.preventDefault();
                        e.stopPropagation();
                        e.currentTarget.blur();
                    }
                }}
            />
            ) : (
                <span 
                    className="text-sm text-card-foreground truncate"
                    onDoubleClick={() => setEditingTabName(tab)}
                >
                    {tab}
                </span>
            )}
            
            <button
              onClick={(e) => removeTab(tab, e)}
              className="ml-2 p-0.5 rounded-sm hover:bg-accent text-card-foreground hover:text-accent-foreground"
            >
              <X size={14} />
            </button>
          </div>
        ))}
      </div>
      {showScrollbar && (
        <div
          ref={scrollbarRef}
          className={`
            absolute bottom-0 left-0 right-8 h-1 bg-transparent
            transition-opacity duration-300 ease-in-out
            ${isHovering ? 'opacity-100' : 'opacity-0'}
          `}
          style={{
            overflowX: 'auto',
            overflowY: 'hidden',
          }}
          onScroll={handleScrollbarScroll}
        >
          <div
            style={{
              width: scrollContainerRef.current?.scrollWidth,
              height: '1px',
            }}
          />
        </div>
      )}
      <button
        onClick={addTab}
        className="flex items-center justify-center w-8 h-8 hover:bg-muted-foreground text-primary hover:text-primary"
      >
        <Plus size={16} />
      </button>
    </div>
  )
}

export default TabBar

