import axios from 'axios';
import {
  mockFarmer,
  mockCrops,
  mockCentres,
  mockSlots,
  mockActiveBooking,
  mockQueueTracker,
  mockProcurementTimeline,
  mockPaymentRecord,
  mockNotifications
} from './mockData';

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
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
    try {
      const response = await api.post('/auth/register', data);
      return response.data;
    } catch (err) {
      console.warn("Backend auth register fallback to mock response", err.message);
      const userRole = data.role === 'OWNER' ? 'OWNER' : 'FARMER';
      return {
        token: "mock-jwt-token-" + Date.now(),
        message: "User registered successfully (Demo Mode)",
        farmerId: userRole === 'FARMER' ? 1 : null,
        userId: Date.now(),
        role: userRole
      };
    }
  },

  async login(data) {
    try {
      const response = await api.post('/auth/login', data);
      return response.data;
    } catch (err) {
      console.warn("Backend auth login fallback to mock response", err.message);
      let role = "FARMER";
      if (data.phone === "9999999999") {
        role = "ADMIN";
      } else if (data.phone === "9876543211" || data.phone === "9849012345") {
        role = "OWNER";
      }
      return {
        token: "mock-jwt-token-9876543210",
        message: "Login successful (Demo Mode)",
        farmerId: role === 'FARMER' ? 1 : null,
        userId: 1,
        centreId: role === 'OWNER' ? 1 : null,
        role: role
      };
    }
  }
};

// Farmer Profile Service
export const farmerService = {
  async getProfile(farmerId = 1) {
    try {
      const response = await api.get(`/farmers/${farmerId}`);
      return response.data;
    } catch (err) {
      return mockFarmer;
    }
  },

  async updateProfile(farmerId = 1, data) {
    try {
      const response = await api.put(`/farmers/${farmerId}`, data);
      return response.data;
    } catch (err) {
      return { ...mockFarmer, ...data };
    }
  },

  async updateLanguage(farmerId = 1, language) {
    try {
      const response = await api.put(`/farmers/${farmerId}/language`, { language, preferredLanguage: language });
      return response.data;
    } catch (err) {
      return { ...mockFarmer, language };
    }
  },

  async addCrop(farmerId = 1, cropData) {
    try {
      const response = await api.post(`/farmers/${farmerId}/crops`, cropData);
      return response.data;
    } catch (err) {
      return {
        cropId: Date.now(),
        cropType: cropData.cropType,
        variety: cropData.variety || 'Standard',
        quantity: cropData.quantity,
        unit: cropData.unit || 'kg',
        expectedHarvestDate: cropData.expectedHarvestDate || '2026-09-20',
        farmerId
      };
    }
  },

  async getCrops(farmerId = 1) {
    try {
      const response = await api.get(`/farmers/${farmerId}/crops`);
      return response.data;
    } catch (err) {
      return mockCrops;
    }
  }
};

