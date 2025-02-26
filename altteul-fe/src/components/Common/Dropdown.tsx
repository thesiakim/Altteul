import { useState } from 'react';
import arrowIcon from '@assets/icon/friend/arrow.svg';

type DropdownProps = {
  options: { id: number | null; value: string; label: string }[];
  value: string;
  onChange: (selected: string) => void;
  width?: string;
  height?: string;
  className?: string;
  optionCustomName?: string
  borderColor?: string
  fontSize?: string;
};

const Dropdown = ({ options, value, onChange, width = '', className, optionCustomName, borderColor, fontSize }: DropdownProps) => {
  const [isOpen, setIsOpen] = useState(false);
  const selectedOption = options.find(opt => opt.value === value);

  return (
    <div className="relative" style={{ width }}>
      <div
        className={`flex items-center justify-between border px-4 py-2 bg-gray-03 text-primary-white rounded-lg cursor-pointer ${className}`}
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
        <div className={`absolute top-9 left-0 mt-1 w-full bg-gray-03 rounded-lg overflow-hidden z-10 ${optionCustomName}`}>
          {' '}
          {/* w-full로 변경 */}
          {options.map(option => (
            <div
              key={option.id}
              className={`px-4 py-2 text-primary-white hover:bg-gray-05 cursor-pointer z-30 border-b dropdown-last ${borderColor} ${fontSize}`}
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

export default Dropdown;
