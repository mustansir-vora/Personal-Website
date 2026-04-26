"use client";

import React, { useState, useRef } from 'react';
import { motion, useScroll, useSpring, AnimatePresence } from 'framer-motion';
import { Briefcase } from 'lucide-react';
import { assetPath } from '@/lib/basePath';

const experiences = [
  {
    id: 'rogers',
    role: 'Agentic AI & IVR Integration',
    company: 'Rogers & Shaw Communications Inc.',
    period: 'Nov 2025 – Present',
    shortDesc: 'Architecting AI-driven IVR systems powered by Dialogflow CX, RAG pipelines, and automated cloud deployments.',
    bullets: [
      'Architected and developed core IVR components leveraging Google Dialogflow CX, resulting in seamless integration and natural, AI-driven user interactions.',
      'Built robust API integrations using Cloud Run functions to facilitate connectivity between AI services and internal/external backend systems.',
      'Reduced inaccurate AI responses (hallucinations) by 44% by implementing safeguards to keep the AI responses on track.',
      'Implemented a RAG pipeline that lets the AI voice bot check backend databases in real time, giving callers instant, accurate updates on their insurance claims.',
      'Sped up deployment times by 100% by setting up automated cloud pipelines to deploy Cloud Run functions, buckets, and conversational agents.',
      'Automated IVR reporting and analytics by implementing smooth data ingestion processes within Google Cloud Platform (BigQuery) and running Cloud Scheduler jobs for data cleansing.',
    ]
  },
  {
    id: 'new-jersey',
    role: 'Cloud Migration & IVR',
    company: 'State of New Jersey – Conduent',
    period: 'Aug 2025 – Feb 2026',
    shortDesc: 'Led end-to-end migration from on-premise phone systems to modern cloud IVR with Genesys Cloud.',
    bullets: [
      'Managed the migration of the state\'s older, on-premise phone system to a modern cloud setup.',
      'Traveled to the Princeton office to coordinate the launch and ensure zero business downtime.',
      'Built the conversational solution using bot flow within Genesys Cloud.',
      'Integrated the state\'s backend systems within the IVR for seamless data flow.',
      'Integrated Azure Speech-to-Text & Text-to-Speech services within the IVR for seamless conversations.',
      'Set up real-time data pipelines and Power BI dashboards so leadership could easily see live system performance.',
      'Maintained strong customer relationships through effective communication, achieving a CSAT rating of 5/5.',
    ]
  },
  {
    id: 'farmers',
    role: 'Conversational AI & Data Analytics',
    company: 'Farmers Insurance Group',
    period: 'Jan 2024 – Sep 2025',
    shortDesc: 'Engineered Dialogflow CX IVR connectors, middleware, and advanced reporting pipelines. Maintained a perfect 5/5 CSAT score.',
    bullets: [
      'Maintained a 5/5 CSAT score across 9 major project milestones by ensuring deliverables were reliable and on time.',
      'Built the CLAIMS IVR connector for a Cisco-based cloud infrastructure, enabling seamless communication with Google\'s Dialogflow CX for enhanced speech recognition and NLU.',
      'Developed a middleware client using Java and Spring Data JPA/Hibernate to manage data persistence and streamline API access.',
      'Managed and optimized large datasets for an advanced reporting pipeline using Google BigQuery. Developed complex SQL queries for data visualization in Google Looker Studio.',
    ]
  },
  {
    id: 'adcb',
    role: 'Voice Biometrics & IVR',
    company: 'ADCB Bank',
    period: 'Jun 2023 – Jan 2024',
    shortDesc: 'Implemented real-time voice biometric authentication and built a high-performance IVR on Genesys PureCloud & AWS.',
    bullets: [
      'Contributed to the implementation of a high-performance IVR system using Genesys PureCloud for front-end orchestration and AWS (Lambda, S3) for back-end integration.',
      'Implemented a voice biometric authentication solution using Audiohook for real-time audio processing, enhancing security and eliminating manual authentication.',
      'Integrated the IVR with the bank\'s core system, utilizing Java for robust back-end services.',
    ]
  },
  {
    id: 'fis',
    role: 'IVR Development',
    company: 'FIS EBT California | Servion Global Solutions',
    period: 'Dec 2022 – May 2024',
    shortDesc: 'Developed and maintained reusable IVR components using the Nuance State Engine framework.',
    bullets: [
      'Developed and maintained reusable IVR components, including menus, prompts, and data collection modules, using the Nuance State Engine Development framework (XML-based JAVA).',
      'Optimized IVR performance and scalability.',
    ]
  },
];

