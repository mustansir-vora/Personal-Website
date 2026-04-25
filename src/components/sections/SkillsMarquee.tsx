"use client";

import React from 'react';
import { motion, Variants } from 'framer-motion';
import { assetPath } from '@/lib/basePath';

const TerminalIcon = ({ className }: { className?: string }) => (
  <svg className={className} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <polyline points="4 17 10 11 4 5"></polyline>
    <line x1="12" y1="19" x2="20" y2="19"></line>
  </svg>
);

const CloudIcon = ({ className }: { className?: string }) => (
  <svg className={className} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M17.5 19H9a7 7 0 1 1 6.71-9h1.79a4.5 4.5 0 1 1 0 9Z"></path>
  </svg>
);

const BrainIcon = ({ className }: { className?: string }) => (
  <svg className={className} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M9.5 2A2.5 2.5 0 0 1 12 4.5v15a2.5 2.5 0 0 1-4.96.44 2.5 2.5 0 0 1-2.96-3.08 3 3 0 0 1-.34-5.58 2.5 2.5 0 0 1 1.32-4.24 2.5 2.5 0 0 1 1.98-3A2.5 2.5 0 0 1 9.5 2Z"></path>
    <path d="M14.5 2A2.5 2.5 0 0 0 12 4.5v15a2.5 2.5 0 0 0 4.96.44 2.5 2.5 0 0 0 2.96-3.08 3 3 0 0 0 .34-5.58 2.5 2.5 0 0 0-1.32-4.24 2.5 2.5 0 0 0-1.98-3A2.5 2.5 0 0 0 14.5 2Z"></path>
  </svg>
);

const MessageIcon = ({ className }: { className?: string }) => (
  <svg className={className} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
  </svg>
);

interface Skill {
  name: string;
  icon?: string;
}

interface SkillCategory {
  title: string;
  icon: React.ElementType;
  className: string;
  skills: Skill[];
}

const categories: SkillCategory[] = [
  {
    title: 'Languages & Frameworks',
    icon: TerminalIcon,
    className: 'lg:col-span-2',
    skills: [
      { name: 'Java', icon: assetPath('/Assets/JAVA.svg') },
      { name: 'JavaScript', icon: assetPath('/Assets/JAVASCRIPT.svg') },
      { name: 'Python', icon: assetPath('/Assets/PYTHON.svg') },
      { name: 'Node.js', icon: assetPath('/Assets/NODEJS.svg') },
      { name: 'React.js', icon: assetPath('/Assets/REACT.svg') },
      { name: 'SQL', icon: assetPath('/Assets/SQL.svg') },
    ]
  },
  {
    title: 'Cloud & DevOps',
    icon: CloudIcon,
    className: 'lg:col-span-1',
    skills: [
      { name: 'GCP', icon: assetPath('/Assets/GCP.svg') },
      { name: 'AWS', icon: assetPath('/Assets/AWS.svg') },
      { name: 'Azure', icon: assetPath('/Assets/AZURE.svg') },
      { name: 'Docker', icon: assetPath('/Assets/DOCKER.svg') },
      { name: 'Kubernetes', icon: assetPath('/Assets/KUBERNETES.svg') },
    ]
  },
  {
    title: 'AI & Machine Learning',
    icon: BrainIcon,
    className: 'lg:col-span-1',
    skills: [
      { name: 'LLMs', icon: assetPath('/Assets/LLM.svg') },
      { name: 'RAG', icon: assetPath('/Assets/RAG.svg') },
      { name: 'Agentic AI' },
      { name: 'Machine Learning', icon: assetPath('/Assets/MACHINELEARNING.svg') },
    ]
  },
  {
    title: 'Conversational AI Platforms',
    icon: MessageIcon,
    className: 'lg:col-span-2',
    skills: [
      { name: 'Dialogflow CX', icon: assetPath('/Assets/DIALOGFLOW.svg') },
      { name: 'Copilot Studio', icon: assetPath('/Assets/COPILOTSTUDIO.svg') },
      { name: 'Genesys', icon: assetPath('/Assets/GENESYS.svg') },
      { name: 'Nuance' },
    ]
  },
];

const containerVariants: Variants = {
  hidden: {},
  visible: {
    transition: { staggerChildren: 0.1 }
  }
};

const itemVariants: Variants = {
  hidden: { opacity: 0, y: 20, scale: 0.95 },
  visible: {
    opacity: 1,
    y: 0,
    scale: 1,
    transition: { duration: 0.4, ease: "easeOut" }
  }
};

