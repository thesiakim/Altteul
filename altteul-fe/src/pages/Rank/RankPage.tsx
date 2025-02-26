import React, { useState, useEffect, useMemo } from 'react';
import { useInView } from 'react-intersection-observer';
import rank_page_bg from '@assets/background/rank_page_bg.svg';
import Dropdown from '@components/Common/Dropdown';
import RankingItem from '@components/Ranking/RankingItem';
import { getRank } from '@utils/Api/rankApi';
import type { RankApiFilter, Ranking, RankingResponse } from 'types/types';
import { badges, BadgeFilter } from '@components/Ranking/BadgeFilter';
import Input from '@components/Common/Input';

// 메인 랭킹 페이지 컴포넌트
const RankingPage = () => {
  const [searchNickname, setSearchNickname] = useState('');
  const [selectedLanguage, setSelectedLanguage] = useState('');
  const [selectedTier, setSelectedTier] = useState<number | null>(null);
  const [rankings, setRankings] = useState<Array<Ranking>>([]);
  const [page, setPage] = useState(0);
  const [last, setLast] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [ref, inView] = useInView({
    threshold: 0,
    delay: 500,
  });

  const languageOptions = [
    { id: 0, value: '', label: '전체' },
    { id: 1, value: 'PY', label: 'Python' },
    { id: 2, value: 'JV', label: 'Java' },
  ];

  const filter: RankApiFilter = {
    page,
    size: 10,
    lang: selectedLanguage,
    tierId: selectedTier,
    nickname: searchNickname,
  };

  const resetPagination = () => {
    setPage(0);
    setRankings([]);
    setLast(false);
  };

  const fetchRankList = async () => {
    if (isLoading || last) return;
    try {
      setIsLoading(true);
      const rankingResponse: RankingResponse = await getRank(filter);
      // console.log(rankingResponse);
      setLast(rankingResponse.data.last);
      setRankings(prev => [...prev, ...rankingResponse.data.rankings]);
      setPage(prevPage => prevPage + 1);
    } catch (error) {
      console.error('Failed to fetch rankings:', error);
    } finally {
      setIsLoading(false);
    }
  };

  // 무한 스크롤
  useEffect(() => {
    if (inView) {
      fetchRankList();
    }
  }, [inView]);

  // 티어 선택 핸들러
  const handleTier = (tierId: number) => {
    // console.log(tierId);
    setSelectedTier(prev => (prev === tierId ? null : tierId));
    resetPagination();
  };

  const handleSearch = () => {
    // console.log(searchNickname);
    resetPagination();
  };

  //엔터 눌렀을 때 이벤트
  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  return (
    <div className="relative min-h-screen">
      {/* 배경 이미지 */}
      <div
        className="fixed inset-0 w-full h-full z-0"
        style={{
          backgroundImage: `url(${rank_page_bg})`,
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          backgroundAttachment: 'fixed',
          opacity: 0.5,
        }}
      />

      <div className="relative z-10 max-w-6xl mx-auto pt-[2.5rem] w-[50vw] pl-10">
        <div className="flex justify-between items-center mb-2 mt-12">
          <div className="flex space-x-3">
            {badges
              .slice(1)
              .reverse()
              .map(badge => (
                <BadgeFilter
                  key={badge.id}
                  tierId={badge.id}
                  selectedTier={selectedTier} // 추가
                  onClick={handleTier}
                />
              ))}
          </div>

          {/* 언어 및 닉네임 검색 */}
          <div className="flex gap-3 pb-1">
            <Dropdown
              options={languageOptions}
              value={selectedLanguage}
              onChange={setSelectedLanguage}
              width="7rem"
            />
            <Input
              value={searchNickname}
              onChange={e => setSearchNickname(e.target.value)}
              onKeyDown={handleKeyPress}
              onButtonClick={handleSearch}
              showMagnifier={true}
              placeholder="닉네임 검색"
              name="userRank"
              className="!w-[13rem] px-4 bg-gray-03 !border border-gray-01 rounded-lg !h-[2.42rem]"
            />
          </div>
        </div>

        {/* 랭킹 리스트 */}
        <div>
          <div className="rounded-t-lg grid grid-cols-[0.8fr_1.7fr_0.8fr_1fr_1fr_1fr] py-4 bg-primary-black gray-01 text-center text-balance">
            <div>순위</div>
            <div>Player</div>
            <div>순위변동</div>
            <div>랭킹점수</div>
            <div>선호언어</div>
            <div>평균통과율</div>
          </div>
          <>
            {rankings.map((ranking: Ranking) => (
              <RankingItem
                key={`${ranking.userId}-${ranking.ranking}`}
                data={ranking}
                className={`grid grid-cols-[0.8fr_1.7fr_0.8fr_1fr_1fr_1fr] py-4 bg-primary-black/40 text-center text-balance border-b-[1px]`}
              />
            ))}
          </>

          {isLoading && <div className="text-center py-4 text-primary-white">불러오는 중...</div>}
        </div>

        {!last && <div ref={ref} className="h-20" />}
      </div>
    </div>
  );
};

export default RankingPage;
