import React, { useState } from "react";
import CVUploadOptions from "./CVUploadOptions";
import LoadingAnalyze from "./LoadingAnalyze";
import AddCVWriteJobDescription from "./AddCVWriteJobDescription";

export default function MainAnalysisPage() {
  const [stage, setStage] = useState("upload"); // upload | analyzing | result

  if (stage === "upload")
    return <CVUploadOptions onNext={() => setStage("analyzing")} />;
  if (stage === "analyzing")
    return <LoadingAnalyze onFinish={() => setStage("result")} />;
  return <AddCVWriteJobDescription />;
}
