// Giữ nguyên phần import như bạn đã có
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import TopMenu from "./TopMenu";
import qrImage from "/images/qr-demo.png";
import vnpayLogo from "/images/vnpay.png";
import bidv from "/images/bidv.png";
import mb from "/images/mb.png";
import vietcombank from "/images/vietcombank.png";
import vietinbank from "/images/vietinbank.png";

const plans = [
  {
    name: "Miễn phí",
    price: "0 VNĐ",
    amount: 0,
    description:
      "Miễn phí dành cho các cá nhân muốn tải lên CV và nhận được danh sách kỹ năng hiện tại.",
    button: "➜ Tiếp tục",
  },
  {
    name: "Gói nâng cao",
    price: "100.000 VNĐ / 1 tháng",
    amount: 100000,
    description:
      "Dành cho các cá nhân muốn phân tích đầy đủ kỹ năng từ mô tả công việc cụ thể.",
    button: "➜ Đăng ký",
  },
  {
    name: "Gói toàn diện",
    price: "200.000 VNĐ / 1 tháng",
    amount: 200000,
    description:
      "Bao gồm mọi tính năng của gói phổ thông, theo dõi tiến độ học và các tính năng cao cấp.",
    button: "➜ Đăng ký",
  },
];

const featureGroups = [
  {
    title: "📁 Tải lên CV & mô tả công việc",
    items: [
      { feature: "Tải lên CV của bạn", values: [true, true, true] },
      { feature: "Tải lên mô tả công việc", values: [true, true, true] },
      { feature: "Nhập link mô tả công việc từ TOPCV", values: [true, true, true] },
    ],
  },
  {
    title: "💼 Công việc phù hợp",
    items: [
      { feature: "Danh sách công việc phù hợp", values: [false, true, true] },
      { feature: "Độ phù hợp CV và vị trí công việc", values: [false, true, true] },
    ],
  },
  {
    title: "🧠 Phân tích kỹ năng & Khóa học",
    items: [
      { feature: "Kỹ năng hiện tại của bạn", values: [false, false, true] },
      { feature: "Kỹ năng còn thiếu", values: [false, false, true] },
      { feature: "Kỹ năng yêu cầu của từng mô tả công việc", values: [false, false, true] },
      { feature: "Độ phù hợp giữa kỹ năng hiện tại và yêu cầu", values: [false, false, true] },
      { feature: "Nhận định chung kỹ năng của bạn và mô tả", values: [false, false, true] },
      { feature: "Danh sách khóa học gợi ý", values: [false, false, true] },
      { feature: "Thêm khóa học yêu thích", values: [false, false, true] },
      { feature: "Theo dõi cập nhật tiến độ khóa học", values: [false, false, true] },
    ],
  },
];

