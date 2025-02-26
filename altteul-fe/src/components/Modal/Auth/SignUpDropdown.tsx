import React, { useState } from 'react';
import arrowIcon from '@assets/icon/friend/arrow.svg';

type SignUpDropdownProps = {
  options: { id: number | null; value: string; label: string }[];
  value: string;
  onChange: (selected: string) => void;
  width?: string;
  height?: string;
  className?: string;
};

const SignUpDropdown = ({
  options,
  value,
  onChange,
  width = '',
  height = '',
  className,
}: SignUpDropdownProps) => {
  const [isOpen, setIsOpen] = useState(false);
  const selectedOption = options.find(opt => opt.value === value);

  return (
    <div className="relative" style={{ width, height }}>
      <div
        className={`flex items-center justify-between border-2 border-gray-02 px-4 py-2 rounded-lg cursor-pointer hover:border-primary-orange ${className}`}
        onClick={() => setIsOpen(!isOpen)}
      >
        <span>{selectedOption?.label}</span>
        <img
          src={arrowIcon}
          alt="펼치기"
          className={`w-4 h-4 ml-2 transition-transform ${
            isOpen ? 'rotate-[270deg]' : 'rotate-90'
          }`}
        />
      </div>
      {isOpen && (
        <div className="absolute mt-1 w-full bg-primary-white rounded-xl overflow-hidden border border-gray-02 shadow-lg z-50">
          {options.map((option, index) => (
            <div
              key={option.id}
              className={`px-4 py-2 text-gray-05 hover:text-primary-orange hover:bg-gray-01 cursor-pointer transition-colors duration-200
                ${index !== options.length - 1 ? 'border-b border-gray-01' : ''}`}
              onClick={() => {
                onChange(option.value);
                setIsOpen(false);
              }}
            >
              {option.label}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default SignUpDropdown;
