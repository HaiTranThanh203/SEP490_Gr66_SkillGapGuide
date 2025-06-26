export const ENDPOINTS = {
  auth: {
    login: '/api/auth/login',
    register: '/api/auth/register'
  },
  users: {
    // getAll: '/users',
    // getById: (id) => `/users/${id}`,
    // create: '/users',
    // update: (id) => `/users/${id}`,
    // delete: (id) => `/users/${id}`,
    forgetPassword: '/api/user/forgot-password',
    resetPassword: '/api/user/reset-password',
    // updateProfile: '/api/user/update-profile'   ,

    changePassword: '/api/user/changePassword' ,           
    viewProfile: `/api/profile/viewprofile`,
    updateProfile: `/api/profile/update`,
    viewSubscription: `/api/profile/viewscription`,

  },
  admin : {
    getAllUsers: '/api/admin/getAllUser',
    getUserByEmail: (email) => `/api/admin/getUserDetail/${email}`,
    enableUser: (email) => `/api/admin/enableAccount/${email}`,
    disableUser: (email) => `/api/admin/disableAccount/${email}`,

  pages: {
      // GET endpoints
      getTermsOfService: '/api/admin/pages/getTermsOfService',
      getSocialLink: '/api/admin/pages/getSocialLink',
      getPrivacy: '/api/admin/pages/getPrivacy',
      getHomePage: '/api/admin/pages/getHomePage',
      getAboutUs: '/api/admin/pages/getAboutUs',

      // POST endpoints
      updateTermsOfService: '/api/admin/pages/updateTermsOfService',
      updateSocialLink: '/api/admin/pages/updateSocialLink',
      updatePrivacy: '/api/admin/pages/updatePrivacy',
      updateHomePage: '/api/admin/pages/updateHomePage',
      updateAboutUs: '/api/admin/pages/updateAboutUs',
    },

  },
  
  scraper: {
    scrapeJob: '/api/scrape/job',
    crawlTenJobs: '/api/scrape/crawl-10-jobs'
  },
  feedback :{
    getAllFeedbacks: '/api/feedback/getAll',
    getFeedbackByEmail: (email) => `/api/feedback/getFeedbackById/${email}`,
  
    // createFeedback: '/api/feedback/createFeedback',
    // updateFeedback: (id) => `/api/feedback/updateFeedback/${id}`,
    // deleteFeedback: (id) => `/api/feedback/deleteFeedback/${id}`

  },
  systemAdmin: {
    createAccount: '/api/systemadmin/create-admin',
    changeRole:'/api/systemadmin/change-role',
}};


