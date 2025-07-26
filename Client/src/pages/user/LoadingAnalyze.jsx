import React, { useEffect, useState } from "react";
import { useAnalysisStore } from "../../stores/useAnalysisStore";
import { cvService } from "../../services/cvJobService";
import { skillGapService } from "../../services/skillGapService";

const stepLabels = [
  "Đang đọc kỹ năng từ CV...",
  "Đang lấy danh sách công việc...",
  "Đang phân tích AI từng công việc...",
  "Hoàn thành!"
];

const LoadingAnalyze = ({ onFinish }) => {
  const setSkills = useAnalysisStore((s) => s.setSkills);
  const setJobList = useAnalysisStore((s) => s.setJobList);
  const setJobDetails = useAnalysisStore((s) => s.setJobDetails);
  const clearAll = useAnalysisStore((s) => s.clearAll);
  const setAnalyzeStep = useAnalysisStore((s) => s.setAnalyzeStep);
  const setAnalyzeJobIndex = useAnalysisStore((s) => s.setAnalyzeJobIndex);
  const setAnalyzeJobTotal = useAnalysisStore((s) => s.setAnalyzeJobTotal);

  const [step, setStep] = useState(0);
  const [progress, setProgress] = useState(0);

  useEffect(() => {
    let isCanceled = false;
    clearAll(); // luôn reset khi vào loading

    (async () => {
      // 1. Lấy kỹ năng CV
      setAnalyzeStep("reading-cv");
      setStep(0);
      setProgress(10);
      const skillRes = await cvService.getCVSkill();
      setSkills(skillRes.result || []);
      setProgress(25);
      if (isCanceled) return;

      // 2. Lấy danh sách job
      setAnalyzeStep("reading-job");
      setStep(1);
      setProgress(35);
      const jobRes = await skillGapService.getJobList();
      const jobs = jobRes.result || [];
      setJobList(jobs);
      setProgress(50);
      if (isCanceled) return;

      // 3. Phân tích từng job
      setAnalyzeStep("ai-analyzing-job");
      setStep(2);
      let jobDetails = {};
      setAnalyzeJobTotal(jobs.length);

      for (let i = 0; i < jobs.length; ++i) {
        setAnalyzeJobIndex(i + 1); // 1-based cho user
        // Cập nhật progress bar theo từng job (50 -> 95%)
        setProgress(50 + Math.floor(((i + 1) / jobs.length) * 45));
        const job = jobs[i];
        try {
          const [gapRes, cmtRes, jobSkillsRes] = await Promise.all([
            skillGapService.getSkillGap(job.jobId, job.cvId),
            skillGapService.getCommentSkill(job.jobId, job.cvId),
            skillGapService.getJobSkills(job.jobId),
          ]);
          jobDetails[job.jobId] = {
            skillGap: gapRes.result || [],
            commentData: cmtRes.result || {},
            jobSkills: jobSkillsRes.result || [],
            error: null,
          };
        } catch (err) {
          jobDetails[job.jobId] = {
            skillGap: [],
            commentData: {},
            jobSkills: [],
            error: err?.message || "Lỗi khi phân tích job",
          };
        }
        setJobDetails({ ...jobDetails }); // cập nhật liên tục cho mượt
        if (isCanceled) return;
      }

      // 4. Hoàn thành (giả lập chờ đẹp UX)
      setAnalyzeStep("done");
      setStep(3);
      setProgress(100);
      setTimeout(() => {
        if (!isCanceled) onFinish();
      }, 1100);
    })();

    return () => {
      isCanceled = true;
    };
    // eslint-disable-next-line
  }, []);

  return (
    <div className="flex flex-col items-center justify-center min-h-[60vh]">
      <div className="w-full max-w-lg">
        {/* Stepper list */}
        <div className="mb-7">
          <ol className="relative border-l border-blue-200">
            {stepLabels.map((label, idx) => (
              <li key={label} className="mb-6 ml-6">
                <span
                  className={`absolute -left-3 flex items-center justify-center w-6 h-6
                  ${idx <= step ? "bg-blue-600 text-white" : "bg-blue-100 text-blue-400"}
                  rounded-full border-2 border-blue-300`}
                >
                  {idx < step ? "✔" : idx + 1}
                </span>
                <span className={`ml-2 ${idx === step ? "font-bold text-blue-700" : ""}`}>
                  {label}
                </span>
              </li>
            ))}
          </ol>
        </div>
        {/* Progress bar */}
        <div className="w-full h-4 bg-blue-100 rounded mb-2">
          <div
            className="h-4 bg-blue-500 rounded transition-all"
            style={{ width: `${progress}%` }}
          />
        </div>
        {/* Status text */}
        <div className="text-blue-600 text-lg font-semibold text-center mt-6">
          {step === 2
            ? (
              <>
                Đang phân tích công việc&nbsp;
                <span className="font-bold text-blue-900">
                  {useAnalysisStore.getState().analyzeJobIndex}
                </span>
                /
                <span className="font-bold text-blue-900">
                  {useAnalysisStore.getState().analyzeJobTotal}
                </span>
                ...
              </>
            )
            : stepLabels[step]
          }
        </div>
      </div>
    </div>
  );
};

export default LoadingAnalyze;
