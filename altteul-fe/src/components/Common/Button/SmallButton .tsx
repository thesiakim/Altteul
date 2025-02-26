// 로그인 버튼 기준

import React from "react";

type ButtonProps = {
  onClick?: () => void; // 버튼 클릭 시 동작
  type?: "button" | "submit";
  children: React.ReactNode; // 필수, 버튼에 표시될 텍스트
  backgroundColor?: string;
  fontColor?: string;
  className?: string;
  disabled?: boolean;
};

const SmallButton = ({
  onClick,
  type = "button",
  children,
  backgroundColor = "primary-orange",
  fontColor = "primary-white",
  className = "",
  disabled = false,
}: ButtonProps) => (
  <button
    onClick={onClick}
    type={type}
    className={`rounded-lg px-3 py-1 bg-${backgroundColor} text-${fontColor} ${className}`}
  >
    {children}
  </button>
);

export default SmallButton;
