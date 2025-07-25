import React from "react";
import { useNavigate } from "react-router-dom";
import CVUploadOptions from "./CVUploadOptions";
import TopMenu from "./TopMenu";

export default function AnalyzeUpload() {
  const navigate = useNavigate();
  // Khi upload xong chuyển sang loading
  return (
    <div>
      <TopMenu />
      <CVUploadOptions onNext={() => navigate("/analyze/loading")} />
    </div>
  );
}
