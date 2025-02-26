import useGameStore from '@stores/useGameStore';
import { ReactNode } from 'react';
import parse, { DOMNode, domToReact } from 'html-react-parser';
import { useLocation } from 'react-router-dom';

const ProblemInfo = () => {
  const { problem, testcases } = useGameStore();
  const location = useLocation();
  const isTeam = location.pathname.includes('/game/team');

  if (!problem || !testcases) {
    return null;
  }

  const options = {
    replace: (domNode: DOMNode, index: number) => {
      if (
        'type' in domNode &&
        domNode.type === 'tag' &&
        'name' in domNode &&
        domNode.name === 'pre'
      ) {
        return (
          <>
            <h3
              key={`h3-${index}`}
              style={{
                marginTop: '2rem',
              }}
            >
              {domToReact(domNode.children as DOMNode[])}
            </h3>
            <p
              key={`p-${index}`}
              style={{
                marginTop: '2rem',
                marginBottom: '1rem',
              }}
            >
              {domToReact(domNode.children as DOMNode[])}
            </p>
            <img
              key={`img-${index}`}
              style={{
                marginBottom: '1rem',
              }}
            />
            <pre
              key={`pre-${index}`}
              style={{
                marginTop: '2rem',
                backgroundColor: '#292F37',
                padding: '1rem',
                maxWidth: '100%',
                textWrap: 'pretty',
              }}
            >
              {domToReact(domNode.children as DOMNode[])}
            </pre>
          </>
        );
      }
    },
  };

  return (
    <div className={`${isTeam ? 'h-[50rem]' : ''} overflow-auto mb-12`}>
      <h2 className="text-lg font-semibold border-b border-gray-04 p-4">
        {problem.problemId}. {problem.problemTitle}
      </h2>
      <div className="max-h-[30rem] min-h-[20rem] overflow-y-auto p-4 border-b border-gray-04">
        <p className="mb-3 text-sm font-semibold text-gray-02">문제 설명 </p>
        {/* <p className="text-md font-regular">{problem.description}</p> */}
        <div className="text-md font-regular">
          {parse(problem.description, options) as ReactNode}
        </div>
      </div>

      <div className="p-4">
        {testcases.map((testcase, index) => (
          <div key={testcase.testcaseId} className="rounded flex gap-2 mb-8">
            <div className="basis-1/2 max-w-1/2x">
              <p className="mb-2 text-sm font-semibold text-gray-02">입력 예시 #{index + 1}</p>
              <div className="min-h-40 bg-gray-04 rounded-xs p-3 font-firacode text-sm">
                {testcase.input}
              </div>
            </div>
            <div className="basis-1/2 max-w-1/2">
              <p className="mb-2 text-sm font-semibold text-gray-02">출력 예시 #{index + 1}</p>
              <div className="min-h-40 bg-gray-04 rounded-xs p-3 font-firacode text-sm">
                {testcase.output}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ProblemInfo;
