// src/stores/useAnalysisStore.js
import { create } from "zustand";
import { persist } from "zustand/middleware";

export const useAnalysisStore = create(
  persist(
    (set) => ({
      skills: [],
      setSkills: (skills) => set({ skills }),

      jobList: [],
      setJobList: (jobList) => set({ jobList }),

      jobDetails: {}, // { [jobId]: { skillGap, commentData, jobSkills, error } }
      setJobDetails: (jobDetails) => set({ jobDetails }),
      isAnalysisLoading: false,
      analyzeStep: "idle",
      setAnalyzeStep: (step) => set({ analyzeStep: step }),
      setAnalysisLoading: (isLoading) => set({ isAnalysisLoading: isLoading }),
      clearAll: () => set({ skills: [], jobList: [], jobDetails: {} }),
      analyzeJobIndex: 0,
      setAnalyzeJobIndex: (i) => set({ analyzeJobIndex: i }),
      analyzeJobTotal: 0,
      setAnalyzeJobTotal: (n) => set({ analyzeJobTotal: n }),
    }),
    {
      name: "analysis-persist",
    }
  )
);
