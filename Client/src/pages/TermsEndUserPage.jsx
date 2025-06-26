import React, { useEffect, useState, useRef } from "react";
import { staticPageService } from "../services/staticPageService";

// 3 Tab lựa chọn, gọi API tương ứng
const tabs = [
  { label: "Terms of Service", key: "terms", fetcher: staticPageService.getTermsOfService },
  { label: "Privacy Policy", key: "privacy", fetcher: staticPageService.getPrivacy },
  { label: "Social Links", key: "social", fetcher: staticPageService.getSocialLink },
];

export default function TermsEndUserPage() {
  const [selectedTab, setSelectedTab] = useState(tabs[0].key);
  const [sections, setSections] = useState([]);
  const [loading, setLoading] = useState(false);

  // refs để scroll đến section khi click menu
  const sectionRefs = useRef({});

  useEffect(() => {
    const tab = tabs.find((t) => t.key === selectedTab);
    if (!tab) return;
    setLoading(true);
    tab.fetcher()
      .then((data) => setSections(Array.isArray(data) ? data : []))
      .catch(() => setSections([]))
      .finally(() => setLoading(false));
  }, [selectedTab]);

  // Tạo danh sách mục lục từ title section
  const tocItems = sections.map((s, idx) => ({
    id: `section-${idx}`,
    label: s.title,
  }));

  // Scroll đến section khi click mục lục
  const scrollToSection = (id) => {
    const ref = sectionRefs.current[id];
    if (ref) ref.scrollIntoView({ behavior: "smooth", block: "start" });
  };

  return (
    <div className="min-h-screen flex bg-gray-50">
      {/* Sidebar mục lục */}
      <aside className="w-72 min-w-60 bg-white border-r px-6 py-10 hidden md:block sticky top-0 h-screen overflow-y-auto">
        {/* Tabs */}
        <div className="mb-8">
          {tabs.map(tab => (
            <button
              key={tab.key}
              className={`block w-full text-left font-semibold py-2 px-3 rounded mb-1 transition 
                ${selectedTab === tab.key ? "bg-blue-100 text-blue-900" : "text-gray-700 hover:bg-gray-100"}`}
              onClick={() => setSelectedTab(tab.key)}
            >
              {tab.label}
            </button>
          ))}
        </div>
        {/* Table of contents */}
        <nav className="border-t pt-4">
          <ul className="space-y-1 text-gray-700 text-base">
            {tocItems.map((item) => (
              <li key={item.id}>
                <button
                  className="hover:text-blue-700 text-left w-full"
                  onClick={() => scrollToSection(item.id)}
                >
                  {item.label}
                </button>
              </li>
            ))}
          </ul>
        </nav>
      </aside>

      {/* Main content */}
      <main className="flex-1 px-4 md:px-16 py-10 max-w-4xl mx-auto">
        {loading ? (
          <div className="py-24 text-center text-blue-700 text-xl">Đang tải nội dung...</div>
        ) : (
          <div>
            {/* Render động các section */}
            {sections.map((sec, idx) => (
              <section
                key={idx}
                id={`section-${idx}`}
                ref={el => sectionRefs.current[`section-${idx}`] = el}
                className={idx !== 0 ? "mt-12" : ""}
              >
                {/* Title lớn nếu là section đầu, các section sau nhỏ hơn */}
                <h2 className={`mb-3 ${idx === 0 ? "text-2xl md:text-3xl font-bold mt-0" : "text-xl md:text-2xl font-bold mt-6"}`}>
                  {sec.title}
                </h2>
                {/* Nội dung: có thể là HTML hoặc plain text */}
                <div
                  className="mb-4 text-gray-800 leading-relaxed"
                  style={{ whiteSpace: "pre-line" }}
                  dangerouslySetInnerHTML={{ __html: sec.content }}
                />
              </section>
            ))}
            {/* Fallback nếu chưa có section */}
            {sections.length === 0 && (
              <div className="text-gray-500 py-16 text-lg">Không có dữ liệu.</div>
            )}
          </div>
        )}
      </main>
    </div>
  );
}
