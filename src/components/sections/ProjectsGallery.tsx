"use client";

import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { ExternalLink } from 'lucide-react';

const GithubIcon = ({ className }: { className?: string }) => (
  <svg className={className} viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
    <path d="M12 0C5.37 0 0 5.37 0 12c0 5.31 3.435 9.795 8.205 11.385.6.105.825-.255.825-.57 0-.285-.015-1.23-.015-2.235-3.015.555-3.795-.735-4.035-1.41-.135-.345-.72-1.41-1.23-1.695-.42-.225-1.02-.78-.015-.795.945-.015 1.62.87 1.845 1.23 1.08 1.815 2.805 1.305 3.495.99.105-.78.42-1.305.765-1.605-2.67-.3-5.46-1.335-5.46-5.925 0-1.305.465-2.385 1.23-3.225-.12-.3-.54-1.53.12-3.18 0 0 1.005-.315 3.3 1.23.96-.27 1.98-.405 3-.405s2.04.135 3 .405c2.295-1.56 3.3-1.23 3.3-1.23.66 1.65.24 2.88.12 3.18.765.84 1.23 1.905 1.23 3.225 0 4.605-2.805 5.625-5.475 5.925.435.375.81 1.095.81 2.22 0 1.605-.015 2.895-.015 3.3 0 .315.225.69.825.57A12.02 12.02 0 0024 12c0-6.63-5.37-12-12-12z" />
  </svg>
);

interface Project {
  id: string;
  title: string;
  client: string;
  tags: string[];
  shortDesc: string;
  bullets: string[];
  githubUrl?: string;
  liveUrl?: string;
}

