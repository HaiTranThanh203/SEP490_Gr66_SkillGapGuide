import { apiService } from './api';
import { ENDPOINTS } from '../constants/apiEndpoints';

export const scrapeJobService = {
  scrapeJob: async (url) => {
    return await apiService.post(ENDPOINTS.scraper.scrapeJob, {url});
  },

  crawlTenJobs: async (url) => {
    return await apiService.post(ENDPOINTS.scraper.crawlTenJobs, {url});
  }
};