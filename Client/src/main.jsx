import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import './styles/custom.css'
import './styles/global.css'

import App from './App.jsx'
import { UserProvider } from './context/UserContext.jsx'

import { setupTokenExpiryWatcher, decodeJWT } from './utils/jwt.js'
import { logoutAndRedirect } from './utils/auth.js'

// ✅ Kiểm tra token ngay khi app khởi động
const token = localStorage.getItem('token');
if (token) {
  const payload = decodeJWT(token);
  if (payload?.exp) {
    const now = Date.now();
    const expTime = payload.exp * 1000;
    if (expTime <= now) {
      logoutAndRedirect(); // Token đã hết hạn → logout ngay
    } else {
      setupTokenExpiryWatcher(
        token,
        logoutAndRedirect,
        () => {
          alert("Phiên đăng nhập sẽ hết hạn sau 1 phút!");
        },
        60000
      );
    }
  } else {
    logoutAndRedirect(); // Token không hợp lệ
  }
}

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <UserProvider>
      <App />
    </UserProvider>
  </StrictMode>
)
