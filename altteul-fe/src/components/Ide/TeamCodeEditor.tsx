import { useEffect, useMemo, useState, useCallback } from 'react';
import Editor, { OnMount } from '@monaco-editor/react';
import { configureMonaco } from '@utils/monacoConfig';
import Dropdown from '@components/Common/Dropdown';
import * as Y from 'yjs';
import { WebsocketProvider } from 'y-websocket';
import { MonacoBinding } from 'y-monaco';
import * as monaco from 'monaco-editor';

const DEFAULT_CODE = {
  python: 'print("Hello World!")',
  java: 'public class Main {\n  public static void main(String[] args) {\n    System.out.println("Hello World!");\n  }\n}',
};

const languageOptions = [
  { id: 1, value: 'python', label: 'Python' },
  { id: 2, value: 'java', label: 'Java' },
];

interface CodeEditorProps {
  code?: string | null;
  setCode: React.Dispatch<React.SetStateAction<string>>;
  language?: 'python' | 'java';
  setLanguage?: (lang: 'python' | 'java') => void;
  readOnly?: boolean;
  roomId: string;
  onCodeChange?: (code: string) => void;
  myRoomId: string;
  item?: { type: string; duration: number } | null;
  // team:
}

const SOCKET_URL =
  window.location.hostname === 'localhost'
    ? import.meta.env.VITE_SIGNALING_URL_DEV
    : import.meta.env.VITE_SIGNALING_URL_PROD;

const CodeEditor = ({
  code,
  setCode,
  language,
  setLanguage,
  readOnly,
  roomId,
  myRoomId,
  item,
}: CodeEditorProps) => {
  const ydoc = useMemo(() => new Y.Doc(), []);
  const [editor, setEditor] = useState<any | null>(null);
  const [provider, setProvider] = useState<WebsocketProvider | null>(null);
  const [binding, setBinding] = useState<MonacoBinding | null>(null);
  const [isHidden, setIsHidden] = useState(false);

  // WebSocket Provider 설정
  useEffect(() => {
    if (!roomId) {
      console.error('roomId가 설정되지 않았습니다.');
      return;
    }

    const newProvider = new WebsocketProvider(SOCKET_URL, roomId, ydoc); // 상대팀 코드 parameter
    setProvider(newProvider);
    // console.log(editor)
    // console.log(myRoomId);
    return () => {
      newProvider.destroy();
    };
  }, [roomId, editor]);

  useEffect(() => {
    if (item?.type === 'blind') {
      console.log('코드 가리기 아이템 적용');
      setIsHidden(true);

      setTimeout(() => {
        console.log('코드 가리기 아이템 해제');
        setIsHidden(false);
      }, item.duration * 1000);
    }
  }, [item]);

  useEffect(() => {
    if (provider == null || editor == null) {
      console.log('오지마');
      return;
    }
    console.log('어서와');
    const ytext = ydoc.getText('monaco');
    const newBinding = new MonacoBinding(
      ytext,
      editor.getModel(),
      new Set([editor]),
      provider?.awareness
    );
    setBinding(newBinding);
    return () => {
      newBinding.destroy();
    };
  }, [provider, editor]);

  // useEffect(() => {
  //   console.log('item 변화 감지 후 효과 적용');
  //   deleteRandomLine();
  // }, [item]);

  // const deleteRandomLine = useCallback(() => {
  //   if (!editor) return;

  //   const model = editor.getModel();
  //   if (!model) return;
  //   console.log('모델 있음' + model);
  //   const lineCount = model.getLineCount();
  //   if (lineCount <= 1) return; // 한 줄만 있d을 경우 삭제하지 않음
  //   console.log('라인 개수' + lineCount);
  //   const randomLineNumber = Math.floor(Math.random() * lineCount) + 1;
  //   console.log('랜덤 추출 라인 개수' + randomLineNumber);
  //   // Monaco의 Range 객체 생성
  //   const range = new monaco.Range(
  //     randomLineNumber,
  //     1,
  //     randomLineNumber,
  //     model.getLineMaxColumn(randomLineNumber)
  //   );
  //   console.log('효과 범위' + range.toString());
  //   // 에디터에 삭제 명령 실행
  //   editor.executeEdits('deleteRandomLine', [
  //     {
  //       range: range,
  //       text: null,
  //       forceMoveMarkers: true,
  //     },
  //   ]);
  // }, [editor]);

  // 에디터 마운트 시 설정
  const handleEditorMount: OnMount = useCallback((editorInstance, monaco) => {
    configureMonaco();
    setCode(DEFAULT_CODE[language]);

    setEditor(editorInstance);
    editorInstance.focus();

    monaco.editor.defineTheme('custom-dark', {
      base: 'vs-dark',
      inherit: true,
      rules: [],
      colors: {
        'editor.background': '#242A32',
      },
    });

    monaco.languages.register({ id: 'java' });
    monaco.languages.setLanguageConfiguration('java', {
      autoClosingPairs: [
        { open: '{', close: '}' },
        { open: '[', close: ']' },
        { open: '(', close: ')' },
        { open: '"', close: '"' },
        { open: "'", close: "'" },
      ],
    });

    monaco.editor.setTheme('custom-dark');
  }, []);

  return (
    <div className="flex flex-col border-b border-gray-04 items-end">
      {setLanguage && (
        <Dropdown
          options={languageOptions}
          value={language}
          onChange={newLang => setLanguage(newLang as typeof language)}
          width="10rem"
          height="3.7rem"
          className="bg-gray-06 border-0 text-sm"
          optionCustomName="bg-gray-05 border-0"
          borderColor="border-gray-06"
          fontSize="text-sm"
        />
      )}
      {isHidden && (
        <div
          className="absolute inset-0 bg-black opacity-70 flex items-center justify-center text-white font-bold text-xl z-50"
          style={{ pointerEvents: 'none' }}
        >
          5초 동안 아무것도 못하세요. 🤭🙏
        </div>
      )}
      <Editor
        height="55vh"
        language={language}
        theme="custom-dark"
        options={{
          minimap: { enabled: false },
          fontSize: 14,
          automaticLayout: true,
          scrollBeyondLastLine: false,
          scrollbar: {
            vertical: 'auto',
            horizontal: 'auto',
          },
          readOnly: readOnly ? true : false,
          roundedSelection: true,
          quickSuggestions: true,
        }}
        loading="에디터를 불러오는 중입니다..."
        onChange={value => setCode(value || '')}
        onMount={handleEditorMount}
      />
    </div>
  );
};

export default CodeEditor;
