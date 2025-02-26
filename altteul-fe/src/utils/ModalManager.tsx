// src/components/common/Modal/ModalManager.tsx
import SignUpModal from '@components/Modal/Auth/SignUpModal';
import LoginModal from '@components/Modal/Auth/LoginModal';
import ResultModal from '@components/Modal/Result/ResultModal';
import useModalStore from '@stores/modalStore';
import { MODAL_TYPES } from 'types/modalTypes';
import AdditionalModal from '@components/Modal/Result/AdditionalModal';
import NavigateModal from '@components/Modal/Result/NavigateModal';
import ResultDetailModal from '@components/Modal/Result/ResultDetailModal';
import MainModal from '@components/Modal/FriendChat/MainModal';
import EditProfileModal from '@components/Modal/Auth/EditProfileModal';

const ModalManager = () => {
  const { closeModal, isOpen, getModalInfo } = useModalStore();

  // 모달 정보 가져오기
  const modalInfo = getModalInfo();

  return (
    <>
      {/* 인증 관련 모달 */}
      <SignUpModal isOpen={isOpen(MODAL_TYPES.SIGNUP)} onClose={() => closeModal()} />
      <LoginModal isOpen={isOpen(MODAL_TYPES.LOGIN)} onClose={() => closeModal()} />

      {/* 회원정보 수정 모달 */}
      <EditProfileModal isOpen={isOpen(MODAL_TYPES.EDIT)} onClose={() => closeModal()} />

      {/* 게임 결과 관련 모달 */}
      <ResultModal
        isOpen={isOpen(MODAL_TYPES.RESULT)}
        onClose={() => closeModal()}
        type={modalInfo?.type}
        result={modalInfo?.result}
      />

      {/* 개인전 결과 리스트 모달 */}
      <ResultDetailModal isOpen={isOpen(MODAL_TYPES.LIST)} onClose={() => closeModal()} />

      {/* 게임 네비게이션 모달 */}
      <NavigateModal
        isOpen={isOpen(MODAL_TYPES.NAVIGATE)}
        onClose={() => closeModal()}
        type={modalInfo?.type}
      />

      {/* 공통 모달 (코드/코칭) */}
      <AdditionalModal
        isOpen={isOpen(MODAL_TYPES.COMMON)}
        onClose={() => closeModal()}
        type={modalInfo?.type}
        modalType={modalInfo?.modalType}
      />

      {/* 채팅 모달 */}
      <MainModal isOpen={isOpen(MODAL_TYPES.MAIN)} onClose={() => closeModal()} />
    </>
  );
};

export default ModalManager;
