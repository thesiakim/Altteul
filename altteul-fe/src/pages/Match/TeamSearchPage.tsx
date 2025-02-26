import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import UserProfile from '@components/Match/UserProfile';
import Button from '@components/Common/Button/Button';
import backgroundImage from '@assets/background/team_matching_bg.svg';
import tmi from '@assets/tmi.json';
import { useMatchStore } from '@stores/matchStore'; // ← Zustand Store
import { useSocketStore } from '@stores/socketStore';
import socketResponseMessage from 'types/socketResponseMessage';
import { cancelTeamMatch } from '@utils/Api/matchApi';
import useGameStore from '@stores/useGameStore';
import { User } from 'types/types';
import { PacmanLoader } from 'react-spinners';

const TeamSearchPage = () => {
  const navigate = useNavigate();
  const socket = useSocketStore();
  const gameStore = useGameStore();

  // Store에서 matchData를 직접 구독 (초기 users를 복사하지 않음)
  const { matchData, setMatchData, setMathId, setMyTeam, setOpponent } = useMatchStore(state => ({
    matchData: state.matchData,
    setMatchData: state.setMatchData,
    setMathId: state.setMathId,
    setMyTeam: state.setMyTeam,
    setOpponent: state.setOpponent,
  }));

  const { roomId, users } = matchData;

  // 재미 요소 TMI(facts)
  const [fact, setFact] = useState<string>('');
  const [facts] = useState<string[]>(tmi.facts);

  useEffect(() => {
    socket.subscribe(`/sub/team/room/${roomId}`, handleMessage);

    return () => {
      const matchId = sessionStorage.getItem('matchId');
      // console.log('TeamSearchPage unmount. Unsubscribe socket.');
      socket.unsubscribe(`/sub/team/room/${roomId}`);
      matchId ? socket.unsubscribe(`/sub/team/room/${matchId}`) : null;
    };
  }, [roomId]);

  // 소켓 메시지 핸들러
  const handleMessage = (message: socketResponseMessage) => {
    // console.log(message);
    const { type, data } = message;

    // 매칭이 잡히면 matchId를 기록하고 새 구독
    if (type === 'MATCHED') {
      setMathId(data.matchId);
      socket.subscribe(`/sub/team/room/${data.matchId}`, handleMessage);
    }

    // COUNTING_READY가 오면 게임 최종 페이지로 이동
    if (type === 'COUNTING_READY') {
      setMyTeam(data.team1);
      setOpponent(data.team2);

      // GameStore에도 저장
      gameStore.setMyTeam(data.team1);
      gameStore.setOpponent(data.team2);
      gameStore.setProblem(data.problem);
      gameStore.setTestcases(data.testcases);

      navigate('/match/team/final');
    }

    // 매칭 취소 성공 시 MATCH_CANCEL_SUCCESS
    if (type === 'MATCH_CANCEL_SUCCESS') {
      // myTeam 재설정
      setMyTeam(data);
      // 다시 팀 구성 페이지로
      navigate('/match/team/composition');
    }
  };

  // 매칭 취소 버튼
  const handleMatchCancelButton = () => {
    cancelTeamMatch(roomId);
  };

  // 최초 & 주기적인 TMI 보여주기
  useEffect(() => {
    setFact(facts[Math.floor(Math.random() * facts.length)]);

    const factRotation = setInterval(() => {
      setFact(facts[Math.floor(Math.random() * facts.length)]);
    }, 5000);

    return () => clearInterval(factRotation);
  }, [facts]);

  return (
    <div
      className="w-full -mt-[3.5rem] bg-cover bg-center"
      style={{ backgroundImage: `url(${backgroundImage})` }}
    >
      <div className="absolute inset-0 bg-black/50"></div>

      <div className="relative min-h-screen w-full flex flex-col items-center justify-center">
        <div className="text-white text-[2rem] mb-8 flex flex-col items-center">
          대전 할 상대를 찾고 있어요. 🧐
          <div className="flex text-[1.2rem] mt-3">
            조금만 기다려 주세요
            <PacmanLoader color="#ffffff" size={12} className="ml-3" />
          </div>
        </div>

        {/* 팀 정보 */}
        <div className="flex justify-center items-center gap-20">
          {users.map((user: User) => (
            <UserProfile
              key={user.userId}
              nickname={user.nickname}
              profileImg={user.profileImg}
              tierId={user.tierId}
              className="w-20 h-20"
            />
          ))}
        </div>

        <div className="flex gap-6 mt-12">
          <Button onClick={handleMatchCancelButton}>매칭 취소하기</Button>
        </div>

        {/* 랜덤 TMI */}
        <div className="absolute bottom-14 text-gray-100 text-[1.1rem]">{fact}</div>
      </div>
    </div>
  );
};

export default TeamSearchPage;