const ServicePayment = () => {
  const navigate = useNavigate();
  const [selectedPlan, setSelectedPlan] = useState(null);
  const [paymentMethod, setPaymentMethod] = useState("");

  const handleSelectPlan = (plan) => {
    if (plan.amount === 0) {
      navigate("/cv-upload-options");
    } else {
      setSelectedPlan(plan);
      setPaymentMethod("");
    }
  };
  

  const renderPaymentPopup = () => {
  if (!selectedPlan) return null;

  const handleConfirmTransfer = () => {
    alert("Cảm ơn bạn! Hệ thống sẽ xác nhận thanh toán trong giây lát.");
    setSelectedPlan(null);
    setPaymentMethod("");
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-30 backdrop-blur-sm flex items-center justify-center z-50">
      <div className="bg-white p-6 rounded-xl shadow-xl w-[400px] border border-gray-300 relative">
        <h3 className="text-red-600 font-semibold text-base mb-4">
          Thanh toán – {selectedPlan.name}
        </h3>

        <p className="font-medium text-sm mb-2">Chọn phương thức thanh toán</p>

        {/* Radio: QR */}
        <label className="flex items-center gap-2 mb-3 text-sm cursor-pointer">
          <input
            type="radio"
            name="method"
            value="qr"
            checked={paymentMethod === "qr"}
            onChange={(e) => setPaymentMethod(e.target.value)}
          />
          <span className="text-blue-600 font-medium">Thanh toán qua QR code</span>
        </label>

        {/* Radio: VNPay */}
        <label className="flex items-center gap-2 mb-4 text-sm cursor-pointer">
          <input
            type="radio"
            name="method"
            value="vnpay"
            checked={paymentMethod === "vnpay"}
            onChange={(e) => setPaymentMethod(e.target.value)}
          />
          <span className="text-gray-800 font-medium">Thanh toán qua VNPay</span>
          <img src={vnpayLogo} alt="vnpay" className="h-5" />
        </label>

        {/* QR thanh toán */}
        {paymentMethod === "qr" && (
          <>
            <div className="flex items-center justify-center relative mb-2">
              <img src={qrImage} alt="QR" className="w-40 h-40 rounded" />
            </div>

            <p className="text-center text-red-600 text-sm font-semibold">
              Mức phí: {selectedPlan.amount.toLocaleString()} VNĐ
            </p>
            <p className="text-xs text-gray-500 text-center mt-1">
              (Khách hàng vui lòng không thay đổi nội dung chuyển khoản)
            </p>

            <div className="mt-4 text-center">
              <button
                onClick={handleConfirmTransfer}
                className="bg-green-600 hover:bg-green-700 text-white text-sm px-4 py-2 rounded-md"
              >
                ✅ Tôi đã chuyển khoản
              </button>
            </div>
          </>
        )}

        {/* VNPay redirect */}
        {paymentMethod === "vnpay" && (
          <div className="mt-4 border rounded bg-white p-3 text-sm text-center">
            <p className="text-gray-700 mb-2">
              Bạn sẽ được chuyển hướng tới cổng thanh toán VNPay.
            </p>
            <button
              className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 text-sm"
              onClick={() => {
                window.location.href =
                  "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
              }}
            >
              Tiếp tục thanh toán
            </button>
          </div>
        )}

        {/* Nút đóng */}
        <button
          onClick={() => {
            setSelectedPlan(null);
            setPaymentMethod("");
          }}
          className="absolute top-2 right-3 text-gray-400 hover:text-black text-lg"
        >
          &#10005;
        </button>
      </div>
    </div>
  );
};


  const renderFeatureGroup = (title, featureList) => (
    <div className="space-y-2">
      <h3 className="text-md font-semibold text-gray-700">{title}</h3>
      <div className="border border-blue-400 rounded-xl overflow-hidden text-sm">
        <table className="w-full border-collapse">
          <thead className="bg-blue-50 text-gray-800 font-semibold text-center">
            <tr>
              <th className="border px-4 py-3 text-left w-[50%]">Tính năng</th>
              <th className="border px-4 py-3">Miễn phí</th>
              <th className="border px-4 py-3">Gói nâng cao</th>
              <th className="border px-4 py-3">Gói toàn diện</th>
            </tr>
          </thead>
          <tbody>
            {featureList.map((item, idx) => (
              <tr key={idx} className="text-center align-top">
                <td className="border px-4 py-3 text-left text-gray-800">{item.feature}</td>
                {item.values.map((value, i) => (
                  <td key={i} className="border px-4 py-3">{value ? "✔" : "✖"}</td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );

  return (
    <>
      <div className="max-w-7xl mx-auto">
        <TopMenu />
      </div>
      <div className="bg-white min-h-screen p-6 max-w-6xl mx-auto space-y-8">
        <h2 className="text-xl font-semibold text-gray-800">Thanh toán – Đăng ký gói dịch vụ</h2>

        {/* Plans */}
        <div className="grid md:grid-cols-3 gap-4">
          {plans.map((plan, idx) => (
            <div key={idx} className="border border-blue-300 rounded-xl p-4 shadow-sm bg-white">
              <h3 className="text-center font-semibold text-[16px] text-gray-800 mb-1">{plan.name}</h3>
              <p className="text-center text-red-600 font-semibold text-sm">{plan.price}</p>
              <p className="text-sm text-gray-700 mt-2 text-center">{plan.description}</p>
              <div className="mt-4 text-center">
                <button
                  className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-1 text-sm rounded-md"
                  onClick={() => handleSelectPlan(plan)}
                >
                  {plan.button}
                </button>
              </div>
            </div>
          ))}
        </div>

        {/* Render feature groups */}
        {featureGroups.map((group, index) =>
          renderFeatureGroup(group.title, group.items)
        )}

        {renderPaymentPopup()}
      </div>
    </>
  );
};

export default ServicePayment;
