interface TerminalProps {
  output: string;
  isTeam: boolean;
}

const Terminal = ({ output, isTeam }: TerminalProps) => {
  return (
    <div
      className={`h-[25vh] bg-gray-04 text-gray-02 p-3 overflow-auto ${isTeam && 'ml-[4rem] mr-[1rem]'}`}
    >
      <pre className="whitespace-pre-wrap text-sm text-gray-01">{output}</pre>
    </div>
  );
};

export default Terminal;
