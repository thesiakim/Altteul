import { useLocation, useNavigate, useParams } from 'react-router-dom';
import useAuthStore from '@stores/authStore';
import useModalStore from '@stores/modalStore';
import React from 'react';
import logo from '@assets/icon/Altteul.svg';
import { useSocketStore } from '@stores/socketStore';

const MainGnb = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const params = useParams();
  const { token, logout } = useAuthStore();
  const { openModal, closeModal } = useModalStore();
  const { disconnect } = useSocketStore();
  const { userId } = useAuthStore();

  const isSelectPage = location.pathname.startsWith('/match');
  const transparentNavigation = ['/match/select', '/rank', `/users/${params.userId}`].includes(
    location.pathname
  );

  // 게임 시작 버튼 클릭 시 유저 있냐없냐에 따라 다르게
  const handleGameStart = () => {
    if (token) {
      navigate('/match/select');
    } else {
      // 아직 로그인 모달 안된거같아서 임시로 만듬
      openModal('login');
    }
  };

  // const scrollToGameGuide = () => {
  //   navigate('/');
  //   // 아직 게임 방법 컴포넌트 제작 안해서 임시로 로직만 짜둠
  //   setTimeout(() => {
  //     document.getElementById('game-guide')?.scrollIntoView({ behavior: 'smooth' });
  //   }, 100);
  // };

  const handleLogout = () => {
    logout();
    disconnect();
    navigate('/');
    closeModal();
  };

  return (
    <>
      <nav
        className={`fixed top-0 w-full z-20 px-8 text-sm h-[3.5rem] ${transparentNavigation ? 'bg-gradient-to-b from-primary-black to-transparent' : 'bg-primary-black'}`}
      >
        <div className="flex items-center justify-between h-[3.5rem]">
          {/* 좌측 영역 */}
          <div className="flex items-center mr-auto">
            <button onClick={() => navigate('/')} className="flex items-center">
              <img src={logo} alt="홈으로" className="w-5/6" />
            </button>
            {!isSelectPage && (
              <div className="flex space-x-2">
                <button
                  onClick={handleGameStart}
                  className="px-3 py-1 bg-primary-orange text-primary-white rounded-lg hover:bg-secondary-orange hover:text-gray-01 transition-colors"
                >
                  게임 시작
                </button>
              </div>
            )}
          </div>

          {/* 우측 영역 */}
          <div className="flex items-center space-x-4 ml-auto">
            {/* <button
              onClick={scrollToGameGuide}
              className="py-2 text-primary-white hover:text-gray-03 transition-colors"
            >
              게임 방법
            </button> */}
            <button
              onClick={() => navigate('/rank')}
              className="py-2 text-primary-white hover:text-gray-03 transition-colors"
            >
              랭킹
            </button>
            {/* 로그인 상태에 따라 렌더링 변경 */}
            {token ? (
              <>
                <button
                  onClick={() => navigate(`/users/${userId}`)}
                  className="py-2 text-primary-white hover:text-gray-03 transition-colors"
                >
                  대전 기록
                </button>
                <button
                  onClick={handleLogout}
                  className="ml-2 px-3 py-1 bg-primary-orange text-primary-white rounded-lg hover:bg-secondary-orange hover:text-gray-01 transition-colors"
                >
                  로그아웃
                </button>
              </>
            ) : (
              <button
                onClick={() => openModal('login')}
                className="ml-2 px-3 py-1 bg-primary-orange text-primary-white rounded-lg hover:bg-secondary-orange hover:text-gray-01 transition-colors"
              >
                로그인
              </button>
            )}
          </div>
        </div>
      </nav>
    </>
  );
};

export default React.memo(MainGnb);
