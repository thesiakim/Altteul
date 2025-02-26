import BattleRecord from '@components/User/BattleRecord';
import UserInfo from '@components/User/UserInfo';
import backgroundImage from '@assets/background/myPageBg.svg';

const UserPage = () => {
  return (
    <div
      className="min-h-screen bg-fixed bg-cover bg-center bg-no-repeat -mt-[3.5rem] pt-[5rem]"
      style={{ backgroundImage: `url(${backgroundImage})` }}
    >
      {/* 배경 오버레이 (어두운 필터) */}
      <div className="min-h-screen fixed inset-0 bg-black/70"></div>

      {/* 실제 컨텐츠는 오버레이보다 위에 위치 */}
      <div className="relative w-full max-w-[60rem] mx-auto">
        <UserInfo />
        <BattleRecord />
      </div>
    </div>
  );
};

export default UserPage;
