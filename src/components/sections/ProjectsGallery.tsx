"use client";

    import React, { useState } from 'react';
    import { motion, AnimatePresence } from 'framer-motion';
    import { X, ExternalLink } from 'lucide-react';

    const projects = [
      {
        id: 'rogers-ivr',
        title: 'Intelligent IVR System',
        client: 'Rogers and Shaw Communications Inc.',
        tags: ['Microsoft Copilot Studio', 'Power Automate', 'Azure'],
        shortDesc: 'End-to-end architecture and deployment of a highly integrated IVR system leveraging the Microsoft Cloud ecosystem.',
        detailedDesc: 'Built core IVR components directly within Microsoft Copilot Studio. Designed seamless integrations using custom connectors, complex business logic orchestration with Power Automate flows, and Azure Functions for robust backend API communication. Resulted in a natural, AI-driven user interaction.'
      },
      {
        id: 'farmers-ivr',
        title: 'Conversational AI & RAG Pattern',
        client: 'Farmers Insurance Group',
        tags: ['Google Dialogflow CX', 'RAG', 'BigQuery'],
        shortDesc: 'Sophisticated IVR and conversational AI solutions focusing on Google Cloud and Cisco technologies.',
        detailedDesc: 'Engineered a sophisticated Retrieval Augmented Generation (RAG) pattern for the SERVICE OPs IVR, allowing real-time accurate data retrieval. Designed complete conversational flows with Dialogflow CX, and created advanced reporting dashboards using BigQuery and Looker Studio.'
      },
      {
        id: 'adcb-bank',
        title: 'Real-time Voice Biometrics IVR',
        client: 'ADCB Bank',
        tags: ['Genesys PureCloud', 'AWS Lambda', 'Voice Biometrics'],
        shortDesc: 'High-performance, secure IVR system featuring real-time voice biometric authentication.',
        detailedDesc: 'Architected a highly scalable IVR utilizing Genesys PureCloud for front-end orchestration and AWS Lambda for event-driven backend logic. A key highlight was implementing real-time voice biometric authentication via Audiohook and a Java backend, significantly enhancing security.'
      },
      {
        id: 'ai-resume',
        title: 'AI Resume Optimizer',
        client: 'Personal Project',
        tags: ['Python', 'Streamlit', 'Google Gemini LLM', 'XML Parsing'],
        shortDesc: 'A system that optimizes a resume against a job description without destroying the original .docx formatting.',
        detailedDesc: 'Leveraged Python, Streamlit, and the Gemini LLM. The core innovation was parsing the .docx as an XML tree to inject AI-optimized text while perfectly preserving the document\'s original formatting, solving a major pain point in existing text-based resume tools.'
      }
    ];

    export default function ProjectsGallery() {
      const [selectedId, setSelectedId] = useState<string | null>(null);

      const selectedProject = projects.find(p => p.id === selectedId);

      return (
        <section className="py-24 relative" id="projects">
          <div className="max-w-7xl mx-auto px-8">

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

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 gap-8">
              {projects.map((project) => (
                <motion.div
                  layoutId={`card-${project.id}`}
                  key={project.id}
                  onClick={() => setSelectedId(project.id)}
                  className="glass p-8 rounded-3xl cursor-pointer hover:bg-white/10 transition-colors border border-white/10 hover:border-emerald-500/50 group flex flex-col h-full"
                >
                  <motion.h3 layoutId={`title-${project.id}`} className="text-2xl font-bold text-white mb-2 group-hover:text-lime-300 transition-colors">
                    {project.title}
                  </motion.h3>
                  <motion.p layoutId={`client-${project.id}`} className="text-lime-300 font-medium mb-4">
                    {project.client}
                  </motion.p>

                  <motion.div layoutId={`tags-${project.id}`} className="flex flex-wrap gap-2 mb-6">
                    {project.tags.map(tag => (
                      <span key={tag} className="px-3 py-1 bg-white/5 border border-white/10 rounded-full text-xs font-medium text-white/80">
                        {tag}
                      </span>
                    ))}
                  </motion.div>

                  <motion.p layoutId={`desc-${project.id}`} className="text-muted-foreground flex-grow">
                    {project.shortDesc}
                  </motion.p>

                  <div className="mt-8 text-sm font-semibold text-emerald-400 uppercase tracking-wider flex items-center gap-2">
                    Read Case Study <ExternalLink className="w-4 h-4" />
                  </div>
                </motion.div>
              ))}
            </div>

          </div>

          {/* Full Screen Modal for Project Details */}
          <AnimatePresence>
            {selectedId && selectedProject && (
              <div className="fixed inset-0 z-50 flex items-center justify-center p-4 md:p-12">
                <motion.div
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  exit={{ opacity: 0 }}
                  onClick={() => setSelectedId(null)}
                  className="absolute inset-0 bg-black/60 backdrop-blur-sm"
                />

                <motion.div
                  layoutId={`card-${selectedProject.id}`}
                  className="glass w-full max-w-4xl max-h-[90vh] overflow-y-auto rounded-3xl p-8 md:p-12 relative z-10 bg-gray-900/90 border border-white/20 shadow-2xl"
                >
                  <button
                    onClick={() => setSelectedId(null)}
                    className="absolute top-6 right-6 p-2 bg-white/10 rounded-full hover:bg-white/20 transition-colors text-white"
                  >
                    <X className="w-6 h-6" />
                  </button>

                  <motion.h3 layoutId={`title-${selectedProject.id}`} className="text-3xl md:text-4xl font-bold text-white mb-2 pr-12">
                    {selectedProject.title}
                  </motion.h3>
                  <motion.p layoutId={`client-${selectedProject.id}`} className="text-xl text-lime-300 font-medium mb-6">
                    {selectedProject.client}
                  </motion.p>

                  <motion.div layoutId={`tags-${selectedProject.id}`} className="flex flex-wrap gap-2 mb-8 pb-8 border-b border-white/10">
                    {selectedProject.tags.map(tag => (
                      <span key={tag} className="px-3 py-1 bg-white/10 border border-white/20 rounded-full text-sm font-medium text-white/90">
                        {tag}
                      </span>
                    ))}
                  </motion.div>

                  <div className="space-y-6 text-lg text-muted-foreground leading-relaxed">
                    <motion.p layoutId={`desc-${selectedProject.id}`} className="text-white font-medium">
                      {selectedProject.shortDesc}
                    </motion.p>

                    <motion.div
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ delay: 0.2 }}
                      className="space-y-4"
                    >
                      <h4 className="text-xl font-bold text-white">Project Details & Contributions:</h4>
                      <p>{selectedProject.detailedDesc}</p>

                      {/* Template placeholder for user to add truncated details */}
                      <div className="p-4 rounded-xl border border-dashed border-emerald-500/50 bg-emerald-500/5 mt-8">
                        <p className="text-sm text-emerald-400 text-center">[ Template: Add your truncated details or bullet points here ]</p>
                      </div>
                    </motion.div>
                  </div>

                </motion.div>
              </div>
            )}
          </AnimatePresence>
        </section>
      );
    }
