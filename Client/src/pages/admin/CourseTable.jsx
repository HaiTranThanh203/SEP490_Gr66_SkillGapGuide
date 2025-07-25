import React, { useState, useEffect } from "react";
import {
  FiSearch,
  FiEye,
  FiRefreshCw,
  FiLoader,
  FiPlusCircle,
  FiZap,
} from "react-icons/fi";
import { courService } from "../../services/courService";

const statusColor = {
  ACTIVE: "text-green-600 font-bold",
  INACTIVE: "text-gray-400 font-semibold",
};

const statusLabel = {
  ACTIVE: "Hoạt động",
  INACTIVE: "Ngưng hoạt động",
};

const defaultCourseForm = {
  title: "",
  rating: "",
  difficulty: "",
  description: "",
  provider: "",
  status: "ACTIVE",
  url: "",
  createdAt: "",
};

const pageSizeOptions = [5, 10, 20];

const CourseTable = () => {
  // Main states
  const [search, setSearch] = useState("");
  const [courses, setCourses] = useState([]);
  const [filtered, setFiltered] = useState([]);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [totalCourses, setTotalCourses] = useState(0);

  // Update modal logic
  const [showOptionModal, setShowOptionModal] = useState(false);
  const [showManualModal, setShowManualModal] = useState(false);
  const [showAutoModal, setShowAutoModal] = useState(false);

  // Manual add form
  const [courseForm, setCourseForm] = useState(defaultCourseForm);
  const [formLoading, setFormLoading] = useState(false);

  // Auto scrape
  const [scrapeLoading, setScrapeLoading] = useState(false);
  const [scrapePages, setScrapePages] = useState(1);
  const [scrapeItems, setScrapeItems] = useState(pageSize);

  // Detail modal
  const [showDetail, setShowDetail] = useState(false);
  const [detailCourse, setDetailCourse] = useState(null);

  // Load course list
  useEffect(() => {
    fetchCourses();
    // eslint-disable-next-line
  }, [page, pageSize]);

  const fetchCourses = async () => {
    setLoading(true);
    try {
      const res = await courService.getAllCourses(page, pageSize);
      const apiCourses = res.result?.content || [];
      setCourses(apiCourses);
      setTotalPages(res.result?.totalPages || 1);
      setTotalCourses(res.result?.totalElements || apiCourses.length);
    } catch {
      setCourses([]);
      setTotalPages(1);
      setTotalCourses(0); // reset về 0 nếu fail
    }
    setLoading(false);
  };

  // Search filter
  useEffect(() => {
    if (!search.trim()) setFiltered(courses);
    else {
      setFiltered(
        courses.filter((c) =>
          c.title.toLowerCase().includes(search.toLowerCase())
        )
      );
    }
  }, [courses, search]);

  // Xử lý submit form thủ công
  const handleManualSubmit = async (e) => {
    e.preventDefault();
    setFormLoading(true);
    try {
      await courService.addCourseManually({
        ...courseForm,
        createdAt: new Date().toISOString(),
        // ? new Date(courseForm.createdAt).toISOString()
        //  new Date().toISOString(),
      });
      setCourseForm(defaultCourseForm);
      setShowManualModal(false);
      fetchCourses();
      alert("Thêm khóa học thành công!");
    } catch {
      alert("Thêm khóa học thất bại!");
    }
    setFormLoading(false);
  };

  // Xử lý scrape tự động
  const handleScrape = async () => {
    setScrapeLoading(true);
    try {
      await courService.scrapeAutomation(scrapePages, scrapeItems);
      setShowAutoModal(false);
      fetchCourses();
      alert("Cập nhật tự động thành công!");
    } catch {
      alert("Cập nhật tự động thất bại!");
    }
    setScrapeLoading(false);
  };

  // Hiện chi tiết khoá học
  const handleShowDetail = (course) => {
    setDetailCourse(course);
    setShowDetail(true);
  };

  // Khi đổi pageSize thì reset về trang 1
  const handleChangePageSize = (e) => {
    setPageSize(Number(e.target.value));
    setPage(1);
    setScrapeItems(Number(e.target.value));
  };

  return (
    <div className="max-w-5xl mx-auto px-4 py-8 font-sans">
      <h2 className="text-3xl font-bold text-indigo-900 mb-1">
        Danh sách khóa học
      </h2>
      <p className="mb-5 text-gray-600 font-medium">
        Tổng số khoá học:{" "}
        <span className="text-blue-600 font-bold">{totalCourses}</span>
      </p>

      {/* Search + Button + Page Size */}
      <div className="flex flex-wrap items-center gap-4 mb-4">
        <div className="relative">
          <input
            type="text"
            placeholder="Nhập tên khóa học"
            className="border border-black rounded-full px-4 py-2 pl-10 text-blue-700 font-medium focus:outline-none w-64"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
          <FiSearch className="absolute left-3 top-2.5 text-black" />
        </div>
        {/* page size select */}
        <label className="text-gray-700 font-semibold flex items-center gap-1">
          Số dòng/trang:
          <select
            className="ml-1 border rounded px-2 py-1"
            value={pageSize}
            onChange={handleChangePageSize}
          >
            {pageSizeOptions.map((opt) => (
              <option value={opt} key={opt}>
                {opt}
              </option>
            ))}
          </select>
        </label>
        <button
          className="bg-red-600 text-white px-4 py-2 rounded shadow hover:bg-red-700 flex items-center gap-2"
          onClick={() => setShowOptionModal(true)}
        >
          <FiRefreshCw />
          Cập nhật dữ liệu
        </button>

        {loading && (
          <span className="text-blue-600 font-semibold animate-pulse flex items-center gap-1">
            <FiLoader className="animate-spin" /> Đang tải...
          </span>
        )}
      </div>

      {/* Table */}
      <div className="overflow-x-auto">
        <table className="w-full border border-black text-left text-gray-700 bg-white">
          <thead className="bg-white">
            <tr>
              <th className="border border-black px-3 py-2 w-1/5">Tên</th>
              <th className="border border-black px-3 py-2">Mô tả</th>
              <th className="border border-black px-3 py-2 w-1/6">Nguồn</th>
              <th className="border border-black px-3 py-2 w-1/12">Đánh giá</th>
              <th className="border border-black px-3 py-2 w-1/12">Độ khó</th>
              <th className="border border-black px-3 py-2 w-1/12">
                Trạng thái
              </th>
              <th className="border border-black px-3 py-2 w-[90px]"></th>
            </tr>
          </thead>
          <tbody>
            {filtered.length === 0 && !loading && (
              <tr>
                <td
                  colSpan={7}
                  className="text-center text-gray-500 py-4 font-semibold"
                >
                  Không có khóa học nào phù hợp
                </td>
              </tr>
            )}
            {filtered.map((course, i) => (
              <tr key={course.courseId || i}>
                <td className="border border-black px-3 py-2 font-bold text-indigo-900">
                  {course.title}
                </td>
                <td className="border border-black px-3 py-2 truncate max-w-xs">
                  {course.description}
                </td>
                <td className="border border-black px-3 py-2 font-medium">
                  {course.provider}
                </td>
                <td className="border border-black px-3 py-2 text-center">
                  {course.rating}
                </td>
                <td className="border border-black px-3 py-2 text-center">
                  {course.difficulty}
                </td>
                <td
                  className={`border border-black px-3 py-2 text-center ${
                    statusColor[course.status] || ""
                  }`}
                >
                  {statusLabel[course.status] || course.status}
                </td>
                <td className="border border-black px-3 py-2 text-center">
                  <button
                    className="bg-blue-400 hover:bg-blue-500 text-white font-bold px-3 py-1 rounded flex items-center gap-1 mx-auto"
                    onClick={() => handleShowDetail(course)}
                  >
                    <FiEye /> Xem chi tiết
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      <div className="flex justify-center items-center gap-1 text-sm mt-6">
        <button
          className="px-2 py-1 border bg-white text-black rounded hover:bg-gray-100"
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
              page === i + 1
                ? "bg-blue-700 text-white"
                : "bg-white text-black hover:bg-gray-100"
            }`}
          >
            {i + 1}
          </button>
        ))}
        <button
          className="px-2 py-1 border bg-white text-black rounded hover:bg-gray-100"
          onClick={() => setPage(totalPages)}
          disabled={page === totalPages}
        >
          &gt;&gt;
        </button>
      </div>

      {/* Modal chọn cách cập nhật */}
      {showOptionModal && (
        <div className="fixed inset-0 bg-black/10 z-50 flex justify-center items-start pt-8">
          <div className="bg-white rounded-lg p-8 shadow-xl min-w-[320px] flex flex-col gap-6 relative">
            <button
              className="absolute top-2 right-3 text-2xl text-gray-500 hover:text-red-500"
              onClick={() => setShowOptionModal(false)}
            >
              ×
            </button>
            <h3 className="text-lg font-bold mb-2 text-indigo-900">
              Chọn cách cập nhật dữ liệu
            </h3>
            <button
              className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 rounded mb-3 flex items-center gap-3 justify-center transition"
              onClick={() => {
                setShowOptionModal(false);
                setShowManualModal(true);
              }}
            >
              <FiPlusCircle size={20} /> Cập nhật thủ công
            </button>
            <button
              className="bg-yellow-500 hover:bg-yellow-600 text-white font-bold py-3 rounded flex items-center gap-3 justify-center transition"
              onClick={() => {
                setShowOptionModal(false);
                setShowAutoModal(true);
              }}
            >
              <FiZap size={20} /> Cập nhật tự động
            </button>
          </div>
        </div>
      )}

      {/* Modal cập nhật thủ công */}
      {showManualModal && (
        <div
          className="fixed inset-0 bg-black/10 z-50 flex justify-center items-start pt-8"
          onClick={() => setShowManualModal(false)}
        >
          <form
            className="bg-white rounded-lg p-8 shadow-xl min-w-[350px] max-w-lg grid grid-cols-1 gap-4 relative"
            onClick={(e) => e.stopPropagation()}
            onSubmit={handleManualSubmit}
          >
            <button
              className="absolute top-2 right-3 text-2xl text-gray-500 hover:text-red-500"
              onClick={() => setShowManualModal(false)}
              type="button"
            >
              ×
            </button>
            <h3 className="text-lg font-bold mb-2 text-indigo-900">
              Thêm khoá học thủ công
            </h3>
            <input
              type="text"
              required
              placeholder="Tên khóa học"
              className="border rounded px-3 py-2"
              value={courseForm.title}
              onChange={(e) =>
                setCourseForm({ ...courseForm, title: e.target.value })
              }
            />
            <input
              type="text"
              required
              placeholder="Đánh giá"
              className="border rounded px-3 py-2"
              value={courseForm.rating}
              onChange={(e) =>
                setCourseForm({ ...courseForm, rating: e.target.value })
              }
            />
            <input
              type="text"
              required
              placeholder="Độ khó"
              className="border rounded px-3 py-2"
              value={courseForm.difficulty}
              onChange={(e) =>
                setCourseForm({ ...courseForm, difficulty: e.target.value })
              }
            />
            <input
              type="text"
              required
              placeholder="Nguồn"
              className="border rounded px-3 py-2"
              value={courseForm.provider}
              onChange={(e) =>
                setCourseForm({ ...courseForm, provider: e.target.value })
              }
            />
            <select
              className="border rounded px-3 py-2"
              value={courseForm.status}
              onChange={(e) =>
                setCourseForm({ ...courseForm, status: e.target.value })
              }
            >
              <option value="Active">Hoạt động</option>
              <option value="Inactive">Ngưng hoạt động</option>
            </select>
            <input
              type="text"
              required
              placeholder="URL"
              className="border rounded px-3 py-2"
              value={courseForm.url}
              onChange={(e) =>
                setCourseForm({ ...courseForm, url: e.target.value })
              }
            />
            <textarea
              placeholder="Mô tả"
              className="border rounded px-3 py-2"
              value={courseForm.description}
              onChange={(e) =>
                setCourseForm({ ...courseForm, description: e.target.value })
              }
            />
            {/* <input
              type="datetime-local"
              className="border rounded px-3 py-2"
              value={
                courseForm.createdAt
                  ? courseForm.createdAt.substring(0, 16)
                  : ""
              }
              onChange={(e) =>
                setCourseForm({ ...courseForm, createdAt: e.target.value })
              }
            /> */}
            <button
              type="submit"
              className={`mt-2 px-5 py-2 rounded bg-blue-700 text-white font-bold shadow hover:bg-blue-800 flex items-center gap-2 ${
                formLoading ? "opacity-70 cursor-wait" : ""
              }`}
              disabled={formLoading}
            >
              {formLoading ? (
                <FiLoader className="animate-spin" />
              ) : (
                <FiPlusCircle />
              )}
              Thêm thủ công
            </button>
          </form>
        </div>
      )}

      {/* Modal cập nhật tự động */}
      {showAutoModal && (
        <div
          className="fixed inset-0 bg-black/10 z-50 flex justify-center items-start pt-8"
          onClick={() => setShowAutoModal(false)}
        >
          <div
            className="bg-white rounded-lg p-8 shadow-xl min-w-[320px] max-w-md relative"
            onClick={(e) => e.stopPropagation()}
          >
            <button
              className="absolute top-2 right-3 text-2xl text-gray-500 hover:text-red-500"
              onClick={() => setShowAutoModal(false)}
            >
              ×
            </button>
            <h3 className="text-lg font-bold mb-4 text-indigo-900">
              Cập nhật dữ liệu tự động
            </h3>
            <div className="flex gap-3 items-center mb-4">
              <label>Số trang:</label>
              <input
                type="number"
                min={1}
                className="border rounded px-3 py-1 w-20"
                value={scrapePages}
                onChange={(e) => setScrapePages(e.target.value)}
              />
              <label>Số lượng/trang:</label>
              <input
                type="number"
                min={1}
                className="border rounded px-3 py-1 w-20"
                value={scrapeItems}
                onChange={(e) => setScrapeItems(e.target.value)}
              />
            </div>
            <button
              onClick={handleScrape}
              disabled={scrapeLoading}
              className={`w-full px-5 py-2 rounded bg-yellow-500 text-white font-bold shadow hover:bg-yellow-600 flex items-center gap-2 justify-center ${
                scrapeLoading ? "opacity-60 cursor-wait" : ""
              }`}
            >
              {scrapeLoading ? (
                <FiLoader className="animate-spin" />
              ) : (
                <FiZap />
              )}
              Nhập tự động
            </button>
            <p className="text-gray-500 mt-2 text-sm">
              Có thể mất 10-30s tuỳ số lượng.
            </p>
          </div>
        </div>
      )}

      {/* Modal: Show detail */}
      {showDetail && detailCourse && (
        <div
          className="fixed inset-0 bg-black/10 z-50 flex justify-center items-center"
          onClick={() => setShowDetail(false)}
        >
          <div
            className="bg-white rounded-lg p-7 w-[95vw] max-w-lg relative shadow-xl
                  max-h-[90vh] overflow-y-auto"
            onClick={(e) => e.stopPropagation()}
          >
            <button
              className="absolute top-2 right-2 text-gray-400 hover:text-red-500 font-bold text-lg"
              onClick={() => setShowDetail(false)}
            >
              ×
            </button>
            <h3 className="text-xl font-bold mb-2 text-indigo-800">
              {detailCourse.title}
            </h3>
            <p className="mb-1 text-gray-700">
              <span className="font-semibold">Đánh giá:</span>{" "}
              {detailCourse.rating}
            </p>
            <p className="mb-1 text-gray-700">
              <span className="font-semibold">Độ khó:</span>{" "}
              {detailCourse.difficulty}
            </p>
            <p className="mb-1 text-gray-700">
              <span className="font-semibold">Nguồn:</span>{" "}
              {detailCourse.provider}
            </p>
            <p className="mb-1 text-gray-700">
              <span className="font-semibold">Trạng thái:</span>{" "}
              {statusLabel[detailCourse.status] || detailCourse.status}
            </p>
            <p className="mb-3 text-gray-700">
              <span className="font-semibold">Mô tả:</span> <br />
              {detailCourse.description}
            </p>
            <p className="mb-1 text-gray-500">
              <span className="font-semibold">Ngày tạo:</span>{" "}
              {new Date(detailCourse.createdAt).toLocaleString()}
            </p>
            <a
              href={detailCourse.url}
              target="_blank"
              rel="noopener noreferrer"
              className="inline-block mt-4 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded font-semibold"
            >
              Đến khoá học
            </a>
          </div>
        </div>
      )}
    </div>
  );
};

export default CourseTable;
