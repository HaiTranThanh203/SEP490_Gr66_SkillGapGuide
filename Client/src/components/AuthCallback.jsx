import { useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { supabase } from '../config/supabase';
import { authService } from '../services/authService';
import { userService } from '../services/userService';
import { UserContext } from '../context/UserContext';

function AuthCallback() {
  const navigate = useNavigate();
  const { setUser } = useContext(UserContext); // Nếu bạn có dùng context

  useEffect(() => {
    const handleCallback = async () => {
      try {
        await new Promise(resolve => setTimeout(resolve, 100));
        const { data: { session }, error } = await supabase.auth.getSession();

        if (error) throw error;

        if (session) {
          // 1. Gửi session về backend lấy JWT
          const response = await authService.sendUserToBackend(session);

          if (response.status === 200 && response.result?.token) {
            localStorage.setItem('token', response.result.token);
            window.dispatchEvent(new Event('authStateChanged'));
            console.log('Google login successful');

            // 2. Gọi tiếp API lấy profile
            const userData = await userService.viewProfile();

            if (!userData || !userData.email) {
              throw new Error("Không lấy được profile user");
            }

            // 3. Lưu vào context hoặc localStorage
            setUser && setUser(userData); // nếu có context
            localStorage.setItem('user', JSON.stringify(userData));

            // 4. Điều hướng
            navigate('/profile');
          } else {
            throw new Error('Invalid response format from server');
          }
        } else {
          navigate('/login');
        }
      } catch (error) {
        console.error('Auth callback error:', error);
        navigate('/login');
      }
    };

    handleCallback();
  }, [navigate, setUser]);

  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="text-center">
        <div className="text-xl mb-4">Đang xử lý đăng nhập...</div>
        <div className="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-500 mx-auto"></div>
      </div>
    </div>
  );
}

export default AuthCallback;
