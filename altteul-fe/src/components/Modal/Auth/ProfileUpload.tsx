import React, { useState, useRef, useEffect } from 'react';
import PeopleIcon from '@assets/icon/People.svg';

// ProfileImageType은 File, 문자열 URL, 또는 null일 수 있음
export type ProfileImageType = string | File | null;

type ProfileUploadProps = {
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  currentImage: ProfileImageType;
};

const ProfileUpload = ({ onChange, currentImage }: ProfileUploadProps) => {
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [preview, setPreview] = useState<string | null>(null);

  // currentImage가 변경될 때마다 preview 업데이트
  useEffect(() => {
    // currentImage가 File인 경우 URL로 변환
    if (currentImage instanceof File) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreview(reader.result as string);
      };
      reader.readAsDataURL(currentImage);
    } else if (typeof currentImage === 'string') {
      // 이미 문자열(URL)인 경우 그대로 사용
      setPreview(currentImage);
    } else {
      // null인 경우 미리보기 초기화
      setPreview(null);
    }
  }, [currentImage]);

  // 이미지 선택시 미리보기 생성
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    onChange(e);
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreview(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  // 파일 선택 트리거
  const handleButtonClick = () => {
    fileInputRef.current?.click();
  };

  // 현재 표시할 이미지 결정 - preview, 문자열 URL, 또는 기본 아이콘
  const getDisplayImage = () => {
    if (preview) return preview;
    if (typeof currentImage === 'string') return currentImage;
    return PeopleIcon;
  };

  const displayImage = getDisplayImage();
  const isImageChanged = preview && 
    (typeof currentImage === 'string' ? preview !== currentImage : true);

  return (
    <div className="flex flex-col items-center gap-4 w-[22rem]">
      <div
        className="relative w-32 h-32 rounded-full overflow-hidden border-2 border-gray-02 hover:border-primary-orange cursor-pointer"
        onClick={handleButtonClick}
      >
        <img
          src={displayImage}
          alt="프로필 이미지"
          className="w-full h-full object-cover"
        />
        <div className="absolute inset-0 bg-black bg-opacity-40 flex items-center justify-center opacity-0 hover:opacity-100 transition-opacity">
          <span className="text-primary-white text-sm">이미지 변경</span>
        </div>
      </div>

      <input
        ref={fileInputRef}
        type="file"
        name="profileImg"
        onChange={handleFileChange}
        accept="image/png, image/jpg, image/jpeg"
        className="hidden"
      />

      <button
        type="button"
        onClick={handleButtonClick}
        className="px-4 py-2 text-sm text-gray-03 hover:text-primary-orange border-2 border-gray-02 rounded-xl hover:border-primary-orange"
      >
        {isImageChanged ? '다른 사진 선택하기' : '프로필 사진 선택하기'}
      </button>

      {isImageChanged && (
        <button
          type="button"
          onClick={() => {
            if (typeof currentImage === 'string') {
              setPreview(currentImage);
            } else {
              setPreview(null);
            }
            
            if (fileInputRef.current) {
              fileInputRef.current.value = '';
            }
            
            // originalImage 값을 보존하기 위한 이벤트 객체 생성
            const mockEvent = {
              target: {
                name: 'profileImg',
                value: currentImage,
                files: null
              }
            } as React.ChangeEvent<HTMLInputElement>;
            onChange(mockEvent);
          }}
          className="text-sm border-2 border-gray-02 text-gray-02 hover:text-primary-orange hover:border-primary-orange w-44 rounded-lg h-8"
        >
          변경 취소
        </button>
      )}
    </div>
  );
};

export default ProfileUpload;