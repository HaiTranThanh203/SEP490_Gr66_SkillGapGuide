import { motion } from "framer-motion";
import image from "../assets/image.png";
import React, { useState, useEffect } from "react";
import { staticPageService } from "../services/staticPageService";

export default function AboutSection() {
  const [sections, setSections] = useState([]);

  useEffect(() => {
    const fetchSections = async () => {
      try {
        const data = await staticPageService.getAboutUs();
        setSections(data || []);
      } catch (error) {
        console.error("Error fetching about us sections:", error);
      }
    };
    fetchSections();
  }, []);

  return (
    <section className="w-full bg-gradient-to-b from-white to-blue-50 py-16 md:py-24 px-4 md:px-8">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true }}
        transition={{ duration: 0.8 }}
        className="max-w-6xl mx-auto flex flex-col md:flex-row items-center gap-12 md:gap-16"
      >
        {/* Content */}
        <div className="flex-1 min-w-0">
          {sections.map((sec, idx) => (
            <motion.div
              key={sec.title}
              initial={{ opacity: 0, x: -20 * (idx + 1) }}
              whileInView={{ opacity: 1, x: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.6 + idx * 0.1 }}
              className={idx === 0
                ? "mb-8"
                : "bg-white/80 backdrop-blur-sm rounded-2xl p-6 shadow-lg mb-8"}
            >
              <h2 className={`font-bold mb-4 ${idx === 0 ? "text-3xl md:text-4xl text-blue-800" : "text-2xl text-blue-800"}`}>
                {sec.title}
              </h2>
              <p className={`text-gray-700 ${idx === 0 ? "text-lg md:text-xl leading-relaxed font-light" : "text-lg leading-relaxed"}`}>
                {sec.content}
              </p>
            </motion.div>
          ))}

          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            className="bg-gradient-to-r from-yellow-400 to-yellow-500 hover:from-yellow-500 hover:to-yellow-600 
                     text-blue-900 font-bold px-10 py-4 rounded-full shadow-lg transition-all duration-300 
                     text-lg flex items-center gap-2 group"
          >
            Bắt đầu ngay
            <svg
              className="w-5 h-5 transform group-hover:translate-x-1 transition-transform"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M13 7l5 5m0 0l-5 5m5-5H6"
              />
            </svg>
          </motion.button>
        </div>

        {/* Image */}
        <motion.div
          initial={{ opacity: 0, x: 20 }}
          whileInView={{ opacity: 1, x: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.8 }}
          className="flex-1 flex items-center justify-center min-w-[320px]"
        >
          <img
            src={image}
            alt="Về chúng tôi"
            className="w-full max-w-lg rounded-2xl shadow-2xl hover:shadow-3xl transition-shadow duration-300
                     transform hover:scale-[1.02] transition-transform duration-300"
            loading="lazy"
          />
        </motion.div>
      </motion.div>
    </section>
  );
}
