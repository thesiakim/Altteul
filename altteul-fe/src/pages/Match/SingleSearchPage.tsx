import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { formatTime } from '@utils/formatTime';
import UserProfile from '@components/Match/UserProfile';
import Button from '@components/Common/Button/Button';
import backgroundImage from '@assets/background/single_matching_bg.svg';
import tmi from '@assets/tmi.json';
import { useTimer } from '@hooks/useTimer';
import { User } from 'types/types';
import { useMatchStore } from '@stores/matchStore';
import { useSocketStore } from '@stores/socketStore';
import { singleOut, singleStart } from '@utils/Api/matchApi';
import socketResponseMessage from 'types/socketResponseMessage';
import { PacmanLoader } from 'react-spinners';
import { toast } from 'react-toastify';

const SingleSearchPage = () => {
  const navigate = useNavigate();
  const matchStore = useMatchStore();
  const socket = useSocketStore();
  const [fact, setFact] = useState<string>('');
  const [facts] = useState<string[]>(tmi.facts);
  const [leaderId, setLeaderId] = useState(matchStore.matchData.leaderId);
  //waitUsers: 방장을 포함하지 않은 대기 유저
  const [waitUsers, setWaitUsers] = useState(
    matchStore.matchData.users.filter(user => user.userId !== leaderId)
  );
  const [headUser, setHeadUser] = useState<User>(
    matchStore.matchData.users.find(user => user.userId === leaderId)
  );
  const roomId = matchStore.matchData.roomId;
  const currentUserId = Number(sessionStorage.getItem('userId'));
  const [isLeader, setIsLeader] = useState(currentUserId === leaderId);

  //유저 퇴장 로직
  const userOut = () => {
    singleOut(roomId); //의도적으로 나간 경우에 서버에 나갔다고 알림, 새로고침시(언마운트)에는 서버에서 아직 방에 남아있다고 판단.
    matchStore.clear(); //의도적으로 나간 경우 matchData remove
    navigate('/match/select');
  };

  //구독처리
  useEffect(() => {
    socket.subscribe(`/sub/single/room/${roomId}`, handleMessage);

    //언마운트 시 구독에 대한 콜백함수(handleMessage 정리)
    return () => {
      // console.log('singleSearchPage Out');
      socket.unsubscribe(`/sub/single/room/${roomId}`);
    };
  }, [roomId]);

  //소켓 응답 처리
  const handleMessage = (message: socketResponseMessage) => {
    // console.log(message);
    const { type, data } = message;
    if (type === 'ENTER' || type === 'LEAVE') {
      setLeaderId(data.leaderId);
      setWaitUsers(data.users.filter(user => user.userId !== data.leaderId));
      setHeadUser(data.users.find(user => user.userId === data.leaderId));
      setIsLeader(currentUserId === data.leaderId);

      // 대기 유저가 8명이 되면 자동으로 게임 시작
      if (data.users.length >= 8) {
        navigateFinalPage();
      }

    } else if (type === 'COUNTING') {
      navigate('/match/single/final');
    }


    reset(); //소켓 응답으로 유저 정보 업데이트 시 타이머 리셋
  };

  // 타이머 설정
  const { seconds, reset } = useTimer({
    initialSeconds: 90, //TODO: 최종, 3분(?)으로 설정

    // 타이머 완료 시 페이지 이동 처리
    onComplete: () => {
      //1. 혼자만 있으면 시작 x
      if (waitUsers.length === 0) {
        toast.error('상대 유저가 입장하지 않아 종료합니다.', {
          position: 'top-center',
          autoClose: 3000,
          onClose: () => userOut(),
        });
        return;
      }
      //2. 방장 제외 1명 이상의 플레이어만 충족하면 시작
      navigateFinalPage();
    },
  });

  //게임 시작 버튼 클릭
  const handleStartButton = async () => {
    //혼자만 있을 때
    if (waitUsers.length === 0) {
      toast.warning('상대 유저가 입장하지 않았습니다.', {
        position: 'top-center',
        autoClose: 3000,
      });
      return;
    }

    //8명 상관없이 시작할건지 확인
    toast.info(
      <div className="w-[14rem] flex flex-col items-end">
        <p className="text-white">바로 시작하시겠습니까?</p>
        <div className="mt-3 flex justify-end gap-3">
          <button
            onClick={() => {
              toast.dismiss();
              navigateFinalPage();
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

  //Final 페이지 이동 조건 충족시
  const navigateFinalPage = async () => {
    // Final 페이지로 넘어가기 전, 마지막 상태 데이터 저장
    const matchData = {
      roomId: roomId,
      leaderId: leaderId,
      users: [headUser, ...waitUsers],
    };

    matchStore.setMatchData(matchData);

    //게임 시작 API 호출(For socket 응답 변환)
    await singleStart(roomId);
  };

  // TMI: 첫 fact 생성 후 5초 간격으로 Rotation
  useEffect(() => {
    setFact(facts[Math.floor(Math.random() * facts.length)]);
    const factRotation = setInterval(() => {
      setFact(facts[Math.floor(Math.random() * facts.length)]);
    }, 5000);
    return () => clearInterval(factRotation);
  }, [facts]);

  return (
    <div
      className="relative -mt-[3.5rem] min-h-screen w-full bg-cover bg-center"
      style={{ backgroundImage: `url(${backgroundImage})` }}
    >
      {/* 배경 오버레이 */}
      <div className="absolute inset-0 bg-black/50"></div>

      {/* 컨텐츠 */}
      <div className="relative min-h-screen w-full z-10 flex flex-col items-center justify-center">
        {/* Timer */}
        <div className="text-white text-4xl mb-8">{formatTime(seconds)}</div>

        {/* 방장: 리더아이디에 해당하는 유저 정보 넣어야 함*/}
        <UserProfile
          nickname={''}
          profileImg={headUser ? headUser.profileImg : null}
          tierId={headUser ? headUser.tierId : null}
          className="mb-1"
          headUser="w-24 h-24"
          headTier="!w-10 !h-10"
        />

        {/* 방장 이름 */}
        <div className="text-white text-2xl mb-4">{headUser?.nickname}</div>

        {/* Status Message */}
        <div className="text-white text-xl mb-8 flex flex-col items-center">
          같이 플레이 할 상대를 찾고 있어요. 🧐
          <div className="flex text-base pt-1.5">
            조금만 기다려 주세요
            <div className="ml-2">
              <PacmanLoader color="#ffffff" size={12} />
            </div>
          </div>
        </div>

        {/* 버튼 */}
        <div className="flex gap-6 mb-12">
          {isLeader && (
            <Button
              className="w-28 h-10 text-lg transition-all duration-300 hover:shadow-orange"
              onClick={handleStartButton}
            >
              게임 시작
            </Button>
          )}
          <Button
            className="w-36 h-10 text-lg transition-all duration-300 hover:shadow-orange"
            onClick={userOut}
          >
            매칭 취소하기
          </Button>
        </div>

        {/* 방장 제외 대기 유저 */}
        <div className="flex justify-center items-center gap-20">
          {waitUsers
            .filter(user => user.userId !== leaderId)
            .map((user: User) => (
              <UserProfile
                key={user.userId}
                nickname={user.nickname}
                profileImg={user.profileImg}
                tierId={user.tierId}
              />
            ))}
        </div>

        {/* TMI */}
        <div className="absolute bottom-14 text-gray-100 text-[1.1rem]">{fact}</div>
      </div>
    </div>
  );
};

export default SingleSearchPage;
