import axios from 'axios';

export const API_BASE_URL = import.meta.env.VITE_API_URL;

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 15000,
});

// Interceptor to inject Bearer token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Auth Service
export const authService = {
  async register(data) {
    const response = await api.post('/auth/register', data);
    return response.data;
  },

  async login(data) {
    const response = await api.post('/auth/login', data);
    return response.data;
  }
};

// Farmer Profile Service
export const farmerService = {
  async getProfile(farmerId) {
    const response = await api.get(`/farmers/${farmerId}`);
    return response.data;
  },

  async updateProfile(farmerId, data) {
    const response = await api.put(`/farmers/${farmerId}`, data);
    return response.data;
  },

  async updateLanguage(farmerId, language) {
    const response = await api.put(`/farmers/${farmerId}/language`, { language, preferredLanguage: language });
    return response.data;
  },

  async addCrop(farmerId, cropData) {
    const response = await api.post(`/farmers/${farmerId}/crops`, cropData);
    return response.data;
  },

  async getCrops(farmerId) {
    const response = await api.get(`/farmers/${farmerId}/crops`);
    return response.data;
  }
};

// Procurement Centre Service
export const centreService = {
  async getAllCentres() {
    const response = await api.get('/centres');
    return response.data;
  },

  async getRecommendations(latitude, longitude, cropType = 'Paddy', quantity = 500) {
    const response = await api.get('/centres/recommend', {
      params: { latitude, longitude, cropType, quantity }
    });
    return response.data;
  },

  async getCentreById(id) {
    const response = await api.get(`/centres/${id}`);
    return response.data;
  },

  async getSlots(centreId) {
    const response = await api.get(`/centres/${centreId}/slots`);
    return response.data;
  }
};

// Booking Service
export const bookingService = {
  async createBooking(bookingData) {
    try {
      const response = await api.post('/bookings', bookingData);
      return response.data;
    } catch (err) {
      if (err.response && err.response.data && err.response.data.reason) {
        throw new Error(err.response.data.reason);
      }
      throw err;
    }
  },

  async getBooking(id) {
    const response = await api.get(`/bookings/${id}`);
    return response.data;
  },

  async cancelBooking(id) {
    const response = await api.put(`/bookings/${id}/cancel`);
    return response.data;
  },

  async rescheduleBooking(id, rescheduleData) {
    const response = await api.put(`/bookings/${id}/reschedule`, rescheduleData);
    return response.data;
  },

  async startTravelling(id, latitude, longitude) {
    const response = await api.put(`/bookings/${id}/start-travelling?latitude=${latitude || ''}&longitude=${longitude || ''}`);
    return response.data;
  }
};

// Real-Time Queue & Smart Departure Service
export const queueService = {
  async getQueueStatus(bookingId) {
    const cleanId = typeof bookingId === 'string' ? (bookingId.replace('#', '') || bookingId) : bookingId;
    const response = await api.get(`/queue/farmer/${cleanId}`);
    return response.data;
  },

  async getCentreQueue(centreId) {
    const response = await api.get(`/queue/centre/${centreId}`);
    return response.data;
  },

  async requestCancellation(bookingId) {
    const cleanId = typeof bookingId === 'string' ? (bookingId.replace('#', '') || bookingId) : bookingId;
    const response = await api.post(`/queue/${cleanId}/request-cancel`);
    return response.data;
  },

  async getProcurementTimeline(bookingId) {
    const response = await api.get(`/queue/farmer/${bookingId}/timeline`);
    return response.data;
  }
};

// Payment Service
export const paymentService = {
  async getPaymentRecord(farmerId) {
    const response = await api.get(`/payments/farmer/${farmerId}`);
    if (response.data && response.data.length > 0) {
      return response.data[0];
    }
    return null;
  },

  async getFarmerPayments(farmerId) {
    const response = await api.get(`/payments/farmer/${farmerId}`);
    return response.data;
  }
};

// Notification Service
export const notificationService = {
  async getNotifications(farmerId) {
    const response = await api.get('/notifications', { params: farmerId ? { farmerId } : {} });
    return response.data.map(n => ({
      id: n.notificationId,
      title: n.title,
      message: n.message,
      type: n.eventType,
      channel: n.channel,
      status: n.status,
      unread: n.status !== 'READ',
      time: n.createdAt ? new Date(n.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : 'Just now',
      createdAt: n.createdAt
    }));
  },

  async getUnreadCount(farmerId) {
    const response = await api.get('/notifications/unread-count', { params: farmerId ? { farmerId } : {} });
    return response.data.unreadCount;
  },

  async markAsRead(id, farmerId) {
    const response = await api.patch(`/notifications/${id}/read`, null, { params: farmerId ? { farmerId } : {} });
    return response.data;
  },

  async markAllAsRead(farmerId) {
    const response = await api.patch('/notifications/read-all', null, { params: farmerId ? { farmerId } : {} });
    return response.data;
  },

  async getPreferences(farmerId) {
    const response = await api.get('/notification-preferences', { params: farmerId ? { farmerId } : {} });
    return response.data;
  },

  async updatePreferences(data, farmerId) {
    const response = await api.patch('/notification-preferences', data, { params: farmerId ? { farmerId } : {} });
    return response.data;
  }
};

export default api;
