import { Eye, EyeOff } from 'lucide-react';
import React, { useState } from 'react';
import Magnifier from '@assets/icon/friend/Search_orange.svg';

type InputProps = {
  type?: 'text' | 'password' | 'search';
  placeholder?: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  name: string;
  width?: string;
  height?: string;
  className?: string;
  buttonText?: string;
  onButtonClick?: (e: React.MouseEvent) => void;
  showPasswordToggle?: boolean;
  showMagnifier?: boolean;
  onKeyDown?: (e: React.KeyboardEvent<HTMLInputElement>) => void;
  buttonDisabled?: boolean; // 버튼 비활성화 여부
};

const DEFAULT_INPUT_STYLE =
  'text-lg font-light text-primary-black bg-primary-white border-2 border-gray-02 rounded-lg px-4 py-2 w-full focus:ring-3 focus:ring-primary-orange focus:outline-none';
const BUTTON_STYLE =
  'ml-2 px-3 py-2 border border-primary-orange text-primary-orange bg-primary-white rounded-lg cursor-pointer hover:bg-gray-100 transition-colors border-2 '; // 호버배경 gray-100이 예뻐서 그냥 둠!

const Input = ({
  type = 'text',
  placeholder = '내용을 입력하세요',
  value,
  onChange,
  name,
  height = '3rem',
  className = '',
  buttonText,
  onButtonClick,
  showPasswordToggle = false,
  showMagnifier = false,
  buttonDisabled = false,
  onKeyDown,
}: InputProps) => {
  const [isPasswordVisible, setIsPasswordVisible] = useState(false);

  return (
    <div className="flex items-center relative">
      <div className="flex-grow" style={{ width: `calc(100% - ${buttonText ? '6rem' : '0rem'})` }}>
        <input
          className={`${className || DEFAULT_INPUT_STYLE} ${showPasswordToggle ? 'pr-12' : ''} w-full h-[${height}]`}
          type={showPasswordToggle ? (isPasswordVisible ? 'text' : 'password') : type}
          placeholder={placeholder}
          value={value}
          onChange={onChange}
          name={name}
          onKeyDown={onKeyDown}
        />
        {showPasswordToggle && (
          <button
            type="button"
            onClick={() => setIsPasswordVisible(!isPasswordVisible)}
            className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-03 hover:text-gray-02"
          >
            {isPasswordVisible ? <Eye className="w-5 h-5" /> : <EyeOff className="w-5 h-5" />}
          </button>
        )}{' '}
        {showMagnifier && (
          <button
            onClick={onButtonClick}
            className="absolute right-3 top-1/2 -translate-y-1/2 hover:opacity-80 transition-opacity disabled:opacity-50"
            aria-label="검색"
          >
            <img src={Magnifier} alt="검색" className="w-5 h-5" />
          </button>
        )}
      </div>
      {buttonText && onButtonClick && (
        <button
          type="button"
          onClick={onButtonClick}
          disabled={buttonDisabled}
          className={`${BUTTON_STYLE}
             ${buttonDisabled && '!bg-gray-02 border !border-gray-02 text-white hover:bg-gray-02 !cursor-auto'
              
             }`}
        >
          {buttonText}
        </button>
      )}
    </div>
  );
};

export default Input;
