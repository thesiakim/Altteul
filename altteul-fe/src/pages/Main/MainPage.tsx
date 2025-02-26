import AnimatedCodeEditor from '@components/Main/AnimatedCodeEditor';
import useAuthStore from '@stores/authStore';
import { useEffect, useState } from 'react';
import throttle from 'lodash/throttle';
// import GameGuide from '@components/Main/GameGuide';
import SmallButton from '@components/Common/Button/SmallButton ';
import { useNavigate } from 'react-router-dom';
import LoginModal from '@components/Modal/Auth/LoginModal';
import useModalStore from '@stores/modalStore';

const MainPage = () => {
  const { setToken, setUserId, token } = useAuthStore();
  const [showGameMethod, setShowGameMethod] = useState(false);
  const [lastScrollTop, setLastScrollTop] = useState(0);
  const navigate = useNavigate();
  const { openModal, closeModal, isOpen } = useModalStore();

  // URL 파라미터 처리
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const token = params.get('accessToken');
    const userId = params.get('userId');

    if (token && userId) {
      localStorage.setItem('token', token);
      localStorage.setItem('userId', userId);
      setToken(token);
      setUserId(userId);
      window.history.replaceState({}, document.title, '/');
    }
  }, []);

  // 스크롤 이벤트 처리
  useEffect(() => {
    const handleScroll = throttle(() => {
      const windowHeight = window.innerHeight;
      const scrollTop = window.scrollY;

      if (scrollTop > windowHeight * 0.7) {
        // 스크롤이 아래로 내려가면 "게임 방법" 표시
        if (scrollTop > lastScrollTop) {
          setShowGameMethod(true);
        }
      } else {
        // 스크롤이 위로 올라가면 "게임 방법" 숨김
        if (scrollTop < lastScrollTop) {
          setShowGameMethod(false);
        }
      }

      // 마지막 스크롤 위치 업데이트
      setLastScrollTop(scrollTop);
    }, 200);

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, [lastScrollTop]);

  const handleGameStart = () => {
    if (token) {
      navigate('/match/select');
    } else {
      openModal('login');
    }
  };

  return (
    <div className="overflow-hidden">
      {/* 첫 번째 섹션 */}
      <section className="h-[calc(100vh-3.5rem)] relative overflow-hidden">
        <AnimatedCodeEditor />
        <div className="absolute top-0 bottom-0 right-20 flex flex-col items-end justify-center text-primary-white  z-20 tracking-tight">
          <div className="text-5xl font-semibold text-right">
            <p className="text-primary-orange">알뜰?</p>
            <p className="text-gray-02">
              <span className="text-primary-white">알</span>고리즘 한 판
            </p>
            <p className="text-gray-02">
              <span className="text-primary-white">뜰</span>래?
            </p>
          </div>

          <div className="text-lg font-medium text-right text-gray-01 mt-6 mb-6">
            <p>개인 배틀부터 팀과 같이 푸는 협동 배틀까지,</p>
            <p>랜덤 매칭으로 다양한 알고리즘 문제를 풀어보세요.</p>
          </div>
          <SmallButton onClick={handleGameStart}>게임 시작</SmallButton>
        </div>
      </section>
      {/* 두 번째 섹션 */}
      {/* <section
        className={`min-h-[57rem] p-8 transition-opacity duration-500 mt-4 ${
          showGameMethod ? 'opacity-100 pointer-events-auto' : 'opacity-0 pointer-events-none'
        }`}
      >
        <GameGuide />
      </section> */}
      <LoginModal isOpen={isOpen('login')} onClose={() => closeModal()} />{' '}
    </div>
  );
};

export default MainPage;