const projects: Project[] = [
  // ── Client Work ──
  {
    id: 'rogers-ivr',
    title: 'Agentic AI IVR Platform',
    client: 'Rogers & Shaw Communications Inc.',
    tags: ['Dialogflow CX', 'RAG', 'Cloud Run', 'BigQuery', 'Agentic AI'],
    shortDesc: 'End-to-end architecture of an AI-driven IVR system with RAG pipelines, reducing hallucinations by 44% and doubling deployment speed.',
    bullets: [
      'Architected and developed core IVR components leveraging Google Dialogflow CX, resulting in seamless IVR integration and natural, AI-driven user interactions.',
      'Built robust API integrations using Cloud Run functions to facilitate connectivity between AI services and internal/external systems.',
      'Reduced inaccurate AI responses (hallucinations) by 44% by implementing safeguards to keep the AI responses on track.',
      'Implemented a RAG pipeline that lets the AI voice bot check backend databases in real time, giving callers instant, accurate updates on their insurance claims.',
      'Sped up deployment times by 100% by setting up automated cloud pipelines to deploy Cloud Run functions, storage buckets, and conversational agents.',
      'Automated IVR reporting and analytics by implementing smooth data ingestion within Google Cloud Platform (BigQuery) and running Cloud Scheduler jobs for data cleansing.',
    ]
  },
  {
    id: 'new-jersey',
    title: 'Cloud IVR Migration & Launch',
    client: 'State of New Jersey – Conduent',
    tags: ['Genesys Cloud', 'Azure STT/TTS', 'Power BI', 'Bot Flow'],
    shortDesc: 'Led the end-to-end migration of the state\'s legacy on-premise phone system to a modern, speech-enabled cloud IVR with zero downtime.',
    bullets: [
      'Managed the full migration of the state\'s older, on-premise phone system to a modern Genesys Cloud setup.',
      'Traveled on-site to the Princeton office to coordinate the launch and ensure zero business downtime.',
      'Built the conversational solution using bot flow within Genesys Cloud.',
      'Integrated the state\'s backend systems within the IVR for seamless data flow.',
      'Integrated Azure Speech-to-Text & Text-to-Speech services within the IVR for seamless natural conversations.',
      'Set up real-time data pipelines and Power BI dashboards so leadership could easily monitor live system performance.',
      'Maintained strong customer relationships through effective communication, persistent resolution of client concerns, and achieving a CSAT rating of 5/5.',
    ]
  },
  {
    id: 'farmers-ivr',
    title: 'CLAIMS IVR & Data Analytics Platform',
    client: 'Farmers Insurance Group',
    tags: ['Dialogflow CX', 'Cisco', 'Java', 'Spring Data JPA', 'BigQuery', 'Looker Studio'],
    shortDesc: 'Engineered the CLAIMS IVR connector and advanced reporting pipelines, maintaining a perfect 5/5 CSAT score across 9 milestones.',
    bullets: [
      'Maintained a 5/5 CSAT score across 9 major project milestones by ensuring deliverables were reliable and on time.',
      'Built the CLAIMS IVR connector for a Cisco-based cloud infrastructure, enabling seamless communication with Google\'s Dialogflow CX for enhanced speech recognition and NLU.',
      'Developed a middleware client using Java and Spring Data JPA/Hibernate to manage data persistence and streamline API access for the application.',
      'Managed and optimized large datasets for an advanced reporting pipeline using Google BigQuery.',
      'Developed complex SQL queries to extract and process data for visualization in Google Looker Studio.',
    ]
  },
  {
    id: 'adcb-bank',
    title: 'Voice Biometric Authentication IVR',
    client: 'ADCB Bank',
    tags: ['Genesys PureCloud', 'AWS Lambda', 'S3', 'Audiohook', 'Voice Biometrics', 'Java'],
    shortDesc: 'Implemented real-time voice biometric authentication on a high-performance Genesys PureCloud IVR backed by AWS.',
    bullets: [
      'Contributed to the implementation of a high-performance IVR system using Genesys PureCloud for front-end orchestration and AWS (Lambda, S3) for back-end integration.',
      'Implemented a voice biometric authentication solution using Audiohook for real-time audio processing, enhancing security and eliminating manual authentication.',
      'Integrated the IVR with the bank\'s core system, utilizing Java for robust back-end services.',
    ]
  },
  {
    id: 'fis-ebt',
    title: 'EBT IVR System',
    client: 'FIS EBT California',
    tags: ['Nuance', 'XML', 'Java', 'IVR Components'],
    shortDesc: 'Developed reusable IVR components using the Nuance State Engine framework, optimizing performance and scalability.',
    bullets: [
      'Developed and maintained reusable IVR components, including menus, prompts, and data collection modules, using the Nuance State Engine Development framework (XML-based JAVA).',
      'Optimized IVR performance and scalability for the state\'s EBT (Electronic Benefits Transfer) system.',
    ]
  },
  // ── Personal Projects ──
  {
    id: 'ai-resume',
    title: 'AI Resume Optimizer',
    client: 'Personal Project',
    tags: ['Python', 'Streamlit', 'Google Gemini LLM', 'lxml', 'XML Parsing'],
    shortDesc: 'An AI agent that optimizes resumes by extracting DOCX content into XML, using Gemini LLM for job-specific enhancement, and rebuilding the DOCX with original formatting preserved.',
    githubUrl: 'https://github.com/mustansir-vora/AI-Resume-Optimizer',
    liveUrl: 'https://ai-resume-optimizer-covxskfybhkh5vnecsvyxr.streamlit.app/',
    bullets: [
      'Built with Streamlit for a clean, interactive UI: upload a .docx resume, paste a job description, and download an optimized version.',
      'Core innovation: parses the .docx as XML using lxml, extracting every text element with its unique XPath for precise, position-aware content replacement.',
      'Sends structured text + XPaths to Google Gemini LLM, which rewrites content to align with job requirements and returns structured JSON.',
      'Reconstructs a new .docx in memory, replacing text at each XPath with optimized content while preserving all original fonts, layouts, and styling.',
      'Includes detailed change analysis: strong points, weak points, and specific modifications for user transparency.',
    ]
  },
  {
    id: 'github-summarizer',
    title: 'GitHub Repository Summarizer',
    client: 'Personal Project',
    tags: ['Python', 'Streamlit', 'Gemini AI', 'PyGithub', 'Plotly', 'Graph Viz'],
    shortDesc: 'A Streamlit web app that analyzes any public GitHub repo — visualizes file/function architecture, generates AI-powered summaries, and plots commit activity.',
    githubUrl: 'https://github.com/mustansir-vora/Github-Summarizer',
    liveUrl: 'https://app-summarizer-ea7fhftuww2pgkixziufis.streamlit.app/',
    bullets: [
      'Fetches all files and commit history from a specified GitHub repository using the PyGithub API.',
      'Extracts code and function names from supported file types, compiling them for AI analysis.',
      'Uses Google Gemini to generate detailed, AI-powered explanations of the repository\'s code architecture and purpose.',
      'Visualizes file/function relationships as an interactive graph (red hexagons for files, blue dots for functions) using streamlit-agraph.',
      'Plots commit frequency over time with Plotly, revealing development patterns and activity trends.',
    ]
  },
  {
    id: 'eprocure-scraper',
    title: 'Government Tender Data Scraper',
    client: 'Personal Project',
    tags: ['Python', 'Selenium', 'Web Scraping', 'CSV', 'Automation'],
    shortDesc: 'A resilient web scraper for extracting tender data from India\'s GeM-CPPP (Central Public Procurement Portal), with auto-retry, checkpointing, and CSV export.',
    githubUrl: 'https://github.com/mustansir-vora/eprocure.gov-Scraper',
    bullets: [
      'Automates extraction of government tender data from the GeM-CPPP portal using Selenium WebDriver with headless Chrome.',
      'Resilient to network interruptions: implements automatic retry logic and per-row checkpointing for safe resumption.',
      'Saves all extracted data to a timestamped CSV file and generates a preview CSV with the first 10 rows for quick inspection.',
      'Runs Google Chrome in the background — no manual browser interaction needed for fully autonomous operation.',
    ]
  },
  {
    id: 'gps-tracker',
    title: 'GPS-GSM Vehicle Tracking Device',
    client: 'B.E. Final Year Project',
    tags: ['Arduino', 'C++', 'GPS', 'GSM', 'IoT', 'Hardware'],
    shortDesc: 'A low-cost, real-time GPS + GSM vehicle tracking system built with Arduino, sending live location updates via SMS with Google Maps links.',
    githubUrl: 'https://github.com/mustansir-vora/GPS-GSM-based-Tracking-Device',
    bullets: [
      'Built with Arduino UNO, NEO-6M GPS module, and SIM900A GSM module for a cost-effective, real-time tracking solution.',
      'Parses raw NMEA GPS data using the TinyGPS++ library to extract accurate latitude and longitude coordinates.',
      'Transmits location data via SMS to a registered mobile number, including a direct Google Maps link for easy viewing.',
      'Sends location updates every 3 seconds, providing near-real-time tracking capability.',
      'Developed as a B.E. (Electronics & Telecommunication) final year project at the University of Mumbai.',
    ]
  },
];

