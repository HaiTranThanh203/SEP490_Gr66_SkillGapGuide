import { apiService } from "./api";
import { ENDPOINTS } from "../constants/apiEndpoints";

export const courService = {
  showCourse: async (courseId) => {
    return await apiService.post(
      ENDPOINTS.course.showCourse.replace("{courseId}", courseId)
    );
  },
  getAllCourses: async (pageNo=1 ,pageSize =10) => {
    return await apiService.get(`${ENDPOINTS.course.getAllCourses}?pageNo=${pageNo}&pageSize=${pageSize}`);
  },

  scrapeAutomation: async (numPage=1, numItems=10) => {
    return await apiService.post(`${ENDPOINTS.course.scrape}?numPage=${numPage}&numItems=${numItems}`);
  },
  addCourseManually: async (data) => {
  // Validate và chuẩn hóa đầu vào
  const payload = {
    title: data.title?.trim() || "",
    rating: data.rating?.toString() || "",
    difficulty: data.difficulty || "",
    description: data.description || "",
    provider: data.provider || "",
    status: data.status || "ACTIVE", // hoặc giá trị mặc định backend yêu cầu
    url: data.url || "",
    createdAt: data.createdAt || new Date().toISOString(),
  };
  // Gửi tới API, trả về response.data (nếu apiService trả về nhiều trường khác)
  const response = await apiService.post(ENDPOINTS.course.addCourseManually, payload);
  return response?.result || response;
}
,

  hideCourse: async (courseId) => {
    return await apiService.post(
      ENDPOINTS.course.hideCourse.replace("{courseId}", courseId)
    );
  },

  changeFavoriteCourseStatus: async (courseId, userId, status) => {
    return await apiService.post(
      ENDPOINTS.course.changeFavoriteCourseStatus
        .replace("{courseId}", courseId)
        .replace("{userId}", userId)
        .replace("{status}", status)
    );
  },

  addCourseToFavorites: async (userId, courseId) => {
    return await apiService.post(
      ENDPOINTS.course.addCourseToFavorites
        .replace("{userId}", userId)
        .replace("{courseId}", courseId)
    );
  },

  addCourseManually: async (data) => {
    return await apiService.post(ENDPOINTS.course.addCourseManually, data);
  },

  findById: async (courseId) => {
    return await apiService.get(
      ENDPOINTS.course.findById.replace("{courseId}", courseId)
    );
  },

  findAllFavoriteCourses: async (userId, pageNo = 1, pageSize = 10) => {
    const url = `${ENDPOINTS.course.findAllFavoriteCourses.replace(
      "{userId}",
      userId
    )}?pageNo=${pageNo}&pageSize=${pageSize}`;
    return await apiService.get(url);
  },

  deleteFavoriteCourse: async (userId, courseId) => {
    return await apiService.delete(
      ENDPOINTS.course.deleteFavoriteCourse
        .replace("{userId}", userId)
        .replace("{courseId}", courseId)
    );
  },

  deleteAllFavoriteCourse: async (userId) => {
    return await apiService.delete(
      ENDPOINTS.course.deleteAllFavoriteCourse.replace("{userId}", userId)
    );
  },
};
