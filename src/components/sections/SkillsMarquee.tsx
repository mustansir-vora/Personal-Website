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
    <section className="py-24 relative" id="skills">
      {/* Background glow for the section */}
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-full max-w-2xl h-96 bg-emerald-600/10 rounded-full filter blur-[100px] pointer-events-none z-0 transform-gpu"></div>

      <div className="max-w-6xl mx-auto px-6 md:px-8 relative z-10">

        <div className="text-center mb-16">
          <motion.h2
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            className="text-3xl md:text-5xl font-bold tracking-tight inline-block px-8 py-3 glass rounded-full"
          >
            Tech Stack
          </motion.h2>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {categories.map((category) => {
            const Icon = category.icon;
            return (
              <motion.div
                key={category.title}
                initial={{ opacity: 0, y: 30 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true, margin: "-50px" }}
                className={`${category.className} flex`}
              >
                <CategoryCard className="w-full">
                  <div className="flex items-center gap-3 mb-6 lg:mb-8">
                    <div className="p-2 rounded-lg bg-emerald-500/10 border border-emerald-500/20">
                      <Icon className="w-5 h-5 text-emerald-400" />
                    </div>
                    <h3 className="text-lg md:text-xl font-bold text-white tracking-wide">
                      {category.title}
                    </h3>
                  </div>

                  <motion.div
                    variants={containerVariants}
                    initial="hidden"
                    whileInView="visible"
                    viewport={{ once: true }}
                    className="flex sm:flex-wrap overflow-x-auto sm:overflow-visible snap-x snap-mandatory sm:snap-none pb-4 sm:pb-0 -mb-4 sm:mb-0 gap-3 md:gap-4 [&::-webkit-scrollbar]:hidden [-ms-overflow-style:none] [scrollbar-width:none] flex-grow items-start"
                  >
                    {category.skills.map((skill) => (
                      <motion.div
                        key={skill.name}
                        variants={itemVariants}
                        className="group relative flex flex-col sm:flex-row items-center justify-center gap-1.5 sm:gap-3 p-2 sm:px-5 sm:py-3.5 w-[84px] h-[84px] sm:w-auto sm:h-auto rounded-2xl bg-white/5 border border-white/10 hover:border-emerald-500/50 hover:bg-emerald-500/10 transition-all duration-300 cursor-default shrink-0 snap-center shadow-lg hover:shadow-[0_0_20px_rgba(16,185,129,0.15)]"
                      >
                        {/* Hover inner glow */}
                        <div className="absolute inset-0 rounded-2xl bg-emerald-500/0 group-hover:bg-emerald-500/5 blur-md transition-all duration-500 pointer-events-none" />

                        {skill.icon ? (
                          <img
                            src={skill.icon}
                            alt={skill.name}
                            className="w-7 h-7 sm:w-8 sm:h-8 relative z-10 transition-transform duration-300 group-hover:scale-110 drop-shadow-md shrink-0"
                          />
                        ) : (
                          <div className="w-7 h-7 sm:w-8 sm:h-8 rounded-lg bg-gradient-to-br from-emerald-500/30 to-teal-500/30 flex items-center justify-center relative z-10 border border-emerald-500/20 shadow-inner shrink-0">
                            <span className="text-[10px] sm:text-xs font-bold text-emerald-300">
                              {skill.name.split(' ').map(w => w[0]).join('').substring(0, 2).toUpperCase()}
                            </span>
                          </div>
                        )}

                        <span className="font-medium text-[11px] leading-tight sm:text-base text-white/80 group-hover:text-white relative z-10 text-center sm:whitespace-nowrap transition-colors duration-300">
                          {skill.name}
                        </span>
                      </motion.div>
                    ))}
                  </motion.div>
                </CategoryCard>
              </motion.div>
            );
          })}
        </div>

      </div>
    </section>
  );
}