// Procurement Centre Service
export const centreService = {
  async getAllCentres() {
    try {
      const response = await api.get('/centres');
      // Merge distance and load percent metrics
      return response.data.map((c, idx) => ({
        ...mockCentres[idx % mockCentres.length],
        centreId: c.centreId || c.id,
        name: c.name,
        location: c.location,
        totalCapacityKg: c.totalCapacity,
        currentLoadKg: c.currentLoad,
        availableCapacityKg: c.availableCapacity
      }));
    } catch (err) {
      return mockCentres;
    }
  },

  async getRecommendations(latitude = 17.3850, longitude = 78.4867, cropType = 'Paddy', quantity = 500) {
    try {
      const response = await api.get('/centres/recommend', {
        params: { latitude, longitude, cropType, quantity }
      });
      return response.data;
    } catch (err) {
      console.warn("Backend recommendation API fallback to mock", err.message);
      return {
        recommendedCentre: {
          centreId: 2,
          name: "Centre B (Regional Grain Mandi)",
          location: "Sangareddy Highway, Medak",
          distanceKm: 15.0,
          travelTimeMinutes: 30,
          trafficLevel: "LOW",
          trafficAware: true,
          travelTimeSource: "google",
          queueLength: 8,
          totalCapacity: 50.0,
          currentLoad: 17.5,
          loadPercentage: 35.0,
          loadClassification: "LOW",
          averageProcessingMinutes: 5.0,
          estimatedWaitMinutes: 40,
          estimatedTotalMinutes: 70,
          operatingStatus: "ACTIVE",
          availability: "AVAILABLE",
          score: 85.0,
          reasons: [
            "Short queue: 8 farmers",
            "Low traffic",
            "Fast processing speed (5 min/farmer)",
            "Available capacity (35% load)",
            "Traffic-aware travel time: 30 minutes",
            "Estimated wait: 40 minutes"
          ],
          warnings: ["Centre is 15 km away"],
          recommendationBadge: "RECOMMENDED"
        },
        centres: [
          {
            centreId: 2,
            name: "Centre B (Regional Grain Mandi)",
            location: "Sangareddy Highway, Medak",
            distanceKm: 15.0,
            travelTimeMinutes: 30,
            trafficLevel: "LOW",
            trafficAware: true,
            travelTimeSource: "google",
            queueLength: 8,
            totalCapacity: 50.0,
            currentLoad: 17.5,
            loadPercentage: 35.0,
            loadClassification: "LOW",
            averageProcessingMinutes: 5.0,
            estimatedWaitMinutes: 40,
            estimatedTotalMinutes: 70,
            operatingStatus: "ACTIVE",
            availability: "AVAILABLE",
            score: 85.0,
            reasons: [
              "Short queue: 8 farmers",
              "Low traffic",
              "Fast processing speed (5 min/farmer)",
              "Available capacity (35% load)",
              "Traffic-aware travel time is 30 minutes",
              "Estimated wait: 40 minutes"
            ],
            warnings: ["Centre is 15 km away"],
            recommendationBadge: "RECOMMENDED"
          },
          {
            centreId: 1,
            name: "Centre A (ABC Procurement Centre)",
            location: "Kondapur Main Road, Medak",
            distanceKm: 8.0,
            travelTimeMinutes: 25,
            trafficLevel: "HIGH",
            trafficAware: true,
            travelTimeSource: "google",
            queueLength: 47,
            totalCapacity: 50.0,
            currentLoad: 47.0,
            loadPercentage: 94.0,
            loadClassification: "HIGH",
            averageProcessingMinutes: 3.83,
            estimatedWaitMinutes: 180,
            estimatedTotalMinutes: 205,
            operatingStatus: "ACTIVE",
            availability: "NEAR_CAPACITY",
            score: 42.0,
            reasons: ["Close distance (8 km)"],
            warnings: [
              "High queue: 47 farmers",
              "High traffic",
              "High centre load (94%)",
              "Estimated wait: 3 hours (180 mins)"
            ],
            recommendationBadge: "ALTERNATIVE"
          },
          {
            centreId: 3,
            name: "Centre C (North Farmers Hub)",
            location: "Tupran Bypass, Medak",
            distanceKm: 5.0,
            travelTimeMinutes: 15,
            trafficLevel: "MODERATE",
            trafficAware: false,
            travelTimeSource: "fallback",
            queueLength: 0,
            totalCapacity: 50.0,
            currentLoad: 0.0,
            loadPercentage: 0.0,
            loadClassification: "LOW",
            averageProcessingMinutes: 10.0,
            estimatedWaitMinutes: 0,
            estimatedTotalMinutes: 15,
            operatingStatus: "CLOSED",
            availability: "CLOSED",
            score: -1.0,
            reasons: [],
            warnings: ["Centre is currently closed"],
            recommendationBadge: "NOT_RECOMMENDED"
          },
          {
            centreId: 4,
            name: "Centre D (South Grain Hub)",
            location: "Patancheru Industrial Area, Medak",
            distanceKm: 12.0,
            travelTimeMinutes: 30,
            trafficLevel: "MODERATE",
            trafficAware: false,
            travelTimeSource: "fallback",
            queueLength: 55,
            totalCapacity: 50.0,
            currentLoad: 55.0,
            loadPercentage: 110.0,
            loadClassification: "OVERLOADED",
            averageProcessingMinutes: 10.0,
            estimatedWaitMinutes: 550,
            estimatedTotalMinutes: 580,
            operatingStatus: "OVERLOADED",
            availability: "OVERLOADED",
            score: 10.0,
            reasons: [],
            warnings: ["Centre is overloaded (110% capacity)", "Excessive waiting queue (55 farmers)"],
            recommendationBadge: "NOT_RECOMMENDED"
          }
        ],
        calculationTimestamp: new Date().toISOString(),
        recommendationSummary: "Centre B is recommended even though it is farther away because its shorter queue, lower traffic and faster processing result in a lower estimated total travel + waiting time."
      };
    }
  },

  async getCentreById(id) {
    try {
      const response = await api.get(`/centres/${id}`);
      return response.data;
    } catch (err) {
      return mockCentres.find(c => c.centreId === Number(id)) || mockCentres[0];
    }
  },

  async getSlots(centreId) {
    return mockSlots;
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
      return {
        ...mockActiveBooking,
        bookingId: Date.now(),
        centreId: bookingData.centreId,
        quantityKg: bookingData.quantity,
        bookingDate: bookingData.bookingDate,
        slotWindow: bookingData.slot
      };
    }
  },

  async getBooking(id) {
    try {
      const response = await api.get(`/bookings/${id}`);
      return response.data;
    } catch (err) {
      return mockActiveBooking;
    }
  },

  async cancelBooking(id) {
    try {
      const response = await api.put(`/bookings/${id}/cancel`);
      return response.data;
    } catch (err) {
      return { ...mockActiveBooking, status: 'CANCELLED' };
    }
  },

  async rescheduleBooking(id, rescheduleData) {
    try {
      const response = await api.put(`/bookings/${id}/reschedule`, rescheduleData);
      return response.data;
    } catch (err) {
      return {
        ...mockActiveBooking,
        status: 'RESCHEDULED',
        bookingDate: rescheduleData.bookingDate,
        slotWindow: rescheduleData.slot
      };
    }
  }
};

