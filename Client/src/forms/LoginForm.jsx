import { useForm } from "react-hook-form";
import { authService } from '../services/authService';
import { useNavigate, Link } from 'react-router-dom';
import { useState, memo, useCallback } from "react";
import { userService } from "../services/userService";
import { useContext } from 'react';
import { UserContext } from '../context/UserContext';

export default memo(function LoginForm() {
  const { register, handleSubmit, formState: { errors } } = useForm();
  const navigate = useNavigate();
  const [loginError, setLoginError] = useState('');
  const { setUser } = useContext(UserContext);
  
  const onSubmit = useCallback(async (data) => {
    try {
      setLoginError(''); // Clear previous errors
      await authService.loginWithEmail(data.email, data.password);
      const userData = await userService.viewProfile();
      
      if (!userData || !userData.email) {
        throw new Error("Lỗi tải thông tin người dùng");
      }
      
      // First set in context
      setUser(userData);
      // Then explicitly store in localStorage
      
      
      console.log('🔐 User saved:', userData);
      navigate('/');
    } catch (error) {
      console.error("Login failed:", error);
      setLoginError(error.message);
    }
  }, [navigate, setUser]);

  const handleGoogleLogin = useCallback(async () => {
    try {
      await authService.loginWithGoogle();
      const userData = await userService.viewProfile();
      
      if (!userData || !userData.email) {
        throw new Error("Lỗi tải thông tin người dùng");
      }
      
      // First set in context
      setUser(userData);
      // Then explicitly store in localStorage
      localStorage.setItem('user', JSON.stringify(userData));
      
      console.log('🔐 User saved:', userData);
      navigate('/'); // or wherever you want to redirect
    } catch (error) {
      console.error("Google login failed:", error);
      setLoginError("Đăng nhập thất bại");
    }
  }, [navigate, setUser]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-tr from-blue-200 via-blue-300 to-blue-500">
      <div className="flex flex-col md:flex-row items-center justify-center w-full max-w-3xl">
        {/* Form Box */}
        <div className="bg-white rounded-2xl shadow-2xl p-8 w-full max-w-sm">
          <h2 className="text-2xl font-bold mb-6 text-gray-800 text-center">Đăng nhập</h2>
          {loginError && (
            <div className="mb-4 p-2 bg-red-100 border border-red-400 text-red-700 rounded">
              {loginError}
            </div>
          )}
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div>
              <label className="block text-gray-700 font-medium mb-1">Email</label>
              <input
                {...register("email", { required: "Email không được bỏ trống" })}
                placeholder="nguyena@gmail.com"
                className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-300 transition"
                autoComplete="email"
              />
              {errors.email && <p className="text-red-500 text-sm mt-1">{errors.email.message}</p>}
            </div>
            <div>
              <label className="block text-gray-700 font-medium mb-1">Mật khẩu</label>
              <input
                type="password"
                {...register("password", { required: "Mật khẩu không được bỏ trống" })}
                placeholder="Nhập mật khẩu của bạn"
                className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-300 transition"
                autoComplete="current-password"
              />
              {errors.password && <p className="text-red-500 text-sm mt-1">{errors.password.message}</p>}
            </div>
            <button
              type="submit"
              className="w-full bg-blue-800 hover:bg-blue-900 text-white font-bold py-2 rounded shadow transition focus:ring-2 focus:ring-blue-400"
            >
              Đăng nhập
            </button>
          </form>
          <div className="mt-4 flex items-center justify-center">
            <span className="border-t w-full"></span>
            <span className="px-4 text-gray-500">hoặc</span>
            <span className="border-t w-full"></span>
          </div>
          <button
            type="button"
            onClick={handleGoogleLogin}
            className="mt-4 w-full flex items-center justify-center gap-2 bg-white border border-gray-300 rounded-lg px-4 py-2 text-gray-700 hover:bg-gray-50 transition"
          >
            <img src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg" alt="Google" className="w-5 h-5" />
            Đăng nhập bằng Google
          </button>
          <div className="text-center mt-4 text-sm">
            Chưa có tài khoản?{" "}
            <Link to="/register" className="text-blue-800 font-semibold hover:underline">
              Đăng ký
            </Link>
          </div>
           <div className="text-center mt-4 text-sm">
            Quên mật khẩu ?{" "}
            <Link to="/forgot-password" className="text-blue-800 font-semibold hover:underline">
              Lấy lại mật khẩu
            </Link>
          </div>
        </div>
        {/* Text Right */}
        <div className="md:ml-10 mt-8 md:mt-0 text-white flex-1 text-center md:text-left flex items-center justify-center">
          <div className="text-2xl md:text-3xl font-bold drop-shadow">
            Đăng nhập<br />với tài khoản của bạn
          </div>
        </div>
      </div>
    </div>
  );
});
