import React, { useState } from 'react';
import { loginUser } from '@utils/Api/auth';
import Modal from '@components/Common/Modal';
import Input from '@components/Common/Input';
import Button from '@components/Common/Button/Button';
import useAuthStore from '@stores/authStore';
import useModalStore from '@stores/modalStore';
import gitHubLogo from '@assets/icon/github_logo.svg';
import { useSocketStore } from '@stores/socketStore';
import { useNavigate } from 'react-router-dom';

const LoginModal = ({ isOpen = false, onClose = () => {} }) => {
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const { setToken, setUserId } = useAuthStore();
  const { openModal, closeModal } = useModalStore();
  const { connect } = useSocketStore();
  const navigate = useNavigate();

  const handleSignUpClick = () => {
    closeModal();
    openModal('signup');
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError('');
  };

  const handleSubmit = async (e?: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError('');
    if (!form.username.trim() || !form.password.trim()) {
      setError('아이디와 비밀번호를 모두 입력해주세요.');
      return;
    }

    try {
      //로그인 처리
      const response = await loginUser(form.username, form.password);
      const token = response.headers?.authorization || response.headers?.['authorization'];
      const userId = response.headers?.userid || response.headers?.['userid'];
      const cleanToken = token.replace(/^Bearer\s+/i, '');
      setToken(cleanToken);
      setUserId(userId.toString());
      connect();
      setForm({ username: '', password: '' });
      closeModal();
      navigate('/'); //리다이렉트 시켜서 App.tsx에 있는 소켓 관련 연결을 시도
    } catch (error) {
      console.error('로그인 실패:', error);
      setError('로그인에 실패했습니다. 아이디와 비밀번호를 확인해 주세요.');
    }
  };

  // 깃허브 로그인
  const handleGithubLogin = () => {
    window.location.href = 'http://localhost:8080/oauth2/authorization/github';
  };

  //엔터 눌렀을 때 이벤트
  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      handleSubmit();
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={closeModal}
      height="29rem"
      minHeight="29rem"
      title="알뜰 로그인"
      className="bg-primary-white"
    >
      <form onSubmit={handleSubmit} className="flex flex-col gap-5">
        <div className="mt-2"></div>
        <Input
          type="text"
          name="username"
          placeholder="아이디를 입력해 주세요"
          value={form.username}
          onKeyDown={handleKeyPress}
          onChange={handleChange}
        />

        <Input
          type="password"
          name="password"
          placeholder="비밀번호를 입력해 주세요"
          value={form.password}
          onChange={handleChange}
          onKeyDown={handleKeyPress}
          showPasswordToggle={true}
        />
        {error && <p className="text-primary-orange">{error}</p>}

        <Button type="submit" className="h-[2.8rem] w-full hover:brightness-90">
          로그인
        </Button>

        <Button
          type="button"
          backgroundColor="gray-01"
          fontColor="gray-04"
          className="h-[2.8rem] w-full hover:brightness-90 bg-gray-01 text-gray-04"
          onClick={handleSignUpClick}
        >
          회원가입
        </Button>
        {/* 
        <a
          href="#"
          className="text-right text-gray-03 cursor-pointer underline hover:font-semibold"
        >
          비밀번호 재설정
        </a> */}

        <Button
          onClick={handleGithubLogin}
          type="button"
          backgroundColor="primary-black"
          className="h-[2.8rem] w-[22rem]  hover:brightness-110"
          img={gitHubLogo}
        >
          github로 간편하게 시작하기
        </Button>
      </form>
    </Modal>
  );
};

export default LoginModal;
