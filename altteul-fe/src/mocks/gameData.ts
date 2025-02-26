export const mockGameData = {
  type: "GAME_START",
  data: {
    gameId: 1,
    leaderId: 23,
    users: [
      { userId: 2, nickName: "알고리즘짱", profileImg: "이미지Byte", tierId: 1 },
      { userId: 3, nickName: "알뜰짱", profileImg: "이미지Byte", tierId: 2 },
      { userId: 4, nickName: "잠온다능", profileImg: "이미지Byte", tierId: 3 },
    ],
    problem: {
      problemId: 3,
      problemTitle: "물류 창고 로봇",
      description: "물류 창고 로봇이 있다 어쩌구 저쩌구",
    },
    testcases: [
      { testcaseId: 24, number: 1, input: "~~", output: "~~" },
      { testcaseId: 25, number: 2, input: "~~", output: "~~" },
    ],
  },
};
