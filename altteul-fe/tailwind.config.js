/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      fontFamily: {
        pretendard: ['Pretendard Variable', 'sans-serif'],
        firacode: ['Firacode', 'sans-serif'],
        pressStart: ['Press Start 2P', 'sans-serif'],
      },
      colors: {
        'primary-black': '#242A32',
        'primary-white': '#f6f6f6',
        'primary-orange': '#E06C2D',
        'primary-blue': '#50F8FD',
        'secondary-orange': '#BB5F2F',
        'gray-01': '#e0e0e0',
        'gray-02': '#a2a2a2',
        'gray-03': '#5C6269',
        'gray-04': '#323840',
        'gray-05': '#2F3740',
        'gray-06': '#292F37',
        'lang-JV': '#AB805D',
        'lang-PY': '#506C9E',
      },

      fontSize: {
        xs: '0.75rem', // 12px
        sm: '0.875rem', // 14px
        md: '1rem', // 16px
        lg: '1.125rem', // 18px
        xl: '1.25rem', // 20px
        xxl: '1.5rem', // 24px
      },

      // 팀매칭때 유저 좌우 슬라이드 효과
      keyframes: {
        slideLeft: {
          '0%': { transform: 'translateX(0)' },
          '100%': { transform: 'translateX(-5rem)' },
        },
        slideRight: {
          '0%': { transform: 'translateX(0)' },
          '100%': { transform: 'translateX(5rem)' },
        },
      },
      animation: {
        'slide-left': 'slideLeft 1s ease-out forwards',
        'slide-right': 'slideRight 1s ease-out forwards',
      },

      boxShadow: {
        sm: '0 1px 2px 0 rgba(0, 0, 0, 0.05)',
        DEFAULT: '0 0 15px 2px var(--primary-orange), 0 0 15px 2px var(--primary-blue)',
        md: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
        lg: '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
        xl: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)',
        '2xl': '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
        '3xl': '0 35px 60px -15px rgba(0, 0, 0, 0.3)',
        side: '0 0 10px 3px rgba(0, 0, 0, 0.1)',
        passProfile: '0 0 5px 2px rgba(224, 108, 45, 1)',
        inner: 'inset 0 2px 4px 0 rgba(0, 0, 0, 0.06)',
        none: 'none',
        orange: '0 0 15px 2px #E06C2D',
        blue: '0 0 15px 2px #50F8FD',
        white: '0 0 10px 0 #000000',
        gray: '0 0 8px 0 #5C6269',
        diamond: '0 0 30px 8px #FE7D7C',
        platinum: '0 0 30px 8px #AC64FE',
        gold: '0 0 30px 8px #FCE238',
        silver: '0 0 30px 8px #50BCEE',
        bronze: '0 0 30px 8px #D58331',
      },
    },
  },
  safelist: ['shadow-diamond', 'shadow-platinum', 'shadow-gold', 'shadow-silver', 'shadow-bronze'],
  plugins: [require('tailwind-scrollbar'), require('tailwind-scrollbar-hide')],
};