export default function ProjectsGallery() {
  const [expandedId, setExpandedId] = useState<string | null>(null);

  return (
    <section className="py-24 relative" id="projects">
      <div className="max-w-5xl mx-auto px-8">

        <div className="text-center mb-16">
          <motion.h2
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            className="text-3xl md:text-5xl font-bold tracking-tight inline-block px-8 py-3 glass rounded-full"
          >
            My Work
          </motion.h2>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {projects.map((project) => {
            const isExpanded = expandedId === project.id;

            return (
              <motion.div
                key={project.id}
                onClick={() => setExpandedId(isExpanded ? null : project.id)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    setExpandedId(isExpanded ? null : project.id);
                  }
                }}
                role="button"
                tabIndex={0}
                initial={{ opacity: 0, y: 30 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true, margin: "-50px" }}
                className="glass p-6 md:p-8 rounded-3xl cursor-pointer hover:bg-white/10 transition-colors border border-white/10 hover:border-emerald-500/50 outline-none focus-visible:ring-2 focus-visible:ring-emerald-500"
              >
                <h3 className="text-xl md:text-2xl font-bold text-white mb-1">
                  {project.title}
                </h3>
                <p className="text-lime-300 font-medium mb-1 text-sm">
                  {project.client}
                </p>

                <div className="flex flex-wrap gap-2 mb-4">
                  {project.tags.slice(0, isExpanded ? project.tags.length : 4).map(tag => (
                    <span key={tag} className="px-3 py-1 bg-white/5 border border-white/10 rounded-full text-xs font-medium text-white/80">
                      {tag}
                    </span>
                  ))}
                  {!isExpanded && project.tags.length > 4 && (
                    <span className="px-3 py-1 bg-white/5 border border-white/10 rounded-full text-xs font-medium text-white/60">
                      +{project.tags.length - 4}
                    </span>
                  )}
                </div>

                <p className="text-white/80 text-sm">
                  {project.shortDesc}
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
                      <h4 className="text-sm font-bold text-white mb-3 uppercase tracking-wider">Key Contributions</h4>
                      <ul className="space-y-2 text-sm md:text-base">
                        {project.bullets.map((bullet, i) => (
                          <li key={i} className="flex items-start gap-2">
                            <span className="text-emerald-400 mt-1 shrink-0">▸</span>
                            <span>{bullet}</span>
                          </li>
                        ))}
                      </ul>

                      <div className="mt-5 flex flex-wrap gap-3">
                        {project.liveUrl && (
                          <a
                            href={project.liveUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                            onClick={(e) => e.stopPropagation()}
                            className="inline-flex items-center gap-2 px-5 py-2.5 bg-emerald-500/10 rounded-xl text-emerald-400 text-sm font-medium hover:bg-emerald-500/20 transition-colors border border-emerald-500/30 hover:border-emerald-500/50"
                          >
                            Try Live App
                            <ExternalLink className="w-3.5 h-3.5" />
                          </a>
                        )}

                        {project.githubUrl && (
                          <a
                            href={project.githubUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                            onClick={(e) => e.stopPropagation()}
                            className="inline-flex items-center gap-2 px-5 py-2.5 glass rounded-xl text-white text-sm font-medium hover:bg-white/15 transition-colors border border-white/10 hover:border-emerald-500/50"
                          >
                            <GithubIcon className="w-4 h-4" />
                            View Source
                            {!project.liveUrl && <ExternalLink className="w-3.5 h-3.5 text-emerald-400" />}
                          </a>
                        )}
                      </div>
                    </motion.div>
                  )}
                </AnimatePresence>

                <div className="mt-4 flex items-center justify-between">
                  <span className="text-xs font-semibold text-emerald-400 uppercase tracking-wider">
                    {isExpanded ? 'Show Less' : 'Click for Details'}
                  </span>
                  {!isExpanded && (project.githubUrl || project.liveUrl) && (
                    <div className="flex items-center gap-2">
                      {project.liveUrl && (
                        <a
                          href={project.liveUrl}
                          target="_blank"
                          rel="noopener noreferrer"
                          onClick={(e) => e.stopPropagation()}
                          className="p-2 bg-emerald-500/10 rounded-full hover:bg-emerald-500/20 transition-all border border-emerald-500/30 hover:scale-110"
                          title="Try Live App"
                        >
                          <ExternalLink className="w-4 h-4 text-emerald-400" />
                        </a>
                      )}
                      {project.githubUrl && (
                        <a
                          href={project.githubUrl}
                          target="_blank"
                          rel="noopener noreferrer"
                          onClick={(e) => e.stopPropagation()}
                          className="p-2 glass rounded-full hover:bg-white/20 transition-all hover:scale-110"
                          title="View on GitHub"
                        >
                          <GithubIcon className="w-4 h-4 text-white/70" />
                        </a>
                      )}
                    </div>
                  )}
                </div>
              </motion.div>
            );
          })}
        </div>

      </div>
    </section>
  );
}
