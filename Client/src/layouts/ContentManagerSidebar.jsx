import { NavLink } from "react-router-dom";
import {
  Users,
  FileText,
  Info,
  MessageSquare,
  Link,
  Tags,
  Home,
} from "lucide-react";

const menu = [
  { label: "Quản lý nội dung Homepage", to: "/content-manager/homepage-manage", icon: <Home size={20} /> },
  { label: "Quản lý nội dung About Us", to: "/content-manager/about-us", icon: <Info size={20} /> },

  { label: "Quản lý liên kết", to: "/content-manager/social-link", icon: <Link size={20} /> },
  { label: "Quản lý phản hồi", to: "/content-manager/feedback", icon: <MessageSquare size={20} /> },
  { label: "Quản lý thẻ kỹ năng", to: "/content-manager/tag-skills", icon: <Tags size={20} /> },
  { label: "Quản lý nội dung tĩnh", to: "/content-manager/static-content", icon: <FileText size={20} /> },
];

export default function ContentManagerSidebar() {
  return (
    <aside className="bg-blue-300 min-h-screen w-64 p-6 flex flex-col gap-2 rounded-tr-3xl rounded-br-3xl shadow-lg">
      <nav className="flex flex-col gap-2">
        {menu.map((item, idx) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              `block py-2 px-4 rounded-lg text-white font-semibold tracking-wide transition-all
              ${isActive ? "bg-blue-500 shadow text-white" : "hover:bg-blue-400 hover:text-white"}`
            }
            end={idx === 0} // Make "Bảng điều khiển" exact match
          >
            <span className="flex items-center gap-2">
              {item.icon}
              {item.label}
            </span>
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
