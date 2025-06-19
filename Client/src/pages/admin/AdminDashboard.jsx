import { useState } from "react";
import {
  BarChart, Bar,
  LineChart, Line,
  PieChart, Pie, Cell,
  XAxis, YAxis, Tooltip, ResponsiveContainer,
  CartesianGrid,
} from "recharts";

const AdminDashboard = () => {
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");

  const barData = [
    { name: "T2", orders: 5 },
    { name: "T3", orders: 12 },
    { name: "T4", orders: 9 },
    { name: "T5", orders: 8 },
    { name: "T6", orders: 13 },
    { name: "T7", orders: 47 },
    { name: "CN", orders: 6 },
  ];

  const lineData = [
    { name: "T2", new: 3, old: 2 },
    { name: "T3", new: 7, old: 4 },
    { name: "T4", new: 6, old: 5 },
    { name: "T5", new: 4, old: 6 },
    { name: "T6", new: 9, old: 2 },
    { name: "T7", new: 24, old: 8 },
    { name: "CN", new: 10, old: 3 },
  ];

  const pieData = [
    { name: "Trả phí", value: 70 },
    { name: "Không trả phí", value: 30 },
  ];
  const COLORS = ["#4B8A94", "#d1d5db"];

  return (
    <div className="flex flex-col lg:flex-row gap-6 p-6">
      {/* Main Chart Section */}
      <div className="flex-1 bg-white shadow rounded-lg p-6">
        <h2 className="text-2xl font-semibold mb-4">Tổng doanh thu</h2>
        <div className="flex gap-4 mb-6">
          <input
            type="date"
            className="border px-3 py-2 rounded text-sm text-gray-600"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
          />
          <input
            type="date"
            className="border px-3 py-2 rounded text-sm text-gray-600"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
          />
          <button className="bg-indigo-500 text-white px-4 py-2 rounded text-sm hover:bg-indigo-600">
            Lọc
          </button>
        </div>
        <div className="w-full h-[300px]">
  <ResponsiveContainer width="100%" height="100%">
    <LineChart data={[
      { name: 'T2', value: 10 },
      { name: 'T3', value: 30 },
      { name: 'T4', value: 20 },
      { name: 'T5', value: 40 },
      { name: 'T6', value: 25 },
      { name: 'T7', value: 50 },
    ]}>
      <XAxis dataKey="name" stroke="#888" />
      <YAxis stroke="#888" />
      <CartesianGrid strokeDasharray="3 3" vertical={false} />
      <Tooltip />
      <Line
        type="monotone"
        dataKey="value"
        stroke="#000"
        strokeWidth={3}
        dot={{ r: 3, strokeWidth: 2, fill: "white" }}
        activeDot={{ r: 6 }}
      />
    </LineChart>
  </ResponsiveContainer>
</div>

      </div>

      {/* Side Charts */}
      <div className="w-full lg:w-[300px] flex flex-col gap-4">
        {/* Tổng đơn */}
        <div className="bg-white p-4 rounded shadow">
          <div className="flex justify-between items-center">
            <div>
              <h4 className="text-sm font-medium text-gray-700">Tổng đơn</h4>
              <p className="text-xs text-gray-500">7 ngày gần nhất</p>
            </div>
            <p className="text-red-600 font-bold text-lg">47</p>
          </div>
          <ResponsiveContainer width="100%" height={80}>
            <BarChart data={barData}>
              <Bar dataKey="orders" fill="#4B8A94" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Khách hàng mới */}
        <div className="bg-white p-4 rounded shadow">
          <div className="flex justify-between items-center">
            <div>
              <h4 className="text-sm font-medium text-gray-700">Khách hàng mới</h4>
              <p className="text-xs text-gray-500">7 ngày gần nhất</p>
            </div>
            <p className="text-red-600 font-bold text-lg">24</p>
          </div>
          <ResponsiveContainer width="100%" height={80}>
            <LineChart data={lineData}>
              <XAxis dataKey="name" hide />
              <YAxis hide />
              <CartesianGrid strokeDasharray="3 3" vertical={false} />
              <Tooltip />
              <Line
                type="monotone"
                dataKey="new"
                stroke="#4B8A94"
                strokeWidth={2}
                dot={{ r: 3, strokeWidth: 1, fill: "#4B8A94" }}
                activeDot={{ r: 5 }}
              />
              <Line
                type="monotone"
                dataKey="old"
                stroke="#a3a3a3"
                strokeWidth={2}
                dot={{ r: 2, strokeWidth: 1, fill: "#a3a3a3" }}
              />
            </LineChart>
          </ResponsiveContainer>
        </div>

        {/* Trả phí và không trả phí */}
        <div className="bg-white p-4 rounded shadow text-center">
          <div className="flex justify-between items-center">
            <div className="text-left">
              <h4 className="text-sm font-medium text-gray-700">Trả phí và không trả phí</h4>
              <p className="text-xs text-gray-500">7 ngày gần nhất</p>
            </div>
          </div>
          <ResponsiveContainer width="100%" height={120}>
            <PieChart>
              <Pie
                data={pieData}
                cx="50%"
                cy="50%"
                innerRadius={35}
                outerRadius={45}
                startAngle={180}
                endAngle={0}
                paddingAngle={2}
                dataKey="value"
              >
                {pieData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index]} />
                ))}
              </Pie>
            </PieChart>
          </ResponsiveContainer>
          <div className="text-xs mt-2">
            <span className="text-[#4B8A94] font-semibold">Khách hàng trả phí</span> |{" "}
            <span className="text-gray-400">Không trả phí</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;
