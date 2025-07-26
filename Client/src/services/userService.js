import { apiService } from "./api";
import { ENDPOINTS } from "../constants/apiEndpoints";

export const userService = {
  getAllUsers: () => apiService.get(ENDPOINTS.users.getAll),

  getUserById: (id) => apiService.get(ENDPOINTS.users.getById(id)),

  createUser: (userData) => apiService.post(ENDPOINTS.users.create, userData),

  updateUser: (id, userData) =>
    apiService.put(ENDPOINTS.users.update(id), userData),

  deleteUser: (id) => apiService.delete(ENDPOINTS.users.delete(id)),

  forgotPassword: async (email) => {
    return await apiService.post(ENDPOINTS.users.forgetPassword, { email });
  },

  resetPassword: async (token, newPassword) => {
    return await apiService.get(ENDPOINTS.users.resetPassword, {
      token,
      newPassword,
    });
  },
  viewProfile: async () => {
    return await apiService.get(ENDPOINTS.users.viewProfile);
  },
  viewSubscription: async () => {
    return await apiService.get(ENDPOINTS.users.viewSubscription);
  },
  updateProfile: async (profileData) => {
    const { fullName, phone, avatar } = profileData;
    return await apiService.put(ENDPOINTS.users.updateProfile, {
      fullName,
      phone,
      avatar,
    });
  },
  changePassword: async (currentPassword, newPassword) => {
    return await apiService.post(ENDPOINTS.users.changePassword, {
      oldPassword: currentPassword,
      newPassword,
    });
  },
 getFavoriteMissingSkills: async (userId, pageNo = 1, pageSize = 10) => {
  return await apiService.get(
    `${ENDPOINTS.users.getFavoriteMissingSkills.replace("{userId}", userId)}?pageNo=${pageNo}&pageSize=${pageSize}`
  );
},

  addFavoriteMissingSkill: async (userId, skillId) => {
    return await apiService.post(
      ENDPOINTS.users.addFavoriteMissingSkill.replace("${userId}", userId).replace("${skillId}", skillId)
    );
  },
  getFileHistory: async () => {
    return await apiService.get(ENDPOINTS.users.fileHistory);
  },
};
