import { useEffect, useState } from "react";
import { Search, Plus, Pencil, X, Eye, Save } from "lucide-react";
import { careerService } from "../../services/career";
import StatusDot from "../../components/StatusDot";
import { showSuccess, showError } from "../../utils/alert";

export default function JobSkillAdmin() {
  // Main state
  const [groups, setGroups] = useState([]);
  const [occupations, setOccupations] = useState([]);
  const [specializations, setSpecializations] = useState([]);

  // UI state
  const [selectedGroupId, setSelectedGroupId] = useState(null);
  const [search, setSearch] = useState("");
  const [modal, setModal] = useState({ type: null, data: null, parent: null });
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 5;

  // Form state
  const [form, setForm] = useState({
    name: "",
    status: "Enable",
    occupationGroupId: null,
    occupationId: null, // For occupation
  });

  // Fetch data on mount
  useEffect(() => {
    (async () => {
      const [g, o, s] = await Promise.all([
        careerService.viewOccupationGroups(),
        careerService.viewOccupations(),
        careerService.viewSpecialization(),
      ]);
      setGroups(Array.isArray(g) ? g : g?.data || []);
      setOccupations(Array.isArray(o) ? o : o?.data || []);
      setSpecializations(Array.isArray(s) ? s : s?.data || []);
    })();
  }, []);

  const totalGroups = groups.length;
  const totalOccupations = occupations.length;
  const totalSpecializations = specializations.length;

  // Filtered occupations theo nhóm nghề
  const filteredOccupations = occupations.filter(
    (occ) =>
      (!selectedGroupId || occ.groupId === selectedGroupId) &&
      occ.name.toLowerCase().includes(search.toLowerCase())
  );

  // Pagination logic
  const totalPages = Math.ceil(filteredOccupations.length / pageSize);
  const pageOccupations = filteredOccupations.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  );

  // Vị trí chuyên môn theo occupationId
  const getSpecs = (occId) =>
    specializations.filter((s) => s.occupationId === occId);

  // ===== CRUD HANDLERS & MODAL CONTROLS =====
  // Handle Add, Edit, and Disable
  const handleAddGroup = async (data) => {
    try {
      const response = await careerService.addOccupationGroups(data);
      if (response.status === 400) {
        throw new Error(
          response.message || "Đã xảy ra lỗi khi thêm nhóm nghề."
        );
      }
      showSuccess("Thêm nhóm nghề thành công!");
      setGroups([...groups, response.result]);
    } catch (error) {
      // Kiểm tra lỗi từ response của backend

      // Xử lý trường hợp không có response (ví dụ: mạng không ổn định)
      showError(error.message);
    }
  };

  const handleEditGroup = async (id, data) => {
    try {
      const response = await careerService.editOccupationGroups(id, data);
      showSuccess("Cập nhật nhóm nghề thành công!");
      setGroups(
        groups.map((group) => (group.id === id ? { ...group, ...data } : group))
      );
    } catch (error) {
      showError("Đã xảy ra lỗi khi cập nhật nhóm nghề.");
    }
  };

  const handleDisableGroup = async (id) => {
    try {
      await careerService.disableOccupationGroups(id);
      setGroups(groups.filter((group) => group.id !== id));
    } catch (error) {
      showError("Đã xảy ra lỗi khi vô hiệu hóa nhóm nghề.");
    }
  };

  const handleAddOccupation = async (data) => {
    try {
      const response = await careerService.addOccupations(data);
      if (response.status === 400) {
        throw new Error(response?.message || "Đã xảy ra lỗi khi thêm nghề .");
      }
      const { id, name, status, occupationGroup } = response.result;

      const newOccupation = {
        id,
        name,
        status,
        groupId: occupationGroup.id,
        groupName: occupationGroup.name,
      };
      setOccupations([...occupations, newOccupation]);
      showSuccess("Thêm nghề thành công!");
    } catch (error) {
      showError(error.message || "Đã xảy ra lỗi khi thêm nghề.");
    }
  };

  const handleEditOccupation = async (id, data) => {
    try {
      const response = await careerService.editOccupations(id, data);
      if (response.status === 400) {
        throw new Error(
          response?.message || "Đã xảy ra lỗi khi cập nhật nghề."
        );
      }
      showSuccess("Cập nhật nghề thành công!");
      setOccupations(
        occupations.map((occ) =>
          occ.id === id
            ? { ...occ, ...data, groupId: data.occupationGroupId }
            : occ
        )
      );
    } catch (error) {
      showError(error.message || "Đã xảy ra lỗi khi cập nhật nghề.");
    }
  };

  const handleDisableOccupation = async (id) => {
    try {
      await careerService.disableOccupation(id);
      setOccupations(occupations.filter((occ) => occ.id !== id));
    } catch (error) {
      showError("Đã xảy ra lỗi khi vô hiệu hóa nghề.");
    }
  };

  const handleAddSpecialization = async (data) => {
    try {
      const response = await careerService.addSpecialization(data);
      showSuccess("Thêm vị trí chuyên môn thành công!");
      setSpecializations([...specializations, response.result]);
    } catch (error) {
      // Kiểm tra lỗi từ response của backend
      if (error.response && error.response.status === 400) {
        showError(
          error.response.data.message ||
            "Đã xảy ra lỗi khi thêm vị trí chuyên môn."
        );
      }
    }
  };

  const handleEditSpecialization = async (id, data) => {
    try {
      const response = await careerService.editSpecialization(id, data);
      showSuccess("Cập nhật vị trí chuyên môn thành công!");
      setSpecializations(
        specializations.map((spec) =>
          spec.id === id
            ? { ...spec, ...response.result, occupationId: data.occupationId }
            : spec
        )
      );
    } catch (error) {
      showError("Đã xảy ra lỗi khi cập nhật vị trí chuyên môn.");
    }
  };

  const handleDisableSpecialization = async (id) => {
    try {
      await careerService.disableSpecialization(id);
      setSpecializations(specializations.filter((spec) => spec.id !== id));
    } catch (error) {
      showError("Đã xảy ra lỗi khi vô hiệu hóa vị trí chuyên môn.");
    }
  };

  // Show modal (type: group/occupation/specialization, data: record or null, parent: for occupation/specialization)
  const openModal = (type, data = null, parent = null) => {
    setModal({ type, data, parent });
    setForm(data || {}); // for edit, fill form, for add, empty
  };

  const closeModal = () => setModal({ type: null, data: null, parent: null });

  const handleSubmit = async (e) => {
    e.preventDefault();
    const formData = {
      name: form.name,
      status: form.status || "Enable",
      // Mặc định giá trị là "Enable" nếu không có
    };

    try {
      if (modal.type === "group") {
        if (modal.data) {
          await handleEditGroup(modal.data.id, formData);
        } else {
          await handleAddGroup(formData);
        }
      } else if (modal.type === "occupation") {
        if (modal.data) {
          await handleEditOccupation(modal.data.id, {
            ...formData,
            occupationGroupId: form.occupationGroupId,
          });
        } else {
          await handleAddOccupation({
            ...formData,
            occupationGroupId: modal.parent.id,
          });
        }
      } else if (modal.type === "specialization") {
        if (modal.data) {
          await handleEditSpecialization(modal.data.id, {
            ...formData,
            occupationId: form.occupationId,
          });
        } else {
          await handleAddSpecialization({
            ...formData,
            occupationId: modal.parent.id,
          });
        }
      }
      closeModal();
    } catch (error) {
      showError(error.message || "Đã xảy ra lỗi khi lưu dữ liệu.");
    }
  };

  return (
    <div className="flex min-h-screen bg-white">
      {/* Sidebar nhóm nghề */}
      <aside className="w-72 border-r border-blue-100 p-6 flex flex-col gap-3 bg-blue-50 min-h-screen">
        <div className="flex items-center justify-between mb-4">
          <span className="font-bold text-lg text-blue-900">Nhóm nghề</span>
          <button
            className="p-2 bg-blue-600 text-white rounded-full hover:bg-blue-700 transition"
            title="Thêm nhóm nghề"
            onClick={() => openModal("group")}
          >
            <Plus size={18} />
          </button>
        </div>
        <button
          className={`py-2 px-3 rounded-xl text-left mb-1 ${
            !selectedGroupId
              ? "bg-indigo-600 text-white font-bold"
              : "hover:bg-blue-100 text-blue-900"
          }`}
          onClick={() => setSelectedGroupId(null)}
        >
          Tất cả ({groups.length})
        </button>
        {groups.map((g) => (
          <div className="flex items-center gap-2" key={g.id}>
            <StatusDot status={g.status} />
            <button
              className={`flex-1 py-2 px-3 rounded-xl text-left ${
                selectedGroupId === g.id
                  ? "bg-indigo-600 text-white font-bold"
                  : "hover:bg-blue-100 text-blue-900"
              }`}
              onClick={() => setSelectedGroupId(g.id)}
            >
              {g.name}
            </button>
            <button
              className="text-blue-500 p-1 hover:bg-blue-100 rounded-full"
              title="Cập nhật"
              onClick={() => openModal("group", g)}
            >
              <Pencil size={16} />
            </button>
          </div>
        ))}
      </aside>

      {/* Main content */}
      <main className="flex-1 px-6 py-8">
        {/* Header */}
        <div className="flex flex-wrap gap-6 justify-between items-center mb-4">
          <h1 className="text-2xl font-bold text-blue-900 mb-2">
            Nghề và vị trí chuyên môn
          </h1>
          <div className="relative">
            <input
              type="text"
              placeholder="Tìm kiếm nghề..."
              className="rounded-xl border border-blue-200 pl-10 pr-3 py-2 w-72 focus:ring-2 focus:ring-blue-400 outline-none text-blue-900"
              value={search}
              onChange={(e) => {
                setSearch(e.target.value);
                setCurrentPage(1);
              }}
            />
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-blue-400" />
          </div>
        </div>
        <div className="flex gap-4 mt-4">
          <div className="bg-blue-100 text-blue-800 rounded-lg px-4 py-2 text-sm font-semibold flex items-center gap-2">
            <span>Nhóm nghề:</span>
            <span className="text-xl">{totalGroups}</span>
          </div>
          <div className="bg-indigo-100 text-indigo-800 rounded-lg px-4 py-2 text-sm font-semibold flex items-center gap-2">
            <span>Nghề:</span>
            <span className="text-xl">{totalOccupations}</span>
          </div>
          <div className="bg-green-100 text-green-800 rounded-lg px-4 py-2 text-sm font-semibold flex items-center gap-2">
            <span>Vị trí chuyên môn:</span>
            <span className="text-xl">{totalSpecializations}</span>
          </div>
        </div>

        {/* Table */}
        <div className="w-full rounded-xl shadow border overflow-x-auto bg-white mt-6">
          <table className="w-full text-left">
            <thead>
              <tr className="bg-blue-50 text-blue-900">
                <th className="py-3 px-5 font-semibold w-2/6">Nghề</th>
                <th className="py-3 px-5 font-semibold w-3/6">
                  Vị trí chuyên môn
                </th>
                <th className="py-3 px-5 font-semibold text-center w-1/6">
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody>
              {pageOccupations.length === 0 ? (
                <tr>
                  <td colSpan={3} className="text-center text-gray-400 py-10">
                    Không tìm thấy nghề nào.
                  </td>
                </tr>
              ) : (
                pageOccupations.map((job) => {
                  const specs = getSpecs(job.id);
                  return (
                    <tr
                      key={job.id}
                      className="border-t last:border-b group hover:bg-blue-50"
                    >
                      {/* Nghề */}

                      <td className="px-5 py-4 font-medium flex items-center gap-2">
                        <StatusDot status={job.status} />
                        {job.name}
                        <button
                          className="text-blue-500 hover:bg-blue-100 p-2 rounded-full ml-2"
                          title="Cập nhật nghề"
                          onClick={() =>
                            openModal(
                              "occupation",
                              job,
                              groups.find((g) => g.id === job.groupId)
                            )
                          }
                        >
                          <Pencil size={16} />
                        </button>
                      </td>
                      {/* Vị trí chuyên môn */}
                      <td className="px-5 py-4">
                        <div className="flex flex-wrap gap-2 items-center">
                          {specs.length === 0 ? (
                            <span className="text-gray-400 italic">
                              Chưa có
                            </span>
                          ) : (
                            specs.slice(0, 3).map((s) => (
                              <span
                                key={s.id}
                                className="bg-blue-100 text-blue-700 px-3 py-1 rounded-full text-sm flex items-center gap-1"
                              >
                                <StatusDot status={s.status} />
                                {s.name}
                                <button
                                  className="inline-block text-blue-500 hover:bg-blue-200 p-1 rounded-full"
                                  title="Cập nhật"
                                  onClick={() =>
                                    openModal("specialization", s, job)
                                  }
                                >
                                  <Pencil size={14} />
                                </button>
                              </span>
                            ))
                          )}
                          {specs.length > 3 && (
                            <button
                              className="ml-2 underline text-blue-700 hover:text-blue-900 text-sm"
                              onClick={() => openModal("specView", job)}
                            >
                              +{specs.length - 3} nữa
                            </button>
                          )}
                          <button
                            className="ml-2 px-2 py-1 rounded text-xs text-white bg-indigo-500 hover:bg-indigo-600"
                            onClick={() =>
                              openModal("specialization", null, job)
                            }
                          >
                            + Thêm vị trí chuyên môn
                          </button>
                        </div>
                      </td>
                      {/* Thao tác */}
                      <td className="px-5 py-4 text-center">
                        <button
                          className="inline-flex items-center gap-1 px-4 py-1.5 bg-white border border-indigo-500 text-indigo-700 font-bold rounded-xl shadow-sm hover:bg-indigo-50 transition text-sm"
                          onClick={() => openModal("specView", job)}
                        >
                          <Eye size={16} />
                          Xem tất cả
                        </button>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="flex justify-center mt-6 items-center gap-3">
            <button
              className="p-2 rounded-full hover:bg-blue-100 disabled:opacity-50"
              disabled={currentPage === 1}
              onClick={() => setCurrentPage((p) => Math.max(p - 1, 1))}
            >
              {"<"}
            </button>
            <span>
              Trang <b>{currentPage}</b> / {totalPages}
            </span>
            <button
              className="p-2 rounded-full hover:bg-blue-100 disabled:opacity-50"
              disabled={currentPage === totalPages}
              onClick={() => setCurrentPage((p) => Math.min(p + 1, totalPages))}
            >
              {">"}
            </button>
          </div>
        )}

        {/* Nút thêm nghề (phải có nhóm nghề) */}
        {selectedGroupId && (
          <div className="flex justify-end mt-4">
            <button
              className="flex items-center gap-2 px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition"
              onClick={() =>
                openModal(
                  "occupation",
                  null,
                  groups.find((g) => g.id === selectedGroupId)
                )
              }
            >
              <Plus size={18} />
              Thêm nghề mới
            </button>
          </div>
        )}
      </main>

      {/* ========== MODAL FORM ========== */}
      {modal.type && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
          <div className="relative bg-white rounded-xl shadow-2xl w-full max-w-md">
            <button
              onClick={closeModal}
              className="absolute top-3 right-3 p-2 hover:bg-gray-100 rounded-full"
            >
              <X size={20} className="text-gray-500" />
            </button>
            {/* Group form */}
            {/* Group form */}
            {modal.type === "group" && (
              <form className="p-8" onSubmit={handleSubmit} autoComplete="off">
                <div className="mb-4">
                  <h3 className="text-lg font-bold text-blue-900 mb-2">
                    {modal.data ? "Cập nhật nhóm nghề" : "Thêm nhóm nghề"}
                  </h3>
                  {/* Hiển thị tên và trạng thái */}
                  {modal.data && (
                    <div className="text-sm text-gray-600 mb-4">
                      <p>
                        <strong>Tên nhóm nghề:</strong> {modal.data.name}
                      </p>
                      <p>
                        <strong>Trạng thái:</strong> {modal.data.status}
                      </p>
                    </div>
                  )}

                  <input
                    type="text"
                    placeholder="Tên nhóm nghề"
                    required
                    className="w-full px-3 py-2 border rounded-lg mt-2"
                    value={form.name || ""}
                    onChange={(e) =>
                      setForm((f) => ({ ...f, name: e.target.value }))
                    }
                  />
                  <select
                    className="w-full px-3 py-2 border rounded-lg mt-2"
                    value={form.status || "Enable"}
                    onChange={(e) =>
                      setForm((f) => ({ ...f, status: e.target.value }))
                    }
                    required
                  >
                    <option value="Enable">Hiện</option>
                    <option value="Disable">Ẩn</option>
                  </select>
                </div>
                <div className="flex justify-end gap-2 mt-4">
                  <button
                    type="button"
                    onClick={closeModal}
                    className="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg"
                  >
                    Huỷ
                  </button>
                  <button
                    type="submit"
                    className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
                  >
                    <Save size={18} /> Lưu
                  </button>
                </div>
              </form>
            )}

            {/* Occupation form */}
            {modal.type === "occupation" && (
              <form className="p-8" onSubmit={handleSubmit}>
                <div className="mb-4">
                  <h3 className="text-lg font-bold text-blue-900 mb-2">
                    {modal.data
                      ? "Cập nhật nghề"
                      : `Thêm nghề mới cho nhóm "${modal.parent?.name}"`}
                  </h3>
                  {/* Hiển thị tên và trạng thái */}
                  {modal.data && (
                    <div className="text-sm text-gray-600 mb-4">
                      <p>
                        <strong>Tên nghề:</strong> {modal.data.name}
                      </p>
                      <p>
                        <strong>Trạng thái:</strong> {modal.data.status}
                      </p>
                    </div>
                  )}
                  <input
                    type="text"
                    placeholder="Tên nghề"
                    required
                    className="w-full px-3 py-2 border rounded-lg mt-2"
                    value={form.name || ""}
                    onChange={(e) =>
                      setForm((f) => ({ ...f, name: e.target.value }))
                    }
                  />
                  {/* Nếu là cập nhật, cho phép chọn lại nhóm nghề */}
                  {modal.data && (
                    <select
                      className="w-full px-3 py-2 border rounded-lg mt-2"
                      value={form.occupationGroupId || ""}
                      onChange={(e) =>
                        setForm((f) => ({
                          ...f,
                          occupationGroupId: Number(e.target.value),
                        }))
                      }
                      required
                    >
                      <option value="">Chọn nhóm nghề</option>
                      {groups.map((g) => (
                        <option key={g.id} value={g.id}>
                          {g.name}
                        </option>
                      ))}
                    </select>
                  )}
                  <select
                    className="w-full px-3 py-2 border rounded-lg mt-2"
                    value={form.status || "Enable"}
                    onChange={(e) =>
                      setForm((f) => ({ ...f, status: e.target.value }))
                    }
                    required
                  >
                    <option value="Enable">Hiện</option>
                    <option value="Disable">Ẩn</option>
                  </select>
                </div>
                <div className="flex justify-end gap-2 mt-4">
                  <button
                    type="button"
                    onClick={closeModal}
                    className="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg"
                  >
                    Huỷ
                  </button>
                  <button
                    type="submit"
                    className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
                  >
                    <Save size={18} /> Lưu
                  </button>
                </div>
              </form>
            )}

            {/* Specialization form */}
            {/* Specialization form */}
            {modal.type === "specialization" && (
              <form className="p-8" onSubmit={handleSubmit}>
                <div className="mb-4">
                  <h3 className="text-lg font-bold text-blue-900 mb-2">
                    {modal.data
                      ? "Cập nhật vị trí chuyên môn"
                      : `Thêm vị trí chuyên môn cho nghề "${modal.parent?.name}"`}
                  </h3>
                  {/* Hiển thị tên và trạng thái */}
                  {modal.data && (
                    <div className="text-sm text-gray-600 mb-4">
                      <p>
                        <strong>Tên vị trí chuyên môn:</strong>{" "}
                        {modal.data.name}
                      </p>
                      <p>
                        <strong>Trạng thái:</strong> {modal.data.status}
                      </p>
                    </div>
                  )}
                  <input
                    type="text"
                    placeholder="Tên vị trí chuyên môn"
                    required
                    className="w-full px-3 py-2 border rounded-lg mt-2"
                    value={form.name || ""}
                    onChange={(e) =>
                      setForm((f) => ({ ...f, name: e.target.value }))
                    }
                  />
                  {/* Nếu là cập nhật, cho phép chọn lại nghề */}
                  {modal.data && (
                    <select
                      className="w-full px-3 py-2 border rounded-lg mt-2"
                      value={form.occupationId || ""}
                      onChange={(e) =>
                        setForm((f) => ({
                          ...f,
                          occupationId: Number(e.target.value),
                        }))
                      }
                      required
                    >
                      <option value="">Chọn nghề</option>
                      {occupations
                        .filter((occ) => occ.groupId === modal.parent.groupId) // Chỉ lọc những nghề có cùng groupId
                        .map((occ) => (
                          <option key={occ.id} value={occ.id}>
                            {occ.name}
                          </option>
                        ))}
                    </select>
                  )}
                  {/* Chọn trạng thái */}
                  <select
                    className="w-full px-3 py-2 border rounded-lg mt-2"
                    value={form.status || "Enable"}
                    onChange={(e) =>
                      setForm((f) => ({ ...f, status: e.target.value }))
                    }
                    required
                  >
                    <option value="Enable">Hiện</option>
                    <option value="Disable">Ẩn</option>
                  </select>
                </div>
                <div className="flex justify-end gap-2 mt-4">
                  <button
                    type="button"
                    onClick={closeModal}
                    className="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg"
                  >
                    Huỷ
                  </button>
                  <button
                    type="submit"
                    className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
                  >
                    <Save size={18} /> Lưu
                  </button>
                </div>
              </form>
            )}

            {/* Specialization view */}
            {modal.type === "specView" && (
              <div className="p-8">
                <h3 className="text-lg font-bold text-blue-900 mb-4">
                  Danh sách vị trí chuyên môn{" "}
                  <span className="text-indigo-600">({modal.data.name})</span>
                </h3>
                <ul className="space-y-2 max-h-72 overflow-auto">
                  {getSpecs(modal.data.id).length === 0 ? (
                    <li className="text-gray-400 italic">
                      Chưa có vị trí chuyên môn.
                    </li>
                  ) : (
                    getSpecs(modal.data.id).map((s) => (
                      <li key={s.id} className="flex items-center gap-2">
                        <span className="w-2 h-2 rounded-full bg-indigo-400" />
                        <span>{s.name}</span>
                        <button
                          className="ml-2 p-1 rounded-full hover:bg-blue-100 text-blue-500"
                          onClick={() =>
                            openModal("specialization", s, modal.data)
                          }
                        >
                          <Pencil size={14} />
                        </button>
                      </li>
                    ))
                  )}
                </ul>
                <div className="flex justify-end mt-6">
                  <button
                    onClick={closeModal}
                    className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
                  >
                    Đóng
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
