import React, { useState, useRef, useEffect } from 'react';

const TextEditor = () => {
  const [text, setText] = useState('');
  const [cursorPosition, setCursorPosition] = useState(0);
  const [currentLine, setCurrentLine] = useState(1);
  const [lineMetrics, setLineMetrics] = useState([]);
  const textareaRef = useRef(null);
  const editorRef = useRef(null);

  const calculateCurrentLine = (position) => {
    const lines = text.slice(0, position).split('\n');
    return lines.length == 1 ? 1 : lines.length - 1; // The number of lines before the cursor is the current line number
  };


  const handleTextChange = (e) => {
    setText(e.target.value);
    setCursorPosition(e.target.selectionStart);
    setCurrentLine(calculateCurrentLine(e.target.selectionStart));
  };

  const handleTab = (e) => {
    if (e.key === 'Tab') {
      e.preventDefault();
      const newText = text.substring(0, cursorPosition) + '    ' + text.substring(cursorPosition);
      setText(newText);
      setCursorPosition(cursorPosition + 4);
    }
  };

  useEffect(() => {
    console.log(text.length)
    const calculateLineMetrics = () => {
      if (!textareaRef.current || !editorRef.current) return;
  
      const lines = text.split('\n');
      const containerWidth = editorRef.current.clientWidth - 100; // Account for padding and line numbers
      const testDiv = document.createElement('div');
      testDiv.style.font = window.getComputedStyle(textareaRef.current).font;
      testDiv.style.whiteSpace = 'pre';
      testDiv.style.position = 'absolute';
      testDiv.style.visibility = 'hidden';
      document.body.appendChild(testDiv);
  
      const metrics = lines.map(line => {
        testDiv.textContent = line;
        const width = testDiv.offsetWidth;
        const wrappedLines = Math.ceil(width / containerWidth);
        return wrappedLines;
      });
  
      document.body.removeChild(testDiv);
      setLineMetrics(metrics);
    };
  
    calculateLineMetrics();
  }, [text]);

  const renderLineNumbers = () => {
    const lineNumbers = [];
    let currentLine = 1;

    lineMetrics.forEach((wrappedLines, index) => {
      // Add the actual line number
      lineNumbers.push(
        <div key={`line-${currentLine}-0`} className="h-6">
          {currentLine}
        </div>
      );
      
      // Add empty spaces for wrapped lines
      for (let i = 1; i < wrappedLines; i++) {
        lineNumbers.push(
          <div key={`line-${currentLine}-${i}`} className="h-6">
            &nbsp;
          </div>
        );
      }
      currentLine++;
    });

    return lineNumbers;
  };

  return (
    <div className="w-full max-w-4xl border rounded-lg shadow-lg bg-gray-900 text-white" ref={editorRef}>
      {/* Toolbar */}
      <div className="flex items-center p-2 border-b border-gray-700">
        <div className="flex space-x-2">
          <div className="w-3 h-3 rounded-full bg-red-500"></div>
          <div className="w-3 h-3 rounded-full bg-yellow-500"></div>
          <div className="w-3 h-3 rounded-full bg-green-500"></div>
        </div>
      </div>
      
      {/* Editor Area */}
      <div className="flex">
        {/* Line Numbers */}
        <div className="p-4 text-right bg-gray-800 text-gray-500 select-none min-w-[4rem] font-mono">
          {renderLineNumbers()}
        </div>
        
        {/* Text Area */}
        <div className="w-full relative">
          <textarea
            ref={textareaRef}
            value={text}
            onChange={handleTextChange}
            onKeyDown={handleTab}
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
        <span>Ln {currentLine}, Col {cursorPosition} | {text.length} characters</span>
        <span>Lines: {text.split('\n').length}</span>
        <span>Position: {cursorPosition}</span>
      </div>
    </div>
  );
};

export default TextEditor;
