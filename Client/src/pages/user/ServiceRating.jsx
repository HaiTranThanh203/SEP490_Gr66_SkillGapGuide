import React, { useState } from "react";
import TopMenu from "./TopMenu";
import { FaStar } from "react-icons/fa";

const ServiceRating = () => {
  const [rating, setRating] = useState(0);
  const [hover, setHover] = useState(0);
  const [comment, setComment] = useState("");

  const handleSubmit = () => {
    if (rating === 0 || comment.trim() === "") {
      alert("Vui lòng chọn số sao và nhập nội dung đánh giá.");
      return;
    }
    alert(`Đánh giá đã gửi!\nSố sao: ${rating}\nNội dung: ${comment}`);
    setRating(0);
    setHover(0);
    setComment("");
  };

  return (
    <>
      <div className="max-w-7xl mx-auto">
        <TopMenu />
      </div>

      <div className="max-w-3xl mx-auto p-6">
        <h2 className="text-xl font-semibold text-gray-800 mb-4">Đánh giá dịch vụ</h2>
        <p className="text-sm text-gray-600 mb-2">Đánh giá từ 1–5</p>

        <div className="flex items-center gap-1 mb-4">
          {[...Array(5)].map((_, i) => {
            const value = i + 1;
            return (
              <FaStar
                key={value}
                size={24}
                className={
                  value <= (hover || rating)
                    ? "text-yellow-400 cursor-pointer"
                    : "text-gray-300 cursor-pointer"
                }
                onClick={() => setRating(value)}
                onMouseEnter={() => setHover(value)}
                onMouseLeave={() => setHover(0)}
              />
            );
          })}
        </div>

        <textarea
          rows="5"
          placeholder="Nhập đánh giá của bạn ....................."
          value={comment}
          onChange={(e) => setComment(e.target.value)}
          className="w-full border-2 border-blue-400 rounded-md p-3 text-sm focus:outline-none focus:ring focus:ring-blue-300"
        ></textarea>

        <div className="mt-4">
          <button
            onClick={handleSubmit}
            className="bg-yellow-400 hover:bg-yellow-500 text-white px-5 py-2 text-sm rounded"
          >
            Gửi đánh giá
          </button>
        </div>

        <p className="text-center text-sm text-gray-600 mt-6">
          Chúng tôi luôn lắng nghe mọi phản hồi của khách hàng để có sự cải thiện tốt hơn!<br />
          Cảm ơn bạn đã luôn tin tưởng!
        </p>

        
      </div>
    </>
  );
};

export default ServiceRating;
