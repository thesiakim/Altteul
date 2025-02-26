import React, { useEffect, useState, useRef } from 'react';
import MonacoEditor, { loader } from '@monaco-editor/react';
import { configureMonaco } from '@utils/monacoConfig';
// 1) txt 파일 임포트
import mcWar from '@assets/solved/MC 전쟁.txt';
import gridCity from '@assets/solved/격자 도시.txt?raw';
import headMeeting from '@assets/solved/머리맞대기.txt?raw';
import warehouseRobot from '@assets/solved/물류 창고 로봇.txt?raw';
import busTransfer from '@assets/solved/버스 환승.txt?raw';

// 2) 여러 txt 파일 내용을 배열로
const codeSnippets = [
  mcWar,
  gridCity,
  headMeeting,
  warehouseRobot,
  busTransfer,
];

const AnimatedCodeEditor = (): JSX.Element => {
  const [typedCode, setTypedCode] = useState('');
  const [codeSnippet, setCodeSnippet] = useState(''); // 무작위로 선택될 코드
  const indexRef = useRef<number>(0);

  // Monaco 설정 (언어 등록 등)
  useEffect(() => {
    configureMonaco(); 
  }, []);

  // 마운트 시점에 codeSnippets에서 랜덤으로 하나를 선택
  useEffect(() => {
    const randomIndex = Math.floor(Math.random() * codeSnippets.length);
    setCodeSnippet(codeSnippets[randomIndex]);
  }, []);

  // codeSnippet이 정해지면, 한 글자씩 타이핑
  useEffect(() => {
    // codeSnippet이 아직 없으면 진행 X
    if (!codeSnippet) return;

    // 타이핑 로직 초기화
    setTypedCode('');
    indexRef.current = 0;

    const interval = setInterval(() => {
      if (indexRef.current < codeSnippet.length) {
        setTypedCode(prev => prev + codeSnippet[indexRef.current]);
        indexRef.current += 1;
      } else {
        clearInterval(interval);
      }
    }, 30);

    return () => clearInterval(interval);
  }, [codeSnippet]);

  return (
    <div className="relative w-full h-[calc(100vh-3.5rem)] overflow-hidden">
      <div className="absolute inset-0 z-10 bg-gradient-to-r from-transparent via-primary-black/80 to-primary-black" />

      <MonacoEditor
        height="100%"
        /* 필요하다면 java, python 등 원하는 언어로 변경 가능 */
        defaultLanguage="python"
        theme="vs-dark"
        value={typedCode}
        options={{
          readOnly: true,
          minimap: { enabled: false },
          fontSize: 16,
          wordWrap: 'on',
          scrollbar: { vertical: 'hidden', horizontal: 'hidden' },
          scrollBeyondLastLine: false,
          automaticLayout: true,
        }}
      />
    </div>
  );
};

export default AnimatedCodeEditor;
