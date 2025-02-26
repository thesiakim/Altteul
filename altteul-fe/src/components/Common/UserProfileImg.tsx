import bronze from '@assets/icon/badge/Badge_01.svg';
import silver from '@assets/icon/badge/Badge_04.svg';
import gold from '@assets/icon/badge/Badge_05.svg';
import platinum from '@assets/icon/badge/Badge_07.svg';
import dia from '@assets/icon/badge/Badge_08.svg';
import baseImage from '@assets/icon/People.svg';

const tierIcons = {
  1: bronze,
  2: silver,
  3: gold,
  4: platinum,
  5: dia,
} as const;

interface UserProfileImg {
  profileImg: string | null;
  tierId: number;
  customClass?: string;
}

const UserProfileImg = ({ profileImg = null, tierId, customClass }: UserProfileImg) => {
  return (
    <div className={`relative border border-gray-02 rounded-full aspect-square p-2 ${customClass}`}>
      <img
        src={profileImg ? profileImg : baseImage}
        alt={'유저 프로필 이미지'}
        className="w-[1.8rem] max-w-full"
      />
      <div className="absolute -bottom-1 -right-1 w-[1.5rem]">
        <img
          src={tierIcons[tierId as keyof typeof tierIcons]}
          alt={`${tierId} tier`}
          className="w-12 aspect-square"
        />
      </div>
    </div>
  );
};

export default UserProfileImg;