// Real-Time Queue & Smart Departure Service
export const queueService = {
  async getQueueStatus(bookingId = 1) {
    // If bookingId is string like "#103", extract digits or default to 1
    const cleanId = typeof bookingId === 'string' ? (bookingId.replace('#', '') || 1) : bookingId;
    try {
      const response = await api.get(`/queue/farmer/${cleanId}`);
      return response.data;
    } catch (err) {
      console.warn("Backend queue status fallback to mock", err.message);
      return mockQueueTracker;
    }
  },

  async getCentreQueue(centreId = 1) {
    try {
      const response = await api.get(`/queue/centre/${centreId}`);
      return response.data;
    } catch (err) {
      console.warn("Backend centre queue fallback to mock", err.message);
      return [];
    }
  },

  async requestCancellation(bookingId = 1) {
    const cleanId = typeof bookingId === 'string' ? (bookingId.replace('#', '') || 1) : bookingId;
    try {
      const response = await api.post(`/queue/${cleanId}/request-cancel`);
      return response.data;
    } catch (err) {
      console.warn("Backend cancellation request fallback", err.message);
      return { status: 'CANCEL_REQUESTED', bookingId: cleanId };
    }
  },

  async getProcurementTimeline(bookingId) {
    return mockProcurementTimeline;
  }
};

// Payment Service
export const paymentService = {
  async getPaymentRecord(farmerId = 1) {
    try {
      const response = await api.get(`/payments/farmer/${farmerId}`);
      if (response.data && response.data.length > 0) {
        return response.data[0];
      }
      return mockPaymentRecord;
    } catch (err) {
      return mockPaymentRecord;
    }
  },

  async getFarmerPayments(farmerId = 1) {
    try {
      const response = await api.get(`/payments/farmer/${farmerId}`);
      return response.data;
    } catch (err) {
      const { mockPaymentsHistory } = await import('./mockData');
      return mockPaymentsHistory || [mockPaymentRecord];
    }
  }
};

// Notification Service
export const notificationService = {
  async getNotifications(farmerId) {
    try {
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
    } catch (err) {
      console.warn("Backend notifications API fallback to mock", err.message);
      return mockNotifications;
    }
  },

  async getUnreadCount(farmerId) {
    try {
      const response = await api.get('/notifications/unread-count', { params: farmerId ? { farmerId } : {} });
      return response.data.unreadCount;
    } catch (err) {
      return 1;
    }
  },

  async markAsRead(id, farmerId) {
    try {
      const response = await api.patch(`/notifications/${id}/read`, null, { params: farmerId ? { farmerId } : {} });
      return response.data;
    } catch (err) {
      return { success: true };
    }
  },

  async markAllAsRead(farmerId) {
    try {
      const response = await api.patch('/notifications/read-all', null, { params: farmerId ? { farmerId } : {} });
      return response.data;
    } catch (err) {
      return { success: true };
    }
  },

  async getPreferences(farmerId) {
    try {
      const response = await api.get('/notification-preferences', { params: farmerId ? { farmerId } : {} });
      return response.data;
    } catch (err) {
      return { farmerId: farmerId || 1, appEnabled: true, smsEnabled: true, voiceEnabled: true, language: 'Telugu' };
    }
  },

  async updatePreferences(data, farmerId) {
    try {
      const response = await api.patch('/notification-preferences', data, { params: farmerId ? { farmerId } : {} });
      return response.data;
    } catch (err) {
      return { ...data, farmerId: farmerId || 1 };
    }
  }
};

export default api;
