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

const features = [
  {
    feature: "Tải lên CV của bạn",
    values: [true, true, true],
  },
  {
    feature: "Tải lên mô tả công việc",
    values: [true, true, true],
  },
  {
    feature: "Tải lên đường link mô tả từ TOPCV",
    values: [true, true, true],
  },
  {
    feature: "Xem kỹ năng hiện tại",
    values: [true, true, true],
  },
  {
    feature: "Xem độ phù hợp của kỹ năng hiện tại – mô tả công việc",
    values: [false, true, true],
  },
  {
    feature: "Xem nhận định chênh cho các kỹ năng hiện tại – mô tả công việc",
    values: [false, true, true],
  },
  {
    feature: "Xem kỹ năng còn thiếu so với mô tả công việc",
    values: [false, true, true],
  },
  {
    feature: "Xem độ phù hợp giữa kỹ năng còn thiếu và kỹ năng hiện tại",
    values: [false, true, true],
  },
  {
    feature: "Xem danh sách công việc phù hợp",
    values: [false, false, true],
  },
  {
    feature: "Xem độ phù hợp giữa CV và vị trí công việc",
    values: [false, false, true],
  },
  {
    feature: "Xem khóa học phù hợp với kỹ năng còn thiếu",
    values: [false, false, true],
  },
  {
    feature: "Thêm khóa học yêu thích",
    values: [false, false, true],
  },
  {
    feature: "Theo dõi cập nhật tiến độ khóa học",
    values: [false, false, true],
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

  // ... giữ nguyên các phần import và khai báo khác như cũ ...

const renderPaymentPopup = () => {
  if (!selectedPlan) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-20 backdrop-blur-sm flex items-center justify-center z-50">
      <div className="bg-white w-[480px] p-6 rounded-xl shadow-xl relative border border-gray-300">
        <h3 className="text-lg font-semibold text-red-600 mb-4">
          Thanh toán – {selectedPlan.name}
        </h3>

        <p className="font-medium text-sm mb-3">Chọn phương thức thanh toán</p>

        <div className="space-y-4">
          <label className="flex items-start gap-2 cursor-pointer">
            <input
              type="radio"
              name="method"
              value="bank"
              checked={paymentMethod === "bank"}
              onChange={(e) => setPaymentMethod(e.target.value)}
              className="mt-1"
            />
            <div>
              <div className="text-sm font-medium mb-1">Smart Banking (Thẻ ngân hàng)</div>
              <div className="grid grid-cols-2 gap-2">
                <img src={bidv} className="h-5" alt="bidv" />
                <img src={vietcombank} className="h-5" alt="vietcombank" />
                <img src={vietinbank} className="h-5" alt="vietinbank" />
                <img src={mb} className="h-5" alt="mbbank" />
              </div>
            </div>
          </label>

          <label className="flex items-center gap-2 cursor-pointer">
            <input
              type="radio"
              name="method"
              value="vnpay"
              checked={paymentMethod === "vnpay"}
              onChange={(e) => setPaymentMethod(e.target.value)}
            />
            <span className="text-sm font-medium">VNPAY</span>
            <img src={vnpayLogo} className="h-5" alt="vnpay" />
          </label>
        </div>

        {paymentMethod === "bank" && (
          <div className="text-center mt-5">
            <p className="text-sm mb-2">Quét mã để thanh toán</p>
            <img src={qrImage} alt="QR" className="w-36 h-36 mx-auto rounded" />
            <p className="text-red-500 text-sm mt-2 font-semibold">
              Mức phí: {selectedPlan.amount.toLocaleString()} VNĐ
            </p>
            <p className="text-xs text-gray-500 mt-1">
              (Khách hàng vui lòng không thay đổi nội dung chuyển khoản)
            </p>
          </div>
        )}

        {paymentMethod === "vnpay" && (
  <div className="mt-5 border rounded bg-white p-3 text-sm text-center">
    <p className="text-gray-700">Bạn sẽ được chuyển hướng tới cổng thanh toán VNPay.</p>
    <button
      className="mt-3 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 text-sm"
      onClick={() => {
        window.location.href = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html"; // Mô phỏng redirect
      }}
    >
      Tiếp tục thanh toán
    </button>
  </div>
)}

        <button
          onClick={() => {
            setSelectedPlan(null);
            setPaymentMethod("");
          }}
          className="absolute top-3 right-4 text-gray-400 hover:text-black text-lg"
        >
          &#10005;
        </button>
      </div>
    </div>
  );
};

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
            <div
              key={idx}
              className={`border border-blue-300 rounded-xl p-4 shadow-sm bg-white`}
            >
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

        {/* Feature Table */}
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
              {features.map((item, idx) => (
                <tr key={idx} className="text-center align-top">
                  <td className="border px-4 py-3 text-left text-gray-800">{item.feature}</td>
                  {item.values.map((value, i) => (
                    <td key={i} className="border px-4 py-3">
                      {value ? "✔" : "✖"}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {renderPaymentPopup()}
      </div>
    </>
  );
};

export default ServicePayment;