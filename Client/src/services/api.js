import axios from 'axios';
import { showError } from "../utils/alert"; 
const BASE_URL = import.meta.env.VITE_APP_API_URL || 'http://localhost:8080';
// 1. api instance - Cấu hình cơ bản axios
const api = axios.create({
  baseURL: BASE_URL,
  timeout: 200000,// 180 giây timeout
  // headers: {
  //   'Content-Type': 'application/json',
  // }
});

// Request interceptor
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response) {
      const message = error.response.data?.message || '';
      if (
        error.response.status === 401 ||
        error.response.status === 403
      ) {
        // Kiểm tra message backend trả về có thông tin hết hạn JWT không
        if (
          message.includes('ExpiredJwtException') ||
          message.toLowerCase().includes('jwt expired')
        ) {
          localStorage.removeItem('token');
          showError(
            "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.",
            "Thông báo"
          );
          setTimeout(() => {
            window.location.href = "/login";
          }, 1500);
        } else {
          
        }
      } else if (error.response.status === 404) {
        showError(
          "Không tìm thấy tài nguyên. Vui lòng kiểm tra lại URL hoặc liên hệ hỗ trợ.",
          "Lỗi 404"
        );
      } else {
        showError(
          `Lỗi: ${message || 'Có lỗi xảy ra, vui lòng thử lại.'}`,
          "Lỗi"
        );
        console.error("API Error:", error.response.data);
      }
    }
    return Promise.reject(error);
  }
);


// 2. apiService - Wrapper với xử lý lỗi và interface đơn giản
export const apiService = {
  get: async (endpoint, params = {}) => {
    try {
      // Sử dụng api instance bên trong
      return await api.get(endpoint, { params });
    } catch (error) {
      throw error;
    }
  },

  post: async (endpoint, data = {}) => {
    try {
      return await api.post(endpoint, data);
    } catch (error) {
      throw error;
    }
  },

  put: async (endpoint, data = {}) => {
    try {
      return await api.put(endpoint, data);
    } catch (error) {
      throw error;
    }
  },

  delete: async (endpoint) => {
    try {
      return await api.delete(endpoint);
    } catch (error) {
      throw error;
    }
  },

  patch: async (endpoint, data = {}) => {
    try {
      return await api.patch(endpoint, data);
    } catch (error) {
      throw error;
    }
  }
};

export default apiService;
