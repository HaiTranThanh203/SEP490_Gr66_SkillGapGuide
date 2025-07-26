// utils/auth.js
import { showError } from "./alert";

export function logoutAndRedirect() {
  localStorage.clear();
  showError("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.", "Thông báo");
  setTimeout(() => {
    window.location.href = "/login";
  }, 1500);
}