const CategoryCard = ({ children, className = "" }: { children: React.ReactNode, className?: string }) => {
  return (
    <div className={`relative overflow-hidden rounded-3xl border border-white/10 bg-teal-950/20 shadow-2xl backdrop-blur-md transition-colors hover:border-white/20 hover:bg-teal-900/30 ${className}`}>
      <div className="relative z-10 h-full p-6 md:p-8 flex flex-col">
        {children}
      </div>
    </div>
  );
};

export default function SkillsMarquee() {
  return (
    <section className="py-24 md:py-32 relative overflow-hidden" id="skills">
      {/* Background glow for the section */}
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-full max-w-4xl h-[500px] bg-emerald-600/10 rounded-full filter blur-[120px] pointer-events-none z-0 transform-gpu"></div>

      <div className="relative z-10 w-full flex flex-col items-center mb-16 md:mb-24">
        <motion.h2
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          className="text-3xl md:text-5xl font-bold tracking-tight inline-block px-8 py-3 glass rounded-full"
        >
          Tech Stack
        </motion.h2>
      </div>

      {/* MOBILE LAYOUT (Grid Cards + Marquees) */}
      <div className="block lg:hidden max-w-6xl mx-auto px-6 relative z-10">
        <div className="grid grid-cols-1 gap-6">
          {categories.map((category) => {
            const Icon = category.icon;
            return (
              <motion.div
                key={category.title + '-mobile'}
                initial={{ opacity: 0, y: 30 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true, margin: "-50px" }}
                className="flex w-full"
              >
                <CategoryCard className="w-full">
                  <div className="flex items-center gap-3 mb-6">
                    <div className="p-2 rounded-lg bg-emerald-500/10 border border-emerald-500/20">
                      <Icon className="w-5 h-5 text-emerald-400" />
                    </div>
                    <h3 className="text-lg font-bold text-white tracking-wide">
                      {category.title}
                    </h3>
                  </div>

                  <div className="flex overflow-hidden w-full relative [mask-image:linear-gradient(to_right,transparent,black_5%,black_95%,transparent)]">
                    <div className="flex w-max animate-[marquee_14s_linear_infinite] pb-4 -mb-4 gap-3 pr-3 shrink-0 items-start transform-gpu">
                      {category.skills.map((skill) => (
                        <div
                          key={skill.name + '-mobile'}
                          className="group relative flex flex-col items-center justify-center gap-1.5 p-2 w-[84px] h-[84px] rounded-2xl bg-white/5 border border-white/10 transition-all duration-300 cursor-default shrink-0 shadow-lg"
                        >
                          <div className="absolute inset-0 rounded-2xl bg-emerald-500/0 blur-md transition-all duration-500 pointer-events-none" />
                          {skill.icon ? (
                            <img src={skill.icon} alt={skill.name} className="w-7 h-7 relative z-10 drop-shadow-md shrink-0" />
                          ) : (
                            <div className="w-7 h-7 rounded-lg bg-gradient-to-br from-emerald-500/30 to-teal-500/30 flex items-center justify-center relative z-10 border border-emerald-500/20 shadow-inner shrink-0">
                              <span className="text-[10px] font-bold text-emerald-300">{skill.name.split(' ').map(w => w[0]).join('').substring(0, 2).toUpperCase()}</span>
                            </div>
                          )}
                          <span className="font-medium text-[11px] leading-tight text-white/80 relative z-10 text-center transition-colors duration-300">{skill.name}</span>
                        </div>
                      ))}
                    </div>

                    {/* Duplicate Track for Mobile Marquee */}
                    <div aria-hidden="true" className="flex animate-[marquee_14s_linear_infinite] w-max pb-4 -mb-4 gap-3 pr-3 shrink-0 items-start transform-gpu">
                      {category.skills.map((skill) => (
                        <div
                          key={skill.name + '-dup-mobile'}
                          className="group relative flex flex-col items-center justify-center gap-1.5 p-2 w-[84px] h-[84px] rounded-2xl bg-white/5 border border-white/10 transition-all duration-300 cursor-default shrink-0 shadow-lg"
                        >
                          <div className="absolute inset-0 rounded-2xl bg-emerald-500/0 blur-md transition-all duration-500 pointer-events-none" />
                          {skill.icon ? (
                            <img src={skill.icon} alt={skill.name} className="w-7 h-7 relative z-10 drop-shadow-md shrink-0" />
                          ) : (
                            <div className="w-7 h-7 rounded-lg bg-gradient-to-br from-emerald-500/30 to-teal-500/30 flex items-center justify-center relative z-10 border border-emerald-500/20 shadow-inner shrink-0">
                              <span className="text-[10px] font-bold text-emerald-300">{skill.name.split(' ').map(w => w[0]).join('').substring(0, 2).toUpperCase()}</span>
                            </div>
                          )}
                          <span className="font-medium text-[11px] leading-tight text-white/80 relative z-10 text-center transition-colors duration-300">{skill.name}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                </CategoryCard>
              </motion.div>
            );
          })}
        </div>
      </div>

      {/* DESKTOP LAYOUT (Stacked Rows) */}
      <div className="hidden lg:flex flex-col gap-6 md:gap-10 relative z-10 w-full overflow-hidden [mask-image:linear-gradient(to_right,transparent,black_15%,black_85%,transparent)]">
        {categories.map((category, index) => {
          const isReverse = index % 2 !== 0;
          // Vary the speed slightly per row for a more dynamic feel
          const speeds = ['30s', '35s', '28s', '32s'];
          const animationClass = isReverse 
            ? `animate-[marquee_${speeds[index]}_linear_infinite_reverse]` 
            : `animate-[marquee_${speeds[index]}_linear_infinite]`;

          const renderSkills = (keySuffix: string) => (
            <>
              {/* Category Pill */}
              <div className="flex items-center justify-center gap-2 px-5 py-3 md:px-8 md:py-4 rounded-2xl bg-emerald-500/10 border border-emerald-500/20 whitespace-nowrap shrink-0 shadow-lg">
                <category.icon className="w-5 h-5 md:w-6 md:h-6 text-emerald-400" />
                <span className="font-bold text-white text-sm md:text-base tracking-wide uppercase">{category.title}</span>
              </div>
              
              {/* Skills */}
              {category.skills.map((skill) => (
                <div
                  key={skill.name + keySuffix}
                  className="group relative flex items-center justify-center gap-3 px-5 py-3 md:px-8 md:py-4 rounded-2xl bg-white/5 border border-white/10 hover:border-emerald-500/50 hover:bg-emerald-500/10 transition-all duration-300 cursor-default shrink-0 shadow-lg hover:shadow-[0_0_20px_rgba(16,185,129,0.15)]"
                >
                  <div className="absolute inset-0 rounded-2xl bg-emerald-500/0 group-hover:bg-emerald-500/5 blur-md transition-all duration-500 pointer-events-none" />
                  {skill.icon ? (
                    <img src={skill.icon} alt={skill.name} className="w-6 h-6 md:w-8 md:h-8 relative z-10 transition-transform duration-300 group-hover:scale-110 drop-shadow-md shrink-0" />
                  ) : (
                    <div className="w-6 h-6 md:w-8 md:h-8 rounded-lg bg-gradient-to-br from-emerald-500/30 to-teal-500/30 flex items-center justify-center relative z-10 border border-emerald-500/20 shadow-inner shrink-0">
                      <span className="text-[10px] md:text-xs font-bold text-emerald-300">{skill.name.split(' ').map(w => w[0]).join('').substring(0, 2).toUpperCase()}</span>
                    </div>
                  )}
                  <span className="font-medium text-sm md:text-base text-white/80 group-hover:text-white relative z-10 whitespace-nowrap transition-colors duration-300">{skill.name}</span>
                </div>
              ))}
            </>
          );

          return (
            <div key={category.title + '-desktop'} className="flex w-full">
              <div className={`flex w-max ${animationClass} gap-4 md:gap-6 pr-4 md:pr-6 shrink-0 transform-gpu hover:[animation-play-state:paused]`}>
                {renderSkills('-1')}
              </div>
              <div aria-hidden="true" className={`flex w-max ${animationClass} gap-4 md:gap-6 pr-4 md:pr-6 shrink-0 transform-gpu hover:[animation-play-state:paused]`}>
                {renderSkills('-2')}
              </div>
              {/* Third duplicate to cover ultra-wide monitors since rows are long */}
              <div aria-hidden="true" className={`flex w-max ${animationClass} gap-4 md:gap-6 pr-4 md:pr-6 shrink-0 transform-gpu hover:[animation-play-state:paused]`}>
                {renderSkills('-3')}
              </div>
              <div aria-hidden="true" className={`flex w-max ${animationClass} gap-4 md:gap-6 pr-4 md:pr-6 shrink-0 transform-gpu hover:[animation-play-state:paused]`}>
                {renderSkills('-4')}
              </div>
            </div>
          );
        })}
      </div>
    </section>
  );
}