export default function ExperienceTimeline() {
  const containerRef = useRef<HTMLDivElement>(null);
  const timelineRef = useRef<HTMLDivElement>(null);
  const [expandedId, setExpandedId] = useState<string | null>(null);

  const { scrollYProgress } = useScroll({
    target: timelineRef,
    offset: ["start center", "end center"]
  });

  const scaleY = useSpring(scrollYProgress, {
    stiffness: 100,
    damping: 30,
    restDelta: 0.001
  });

  return (
    <section className="py-24 relative" id="experience" ref={containerRef}>
      <div className="max-w-7xl mx-auto px-8">
        
        <div className="text-center mb-16">
          <motion.h2 
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            className="text-3xl md:text-5xl font-bold tracking-tight inline-block px-8 py-3 glass rounded-full mb-6"
          >
            Experience
          </motion.h2>
        </div>

        <div className="relative lg:grid lg:grid-cols-12 lg:gap-12">
          {/* Left Column */}
          <div className="lg:col-span-4 mb-12 lg:mb-0">
            <div className="lg:sticky lg:top-32 z-20">
              <motion.div 
              initial={{ opacity: 0, scale: 0.95 }}
              whileInView={{ opacity: 1, scale: 1 }}
              viewport={{ once: true }}
              className="glass rounded-3xl p-6 md:p-8 border border-white/10 relative overflow-hidden shadow-2xl max-w-md mx-auto lg:mx-0"
            >
              {/* Ambient background glow */}
              <div className="absolute top-0 right-0 w-40 h-40 bg-emerald-500/10 blur-3xl rounded-full -mr-10 -mt-10" />
              <div className="absolute bottom-0 left-0 w-40 h-40 bg-lime-400/10 blur-3xl rounded-full -ml-10 -mb-10" />

              <div className="flex flex-col items-center text-center relative z-10">
                <div className="p-2 rounded-2xl bg-white/5 mb-4 border border-white/10 shadow-inner flex items-center justify-center w-28 h-16">
                  <img 
                    src={assetPath('/Assets/SERVION.svg')} 
                    alt="Servion Global Solutions" 
                    className="w-full h-full object-contain brightness-0 invert opacity-90"
                  />
                </div>
                
                <h3 className="text-xl md:text-2xl font-bold text-white mb-2">
                  Servion Global Solutions
                </h3>
                
                <p className="text-lime-400 font-semibold text-base md:text-lg mb-3">
                  Software Developer
                </p>
                
                <div className="inline-block bg-white/5 backdrop-blur-sm border border-white/10 px-3 py-1 rounded-full text-xs text-muted-foreground font-medium mb-4">
                  November 2022 – Present
                </div>
                
                <p className="text-muted-foreground text-sm md:text-base leading-relaxed">
                  Driving modern Conversational AI advancements, building scalable API workflows, and executing digital transformations for tier-1 global enterprise organizations.
                </p>
              </div>
            </motion.div>
          </div>
        </div>

          {/* Right Column - Clients Handled Timeline */}
          <div className="lg:col-span-8">
            <div className="mb-12 flex flex-col items-center lg:items-start">
              <motion.h3 
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                className="text-2xl md:text-3xl font-bold text-white/90 tracking-wide text-center lg:text-left"
              >
                Clients Handled
              </motion.h3>
              <div className="w-16 h-1 bg-gradient-to-r from-emerald-500 to-lime-400 mt-3 rounded-full" />
            </div>

            <div className="relative" ref={timelineRef}>
              {/* Scroll Progress Line */}
              <div className="absolute left-0 md:left-1/2 lg:left-0 top-0 bottom-0 w-1 bg-white/10 rounded-full transform md:-translate-x-1/2 lg:translate-x-0 origin-top">
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
                    <div key={exp.id} className={`relative flex flex-col md:flex-row lg:flex-row items-center ${isEven ? 'md:flex-row-reverse lg:flex-row' : ''}`}>
                      
                      {/* Timeline Dot */}
                      <div className="absolute left-[-8px] md:left-1/2 lg:left-[-8px] w-5 h-5 bg-black border-4 border-emerald-500 rounded-full transform md:-translate-x-1/2 lg:translate-x-0 z-10 shadow-[0_0_15px_rgba(16,185,129,0.5)]"></div>

                      {/* Content Card */}
                      <div className={`w-full md:w-1/2 lg:w-full pl-8 md:pl-0 lg:pl-12 ${isEven ? 'md:pr-16 lg:pr-0 text-left md:text-right lg:text-left' : 'md:pl-16 lg:pl-12 text-left'}`}>
                        <motion.div
                          onClick={() => setExpandedId(isExpanded ? null : exp.id)}
                          onKeyDown={(e) => {
                            if (e.key === 'Enter' || e.key === ' ') {
                              e.preventDefault();
                              setExpandedId(isExpanded ? null : exp.id);
                            }
                          }}
                          role="button"
                          tabIndex={0}
                          initial={{ opacity: 0, x: isEven ? 50 : -50 }}
                          whileInView={{ opacity: 1, x: 0 }}
                          viewport={{ once: true, margin: "-50px" }}
                          className="glass p-6 md:p-8 rounded-3xl cursor-pointer hover:bg-white/10 transition-colors border border-white/10 hover:border-emerald-500/50 outline-none focus-visible:ring-2 focus-visible:ring-emerald-500"
                        >
                      <div className="flex items-center gap-3 mb-2 justify-start">
                        <Briefcase className="w-5 h-5 text-lime-300" />
                        <h3 className="text-xl md:text-2xl font-bold text-white">{exp.role}</h3>
                      </div>
                      <p className="text-lime-300 font-medium mb-1">{exp.company}</p>
                      <p className="text-sm text-muted-foreground mb-4">{exp.period}</p>

                      <p className="text-white/80">
                        {exp.shortDesc}
                      </p>

                      {/* Expandable Content Area */}
                      <AnimatePresence initial={false}>
                        {isExpanded && (
                          <motion.div
                            initial={{ opacity: 0, height: 0 }}
                            animate={{ opacity: 1, height: 'auto' }}
                            exit={{ opacity: 0, height: 0 }}
                            className="mt-4 pt-4 border-t border-white/10 text-muted-foreground"
                          >
                            <ul className="space-y-2 text-sm md:text-base">
                              {exp.bullets.map((bullet, i) => (
                                <li key={i} className="flex items-start gap-2">
                                  <span className="text-emerald-400 mt-1 shrink-0">▸</span>
                                  <span>{bullet}</span>
                                </li>
                              ))}
                            </ul>
                          </motion.div>
                        )}
                      </AnimatePresence>

                      <div className="mt-4 text-xs font-semibold text-emerald-400 uppercase tracking-wider">
                        {isExpanded ? 'Show Less' : 'Click for Details'}
                      </div>

                    </motion.div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  </div>
</section>
  );
}
