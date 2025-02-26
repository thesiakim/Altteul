import { api } from '@utils/Api/commonApi';

export const createToken = async (roomId: number, userId: number) => {
  try {
    const response = await api.post('openvidu/token', {
      roomName: roomId,
      participantName: userId,
    });

    if (response.status === 200) {
      const token = response?.data.data.token;
      return token;
    }
  } catch (error) {
    console.error(error);
    return null;
  }
};
