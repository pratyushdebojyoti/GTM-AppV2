/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        obsidian: {
          canvas: '#050508',
          surface: '#0D111A',
          variant: '#131825',
          elevated: '#1B2234',
        },
        electric: {
          cyan: '#38BDF8',
          cyanDark: '#0284C7',
        },
        cyber: {
          blue: '#3B82F6',
        },
        indigo: {
          soft: '#818CF8',
        },
        status: {
          emerald: '#34D399',
          amber: '#FBBF24',
          crimson: '#F87171',
        },
        glass: {
          border: 'rgba(255, 255, 255, 0.12)',
          subtle: 'rgba(255, 255, 255, 0.05)',
          surface: 'rgba(13, 17, 26, 0.75)',
        }
      },
      fontFamily: {
        sans: ['-apple-system', 'BlinkMacSystemFont', 'SF Pro Display', 'Inter', 'Segoe UI', 'Roboto', 'sans-serif'],
      },
      backdropBlur: {
        xs: '2px',
      }
    },
  },
  plugins: [],
}
