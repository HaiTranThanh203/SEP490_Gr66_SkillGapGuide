import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import AnalysisResult from "./AddCVWriteJobDescription";
import TopMenu from "./TopMenu";
import { useAnalysisStore } from "../../stores/useAnalysisStore";

// Nếu reload vào result mà chưa có data thì quay lại upload
export default function AnalyzeResult() {
  const navigate = useNavigate();
  const skills = useAnalysisStore((s) => s.skills);

  useEffect(() => {
    if (!skills || skills.length === 0) {
      navigate("/analyze/upload");
    }
  }, [skills, navigate]);

  return (
    <div>
      <TopMenu />
      <AnalysisResult />
    </div>
  );
}
