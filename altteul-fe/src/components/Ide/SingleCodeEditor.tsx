import { useEffect } from 'react';
import Editor from '@monaco-editor/react';
import { configureMonaco } from '@utils/monacoConfig';
import Dropdown from '@components/Common/Dropdown';

const DEFAULT_CODE = {
  python: 'print("Hello World!")',
  java: 'public class Main {\n  public static void main(String[] args) {\n    System.out.println("Hello World!");\n  }\n}',
};

interface CodeEditorProps {
  code: string;
  setCode: (code: string) => void;
  language: 'python' | 'java';
  setLanguage?: (lang: 'python' | 'java') => void;
  readOnly?: boolean;
  roomId?: string;
}

const CodeEditor = ({ code, setCode, language, setLanguage, readOnly }: CodeEditorProps) => {
  useEffect(() => {
    configureMonaco();
    setCode(DEFAULT_CODE[language]);
  }, [language]);

  const languageOptions = [
    { id: 1, value: 'python', label: 'Python' },
    { id: 2, value: 'java', label: 'Java' },
  ];

  return (
    <div
      className={`relative flex flex-col border-b border-gray-04 items-end ${readOnly ? 'mt-[2.35rem]' : ''} mb-8`}
    >
      {/* 언어 선택 드롭다운 */}
      {readOnly ? (
        ''
      ) : (
        <Dropdown
          options={languageOptions}
          value={language}
          onChange={newLang => {
            setLanguage(newLang as typeof language);
          }}
          width="10rem"
          height="3.7rem"
          className="bg-gray-06 border-0 text-sm"
          optionCustomName="bg-gray-05 border-0"
          borderColor="border-gray-06"
          fontSize="text-sm"
        />
      )}
      <Editor
        height="30rem"
        language={language}
        value={code}
        theme="vs-dark"
        options={{
          minimap: { enabled: false },
          fontSize: 14,
          scrollbar: {
            vertical: 'auto',
            horizontal: 'hidden',
            useShadows: false,
            verticalHasArrows: false,
            horizontalHasArrows: false,
            alwaysConsumeMouseWheel: false,
          },
          overviewRulerBorder: false,
          overviewRulerLanes: 0,
          hideCursorInOverviewRuler: true,
          readOnly: readOnly ? true : false,
        }}
        loading="에디터를 불러오는 중입니다."
        onChange={value => setCode(value || '')}
        beforeMount={monaco => {
          monaco.editor.defineTheme('custom-dark', {
            base: 'vs-dark',
            inherit: false,
            colors: { 'editor.background': '#242A32' },
            rules: [],
          });

          // Java 언어 설정 추가
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
        }}
      />
    </div>
  );
};

export default CodeEditor;
