// 닫기 버튼, 뒤로가기 버튼
import backIcon from '@assets/icon/friend/back.svg';
import exitlineIcon from '@assets/icon/friend/exit_line.svg';

type ModalHeaderProps = {
  showBackButton?: boolean;
  onClose: () => void;
  onBack?: () => void;
};

const ModalHeader = ({
  showBackButton,
  onClose,
  onBack,
}: ModalHeaderProps) => {

  return (
    <>
      {/* 뒤로가기 버튼 */}
      {showBackButton && (
        <button
          onClick={onBack}
          className="absolute top-7 right-5 text-primary-orange hover:opacity-80"
        >
          <img src={backIcon} alt="뒤로가기" className="w-6 h-6" />
        </button>
      )}

      {/* 닫기 버튼 */}
      {!showBackButton && <button
        onClick={onClose}
        className="absolute top-0 right-3 text-primary-orange hover:opacity-80"
      >
        <img src={exitlineIcon} alt="닫기" className="w-8 h-10" />
      </button>}
    </>
  );
};

export default ModalHeader;
