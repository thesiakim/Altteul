import SmallButton from '@components/Common/Button/SmallButton ';
import Modal from '@components/Common/Modal';
import noCodeImg from '@assets/icon/no_code.svg';
const CodeModal = ({
  code,
  isOpen,
  onClose,
  nickname,
}: {
  code: string;
  isOpen: boolean;
  onClose: () => void;
  nickname: string;
}) => {
  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      width="40rem"
      height="30rem"
      title={`${nickname}님이 제출한 코드`}
      titleColor="primary-white"
      className="bg-primary-black relative overflow-hidden border-2 border-primary-orange shadow-orange"
    >
      <div className="p-2 bg-gray-05 text-primary-white rounded-md w-[35rem] h-[20rem] flex items-center justify-center">
        {code ? (
          <pre className="whitespace-pre-wrap max-h-[18rem] overflow-auto">{code}</pre>
        ) : (
          <div className="flex flex-col items-center justify-center bg-gray-05 p-4 rounded-md">
            <img src={noCodeImg} alt="코드 정보 없음" className="w-[3rem]" />
            <p className="p-4 text-center">코드를 제출하지 않았습니다.</p>
          </div>
        )}
      </div>
      <SmallButton onClick={onClose} backgroundColor="primary-orange" className="py-2">
        이전으로
      </SmallButton>
    </Modal>
  );
};

export default CodeModal;
