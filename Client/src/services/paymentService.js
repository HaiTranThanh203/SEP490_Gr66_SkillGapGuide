import { apiService } from './api';
import { ENDPOINTS } from '../constants/apiEndpoints';

export const paymentService = {
  // /api/payment/vnpay-return?param1=xxx&param2=yyy ...
  vnpayReturn: async (params) => {
    return await apiService.get(ENDPOINTS.payment.vnpayReturn, { params });
  },

  // /api/payment/findPaymentByPaymentId?paymentId=xxx
  findPaymentByPaymentId: async (paymentId) => {
    return await apiService.get(ENDPOINTS.payment.findPaymentByPaymentId, { paymentId } );
  },

  // /api/payment/findAllPayments?pageNo=1&pageSize=10
  findAllPayments: async (pageNo = 1, pageSize = 10) => {
    return await apiService.get(ENDPOINTS.payment.findAllPayments,  { pageNo, pageSize } );
  },

  // /api/payment/filter?status=xxx&pageNo=1&pageSize=10
  filter: async (status, pageNo = 1, pageSize = 10) => {
    return await apiService.get(ENDPOINTS.payment.filter,  { status, pageNo, pageSize } );
  },

  // /api/payment/filter/byUserId?userId=xxx&pageNo=1&pageSize=10
  filterByUserId: async (userId, pageNo = 1, pageSize = 10) => {
    return await apiService.get(ENDPOINTS.payment.filterByUserId,  { userId, pageNo, pageSize } );
  },

  // /api/payment/filter/byDatesRange?startDate=xxx&endDate=xxx&pageNo=1&pageSize=10
  filterByDatesRange: async (startDate, endDate, pageNo = 1, pageSize = 10) => {
    return await apiService.get(ENDPOINTS.payment.filterByDatesRange, 
     { startDate, endDate, pageNo, pageSize }
    );
  },

  // /api/payment/export/pdf
  exportPdf: async () => {
    return await apiService.get(ENDPOINTS.payment.exportPdf, { responseType: 'blob' });
  },

  // /api/payment/export/excel
  exportExcel: async () => {
    return await apiService.get(ENDPOINTS.payment.exportExcel, { responseType: 'blob' });
  },

  // /api/payment/create?amount=xxx
  create: async (amount) => {
    return await apiService.get(ENDPOINTS.payment.create,  { amount } );
  },
};
