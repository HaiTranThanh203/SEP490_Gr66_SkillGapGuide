import { PieChart, Pie, Cell } from "recharts";


const Home = () => {
  return (
    <div className="flex flex-col min-h-screen">


      {/* Hero Section */}
      <section className="bg-gray-100 py-12 text-center px-4">
        <div className="flex flex-col md:flex-row items-center justify-between max-w-6xl mx-auto">
          <div className="text-left max-w-xl">
            <h2 className="text-3xl font-semibold leading-snug">
              Khám phá năng lực nghề nghiệp từ chính CV của bạn
            </h2>
            <p className="mt-4 text-gray-600">
              Tải lên CV – nhận ngay bản đồ kỹ năng cần thiết, đề xuất định hướng, khóa học phù hợp
            </p>
            <button className="mt-6 bg-indigo-700 text-white px-6 py-3 rounded-lg shadow-md hover:bg-indigo-800 transition">
              Tải lên CV & nhận phân tích
            </button>
          </div>
          <img
            src="https://cdn-icons-png.flaticon.com/512/4139/4139981.png"
            alt="CV upload"
            className="w-48 mt-8 md:mt-0"
          />
        </div>
      </section>

      {/* Result Steps */}
      <section className="py-16 bg-white text-center relative px-4">
        <h3 className="text-2xl font-semibold mb-16">Kết quả bạn nhận được</h3>

        {/* Container */}
        <div className="relative flex flex-wrap justify-center gap-20 md:gap-32 items-center max-w-6xl mx-auto z-10">

          {/* Step 1 */}
          <div className="flex flex-col items-center w-40 relative z-10">
            <div className="bg-indigo-50 rounded-full w-20 h-20 flex items-center justify-center mb-3">
              <img
                src="https://cdn-icons-png.flaticon.com/512/300/300221.png"
                alt="CV"
                className="w-10 h-10 object-contain"
              />
            </div>
            <p className="text-sm text-indigo-900">Tải CV của bạn</p>
          </div>

          {/* Connector 1 */}
          <div className="hidden md:block h-[2px] w-12 bg-gray-300"></div>

          {/* Step 2 */}
          <div className="flex flex-col items-center w-40 relative z-10">
            <div className="bg-indigo-50 rounded-full w-20 h-20 flex items-center justify-center mb-3">
              <img
                src="https://cdn-icons-png.flaticon.com/512/3523/3523063.png"
                alt="Xử lý"
                className="w-10 h-10 object-contain"
              />
            </div>
            <p className="text-sm text-indigo-900 text-center">
              Chúng tôi xử lý & <br /> phân tích kỹ năng còn thiếu
            </p>
          </div>

          {/* Connector 2 */}
          <div className="hidden md:flex items-center gap-1">
            <div className="h-[2px] w-12 bg-gray-300"></div>
            <div className="w-3 h-3 rotate-45 border-t-2 border-r-2 border-gray-400"></div>
          </div>

          {/* Step 3 */}
          <div className="flex flex-col items-center w-40 relative z-10">
            <div className="bg-indigo-50 rounded-full w-20 h-20 flex items-center justify-center mb-3">
              <img
                src="https://cdn-icons-png.flaticon.com/512/929/929564.png"
                alt="Kết quả"
                className="w-10 h-10 object-contain"
              />
            </div>
            <p className="text-sm text-indigo-900 text-center">
              Nhận kết quả kỹ năng thiếu <br /> & gợi ý khóa học, nghề nghiệp phù hợp
            </p>
          </div>

        </div>
      </section>



      {/* Skill Match Result */}
      <section className="bg-gray-50 py-12 text-center px-4">
        <h3 className="text-2xl font-semibold mb-8">Kết quả kỹ năng</h3>
        <div className="flex flex-wrap justify-center gap-8">
          <div className="p-6 border rounded-lg max-w-sm shadow-md relative flex flex-col items-center">
            <PieChart width={200} height={100}>
              <Pie
                data={[
                  { name: "Matched", value: 94 },
                  { name: "Remaining", value: 6 },
                ]}
                cx="50%"
                cy="100%"
                startAngle={180}
                endAngle={0}
                innerRadius={60}
                outerRadius={80}
                dataKey="value"
              >
                <Cell fill="#4B8A94" />
                <Cell fill="#e5e5e5" />
              </Pie>
            </PieChart>
            <div className="absolute top-[75px] text-xl font-bold text-black">94%</div>
            <p className="mt-4 text-sm text-gray-700 text-center">
              Biểu đồ phần trăm phù hợp giữa kỹ năng của bạn và ngành công việc lựa chọn.
              Đưa ra chi tiết kỹ năng cần bổ sung để hoàn thiện.
            </p>
          </div>

          <div className="p-6 border rounded-lg max-w-sm shadow-md">
            <img
              src="https://cdn-icons-png.flaticon.com/512/2936/2936885.png"
              alt="Khoá học"
              className="mx-auto mb-4 h-16"
            />
            <p>Đề xuất khóa học bổ sung phù hợp</p>
          </div>
          <div className="p-6 border rounded-lg max-w-sm shadow-md">
            <img
              src="https://cdn-icons-png.flaticon.com/512/1077/1077063.png"
              alt="Công việc"
              className="mx-auto mb-4 h-16"
            />
            <p>Đề xuất công việc phù hợp</p>
          </div>
        </div>
      </section>

      {/* Testimonials */}
      <section className="py-12 text-center bg-white px-4">
        <h3 className="text-xl font-semibold mb-8">Người dùng nói gì về chúng tôi?</h3>
        <div className="flex flex-wrap justify-center gap-6">
          <div className="border p-4 rounded-md shadow max-w-sm">
            <img
              src="https://cdn-icons-png.flaticon.com/512/4140/4140048.png"
              alt="User 1"
              className="w-12 h-12 rounded-full mx-auto mb-2"
            />
            <p className="italic">“Tôi đã có trải nghiệm thật tốt.”</p>
            <p className="mt-2 text-yellow-500">⭐️⭐️⭐️⭐️⭐️</p>
          </div>
          <div className="border p-4 rounded-md shadow max-w-sm">
            <img
              src="https://cdn-icons-png.flaticon.com/512/4140/4140061.png"
              alt="User 2"
              className="w-12 h-12 rounded-full mx-auto mb-2"
            />
            <p className="italic">“Nó đã giúp tôi rất nhiều.”</p>
            <p className="mt-2 text-yellow-500">⭐️⭐️⭐️⭐️⭐️</p>
          </div>
        </div>
      </section>

    </div>
  );
};

export default Home;
