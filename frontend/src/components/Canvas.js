import React, { useState, useRef } from 'react';

const Canvas = () => {
  const [text, setText] = useState('');
  const [cursorLine, setCursorLine] = useState(1);
  const [cursorColumn, setCursorColumn] = useState(1);
  const textareaRef = useRef(null);

  const handleCursorPosition = (e) => {
    const cursorPos = e.target.selectionStart; // Get the cursor position in the text
    const textBeforeCursor = text.slice(0, cursorPos); // Get text before the cursor
    const lines = textBeforeCursor.split('\n'); // Split the text into lines

    const currentLine = lines.length; // Line number (1-based)
    const currentColumn = lines[lines.length - 1].length + 1; // Column in the current line (1-based)

    
    setCursorLine(text.slice(0, e.target.selectionStart).split('\n').length);
    setCursorColumn(currentColumn);
  };

  const handleChange = (e) => {
    setText(e.target.value);
    handleCursorPosition(e); // Update cursor position after text change
  };

  const handleKeyUp = (e) => {
    handleCursorPosition(e); // Update cursor position on keyup
  };


  return (
    <div className="w-full max-w-4xl border rounded-lg shadow-lg bg-gray-900 text-white">
    
      {/* Editor Area */}
      <div className="flex">
        <div className="p-4 text-right bg-gray-800 text-gray-500 select-none min-w-[4rem] font-mono">
          
        </div>
        {/* Text Area */}
        <div className="w-full relative">
          <textarea
            ref={textareaRef}
            value={text}
            onChange={handleChange}
            onKeyUp={handleKeyUp}
            onClick={handleKeyUp}
            className="w-full p-4 bg-gray-900 text-gray-100 font-mono resize-none outline-none h-full"
            style={{
              lineHeight: '1.5rem',
              whiteSpace: 'pre-wrap',
              wordWrap: 'break-word',
              overflowWrap: 'break-word',
              minHeight: '15rem'
            }}
            spellCheck="false"
            placeholder="Start typing..."
          />
        </div>
      </div>
      
      {/* Status Bar */}
      <div className="flex justify-between p-2 border-t border-gray-700 text-sm text-gray-400">
        <span>Ln {cursorLine}, Col {cursorColumn} | {text.length} characters</span>
      </div>
    </div>
  );
};

export default Canvas;
