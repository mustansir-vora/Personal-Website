"use client";

import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';

const TABS = ['Summary', 'Journey', 'Philosophy'] as const;
type Tab = typeof TABS[number];

const contentMap: Record<Tab, React.ReactNode> = {
  Summary: (
    <div className="space-y-4 text-base md:text-lg text-muted-foreground leading-relaxed">
      <p>
        I’m a <strong className="text-white">technical problem-solver</strong> with over three years of experience getting projects across the finish line. I pride myself on being able to translate complex tech talk into plain English so that everyone—from devs to stakeholders—is on the same page. I love a fast-paced environment and I’m always looking for ways to learn something new or help the team tackle a tough challenge.
      </p>
      <p>
        I am a <strong className="text-white">dependable team player</strong> who consistently delivers excellent work, and I approach every challenge with a <strong className="text-white">proactive, problem-solving mindset</strong>.
      </p>
    </div>
  ),
  Journey: (
    <div className="space-y-4 text-base md:text-lg text-muted-foreground leading-relaxed">
      <p>
        I didn't start with a <strong className="text-white">perfect roadmap;</strong> I started with a steep learning curve. Those first few months were a real test, but they taught me how to adapt and pick up new skills on the fly.
      </p>
      <p>
        I’ve realized that what matters most isn't just what you already know, but how fast you can master what comes next. That mindset has been my biggest asset, helping me take on <strong className="text-white">tough challenges and lead major client integrations</strong> with confidence.
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
              <div className="absolute -top-24 -right-24 w-48 h-48 bg-[radial-gradient(circle,rgba(16,185,129,0.2)_0%,transparent_70%)] rounded-full pointer-events-none"></div>

              <AnimatePresence mode="wait">
                <motion.div
                  key={activeTab}
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: -10 }}
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
