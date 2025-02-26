import React from 'react';

type BaseModalProps = {
  isOpen: boolean;
  onClose: () => void;
  children: React.ReactNode;
  showBackButton?: boolean;
};

const BaseModal = ({ isOpen, onClose, children }: BaseModalProps) => {
  if (!isOpen) return null;

  return (
    <div
      className="fixed right-4 bottom-4 flex  bg-primary-black bg-opacity-50 z-[9999]"
      onClick={onClose}
    >
      <div
        className="bg-gray-06 border-2 border-primary-orange rounded-lg w-[25vw] min-w-[24rem] h-[85vh] p-4 relative"
        onClick={e => e.stopPropagation()}
      >
        
        {children}
      </div>
    </div>
  );
};

export default BaseModal;
