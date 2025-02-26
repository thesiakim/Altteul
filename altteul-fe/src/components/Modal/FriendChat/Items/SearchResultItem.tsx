import { useSocketStore } from '@stores/socketStore';
import { requestFriend } from '@utils/Api/friendChatApi';
import { useState } from 'react';
import freindRequest from "@assets/icon/friend/freindRequestIcon.svg"
import success from "@assets/icon/friend/success.svg"
import fail from "@assets/icon/friend/fail.svg"
import { toast } from 'react-toastify';

interface SearchResultItemProps {
  user: {
    userId: number;
    nickname: string;
    profileImg: string;
    isOnline: boolean;
  };
}

const SearchResultItem = ({ user }: SearchResultItemProps) => {
  const { sendMessage } = useSocketStore();
  const [isClick, setIsClick] = useState(false);
  const [isError, setIsError] = useState(false);

  const handleRequestFriend = async () => {
    try{
      const res = await requestFriend(user.userId);
      sendMessage('/pub/friend/request', {
        toUserId: user.userId,
      });
      setIsClick(true);
    }catch(error){
      setIsError(true);
      toast.error('친구 요청을 받았거나 요청한 유저입니다.', {
        position: "bottom-center",
        autoClose: 3000
      });
    }
  };

  return (
    <div className="flex items-center justify-between bg-gray-04 p-3 rounded-lg">
      <div className="flex items-center gap-3">
        <div className="relative">
          <img src={user.profileImg} alt="프로필" className="w-10 h-10 rounded-full" />
          <div
            className={`absolute top-0 right-0 w-3 h-3 rounded-full border-2 ${
              user.isOnline ? 'bg-green-500' : 'bg-gray-400'
            }`}
          />
        </div>
        <p className="font-semibold text-primary-white">{user.nickname}</p>
      </div>

      <div className="flex gap-2">
        <button
          onClick={handleRequestFriend}
          disabled={isClick}
        >
          {!isClick && <img src={freindRequest} alt="초대" className='w-8 h-8' />}
          {isClick && !isError && <img src={success} alt="성공" className='w-8 h-8' />}
          {isClick && isError && <img src={fail} alt="실패" className='w-8 h-8' />}
        </button>
      </div>
    </div>
  );
};

export default SearchResultItem;