import { loader } from '@monaco-editor/react';

export const configureMonaco = async () => {
  await loader.init();
  const monaco = loader.__getMonacoInstance();

  // Python 언어 설정
  monaco.languages.register({ id: 'python' });
  monaco.languages.setMonarchTokensProvider('python', {
    keywords: ['def', 'class', 'import', 'from', 'as', 'for', 'in', 'while', 'print', 'Altteul'],
    tokenizer: {
      root: [
        [
          /[a-zA-Z_]\w*/,
          {
            cases: {
              '@keywords': 'keyword',
              '@default': 'identifier',
            },
          },
        ],
      ],
    },
  });

  // Python 자동 완성 설정
  monaco.languages.registerCompletionItemProvider('python', {
    triggerCharacters: ['.', '('],
    provideCompletionItems: (model, position) => {
      const word = model.getWordUntilPosition(position);
      const range = {
        startLineNumber: position.lineNumber,
        endLineNumber: position.lineNumber,
        startColumn: word.startColumn,
        endColumn: word.endColumn,
      };

      return {
        suggestions: [
          {
            label: 'print',
            kind: monaco.languages.CompletionItemKind.Function,
            insertText: 'print(${1:})',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            range: range,
          },
          {
            label: 'def',
            kind: monaco.languages.CompletionItemKind.Keyword,
            insertText: 'def ${1:function}(${2:}):\n\t\n\t${3:return}',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            range: range,
          },
          {
            label: 'def',
            kind: monaco.languages.CompletionItemKind.Keyword,
            insertText: 'def ${1:function}(${2:}):\n\t\n\t${3:return}',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            range: range,
          },
        ],
      };
    },
  });

  // Java 언어 설정
  monaco.languages.register({ id: 'java' });
  monaco.languages.setLanguageConfiguration('java', {
    comments: {
      lineComment: '//',
      blockComment: ['/*', '*/'],
    },
  });

  // Java 자동 완성 설정
  monaco.languages.registerCompletionItemProvider('java', {
    triggerCharacters: ['.', '('],
    provideCompletionItems: (model, position) => {
      const word = model.getWordUntilPosition(position);
      const range = {
        startLineNumber: position.lineNumber,
        endLineNumber: position.lineNumber,
        startColumn: word.startColumn,
        endColumn: word.endColumn,
      };

      return {
        suggestions: [
          {
            label: 'psvm',
            kind: monaco.languages.CompletionItemKind.Snippet,
            insertText: [
              'public static void main(String[] args) {',
              '\t${1:System.out.println("Hello World!");}',
              '}',
            ].join('\n'),
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            range: range,
          },
          {
            label: 'System.out.println',
            kind: monaco.languages.CompletionItemKind.Method,
            insertText: 'System.out.println(${1:})',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            range: range,
          },
        ],
      };
    },
  });
};
