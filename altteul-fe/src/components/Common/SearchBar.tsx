import Input from '@components/Common/Input';
import React, { useState } from 'react';
import searchIcon from '@assets/icon/friend/Search.svg';

interface SearchBarProps {
  onSearch: (searchTerm: string) => void;
  initiaValue?: string; // 초기 검색어
  placeholder?: string;
  width?: string;
  className?: string;
}

const SearchBar = ({
  onSearch,
  initiaValue = '',
  placeholder = '검색어를 입력하세요',
  width = '',
  className = '',
}: SearchBarProps) => {
  const [searchTerm, setSearchTerm] = useState(initiaValue);

  // 입력값 변경
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
  };

  // 검색 제출
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSearch(searchTerm.trim());
  };

  return (
    <form onSubmit={handleSubmit} className={`relative flex items-center ${className}`}>
      <Input
        type="text"
        name="search"
        value={searchTerm}
        onChange={handleChange}
        placeholder={placeholder}
        width={width}
        className="pr-10"
      />
      <button
        type="submit"
        className="absolute right-3 top-1/2 -translate-y-1/2 hover:opacity-80"
        aria-label="검색"
      >
        <img src={searchIcon} alt="돋보기" className="w-5 h-5" />
      </button>
    </form>
  );
};

export default SearchBar;
