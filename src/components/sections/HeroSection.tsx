"use client";

import React from 'react';
import { motion } from 'framer-motion';
import { Download, Phone, Mail } from 'lucide-react';
import { assetPath } from '@/lib/basePath';

const CLIENTS = [
  { name: 'Rogers communication Inc.', icon: '/Assets/ROGERS.svg' },
  { name: 'Farmers Insurance Group', icon: '/Assets/FARMERSINSURANCE.svg' },
  { name: 'Conduent Inc.', icon: '/Assets/CONDUENT.svg' },
  { name: 'ADCB (Abu Dhabi Central Bank)', icon: '/Assets/ADCB.svg' },
  { name: 'FIS EBT (Electronic Benefit Transfer)', icon: '/Assets/FIS.svg' }
];

export default function HeroSection() {
  return (
    <section id="home" className="min-h-screen flex flex-col items-center justify-center relative px-6 pt-16 pb-32 md:p-0 md:pt-24 md:pb-12 overflow-hidden w-full max-w-[100vw]">
      {/* Background ambient effects */}
      <div className="absolute top-1/4 -left-10 w-96 h-96 bg-[radial-gradient(circle,rgba(13,148,136,0.2)_0%,transparent_70%)] rounded-full blur-3xl pointer-events-none transform-gpu"></div>
      <div className="absolute top-1/3 -right-10 w-96 h-96 bg-[radial-gradient(circle,rgba(16,185,129,0.2)_0%,transparent_70%)] rounded-full blur-3xl pointer-events-none transform-gpu"></div>
      <div className="absolute -bottom-20 left-1/2 -translate-x-1/2 w-96 h-96 bg-[radial-gradient(circle,rgba(190,242,100,0.1)_0%,transparent_70%)] rounded-full blur-3xl pointer-events-none transform-gpu"></div>

      {/* 2-Column Split Container */}
      <div className="max-w-7xl mx-auto w-full px-6 flex flex-col lg:flex-row items-center justify-between gap-12 lg:gap-8 mt-0 md:mt-10">

        {/* Left Column */}
        <div className="w-full lg:w-1/2 flex flex-col items-center lg:items-start text-center lg:text-left z-10 space-y-6 md:space-y-8">

          {/* Mobile Portrait Sphere */}
          <motion.div 
            initial={{ opacity: 0, scale: 0.8 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ duration: 1.2, ease: "easeOut" }}
            className="block lg:hidden relative w-[150px] h-[150px] mx-auto"
          >
            {/* Outer ambient glow */}
            <div className="absolute inset-[5%] rounded-full bg-emerald-500/10 blur-xl pointer-events-none" />

            {/* Outer rotating ring */}
            <motion.div 
              animate={{ rotate: 360 }}
              transition={{ duration: 30, repeat: Infinity, ease: "linear" }}
              className="absolute inset-0 rounded-full border border-emerald-500/30 border-t-lime-400/70 shadow-[0_0_30px_rgba(16,185,129,0.15)] transform-gpu will-change-transform"
              style={{ margin: '8%' }}
            />

            {/* Second counter-rotating ring */}
            <motion.div 
              animate={{ rotate: -360 }}
              transition={{ duration: 20, repeat: Infinity, ease: "linear" }}
              className="absolute inset-0 rounded-full border border-white/10 border-b-lime-400/30 transform-gpu will-change-transform"
              style={{ margin: '14%' }}
            />

            {/* Portrait sphere */}
            <div className="relative w-[68%] h-[68%] rounded-full overflow-hidden border border-white/20 shadow-[0_0_20px_rgba(16,185,129,0.25)] top-[16%] left-[16%]">
              <img
                src={assetPath('/Assets/PORTRAIT.png')}
                alt="Mustansir Vora"
                className="w-full h-full object-cover object-[calc(50%-5px)_55%]"
              />
              <div className="absolute inset-0 bg-gradient-to-t from-[#050505]/60 via-transparent to-transparent pointer-events-none" />
              <div className="absolute top-2 left-3 w-1/3 h-1/5 rounded-full bg-gradient-to-b from-white/30 to-transparent rotate-[-30deg] blur-[2px] pointer-events-none" />
            </div>
          </motion.div>

          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8, ease: "easeOut" }}
            className="inline-block glass px-4 py-1.5 rounded-full text-sm font-medium text-lime-400"
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
            className="text-xl md:text-2xl text-muted-foreground max-w-2xl leading-relaxed"
          >
            Full Stack Developer & AI Enthusiast specializing in <span className="text-white font-semibold">Agentic AI</span>, complex backend integrations, and modern <span className="text-white font-semibold">Conversational AI</span> solutions.
          </motion.p>

          {/* CTA Buttons */}
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8, delay: 0.6, ease: "easeOut" }}
            className="flex flex-col sm:flex-row items-center justify-center lg:justify-start gap-4 pt-4"
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

          {/* Social Icons for Mobile ONLY */}
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 1, delay: 1 }}
            className="flex md:hidden items-center justify-center gap-6 pt-6"
          >
            <a href="https://linkedin.com/in/mustansir-vora-56128b216" target="_blank" rel="noopener noreferrer" className="p-3 glass rounded-full hover:bg-white/20 transition-colors">
              <img src={assetPath('/Assets/LINKEDIN_TOP.svg')} alt="LinkedIn" className="w-5 h-5 invert" />
            </a>
            <a href="https://github.com/mustansir-vora" target="_blank" rel="noopener noreferrer" className="p-3 glass rounded-full hover:bg-white/20 transition-colors">
              <img src={assetPath('/Assets/GITHUB_TOP.svg')} alt="GitHub" className="w-5 h-5 invert" />
            </a>
            <a href="https://wa.me/918208928683?text=Hi%20Mustansir%2C%20I%20came%20across%20your%20portfolio%20and%20would%20love%20to%20connect.%20I%E2%80%99d%20like%20to%20discuss%20a%20potential%20opportunity%20with%20you." target="_blank" rel="noopener noreferrer" className="p-3 glass rounded-full hover:bg-white/20 transition-colors">
              <svg className="w-5 h-5 text-emerald-400" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
                <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 00-3.48-8.413z" />
              </svg>
            </a>
            <a href="mailto:voramustansir9278@gmail.com" className="p-3 glass rounded-full hover:bg-white/20 transition-colors">
              <Mail className="w-5 h-5 text-emerald-400" />
            </a>
          </motion.div>

        </div>

        {/* Right Column - 3D Glassmorphic Sphere with Portrait */}
        <motion.div
          initial={{ opacity: 0, scale: 0.8 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 1.2, delay: 0.4, ease: "easeOut" }}
          className="hidden lg:flex flex-1 justify-center items-center relative w-full aspect-square mt-10 lg:mt-0 max-w-[500px]"
        >
          {/* Outer ambient glow */}
          <div className="absolute inset-[5%] rounded-full bg-emerald-500/10 blur-3xl pointer-events-none" />

          {/* Outer rotating ring */}
          <motion.div
            animate={{ rotate: 360 }}
            transition={{ duration: 30, repeat: Infinity, ease: "linear" }}
            className="absolute inset-0 rounded-full border border-emerald-500/30 border-t-lime-400/70 shadow-[0_0_60px_rgba(16,185,129,0.15)] transform-gpu will-change-transform"
            style={{ margin: '8%' }}
          />

          {/* Second counter-rotating ring */}
          <motion.div
            animate={{ rotate: -360 }}
            transition={{ duration: 20, repeat: Infinity, ease: "linear" }}
            className="absolute inset-0 rounded-full border border-white/10 border-b-lime-400/30 transform-gpu will-change-transform"
            style={{ margin: '14%' }}
          />

          {/* Portrait sphere */}
          <div className="relative w-[68%] h-[68%] rounded-full overflow-hidden border-2 border-white/20 shadow-[0_0_40px_rgba(16,185,129,0.25)]">
            {/* The photo — object-position moves the crop window up to center the face */}
            <img
              src={assetPath('/Assets/PORTRAIT.png')}
              alt="Mustansir Vora"
              className="w-full h-full object-cover object-[calc(100%-5px)_55%]"
            />

            {/* Subtle gradient overlay for depth — heavier at bottom so it blends with the dark bg */}
            <div className="absolute inset-0 bg-gradient-to-t from-[#050505]/60 via-transparent to-transparent pointer-events-none" />

            {/* Glass reflection highlight */}
            <div className="absolute top-3 left-5 w-1/3 h-1/5 rounded-full bg-gradient-to-b from-white/30 to-transparent rotate-[-30deg] blur-[3px] pointer-events-none" />
          </div>
        </motion.div>
      </div>

      {/* Scroll indicator */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 1, delay: 1.5 }}
        className="relative mt-12 md:mt-20 flex flex-col items-center gap-2 pb-6 md:pb-10"
      >
        <span className="text-xs text-muted-foreground uppercase tracking-widest">Scroll</span>
        <div className="w-px h-12 bg-gradient-to-b from-muted-foreground to-transparent relative overflow-hidden">
          <div className="absolute top-0 left-0 w-full h-1/2 bg-white animate-[scroll-down_1.5s_ease-in-out_infinite]"></div>
        </div>
      </motion.div>

      {/* Worked With Banner */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 1, delay: 1.2 }}
        className="w-full mt-4 md:mt-12 flex flex-col items-center"
      >
        <span className="text-xs font-semibold text-white/50 tracking-[0.2em] uppercase mb-8">
          Worked with
        </span>
        <div className="w-full max-w-7xl relative overflow-hidden [mask-image:linear-gradient(to_right,transparent,black_10%,black_90%,transparent)]">
          <div className="flex w-max animate-[marquee_40s_linear_infinite] gap-12 md:gap-24 pr-12 md:pr-24 items-center opacity-80 hover:opacity-100 transition-opacity transform-gpu will-change-transform">
            {/* Original track */}
            {CLIENTS.map((client, i) => (
              <div key={`original-${i}`} className="flex items-center justify-center min-w-[120px] md:min-w-[160px]">
                <img src={assetPath(client.icon)} alt={client.name} className="h-8 md:h-11 w-auto object-contain max-w-[120px] md:max-w-[160px]" />
              </div>
            ))}
            {/* Duplicate track */}
            {CLIENTS.map((client, i) => (
              <div key={`dup1-${i}`} className="flex items-center justify-center min-w-[120px] md:min-w-[160px]">
                <img src={assetPath(client.icon)} alt={client.name} className="h-8 md:h-11 w-auto object-contain max-w-[120px] md:max-w-[160px]" />
              </div>
            ))}
            {/* Duplicate track 2 */}
            {CLIENTS.map((client, i) => (
              <div key={`dup2-${i}`} className="flex items-center justify-center min-w-[120px] md:min-w-[160px]">
                <img src={assetPath(client.icon)} alt={client.name} className="h-8 md:h-11 w-auto object-contain max-w-[120px] md:max-w-[160px]" />
              </div>
            ))}
          </div>
        </div>
      </motion.div>
    </section>
  );
}
