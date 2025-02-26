import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import UserProfile from '@components/Match/UserProfile';
import Button from '@components/Common/Button/Button';
import backgroundImage from '@assets/background/team_matching_bg.svg';
import { useMatchStore } from '@stores/matchStore'; // ← Zustand Store
import { useSocketStore } from '@stores/socketStore';
import { teamOut, teamStart } from '@utils/Api/matchApi';
import socketResponseMessage from 'types/socketResponseMessage';
import { User } from 'types/types';
import { toast } from 'react-toastify';

const TeamcompositionPage = () => {
  const navigate = useNavigate();
  const socket = useSocketStore();

  // Store에서 matchData와 setter를 직접 구독
  const { matchData, setMatchData } = useMatchStore(state => ({
    matchData: state.matchData,
    setMatchData: state.setMatchData,
  }));

  // matchData 구조 분해할당
  const { roomId, leaderId, users } = matchData;
  const currentUserId = Number(sessionStorage.getItem('userId'));
  const isLeader = currentUserId === leaderId;

  // 소켓 구독 처리
  useEffect(() => {
    socket.subscribe(`/sub/team/room/${roomId}`, handleMessage);

    return () => {
      // console.log('TeamcompositionPage unmount. Unsubscribe socket.');
      socket.unsubscribe(`/sub/team/room/${roomId}`);
    };
  }, [roomId]);

  // 소켓 메시지 핸들러
  const handleMessage = (message: socketResponseMessage) => {
    // console.log(message);
    const { type, data } = message;

    // ENTER or LEAVE 이벤트가 들어오면 store의 matchData 갱신
    if (type === 'ENTER' || type === 'LEAVE') {
      setMatchData({
        roomId: roomId,
        leaderId: data.leaderId,
        users: data.users,
      });

      // 4명이 되면 자동 매칭 시작
      if (data.users.length >= 4) {
        navigateMatchPage();
      }
    }

    // 매칭이 시작되면 TeamSearchPage로 이동
    if (type === 'MATCHING') {
      navigate('/match/team/search');
    }
  };

  // 매칭 시작 버튼 핸들러
  const handleStartButton = () => {
    if (users.length === 1) {
      toast.error('혼자서는 플레이 할 수 없습니다.', {
        position: 'top-center',
        autoClose: 3000,
      });
      return;
    }

    if (users.length === 4) {
      navigateMatchPage();
      return;
    }

    toast.info(
      <div className="w-[14rem] flex flex-col items-end">
        <p className="text-white">바로 시작하시겠습니까?</p>
        <div className="mt-3 flex justify-end gap-3">
          <button
            onClick={() => {
              toast.dismiss();
              navigateMatchPage();
            }}
            className="px-4 py-2 bg-primary-orange text-white rounded-md"
          >
            시작
          </button>
          <button
            onClick={() => toast.dismiss()}
            className="px-4 py-2 mr-5 bg-gray-500 text-white rounded-md"
          >
            취소
          </button>
        </div>
      </div>,
      {
        position: 'top-center',
        autoClose: false,
        closeButton: false,
        closeOnClick: false,
      }
    );
  };

  // 매칭 조건 충족 시 다음 페이지로 이동 & API 호출
  const navigateMatchPage = async () => {
    await teamStart(roomId);
    // teamStart() 후 소켓 응답(MATCHING)에서 실제 이동 처리
  };

  // 팀 나가기 버튼
  const userOut = () => {
    teamOut(roomId);
    socket.unsubscribe(`/sub/team/room/${roomId}`);
    navigate('/match/select');
  };

  return (
    <div
      className="relative -mt-[3.5rem] min-h-screen w-full bg-cover bg-center"
      style={{ backgroundImage: `url(${backgroundImage})` }}
    >
      <div className="absolute inset-0 bg-black/50"></div>

      <div className="relative min-h-screen w-full z-10 flex flex-col items-center justify-center">
        {/* 팀 정보 */}
        <div className="flex justify-center items-center gap-20">
          {users.map((user: User) => (
            <UserProfile
              key={user.userId}
              nickname={user.nickname}
              profileImg={user.profileImg}
              tierId={user.tierId}
              className="w-24 h-24"
            />
          ))}
        </div>

        {/* 버튼 */}
        <div className="flex gap-6 mt-12">
          {isLeader && (
            <Button
              onClick={handleStartButton}
              className="w-28 h-10 text-lg transition-all duration-300 hover:shadow-orange"
            >
              매칭 시작
            </Button>
          )}
          <Button
            onClick={userOut}
            className="w-32 h-10 text-lg transition-all duration-300 hover:shadow-orange"
          >
            나가기
          </Button>
        </div>
      </div>
    </div>
  );
};

export default TeamcompositionPage;
