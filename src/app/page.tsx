import React from 'react';
import HeroSection from '@/components/sections/HeroSection';
import AboutSection from '@/components/sections/AboutSection';
import SkillsMarquee from '@/components/sections/SkillsMarquee';
import ExperienceTimeline from '@/components/sections/ExperienceTimeline';
import TestimonialsSection from '@/components/sections/TestimonialsSection';
import ProjectsGallery from '@/components/sections/ProjectsGallery';

export default function Home() {
  return (
    <main className="min-h-screen w-full">
      <HeroSection />
      <AboutSection />
      <SkillsMarquee />
      <ExperienceTimeline />
      <TestimonialsSection />
      <ProjectsGallery />
      
      {/* Footer / Contact Section */}
      <footer className="w-full py-12 mt-12 border-t border-white/10 text-center text-muted-foreground">
        <p>© {new Date().getFullYear()} Mustansir Vora. Building intelligent digital experiences.</p>
        <p className="text-sm mt-2">Next.js + Tailwind CSS + Framer Motion</p>
      </footer>
    </main>
  );
}
