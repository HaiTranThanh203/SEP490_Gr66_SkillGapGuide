import React from "react";
import TopMenu from "./TopMenu";
const jobList = [
  {
    title: "1. Vị trí: Kiểm thử phần mềm – TNHH Công nghệ và Tư vấn Doanh nghiệp InnoCom",
    description:
      "Phụ trách thiết kế và thực hiện các kịch bản kiểm thử phần mềm. Yêu cầu ~4 năm kinh nghiệm, mức lương 12-16 triệu. Hình thức: QA phần mềm, hỗ trợ QA backend/front-end.",
    match: 90,
  },
  {
    title: "2. Vị trí: Quản lý kỹ thuật – Cổ phần SmartOSC",
    description:
      "Quản lý kỹ thuật, dẫn dắt team phát triển backend. Yêu cầu >5 năm kinh nghiệm, mức lương lên tới ~3500 USD. Làm việc tại Hà Nội.",
    match: 80,
  },
  {
    title: "3. Vị trí: Nhân viên hỗ trợ phần cứng – CP Thực phẩm và Dịch vụ My Way",
    description:
      "Hỗ trợ phần cứng/phần mềm tại văn phòng Đông Triều. 2 năm kinh nghiệm, mức lương 8-10 triệu.",
    match: 60,
  },
];

const getMatchColor = (percent) => {
  if (percent >= 85) return "text-red-600";
  if (percent >= 70) return "text-orange-500";
  return "text-gray-600";
};

const MatchingJobs = () => {
  return (
    <>
    <div className="max-w-7xl mx-auto">
        <TopMenu />
      </div>
    <div className="bg-white min-h-screen p-6 max-w-6xl mx-auto">
      {/* Tiêu đề bảng */}
      <div className="flex justify-between items-center font-semibold text-[18px] border-b border-gray-300 pb-2 mb-4">
        <span className="text-red-600">Công việc phù hợp</span>
        <span className="text-red-600">Mức độ phù hợp</span>
      </div>

      {/* Danh sách công việc */}
      <div className="space-y-4">
        {jobList.map((job, index) => (
          <div
            key={index}
            className="flex justify-between items-start border border-blue-400 rounded-xl p-4 bg-white shadow-sm"
          >
            <div className="w-[90%]">
              <h3 className="font-semibold text-sm text-gray-800 mb-1">{job.title}</h3>
              <p className="text-sm text-gray-600">{job.description}</p>
            </div>
            <div className="flex justify-center items-center w-[10%]">
              <div
                className={`text-[14px] font-bold ${getMatchColor(job.match)}`}
              >
                {job.match}%
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
    </>
  );
};

export default MatchingJobs;
