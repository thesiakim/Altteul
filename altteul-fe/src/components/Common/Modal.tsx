import React from 'react';
import { useLocation, useParams } from 'react-router-dom';

type ModalProps = {
  isOpen: boolean;
  onClose: () => void;
  title?: string;
  children: React.ReactNode; // 내용
  width?: string;
  height?: string;
  className?: string;
  titleColor?: string;
  onReset?: () => void;
  minWidth?: string;
  minHeight?: string;
};

const Modal = ({
  isOpen,
  onClose,
  title,
  children,
  minWidth = '28rem',
  width,
  height,
  minHeight = '15rem',
  className = '',
  titleColor = '',
  onReset,
}: ModalProps) => {
  const params = useParams();
  const location = useLocation();
  if (!isOpen) return null; // isOpen이 false이면 모달을 렌더링하지 않음

  const handleClose = () => {
    onClose();
    if (onReset) onReset();
  };

  const isAuth = location.pathname.length === 1;
  const isEditUserInfo = location.pathname.includes(`/users/${params.userId}`);

  return (
    <div
      className={`fixed inset-0 z-20 bg-black bg-opacity-55 flex justify-center items-center`}
      onClick={isAuth || isEditUserInfo ? handleClose : undefined}
    >
      <div
        className={`flex flex-col items-center text-primary-black rounded-2xl p-5 overflow-auto ${className} z-100`.trim()}
        onClick={e => e.stopPropagation()}
        style={{ minWidth, minHeight, zIndex: 999 }}
      >
        {title && (
          <h2 className={`text-xxl font-bold text-center mt-4 text-${titleColor}`}>{title}</h2>
        )}
        <div className="flex flex-col items-center gap-5">{children}</div>
      </div>
    </div>
  );
};

export default Modal;
