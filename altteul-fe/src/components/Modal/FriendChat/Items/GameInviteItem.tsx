// src/components/Modal/FriendChat/Items/GameInviteItem.tsx
import { useSocketStore } from '@stores/socketStore';

export interface GameInvite {
  id: number;
  userId: number;
  nickname: string;
  profileImg: string;
  gameType: string;
  invitedAt: string;
}

interface GameInviteItemProps {
  invite: GameInvite;
  onRefresh: () => void;
}

const GameInviteItem = ({ invite, onRefresh }: GameInviteItemProps) => {
  const { sendMessage } = useSocketStore();

  const handleAccept = () => {
    sendMessage('/pub/game/invite/response', {
      inviteId: invite.id,
      inviterId: invite.userId,
      status: 'ACCEPTED'
    });
    onRefresh();
  };

  const handleReject = () => {
    sendMessage('/pub/game/invite/response', {
      inviteId: invite.id,
      inviterId: invite.userId,
      status: 'REJECTED'
    });
    onRefresh();
  };

  return (
    <div className="flex items-center justify-between bg-gray-04 p-3 rounded-lg hover:bg-gray-03 transition-colors">
      <div className="flex items-center gap-3">
        <img src={invite.profileImg} alt="프로필" className="w-10 h-10 rounded-full" />
        <div>
          <p className="font-semibold text-primary-white">{invite.nickname}</p>
          <p className="text-sm text-gray-400">{invite.gameType} 게임에 초대했습니다</p>
        </div>
      </div>
      <div className="flex gap-2">
        <button
          onClick={handleAccept}
          className="px-3 py-1 bg-primary-orange text-white rounded hover:bg-primary-orange/80"
        >
          참가
        </button>
        <button
          onClick={handleReject}
          className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-500/80"
        >
          거절
        </button>
      </div>
    </div>
  );
};

export default GameInviteItem;