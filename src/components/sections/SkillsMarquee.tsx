"use client";

import React from 'react';
import { BrainCircuit, CodeSquare, Bot } from 'lucide-react';

type Skill = {
  name: string;
  icon?: string;
  iconComponent?: any;
  color?: string;
};

const existingSkills: Skill[] = [
  { name: 'Java', icon: '/Assets/JAVA.svg' },
  { name: 'JavaScript', icon: '/Assets/JAVASCRIPT.svg' },
  { name: 'Node.js', icon: '/Assets/NODEJS.svg' },
  { name: 'React.js', icon: '/Assets/REACT.svg' },
  { name: 'Python', icon: '/Assets/PYTHON.svg' },
  { name: 'SQL', icon: '/Assets/SQL.svg' },
  { name: 'AWS', icon: '/Assets/AWS.svg' },
  { name: 'GCP', icon: '/Assets/GCP.svg' },
  { name: 'Azure', icon: '/Assets/AZURE.svg' },
  { name: 'Dialogflow CX', icon: '/Assets/DIALOGFLOW.svg' },
  { name: 'Copilot Studio', icon: '/Assets/COPILOTSTUDIO.svg' },
  { name: 'Genesys', icon: '/Assets/GENESYS.svg' },
  { name: 'LLMs', icon: '/Assets/LLM.svg' },
  { name: 'RAG', icon: '/Assets/RAG.svg' },
  { name: 'Machine Learning', icon: '/Assets/MACHINELEARNING.svg' },
  { name: 'Docker', icon: '/Assets/DOCKER.svg' },
  { name: 'Kubernetes', icon: '/Assets/KUBERNETES.svg' }
];

const newSkills: Skill[] = [
  { name: 'Agentic AI', iconComponent: BrainCircuit, color: 'text-emerald-400' },
  { name: 'OpenAPI Schemas', iconComponent: CodeSquare, color: 'text-teal-400' },
  { name: 'Advanced Dialogflow (Playbooks)', iconComponent: Bot, color: 'text-lime-400' },
];

export default function SkillsMarquee() {
  const allSkills = [...existingSkills, ...newSkills];
  
  // Double the list for seamless loop
  const marqueeItems = [...allSkills, ...allSkills];

  return (
    <section className="py-20 overflow-hidden relative w-full">
      {/* Fade edges */}
      <div className="absolute left-0 top-0 bottom-0 w-32 bg-gradient-to-r from-background to-transparent z-10 pointer-events-none"></div>
      <div className="absolute right-0 top-0 bottom-0 w-32 bg-gradient-to-l from-background to-transparent z-10 pointer-events-none"></div>
      
      <div className="text-center mb-12 relative z-20">
        <h2 className="text-3xl md:text-5xl font-bold tracking-tight inline-block px-8 py-3 glass rounded-full">
          Tech Stack
        </h2>
      </div>

      <div className="flex w-max space-x-6 px-4 hover:[animation-play-state:paused]" style={{ animation: 'marquee 80s linear infinite' }}>
        {marqueeItems.map((skill, index) => (
          <div 
            key={`${skill.name}-${index}`} 
            className="flex items-center space-x-3 glass px-6 py-4 rounded-2xl transition-all duration-300 hover:scale-110 hover:bg-white/10 hover:border-white/30 cursor-default group"
          >
            {skill.icon ? (
              <img src={skill.icon} alt={skill.name} className="w-10 h-10 transition-transform group-hover:rotate-12" />
            ) : (
              skill.iconComponent && <skill.iconComponent className={`w-10 h-10 ${skill.color} transition-transform group-hover:rotate-12`} />
            )}
            <span className="font-semibold whitespace-nowrap text-lg group-hover:text-white transition-colors">{skill.name}</span>
          </div>
        ))}
      </div>
      
      <div className="flex w-max space-x-6 px-4 mt-6 hover:[animation-play-state:paused]" style={{ animation: 'marquee-reverse 80s linear infinite' }}>
        {[...marqueeItems].reverse().map((skill, index) => (
          <div 
            key={`rev-${skill.name}-${index}`} 
            className="flex items-center space-x-3 glass px-6 py-4 rounded-2xl transition-all duration-300 hover:scale-110 hover:bg-white/10 hover:border-white/30 cursor-default group"
          >
            {skill.icon ? (
              <img src={skill.icon} alt={skill.name} className="w-10 h-10 transition-transform group-hover:-rotate-12" />
            ) : (
              skill.iconComponent && <skill.iconComponent className={`w-10 h-10 ${skill.color} transition-transform group-hover:-rotate-12`} />
            )}
            <span className="font-semibold whitespace-nowrap text-lg group-hover:text-white transition-colors">{skill.name}</span>
          </div>
        ))}
      </div>
    </section>
  );
}
