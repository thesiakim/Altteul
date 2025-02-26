export const mockSingleEnterData = { //처음 방 입장시
    "data": {
        "roomId":"1",      // 입장한 방 id
        "leaderId":"1",   // 방장 id
        "users":[
                    {"userId":1,
                    "nickname":"오리진",
                    "profileImage":"",
                    "tierId":1
                    },
                    {"userId":2,
                    "nickname":"리카스",
                    "profileImage":"",
                    "tierId":2
                    },
                    {"userId":3,
                    "nickname":"콜드캐슬",
                    "profileImage":"",
                    "tierId":3
                    },
        ],
        "message": "OK",
        "status": "200 OK"
  },
};

export const mockUserInData = {   //대기중일 때 신규 상대 등장 시
	"type":"ENTER",
	"data": {
		"roomId":"1",      // 입장한 방 id
		"leaderId":"1",   // 방장 id
		"users":[
                    {"userId":1,
                    "nickname":"오리진",
                    "profileImage":"",
                    "tierId":1
                    },
                    {"userId":2,
                    "nickname":"리카스",
                    "profileImage":"",
                    "tierId":2
                    },
                    {"userId":3,
                    "nickname":"콜드캐슬",
                    "profileImage":"",
                    "tierId":3
                    },
                    {"userId":4,
                    "nickname":"newUser",
                    "profileImage":"",
                    "tierId":7
                    },
        ],
    },
}




export const mockUserOutData = {   //한 유저가 방을 나갔을 때의 data
    "message": "LEAVE",
    "data": {
        "roomId": 3,
        "leaderId": 2,
        "leftUser": {
            "userId": 1,
            "nickname": "오리진",
            "profileImg": "",
            "tierId":1
        },
        "remainingUsers": [
            {"userId":2,
            "nickname":"리카스",
            "profileImage":"",
            "tierId":2
            },
            {"userId":3,
            "nickname":"콜드캐슬",
            "profileImage":"",
            "tierId":3
            },
            {"userId":4,
            "nickname":"newUser",
            "profileImage":"",
            "tierId":7
            },
        ]
    }
}