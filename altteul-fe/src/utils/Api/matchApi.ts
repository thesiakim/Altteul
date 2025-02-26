//매칭관련 API(싱글이든 팀이든)
import { sigleApi, teamApi } from '@utils/Api/commonApi';
import axios from 'axios';

//매칭 입장시 사용 api
export const matchRoomEnter = async (type: string) => {
  try {
    const res = type === 'single' ? await sigleApi.post('enter') : await teamApi.post('enter');
    if (res.data.status === 200) {
      return res.data;
    }
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      // console.log("개인전 입장 error: ", (error.response.data as { message: string }).message)
    }
  }
};

//싱글 매칭 퇴장시 사용 api
export const singleOut = async (roomId: number) => {
  const res = await sigleApi.post(`leave/${roomId}`);
  return res.data.status;
};

//싱글 매칭 시작시 사용 api
export const singleStart = async (roomId: number) => {
  const res = await sigleApi.post(`start/${roomId}`);
  return res;
};

//팀전 퇴장 api
export const teamOut = async (roomId: number) => {
  const res = await teamApi.post(`leave/${roomId}`);
  return res.data.status;
};

//팀전 매칭 시작 api
export const teamStart = async (roomId: number) => {
  const res = await teamApi.post(`/matching/${roomId}`);
};

//팀전 매칭 취소 api
export const cancelTeamMatch = async (roomId: number) => {
  const res = await teamApi.post(`matching/cancel/${roomId}`);
};

//팀전 초대 수락 api
export const inviteResponse = async (nickname: string, roomId: number, accepted: boolean) => {
  const data = { nickname: nickname, roomId: roomId, accepted: accepted };
  const res = await teamApi.post('invite/reaction', data);
  return res.data;
};
