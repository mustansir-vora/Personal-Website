"use client";

import React, { useState, useRef } from 'react';
import { motion, useScroll, useSpring } from 'framer-motion';
import { Briefcase } from 'lucide-react';

const experiences = [
  {
    id: 'rogers',
    role: 'Software Developer (Agentic AI & Integration)',
    company: 'Rogers Telecommunications / Servion',
    period: 'Recent - Present',
    shortDesc: 'Integrating agentic AI with legacy backend systems using OpenAPI schemas.',
    fullDesc: 'Lead the architecture and development of advanced conversational AI systems. Built Dialogflow CX agents using playbooks, deterministic flows, and generators. Successfully integrated modern Agentic AI capabilities with legacy backend systems via robust OpenAPI schemas, driving complex automated customer journeys.'
  },
  {
    id: 'servion-swe',
    role: 'Software Developer',
    company: 'Servion Global Solutions',
    period: 'Dec 2023 - Present',
    shortDesc: 'Client Appreciation Award and ACE Award FY24-25 winner for complex customer integrations.',
    fullDesc: 'Rewarded with the ‘Client Appreciation Award’ and ‘ACE Award FY’24-25’ for significant contributions and excellent customer management. Awarded “Best new associate of FY’23-24” for consistently exceeding KPIs and making significant contributions.'
  },
  {
    id: 'servion-get',
    role: 'Graduate Engineer Trainee',
    company: 'Servion Global Solutions',
    period: 'Nov 2022 - Nov 2023',
    shortDesc: 'Designed and led complex customer integrations within second year.',
    fullDesc: 'Received exceptional performance reviews, with the Director of Professional Services department describing me as a "once in a generation find with the ability to lead complex integrations and teams," which led to opportunities to design and lead complex customer integrations within my second year.'
  }
];

export default function ExperienceTimeline() {
  const containerRef = useRef<HTMLDivElement>(null);
  const [expandedId, setExpandedId] = useState<string | null>(null);

  const { scrollYProgress } = useScroll({
    target: containerRef,
    offset: ["start center", "end center"]
  });

  const scaleY = useSpring(scrollYProgress, {
    stiffness: 100,
    damping: 30,
    restDelta: 0.001
  });

  return (
    <section className="py-24 relative" id="experience" ref={containerRef}>
      <div className="max-w-5xl mx-auto px-8">
        
        <div className="text-center mb-20">
          <motion.h2 
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            className="text-3xl md:text-5xl font-bold tracking-tight inline-block px-8 py-3 glass rounded-full"
          >
            Experience
          </motion.h2>
          <p className="mt-6 text-xl text-muted-foreground">Building Future-Ready Digital Solutions</p>
        </div>

        <div className="relative">
          {/* Scroll Progress Line */}
          <div className="absolute left-0 md:left-1/2 top-0 bottom-0 w-1 bg-white/10 rounded-full transform md:-translate-x-1/2 origin-top">
            <motion.div 
              className="absolute top-0 left-0 w-full bg-emerald-500 rounded-full origin-top"
              style={{ scaleY, height: '100%' }}
            />
          </div>

          <div className="space-y-12">
            {experiences.map((exp, index) => {
              const isEven = index % 2 === 0;
              const isExpanded = expandedId === exp.id;

              return (
                <div key={exp.id} className={`relative flex flex-col md:flex-row items-center ${isEven ? 'md:flex-row-reverse' : ''}`}>
                  
                  {/* Timeline Dot */}
                  <div className="absolute left-[-8px] md:left-1/2 w-5 h-5 bg-black border-4 border-emerald-500 rounded-full transform md:-translate-x-1/2 z-10 shadow-[0_0_15px_rgba(16,185,129,0.5)]"></div>

                  {/* Content Card */}
                  <div className={`w-full md:w-1/2 pl-8 md:pl-0 ${isEven ? 'md:pr-16 text-left md:text-right' : 'md:pl-16 text-left'}`}>
                    <motion.div 
                      layout
                      onClick={() => setExpandedId(isExpanded ? null : exp.id)}
                      initial={{ opacity: 0, x: isEven ? 50 : -50 }}
                      whileInView={{ opacity: 1, x: 0 }}
                      viewport={{ once: true, margin: "-50px" }}
                      className="glass p-6 md:p-8 rounded-3xl cursor-pointer hover:bg-white/10 transition-colors border border-white/10 hover:border-emerald-500/50"
                    >
                      <motion.div layout className="flex items-center gap-3 mb-2 justify-start md:justify-start">
                        <Briefcase className="w-5 h-5 text-lime-300 hidden md:block" />
                        <h3 className="text-xl md:text-2xl font-bold text-white">{exp.role}</h3>
                      </motion.div>
                      
                      <motion.p layout className="text-lime-300 font-medium mb-1">{exp.company}</motion.p>
                      <motion.p layout className="text-sm text-muted-foreground mb-4">{exp.period}</motion.p>
                      
                      <motion.p layout className="text-white/80">
                        {exp.shortDesc}
                      </motion.p>

                      {/* Expandable Content Area (Template for user to fill more details) */}
                      {isExpanded && (
                        <motion.div 
                          initial={{ opacity: 0, height: 0 }}
                          animate={{ opacity: 1, height: 'auto' }}
                          exit={{ opacity: 0, height: 0 }}
                          className="mt-4 pt-4 border-t border-white/10 text-muted-foreground"
                        >
                          <p>{exp.fullDesc}</p>
                          {/* User can add bullet points or metrics here later */}
                        </motion.div>
                      )}

                      <motion.div layout className="mt-4 text-xs font-semibold text-emerald-400 uppercase tracking-wider">
                        {isExpanded ? 'Show Less' : 'Click for Details'}
                      </motion.div>

                    </motion.div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

      </div>
    </section>
  );
}
