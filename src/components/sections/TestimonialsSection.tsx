"use client";

import React from 'react';
import { motion, Variants } from 'framer-motion';
import { Quote } from 'lucide-react';

const testimonials = [
  {
    name: "Ralyea Daphne",
    title: "Client, Conduent",
    content: "I want you both to know how much I appreciate the Servion team that I've been working closely with. Most recently, Mustansir has been an incredible resource. They were able to really understand the client and see through to the core issues. Their responses were thoughtful and thorough. Mustansir came up with a great idea for 'hidden' intents that I think will address the client concern but will safeguard our containment as well. Thank you for your partnership!!",
  },
  {
    name: "Deepak Krishna",
    title: "Director - Servion",
    content: "Mustansir is a once in a generation find for us who can be groomed well to lead more complex integrations and lead teams in immediate future. He should continue on this path and build on his success. Despite being a junior he was able to clearly articulate and communicate with peers and leads and delivers work well. Very good feedback both from internal team and customer too.",
  },
  {
    name: "Pavithra N",
    title: "Senior Technical Architect - Servion",
    content: "Mustansir proactively engages with customers to de-escalate issues and rebuild trust. He played a pivotal role in the successful delivery of the Farmers Insurance project, demonstrating remarkable independence. He consistently owns assigned tasks and projects, ensuring their successful delivery. He consistently delivers high-quality code, demonstrating a strong understanding of coding principles and a focus on future adaptability and reusability.",
  },
  {
    name: "Vengatesh Ramachandran",
    title: "Senior Director - Servion",
    content: "Thanks for your great effort on this project. You being in the project, shows how much of a PM effort will reduce when we have strong, flexible and owners of deliverables technical member in the team. Overall, the project exemplifies outstanding collaboration. At the beginning of the project, entire Farmers (Client) team were having big doubts on the deliverables. At the end, we are the only team able to deliver without any hiccups. This means a lot.",
  }
];

const containerVariants: Variants = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: {
      staggerChildren: 0.2,
    },
  },
};

const itemVariants: Variants = {
  hidden: { y: 20, opacity: 0 },
  visible: {
    y: 0,
    opacity: 1,
    transition: {
      type: "spring" as const,
      stiffness: 100,
      damping: 20,
    },
  },
};

export default function TestimonialsSection() {
  return (
    <section id="testimonials" className="relative py-24 md:py-32 overflow-hidden scroll-mt-20">
      <div className="container mx-auto px-6 relative z-10">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.5 }}
          className="text-center mb-16"
        >
          <h2 className="text-4xl md:text-5xl font-bold mb-6 text-white tracking-tight">
            Leadership & <span className="text-emerald-400">Client Impact</span>
          </h2>
          <p className="text-white/60 max-w-2xl mx-auto text-lg">
            Feedback from stakeholders, directors, and clients I&apos;ve had the pleasure of working with.
          </p>
        </motion.div>

        <motion.div
          variants={containerVariants}
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true }}
          className="grid grid-cols-1 md:grid-cols-2 gap-6 md:gap-8 max-w-7xl mx-auto"
        >
          {testimonials.map((testimonial, index) => (
            <motion.div
              key={index}
              variants={itemVariants}
              className="relative p-8 rounded-3xl glass border border-white/10 hover:border-emerald-500/30 transition-all duration-300 group flex flex-col justify-between"
            >
              {/* Quote Icon Background */}
              <div className="absolute top-6 right-8 text-emerald-500/10 group-hover:text-emerald-500/20 transition-colors duration-300">
                <Quote size={80} strokeWidth={1} />
              </div>

              <div className="relative z-10 mb-8">
                <Quote className="text-emerald-400 w-8 h-8 mb-4 opacity-50" />
                <p className="text-white/80 leading-relaxed italic text-sm md:text-base">
                  &quot;{testimonial.content}&quot;
                </p>
              </div>

              <div className="relative z-10 flex items-center gap-4 mt-auto">
                <div className="w-12 h-12 rounded-full bg-gradient-to-br from-emerald-500/20 to-teal-500/20 flex items-center justify-center border border-emerald-500/30">
                  <span className="text-emerald-400 font-bold text-lg">
                    {testimonial.name.charAt(0)}
                  </span>
                </div>
                <div>
                  <h4 className="text-white font-bold">{testimonial.name}</h4>
                  <p className="text-emerald-400/80 text-sm font-medium">{testimonial.title}</p>
                </div>
              </div>
            </motion.div>
          ))}
        </motion.div>
      </div>

      {/* Decorative ambient blobs */}
      <div className="absolute top-1/2 left-0 w-96 h-96 bg-emerald-500/10 blur-[100px] rounded-full transform -translate-y-1/2 -translate-x-1/2 pointer-events-none transform-gpu" />
      <div className="absolute top-1/2 right-0 w-96 h-96 bg-teal-500/10 blur-[100px] rounded-full transform -translate-y-1/2 translate-x-1/2 pointer-events-none transform-gpu" />
    </section>
  );
}
