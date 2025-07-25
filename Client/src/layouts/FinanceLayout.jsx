// src/layouts/AdminLayout.jsx
import FinanceSideBar from "./FinanceSidebar";
import { Outlet } from "react-router-dom";
import AdminHeader from "./AdminHeader";
import { useState } from "react";

export default function FinanceLayout() {
  const [sidebarOpen, setSidebarOpen] = useState(true);

  return (
    <div className="min-h-screen bg-slate-50">
      <AdminHeader 
        className="fixed top-0 w-full z-40 bg-white/80 backdrop-blur-sm border-b border-slate-200" 
        onMenuClick={() => setSidebarOpen(!sidebarOpen)}
      />
      
      <div className="flex">
        <div className={`fixed left-0 top-0 h-screen transition-all duration-300 ease-in-out
          ${sidebarOpen ? 'w-64' : 'w-20'} z-30`}>
          <FinanceSideBar collapsed={!sidebarOpen} />
        </div>
        
        <main className={`flex-1 transition-all duration-300 ease-in-out
          ${sidebarOpen ? 'ml-64' : 'ml-20'} mt-16`}>
          <div className="container mx-auto px-4 py-8">
            <div className="bg-white rounded-xl shadow-sm border border-slate-200/60 p-6 
              backdrop-blur-xl backdrop-saturate-150">
              <Outlet />
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
