"use client";

import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';

const NAV_ITEMS = [
  { name: 'Home', href: '#home' },
  { name: 'About', href: '#about' },
  { name: 'Experience', href: '#experience' },
  { name: 'Projects', href: '#projects' },
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
    <motion.nav 
      initial={{ y: -100, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      transition={{ type: "spring", stiffness: 100, damping: 20 }}
      className={`fixed bottom-6 md:bottom-auto md:top-0 left-0 right-0 z-50 flex justify-center transition-all duration-500 md:pt-6 ${isScrolled ? 'md:pt-4' : ''}`}
    >
      <div className="flex items-center gap-1 md:gap-2 px-2 md:px-3 py-2 mx-4 md:mx-0 rounded-full border transition-all duration-500 bg-teal-950/40 backdrop-blur-xl border-emerald-500/20 shadow-lg shadow-emerald-900/20">
        {NAV_ITEMS.map((item) => {
          const isActive = activeItem === item.name;
          return (
            <a
              key={item.name}
              href={item.href}
              onClick={(e) => handleClick(e, item.href, item.name)}
              className={`relative px-4 md:px-5 py-2 text-xs md:text-sm font-medium transition-colors rounded-full ${
                isActive ? 'text-teal-950' : 'text-white/70 hover:text-white'
              }`}
            >
              {isActive && (
                <motion.div
                  layoutId="navbar-active-pill"
                  className="absolute inset-0 bg-lime-400 rounded-full -z-10"
                  transition={{ type: "spring", stiffness: 400, damping: 30 }}
                />
              )}
              <span className="relative z-10">{item.name}</span>
            </a>
          );
        })}
      </div>
    </motion.nav>
  );
}
