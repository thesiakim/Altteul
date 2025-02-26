// src/hooks/useTimer.ts
import { useEffect, useState } from 'react';

interface UseTimerProps {
  initialSeconds: number;
  onComplete?: () => void;
}

export const useTimer = ({ initialSeconds, onComplete }: UseTimerProps) => {
  const [seconds, setSeconds] = useState(initialSeconds);
  const [isRunning, setIsRunning] = useState(true);

  useEffect(() => {
    if (!isRunning) return;

    const timer = setInterval(() => {
      setSeconds((prev) => {
        if (prev <= 1) {
          clearInterval(timer);
          setIsRunning(false);
          onComplete?.();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [isRunning, onComplete]);

  const pause = () => setIsRunning(false);  //타이머 일시정지
  const resume = () => setIsRunning(true);  //타이머 재개
  //타이머 리셋
  const reset = () => {
    setSeconds(initialSeconds);
    setIsRunning(true);
  };

  return {
    seconds,
    isRunning,
    pause,
    resume,
    reset
  };
};