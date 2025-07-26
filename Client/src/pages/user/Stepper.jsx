// Stepper.jsx
import React from "react";
import { useAnalysisStore } from "../../stores/useAnalysisStore";

const STEP_LABEL = {
  idle: "Chờ bắt đầu phân tích",
  "reading-cv": "Đang đọc CV...",
  "reading-job": "Đang lấy danh sách công việc...",
  "ai-analyzing-job": "Đang phân tích AI...",
  done: "Hoàn tất phân tích!",
};

const STEPS = [
  { key: "reading-cv", label: "Đọc CV" },
  { key: "reading-job", label: "Lấy job" },
  { key: "ai-analyzing-job", label: "Phân tích từng job" },
  { key: "done", label: "Hoàn tất" }
];

const Stepper = () => {
  const step = useAnalysisStore((s) => s.analyzeStep);
  const jobIdx = useAnalysisStore((s) => s.analyzeJobIndex);
  const jobTotal = useAnalysisStore((s) => s.analyzeJobTotal);

  // Xác định bước hiện tại
  let currentStep = 0;
  if (step === "reading-cv") currentStep = 0;
  else if (step === "reading-job") currentStep = 1;
  else if (step === "ai-analyzing-job") currentStep = 2;
  else if (step === "done") currentStep = 3;

  // Progress bar: nếu đang phân tích job
  let percent = 0;
  if (currentStep === 2 && jobTotal > 0) {
    percent = Math.round((jobIdx / jobTotal) * 100);
  } else {
    percent = (currentStep / (STEPS.length - 1)) * 100;
  }

  return (
    <div className="w-full flex flex-col items-center my-4">
      {/* Stepper */}
      <div className="flex items-center gap-3 w-full max-w-2xl mb-2">
        {STEPS.map((item, idx) => (
          <React.Fragment key={item.key}>
            <div className={`flex flex-col items-center flex-1`}>
              <div
                className={`rounded-full w-8 h-8 flex items-center justify-center font-bold border-2 ${
                  idx < currentStep
                    ? "bg-blue-600 text-white border-blue-600"
                    : idx === currentStep
                    ? "bg-blue-100 text-blue-800 border-blue-400"
                    : "bg-gray-100 text-gray-400 border-gray-300"
                }`}
              >
                {idx < currentStep ? "✔" : idx + 1}
              </div>
              <span className="text-xs font-semibold mt-2 text-center">
                {item.label}
                {item.key === "ai-analyzing-job" && jobTotal > 0 && currentStep === 2
                  ? ` (${jobIdx}/${jobTotal})`
                  : ""}
              </span>
            </div>
            {idx !== STEPS.length - 1 && (
              <div className={`flex-1 h-1 ${idx < currentStep ? "bg-blue-600" : "bg-gray-300"}`}></div>
            )}
          </React.Fragment>
        ))}
      </div>
      {/* Progress bar */}
      <div className="w-full max-w-2xl h-3 bg-gray-100 rounded-full overflow-hidden">
        <div
          className="h-full bg-blue-500 transition-all duration-300"
          style={{ width: `${percent}%` }}
        ></div>
      </div>
      {/* Status */}
      <div className="mt-3 text-blue-700 font-semibold text-base text-center w-full">
        {step === "ai-analyzing-job" && jobTotal > 0
          ? `Đang phân tích công việc ${jobIdx} / ${jobTotal}...`
          : STEP_LABEL[step] || "Đang xử lý..."}
      </div>
    </div>
  );
};

export default Stepper;
