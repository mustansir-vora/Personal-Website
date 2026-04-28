# Personal Website / Portfolio

A stunning, futuristic, and accessible personal portfolio website built with modern web technologies. The site is designed to be fully responsive, desktop-first but mobile-friendly, and optimized for high performance and visual polish.

## 🛠️ Technical Stack

- **Framework**: [Next.js](https://nextjs.org/) v16 (App Router)
- **Language**: [TypeScript](https://www.typescriptlang.org/)
- **Styling**: [Tailwind CSS v4](https://tailwindcss.com/) (using glassmorphism, gradients, and custom utility configurations)
- **Animations**: [Framer Motion](https://www.framer.com/motion/)
- **Icons**: [Lucide React](https://lucide.dev/)
- **Deployment**: Configured for Static HTML Export (`next build` output to `/out`)

## 🎨 Design System

The visual aesthetic is "Tech-Futurism meets Elegance". It heavily utilizes:
- **Glassmorphism**: Blurred, semi-transparent backgrounds for cards and interactive elements.
- **Gradients**: Subtle radial and linear gradients for backgrounds and text.
- **Typography**: Outfit (Primary sans font used across the website).

## 🚀 Getting Started

### Prerequisites
- [Node.js](https://nodejs.org/) (v18 or higher recommended)
- `npm` (or your preferred package manager like `yarn`, `pnpm`, `bun`)

### Installation & Setup

1. **Clone the repository** (if you haven't already)
2. **Install dependencies**:
   ```bash
   npm install
   ```
3. **Run the development server**:
   ```bash
   npm run dev
   ```
4. **Open in browser**: Navigate to [http://localhost:3000](http://localhost:3000)

## 📦 Building for Production

This project is configured as a static export (see `next.config.ts`). 

To generate a static build:
```bash
npm run build
```

This will create an `out` directory containing the optimized HTML, CSS, JavaScript, and assets. You can deploy this directory to any static hosting provider (e.g., GitHub Pages, Vercel, Netlify, AWS S3).
