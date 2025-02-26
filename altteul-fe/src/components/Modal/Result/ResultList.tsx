import ResultItem from '@components/Modal/Result/ResultItem';
import { useEffect, useState } from 'react';
import { MemberInfo, ResultData, TeamInfo } from 'types/types';

interface ResultListProps {
  results: ResultData;
}

export type SortedPlayer = {
  duration: string | null;
  executeMemory: string | number | null;
  executeTime: string | number | null;
  gameResult: number | string;
  isFinish: boolean;
  lang: string | null;
  nickname: string;
  passRate: number;
  point: number;
  profileImage: string;
  tierId: number;
  totalHeadCount: number;
  userId: number;
};

const ResultList = ({ results }: ResultListProps) => {
  const [sortedPlayers, setSortedPlayers] = useState([]);

  useEffect(() => {
    if (results.restTeam && results.submittedTeam) {
      const myTeamMembers = results.submittedTeam?.members.map((member: MemberInfo) => ({
        ...member,
        gameResult: results.submittedTeam.gameResult,
        lang: results.submittedTeam.lang,
        totalHeadCount: results.submittedTeam.totalHeadCount,
        executeTime: results.submittedTeam.executeTime,
        executeMemory: results.submittedTeam.executeMemory,
        point: results.submittedTeam.point,
        passRate: results.submittedTeam.passRate,
        duration: results.submittedTeam.duration,
        isFinish: true,
      }));

      const opponentMembers = results.restTeam?.flatMap((opponent: TeamInfo) =>
        opponent.members.map((member: MemberInfo) => ({
          ...member,
          gameResult: opponent.gameResult,
          lang: opponent.lang,
          totalHeadCount: opponent.totalHeadCount,
          executeTime: opponent.executeTime,
          executeMemory: opponent.executeMemory,
          point: opponent.point,
          passRate: opponent.passRate,
          duration: opponent.duration,
          isFinish: false,
        }))
      );

      const sortedPlayers: SortedPlayer[] = [...myTeamMembers, ...opponentMembers].sort((a, b) => {
        if (a.gameResult === 0 && b.gameResult !== 0) return 1;
        if (b.gameResult === 0 && a.gameResult !== 0) return -1;

        const gameResultA = Number(a.gameResult);
        const gameResultB = Number(b.gameResult);

        if (b.gameResult !== a.gameResult) {
          return gameResultA - gameResultB;
        }
      });

      setSortedPlayers(sortedPlayers);
    }
  }, [results]);

  return (
    <div>
      <div className="relative flex text-primary-white w-[62rem] justify-between text-sm pl-8 mt-10">
        <p className="w-10 text-center">순위</p>
        <p className="w-32 text-center">플레이어</p>
        <p className="w-20 text-center text-sm">포인트</p>
        <p className="w-10 text-center">시간</p>
        <p className="w-8 text-center">해결</p>
        <p className="w-10 text-center">통과율</p>
        <p className="w-14 text-center">언어</p>
        <p className="w-16 text-center">실행 속도</p>
        <p className="w-18 text-center">메모리</p>
        <p className="w-20 text-center"></p>
      </div>
      <ul className="relative flex flex-col text-primary-white w-[64rem] justify-between text-sm p-4">
        {sortedPlayers.map((player, index) => (
          <ResultItem key={index} player={player} rank={index + 1} />
        ))}
      </ul>
    </div>
  );
};

export default ResultList;
