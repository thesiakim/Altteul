import { CodeExecutionState } from 'types/types';
import { create } from 'zustand';

const useCodeExecutionStore = create<CodeExecutionState>((set, get) => ({
  code: '',
  language: 'python',
  output: [],

  setCode: (code) => set({ code }),
  setLanguage: (language) => set({ language }),

  executeCode: () => {
    const { code, language } = get();
    const newOutput = [...get().output];

    try {
      newOutput.push('코드를 실행하고 있어요. ✍️');

      if (language === 'python') {
        const logs: string[] = [];
        const originalLog = console.log;

        console.log = (...args) => {
          logs.push(args.map((arg) => (typeof arg === 'object' ? JSON.stringify(arg) : arg)).join(' '));
        };

        const execCode = `(async () => { ${code} })()`;
        eval(execCode);

        console.log = originalLog;
        newOutput.push(...logs, '코드 실행이 완료되었어요. 👌');
      } else {
        throw new Error('Java execution requires a backend server');
      }
    } catch (error) {
      newOutput.push(`Error: ${error instanceof Error ? error.message : '알 수 없는 에러가 발생했어요. 🤯'}`);
    }

    set({ output: newOutput });
  },

  clearOutput: () => set({ output: [] }),
}));

export default useCodeExecutionStore;
