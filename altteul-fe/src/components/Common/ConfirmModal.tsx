interface ConfirmModalProps {
  onConfirm: () => void;
  onCancel: () => void;
}

const ConfirmModal = ({ onConfirm, onCancel }: ConfirmModalProps) => {
  return (
    <div
      className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
      style={{ zIndex: 51 }}
    >
      <div className="bg-primary-black p-8 rounded-md shadow-side w-[30rem] shadow-gray-03 text-center">
        <h2 className="text-lg font-semibold text-primary-white">정말 게임을 나가시겠습니까?</h2>
        <p className="text-gray-02 mt-2">나가기를 선택하면 진행 중인 게임에서 패배하게 됩니다.</p>
        <div className="mt-6 flex justify-center space-x-4">
          <button
            onClick={onCancel}
            className="px-4 py-2 border border-gray-04 rounded-md text-gray-02 hover:bg-gray-03 transition-colors"
          >
            취소
          </button>
          <button
            onClick={onConfirm}
            className="px-4 py-2 bg-primary-orange text-primary-white rounded-md hover:bg-secondary-orange transition-colors"
          >
            확인
          </button>
        </div>
      </div>
    </div>
  );
};

export default ConfirmModal;
