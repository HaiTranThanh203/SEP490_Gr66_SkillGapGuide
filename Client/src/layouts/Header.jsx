import { Link, useNavigate } from "react-router-dom";
import { authService } from "../services/authService";
import { useState, useEffect, memo, useRef ,useContext } from "react";
import { UserContext } from "../context/UserContext";
const Header = memo(function Header() {
  const [openMenu, setOpenMenu] = useState(null);
  const navigate = useNavigate();
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('token'));
  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const [userAvatar, setUserAvatar] = useState("/default_avatar.png"); // Default avatar
  const profileMenuRef = useRef(null);
const { user } = useContext(UserContext); // <-- Lấy user từ context
 
  useEffect(() => {
    const checkLoginStatus = () => {
      setIsLoggedIn(!!localStorage.getItem('token'));
    };

    // Kiểm tra khi component mount
    checkLoginStatus();

    // Lắng nghe sự thay đổi từ event custom
    window.addEventListener('authStateChanged', checkLoginStatus);

    return () => {
      window.removeEventListener('authStateChanged', checkLoginStatus);
    };
  }, []);

  useEffect(() => {
    // Click outside to close profile menu
    const handleClickOutside = (event) => {
      if (profileMenuRef.current && !profileMenuRef.current.contains(event.target)) {
        setShowProfileMenu(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);
  useEffect(() => {
    if (user && user.avatar) {
      setUserAvatar(user.avatar);
    } else {
      setUserAvatar("/default-avatar.png");
    }
  }, [user]);

  const handleLogout = async () => {
    try {
      await authService.logout();
      setIsLoggedIn(false);
      navigate('/login');
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  // Menu có submenu
  const menus = [
    { label: "Trang chủ", to: "/" },
    {
      label: "Về chúng tôi",
      submenu: [
        { label: "Giới thiệu", to: "/about-us" },
        { label: "Liên hệ", to: "/contact" },
      ],
    },
    {
      label: "Thanh toán",
      submenu: [
        { label: "Học phí", to: "/pricing" },
        { label: "Hỗ trợ", to: "/support" },
      ],
    },
  ];

  return (
    <header className="bg-gradient-to-r from-blue-900 via-blue-400 to-blue-300 px-6 py-3 shadow-md w-full">
      <nav className="max-w-7xl mx-auto flex items-center">
        {/* Logo */}
        <Link to="/" className="text-white text-3xl font-bold mr-12 tracking-wide">
          SkillGapGuide
        </Link>

        {/* Menu */}
        <ul className="flex-1 flex items-center gap-8 text-white font-normal text-lg">
          {menus.map((menu, i) =>
            menu.submenu ? (
              <li
                key={menu.label}
                className="relative group"
                onMouseEnter={() => setOpenMenu(i)}
                onMouseLeave={() => setOpenMenu(null)}
              >
                <span className="flex items-center gap-1 hover:text-blue-100 cursor-pointer py-2">
                  {menu.label}
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                  </svg>
                </span>
                {/* Dropdown with padding to prevent hover gap */}
                <div className="absolute left-0 top-full pt-2">
                  <ul className={`bg-white rounded-lg shadow-xl text-gray-700 overflow-hidden ${
                    openMenu === i ? "block" : "hidden"
                  }`}>
                    {menu.submenu.map((item) => (
                      <li key={item.to}>
                        <Link
                          to={item.to}
                          className="block px-6 py-3 hover:bg-blue-50 whitespace-nowrap text-base transition-colors"
                        >
                          {item.label}
                        </Link>
                      </li>
                    ))}
                  </ul>
                </div>
              </li>
            ) : (
              <li key={menu.label}>
                <Link
                  to={menu.to}
                  className="hover:underline"
                >
                  {menu.label}
                </Link>
              </li>
            )
          )}
        </ul>

        {/* Button Đăng ký/Đăng nhập/Profile */}
        <div className="flex items-center gap-2 ml-4">
          {!isLoggedIn ? (
            <>
              <Link to="/login" className="bg-white text-blue-900 font-semibold rounded-xl px-5 py-1.5 shadow hover:bg-blue-100 transition border border-blue-200">
                Đăng Nhập
              </Link>
              <Link to="/register" className="bg-white text-blue-900 font-semibold rounded-xl px-5 py-1.5 shadow hover:bg-blue-100 transition border border-blue-200">
                Đăng Kí
              </Link>
            </>
          ) : (
            <div className="flex items-center gap-3">
              <div className="relative" ref={profileMenuRef}>
                <button 
                  onClick={() => setShowProfileMenu(!showProfileMenu)}
                  className="flex items-center gap-2 bg-white rounded-full p-1 hover:ring-2 hover:ring-blue-300 transition"
                >
                  <img 
                    src={userAvatar} 
                    alt="Profile" 
                    className="w-8 h-8 rounded-full object-cover"
                  />
                </button>

                {/* Profile Dropdown Menu */}
                {showProfileMenu && (
                  <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-xl z-50">
                    <div className="py-1">
                      <Link
                        to="/profile"
                        className="block px-4 py-2 text-sm text-gray-700 hover:bg-blue-50"
                      >
                        Thông tin cá nhân
                      </Link>
                      <Link
                        to="/settings"
                        className="block px-4 py-2 text-sm text-gray-700 hover:bg-blue-50"
                      >
                        Cài đặt
                      </Link>
                      <hr className="my-1" />
                      <button
                        onClick={handleLogout}
                        className="block w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50"
                      >
                        Đăng xuất
                      </button>
                    </div>
                  </div>
                )}
              </div>
            </div>
          )}
        </div>
      </nav>
    </header>
  );
});

export default Header;
