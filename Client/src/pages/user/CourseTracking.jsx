import React, { useState } from "react";
import { FaStar } from "react-icons/fa";
import TopMenu from "./TopMenu";

const courseProgressOptions = ["Chưa bắt đầu", "Đang học", "Hoàn thành", "Hủy"];

const trackedCourses = [
  {
    id: 1,
    title: "Teamwork Skills: Communicating Effectively in Groups",
    description:
      "Học cách giao tiếp hiệu quả, lắng nghe chủ động, đưa phản hồi tích cực và giải quyết xung đột trong nhóm.",
    rating: 5.0,
    progress: "Đang học",
    image: "/images/course1.png", // ← dùng ảnh nội bộ
  },
  {
    id: 2,
    title: "High-Performance Collaboration: Leadership, Teamwork",
    description:
      "Nâng cao kỹ năng lãnh đạo, phối hợp và đảm nhận các tình huống thực tế, phù hợp cho môi trường doanh nghiệp.",
    rating: 4.9,
    progress: "Chưa bắt đầu",
    image: "/images/course2.png", // ← dùng ảnh nội bộ
  },
];

const CourseTracking = () => {
  const [progress, setProgress] = useState(() =>
    Object.fromEntries(trackedCourses.map((c) => [c.id, c.progress]))
  );

  const handleProgressChange = (id, value) => {
    setProgress((prev) => ({ ...prev, [id]: value }));
  };

  return (
    <>
    <div className="max-w-7xl mx-auto">
        <TopMenu />
      </div>
    <div className="max-w-4xl mx-auto mt-6 p-4">
      <h2 className="text-xl font-semibold mb-4">Theo dõi tiến độ khóa học</h2>
      <div className="space-y-4">
        {trackedCourses.map((course) => (
          <div
            key={course.id}
            className="border border-gray-300 rounded-xl p-4 flex gap-4 items-start shadow-sm bg-white"
          >
            <img
              src={course.image}
              alt={course.title}
              className="w-16 h-16 object-contain rounded-md"
            />
            <div className="flex-1">
              <div className="flex justify-between items-start">
                <h3 className="text-sm font-semibold text-gray-800">
                  {course.title}
                </h3>
                <div className="text-sm text-red-500 font-medium">
                  Xếp hạng: {course.rating}
                </div>
              </div>
              <p className="text-sm text-gray-600 mt-1">{course.description}</p>
              <div className="mt-3">
                <label className="text-sm font-medium mr-2">Tiến độ</label>
                <select
                  value={progress[course.id]}
                  onChange={(e) => handleProgressChange(course.id, e.target.value)}
                  className="border border-gray-300 rounded px-3 py-1 text-sm"
                >
                  {courseProgressOptions.map((option) => (
                    <option key={option} value={option}>
                      {option}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
    </>
  );
};

export default CourseTracking;
