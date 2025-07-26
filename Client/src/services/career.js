import { apiService } from './api';
import { ENDPOINTS } from '../constants/apiEndpoints';
export const careerService = {
  viewSpecialization: async () => {
    const response = await apiService.get(ENDPOINTS.carrer.viewSpecialization);
    return response.result;
  },

  viewSpecializationEnable: async () => {
    const response = await apiService.get(ENDPOINTS.carrer.viewSpecializationEnable);
    return response.result;
  },

  viewOccupations: async () => {
    const response = await apiService.get(ENDPOINTS.carrer.viewOccupations);
    return response.result;
  },

  viewOccupationsEnable: async () => {
    const response = await apiService.get(ENDPOINTS.carrer.viewOccupationsEnable);
    return response.result;
  },

  viewOccupationGroups: async () => {
    const response = await apiService.get(ENDPOINTS.carrer.viewOccupationGroups);
    return response.result;
  },

  viewOccupationGroupsEnable: async () => {
    const response = await apiService.get(ENDPOINTS.carrer.viewOccupationGroupsEnable);
    return response.result;
  },

//   viewJobCategory: async () => {
//     const response = await apiService.get(ENDPOINTS.carrer.viewJobCategory);
//     return response;
//   },

//   searchSpecialization: async (query) => {
//     const response = await apiService.get(`${ENDPOINTS.carrer.searchSpecialization}?query=${query}`);
//     return response;
//   },

//   filterSpecializationsByOccupation: async (occupationId) => {
//     const response = await apiService.get(`${ENDPOINTS.carrer.filterSpecializationsByOccupation}?occupationId=${occupationId}`);
//     return response;
//   },

//   filterOccupationByGroup: async (groupId) => {
//     const response = await apiService.get(`${ENDPOINTS.carrer.filterOccupationByGroup}?groupId=${groupId}`);
//     return response;
//   }
  editSpecialization: async (id, data) => {
    const { name, occupationId, status } = data;
    const response = await apiService.put(`${ENDPOINTS.carrer.editSpecialization.replace('{id}', id)}`, { name, occupationId, status });
    return response;
  },

  editOccupations: async (id, data) => {
    const { name, occupationGroupId, status } = data;
    const response = await apiService.put(`${ENDPOINTS.carrer.editOccupations.replace('{id}', id)}`, { name, occupationGroupId, status });
    return response;
  },

  editOccupationGroups: async (id, data) => {
    const { name, status } = data;
    const response = await apiService.put(ENDPOINTS.carrer.editOccupationGroups, { id, name, status });
    return response;
  },

  

  disableSpecialization: async (id) => {
    const response = await apiService.put(`${ENDPOINTS.carrer.disableSpecialization.replace('{id}', id)}`);
    return response;
  },

  disableOccupation: async (id) => {
    const response = await apiService.put(`${ENDPOINTS.carrer.disableOccupation.replace('{id}', id)}`);
    return response;
  },

  disableOccupationGroups: async (id) => {
    const response = await apiService.put(`${ENDPOINTS.carrer.disableOccupationGroups.replace('{id}', id)}`);
    return response;
  },
  /**
     * @param {{name: string,occupationId: number , status: string}} data
     */

  addSpecialization: async (data) => {
    const { name, occupationId, status } = data;
    const response = await apiService.post(ENDPOINTS.carrer.addSpecialization, { name, occupationId, status });
    return response;
  },
 /**
     * @param {{name: string,occupationGroupId: number , status: string}} data
     */
  addOccupations: async (data) => {
    const { name, occupationGroupId, status } = data;
    const response = await apiService.post(ENDPOINTS.carrer.addOccupations, { name, occupationGroupId, status });
    return response;
  },

 /**
     * @param {{name: string, status: string}} data
     */
  addOccupationGroups: async (data) => {
    const { name, status } = data;
    const response = await apiService.post(ENDPOINTS.carrer.addOccupationGroups, { name, status });
    return response;
  },

  // addJobCategory: async (data) => {
  //   const response = await apiService.post(ENDPOINTS.carrer.addJobCategory, data);
  //   return response;
  // }
};