import { getUserInfo } from '@utils/Api/userApi';
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { UserInfo as UserInfoType } from 'types/types';
import bronze from '@assets/icon/badge/Badge_01.svg';
import silver from '@assets/icon/badge/Badge_04.svg';
import gold from '@assets/icon/badge/Badge_05.svg';
import platinum from '@assets/icon/badge/Badge_07.svg';
import diamond from '@assets/icon/badge/Badge_08.svg';
import LoadingSpinner from '@components/Common/LoadingSpinner';
import useModalStore from '@stores/modalStore';
import UserProfile from '@components/Match/UserProfile';

const tierIcons = {
  bronze: bronze,
  silver: silver,
  gold: gold,
  platinum: platinum,
  diamond: diamond,
} as const;

const UserInfo = () => {
  const { userId } = useParams();
  const [userInfo, setUserInfo] = useState<UserInfoType | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const { openModal, profileUpdated, setProfileUpdated } = useModalStore();
  const [isCurrentUser, setIsCurrentUser] = useState(false);

  useEffect(() => {
    // 현재 로그인한 사용자와 조회 중인 사용자가 같은지 확인
    const currentUserId = sessionStorage.getItem('userId');
    setIsCurrentUser(currentUserId === userId);
    
    const fetchUserInfo = async () => {
      try {
        setIsLoading(true);

        const response = await getUserInfo(userId);
        const data = response.data;
        setUserInfo(data);
      } catch (error) {
        console.error(error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchUserInfo();
  }, [userId, profileUpdated, setProfileUpdated]);

  const handleEditProfile = () => {
    if (isCurrentUser) {
      openModal('edit-profile', { nickname: userInfo.nickname, profileImg: userInfo.profileImg });
    }
  };

  if (isLoading) return <LoadingSpinner loading={isLoading} />;

  if (!userInfo)
    return (
      <div>
        <p>유저 정보를 불러올 수 없습니다.</p>
      </div>
    );

  return (
    <div className="mb-10">
      <div className="relative w-24 mx-auto">
        {isCurrentUser ? (
          // 본인 프로필일 때: 버튼으로 수정 가능
          <button
            className="relative w-24 h-24 rounded-full overflow-hidden border-2 border-gray-01 hover:border-primary-orange cursor-pointer"
            onClick={handleEditProfile}
          >
            <img
              src={userInfo.profileImg}
              alt="Profile"
              className="w-full h-full object-cover"
            />
            <div className="absolute inset-0 bg-black bg-opacity-40 flex items-center justify-center opacity-0 hover:opacity-100 transition-opacity">
              <span className="text-primary-white text-sm font-bold">정보 수정</span>
            </div>
          </button>
        ) : (
          // 다른 사용자 프로필일 때: 단순 이미지만 표시
          <div className="relative w-24 h-24 rounded-full overflow-hidden border-2 border-gray-01">
            <img
              src={userInfo.profileImg}
              alt="Profile"
              className="w-full h-full object-cover"
            />
          </div>
        )}

        <div className="absolute -bottom-2 -right-2 rounded-full">
          <img
            src={tierIcons[userInfo.tierName.toLowerCase() as keyof typeof tierIcons]}
            alt={`${userInfo.tierName} tier`}
            className="w-12 aspect-square"
          />
        </div>
      </div>

      <div className="flex flex-col items-center">
        <div>
          <h2 className="text-xl font-bold">{userInfo.nickname}</h2>
          <span className="text-gray-02">@{userInfo.username}</span>
        </div>

        <div className="text-center">
          <div>
            <span className="font-md">상위 {userInfo.rankPercentile}%</span>
          </div>
          <p className="font-medium">
            현재 등수 {userInfo.rank}위
            {userInfo.rankChange > 0 && (
              <span className="text-primary-orange ml-1">(▲{userInfo.rankChange})</span>
            )}
            {userInfo.rankChange === 0 && <span className="text-gray-03 ml-1">(-)</span>}
            {userInfo.rankChange < 0 && (
              <span className="text-gray-03 ml-1">(▼{Math.abs(userInfo.rankChange)})</span>
            )}
          </p>
        </div>
      </div>
    </div>
  );
};

export default UserInfo;