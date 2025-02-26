// '게임시작' 버튼 기준

import React from "react";

type ButtonProps = {
  onClick?: () => void; // 버튼 클릭 시 동작
  type?: "button" | "submit";
  children: React.ReactNode; // 필수, 버튼에 표시될 텍스트
  backgroundColor?: string;
  fontColor?: string;
  width?: string;
  height?: string;
  className?: string;
};

const MediumButton = ({
  onClick,
  type = "button",
  children,
  backgroundColor = "primary-orange",
  fontColor = "primary-white",
  width = "10rem",
  height = "3rem",
  className = "",
}: ButtonProps) => (
  <button
    onClick={onClick}
    type={type}
    className={`rounded-lg cursor-pointer px-5 py-2 bg-${backgroundColor} text-${fontColor} text-lg font-semibold w-[${width}] h-[${height}] ${className}`}
  >
    {children}
  </button>
);

export default MediumButton;
