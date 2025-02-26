import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import UserProfile from '@components/Match/UserProfile';
import backgroundImage from '@assets/background/team_matching_bg.svg';
import { User } from 'types/types';
import { useMatchStore } from '@stores/matchStore';
import { useSocketStore } from '@stores/socketStore';
import socketResponseMessage from 'types/socketResponseMessage';
import useGameStore from '@stores/useGameStore';
import useAuthStore from '@stores/authStore';
import { toast } from 'react-toastify';

const TeamFinalPage = () => {
  const navigate = useNavigate();
  const matchStore = useMatchStore();
  const gameStore = useGameStore();
  const socket = useSocketStore();
  const { userId } = useAuthStore();
  const matchId = matchStore.matchId;
  const alliance = matchStore.myTeam.users;
  const opponent = matchStore.opponent.users;
  const [problemTitle] = useState(gameStore.problem.problemTitle);
  const [displayText, setDisplayText] = useState(''); //타이핑 효과로 나타나는 텍스트 변수
  const [textIndex, setTextIndex] = useState(0); //타이핑 효과 추적 변수
  const [seconds, setSeconds] = useState<number>(10); //응답 데이터로 렌더링 전 초기값(10) 설정

  //구독처리
  useEffect(() => {
    socket.subscribe(`/sub/team/room/${matchId}`, handleMessage);

    //언마운트 시 구독에 대한 콜백함수(handleMessage 정리)
    return () => {
      // console.log('teamFinal Out, 콜백함수 정리');
      socket.unsubscribe(`/sub/team/room/${matchId}`);
    };
  }, [matchId]);

  //소켓 응답 처리
  const handleMessage = (message: socketResponseMessage) => {
    // console.log(message);
    const { type, data } = message;

    //카운트 응답
    if (type === 'COUNTING') {
      setSeconds(data.time);
    }

    if (type === 'COUNTING_CANCEL') {
      toast.error('유저 이탈로 메인페이지로 이동합니다.', {
        position: 'top-center',
        autoClose: 3000,
        onClose: () => navigate('/match/select'),
      });
    }

    if (type === 'GAME_START') {
      const isUserInTeam1 = data.team1.users.some(user => user.userId === Number(userId));

      if (isUserInTeam1) {
        gameStore.setMyTeam(data.team1);
        gameStore.setOpponent(data.team2);
      } else {
        gameStore.setMyTeam(data.team2);
        gameStore.setOpponent(data.team1);
      }
      //IDE에서 쓸 데이터 setting(소켓 응답데이터 전부)
      gameStore.setGameId(data.gameId);
      gameStore.setMatchId(matchId);

      //IDE 이동 시 match에서 쓰는 데이터 삭제(필요 없음)
      matchStore.clear();

      //페이지 이동
      setTimeout(() => {
        // console.log('IDE 페이지 이동');
        navigate(`/game/team/${data.gameId}/${matchId}`);
      }, 100); // 데이터 저장 후 안전하게 페이지 이동
    }
  };

  // 타이핑 효과 로직
  useEffect(() => {
    if (textIndex < problemTitle.length) {
      //현재 타이핑 된 것보다 문제 제목이 더 길 때
      const typingTimer = setTimeout(() => {
        setDisplayText(prev => prev + problemTitle[textIndex]);
        setTextIndex(prev => prev + 1);
      }, 150); // 각 글자가 타이핑되는 속도 (밀리초)

      return () => clearTimeout(typingTimer);
    }
  }, [textIndex, problemTitle]);

  return (
    <div
      className="relative -mt-[3.5rem] min-h-screen w-full bg-cover bg-center"
      style={{ backgroundImage: `url(${backgroundImage})` }}
    >
      {/* 배경 오버레이 */}
      <div className="absolute inset-0 bg-black/50"></div>

      {/* 컨텐츠 */}
      <div className="relative min-h-screen w-full z-10 flex flex-col items-center justify-center">
        {/* 문제정보(제목) */}
        <div className="text-white text-6xl font-bold mb-8 flex items-center">
          {displayText}
          {/* 커서 효과 부분(텍스트 모두 입력시 사라짐) */}
          {textIndex < problemTitle.length && <span className="animate-pulse">|</span>}
        </div>

        {/* Message */}
        <div className="text-white text-4xl mb-2 flex flex-col items-center">
          대전이 시작됩니다!
        </div>

        {/* 타이머 */}
        <div className="text-white text-3xl mb-8">{seconds}</div>

        {/* 유저 정보 */}
        <div className="flex justify-center items-center">
          {/* 아군 유저 */}
          <div className="flex gap-20 animate-slide-left">
            {alliance.map((user: User) => (
              <UserProfile
                key={user.userId}
                nickname={user.nickname}
                profileImg={user.profileImg}
                tierId={user.tierId}
                className="w-24 h-24"
              />
            ))}
          </div>

          {/* vstext */}
          <div className="text-white text-5xl">vs</div>

          {/* 상대 유저 */}
          <div className="flex animate-slide-right gap-20">
            {opponent.map((user: User) => (
              <UserProfile
                key={user.userId}
                nickname={user.nickname}
                profileImg={user.profileImg}
                tierId={user.tierId}
                className="w-24 h-24"
              />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default TeamFinalPage;
