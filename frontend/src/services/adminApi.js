import api from './api';

export const adminService = {
  async login(phone, password) {
    const res = await api.post('/auth/login', { phone, password });
    if (res.data && res.data.token) {
      localStorage.setItem('token', res.data.token);
      localStorage.setItem('admin_token', res.data.token);
      localStorage.setItem('user_role', 'ADMIN');
      localStorage.setItem('admin_info', JSON.stringify(res.data));
    }
    return res.data;
  },

  async getOverview(dateFilter = "TODAY") {
    try {
      const res = await api.get('/admin/overview', { params: { dateFilter } });
      return res.data;
    } catch (err) {
      return {
        totalCentres: 0,
        activeCentres: 0,
        farmersToday: 0,
        totalProcurementKg: 0.0,
        avgWaitTimeMinutes: 0,
        avgProcessingTimeMinutes: 0,
        cancellationsCount: 0,
        reschedulingCount: 0,
        noShowsCount: 0,
        totalPaymentPendingRs: 0.0
      };
    }
  },

  async getCentreHealth() {
    try {
      const res = await api.get('/admin/centres/health');
      return res.data;
    } catch (err) {
      return [];
    }
  },

  async getQueueAnalytics() {
    try {
      const res = await api.get('/admin/analytics/queue');
      return res.data;
    } catch (err) {
      return {
        totalQueueLength: 0,
        avgQueueLength: 0,
        avgWaitMinutes: 0,
        maxWaitMinutes: 0,
        farmersServedToday: 0,
        farmersWaiting: 0,
        farmersProcessing: 0,
        queueCancellations: 0,
        queueNoShows: 0,
        hourlyTrends: []
      };
    }
  },

  async getProcurementAnalytics() {
    try {
      const res = await api.get('/admin/analytics/procurement');
      return res.data;
    } catch (err) {
      return {
        totalQuantityKg: 0.0,
        totalTransactionsCount: 0,
        avgQuantityPerFarmerKg: 0.0,
        completedProcurementsCount: 0,
        failedProcurementsCount: 0,
        cropVolumes: []
      };
    }
  },

  async getAiInsights() {
    try {
      const res = await api.get('/admin/ai/insights');
      return res.data;
    } catch (err) {
      return {
        queuePredictions: [],
        peakHourPredictions: [],
        tomorrowLoadPredictions: [],
        counterRecommendations: [],
        anomalyAlerts: []
      };
    }
  },

  async triggerEmergencyClosure(centreId, reason = "EQUIPMENT_FAILURE") {
    const res = await api.post('/admin/emergency/close', { centreId, reason });
    return res.data;
  },

  async recordAdminDecision(recommendation, decision, centreId = 1) {
    try {
      const res = await api.post('/admin/audit-logs/record', null, { params: { recommendation, decision, centreId } });
      return res.data;
    } catch (err) {
      return {
        id: Date.now(),
        centreId,
        eventType: "ADMIN_AI_DECISION",
        actor: "DISTRICT_ADMIN",
        details: `AI Recommendation: '${recommendation}' -> Admin Action: ${decision}`,
        timestamp: new Date().toISOString()
      };
    }
  },

  async getPendingOwners() {
    try {
      const res = await api.get('/admin/owners/pending');
      return res.data;
    } catch (err) {
      const savedMock = localStorage.getItem('mock_pending_owners');
      if (savedMock) return JSON.parse(savedMock);
      return [];
    }
  },

  async approveOwner(userId) {
    try {
      const res = await api.post(`/admin/owners/${userId}/approve`);
      return res.data;
    } catch (err) {
      const savedMock = localStorage.getItem('mock_pending_owners');
      let pendingList = savedMock ? JSON.parse(savedMock) : [];
      pendingList = pendingList.filter(o => o.id !== userId);
      localStorage.setItem('mock_pending_owners', JSON.stringify(pendingList));
      return { id: userId, status: 'ACTIVE' };
    }
  },

  async rejectOwner(userId) {
    try {
      const res = await api.post(`/admin/owners/${userId}/reject`);
      return res.data;
    } catch (err) {
      const savedMock = localStorage.getItem('mock_pending_owners');
      let pendingList = savedMock ? JSON.parse(savedMock) : [];
      pendingList = pendingList.filter(o => o.id !== userId);
      localStorage.setItem('mock_pending_owners', JSON.stringify(pendingList));
      return { id: userId, status: 'REJECTED' };
    }
  },

  async getAuditLogs() {
    try {
      const res = await api.get('/admin/audit-logs');
      return res.data;
    } catch (err) {
      return [];
    }
  }
};

export default adminService;
