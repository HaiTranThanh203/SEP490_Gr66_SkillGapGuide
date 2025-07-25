import Header from "./Header";
import Footer from "./Footer";
import { Outlet } from "react-router-dom";
import {
  Home,
  User,
  FileText,
  Info,
  Phone
} from 'lucide-react';

const menuItems = [
  { path: "/", name: "Home", icon: <Home size={20} /> },
  { path: "/profile", name: "Profile", icon: <User size={20} /> },
  { path: "/cv-skills", name: "CV Skills", icon: <FileText size={20} /> },
  { path: "/about-us", name: "About Us", icon: <Info size={20} /> },
  { path: "/contact", name: "Contact", icon: <Phone size={20} /> },
];

export default function UserLayout() {
  return (
    <div className="flex flex-col min-h-screen">
  <Header />

  <main className="flex-1">
    <div className="min-h-[700px]"> {/* 👈 Thêm min-h để đẩy footer */}
      <Outlet />
    </div>
  </main>

  <Footer />
</div>

  );
}
