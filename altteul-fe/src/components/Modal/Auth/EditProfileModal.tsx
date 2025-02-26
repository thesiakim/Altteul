import { useState, useEffect } from 'react';
import Input from '@components/Common/Input';
import Modal from '@components/Common/Modal';
import Button from '@components/Common/Button/Button';
import SignUpDropdown from '@components/Modal/Auth/SignUpDropdown';
import { checkNickname } from '@utils/Api/auth';
import ProfileUpload from '@components/Modal/Auth/ProfileUpload';
import useModalStore from '@stores/modalStore';
import { updateProfile } from '@utils/Api/userApi';
import { toast } from 'react-toastify';

// 프로필 이미지 타입 정의
type ProfileImageType = string | File | null;

// 폼 데이터 타입 재정의
interface EditProfileFormData {
  nickname: string;
  mainLang: string;
  profileImg: ProfileImageType;
}

// 에러 타입 재정의
interface ValidationErrors {
  nickname: string;
  mainLang: string;
  profileImg: string;
}

interface EditProfileProps {
  isOpen: boolean;
  onClose: () => void;
}

const EditProfileModal = ({ isOpen, onClose }: EditProfileProps) => {
  const { modalInfo, setProfileUpdated } = useModalStore();

  const languageOptions = [
    { id: 1, value: 'PY', label: 'Python' },
    { id: 2, value: 'JV', label: 'Java' },
  ];

  // 에러 상태
  const [errors, setErrors] = useState<ValidationErrors>({
    nickname: '',
    mainLang: '',
    profileImg: '',
  });

  // API 및 검증 상태
  const [apiError, setApiError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isNicknameTaken, setIsNicknameTaken] = useState(false);
  const [isCheckingNickname, setIsCheckingNickname] = useState(false);
  const [isNicknameVerified, setIsNicknameVerified] = useState(false);
  const [nicknameChanged, setNicknameChanged] = useState(false);

  // 폼 상태 - modalInfo가 변경될 때마다 업데이트
  const [form, setForm] = useState<EditProfileFormData>({
    nickname: '',
    mainLang: 'PY',
    profileImg: null,
  });

  // modalInfo가 변경될 때마다 form 상태 업데이트
  useEffect(() => {
    if (modalInfo) {
      setForm({
        nickname: modalInfo.nickname || '',
        mainLang: 'PY', // 기본값 설정
        profileImg: modalInfo.profileImg || null,
      });
      // 기존 닉네임이면 자동으로 검증 완료 처리
      setIsNicknameVerified(true);
      setNicknameChanged(false);
    }
  }, [modalInfo]);

  // 폼 초기화 함수
  const resetForm = () => {
    if (modalInfo) {
      setForm({
        nickname: modalInfo.nickname || '',
        mainLang: 'PY',
        profileImg: modalInfo.profileImg || null,
      });
      setIsNicknameVerified(true);
      setNicknameChanged(false);
    } else {
      setForm({
        nickname: '',
        mainLang: 'PY',
        profileImg: null,
      });
      setIsNicknameVerified(false);
      setNicknameChanged(false);
    }

    setErrors({
      nickname: '',
      mainLang: '',
      profileImg: '',
    });
    
    setIsNicknameTaken(false);
    setApiError('');
  };

  // 입력값 변경 핸들러
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
    setErrors(prev => ({ ...prev, [name]: '' }));
    setApiError('');

    if (name === 'nickname') {
      const isOriginalNickname = value === modalInfo?.nickname;
      
      setNicknameChanged(!isOriginalNickname);
      
      if (isOriginalNickname) {
        setIsNicknameVerified(true);
        setIsNicknameTaken(false);
      } else {
        setIsNicknameVerified(false);
        setIsNicknameTaken(false);
      }
    }
  };

  // 드롭다운 언어 선택
  const handleSelectChange = (selected: string) => {
    setForm(prev => ({ ...prev, mainLang: selected }));
  };

  // 이미지 파일 업로드 처리
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files?.length) {
      // 새 파일 업로드
      setForm(prev => ({ ...prev, profileImg: e.target.files![0] }));
    } else if (e.target.name === 'profileImg' && e.target.value && typeof e.target.value === 'string') {
      // 취소 시 기존 이미지 복원
      setForm(prev => ({ ...prev, profileImg: e.target.value }));
    } else if (e.target.files === null) {
      // 초기화 시 기본 이미지로
      setForm(prev => ({ ...prev, profileImg: modalInfo?.profileImg || null }));
    }
  };

  // 닉네임 중복확인
  const handleCheckNickname = async (e: React.MouseEvent) => {
    e.preventDefault();

    if (form.nickname === modalInfo?.nickname) {
      setIsNicknameVerified(true);
      return;
    }

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

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    // 닉네임이 변경되지 않았거나 이미 인증된 경우에만 진행
    if (form.nickname !== modalInfo?.nickname && !isNicknameVerified) {
      setApiError('닉네임 중복확인이 필요합니다.');
      toast.warning('닉네임 중복확인이 필요합니다.', {
        position: "top-center",
        autoClose: 3000
      });
      return;
    }

    const formData = new FormData();
    const requestData = {
      nickname: form.nickname,
      mainLang: form.mainLang,
    };

    formData.append('request', JSON.stringify(requestData));

    if (form.profileImg instanceof File) {
      formData.append('image', form.profileImg);
    } else if (form.profileImg === null) {
      formData.append('image', '0');
    }
    // 기존 이미지를 유지하는 경우 profileImg를 전송하지 않음

    try {
      setIsSubmitting(true);
      const response = await updateProfile(formData);
      setProfileUpdated(true); 
      toast.success('정보가 수정되었습니다.', {
        position: "top-center",
        autoClose: 2000,
        onClose: onClose
      });
    } catch (error) {
      if (error instanceof Error) {
        console.error('프로필 수정 중 오류 발생 : ', error);
        setApiError(error.message || '서버와 연결 할 수 없습니다. 다시 시도하세요.');
        toast.error('프로필 수정 중 오류가 발생했습니다.', {
          position: "top-center",
          autoClose: 3000
        });
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  // 닉네임 상태 메시지 표시 여부 결정
  const shouldShowNicknameVerificationMessage = () => {
    return nicknameChanged && isNicknameVerified && !isNicknameTaken && !errors.nickname;
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      onReset={resetForm}
      title="프로필 수정"
      height="auto"
      className="bg-primary-white"
    >
      <form onSubmit={handleSubmit} className="flex flex-col gap-5 mt-6">
        <Input
          name="nickname"
          type="text"
          placeholder="닉네임을 입력해 주세요"
          onChange={handleChange}
          value={form.nickname}
          buttonText={isCheckingNickname ? '확인중...' : '중복확인'}
          onButtonClick={handleCheckNickname}
          buttonDisabled={form.nickname === modalInfo?.nickname}
        />
        {errors.nickname && <p className="text-gray-03 font-semibold text-sm">{errors.nickname}</p>}
        {shouldShowNicknameVerificationMessage() && (
          <p className="text-primary-orange font-semibold text-sm -my-2.5 ml-1">사용 가능한 닉네임입니다.</p>
        )}

        <SignUpDropdown
          options={languageOptions}
          value={form.mainLang}
          onChange={handleSelectChange}
          className="bg-primary-white border rounded-xl"
        />
        {errors.mainLang && <p className="text-primary-orange text-sm">{errors.mainLang}</p>}

        <ProfileUpload
          onChange={handleFileChange} 
          currentImage={typeof form.profileImg === 'string' ? form.profileImg : null} 
        />

        {apiError && <p className="text-primary-orange text-sm">{apiError}</p>}

        <Button type="submit" className="h-[3rem]">
          {isSubmitting ? '처리중...' : '수정하기'}
        </Button>
      </form>
    </Modal>
  );
};

export default EditProfileModal;