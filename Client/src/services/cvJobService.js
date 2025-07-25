import { apiService } from './api'; // Import service API của bạn
import { ENDPOINTS } from '../constants/apiEndpoints';

export const cvService = {
  // API call to upload CV
uploadCV: async (file) => {
  try {
    const formData = new FormData();
    formData.append("file", file);

    // GỬI TRỰC TIẾP KHÔNG SET HEADERS
    const response = await apiService.post(ENDPOINTS.cv.uploadCV, formData);

    return response;
  } catch (error) {
    console.error("Error uploading CV:", error);
    throw new Error(error.message || "Upload CV failed");
  }
},
  // API call to get CV skills
  getCVSkill: async (cvId) => {
    try {
      const response = await apiService.get(ENDPOINTS.cv.getCVSkill);
      return response;
    } catch (error) {
      console.error("Error fetching CV skills:", error);
      throw new Error(error.message || "Fetch CV skills failed");
    }
  },
 uploadJobDescription: async (files) => {
  try {
    const formData = new FormData();
    // files là mảng File (array), ví dụ lấy từ input type="file" multiple
    for (let i = 0; i < files.length; i++) {
      formData.append("file", files[i]);
    }

    // KHÔNG set headers thủ công với FormData
    const response = await apiService.post(ENDPOINTS.job.uploadFileJob, formData);

    return response;
  } catch (error) {
    console.error("Error uploading job description:", error);
    throw new Error(error.message || "Upload job description failed");
  }
}

   // API call to get job skills
  ,
 getDataJobFromLink: async (url) => {
  try {
    const response = await apiService.post(
      ENDPOINTS.scraper.scrapeJob,
      { url } // truyền object có key là "url"
    );
    return response;
  } catch (error) {
    console.error("Error fetching job data from link:", error);
    throw new Error(error.message || "Fetch job data from link failed");
  }
}

};
