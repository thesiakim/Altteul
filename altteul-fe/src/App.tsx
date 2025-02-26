import GameGnb from '@components/Nav/GameGnb';
import MainGnb from '@components/Nav/MainGnb';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import ModalManager from '@utils/ModalManager';
import { useEffect, useMemo } from 'react';
import { useSocketStore } from '@stores/socketStore';
import { inviteResponse } from '@utils/Api/matchApi';
import socketResponseMessage from 'types/socketResponseMessage';
import { MODAL_TYPES } from 'types/modalTypes';
import chatmodalimg from '@assets/icon/chatmodal.svg';

// 임시 친구모달 버튼
import useModalStore from '@stores/modalStore';
import { useMatchStore } from '@stores/matchStore';
import useFriendChatStore from '@stores/friendChatStore';
import useAuthStore from '@stores/authStore';

//react-Toastify
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const App = () => {
  const location = useLocation();
  const isGamePage = location.pathname.startsWith('/game');
  const socket = useSocketStore();
  const navigate = useNavigate();
  const { userId } = useAuthStore();
  const matchStore = useMatchStore();
  const { openModal } = useModalStore();
  const fcStore = useFriendChatStore();

  //로그인 시 소켓 연결 유지
  useEffect(() => {
    const wasConnected = sessionStorage.getItem('wsConnected') === 'true';
    if (wasConnected && !!sessionStorage.getItem('token')) {
      socket.connect(); //로그인 성공시 소켓 연결
    }
  }, []);

  // 로그인 & 소켓 연결 성공 시 친구관련 구독 신청
  useEffect(() => {
    const userId = sessionStorage.getItem('userId');
    if (socket.connected && userId) {
      socket.subscribe(`/sub/invite/${userId}`, handleMessage); //게임 초대 구독
      socket.subscribe(`/sub/notification/${userId}`, handleMessage); //친구 신청 구독
      socket.subscribe(`/sub/friend/update/${userId}`, handleMessage); //친구 수락/거절 구독
    }
  }, [socket.connected]);

  //전체 소켓 응답 메세지 핸들러
  const handleMessage = async (message: socketResponseMessage) => {
    //소켓 응답을 받는 경로
    const allowedPaths = ['/', '/rank', '/match/select'];

    // users/:id 페이지도 허용하려면
    const isUsersPage = location.pathname.startsWith('/users/');

    if (!allowedPaths.includes(location.pathname) && !isUsersPage) {
      // 이 경로가 아니면 처리 안 하고 그냥 return
      return;
    }

    const { type, data } = message;
    // console.log(message);
    //요청을 받은 경우
    if (type === 'INVITE_REQUEST_RECEIVED') {
      //React-Toastify를 사용한 confirm 대체
      toast.info(
        <div>
          <p className="text-primary-white">{`${data.nickname || '알 수 없음'}님이 팀전에 초대하셨습니다.`}</p>
          <div className="mt-2 flex justify-end gap-2">
            <button
              onClick={() => {
                toast.dismiss();
                handleInviteResponse(true, data.nickname, data.roomId);
              }}
              className="px-3 py-1 bg-primary-orange text-white rounded"
            >
              수락
            </button>
            <button
              onClick={() => {
                toast.dismiss();
                handleInviteResponse(false, data.nickname, data.roomId);
              }}
              className="px-3 py-1 bg-gray-500 text-white rounded"
            >
              거절
            </button>
          </div>
        </div>,
        {
          position: 'top-center',
          autoClose: false,
          hideProgressBar: false,
          closeOnClick: false,
          pauseOnHover: true,
          draggable: true,
          closeButton: false,
        }
      );
    }

    //초대 거절 응답
    if (type === 'INVITE_REJECTED') {
      toast.error('초대 요청이 거절되었습니다.', {
        position: 'top-center',
        autoClose: 3000,
      });
    }

    //초대 수락 응답
    if (type === 'INVITE_ACCEPTED') {
      toast.success('초대를 수락했습니다.', {
        position: 'top-center',
        autoClose: 3000,
      });
    }

    if (type === 'SEND_REQUEST') {
      toast.info(`새 친구 신청이 도착했습니다!`, {
        position: 'bottom-center',
        className: 'text-white',
      });
    }

    if (type === 'FRIEND_LIST_UPDATE_REQUIRED' || type === 'FRIEND_RELATION_CHANGED') {
      // friendChatStore의 트리거 함수 호출
      fcStore.triggerFriendsRefresh();
      toast.info(`친구 목록이 업데이트 되었습니다.`, {
        position: 'bottom-center',
        className: 'text-white',
      });
    }
  };

  // 초대 응답 처리 함수
  const handleInviteResponse = async (accepted: boolean, nickname: string, roomId: number) => {
    try {
      const res = await inviteResponse(nickname, roomId, accepted);

      if (accepted) {
        // 초대 수락 시
        matchStore.setMatchData(res.data);
        toast.success(`${nickname}님의 대기방으로 이동합니다.`, {
          position: 'top-center',
          autoClose: 2000,
          onClose: () => navigate(`/match/team/composition`),
        });
      }
    } catch (error) {
      console.error('초대 응답 중 오류 발생:', error);
      toast.error('초대 응답 중 오류가 발생했습니다.', {
        position: 'top-center',
        autoClose: 3000,
      });
    }
  };

  const hideNavigation = useMemo(
    () =>
      new Set([
        '/match/team/composition',
        '/match/team/search',
        '/match/team/final',
        '/match/single/search',
        '/match/single/final',
      ]),
    []
  );

  const showFriendChatModalButton =
    ['/', '/rank', '/match/select', '/match/single/search', '/match/team/composition'].includes(
      location.pathname
    ) || location.pathname.startsWith('/users/');

  const transparentNavigation = useMemo(
    () => new Set(['/match/select', '/rank', `/users/${userId}`]),
    [userId]
  );

  return (
    <>
      <ToastContainer
        position="top-right"
        autoClose={5000}
        hideProgressBar={false}
        newestOnTop
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
      />
      <div className="min-h-screen">
        {!hideNavigation.has(location.pathname) && (isGamePage ? <GameGnb /> : <MainGnb />)}
        <main className={`mt-[3.5rem] bg-primary-black h-[calc(100vh-3.5rem)]`}>
          <Outlet />
          {/* 친구채팅모달 */}
          {showFriendChatModalButton && !!sessionStorage.getItem('token') && (
            <button
              onClick={() => openModal(MODAL_TYPES.MAIN)}
              className="fixed bottom-5 right-5 z-50"
            >
              <img
                src={chatmodalimg}
                alt="친구채팅모달"
                className="w-[4rem] h-[4rem] object-contain"
              />
            </button>
          )}
        </main>
        <ModalManager />
      </div>
    </>
  );
};

export default App;
