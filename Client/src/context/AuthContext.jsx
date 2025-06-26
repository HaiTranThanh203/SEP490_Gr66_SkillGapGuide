import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { supabase } from '../config/supabase';
import { userService } from '../services/userService';
import { useContext } from 'react';
import { UserContext } from '../context/UserContext';

export default function AuthCallbackPage() {
  const navigate = useNavigate();
  const { setUser } = useContext(UserContext);

  useEffect(() => {
    const finishOAuthLogin = async () => {
      try {
        const { data: { session }, error } = await supabase.auth.getSession();

        if (error || !session?.user) {
          console.error("OAuth callback session error:", error);
          return;
        }

        // Gọi API để lấy thông tin profile từ backend
        const profile = await userService.viewProfile();
        console.log('✅ Profile loaded:', profile);

        setUser(profile); // lưu vào context
        localStorage.setItem('user', JSON.stringify(profile)); // lưu vào localStorage

        navigate('/'); // điều hướng về trang chính
      } catch (err) {
        console.error("OAuth login finalization error:", err);
      }
    };

    finishOAuthLogin();
  }, [navigate, setUser]);

  return <div className="p-4 text-center text-lg">🔄 Đang xác thực đăng nhập...</div>;
}
