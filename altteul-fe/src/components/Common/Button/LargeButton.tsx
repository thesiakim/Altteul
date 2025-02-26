// 라지사이즈, 결과창 모달에 사용 ex) 한 문제 더 도전하기

import React from "react";

type ButtonProps = {
  onClick?: () => void; // 버튼 클릭 시 동작
  type?: "button" | "submit";
  children: React.ReactNode; // 필수, 버튼에 표시될 텍스트
  backgroundColor?: string;
  fontColor?: string;
  className?: string;
  width?: string;
  height?: string;
};

const LargeButton = ({
  onClick,
  type = "button",
  children,
  backgroundColor = "primary-orange",
  fontColor = "primary-white",
  width = "26.5rem",
  height = "2.75rem",
  className = "",
}: ButtonProps) => (
  <button
    onClick={onClick}
    type={type}
    className={`rounded-lg cursor-pointer font-medium px-5 py-2 bg-${backgroundColor} text-${fontColor} text-base font-bold w-[${width}] h-[${height}] ${className}`}
  >
    {children}
  </button>
);

export default LargeButton;
