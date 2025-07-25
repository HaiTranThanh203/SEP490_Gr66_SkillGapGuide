import React from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useCVWizardStore } from "../../stores/cvWizardStore";
import { useAnalysisStore } from "../../stores/useAnalysisStore";

const menuItems = [
  {
    label: "Tải lên CV & mô tả công việc",
    path: "/analyze/upload",
    enableCondition: () => true,
  },
   { label: "Công việc phù hợp", path: "/matchingjobs", enableCondition: () => true, },
  { label: "Gợi ý khóa học", path: "/suggestedcourses" , enableCondition: () => true,},
  // {
  //   label: "Phân tích kỹ năng",
  //   path: "/analyze/loading",
  //   enableCondition: () => {
  //     const { cvFile, jobFiles } = useCVWizardStore.getState();
  //     return !!cvFile && jobFiles && jobFiles.length > 0;
  //   },
  // },
  {
    label: "Phân tích kỹ năng",
    path: "/analyze/result",
    enableCondition: () => {
      const { skills } = useAnalysisStore.getState();
      return skills && skills.length > 0;
    },
  },
   { label: "Theo dõi tiến độ", path: "/coursetracking" ,enableCondition: () => true,},
  { label: "Đánh giá", path: "/servicerating" ,enableCondition: () => true,},
  { label: "Đăng ký gói dịch vụ", path: "/servicepayment", enableCondition: () => true, }
  
  // ...thêm menu khác nếu cần
];

const TopMenu = () => {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <nav className="bg-[#f5f9ff] border-b border-blue-200 shadow-sm">
      <ul className="flex justify-center gap-6 px-4 py-2 text-sm font-medium text-gray-800">
        {menuItems.map((item, i) => {
          const isActive = location.pathname === item.path;
          const isEnabled = item.enableCondition();

          return (
            <li
              key={i}
              className={`cursor-pointer transition-all px-2 py-1 rounded ${
                isActive
                  ? "text-white bg-blue-600 shadow font-semibold"
                  : isEnabled
                  ? "hover:text-blue-600 hover:underline"
                  : "text-gray-400 bg-gray-100 cursor-not-allowed"
              }`}
              style={{ opacity: isEnabled ? 1 : 0.5 }}
              onClick={() => {
                if (isEnabled) navigate(item.path);
                else alert("Bạn cần hoàn thành các bước trước!");
              }}
            >
              {item.label}
            </li>
          );
        })}
      </ul>
    </nav>
  );
};

export default TopMenu;
