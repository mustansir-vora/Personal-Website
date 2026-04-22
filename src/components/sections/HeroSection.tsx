"use client";

import React from 'react';
import { motion } from 'framer-motion';
import { ArrowRight, Download, Mail } from 'lucide-react';

export default function HeroSection() {
  return (
    <section id="home" className="min-h-screen flex flex-col items-center justify-center relative p-8 pb-32 md:pb-8">
      {/* Background ambient effects */}
      <div className="absolute top-1/4 -left-10 w-96 h-96 bg-teal-600/20 rounded-full mix-blend-screen filter blur-[100px] animate-blob"></div>
      <div className="absolute top-1/3 -right-10 w-96 h-96 bg-emerald-600/20 rounded-full mix-blend-screen filter blur-[100px] animate-blob" style={{ animationDelay: '2s' }}></div>
      <div className="absolute -bottom-20 left-1/2 -translate-x-1/2 w-96 h-96 bg-lime-600/10 rounded-full mix-blend-screen filter blur-[100px] animate-blob" style={{ animationDelay: '4s' }}></div>

      <div className="z-10 w-full max-w-5xl flex flex-col items-center text-center space-y-8 mt-4 md:mt-20">
        
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, ease: "easeOut" }}
          className="inline-block glass px-4 py-1.5 rounded-full text-sm font-medium text-lime-400 mb-4"
        >
          Open to new opportunities
        </motion.div>

        <motion.h1 
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, delay: 0.2, ease: "easeOut" }}
          className="text-5xl md:text-7xl lg:text-8xl font-bold tracking-tight"
        >
          Hi, I'm <span className="text-gradient">Mustansir</span>
        </motion.h1>

        <motion.p 
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, delay: 0.4, ease: "easeOut" }}
          className="text-xl md:text-2xl text-muted-foreground max-w-3xl leading-relaxed"
        >
          Full Stack Developer & AI Enthusiast specializing in <span className="text-white font-semibold">Agentic AI</span>, complex backend integrations, and modern <span className="text-white font-semibold">Conversational AI</span> solutions.
        </motion.p>
        
        <motion.div 
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, delay: 0.6, ease: "easeOut" }}
          className="flex flex-col sm:flex-row items-center gap-4 pt-8"
        >
          <a href="mailto:voramustansir9278@gmail.com" className="group relative inline-flex items-center justify-center px-8 py-3.5 text-base font-bold text-teal-950 bg-lime-400 rounded-full overflow-hidden transition-all duration-300 hover:scale-105 hover:shadow-[0_0_20px_rgba(190,242,100,0.5)]">
            <span className="relative z-10 flex items-center gap-2">
              Let's Connect <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
            </span>
            <div className="absolute inset-0 h-full w-full bg-gradient-to-r from-lime-400 to-emerald-400 scale-[1.2] opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
          </a>
          
          <a href="https://drive.google.com/uc?export=download&id=1E49zMuKNj9QgAAHkoFHGyz8JV0q7H73W" download className="group inline-flex items-center justify-center px-8 py-3.5 text-base font-bold text-white glass rounded-full transition-all duration-300 hover:bg-white/10 hover:scale-105">
            <span className="flex items-center gap-2">
              Download Resume <Download className="w-4 h-4 group-hover:-translate-y-1 transition-transform" />
            </span>
          </a>
        </motion.div>

        <motion.div 
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 1, delay: 1 }}
          className="flex items-center gap-6 pt-6 md:pt-12"
        >
          <a href="https://linkedin.com/in/mustansir-vora-56128b216" target="_blank" rel="noopener noreferrer" className="p-3 glass rounded-full hover:bg-white/20 transition-colors hover:scale-110 hover:-translate-y-1">
            <img src="/Personal-Website/Assets/LINKEDIN_TOP.svg" alt="LinkedIn" className="w-6 h-6 invert" />
          </a>
          <a href="https://github.com/mustansir-vora" target="_blank" rel="noopener noreferrer" className="p-3 glass rounded-full hover:bg-white/20 transition-colors hover:scale-110 hover:-translate-y-1">
            <img src="/Personal-Website/Assets/GITHUB_TOP.svg" alt="GitHub" className="w-6 h-6 invert" />
          </a>
          <a href="mailto:voramustansir9278@gmail.com" className="p-3 glass rounded-full hover:bg-white/20 transition-colors hover:scale-110 hover:-translate-y-1">
            <Mail className="w-6 h-6 text-emerald-400" />
          </a>
        </motion.div>

      </div>
      
      {/* Scroll indicator */}
      <motion.div 
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 1, delay: 1.5 }}
        className="relative mt-20 flex flex-col items-center gap-2 pb-10"
      >
        <span className="text-xs text-muted-foreground uppercase tracking-widest">Scroll</span>
        <div className="w-px h-12 bg-gradient-to-b from-muted-foreground to-transparent relative overflow-hidden">
          <div className="absolute top-0 left-0 w-full h-1/2 bg-white animate-[scroll-down_1.5s_ease-in-out_infinite]"></div>
        </div>
      </motion.div>
    </section>
  );
}
