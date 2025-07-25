// FavoriteCourses.jsx
import React, { useState, useEffect } from "react";
import { Link, useLocation } from "react-router-dom";
import { FaChalkboardTeacher, FaTrashAlt } from "react-icons/fa";
import { FiUser, FiHeart, FiCheckCircle } from "react-icons/fi";
import { Star, BarChart2, Clock, BookmarkCheck } from "lucide-react";
import { courService } from "../../services/courService";
import { motion } from "framer-motion";
import { showSuccess, showError, showConfirm } from "../../utils/alert";

const statusMap = {
  WANT_TO_LEARN: "Muốn học",
  LEARNING: "Đang học",
  COMPLETED: "Đã hoàn thành",
};

const statusOptions = ["WANT_TO_LEARN", "LEARNING", "COMPLETED"];

const FavoriteCourses = () => {
  const userId = 2; // lấy từ auth nếu có
  const location = useLocation();
  const [courses, setCourses] = useState([]);
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  const fetchCourses = async () => {
    try {
      const res = await courService.findAllFavoriteCourses(userId, page, 10);
      const rawCourses = res.result?.content || [];
      const formatted = rawCourses.map((item) => ({
        id: item.id,
        title: item.course.title,
        platform: item.course.provider,
        link: item.course.url,
        description: item.course.description,
        rating: item.course.rating,
        difficulty: item.course.difficulty,
        status: item.status,
        createdAt: item.createdAt,
        courseId: item.course.courseId,
      }));
      setCourses(formatted);
      setTotalPages(res.result?.totalPages || 1);
    } catch (err) {
      showError("Không thể tải danh sách khóa học.");
    }
  };

  useEffect(() => {
    fetchCourses();
  }, [page]);

  const handleDelete = async (courseId) => {
    const confirm = await showConfirm(
      "Bạn có chắc muốn xóa khóa học này không?"
    );
    if (!confirm.isConfirmed) return;
    try {
      await courService.deleteFavoriteCourse(userId, courseId);
      showSuccess("Đã xóa khóa học khỏi danh sách yêu thích.");
      fetchCourses();
    } catch (err) {
      showError("Xóa khóa học thất bại.");
    }
  };

  const handleDeleteAll = async () => {
    const confirm = await showConfirm(
      "Bạn có chắc muốn xóa TẤT CẢ khóa học yêu thích không?"
    );
    if (!confirm.isConfirmed) return;
    try {
      await courService.deleteAllFavoriteCourse(userId);
      showSuccess("Đã xóa tất cả khóa học yêu thích.");
      fetchCourses();
    } catch (err) {
      showError("Xóa tất cả khóa học thất bại.");
    }
  };

  const handleChangeStatus = async (courseId, newStatus) => {
    try {
      await courService.changeFavoriteCourseStatus(courseId, userId, newStatus);
      showSuccess("Đã cập nhật trạng thái khóa học.");
      fetchCourses();
    } catch (err) {
      showError("Cập nhật trạng thái thất bại.");
    }
  };

  const filtered = courses.filter(
    (c) =>
      c.title.toLowerCase().includes(search.toLowerCase()) ||
      c.platform.toLowerCase().includes(search.toLowerCase())
  );

  const sidebarLinks = [
    {
      label: "Thông tin tài khoản",
      icon: <FiUser className="text-purple-500" />,
      to: "/profile",
    },
    {
      label: "Khóa học yêu thích",
      icon: <FaChalkboardTeacher className="text-black" />,
      to: "/favouriteCourses",
    },
    {
      label: "Kỹ năng yêu thích",
      icon: <FiHeart className="text-sky-500" />,
      to: "/favouriteskills",
    },
    {
      label: "Trạng thái tài khoản",
      icon: <FiCheckCircle className="text-blue-700" />,
      to: "/account-status",
    },
  ];

  return (
    <div className="max-w-7xl mx-auto px-6 py-8 flex gap-8">
      {/* Sidebar */}
      <div className="w-[230px] bg-white rounded-xl shadow-md py-6 flex flex-col justify-between text-[15px] font-medium">
        <div className="space-y-4">
          {sidebarLinks.map((item, index) => (
            <Link
              key={index}
              to={item.to}
              className={`flex items-center gap-2 p-3 rounded hover:bg-gray-100 transition ${
                location.pathname === item.to
                  ? "font-bold text-indigo-700 bg-indigo-50"
                  : "text-gray-700"
              }`}
            >
              <div className="flex items-center gap-2">
                {item.icon}
                <span>{item.label}</span>
              </div>
            </Link>
          ))}
        </div>
      </div>

      {/* Main content */}
      <div className="flex-1">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-lg font-bold">Khóa học yêu thích của bạn</h2>
          <input
            type="text"
            placeholder="Nhập tên khóa học yêu thích"
            className="border border-gray-300 rounded-full px-4 py-2 text-sm w-[250px] focus:outline-none focus:ring-2 focus:ring-blue-400"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        {/* Delete All */}
        <div className="text-right mb-4">
          <button
            onClick={handleDeleteAll}
            className="text-red-500 text-sm flex items-center gap-2 hover:text-red-700"
          >
            <FaTrashAlt /> Xóa tất cả khóa học yêu thích
          </button>
        </div>

        {/* Course List */}
        <div className="space-y-4">
          {filtered.map((course) => (
            <motion.div
              key={course.courseId}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.3 }}
              whileHover={{ scale: 1.02 }}
              className="relative border border-gray-200 bg-white rounded-xl p-4 shadow-sm hover:shadow-md transition"
            >
              <div className="flex justify-between items-start gap-4">
                <div className="flex-1">
                  <h3 className="text-lg font-semibold text-gray-900">
                    {course.title}
                  </h3>
                </div>

                <div className="flex items-center gap-2">
                  <a
                    href={course.link}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-blue-600 text-sm hover:underline"
                  >
                    → {course.platform}
                  </a>

                  <button
                    onClick={() => handleDelete(course.courseId)}
                    className="bg-red-100 text-red-600 px-2 py-1 text-xs rounded hover:bg-red-200 transition"
                  >
                    Xóa
                  </button>
                </div>
              </div>

              <p className="text-sm text-gray-700 mt-2 mb-3">
                {course.description}
              </p>

              <div className="flex flex-wrap gap-4 text-sm text-gray-600">
                <div className="flex items-center gap-2">
                  <Star size={16} className="text-yellow-500" />
                  <span>Rating: {course.rating}</span>
                </div>
                <div className="flex items-center gap-2">
                  <BarChart2 size={16} className="text-indigo-500" />
                  <span>Độ khó: {course.difficulty}</span>
                </div>
                <div className="flex items-center gap-2">
                  <BookmarkCheck size={16} className="text-green-600" />
                  <span>Trạng thái:</span>
                  <select
                    value={course.status}
                    onChange={(e) =>
                      handleChangeStatus(course.courseId, e.target.value)
                    }
                    className="text-sm bg-transparent focus:outline-none border-b border-gray-300"
                  >
                    {statusOptions.map((opt) => (
                      <option key={opt} value={opt} className="text-black">
                        {statusMap[opt]}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="flex items-center gap-2">
                  <Clock size={16} className="text-gray-500" />
                  <span>
                    Thêm lúc: {new Date(course.createdAt).toLocaleString()}
                  </span>
                </div>
              </div>

              <button
                onClick={() => handleDelete(course.courseId)}
                className="absolute top-3 right-3 bg-red-100 text-red-600 px-2 py-1 text-xs rounded hover:bg-red-200 transition"
              >
                Xóa
              </button>
            </motion.div>
          ))}
        </div>

        {/* Pagination */}
        <div className="flex justify-center items-center gap-1 text-sm mt-8">
          <button
            className="border border-gray-300 rounded px-2 py-1 hover:bg-gray-100"
            onClick={() => setPage(1)}
            disabled={page === 1}
          >
            &lt;&lt;
          </button>
          {[...Array(totalPages)].map((_, i) => (
            <button
              key={i}
              onClick={() => setPage(i + 1)}
              className={`px-3 py-1 rounded border ${
                page === i + 1 ? "bg-blue-700 text-white" : "hover:bg-gray-100"
              }`}
            >
              {i + 1}
            </button>
          ))}
          <button
            className="border border-gray-300 rounded px-2 py-1 hover:bg-gray-100"
            onClick={() => setPage(totalPages)}
            disabled={page === totalPages}
          >
            &gt;&gt;
          </button>
        </div>
      </div>
    </div>
  );
};

export default FavoriteCourses;
