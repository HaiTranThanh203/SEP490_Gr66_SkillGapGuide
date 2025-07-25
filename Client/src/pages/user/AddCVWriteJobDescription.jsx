import React from "react";
import { useAnalysisStore } from "../../stores/useAnalysisStore";
import Skeleton from "react-loading-skeleton";
import "react-loading-skeleton/dist/skeleton.css";
import Stepper from "./Stepper";
import { Menu } from "lucide-react";
import TopMenu from "./TopMenu";
// Skeleton components
const renderSkillSkeleton = () => (
  <div className="mb-6 grid grid-cols-1 md:grid-cols-2 gap-3">
    {Array.from({ length: 6 }).map((_, idx) => (
      <div
        key={idx}
        className="h-10 w-full bg-gradient-to-r from-gray-200 via-gray-100 to-gray-200 animate-pulse rounded-lg"
      />
    ))}
  </div>
);

const renderJobSkeleton = () => (
  <div className="border rounded-xl p-5 my-5 bg-gray-50">
    <div className="h-5 w-1/3 bg-gray-200 rounded mb-3 animate-pulse"></div>
    <div className="h-4 w-full bg-gray-100 rounded mb-2 animate-pulse"></div>
    <div className="h-4 w-3/4 bg-gray-100 rounded mb-2 animate-pulse"></div>
    <div className="h-32 w-full bg-gray-100 rounded mb-2 animate-pulse"></div>
    <div className="h-8 w-1/4 bg-gray-200 rounded mt-4 animate-pulse"></div>
  </div>
);

