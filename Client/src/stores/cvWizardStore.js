import { create } from "zustand";

export const useCVWizardStore = create((set) => ({
  // State lưu file CV
  cvFile: null,
  setCVFile: (cvFile) => set({ cvFile }),

  // State lưu kết quả upload CV (tùy ý)
  uploadResult: null,
  setUploadResult: (uploadResult) => set({ uploadResult }),

  // State lưu danh sách link TOPCV
  topcvLinks: [],
  setTopcvLinks: (topcvLinks) => set({ topcvLinks }),

  // Các state khác bạn muốn lưu giữa các bước:
  jobFiles: [],
  setJobFiles: (jobFiles) => set({ jobFiles }),

  // Nếu cần clear toàn bộ khi user hoàn thành/quay lại từ đầu:
  resetWizard: () =>
    set({
      cvFile: null,
      uploadResult: null,
      topcvLinks: [],
      jobFiles: [],
      // reset các state khác tuỳ bạn!
    }),
}));
