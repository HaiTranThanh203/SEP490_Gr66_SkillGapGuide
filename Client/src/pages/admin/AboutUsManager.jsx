import { useState, useEffect } from "react";
import { Save, Eye, Pencil, Trash2, X, Plus, AlertCircle } from "lucide-react";
import { staticPageService } from "../../services/staticPageService";
import { showError ,showSuccess} from "../../utils/alert";
export default function AboutUsManager() {
  const [sections, setSections] = useState([]);
  const [editingSection, setEditingSection] = useState(null);
  const [showPreview, setShowPreview] = useState(false);
  const [loading, setLoading] = useState(false);

  // Fetch about us data
  useEffect(() => {
    const fetchAboutUs = async () => {
      setLoading(true);
      try {
        const data = await staticPageService.getAboutUs();
        setSections(
          data.map((section, index) => ({
            ...section,
            id: index + 1,
            isHighlight: false,
          }))
        );
      } catch (error) {
        console.error("Error fetching about us:", error);
      }
      setLoading(false);
    };
    fetchAboutUs();
  }, []);

  // Handle save changes
  const handleSave = async (section) => {
    try {
      // Call API to update single section
      await staticPageService.updateAboutUs({
        title: section.title,
        content: section.content,
      });

      // Update local state
      const updatedSections = sections.map((s) =>
        s.title === section.title ? section : s
      );

      if (!sections.find((s) => s.title === section.title)) {
        updatedSections.push(section);
      }

      setSections(updatedSections);
      setEditingSection(null);
      showSuccess("Đã lưu thay đổi thành công");
    } catch (error) {
      console.error("Error saving about us:", error);
      alert("Có lỗi xảy ra khi lưu thay đổi");
    }
  };

  return (
    <div className="space-y-6">
      {loading ? (
        <div className="text-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Đang tải...</p>
        </div>
      ) : (
        <>
          {/* Header */}
          <div className="flex justify-between items-center pb-6 border-b">
            <h2 className="text-2xl font-semibold text-gray-800">
              Quản lý trang About Us
            </h2>
            <div className="flex gap-3">
              <button
                onClick={() => setShowPreview(!showPreview)}
                className={`flex items-center gap-2 px-4 py-2 rounded-lg border transition-colors ${
                  showPreview
                    ? "bg-blue-50 text-blue-600 border-blue-200"
                    : "hover:bg-gray-50"
                }`}
              >
                <Eye size={20} />
                {showPreview ? "Tắt xem trước" : "Xem trước"}
              </button>
              <button
                onClick={() => setEditingSection({})}
                className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
              >
                <Plus size={20} />
                Thêm mục mới
              </button>
            </div>
          </div>

          {/* Content Sections */}
          <div className="grid gap-6">
            {sections.map((section) => (
              <div
                key={section.id}
                className="bg-white rounded-lg border shadow-sm"
              >
                <div className="p-6">
                  <div className="flex justify-between items-start mb-4">
                    <div>
                      <h3 className="text-xl font-medium text-gray-900">
                        {section.title}
                      </h3>
                      {section.isHighlight && (
                        <span className="inline-flex items-center mt-2 px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                          Nổi bật
                        </span>
                      )}
                    </div>
                    {!showPreview && (
                      <div className="flex gap-2">
                        <button
                          onClick={() => setEditingSection(section)}
                          className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                        >
                          <Pencil size={18} />
                        </button>
                        <button
                          onClick={() => {
                            if (confirm("Bạn có chắc muốn xóa mục này?")) {
                              setSections(sections.filter((s) => s.id !== section.id));
                            }
                          }}
                          className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                        >
                          <Trash2 size={18} />
                        </button>
                      </div>
                    )}
                  </div>
                  <div
                    className={`prose max-w-none ${
                      showPreview ? "bg-gray-50 p-4 rounded-lg" : ""
                    }`}
                  >
                    {section.content}
                  </div>
                </div>
              </div>
            ))}

            {sections.length === 0 && (
              <div className="text-center py-12 bg-gray-50 rounded-lg border-2 border-dashed">
                <AlertCircle className="mx-auto h-12 w-12 text-gray-400" />
                <h3 className="mt-2 text-sm font-medium text-gray-900">
                  Chưa có nội dung
                </h3>
                <p className="mt-1 text-sm text-gray-500">
                  Bắt đầu bằng việc thêm mục mới
                </p>
                <button
                  onClick={() => setEditingSection({})}
                  className="mt-6 inline-flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                >
                  <Plus size={20} />
                  Thêm mục mới
                </button>
              </div>
            )}
          </div>

          {/* Edit Modal */}
          {editingSection !== null && (
            <div className="fixed inset-0 z-50 overflow-y-auto">
              <div className="flex min-h-screen items-center justify-center p-4">
                <div
                  className="fixed inset-0 bg-black/30 backdrop-blur-sm"
                  onClick={() => setEditingSection(null)}
                />
                <div className="relative bg-white w-full max-w-2xl rounded-xl shadow-2xl">
                  <div className="flex items-center justify-between p-6 border-b">
                    <h3 className="text-xl font-semibold text-gray-900">
                      Chỉnh sửa nội dung: {editingSection.title}
                    </h3>
                    <button
                      onClick={() => setEditingSection(null)}
                      className="p-2 hover:bg-gray-100 rounded-full transition-colors"
                    >
                      <X size={20} className="text-gray-500" />
                    </button>
                  </div>
                  <form
                    onSubmit={(e) => {
                      e.preventDefault();
                      handleSave({
                        ...editingSection,
                        content: e.target.content.value,
                      });
                    }}
                    className="p-6 space-y-4"
                  >
                    <div>
                      <label className="block text-sm font-medium text-gray-700">
                        Nội dung
                      </label>
                      <textarea
                        name="content"
                        rows={6}
                        defaultValue={editingSection.content}
                        className="mt-1 w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                        required
                      />
                    </div>
                    <div className="flex justify-end gap-3 pt-4">
                      <button
                        type="button"
                        onClick={() => setEditingSection(null)}
                        className="px-4 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200"
                      >
                        Hủy
                      </button>
                      <button
                        type="submit"
                        className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                      >
                        <Save size={20} />
                        Lưu thay đổi
                      </button>
                    </div>
                  </form>
                </div>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
}