const AnalysisResult = () => {
  // Dữ liệu từ zustand-persist, F5 không mất data
  const skills = useAnalysisStore((s) => s.skills);
  const jobList = useAnalysisStore((s) => s.jobList);
  const jobDetails = useAnalysisStore((s) => s.jobDetails);
  const isLoading = useAnalysisStore((s) => s.isAnalysisLoading); // Đặt biến này khi LoadingAnalyze đang chạy

  return (
    <div className="bg-white min-h-screen py-6 px-4 max-w-6xl mx-auto">
     
      <Stepper />
      <h1 className="text-2xl font-bold text-gray-800 mb-6">
        Kết quả phân tích kỹ năng
      </h1>
      {/* Hiện kỹ năng từ CV */}
      <h2 className="text-lg font-bold text-gray-800 mb-2 flex items-center gap-2">
        <svg width="22" height="22" viewBox="0 0 20 20" fill="none">
          <path
            d="M7 10a3 3 0 1 1 6 0 3 3 0 0 1-6 0ZM3 16.5a7 7 0 1 1 14 0v.25a1.25 1.25 0 0 1-1.25 1.25H4.25A1.25 1.25 0 0 1 3 16.75V16.5Z"
            fill="#2563eb"
          />
        </svg>
        Kỹ năng hiện tại của bạn
      </h2>
      <div className="mb-6 grid grid-cols-1 md:grid-cols-2 gap-3">
        {isLoading ? (
          renderSkillSkeleton()
        ) : skills && skills.length > 0 ? (
          skills.map((item, idx) => (
            <div
              key={idx}
              className="flex items-center gap-3 px-4 py-3 bg-blue-50 border border-blue-200 rounded-xl shadow-sm text-base font-semibold text-blue-900"
            >
              <svg width="22" height="22" viewBox="0 0 22 22" fill="none">
                <circle
                  cx="11"
                  cy="11"
                  r="10"
                  stroke="#2563eb"
                  strokeWidth="2"
                />
                <circle
                  cx="11"
                  cy="11"
                  r="5"
                  fill="#e0edff"
                  stroke="#2563eb"
                  strokeWidth="1"
                />
              </svg>
              {item.skill || item.cvSkill}
            </div>
          ))
        ) : (
          <div className="flex items-center col-span-2 text-gray-400 italic gap-2 py-3">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="10" stroke="#d1d5db" strokeWidth="2" />
              <path
                d="M9.5 9.5h.01M14.5 9.5h.01M9 15c.7-.7 2.3-.7 3 0"
                stroke="#d1d5db"
                strokeWidth="1.5"
                strokeLinecap="round"
              />
            </svg>
            Chưa có kỹ năng nào phù hợp được tìm thấy.
          </div>
        )}
      </div>

      {/* Hiện job phân tích */}
      <div className="mt-10">
        {isLoading ? (
          <>
            {Array.from({ length: 2 }).map((_, idx) => (
              <div key={idx}>{renderJobSkeleton()}</div>
            ))}
          </>
        ) : !jobList || jobList.length === 0 ? (
          <p className="text-gray-400 italic">Chưa có yêu cầu phân tích nào.</p>
        ) : (
          jobList.map((job) => {
            const jobInfo = jobDetails[job.jobId] || {};
            return (
              <div
                key={job.jobId}
                className="border rounded-xl p-6 my-7 bg-white shadow-sm"
              >
                <h3 className="text-red-600 font-bold mb-2 text-[20px]">
                  {job.title} – {job.company}
                </h3>
                <div className="italic mb-2 text-gray-700">
                  {job.description}
                </div>
                {jobInfo.error ? (
                  <div className="text-red-600 font-semibold">
                    XIN lỖI BẠN AI KHÔNG THỂ PHÂN THÍCH CÔNG VIỆC NÀY{" "}
                  </div>
                ) : (
                  <>
                    {/* Nhận xét */}
                    {jobInfo.commentData && (
                      <div className="bg-blue-50 border-l-4 border-blue-400 p-4 mb-4 rounded">
                        <strong>Nhận xét chung:</strong>{" "}
                        <span className="text-gray-700">
                          {jobInfo.commentData.generalComment}
                        </span>
                        {jobInfo.commentData.skillComment &&
                          jobInfo.commentData.skillComment.length > 0 && (
                            <ul className="mt-2 list-disc list-inside pl-5 text-sm text-blue-900 space-y-1">
                              {jobInfo.commentData.skillComment.map(
                                (cmt, idx) => (
                                  <li key={idx}>
                                    <span className="font-semibold">
                                      {cmt.skill}:
                                    </span>{" "}
                                    {cmt.comment}
                                  </li>
                                )
                              )}
                            </ul>
                          )}
                      </div>
                    )}
                    {/* Bảng skill gap */}
                    <div className="border rounded-xl overflow-hidden text-sm my-6">
                      <table className="w-full border-collapse text-center">
                        <thead className="bg-blue-50 text-gray-800 font-semibold">
                          <tr>
                            <th className="border px-3 py-2">
                              Kỹ năng trong CV
                            </th>
                            <th className="border px-3 py-2">
                              Yêu cầu công việc
                            </th>
                            <th className="border px-3 py-2">
                              Điểm phù hợp (%)
                            </th>
                          </tr>
                        </thead>
                        <tbody>
                          {jobInfo.skillGap && jobInfo.skillGap.length > 0 ? (
                            jobInfo.skillGap.map((item, idx) => (
                              <tr key={idx}>
                                <td className="border px-3 py-2">
                                  {item.cvSkill}
                                </td>
                                <td className="border px-3 py-2">
                                  {item.jobSkill}
                                </td>
                                <td
                                  className={
                                    "border px-3 py-2 font-semibold " +
                                    (item.score >= 0.8
                                      ? "text-green-600"
                                      : item.score >= 0.4
                                      ? "text-yellow-500"
                                      : "text-red-600")
                                  }
                                >
                                  {Math.round(item.score * 100)}%
                                </td>
                              </tr>
                            ))
                          ) : (
                            <tr>
                              <td
                                colSpan={3}
                                className="border px-3 py-2 text-gray-400 italic"
                              >
                                Không có dữ liệu phân tích kỹ năng.
                              </td>
                            </tr>
                          )}
                        </tbody>
                      </table>
                    </div>
                    {/* List job skills */}
                    <div className="mb-3">
                      <h4 className="font-semibold text-blue-700 mb-2">
                        Tất cả kỹ năng yêu cầu của công việc:
                      </h4>
                      <ul className="flex flex-wrap gap-2">
                        {jobInfo.jobSkills && jobInfo.jobSkills.length > 0 ? (
                          jobInfo.jobSkills.map((skill) => (
                            <li
                              key={skill.id}
                              className="bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm"
                            >
                              {skill.skill}
                            </li>
                          ))
                        ) : (
                          <span className="text-gray-400 italic">
                            Không có.
                          </span>
                        )}
                      </ul>
                    </div>
                  </>
                )}
              </div>
            );
          })
        )}
      </div>
    </div>
  );
};

export default AnalysisResult;
