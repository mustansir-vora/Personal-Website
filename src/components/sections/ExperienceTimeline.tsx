"use client";

import React, { useState, useRef } from 'react';
import { motion, useScroll, useSpring } from 'framer-motion';
import { Briefcase } from 'lucide-react';

const experiences = [
  {
    id: 'rogers',
    role: 'Software Developer — Agentic AI & IVR Integration',
    company: 'Rogers & Shaw Communications Inc. | Servion Global Solutions',
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
    role: 'Software Developer — Cloud Migration & IVR',
    company: 'State of New Jersey – Conduent | Servion Global Solutions',
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
    role: 'Software Developer — Conversational AI & Data Analytics',
    company: 'Farmers Insurance Group | Servion Global Solutions',
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
    role: 'Software Developer — Voice Biometrics & IVR',
    company: 'ADCB Bank | Servion Global Solutions',
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
    role: 'Graduate Engineer Trainee — IVR Development',
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

                      {/* Expandable Content Area */}
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
