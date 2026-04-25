import type { Metadata } from "next";
import { Outfit } from "next/font/google";
import "./globals.css";
import CustomCursor from "@/components/ui/CustomCursor";
import Navbar from "@/components/ui/Navbar";

const outfit = Outfit({
  subsets: ["latin"],
  variable: "--font-sans",
});

export const metadata: Metadata = {
  title: "Mustansir Vora - AI & Full Stack Developer",
  description: "Portfolio of Mustansir Vora, a Full Stack Developer and AI Enthusiast specializing in Agentic AI and Conversational AI systems.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html
      lang="en"
      className={`${outfit.variable} font-sans h-full antialiased scroll-smooth`}
    >
      <body className="min-h-full flex flex-col selection:bg-lime-400 selection:text-teal-950 overflow-x-hidden w-full">
        <div className="noise-bg" />
        <CustomCursor />
        <Navbar />
        {children}
      </body>
    </html>
  );
}
