import { UserGameRecord } from 'types/types';
import { useNavigate, useParams } from 'react-router-dom';
import { ReactNode, useState } from 'react';
import bronze from '@assets/icon/badge/Badge_01.svg';
import silver from '@assets/icon/badge/Badge_04.svg';
import gold from '@assets/icon/badge/Badge_05.svg';
import platinum from '@assets/icon/badge/Badge_07.svg';
import dia from '@assets/icon/badge/Badge_08.svg';
import arrow from '@assets/icon/friend/arrow.svg';
import TeamTabs from '@components/User/TeamNavbar';
import SmallButton from '@components/Common/Button/SmallButton ';
import CodeModal from '@components/User/CodeModal';
import parse, { DOMNode, domToReact } from 'html-react-parser';

type BattleRecordItemProps = {
  record: UserGameRecord;
};

const tierIcons = {
  1: bronze,
  2: silver,
  3: gold,
  4: platinum,
  5: dia,
} as const;

const BattleRecordItem = ({ record }: BattleRecordItemProps) => {
  const navigate = useNavigate();

  const { userId } = useParams();
  const [isOpen, setIsOpen] = useState(false);
  const [codeModalOpen, setCodeModalOpen] = useState(false);
  const [selectedCode, setSelectedCode] = useState(null);
  const [selectedNickname, setSelectedNickname] = useState<string>('');

  const handleCodeClick = (code: string, nickname: string) => {
    setSelectedCode(code);
    setCodeModalOpen(true);
    setSelectedNickname(nickname);
  };

  const handleCloseCodeModal = () => {
    setCodeModalOpen(false);
    setSelectedCode(null);
  };

  const options = {
    replace: (domNode: DOMNode, index: number) => {
      if (
        'type' in domNode &&
        domNode.type === 'tag' &&
        'name' in domNode &&
        domNode.name === 'pre'
      ) {
        return (
          <>
            <h3
              key={`h3-${index}`}
              style={{
                marginTop: '2rem',
              }}
            >
              {domToReact(domNode.children as DOMNode[])}
            </h3>
            <p
              key={`p-${index}`}
              style={{
                marginTop: '2rem',
                marginBottom: '1rem',
              }}
            >
              {domToReact(domNode.children as DOMNode[])}
            </p>
            <img
              key={`img-${index}`}
              style={{
                marginBottom: '1rem',
              }}
            />
            <pre
              key={`pre-${index}`}
              style={{
                marginTop: '2rem',
                backgroundColor: '#292F37',
                padding: '1rem',
                maxWidth: '100%',
                textWrap: 'pretty',
              }}
            >
              {domToReact(domNode.children as DOMNode[])}
            </pre>
          </>
        );
      }
    },
  };

  const isTeam = record.gameType === 'T' ? true : false;
  const isWin = isTeam ? record.myTeam.gameResult === 'FIRST' : record.myTeam.gameResult !== 'FAIL';
  const startedAt = record.startedAt;
  const formattedDate = startedAt.replace('T', ' ').slice(0, 19).replace(/-/g, '.');
  const allPlayers = [
    {
      ...record.myTeam.members[0],
      team: 'myTeam',
      ...record.myTeam,
      members: undefined, // members 키 제거
    },
    ...record.opponents.map(opponent => ({
      ...opponent.members[0],
      team: 'opponent',
      ...opponent,
      // members: undefined, // members 키 제거
    })),
  ].sort((a, b) => a.rank - b.rank);

  // 개인전 유저 순위 목록에 사용
  const sortedPlayers = allPlayers.sort((a, b) => {
    if (a.code === null && b.code !== null) return 1; // 코드가 null인 애는 배열 끝으로
    if (a.code !== null && b.code === null) return -1; // null이 아닌 애들은 rank가 작은 순으로
    return a.rank - b.rank;
  });

  const teamCodeInfo = {
    executeMemory: record.myTeam.executeMemory,
    executeTime: record.myTeam.executeTime,
    code: record.myTeam.code,
  };

  const opponentCodeInfo = {
    executeMemory: record.opponents[0].executeMemory,
    executeTime: record.opponents[0].executeTime,
    code: record.opponents[0].code,
  };

  const gameResultMap: Record<string, string> = {
    FIRST: '1st',
    SECOND: '2nd',
    THIRD: '3rd',
    FOURTH: '4th',
    FIFTH: '5th',
    SIXTH: '6th',
    SEVENTH: '7th',
    EIGHTH: '8th',
    FAIL: '-',
  };

  const result = isTeam ? (isWin ? '승리' : '패배') : isWin ? '해결' : '미해결';

  const renderMembers = () => {
    // 팀전
    if (isTeam) {
      return (
        <div className="flex w-full">
          <div className="flex flex-col w-1/2 gap-0.5 mr-3">
            {record.myTeam.members.map(member => (
              <p
                key={member.userId}
                className={`flex gap-2 ${member.userId === Number(userId) ? 'text-primary-orange' : ''} items-center mb-1`}
              >
                <div className="rounded-full">
                  <img
                    src={`${member.profileImage}`}
                    alt={member.nickname + ' 프로필 이미지'}
                    className="w-[1.5rem] h-[1.5rem] aspect-sqaure rounded-full bg-gray-01"
                  />
                </div>
                {member.nickname}
              </p>
            ))}
          </div>
          <div className="flex flex-col w-1/2 gap-0.5">
            {record.opponents[0].members.map(member => (
              <p key={member.userId} className="flex gap-2 items-center mb-1">
                <div className="rounded-full">
                  <img
                    src={`${member.profileImage}`}
                    alt={member.nickname + ' 프로필 이미지'}
                    className="w-[1.5rem] h-[1.5rem] aspect-sqaure rounded-full bg-gray-01"
                  />
                </div>
                {member.nickname}
              </p>
            ))}
          </div>
        </div>
      );
    } else {
      // 개인전
      return (
        <div className="flex w-full justify-end">
          <div className="flex flex-col max-h-[8rem] flex-wrap gap-0.5">
            {allPlayers.map((member, index) => (
              <div
                key={member.userId}
                className={`flex gap-2 ${index === 0 ? 'font-semibold' : 'font-regular'} ${member.userId === Number(userId) ? 'text-primary-orange' : ''} items-center mb-1`}
              >
                <div className="rounded-full">
                  <img
                    src={`${member.profileImage}`}
                    alt={member.nickname + ' 프로필 이미지'}
                    className="w-[1.5rem] h-[1.5rem] aspect-sqaure rounded-full bg-gray-01"
                  />
                </div>
                <p className={` ${allPlayers.length > 4 && index < 3 ? 'mr-4' : ''}`}>
                  {member.nickname}
                </p>
              </div>
            ))}
          </div>
        </div>
      );
    }
  };

  return (
    <>
      <div
        className={`${isWin ? 'border-primary-orange' : 'border-gray-03'} flex gap-2 rounded-md border-l-8 p-4 bg-gray-05 mb-5 min-h-[10rem] min-w-[50rem] shadow-white`}
      >
        <div className="min-w-[11rem]">
          <div className="after:content-[''] after:block after:w-[50px] after:h-[1px] after:bg-gray-03 after:mt-4 after:mb-4">
            <p className="font-semibold text-primary-orange">
              <span>{isTeam ? '팀전 ' : '개인전'} / </span>
              <span>
                {isTeam
                  ? `${record.myTeam.members.length + record.opponents[0].members.length}인`
                  : `${1 + record.opponents.length}인`}
              </span>
            </p>
            <p className="text-xs">{formattedDate}</p>
          </div>
          <div className="text-sm">
            <p>
              <span className="font-semibold">{result}</span>
              {!isTeam && isWin && (
                <span className="font-semibold">
                  {' '}
                  / {gameResultMap[record.myTeam.gameResult] ?? record.myTeam.gameResult}
                </span>
              )}
            </p>
            <p>{record.myTeam.duration !== 'END' ? record.myTeam.duration : '-'}</p>
          </div>
        </div>
        <div className="grow">
          <p className="mb-3">
            <span
              className={`text-xs ${record.myTeam.lang === 'PY' ? 'bg-lang-PY' : 'bg-lang-JV'} rounded-lg p-1 font-semibold mr-2`}
            >
              {record.myTeam.lang === 'PY' ? 'PYTHON' : 'JAVA'}
            </span>
            <span className="font-semibold">
              {record.problem.problemId}. {record.problem.problemTitle}
            </span>
          </p>
          <div className="min-w-[20rem]">
            {isTeam ? (
              <div>
                <div>아이템 목록</div>
                <div className="flex gap-8 mt-4">
                  <div className="text-sm">
                    <p>실행 시간</p>
                    <p className="font-semibold">
                      {record.myTeam.executeTime ? record.myTeam.executeTime + '초' : '-'}
                    </p>
                  </div>
                  <div className="text-sm">
                    <p>메모리</p>
                    <p className="font-semibold">
                      {record.myTeam.executeMemory
                        ? Math.round(Number(record.myTeam.executeMemory) / 1024 / 1024) + 'MB'
                        : '-'}
                    </p>
                  </div>
                </div>
              </div>
            ) : (
              <div className="text-sm flex gap-8">
                <div>
                  <p>획득한 보너스 점수</p>
                  <p className="font-semibold">{record.myTeam.bonusPoint}점</p>
                </div>
                <div>
                  <p>실행 시간</p>
                  <p className="font-semibold">
                    {record.myTeam.executeTime ? record.myTeam.executeTime + '초' : '-'}
                  </p>
                </div>
                <div>
                  <p>메모리</p>
                  <p className="font-semibold">
                    {record.myTeam.executeMemory
                      ? Math.round(Number(record.myTeam.executeMemory) / 1024 / 1024) + 'MB'
                      : '-'}
                  </p>
                </div>
              </div>
            )}
          </div>
        </div>

        <div className="min-w-[10rem] mr-7" style={{ alignContent: 'center' }}>
          {renderMembers()}
        </div>
        <div className="my-auto">
          <button className="text-primary-orange" onClick={() => setIsOpen(prev => !prev)}>
            {isOpen ? (
              <img src={arrow} alt="접기 버튼" className="-rotate-90" />
            ) : (
              <img src={arrow} alt="펼치기 버튼" className="rotate-90" />
            )}
          </button>
        </div>
      </div>
      {isOpen && (
        <div className="bg-gray-06 -mt-4 min-h-[30rem] min-w-[50rem] mb-5 rounded-md py-4 px-6">
          <div>
            {isTeam ? (
              <div className="flex gap-4 justify-center">
                <div className="grow">
                  <div>
                    <div className="flex justify-between text-sm font-semibold border-b-2 border-gray-03 pb-3">
                      <p>
                        <span>우리 팀</span>
                        <span
                          className={`text-xs ${record.myTeam.lang === 'PY' ? 'bg-lang-PY' : 'bg-lang-JV'} rounded-lg p-1 font-semibold ml-2`}
                        >
                          {record.myTeam.lang === 'PY' ? 'PYTHON' : 'JAVA'}
                        </span>
                      </p>
                      <p>{record.myTeam.duration}</p>
                    </div>
                    <div>
                      {record.myTeam.members.map(member => (
                        <div
                          key={member.userId}
                          className="flex gap-2 bg-gray-05 mb-2 rounded-md mt-2 py-2 px-5 text-sm items-center"
                        >
                          <div className="relative border-2 border-gray-02 rounded-full aspect-square mr-2">
                            <img
                              src={`${member.profileImage}`}
                              alt={member.nickname + ' 프로필 이미지'}
                              className="w-[2.5rem] h-[2.5rem] rounded-full aspect-square"
                            />
                            <div className="absolute -bottom-1 -right-1 w-[1.5rem]">
                              <img
                                src={tierIcons[member.tierId as keyof typeof tierIcons]}
                                alt={`${member.tierId} tier`}
                                className="w-12 aspect-square"
                              />
                            </div>
                          </div>

                          <div>
                            <p
                              className={
                                member.userId === Number(userId)
                                  ? 'text-primary-orange font-semibold text-base'
                                  : 'text-base'
                              }
                            >
                              {member.nickname}
                            </p>
                            <p className="text-gray-02 text-xs">현재 순위 {member.rank}위</p>
                          </div>
                          {member.userId !== Number(userId) ? (
                            <div className="ml-auto">
                              <button onClick={() => navigate(`/users/${member.userId}`)}>
                                <img src={arrow} alt="유저 페이지로 이동" />
                              </button>
                            </div>
                          ) : (
                            ''
                          )}
                        </div>
                      ))}
                    </div>
                  </div>
                </div>

                <div className="grow">
                  <div>
                    <div className="flex justify-between text-sm font-semibold border-b-2 border-gray-03 pb-3">
                      <p>
                        <span>상대 팀</span>
                        <span
                          className={`text-xs ${record.opponents[0].lang === 'PY' ? 'bg-lang-PY' : 'bg-lang-JV'} rounded-lg p-1 font-semibold ml-2`}
                        >
                          {record.opponents[0].lang === 'PY' ? 'PYTHON' : 'JAVA'}
                        </span>
                      </p>
                      <p>{record.opponents[0].duration}</p>
                    </div>
                    <div>
                      {record.opponents[0].members.map(member => (
                        <div
                          key={member.userId}
                          className="flex gap-2 bg-gray-05 mb-2 rounded-md mt-2 py-2 px-5 text-sm items-center"
                        >
                          <div className="relative border-2 border-gray-02 rounded-full aspect-square mr-2">
                            <img
                              src={`${member.profileImage}`}
                              alt={member.nickname + ' 프로필 이미지'}
                              className="w-[2.5rem] h-[2.5rem] rounded-full aspect-square"
                            />
                            <div className="absolute -bottom-1 -right-1 w-[1.5rem]">
                              <img
                                src={tierIcons[member.tierId as keyof typeof tierIcons]}
                                alt={`${member.tierId} tier`}
                                className="w-12 aspect-square"
                              />
                            </div>
                          </div>

                          <div>
                            <p
                              className={
                                member.userId === Number(userId)
                                  ? 'text-primary-orange font-semibold text-base'
                                  : 'text-base'
                              }
                            >
                              {member.nickname}
                            </p>
                            <p className="text-gray-02 text-xs">현재 순위 {member.rank}위</p>
                          </div>
                          {member.userId !== Number(userId) ? (
                            <div className="ml-auto">
                              <button onClick={() => navigate(`/users/${member.userId}`)}>
                                <img src={arrow} alt="유저 페이지로 이동" />
                              </button>
                            </div>
                          ) : (
                            ''
                          )}
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              </div>
            ) : (
              <div>
                {sortedPlayers.map((member, index) => (
                  <div
                    key={member.userId}
                    className="flex gap-4 min-h-[2rem] bg-gray-05 mb-4 p-4 rounded-md items-center"
                  >
                    <p
                      className={`min-w-[4rem] ${member.code !== null ? 'text-semibold' : 'text-gray-03'} text-sm text-center`}
                    >
                      {member.code !== null ? `${index + 1}등` : '미해결'}
                    </p>
                    <div className="flex grow items-center">
                      <div className="flex items-center mr-20">
                        <div className="relative border-2 border-gray-02 rounded-full aspect-square mr-4">
                          <img
                            src={`${member.profileImage}`}
                            alt={member.nickname + ' 프로필 이미지'}
                            className="w-[2.5rem] h-[2.5rem] rounded-full aspect-square"
                          />
                          <div className="absolute -bottom-1 -right-1 w-[1.5rem]">
                            <img
                              src={tierIcons[member.tierId as keyof typeof tierIcons]}
                              alt={`${member.tierId} tier`}
                              className="w-12 aspect-square"
                            />
                          </div>
                        </div>
                        <div>
                          <p
                            className={
                              Number(userId) === member.userId
                                ? 'text-primary-orange font-semibold text-base'
                                : 'text-base'
                            }
                          >
                            {member.nickname}
                          </p>
                          <p className="text-gray-02 text-xs">현재 순위 {member.rank}위</p>
                        </div>
                      </div>
                      <div className="flex grow justify-between items-center">
                        <p>{member.lang}</p>
                        <p>{member.executeTime ? member.executeTime + '초' : '-'}</p>
                        <p>
                          {member.executeMemory
                            ? Math.round(member.executeMemory / 1024 / 1024) + 'MB'
                            : '-'}
                        </p>
                        <SmallButton onClick={() => handleCodeClick(member.code, member.nickname)}>
                          코드
                        </SmallButton>
                      </div>
                    </div>
                    <div className="ml-4 w-8">
                      {member.userId !== Number(userId) && (
                        <button onClick={() => navigate(`/users/${member.userId}`)}>
                          <img src={arrow} alt="유저 페이지로 이동" />
                        </button>
                      )}
                    </div>
                  </div>
                ))}
                <CodeModal
                  code={selectedCode}
                  isOpen={codeModalOpen}
                  onClose={handleCloseCodeModal}
                  nickname={selectedNickname}
                />
              </div>
            )}
          </div>
          <div>
            {isTeam ? (
              <TeamTabs
                problemInfo={record.problem}
                teamCodeInfo={teamCodeInfo}
                opponentCodeInfo={opponentCodeInfo}
              />
            ) : (
              <div className="mt-6">
                <p className="bg-gray-05 rounded-md font-semibold text-center px-4 py-2">
                  문제 정보
                </p>
                <div className="px-4 py-6 bg-gray-05 mt-4 rounded-md">
                  <p className="mb-5">
                    <span>{record.problem.problemId}.</span>
                    <span>{record.problem.problemTitle}</span>
                  </p>
                  <div className='max-w-[50rem] overflow-auto'>{parse(record.problem.description) as ReactNode}</div>
                </div>
              </div>
            )}
          </div>
        </div>
      )}
    </>
  );
};

export default BattleRecordItem;
