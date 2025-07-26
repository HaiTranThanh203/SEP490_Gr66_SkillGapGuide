import React, { useState } from "react";
import { FaStar } from "react-icons/fa";
import TopMenu from "./TopMenu";
const courseData = [
  {
    skill: "Kỹ năng làm việc nhóm (Teamwork)",
    courses: [
      {
        id: 1,
        title: "Teamwork Skills: Communicating Effectively in Groups",
        description:
          "Học cách giao tiếp hiệu quả, lắng nghe chủ động, đưa phản hồi tích cực và giải quyết xung đột trong nhóm.",
      },
      {
        id: 2,
        title: "High-Performance Collaboration: Leadership, Teamwork",
        description:
          "Nâng cao kỹ năng lãnh đạo, phối hợp và đảm nhận các tình huống thực tế, phù hợp cho môi trường doanh nghiệp.",
      },
    ],
  },
  {
    skill: "Quản lý thời gian (Time Management)",
    courses: [
      {
        id: 3,
        title: "Organize Yourself: Time Management for Personal",
        description:
          "Học cách xác định ưu tiên, thiết lập kế hoạch và kiểm soát lịch trình để giảm căng thẳng và tăng năng suất.",
      },
    ],
  },
];

const SuggestedCourses = () => {
  const [favorites, setFavorites] = useState({});

  const toggleFavorite = (id) => {
    setFavorites((prev) => ({ ...prev, [id]: !prev[id] }));
  };

  return (
    <>
    <div className="max-w-7xl mx-auto">
        <TopMenu />
      </div>
    <div className="bg-white min-h-screen p-6 max-w-6xl mx-auto">
      <div className="border border-blue-400 rounded-xl overflow-hidden text-sm">
        <table className="w-full border-collapse">
          <thead className="bg-blue-50 text-gray-800 font-semibold text-center">
            <tr>
              <th className="border px-4 py-3 w-[35%]">Kỹ năng bạn còn thiếu</th>
              <th className="border px-4 py-3">Khóa học gợi ý</th>
            </tr>
          </thead>
          <tbody>
            {courseData.map((item, idx) => (
              <tr key={idx} className="align-top">
                <td className="border px-4 py-3 font-medium text-gray-800 bg-white">
                  {item.skill}
                </td>
                <td className="border px-4 py-3 space-y-3">
                  {item.courses.map((course) => (
                    <div
                      key={course.id}
                      className="border border-blue-300 rounded-lg p-3 bg-white shadow-sm"
                    >
                      <div className="flex items-center justify-between">
                        <span className="font-semibold text-[14px] text-gray-800">
                          {course.title}
                        </span>
                        <button
                          onClick={() => toggleFavorite(course.id)}
                          className={`rounded-full w-5 h-5 flex items-center justify-center border ${
                            favorites[course.id]
                              ? "bg-yellow-400 text-white"
                              : "bg-white border-yellow-400 text-yellow-400"
                          } transition`}
                          title="Đánh dấu yêu thích"
                        >
                          <FaStar className="text-sm" />
                        </button>
                      </div>
                      <p className="text-sm text-gray-600 mt-1">{course.description}</p>
                    </div>
                  ))}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
    </>
  );
};

export default SuggestedCourses;
