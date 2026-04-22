"use client";

import React from 'react';
import { motion } from 'framer-motion';
import { ArrowRight, Download, Mail, Phone } from 'lucide-react';
import { assetPath } from '@/lib/basePath';

export default function HeroSection() {
  return (
    <section id="home" className="min-h-screen flex flex-col items-center justify-center relative p-8 pb-32 md:pb-8">
      {/* Background ambient effects */}
      <div className="absolute top-1/4 -left-10 w-96 h-96 bg-teal-600/20 rounded-full mix-blend-screen filter blur-[60px] animate-blob"></div>
      <div className="absolute top-1/3 -right-10 w-96 h-96 bg-emerald-600/20 rounded-full mix-blend-screen filter blur-[60px] animate-blob" style={{ animationDelay: '2s' }}></div>
      <div className="absolute -bottom-20 left-1/2 -translate-x-1/2 w-96 h-96 bg-lime-600/10 rounded-full mix-blend-screen filter blur-[60px] animate-blob" style={{ animationDelay: '4s' }}></div>

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
          <a href="tel:+918208928683" className="group relative inline-flex items-center justify-center px-8 py-3.5 text-base font-bold text-teal-950 bg-lime-400 rounded-full overflow-hidden transition-all duration-300 hover:scale-105 hover:shadow-[0_0_20px_rgba(190,242,100,0.5)]">
            <span className="relative z-10 flex items-center gap-2">
              Let's Connect <Phone className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
            </span>
            <div className="absolute inset-0 h-full w-full bg-gradient-to-r from-lime-400 to-emerald-400 scale-[1.2] opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
          </a>
          
          <a href="https://drive.google.com/file/d/1IDdD7Yk5wW6vA0Qz1ve3gtdQR8iTbkO7/view?usp=sharing" target="_blank" rel="noopener noreferrer" className="group inline-flex items-center justify-center px-8 py-3.5 text-base font-bold text-white glass rounded-full transition-all duration-300 hover:bg-white/10 hover:scale-105">
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
            <img src={assetPath('/Assets/LINKEDIN_TOP.svg')} alt="LinkedIn" className="w-6 h-6 invert" />
          </a>
          <a href="https://github.com/mustansir-vora" target="_blank" rel="noopener noreferrer" className="p-3 glass rounded-full hover:bg-white/20 transition-colors hover:scale-110 hover:-translate-y-1">
            <img src={assetPath('/Assets/GITHUB_TOP.svg')} alt="GitHub" className="w-6 h-6 invert" />
          </a>
          <a href="https://wa.me/918208928683?text=Hi%20Mustansir%2C%20I%20came%20across%20your%20portfolio%20and%20would%20love%20to%20connect.%20I%E2%80%99d%20like%20to%20discuss%20a%20potential%20opportunity%20with%20you." target="_blank" rel="noopener noreferrer" className="p-3 glass rounded-full hover:bg-white/20 transition-colors hover:scale-110 hover:-translate-y-1">
            <svg className="w-6 h-6 text-emerald-400" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
              <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 00-3.48-8.413z"/>
            </svg>
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
