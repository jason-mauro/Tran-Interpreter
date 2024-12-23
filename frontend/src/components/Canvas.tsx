import React, { useState, useRef, ChangeEvent } from 'react';

const Canvas = () => {
    const [text, setText] = useState('');
    const [cursorLine, setCursorLine] = useState(1);
    const [cursorColumn, setCursorColumn] = useState(1);
    const [characterLength, setCharacterLength] = useState(0);
    const textareaRef = useRef(null);
    const TAB_SPACES = 4;

    const getCursorPosition = (textarea: HTMLTextAreaElement) => {
        const cursorPos = textarea.selectionStart;
        const textValue = textarea.value;
        
        const textBeforeCursor = textValue.substring(0, cursorPos);
        
        const lineNumber = (textBeforeCursor.match(/\n/g) || []).length + 1;
        
        const lastNewlineIndex = textBeforeCursor.lastIndexOf('\n');
        const columnNumber = lastNewlineIndex === -1 
            ? cursorPos + 1
            : cursorPos - lastNewlineIndex;

        return { line: lineNumber, column: columnNumber };
    };

    const handleStatus = (e: React.SyntheticEvent<HTMLTextAreaElement>) => {
        const target = e.target as HTMLTextAreaElement;
        const { line, column } = getCursorPosition(target);
        setCharacterLength(text.length);
        setCursorLine(line);
        setCursorColumn(column);
    };

    const handleChange = (e: ChangeEvent<HTMLTextAreaElement>) => {
        setText(e.target.value);
        handleStatus(e);
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
        if (e.key === 'Tab') {
            e.preventDefault();
            const target = e.target as HTMLTextAreaElement;
            const start = target.selectionStart;
            const end = target.selectionEnd;
            
            if (start !== end) {
                const selectedText = text.slice(start, end);
                const lines = selectedText.split('\n');
                const spaces = ' '.repeat(TAB_SPACES);
                
                const newText = lines.map(line => spaces + line).join('\n');
                const beforeSelection = text.slice(0, start);
                const afterSelection = text.slice(end);
                
                setText(beforeSelection + newText + afterSelection);
                
                target.setSelectionRange(
                    start + TAB_SPACES,
                    end + (TAB_SPACES * lines.length)
                );
            } else {
                const spaces = ' '.repeat(TAB_SPACES);
                const newText = text.substring(0, start) + spaces + text.substring(end);
                setText(newText);
                
                setTimeout(() => {
                    target.setSelectionRange(start + TAB_SPACES, start + TAB_SPACES);
                }, 0);
            }
            handleStatus(e);
        }        
    };

    const getLineNumbers = () => {
            const lines = text.split('\n');
            return lines.length > 0 ? lines.map((_, index) => (
                <div key={index} className="font-mono">
                    {index + 1}
                </div>
            )) : [<div key="1" className="font-mono">1</div>];
    };

    return (
        <div className="w-full h-screen mx-auto border rounded-lg shadow-lg bg-gray-900 text-white">
            {/* Editor Area */}
            <div className="flex">
                <div className="p-4 text-right bg-gray-800 text-gray-500 select-none min-w-[4rem] font-mono">
                        {getLineNumbers()}
                </div>
                {/* Text Area Container */}
                <div className="w-full relative">
                    <textarea
                        ref={textareaRef}
                        value={text}
                        onChange={handleChange}
                        onKeyDown={handleKeyDown}
                        onKeyUp={handleStatus}
                        onClick={handleStatus}
                        onMouseUp={handleStatus}
                        onSelect={handleStatus}
                        className="w-full p-4 bg-gray-900 text-gray-100 font-mono resize-none outline-none h-full"
                        style={{
                            lineHeight: '1.5rem',
                            whiteSpace: 'pre',
                            overflow: 'auto',
                            overflowY: 'visible',
                            minHeight: '15rem',
                            scrollbarWidth: 'thin',
                            scrollbarColor: '#4B5563 transparent'
                        }}
                        spellCheck="false"
                        placeholder="Start typing..."
                        autoFocus
                        wrap="off"
                    />
                </div>
            </div>
            {/* Footer */}
            <div className="flex justify-between p-2 border-t border-gray-700 text-sm text-gray-400">
                <span>Ln {cursorLine}, Col {cursorColumn} | {characterLength} characters</span>
            </div>
        </div>
    );
};

export default Canvas;