import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import LoadingAnalyze from "./LoadingAnalyze";
import TopMenu from "./TopMenu";
import { useCVWizardStore } from "../../stores/cvWizardStore";

// Kiểm tra nếu thiếu data thì quay về upload
export default function AnalyzeLoading() {
  const navigate = useNavigate();
  const cvFile = useCVWizardStore((s) => s.cvFile);
  const jobFiles = useCVWizardStore((s) => s.jobFiles);

  useEffect(() => {
    if (!cvFile || !jobFiles || jobFiles.length === 0) {
      navigate("/analyze/upload");
    }
  }, [cvFile, jobFiles, navigate]);

  // Khi loading xong chuyển sang result
  return (
    <div>
      <TopMenu />
      <LoadingAnalyze onFinish={() => navigate("/analyze/result")} />
    </div>
  );
}
