import useAuthStore from '@stores/authStore';
import { User } from 'types/types';
import UserProfile from '@components/Match/UserProfile';

interface GameUserListProps {
  users: User[];
  completeUsers: number[];
  userProgress: Record<number, number>;
  leftUsers: User[];
}

const GameUserList = ({ users, completeUsers, userProgress, leftUsers }: GameUserListProps) => {
  const { userId } = useAuthStore();

  // 완료된 유저 목록
  const completedUsers = users.filter(user => completeUsers.includes(user.userId));

  // 진행중인 유저 목록
  const inProgressUsers = users.filter(
    user =>
      Object.prototype.hasOwnProperty.call(userProgress, user.userId) &&
      !completeUsers.includes(user.userId)
  );

  return (
    <div className="min-w-[8rem] w-full">
      {/* 진행 중인 유저 */}
      <div className="px-8 py-6 border-b border-gray-04">
        <h3 className="text-sm font-semibold mb-4 text-gray-02">진행 중</h3>
        {inProgressUsers.length > 0 ? (
          <ul>
            {inProgressUsers.map(user => {
              const progress = userProgress[user.userId] || 0;
              return (
                <li key={user.userId} className="flex items-center space-x-2 mb-1 py-3 px-4 pl-2">
                  <UserProfile
                    profileImg={user.profileImg}
                    tierId={user.tierId}
                    nickname={user.nickname}
                    isNameShow={false}
                  />
                  <span
                    className={`font-semibold text-sm ${user.userId === Number(userId) ? 'text-primary-orange' : ''}`}
                  >
                    {user.nickname}
                  </span>
                  <span className="text-xs text-gray-02">{progress}% 완료</span>
                </li>
              );
            })}
          </ul>
        ) : (
          <p className="text-sm text-gray-400">👏 모든 유저가 문제를 풀었습니다.</p>
        )}
      </div>

      {/* 완료된 유저 */}
      <div className="px-8 py-6 border-b border-gray-04">
        <h3 className="text-sm font-semibold mb-4 text-gray-02">완료</h3>
        {completedUsers.length > 0 ? (
          <ul>
            {completedUsers.map(user => (
              <li key={user.userId} className="flex items-center space-x-2 mb-1 py-3 px-4 pl-2">
                <UserProfile
                  profileImg={user.profileImg}
                  tierId={user.tierId}
                  nickname={user.nickname}
                  isNameShow={false}
                />
                <span
                  className={`font-semibold text-sm ${user.userId === Number(userId) ? 'text-primary-orange' : ''}`}
                >
                  {user.nickname}
                </span>
                <span className="text-xs text-gray-02">100% 완료</span>
              </li>
            ))}
          </ul>
        ) : (
          <p className="text-sm text-gray-02 ml-4">🧐 아직 완료한 유저가 없습니다.</p>
        )}
      </div>

      {/* 미해결 유저 */}
      <div className="px-8 py-6">
        <h3 className="text-sm font-semibold mb-6 text-gray-02">미해결</h3>
        {leftUsers.length > 0 ? (
          <ul>
            {leftUsers.map(user => (
              <li
                key={user.userId}
                className="flex items-center space-x-2 mb-1 py-3 px-4 pl-2 text-gray-400"
              >
                <UserProfile
                  profileImg={user.profileImg}
                  tierId={user.tierId}
                  nickname={user.nickname}
                  isNameShow={false}
                />
                <span className="font-semibold text-sm">{user.nickname}</span>
                <span className="text-xs">중간 퇴장</span>
              </li>
            ))}
          </ul>
        ) : (
          <p className="text-sm text-gray-02 ml-4">👌 모두 게임에 참여 중입니다.</p>
        )}
      </div>
    </div>
  );
};

export default GameUserList;
