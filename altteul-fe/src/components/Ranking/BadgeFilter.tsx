import { useState } from 'react';
import bronze from '@assets/icon/badge/Badge_01.svg';
import silver from '@assets/icon/badge/Badge_04.svg';
import gold from '@assets/icon/badge/Badge_05.svg';
import platinum from '@assets/icon/badge/Badge_07.svg';
import dia from '@assets/icon/badge/Badge_08.svg';
import numberOne from '@assets/icon/badge/Badge_09.svg';

// 뱃지 목록
const badges = [
  { id: 0, name: 'numberOne', src: numberOne },
  { id: 1, name: 'bronze', src: bronze },
  { id: 2, name: 'silver', src: silver },
  { id: 3, name: 'gold', src: gold },
  { id: 4, name: 'platinum', src: platinum },
  { id: 5, name: 'diamond', src: dia },
];

interface BadgeFilterProps {
  tierId: number;
  selectedTier: number | null;
  onClick: (tierId: number) => void;
}

// 개별 뱃지 컴포넌트
const BadgeFilter = ({ tierId, selectedTier, onClick }: BadgeFilterProps) => {
  const badge = badges.find(b => b.id === tierId);
  if (!badge) return null;

  const isSelected = selectedTier === tierId;

  return (
    <div className="relative">
      <img
      
        key={badge.id}
        src={badge.src}
        alt={badge.name}
        className={`relative z-10 cursor-pointer transition-transform ${
          isSelected ? 'scale-110' : 'hover:scale-110'
        }`}
        onClick={() => onClick(tierId)}
      />
      {/* 선택됐을 때 네온 효과 */}
      {isSelected && <div className={`absolute top-3 right-3 shadow-${badge.name} w-8 h-8`} />}
    </div>
  );
};

export { badges, BadgeFilter };
