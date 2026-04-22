"use client";

import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';

const TABS = ['Summary', 'Journey', 'Philosophy'] as const;
type Tab = typeof TABS[number];

const contentMap: Record<Tab, React.ReactNode> = {
  Summary: (
    <div className="space-y-4 text-base md:text-lg text-muted-foreground leading-relaxed">
      <p>
        I'm an <strong className="text-white">Efficient & Efficacious professional</strong> with a track record of successfully completing projects and resolving challenging technical issues. With more than three years of experience, I am an excellent communicator with both technical and non-technical teams, ensuring a cooperative approach to each project.
      </p>
      <p>
        My commitment to continuous learning and adaptability allows me to thrive in fast-paced environments. I am a dependable team player who consistently delivers excellent work, and I approach every challenge with a <strong className="text-white">proactive, problem-solving mindset</strong>.
      </p>
    </div>
  ),
  Journey: (
    <div className="space-y-4 text-base md:text-lg text-muted-foreground leading-relaxed">
      <p>
        My journey into tech began not with a clear path, but with a <strong className="text-white">formidable challenge</strong>. The initial months were a crucible, forging my sense of adaptability and a passion for rapid, continuous learning.
      </p>
      <p>
        It taught me that a person's true measure isn't just their knowledge, but their ability to acquire and master new skills efficiently. This foundation has been my launchpad, enabling me to turn challenges into opportunities and lead <strong className="text-white">complex, high-stakes client integrations</strong>.
      </p>
    </div>
  ),
  Philosophy: (
    <div className="space-y-4 text-base md:text-lg text-muted-foreground leading-relaxed">
      <p>
        Looking ahead, my ambitions extend beyond mere financial success; it is to establish a thriving career that operates with some level of autonomy, giving me the freedom to pursue my passion for <strong className="text-white">global travel and adrenaline-fueled adventures</strong>.
      </p>
      <p>
        Ultimately, I seek a future where professional success serves as a means to an end, enabling me to a life rich in experiences. This chase for a balance—where <strong className="text-white">professional achievement enables personal exploration</strong>—is what I would define as my ultimate goal.
      </p>
    </div>
  )
};

export default function AboutSection() {
  const [activeTab, setActiveTab] = useState<Tab>('Summary');

  return (
    <section className="py-24 relative overflow-hidden" id="about">
      <div className="max-w-6xl mx-auto px-8 relative z-10">

        <div className="text-center mb-16">
          <motion.h2
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-100px" }}
            className="text-3xl md:text-5xl font-bold tracking-tight inline-block px-8 py-3 glass rounded-full"
          >
            About Me
          </motion.h2>
        </div>

        <div className="flex flex-col lg:flex-row gap-12 items-start">

          {/* Interactive Tabs */}
          <motion.div
            initial={{ opacity: 0, x: -30 }}
            whileInView={{ opacity: 1, x: 0 }}
            viewport={{ once: true }}
            className="w-full lg:w-1/3 flex flex-row flex-wrap lg:flex-col gap-2 justify-center lg:justify-start pb-4 lg:pb-0"
          >
            {TABS.map((tab) => (
              <button
                key={tab}
                onClick={() => setActiveTab(tab)}
                className={`relative px-4 md:px-6 py-3 md:py-4 text-center lg:text-left font-medium rounded-xl transition-colors whitespace-nowrap ${activeTab === tab ? 'text-white' : 'text-muted-foreground hover:text-white/80 hover:bg-white/5'
                  }`}
              >
                {activeTab === tab && (
                  <motion.div
                    layoutId="activeTab"
                    className="absolute inset-0 bg-white/10 border border-white/20 rounded-xl"
                    initial={false}
                    transition={{ type: "spring", bounce: 0.2, duration: 0.6 }}
                  />
                )}
                <span className="relative z-10 text-base md:text-xl">{tab}</span>
              </button>
            ))}
          </motion.div>

          {/* Tab Content */}
          <motion.div
            initial={{ opacity: 0, x: 30 }}
            whileInView={{ opacity: 1, x: 0 }}
            viewport={{ once: true }}
            className="w-full lg:w-2/3 min-h-[300px]"
          >
            <div className="glass p-6 md:p-12 rounded-3xl relative overflow-hidden">
              {/* Subtle accent glow */}
              <div className="absolute -top-24 -right-24 w-48 h-48 bg-emerald-500/20 blur-3xl rounded-full"></div>

              <AnimatePresence mode="wait">
                <motion.div
                  key={activeTab}
                  initial={{ opacity: 0, y: 10, filter: 'blur(4px)' }}
                  animate={{ opacity: 1, y: 0, filter: 'blur(0px)' }}
                  exit={{ opacity: 0, y: -10, filter: 'blur(4px)' }}
                  transition={{ duration: 0.3 }}
                >
                  <h3 className="text-2xl font-bold text-white mb-6">{activeTab}</h3>
                  {contentMap[activeTab]}
                </motion.div>
              </AnimatePresence>
            </div>
          </motion.div>

        </div>
      </div>
    </section>
  );
}
