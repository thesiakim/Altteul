import { useState } from 'react';

import Input from '@components/Common/Input';
import Modal from '@components/Common/Modal';
import Button from '@components/Common/Button/Button';
import SignUpDropdown from '@components/Modal/Auth/SignUpDropdown';

import { checkUsername, registerUser, checkNickname, loginUser } from '@utils/Api/auth';
import { validateSignUpForm, SignUpFormData, ValidationErrors } from '@utils/validation';
import ProfileUpload from '@components/Modal/Auth/ProfileUpload';

import { useNavigate } from 'react-router-dom';
import useAuthStore from '@stores/authStore';
import { useSocketStore } from '@stores/socketStore';

interface SignUpProps {
  isOpen: boolean;
  onClose: () => void;
}

const SignUpModal = ({ isOpen, onClose }: SignUpProps) => {
  const navigate = useNavigate();
  const { setToken, setUserId } = useAuthStore();
  const { connect } = useSocketStore();

  const languageOptions = [
    { id: 1, value: 'PY', label: 'Python' },
    { id: 2, value: 'JV', label: 'Java' },
  ];

  // 폼 초기화 함수
  const resetForm = () => {
    setForm({
      username: '',
      password: '',
      confirmPassword: '',
      nickname: '',
      mainLang: 'PY',
      profileImg: null,
    });

    setErrors({
      username: '',
      password: '',
      confirmPassword: '',
      nickname: '',
      mainLang: '',
      profileImg: '',
    });

    // 검증 상태 초기화
    setIsUsernameVerified(false);
    setIsUsernameTaken(false);
    setIsNicknameVerified(false);
    setIsNicknameTaken(false);
    setApiError('');
  };

  // 폼 상태
  const [form, setForm] = useState<SignUpFormData>({
    username: '',
    password: '',
    confirmPassword: '',
    nickname: '',
    mainLang: 'PY',
    profileImg: null,
  });

  // 에러 상태
  const [errors, setErrors] = useState<ValidationErrors>({
    username: '',
    password: '',
    confirmPassword: '',
    nickname: '',
    mainLang: '',
    profileImg: '',
  });

  // API 및 로딩 상태
  const [apiError, setApiError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  // 아이디 중복확인
  const [isUsernameTaken, setIsUsernameTaken] = useState(false);
  const [isCheckingUsername, setIsCheckingUsername] = useState(false);
  const [isUsernameVerified, setIsUsernameVerified] = useState(false);

  // 닉네임 중복확인
  const [isNicknameTaken, setIsNicknameTaken] = useState(false);
  const [isCheckingNickname, setIsCheckingNickname] = useState(false);
  const [isNicknameVerified, setIsNicknameVerified] = useState(false);

  // 입력값 변경 핸들러
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));

    // 다른 에러 메시지 초기화
    setErrors(prev => ({ ...prev, [name]: '' }));
    setApiError('');

    // username이 변경될 때 verified 상태 리셋
    if (name === 'username') {
      setIsUsernameVerified(false);
      setIsUsernameTaken(false);
    }

    // password나 confirmPassword가 변경될 때 confirmPassword 에러 초기화
    if (name === 'password' || name === 'confirmPassword') {
      setErrors(prev => ({
        ...prev,
        confirmPassword: '',
      }));
    }

    // 비밀번호 실시간 유효성 검사
    if (name === 'password') {
      let passwordError = '';
      if (value.length < 8) {
        passwordError = '비밀번호는 8자 이상이어야 합니다.';
      } else if (!/\d/.test(value)) {
        passwordError = '숫자를 포함해야 합니다.';
      } else if (!/[a-zA-Z]/.test(value)) {
        passwordError = '영문자를 포함해야 합니다.';
      }

      setErrors(prev => ({
        ...prev,
        password: passwordError,
      }));
    }
  };

  // 드롭다운 언어 선택
  const handleSelectChange = (selected: string) => {
    setForm(prev => ({ ...prev, mainLang: selected }));
  };

  // 이미지 파일 업로드 처리
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files?.length) {
      setForm(prev => ({ ...prev, profileImg: e.target.files![0] }));
    } else if (e.target.name === 'profileImg' && e.target.value === null) {
      setForm(prev => ({ ...prev, profileImg: null }));
    }
  };

  // 아이디 중복 확인
  const handleCheckUsername = async (e: React.MouseEvent) => {
    e.preventDefault();

    // 아이디 유효성 검사
    if (!/^[a-zA-Z0-9]{5,8}$/.test(form.username)) {
      setErrors(prev => ({
        ...prev,
        username: '아이디는 영문과 숫자의 조합으로 5자 이상, 8자 이하여야 합니다.',
      }));
      return;
    }

    setIsCheckingUsername(true);
    setIsUsernameTaken(false);
    setApiError('');

    try {
      const response = await checkUsername(form.username);
      if (response.isTaken) {
        setIsUsernameTaken(true);
        setIsUsernameVerified(false);
        setErrors(prev => ({
          ...prev,
          username: '이미 사용 중인 아이디입니다.',
        }));
      } else {
        setIsUsernameTaken(false);
        setIsUsernameVerified(true);
        setErrors(prev => ({
          ...prev,
          username: '',
        }));
      }
    } catch (error) {
      console.error('아이디 중복 확인 실패:', error);
      setErrors(prev => ({
        ...prev,
        username: '이미 사용 중인 아이디입니다.',
      }));
      setIsUsernameVerified(false);
    } finally {
      setIsCheckingUsername(false);
    }
  };

  // 닉네임 중복확인
  const handleCheckNickname = async (e: React.MouseEvent) => {
    e.preventDefault();

    // 닉네임 유효성 검사
    if (form.nickname.length < 2 || form.nickname.length > 8) {
      setErrors(prev => ({
        ...prev,
        nickname: '닉네임은 2자 이상 8자 이하여야 합니다.',
      }));
      return;
    }

    setIsCheckingNickname(true);
    setIsNicknameTaken(false);
    setApiError('');

    try {
      const response = await checkNickname(form.nickname);
      if (response.isTaken) {
        setIsNicknameTaken(true);
        setIsNicknameVerified(false);
        setErrors(prev => ({
          ...prev,
          nickname: '이미 사용 중인 닉네임입니다.',
        }));
      } else {
        setIsNicknameTaken(false);
        setIsNicknameVerified(true);
        setErrors(prev => ({
          ...prev,
          nickname: '',
        }));
      }
    } catch (error) {
      console.error('닉네임 중복 확인 실패:', error);
      setErrors(prev => ({
        ...prev,
        nickname: '이미 사용 중인 닉네임입니다.',
      }));
      setIsNicknameVerified(false);
    } finally {
      setIsCheckingNickname(false);
    }
  };

  // 폼 유효성 검사
  const validateForm = () => {
    const validationResult = validateSignUpForm(form);
    const newErrors = { ...errors };
    let isValid = true;

    // 순차적으로 에러 체크
    if (validationResult.errors.username) {
      newErrors.username = validationResult.errors.username;
      isValid = false;
    } else if (!isUsernameVerified) {
      setApiError('아이디 중복확인이 필요합니다.');
      return false;
    }

    if (validationResult.errors.password) {
      newErrors.password = validationResult.errors.password;
      isValid = false;
    }

    if (form.password !== form.confirmPassword) {
      newErrors.confirmPassword = '비밀번호가 일치하지 않습니다.';
      isValid = false;
    }

    if (validationResult.errors.nickname) {
      newErrors.nickname = validationResult.errors.nickname;
      isValid = false;
    } else if (!isNicknameVerified) {
      setApiError('닉네임 중복확인이 필요합니다.');
      return false;
    }

    setErrors(newErrors);
    return isValid;
  };

  // 비밀번호 일치여부 확인 및 메시지 표시 여부 결정
  const shouldShowPasswordMatchMessage = () => {
    return form.password && form.confirmPassword && !errors.password && !errors.confirmPassword;
  };

  const getPasswordMatchMessage = () => {
    if (form.password && form.confirmPassword) {
      if (form.password !== form.confirmPassword) {
        return '비밀번호가 일치하지 않습니다.';
      }
      if (!errors.password) {
        return '비밀번호가 일치합니다.';
      }
    }
    return '';
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!validateForm()) return;

    const formData = new FormData();
    const requestData = {
      username: form.username,
      password: form.password,
      nickname: form.nickname,
      mainLang: form.mainLang,
    };

    formData.append('request', JSON.stringify(requestData));

    if (form.profileImg) {
      formData.append('profileImg', form.profileImg);
    } else {
      formData.append('profileImg', '0');
    }

    try {
      setIsSubmitting(true);
      const response = await registerUser(formData);

      if (response?.status >= 200 && response?.status < 300) {
        // console.log('회원가입 성공');

        try {
          const loginResponse = await loginUser(form.username, form.password);
          const token =
            loginResponse.headers?.authorization || loginResponse.headers?.['authorization'];
          const userId = loginResponse.headers?.userid || loginResponse.headers?.['userid'];
          const cleanToken = token.replace(/^Bearer\s+/i, '');

          setToken(cleanToken);
          setUserId(userId.toString());
          connect();

          onClose();
          navigate('/');
        } catch (loginError) {
          console.error('자동 로그인 실패:', loginError);
          setApiError(
            '회원가입은 완료되었으나 자동 로그인에 실패했습니다. 로그인을 다시 시도해주세요.'
          );
          resetForm(); // 모든 상태 초기화
          onClose();
        }
      }
    } catch (error: unknown) {
      if (error instanceof Error) {
        console.error('회원가입 중 오류 발생 : ', error);
        setApiError(error.message || '서버와 연결 할 수 없습니다. 다시 시도하세요.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      onReset={resetForm}
      title="알뜰 회원가입"
      height="auto"
      className="bg-primary-white"
    >
      <form onSubmit={handleSubmit} className="flex flex-col gap-5 mt-6">
        <Input
          name="username"
          type="text"
          placeholder="아이디를 입력해 주세요."
          onChange={handleChange}
          value={form.username}
          buttonText={isCheckingUsername ? '확인중...' : '중복확인'}
          onButtonClick={handleCheckUsername}
        />
        {errors.username && (
          <p className="text-gray-03 font-semibold text-sm -my-3 ml-1">{errors.username}</p>
        )}
        {isUsernameVerified && !isUsernameTaken && !errors.username && (
          <p className="text-primary-orange font-semibold text-sm -my-3 ml-1">
            사용 가능한 아이디입니다.
          </p>
        )}

        <Input
          name="password"
          type="password"
          placeholder="비밀번호를 입력해 주세요."
          onChange={handleChange}
          value={form.password}
          showPasswordToggle={true}
        />
        {errors.password && (
          <p className="text-primary-orange text-sm -my-3 ml-1">{errors.password}</p>
        )}

        <Input
          name="confirmPassword"
          type="password"
          placeholder="비밀번호 확인"
          onChange={handleChange}
          value={form.confirmPassword}
          showPasswordToggle={true}
        />
        {errors.confirmPassword && !shouldShowPasswordMatchMessage() && (
          <p className="text-primary-orange text-sm -my-2 ml-1 font-semibold">
            {errors.confirmPassword}
          </p>
        )}
        {shouldShowPasswordMatchMessage() && (
          <p
            className={`text-sm ${
              form.password === form.confirmPassword
                ? 'text-primary-orange font-semibold -my-3 ml-1'
                : 'text-gray-03 font-semibold -my-3 ml-1'
            }`}
          >
            {getPasswordMatchMessage()}
          </p>
        )}

        <Input
          name="nickname"
          type="text"
          placeholder="닉네임을 입력해 주세요"
          onChange={handleChange}
          value={form.nickname}
          buttonText={isCheckingNickname ? '확인중...' : '중복확인'}
          onButtonClick={handleCheckNickname}
        />
        {errors.nickname && (
          <p className="text-gray-03 font-semibold text-sm -my-3 ml-1">{errors.nickname}</p>
        )}
        {isNicknameVerified && !isNicknameTaken && !errors.nickname && (
          <p className="text-primary-orange font-semibold text-sm -my-3 ml-1">
            사용 가능한 닉네임입니다.
          </p>
        )}

        <SignUpDropdown
          options={languageOptions}
          value={form.mainLang}
          onChange={handleSelectChange}
          className="bg-primary-white border rounded-xl"
        />
        {errors.mainLang && (
          <p className="text-primary-orange text-sm -my-3 ml-1">{errors.mainLang}</p>
        )}

        <ProfileUpload onChange={handleFileChange} currentImage={form.profileImg} />

        {apiError && <p className="text-primary-orange text-sm -my-3 ml-1">{apiError}</p>}

        <Button type="submit" className="h-[3rem]">
          {isSubmitting ? '처리중...' : '가입하기'}
        </Button>
      </form>
    </Modal>
  );
};

export default SignUpModal;
