"use client"

import React, { useState, useRef, useEffect } from 'react'
import { X, Plus } from 'lucide-react'


interface File {
    name: string;
    value: string;
  }

interface Tab {
  id: number
  title: string
  active: boolean
}

interface TabProps {
    setFiles: React.Dispatch<React.SetStateAction<Record<string, File>>>
    setFileName: React.Dispatch<React.SetStateAction<string>>
}

const TabBar: React.FC<TabProps> = ({setFiles, setFileName}) => {
  const [tabs, setTabs] = useState<Tab[]>([
    { id: 1, title: 'demo.tran', active: true },
    { id: 2, title: 'demo2.tran', active: false },
  ])
  const [nextId, setNextId] = useState(3)
  const [showScrollbar, setShowScrollbar] = useState(false)
  const [isHovering, setIsHovering] = useState(false)
  const scrollContainerRef = useRef<HTMLDivElement>(null)
  const scrollbarRef = useRef<HTMLDivElement>(null)
  const [editingTabID, setEditingTabID] = useState<number | null>(null)


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
  }, [tabs])

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

  const addTab = () => {
    const newTab = {
      id: nextId,
      title: `untitled-${nextId}.tran`,
      active: false,
    }
    setTabs([...tabs.map((tab) => ({ ...tab, active: false })), { ...newTab, active: true }])
    setEditingTabID(newTab.id)
    setNextId(nextId + 1)
  }

  const removeTab = (tabId: number, event: React.MouseEvent) => {
    event.stopPropagation()
    const tabIndex = tabs.findIndex((tab) => tab.id === tabId)
    const newTabs = tabs.filter((tab) => tab.id !== tabId)

    if (tabs[tabIndex].active && newTabs.length > 0) {
      const newActiveIndex = Math.min(tabIndex, newTabs.length - 1)
      newTabs[newActiveIndex].active = true
    }

    setTabs(newTabs)
  }

  const activateTab = (tabId: number) => {
    setTabs(
      tabs.map((tab) => ({
        ...tab,
        active: tab.id === tabId,
      }))
    )
    setFileName(tabs.find((tab) => tab.id === tabId)?.title || "")
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

  const handleTabRename = (tabId: number, newTitle: string) => {
  newTitle ? setTabs(
    tabs.map((tab) =>
      tab.id === tabId ? { ...tab, title: newTitle } : tab
    )
  ) : setEditingTabID(null);
  setEditingTabID(null);
}


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
            key={tab.id}
            onClick={() => activateTab(tab.id)}
            className={`
              flex items-center justify-between min-w-[120px] max-w-[200px] h-8 px-3
              cursor-pointer select-none
              border-t border-r border-l
              ${
                tab.active
                  ? 'bg-card border-ring rounded-lg border'
                  : 'bg-card rounded-lg border border-input'
              }
            `}
          >
            {tab.active && tab.id === editingTabID ? (
                <input 
                className="flex-1 text-sm text-card-foreground truncate w-[80%]" 
                defaultValue={tab.title}
                onBlur={(e) => {
                    handleTabRename(tab.id, e.target.value);
                }}
                onKeyDown={(e) => {
                    if (e.key === 'Enter') {
                        e.currentTarget.blur();
                    }
                }}
                autoFocus
            />
            ) : (
                <span 
                    className="text-sm text-card-foreground truncate"
                    onDoubleClick={() => setEditingTabID(tab.id)}
                >
                    {tab.title}
                </span>
            )}
            
            <button
              onClick={(e) => removeTab(tab.id, e)}
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

