import rank_up from "@assets/icon/rank_up.svg";
import rank_down from "@assets/icon/rank_down.svg";
import {Ranking} from "types/types";
import { badges } from "./BadgeFilter";
import { useNavigate } from "react-router-dom";

// 랭킹 행 컴포넌트
const RankingItem = ({ data, className }: { data: Ranking, className: string }) => {
  const { userId, nickname, lang, ranking, point, tierId, rankChange, rate } = data;
  const navigate = useNavigate();

  const formatNumber = (num: number) => {
    return num ? num.toLocaleString() : '0';
  };

  const getLanguageDisplay = (lang: string) => {
    return lang === "JV" ? "Java" : "Python";
  };

  const getRankChangeDisplay = () => {
    if (rankChange === 0) return <div>-</div>;
    if (rankChange > 0)
      return (
        <div className="flex justify-center">
          <div>(</div>
          <img src={rank_up} alt="상승" />
          <div>{rankChange})</div>
        </div>
      );
    return (
      <div className="flex justify-center">
        <div>(</div>
        <img src={rank_down} alt="하강" className="" />
        <div>{Math.abs(rankChange)})</div>
      </div>
    );
  };

  const getBadgeImage = () => {
    const badge = ranking === 1 ? badges[0] : badges[tierId];
    return <img src={badge.src} alt={badge.name} className="w-8 h-8" />;
  };

  return (
    // 한 행
    <div className={className}>
      {/* 순위 */}
      <div>{ranking}</div>
      {/* 뱃지&닉네임 */}
      <div className="px-16 text-left" onClick={() => navigate(`/users/${userId}`)}>
        <div className="flex gap-3 hover:cursor-pointer">
          <div>{getBadgeImage()}</div>
          <div>{nickname}</div>
        </div>
      </div>
      {/* 순위변동 */}
      <div>{getRankChangeDisplay()}</div>
      {/* 랭킹점수 */}
      <div>{formatNumber(point)}</div>
      {/* 선호언어 */}
      <div>{getLanguageDisplay(lang)}</div>
      <div>{`${Math.round(rate)}%`}</div>
    </div>
  );
};

export default RankingItem;
