import { useState } from 'react';
import useGameStore from '@stores/useGameStore';
import SmallButton from '@components/Common/Button/SmallButton ';
import { api } from '@utils/Api/commonApi';
import useAuthStore from '@stores/authStore';
import { useSocketStore } from '@stores/socketStore';

interface IdeFooterProps {
  code: string;
  language: 'python' | 'java';
  setOutput: (output: string) => void;
  userRoomId: number;
}

const convertLangToServerFormat = (language: 'python' | 'java'): 'PY' | 'JV' => {
  return language === 'python' ? 'PY' : 'JV';
};

const IdeFooter = ({ code, language, setOutput, userRoomId }: IdeFooterProps) => {
  const { gameId, roomId, problem } = useGameStore();
  const { token } = useAuthStore();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { sendMessage } = useSocketStore();

  /** ✅ 코드 실행 (API 요청) */
  const executeCode = async () => {
    try {
      const serverLang = convertLangToServerFormat(language);
      const response = await api.post(
        '/judge/execution',
        {
          gameId,
          teamId: userRoomId,
          problemId: problem.problemId,
          lang: serverLang,
          code,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      // console.log(response);

      const data = response?.data.data;
      // console.log(data);

      if (response?.data.status === 200) {
        if (data.isNotCompileError) {
          setOutput(`컴파일 에러가 발생했습니다. ${data.message}`);
        } else if (!data.testCases) {
          setOutput(data?.message);
        } else {
          const results = data?.testCases
            .map(
              (test: { testCaseNumber: number; status: string; output: string; answer: string }) =>
                `테스트 케이스 ${test.testCaseNumber}: ${test.status}\n출력: ${test.output}\n정답: ${test.answer}`
            )
            .join('\n\n');

          setOutput(`✅ 코드 실행 결과:\n${results}`);
        }
      } else {
        setOutput(`⚠️ 오류 발생: ${data.message}`);
      }
    } catch (error) {
      console.error('코드 실행 중 오류 발생:', error);
      setOutput('🚨 코드 실행 중 오류가 발생했습니다.');
    }
  };

  /** ✅ 코드 제출 (WebSocket 요청) */
  const handleSubmitCode = () => {
    const serverLang = convertLangToServerFormat(language);
    setIsSubmitting(true);

    sendMessage(`/pub/judge/submition`, {
      gameId: gameId,
      teamId: userRoomId,
      problemId: problem.problemId,
      lang: serverLang,
      code: code,
    });

    console.log('코드 제출');
  };

  return (
    <div className="flex justify-end items-center p-2 gap-2 bg-primary-black border-t border-gray-04">
      <SmallButton onClick={executeCode} children="코드 실행" backgroundColor="gray-04" />
      <SmallButton onClick={handleSubmitCode} children="코드 제출" type="submit" />
    </div>
  );
};

export default IdeFooter;
