// 커스텀 공통 버튼

import React from "react";

type ButtonProps = {
  onClick?: () => void; // 버튼 클릭 시 동작
  type?: "button" | "submit";
  children: React.ReactNode; // 필수, 버튼에 표시될 텍스트
  backgroundColor?: string;
  fontColor?: string;
  className?: string;
  img?: string;
  disabled?: boolean;
};

const Button = ({
  onClick,
  type = "button",
  children,
  backgroundColor = "primary-orange",
  fontColor = "primary-white",
  className = "",
  img,
  disabled = false,
}: ButtonProps) => {
  return (
    <button
      onClick={onClick}
      type={type}
      disabled={disabled}
      className={`rounded-lg cursor-pointer px-4 py-1 ${className} bg-${backgroundColor} text-${fontColor}`}
    >
      {img && <img src={img} alt="button icon" className="h-5 w-12 inline" />}
      {children}
    </button>
  );
};

export default Button;
