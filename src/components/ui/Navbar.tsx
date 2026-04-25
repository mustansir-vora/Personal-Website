"use client";

import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Phone, Home, User, Briefcase, Quote, FolderKanban, Layers } from 'lucide-react';
import { assetPath } from '@/lib/basePath';

const NAV_ITEMS = [
  { name: 'Home', href: '#home', Icon: Home },
  { name: 'About Me', href: '#about', Icon: User },
  { name: 'Tech Stack', href: '#skills', Icon: Layers },
  { name: 'Experience', href: '#experience', Icon: Briefcase },
  { name: 'Testimonials', href: '#testimonials', Icon: Quote },
  { name: 'Projects', href: '#projects', Icon: FolderKanban },
];

export default function Navbar() {
  const [activeItem, setActiveItem] = useState('Home');
  const [isScrolled, setIsScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 50);
    };

    window.addEventListener('scroll', handleScroll, { passive: true });

    const observerOptions = {
      root: null,
      rootMargin: '-40% 0px -40% 0px',
      threshold: 0
    };

    const observer = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          const navItem = NAV_ITEMS.find(item => item.href.substring(1) === entry.target.id);
          if (navItem) {
            setActiveItem(navItem.name);
          }
        }
      });
    }, observerOptions);

    // Initial check since observer only triggers on crossing threshold
    setTimeout(() => {
      NAV_ITEMS.forEach((item) => {
        const element = document.getElementById(item.href.substring(1));
        if (element) observer.observe(element);
      });
    }, 100);

    return () => {
      window.removeEventListener('scroll', handleScroll);
      observer.disconnect();
    };
  }, []);

  const handleClick = (e: React.MouseEvent<HTMLAnchorElement>, href: string, name: string) => {
    e.preventDefault();
    setActiveItem(name);
    const element = document.getElementById(href.substring(1));
    if (element) {
      window.scrollTo({
        top: element.offsetTop,
        behavior: 'smooth'
      });
    } else if (href === '#home') {
      window.scrollTo({
        top: 0,
        behavior: 'smooth'
      });
    }
  };

  return (
    <>
      {/* DESKTOP NAVBAR (Top Bar) */}
      <motion.nav 
        initial={{ y: -100, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ type: "spring", stiffness: 100, damping: 20 }}
        className={`hidden md:block fixed top-0 left-0 right-0 z-50 transition-all duration-500 ${
          isScrolled ? 'bg-[#050505]/80 backdrop-blur-xl border-b border-white/5 shadow-2xl py-4' : 'bg-transparent py-6'
        }`}
      >
        <div className="max-w-7xl mx-auto px-6 flex items-center justify-between">
          
          {/* Navigation Links */}
          <div className="flex items-center gap-8">
            {NAV_ITEMS.map((item) => {
              const isActive = activeItem === item.name;
              return (
                <a
                  key={item.name + '-desktop'}
                  href={item.href}
                  onClick={(e) => handleClick(e, item.href, item.name)}
                  className={`relative px-5 py-2 text-sm font-medium transition-colors ${
                    isActive ? 'text-lime-400' : 'text-white/60 hover:text-white'
                  }`}
                >
                  <span className="relative z-10">{item.name}</span>
                  {isActive && (
                    <motion.div
                      layoutId="navbar-active-indicator"
                      className="absolute bottom-0 left-0 right-0 h-0.5 bg-lime-400"
                      transition={{ type: "spring", stiffness: 400, damping: 30 }}
                    />
                  )}
                </a>
              );
            })}
          </div>

          {/* Social Icons */}
          <div className="flex items-center gap-6">
            <a href="tel:+918208928683" className="text-white/60 hover:text-white transition-colors hover:scale-110 transform" title="Call Me">
              <Phone className="w-5 h-5 opacity-60 hover:opacity-100 transition-opacity" />
            </a>
            <a href="https://wa.me/918208928683?text=Hi%20Mustansir%2C%20I%20came%20across%20your%20portfolio%20and%20would%20love%20to%20connect.%20I%E2%80%99d%20like%20to%20discuss%20a%20potential%20opportunity%20with%20you." target="_blank" rel="noopener noreferrer" className="text-emerald-500/60 hover:text-emerald-400 transition-colors hover:scale-110 transform" title="WhatsApp">
              <svg className="w-5 h-5 opacity-80 hover:opacity-100 transition-opacity" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
                <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 00-3.48-8.413z"/>
              </svg>
            </a>
            <a href="https://linkedin.com/in/mustansir-vora-56128b216" target="_blank" rel="noopener noreferrer" className="text-white/60 hover:text-white transition-colors hover:scale-110 transform">
              <img src={assetPath('/Assets/LINKEDIN_TOP.svg')} alt="LinkedIn" className="w-5 h-5 invert opacity-60 hover:opacity-100 transition-opacity" />
            </a>
            <a href="https://github.com/mustansir-vora" target="_blank" rel="noopener noreferrer" className="text-white/60 hover:text-white transition-colors hover:scale-110 transform">
              <img src={assetPath('/Assets/GITHUB_TOP.svg')} alt="GitHub" className="w-5 h-5 invert opacity-60 hover:opacity-100 transition-opacity" />
            </a>
          </div>

        </div>
      </motion.nav>

      {/* MOBILE NAVBAR (Icon Pill) */}
      <motion.nav 
        initial={{ y: 100, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ type: "spring", stiffness: 100, damping: 20 }}
        className="md:hidden fixed bottom-6 left-0 right-0 z-50 flex justify-center"
      >
        <div className="flex items-center gap-1 px-3 py-2 rounded-full border bg-teal-950/60 backdrop-blur-xl border-emerald-500/20 shadow-lg shadow-emerald-900/20">
          {NAV_ITEMS.map((item) => {
            const isActive = activeItem === item.name;
            const { Icon } = item;
            return (
              <a
                key={item.name + '-mobile'}
                href={item.href}
                title={item.name}
                onClick={(e) => handleClick(e, item.href, item.name)}
                className={`relative flex items-center justify-center w-11 h-11 rounded-full transition-colors duration-200 ${
                  isActive ? 'text-teal-950' : 'text-white/60 hover:text-white'
                }`}
              >
                {isActive && (
                  <motion.div
                    layoutId="navbar-active-pill"
                    className="absolute inset-0 bg-lime-400 rounded-full -z-10"
                    transition={{ type: "spring", stiffness: 400, damping: 30 }}
                  />
                )}
                <Icon className="w-5 h-5 relative z-10" strokeWidth={isActive ? 2.5 : 1.8} />
              </a>
            );
          })}
        </div>
      </motion.nav>
    </>
  );
}
