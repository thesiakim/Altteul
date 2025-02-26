import UserProfile from '@components/Match/UserProfile';
import useAuthStore from '@stores/authStore';
import useGameStore from '@stores/useGameStore';
import { LocalAudioTrack, RemoteAudioTrack } from 'livekit-client';
import { useEffect, useRef, useState } from 'react';

interface AudioComponentProps {
  track?: LocalAudioTrack | RemoteAudioTrack;
  participantIdentity: string;
}

function AudioComponent({ track, participantIdentity }: AudioComponentProps) {
  const audioElement = useRef<HTMLAudioElement | null>(null);
  const { userId } = useAuthStore();
  const { myTeam } = useGameStore();
  const [isMuted, setIsMuted] = useState(track ? track.isMuted : true);

  // 현재 유저 아이디랑 participantIdentity랑 비교해서 같다면 유저 아이디+프로필 렌더링
  // 유저 정보는 gameStore의 myTeam에서 가져올 것

  const userInfo = myTeam.users.find(user => String(user.userId) === participantIdentity);

  useEffect(() => {
    if (track && audioElement.current) {
      track.attach(audioElement.current);
    }

    return () => {
      track?.detach();
      if (audioElement.current) {
        audioElement.current.srcObject = null;
      }
    };
  }, [track]);

  const toggleMute = () => {
    if (track) {
      if ('setMuted' in track) {
        track.setMuted(!track.isMuted);
        setIsMuted(!track.isMuted);
      } else {
        // RemoteAudioTrack의 경우 로컬에서만 음소거 처리
        setIsMuted(!isMuted);
        if (audioElement.current) {
          audioElement.current.muted = !isMuted;
        }
      }
    }
  };

  return (
    <>
      {track && <audio ref={audioElement} id={track?.sid} muted={isMuted} />}

      {userInfo && (
        <>
          <div onClick={userInfo.userId === userId && toggleMute}>
            <div
              className={`p-1 rounded-full ${isMuted ? 'opacity-30 border-2 border-gray-06' : 'border-2  border-primary-orange'} bg-gray-04 ${userInfo.userId === userId && 'cursor-pointer'}`}
            >
              <UserProfile
                nickname={userInfo.nickname}
                profileImg={userInfo.profileImg}
                tierId={userInfo.tierId}
                isNameShow={false}
                className="w-[2.5rem] h-[2.5rem]"
              />
            </div>

            <p
              className={`${userInfo.userId === userId ? 'text-primary-orange' : ''} font-semibold text-center ${isMuted ? 'text-gray-03' : 'text-gray-01'}`}
            >
              {userInfo.nickname}
            </p>
          </div>
        </>
      )}
    </>
  );
}

export default AudioComponent;
