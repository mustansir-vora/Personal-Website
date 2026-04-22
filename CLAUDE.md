# AGENTS.md — Claude Coding Guidelines for Personal Website v2 (Next.js)

## The Mission

Build a stunning, futuristic, and accessible personal portfolio website using **Next.js 14+**, **Tailwind CSS**, and **Framer Motion**. The site must be fully responsive, desktop-first but mobile-friendly, and optimized for high performance and visual polish.

## 🛠️ Technical Stack

- **Framework**: Next.js 14+ (App Router)
- **Language**: TypeScript
- **Styling**: Tailwind CSS (custom config with gradients, blurs, glassmorphism)
- **Animations**: Framer Motion
- **Icons**: Lucide React
- **Fonts**: Inter (primary), Orbitron (headlines), Space Mono (code/tech)
- **Deployment**: Vercel (optimized for static export or serverless)

---

## 🎨 Visual & Design System

### 1. Core Aesthetic: "Tech-Futurism meets Elegance"

The site should feel premium, modern, and slightly futuristic, inspired by:
- 🌌 Sci-fi interfaces (Tron, Blade Runner)
- 🟩 AI/Deep learning visualizations
- 💎 Clean glassmorphism
- ✨ Subtle neon accents

### 2. Color Palette
```typescript
// Primary
const primary = {
  light: '#4ade80',   // Emerald-400
  medium: '#22c55e',  // Emerald-500
  dark: '#16a34a',    // Emerald-600
  glow: '#10b981',    // Emerald-500 (blurred)
}

// Secondary (Accents)
const secondary = {
  purple: '#a855f7',  // Violet-500
  cyan: '#06b6d4',    // Cyan-500
}

// Neutrals
const bg = '#020617', // Slate-950 (deep space black)
const card = 'rgba(15, 23, 42, 0.5)', // Slate-900 with transparency
const border = 'rgba(56, 189, 248, 0.15)', // Cyan-400 border
```

### 3. Glassmorphism

All interactive elements (nav, cards, buttons) must use:
```css
backdrop-filter: blur(16px);
-webkit-backdrop-filter: blur(16px);
background-color: rgba(15, 23, 42, 0.5);
border: 1px solid rgba(56, 189, 248, 0.15);
box-shadow: 0 4px 30px rgba(0, 0, 0, 0.2);
```

### 4. Gradients & Neon Effects

Use linear and radial gradients with `mix-blend-mode: screen` or `overlay` for:
- Background ambient glows
- Button hover effects
- Animated text
- Project card highlights

### 5. Typography
- **Headings**: Orbitron (large, spaced, futuristic)
- **Body**: Inter (clean, readable)
- **Code/Tech**: Space Mono (for tech stack, version numbers, code snippets)

---

## 📱 Layout System

### Responsive Grid (Mobile-First)
```css
.container { max-width: 1400px; padding: 0 1.5rem; }

// Columns
grid-cols-1          // mobile
grid-cols-2          // tablet
grid-cols-3          // desktop
grid-cols-4          // ultra-wide
```

### Spacing
```css
padding: 1.5rem;       // mobile default
padding: 3rem;         // desktop default
gap: 1rem;           // mobile gap
gap: 2rem;           // desktop gap
```

---

## 🚀 Page Structure

### 1. Navigation (Desktop Fixed, Mobile Bottom)
- **Desktop**: Fixed sidebar or top bar (transparent background)
- **Mobile**: Bottom sticky nav (like Instagram/Reels) with large touch targets
- **Content**: Home, About, Projects, Skills, Contact

### 2. Home Section
- **Hero**: Large headline, typing effect, gradient text
- **Subtext**: Brief professional summary (2-3 sentences)
- **CTA**: "View My Work" button (gradient border, hover glow)
- **Social Icons**: LinkedIn, GitHub, X (Twitter), PDF Resume
- **Visuals**: Background ambient gradients, subtle particle effects (optional)

### 3. About Section
- **Layout**: 2-column (image left, text right on desktop; stacked on mobile)
- **Content**: Personal story, mission statement
- **Design**: Glass card with hover lift effect

### 4. Projects Section
- **Layout**: Grid of cards (2-3 per row)
- **Each Card**: Screenshot/GIF, Title, Tags (React, Node, AI, etc.), Description, Links (Live/GitHub)
- **Interactions**: Hover flip, gradient border animation, subtle glow

### 5. Skills Section
- **Layout**: Tech stack badges/tags (clean, rounded)
- **Organization**: Categories (Frontend, Backend, Tools, AI/ML)
- **Visuals**: Orbitron font for category headers, Space Mono for tags, glow on hover

### 6. Contact Section
- **Form**: Clean fields (Name, Email, Message)
- **Design**: Glassmorphic form with subtle focus animations
- **Alternative**: Direct links to LinkedIn/Email
- **Captcha**: Add invisible reCAPTCHA or similar spam protection

---

## 🎯 Animation Guidelines (Framer Motion)

### Global Page Transitions
```typescript
// Layout.tsx
<AnimatePresence mode="wait">
  <motion.main
    initial={{ opacity: 0, x: 30 }}
    animate={{ opacity: 1, x: 0 }}
    exit={{ opacity: 0, x: -30 }}
    transition={{ duration: 0.6, ease: "easeInOut" }}
  >
    {children}
  </motion.main>
</AnimatePresence>
```

### Component Animations

| Component | Animation Type | Details |
|-----------|----------------|---------|
| Hero Text | Typing Effect | Use `type` animation with realistic WPM |
| Nav Items | Slide & Fade | Staggered reveal on mount |
| Buttons | Hover Scale | Scale up + glow on hover |
| Cards | Flip & Lift | 3D flip on hover, glow effect |
| Project Images | Zoom & Fade | Zoom in on hover |
| Section Headers | Reveal | Slide in + gradient fill |

### Performance
- Use `initial={false}` for components that don't need initial animation
- Use `viewport={{ once: true }}` for scroll animations
- Test on mobile devices with throttling

---

## 🧪 Testing Requirements

1. **Mobile Viewport**: Verify on 320px, 375px, 414px
2. **Tablet Viewport**: Verify on 768px, 1024px
3. **Desktop Viewport**: Verify on 1280px, 1440px, 1920px
4. **Performance**: Lighthouse score >90
5. **Accessibility**: Keyboard navigation, ARIA labels, focus states
6. **Interaction**: All buttons/links functional, hover effects work
7. **Responsiveness**: No layout breaks, images resize correctly
8. **Animation**: Smooth transitions (no jank), test on touch devices

---

## 📝 README Checklist

After development, update README.md with:

- [ ] Project title and one-liner
- [ ] Screenshots of desktop and mobile views
- [ ] Tech stack summary
- [ ] Deployment instructions
- [ ] Development setup
- [ ] Design system notes
- [ ] Animation library usage
- [ ] Testing checklist
- [ ] Portfolio links (LinkedIn, GitHub, etc.)
- [ ] SEO meta tags (title, description, Open Graph)

---

## 💡 Tips for AI Agents

1. **Always start with structure** - create components first, then style
2. **Use Tailwind utilities** - avoid custom
