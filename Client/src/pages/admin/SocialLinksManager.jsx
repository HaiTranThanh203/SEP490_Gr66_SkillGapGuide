import { useState, useEffect } from "react";
import {
  Facebook,
  Twitter,
  Linkedin,
  Instagram,
  Youtube,
  Globe,
  Plus,
  Save,
  CheckCircle,
  X,
} from "lucide-react";
import { staticPageService } from "../../services/staticPageService";

const SOCIAL_ICONS = {
  facebook: Facebook,
  twitter: Twitter,
  linkedin: Linkedin,
  instagram: Instagram,
  youtube: Youtube,
  website: Globe,
};

export default function SocialLinksManager() {
  const [links, setLinks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editingId, setEditingId] = useState(null);
  const [saving, setSaving] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");

  useEffect(() => {
    const fetchSocialLinks = async () => {
      try {
        const data = await staticPageService.getSocialLink();
        const transformedLinks = data.map((link, index) => ({
          id: index + 1,
          type: link.title.toLowerCase(),
          title: link.title,
          url: link.content,
          active: true,
        }));
        setLinks(transformedLinks);
      } catch (error) {
        console.error("Error fetching social links:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchSocialLinks();
  }, []);

  const handleSave = async (link) => {
    setSaving(true);
    try {
      await staticPageService.updateSocialLink({
        title:
          link.title || link.type.charAt(0).toUpperCase() + link.type.slice(1),
        content: link.url,
      });

      const updatedLinks = links.map((l) => (l.id === link.id ? link : l));
      setLinks(updatedLinks);
      setEditingId(null);
      showSuccess("Đã lưu liên kết thành công!");
    } catch (error) {
      console.error("Lỗi khi lưu liên kết:", error);
      alert("Không thể lưu liên kết. Vui lòng thử lại.");
    } finally {
      setSaving(false);
    }
  };

  const showSuccess = (message) => {
    setSuccessMessage(message);
    setTimeout(() => setSuccessMessage(""), 3000);
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[200px]">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  const currentLink =
    editingId === "new"
      ? { type: "", title: "", url: "", active: true, id: "new" }
      : links.find((l) => l.id === editingId);

  return (
    <div className="px-8 py-10 max-w-7xl mx-auto space-y-8">
      {/* Header */}
      <div className="flex justify-between items-center">
        <h2 className="text-3xl font-bold text-gray-900">
          Quản lý liên kết mạng xã hội
        </h2>

        <button
          onClick={() => setEditingId("new")}
          className="flex items-center gap-2 px-6 py-3 text-base bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition"
        >
          <Plus size={20} />
          Thêm liên kết mới
        </button>
      </div>

      {/* Links Grid */}
      <div className="grid gap-6 sm:grid-cols-2 xl:grid-cols-3">
        {links.map((link) => (
          <div
            key={link.id}
            className={`group relative bg-white rounded-lg border p-4 hover:shadow-md transition-all ${
              !link.active ? "opacity-75" : ""
            }`}
          >
            <div className="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition-opacity">
              <button
                onClick={() => setEditingId(link.id)}
                className="p-1 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded"
              >
                <Save size={16} />
              </button>
            </div>

            <div className="flex items-start gap-4">
              <div
                className={`p-3 rounded-lg ${
                  link.active
                    ? "bg-blue-50 text-blue-600"
                    : "bg-gray-50 text-gray-400"
                }`}
              >
                {SOCIAL_ICONS[link.type]?.render({ size: 24 })}
              </div>
              <div className="flex-1 min-w-0">
                <h3 className="font-medium text-gray-900 capitalize">
                  {link.title}
                </h3>
                <a
                  href={link.url}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-sm text-blue-600 hover:underline truncate block"
                >
                  {link.url}
                </a>
                <div className="mt-2 flex items-center gap-2">
                  <div
                    className={`w-2 h-2 rounded-full ${
                      link.active ? "bg-green-500" : "bg-gray-300"
                    }`}
                  />
                  <span className="text-sm text-gray-500">
                    {link.active ? "Đang hoạt động" : "Đã tắt"}
                  </span>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Edit Modal */}
      {/* Edit Modal */}
      {editingId !== null && (
        <div className="fixed inset-0 z-50 bg-black/30 backdrop-blur-sm flex items-center justify-center">
          {/* Nền click tắt modal */}
          <div
            className="absolute inset-0"
            onClick={() => setEditingId(null)}
          />
          <div className="relative bg-white w-full max-w-md rounded-xl shadow-2xl z-10 max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between p-6 border-b">
              <h3 className="text-xl font-semibold text-gray-900">
                {editingId === "new"
                  ? "Thêm liên kết mới"
                  : "Chỉnh sửa liên kết"}
              </h3>
              <button
                onClick={() => setEditingId(null)}
                className="p-2 hover:bg-gray-100 rounded-full transition-colors"
              >
                <X size={20} className="text-gray-500" />
              </button>
            </div>

            <form
              onSubmit={(e) => {
                e.preventDefault();
                const formData = new FormData(e.target);
                const url = formData.get("url");
                const type = formData.get("type");
                const title = type.charAt(0).toUpperCase() + type.slice(1);

                const newLink = {
                  id: editingId === "new" ? Date.now() : editingId,
                  type,
                  title,
                  url,
                  active: true,
                };

                handleSave(newLink);
              }}
              className="p-6 space-y-4"
            >
              {/* Loại liên kết */}
              <div className="space-y-2">
                <label className="text-sm font-medium text-gray-700">
                  Loại mạng xã hội
                </label>
                <select
                  name="type"
                  defaultValue={
                    editingId === "new"
                      ? ""
                      : links.find((l) => l.id === editingId)?.type || ""
                  }
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  required
                  disabled={editingId !== "new"}
                >
                  <option value="" disabled>
                    Chọn nền tảng
                  </option>
                  {Object.keys(SOCIAL_ICONS).map((type) => (
                    <option key={type} value={type}>
                      {type.charAt(0).toUpperCase() + type.slice(1)}
                    </option>
                  ))}
                </select>
              </div>

              {/* URL */}
              <div className="space-y-2">
                <label className="text-sm font-medium text-gray-700">URL</label>
                <input
                  type="url"
                  name="url"
                  defaultValue={
                    editingId === "new"
                      ? ""
                      : links.find((l) => l.id === editingId)?.url || ""
                  }
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>

              <div className="flex justify-end gap-3 mt-6">
                <button
                  type="button"
                  onClick={() => setEditingId(null)}
                  className="px-4 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  disabled={saving}
                  className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-75"
                >
                  <Save size={20} />
                  {saving ? "Đang lưu..." : "Lưu"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Success Toast */}
      {successMessage && (
        <div className="fixed bottom-4 right-4 flex items-center gap-2 px-4 py-2 bg-green-50 text-green-700 rounded-lg border border-green-200 shadow-lg">
          <CheckCircle size={20} />
          <span>{successMessage}</span>
        </div>
      )}
    </div>
  );
}
