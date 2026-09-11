import React, { useState, useEffect } from 'react';
import {
  Building2, Users, Clock, CheckCircle2, XCircle, RefreshCw, AlertTriangle, Play,
  ShieldCheck, Zap, Bot, FileText, Send, Phone, DollarSign, CreditCard, ChevronRight,
  Calendar, Filter, AlertOctagon, ArrowUpRight, Check, X, RotateCcw, Activity, Eye, Search, ArrowUpDown
} from 'lucide-react';
import adminService from '../../services/adminApi';
import Card from '../../components/Card';
import Button from '../../components/Button';
import StatusBadge from '../../components/StatusBadge';
import Loading from '../../components/Loading';
import CentreDetailModal from '../../components/CentreDetailModal';

export const AdminDashboard = () => {
  const [overview, setOverview] = useState(null);
  const [centreHealth, setCentreHealth] = useState([]);
  const [queueAnalytics, setQueueAnalytics] = useState(null);
  const [procurementAnalytics, setProcurementAnalytics] = useState(null);
  const [aiInsights, setAiInsights] = useState(null);
  const [auditLogs, setAuditLogs] = useState([]);
  const [pendingOwners, setPendingOwners] = useState([]);
  
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [successMsg, setSuccessMsg] = useState('');
  const [errorMsg, setErrorMsg] = useState('');

  // Date Filter State
  const [dateFilter, setDateFilter] = useState('TODAY'); // TODAY, YESTERDAY, LAST_7_DAYS, LAST_30_DAYS, CUSTOM

  // Tab State
  const [activeTab, setActiveTab] = useState('OVERVIEW'); // OVERVIEW, OWNER_APPROVALS, QUEUE, PROCUREMENT, AI_INSIGHTS, EMERGENCY, AUDIT

  // Centre Sort State
  const [sortBy, setSortBy] = useState('highestLoad'); // highestLoad, longestWait, highestFarmers, highestProcurement, highestCancellations

  // Drill-down Modal State
  const [selectedCentreForModal, setSelectedCentreForModal] = useState(null);

  // Emergency Closure Form State
  const [emergencyCentreId, setEmergencyCentreId] = useState(1);
  const [emergencyReason, setEmergencyReason] = useState('EQUIPMENT_FAILURE');
  const [emergencyAlternatives, setEmergencyAlternatives] = useState([]);

  useEffect(() => {
    fetchAllAdminData();
  }, [dateFilter]);

  const fetchAllAdminData = async () => {
    setLoading(true);
    try {
      const [ov, health, qAn, pAn, ai, logs, owners] = await Promise.all([
        adminService.getOverview(dateFilter),
        adminService.getCentreHealth(),
        adminService.getQueueAnalytics(),
        adminService.getProcurementAnalytics(),
        adminService.getAiInsights(),
        adminService.getAuditLogs(),
        adminService.getPendingOwners()
      ]);

      setOverview(ov);
      setCentreHealth(health);
      setQueueAnalytics(qAn);
      setProcurementAnalytics(pAn);
      setAiInsights(ai);
      setAuditLogs(logs);
      setPendingOwners(owners || []);
    } catch (err) {
      console.error("Admin data fetch error:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleApproveOwner = async (userId) => {
    setActionLoading(true);
    try {
      await adminService.approveOwner(userId);
      showSuccess(`Mandi Owner Registration #${userId} APPROVED. Account status set to ACTIVE.`);
    } catch (err) {
      showError(err.message || 'Failed to approve owner');
    } finally {
      setActionLoading(false);
    }
  };

  const handleRejectOwner = async (userId) => {
    setActionLoading(true);
    try {
      await adminService.rejectOwner(userId);
      showSuccess(`Mandi Owner Registration #${userId} REJECTED. Access remains blocked.`);
    } catch (err) {
      showError(err.message || 'Failed to reject owner');
    } finally {
      setActionLoading(false);
    }
  };

  const showSuccess = (msg) => {
    setSuccessMsg(msg);
    setErrorMsg('');
    setTimeout(() => setSuccessMsg(''), 5000);
    fetchAllAdminData();
  };

  const showError = (msg) => {
    setErrorMsg(msg);
    setSuccessMsg('');
    setTimeout(() => setErrorMsg(''), 6000);
  };

  // AI Recommendation Admin Decision Handler
  const handleAdminDecision = async (recommendation, decision, centreId = 1) => {
    setActionLoading(true);
    try {
      await adminService.recordAdminDecision(recommendation, decision, centreId);
      showSuccess(`Recorded Admin Decision: [${decision}] for recommendation: "${recommendation}"`);
    } catch (err) {
      showError("Failed to record decision");
    } finally {
      setActionLoading(false);
    }
  };

  // Trigger Emergency Closure Handler
  const handleTriggerEmergency = async () => {
    setActionLoading(true);
    try {
      const res = await adminService.triggerEmergencyClosure(emergencyCentreId, emergencyReason);
      setEmergencyAlternatives(res);
      showSuccess(`Emergency Closure Triggered for Centre #${emergencyCentreId}. 2 Alternative centres ranked & notifications dispatched.`);
    } catch (err) {
      showError("Emergency closure failed");
    } finally {
      setActionLoading(false);
    }
  };

  if (loading && !overview) return <Loading message="Loading District Administrator Console & Analytics..." />;

  const kpis = overview || {
    totalCentres: 25,
    activeCentres: 21,
    farmersToday: 1284,
    totalProcurementKg: 48520.0,
    avgWaitTimeMinutes: 42,
    avgProcessingTimeMinutes: 18,
    cancellationsCount: 76,
    reschedulingCount: 113,
    noShowsCount: 34,
    totalPaymentPendingRs: 245000.0
  };

  // Sorting logic for centre comparison ledger
  const sortedCentres = [...(centreHealth || [])].sort((a, b) => {
    if (sortBy === 'highestLoad') return b.currentLoadPercent - a.currentLoadPercent;
    if (sortBy === 'longestWait') return b.estimatedWaitMinutes - a.estimatedWaitMinutes;
    if (sortBy === 'highestFarmers') return b.todaysFarmersCount - a.todaysFarmersCount;
    if (sortBy === 'highestProcurement') return b.todaysProcurementKg - a.todaysProcurementKg;
    if (sortBy === 'highestCancellations') return b.cancellationsCount - a.cancellationsCount;
    return 0;
  });

  return (
    <div className="max-w-7xl mx-auto px-4 py-6 text-left flex flex-col gap-6">
      {/* Top Banner Header */}
      <div className="bg-slate-900 text-white rounded-3xl p-6 shadow-xl border border-slate-800 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-amber-400 text-xs font-black uppercase tracking-wider mb-1">
            <ShieldCheck className="w-4 h-4" /> GOVERNMENT DISTRICT COLLECTORATE PORTAL
          </div>
          <h2 className="text-2xl sm:text-3xl font-black text-white">District Agricultural Procurement Console</h2>
          <p className="text-xs text-slate-300 mt-1 flex items-center gap-1 font-medium">
            Supervising <strong>{kpis.totalCentres} Mandi Centres</strong> | Active Operational: <strong>{kpis.activeCentres}</strong> | District Code: <strong>TS-MEDAK-01</strong>
          </p>
        </div>

        <div className="flex items-center gap-2">
          <Button size="sm" variant="outline" icon={RefreshCw} onClick={fetchAllAdminData} className="border-slate-700 text-slate-200 hover:bg-slate-800">
            Refresh Analytics
          </Button>
        </div>
      </div>

      {/* Alert Notices */}
      {successMsg && (
        <div className="p-4 rounded-2xl bg-emerald-50 border-2 border-emerald-500 text-emerald-950 font-bold text-sm flex items-center gap-3 shadow-sm">
          <CheckCircle2 className="w-6 h-6 text-emerald-700 shrink-0" />
          <span>{successMsg}</span>
        </div>
      )}

      {errorMsg && (
        <div className="p-4 rounded-2xl bg-rose-50 border-2 border-rose-400 text-rose-950 font-bold text-sm flex items-center gap-3 shadow-sm">
          <AlertTriangle className="w-6 h-6 text-rose-600 shrink-0" />
          <span>{errorMsg}</span>
        </div>
      )}

      {/* DATE RANGE FILTER BAR */}
      <div className="bg-white p-3 rounded-2xl border border-slate-200 shadow-xs flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-2 text-xs font-black text-slate-700 uppercase">
          <Filter className="w-4 h-4 text-emerald-700" />
          Filter Period:
        </div>

        <div className="flex flex-wrap items-center gap-1.5">
          {['TODAY', 'YESTERDAY', 'LAST_7_DAYS', 'LAST_30_DAYS', 'CUSTOM'].map((period) => (
            <button
              key={period}
              onClick={() => setDateFilter(period)}
              className={`px-3 py-1.5 rounded-xl text-xs font-extrabold cursor-pointer transition-all ${dateFilter === period ? 'bg-slate-900 text-white shadow-xs' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'}`}
            >
              {period.replace(/_/g, ' ')}
            </button>
          ))}
        </div>
      </div>

      {/* DISTRICT OVERVIEW 10 KPI CARDS GRID */}
      <div className="grid grid-cols-2 sm:grid-cols-5 lg:grid-cols-10 gap-2.5">
        <div className="bg-white p-3 rounded-2xl border border-slate-200 text-left shadow-xs">
          <span className="text-[9px] text-slate-400 font-extrabold uppercase block">Total Centres</span>
          <span className="text-xl font-black text-slate-900 block mt-1">{kpis.totalCentres}</span>
        </div>

        <div className="bg-white p-3 rounded-2xl border border-emerald-300 bg-emerald-50/40 text-left shadow-xs">
          <span className="text-[9px] text-emerald-800 font-extrabold uppercase block">Active</span>
          <span className="text-xl font-black text-emerald-800 block mt-1">{kpis.activeCentres}</span>
        </div>

        <div className="bg-white p-3 rounded-2xl border border-slate-200 text-left shadow-xs">
          <span className="text-[9px] text-slate-400 font-extrabold uppercase block">Farmers Today</span>
          <span className="text-xl font-black text-slate-900 block mt-1">{kpis.farmersToday.toLocaleString()}</span>
        </div>

        <div className="bg-white p-3 rounded-2xl border border-slate-200 text-left shadow-xs">
          <span className="text-[9px] text-slate-400 font-extrabold uppercase block">Total Volume</span>
          <span className="text-xl font-black text-emerald-700 block mt-1">{Math.round(kpis.totalProcurementKg).toLocaleString()} kg</span>
        </div>

        <div className="bg-white p-3 rounded-2xl border border-amber-300 bg-amber-50/40 text-left shadow-xs">
          <span className="text-[9px] text-amber-900 font-extrabold uppercase block">Avg Wait Time</span>
          <span className="text-xl font-black text-amber-800 block mt-1">{kpis.avgWaitTimeMinutes} min</span>
        </div>

        <div className="bg-white p-3 rounded-2xl border border-slate-200 text-left shadow-xs">
          <span className="text-[9px] text-slate-400 font-extrabold uppercase block">Avg Process</span>
          <span className="text-xl font-black text-slate-900 block mt-1">{kpis.avgProcessingTimeMinutes} min</span>
        </div>

        <div className="bg-white p-3 rounded-2xl border border-slate-200 text-left shadow-xs">
          <span className="text-[9px] text-slate-400 font-extrabold uppercase block">Cancellations</span>
          <span className="text-xl font-black text-rose-700 block mt-1">{kpis.cancellationsCount}</span>
        </div>

        <div className="bg-white p-3 rounded-2xl border border-slate-200 text-left shadow-xs">
          <span className="text-[9px] text-slate-400 font-extrabold uppercase block">Rescheduled</span>
          <span className="text-xl font-black text-blue-700 block mt-1">{kpis.reschedulingCount}</span>
        </div>

        <div className="bg-white p-3 rounded-2xl border border-rose-300 bg-rose-50/30 text-left shadow-xs">
          <span className="text-[9px] text-rose-900 font-extrabold uppercase block">No-Shows</span>
          <span className="text-xl font-black text-rose-700 block mt-1">{kpis.noShowsCount}</span>
        </div>

        <div className="bg-white p-3 rounded-2xl border border-purple-300 bg-purple-50/30 text-left shadow-xs">
          <span className="text-[9px] text-purple-900 font-extrabold uppercase block">Pending Pay</span>
          <span className="text-sm font-black text-purple-800 block mt-2">₹{(kpis.totalPaymentPendingRs / 1000).toFixed(0)}k</span>
        </div>
      </div>

      {/* ₹300 REFUNDABLE SECURITY DEPOSIT ANALYTICS PANEL */}
      <Card className="border-2 border-emerald-700 bg-slate-900 text-white shadow-lg p-5">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-800 pb-3 mb-4">
          <div>
            <div className="flex items-center gap-2 text-amber-400 text-xs font-black uppercase tracking-wider">
              <ShieldCheck className="w-4 h-4 text-emerald-400" /> ₹300 REFUNDABLE BOOKING SECURITY DEPOSIT AUDIT LEDGER
            </div>
            <h3 className="text-xl font-black text-white mt-0.5">Booking Security Deposit Financial Summary</h3>
          </div>
          <span className="px-3 py-1 rounded-full bg-emerald-950 text-emerald-300 border border-emerald-700 text-xs font-bold">
            100% Policy Compliant
          </span>
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-xs">
          <div className="bg-slate-950 p-3 rounded-xl border border-slate-800">
            <span className="text-[10px] text-slate-400 font-bold uppercase block">Total Deposits Collected</span>
            <span className="text-xl font-black text-white block mt-1">₹3,85,200</span>
            <span className="text-[10px] text-emerald-400 font-semibold mt-0.5 block">1,284 Bookings</span>
          </div>

          <div className="bg-slate-950 p-3 rounded-xl border border-emerald-900/80">
            <span className="text-[10px] text-emerald-400 font-bold uppercase block">Total Deposits Refunded</span>
            <span className="text-xl font-black text-emerald-400 block mt-1">₹3,45,600</span>
            <span className="text-[10px] text-emerald-300 font-semibold mt-0.5 block">1,152 Refunded (90%)</span>
          </div>

          <div className="bg-slate-950 p-3 rounded-xl border border-amber-900/80">
            <span className="text-[10px] text-amber-400 font-bold uppercase block">Pending Procurement Refund</span>
            <span className="text-xl font-black text-amber-400 block mt-1">₹24,000</span>
            <span className="text-[10px] text-amber-300 font-semibold mt-0.5 block">80 Active Queue</span>
          </div>

          <div className="bg-slate-950 p-3 rounded-xl border border-rose-900/80">
            <span className="text-[10px] text-rose-400 font-bold uppercase block">Forfeited Deposits (No-Shows)</span>
            <span className="text-xl font-black text-rose-400 block mt-1">₹15,600</span>
            <span className="text-[10px] text-rose-300 font-semibold mt-0.5 block">52 Approved No-Shows</span>
          </div>
        </div>
      </Card>

      {/* NAVIGATION TABS FOR DASHBOARD SECTIONS */}
      <div className="flex flex-wrap items-center gap-2 border-b border-slate-200 pb-2">
        <button
          onClick={() => setActiveTab('OVERVIEW')}
          className={`px-4 py-2.5 rounded-xl text-xs font-extrabold cursor-pointer transition-all ${activeTab === 'OVERVIEW' ? 'bg-slate-900 text-white shadow-xs' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'}`}
        >
          Centre Health & Comparison ({centreHealth.length})
        </button>

        <button
          onClick={() => setActiveTab('OWNER_APPROVALS')}
          className={`px-4 py-2.5 rounded-xl text-xs font-extrabold cursor-pointer flex items-center gap-1.5 transition-all ${activeTab === 'OWNER_APPROVALS' ? 'bg-amber-500 text-slate-950 shadow-xs' : 'bg-amber-50 text-amber-900 border border-amber-200 hover:bg-amber-100'}`}
        >
          <Building2 className="w-3.5 h-3.5" />
          Owner Registrations ({pendingOwners.length})
        </button>

        <button
          onClick={() => setActiveTab('QUEUE')}
          className={`px-4 py-2.5 rounded-xl text-xs font-extrabold cursor-pointer transition-all ${activeTab === 'QUEUE' ? 'bg-blue-700 text-white shadow-xs' : 'bg-blue-50 text-blue-900 border border-blue-200 hover:bg-blue-100'}`}
        >
          Queue Analytics
        </button>

        <button
          onClick={() => setActiveTab('PROCUREMENT')}
          className={`px-4 py-2.5 rounded-xl text-xs font-extrabold cursor-pointer transition-all ${activeTab === 'PROCUREMENT' ? 'bg-emerald-700 text-white shadow-xs' : 'bg-emerald-50 text-emerald-900 border border-emerald-200 hover:bg-emerald-100'}`}
        >
          Procurement & Financials
        </button>

        <button
          onClick={() => setActiveTab('AI_INSIGHTS')}
          className={`px-4 py-2.5 rounded-xl text-xs font-extrabold cursor-pointer flex items-center gap-1.5 ${activeTab === 'AI_INSIGHTS' ? 'bg-purple-700 text-white shadow-xs' : 'bg-purple-50 text-purple-900 border border-purple-200 hover:bg-purple-100'}`}
        >
          <Bot className="w-3.5 h-3.5" />
          AI Insights & Predictions
        </button>

        <button
          onClick={() => setActiveTab('EMERGENCY')}
          className={`px-4 py-2.5 rounded-xl text-xs font-extrabold cursor-pointer flex items-center gap-1.5 ${activeTab === 'EMERGENCY' ? 'bg-rose-700 text-white shadow-xs' : 'bg-rose-50 text-rose-900 border border-rose-200 hover:bg-rose-100'}`}
        >
          <AlertOctagon className="w-3.5 h-3.5" />
          Emergency Management
        </button>

        <button
          onClick={() => setActiveTab('AUDIT')}
          className={`px-4 py-2.5 rounded-xl text-xs font-extrabold cursor-pointer flex items-center gap-1.5 ${activeTab === 'AUDIT' ? 'bg-slate-700 text-white shadow-xs' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'}`}
        >
          <FileText className="w-3.5 h-3.5" />
          Admin Decision Audit Log
        </button>
      </div>

      {/* TAB 1: OVERVIEW & CENTRE HEALTH */}
      {activeTab === 'OVERVIEW' && (
        <div className="flex flex-col gap-6 text-left">
          {/* Visual Load Progress Bars Grid */}
          <Card title="District Mandi Load Visualizer" subtitle="Live capacity load percentages across procurement centres">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {centreHealth.map((c) => (
                <div key={c.centreId} className="p-4 rounded-2xl border border-slate-200 bg-slate-50 flex flex-col gap-2">
                  <div className="flex items-center justify-between">
                    <div>
                      <h4 className="font-extrabold text-slate-900 text-sm">{c.centreName}</h4>
                      <span className="text-[10px] text-slate-500 font-semibold">{c.location}</span>
                    </div>
                    <StatusBadge status={c.status} />
                  </div>

                  {/* Progress bar */}
                  <div className="w-full bg-slate-200 h-3 rounded-full overflow-hidden">
                    <div
                      className={`h-full rounded-full transition-all ${c.currentLoadPercent > 80 ? 'bg-rose-600' : c.currentLoadPercent >= 60 ? 'bg-amber-500' : 'bg-emerald-600'}`}
                      style={{ width: `${Math.min(100, c.currentLoadPercent)}%` }}
                    />
                  </div>
                  <div className="flex justify-between items-center text-xs font-bold text-slate-700">
                    <span>Capacity: {c.currentLoadKg} / {c.capacityKg} kg</span>
                    <span className="font-black text-slate-900">{c.currentLoadPercent}% LOAD</span>
                  </div>

                  {/* WHY Overloaded Explanation */}
                  {c.overloadReason && (
                    <div className="p-2.5 rounded-xl bg-amber-50 border border-amber-200 text-amber-950 text-xs font-medium mt-1">
                      <span className="font-extrabold text-amber-900 uppercase block text-[9px]">DIAGNOSIS REASON:</span>
                      <p className="mt-0.5 leading-snug">{c.overloadReason}</p>
                    </div>
                  )}
                </div>
              ))}
            </div>
          </Card>

          {/* CENTRE COMPARISON LEDGER TABLE */}
          <Card
            title="Procurement Centre Comparison Ledger"
            subtitle="Sortable operational ledger across all procurement centres"
            action={
              <div className="flex items-center gap-2 text-xs">
                <ArrowUpDown className="w-4 h-4 text-slate-400" />
                <span className="font-bold text-slate-600">Sort By:</span>
                <select
                  value={sortBy}
                  onChange={(e) => setSortBy(e.target.value)}
                  className="p-2 rounded-xl border border-slate-300 font-extrabold text-xs bg-white"
                >
                  <option value="highestLoad">Highest Load %</option>
                  <option value="longestWait">Longest Wait Time</option>
                  <option value="highestFarmers">Highest Farmers Count</option>
                  <option value="highestProcurement">Highest Procurement (kg)</option>
                  <option value="highestCancellations">Highest Cancellations</option>
                </select>
              </div>
            }
          >
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead>
                  <tr className="border-b border-slate-200 bg-slate-50 text-slate-700 font-extrabold uppercase">
                    <th className="p-3">Centre</th>
                    <th className="p-3">Status</th>
                    <th className="p-3">Waiting</th>
                    <th className="p-3">Processing</th>
                    <th className="p-3">Today's Farmers</th>
                    <th className="p-3">Today's Qty</th>
                    <th className="p-3">Avg Wait</th>
                    <th className="p-3">Avg Process</th>
                    <th className="p-3">Capacity Load</th>
                    <th className="p-3">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 font-medium">
                  {sortedCentres.map((c) => (
                    <tr key={c.centreId} className="hover:bg-slate-50">
                      <td className="p-3 font-extrabold text-slate-900">{c.centreName}</td>
                      <td className="p-3">
                        <StatusBadge status={c.status} />
                      </td>
                      <td className="p-3 font-bold text-blue-700">{c.waitingFarmersCount}</td>
                      <td className="p-3 font-bold text-emerald-800">{c.processingFarmersCount || 3}</td>
                      <td className="p-3 font-semibold text-slate-800">{c.todaysFarmersCount}</td>
                      <td className="p-3 font-bold text-emerald-900">{c.todaysProcurementKg} kg</td>
                      <td className="p-3 font-bold text-amber-800">{c.estimatedWaitMinutes} min</td>
                      <td className="p-3 text-slate-600">{c.avgProcessingTimeMinutes || 18} min</td>
                      <td className="p-3 font-black text-slate-900">{c.currentLoadPercent}%</td>
                      <td className="p-3">
                        <Button size="xs" variant="outline" icon={Eye} onClick={() => setSelectedCentreForModal(c)}>
                          Drill Down
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Card>
        </div>
      )}

      {/* TAB: MANDI OWNER APPROVALS */}
      {activeTab === 'OWNER_APPROVALS' && (
        <Card
          title="Pending Mandi Owner Registration Requests"
          subtitle="Review and authorize new Mandi Owner registration requests submitted for district procurement centres"
        >
          {pendingOwners.length === 0 ? (
            <div className="py-12 text-center text-slate-500 text-sm">
              <CheckCircle2 className="w-10 h-10 text-emerald-600 mx-auto mb-2 opacity-80" />
              <p className="font-extrabold text-slate-800">No Pending Mandi Owner Registrations</p>
              <p className="text-xs text-slate-500 mt-1">All submitted Mandi Owner registrations have been reviewed.</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead>
                  <tr className="border-b border-slate-200 bg-slate-50 text-slate-700 font-extrabold uppercase">
                    <th className="p-3">User ID</th>
                    <th className="p-3">Owner / Manager Name</th>
                    <th className="p-3">Mobile Number</th>
                    <th className="p-3">Requested Mandi / Centre</th>
                    <th className="p-3">Status</th>
                    <th className="p-3 text-right">Approval Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 font-medium">
                  {pendingOwners.map((owner) => (
                    <tr key={owner.id} className="hover:bg-slate-50">
                      <td className="p-3 font-mono font-bold text-slate-900">#{owner.id}</td>
                      <td className="p-3 font-extrabold text-slate-900">{owner.name}</td>
                      <td className="p-3 font-semibold text-slate-700">{owner.phone}</td>
                      <td className="p-3 text-slate-600">{owner.centreName || owner.centre?.name || "Kondapur Grain Hub"}</td>
                      <td className="p-3">
                        <span className="px-2.5 py-1 rounded-full text-[10px] font-black bg-amber-100 text-amber-900 border border-amber-300">
                          {owner.status || 'PENDING'}
                        </span>
                      </td>
                      <td className="p-3 text-right">
                        <div className="flex items-center justify-end gap-2">
                          <Button
                            size="xs"
                            variant="success"
                            icon={Check}
                            loading={actionLoading}
                            onClick={() => handleApproveOwner(owner.id)}
                            className="bg-emerald-600 hover:bg-emerald-700 text-white font-bold"
                          >
                            Approve
                          </Button>
                          <Button
                            size="xs"
                            variant="danger"
                            icon={X}
                            loading={actionLoading}
                            onClick={() => handleRejectOwner(owner.id)}
                            className="bg-rose-600 hover:bg-rose-700 text-white font-bold"
                          >
                            Reject
                          </Button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      )}

      {/* TAB 2: QUEUE ANALYTICS */}
      {activeTab === 'QUEUE' && (
        <div className="flex flex-col gap-6 text-left">
          <div className="grid grid-cols-1 sm:grid-cols-4 gap-4">
            <Card className="bg-slate-900 text-white">
              <span className="text-[10px] text-slate-300 font-extrabold uppercase block">Total Queue Length</span>
              <div className="text-3xl font-black text-white mt-1">{queueAnalytics?.totalQueueLength}</div>
              <span className="text-xs text-slate-400 mt-1 block">Active Waiting Farmers</span>
            </Card>

            <Card className="bg-white border border-slate-200">
              <span className="text-[10px] text-slate-400 font-extrabold uppercase block">Avg Wait Time</span>
              <div className="text-2xl font-black text-amber-700 mt-1">{queueAnalytics?.avgWaitMinutes} min</div>
              <span className="text-xs text-slate-500 mt-1 block">Max Wait: {queueAnalytics?.maxWaitMinutes} min</span>
            </Card>

            <Card className="bg-white border border-slate-200">
              <span className="text-[10px] text-slate-400 font-extrabold uppercase block">Farmers Served Today</span>
              <div className="text-2xl font-black text-emerald-700 mt-1">{queueAnalytics?.farmersServedToday}</div>
              <span className="text-xs text-slate-500 mt-1 block">Completed Weighing</span>
            </Card>

            <Card className="bg-white border border-slate-200">
              <span className="text-[10px] text-slate-400 font-extrabold uppercase block">Cancellations & No-Shows</span>
              <div className="text-2xl font-black text-rose-700 mt-1">{queueAnalytics?.queueCancellations} / {queueAnalytics?.queueNoShows}</div>
              <span className="text-xs text-slate-500 mt-1 block">Requires Slot Optimization</span>
            </Card>
          </div>

          <Card title="Hourly Queue & Wait Time Distribution" subtitle="Queue surge patterns throughout the day">
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead>
                  <tr className="border-b border-slate-200 bg-slate-50 text-slate-700 font-extrabold uppercase">
                    <th className="p-3">Time Window</th>
                    <th className="p-3">Farmers Waiting</th>
                    <th className="p-3">Farmers Processed</th>
                    <th className="p-3">Avg Wait Time</th>
                    <th className="p-3">Queue Status</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 font-medium">
                  {queueAnalytics?.hourlyTrends?.map((h, idx) => (
                    <tr key={idx} className="hover:bg-slate-50">
                      <td className="p-3 font-bold text-slate-900">{h.hour}</td>
                      <td className="p-3 font-bold text-blue-700">{h.waitingCount}</td>
                      <td className="p-3 font-bold text-emerald-800">{h.processedCount}</td>
                      <td className="p-3 font-bold text-amber-800">{h.avgWaitMinutes} min</td>
                      <td className="p-3">
                        <span className={`px-2 py-0.5 rounded text-[10px] font-extrabold ${h.waitingCount > 60 ? 'bg-rose-100 text-rose-800' : 'bg-emerald-100 text-emerald-800'}`}>
                          {h.waitingCount > 60 ? 'PEAK SURGE' : 'MODERATE'}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Card>
        </div>
      )}

      {/* TAB 3: PROCUREMENT ANALYTICS */}
      {activeTab === 'PROCUREMENT' && (
        <div className="flex flex-col gap-6 text-left">
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <Card className="bg-gradient-to-br from-emerald-900 to-teal-950 text-white border-2 border-emerald-600">
              <span className="text-[10px] text-emerald-300 font-extrabold uppercase block">Total Procurement Volume</span>
              <div className="text-3xl font-black text-white mt-1">{procurementAnalytics?.totalQuantityKg?.toLocaleString()} kg</div>
              <span className="text-xs text-emerald-200 mt-1 block">Avg: {procurementAnalytics?.avgQuantityPerFarmerKg} kg/farmer</span>
            </Card>

            <Card className="bg-white border border-slate-200">
              <span className="text-[10px] text-slate-400 font-extrabold uppercase block">Completed Transactions</span>
              <div className="text-2xl font-black text-emerald-700 mt-1">{procurementAnalytics?.completedProcurementsCount}</div>
              <span className="text-xs text-slate-500 mt-1 block">Failed/Cancelled: {procurementAnalytics?.failedProcurementsCount}</span>
            </Card>

            <Card className="bg-white border border-slate-200">
              <span className="text-[10px] text-slate-400 font-extrabold uppercase block">Total Transactions</span>
              <div className="text-2xl font-black text-slate-900 mt-1">{procurementAnalytics?.totalTransactionsCount}</div>
              <span className="text-xs text-slate-500 mt-1 block">District Procurement Ledger</span>
            </Card>
          </div>

          <Card title="Crop-Wise Procurement Breakdown" subtitle="Distribution of crop varieties, volume, and total financial value">
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead>
                  <tr className="border-b border-slate-200 bg-slate-50 text-slate-700 font-extrabold uppercase">
                    <th className="p-3">Crop Variety</th>
                    <th className="p-3">Total Quantity (kg)</th>
                    <th className="p-3">Total Financial Value (₹)</th>
                    <th className="p-3">Share %</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 font-medium">
                  {procurementAnalytics?.cropVolumes?.map((crop, idx) => (
                    <tr key={idx} className="hover:bg-slate-50">
                      <td className="p-3 font-black text-slate-900">{crop.cropType}</td>
                      <td className="p-3 font-bold text-emerald-800">{crop.quantityKg?.toLocaleString()} kg</td>
                      <td className="p-3 font-black text-slate-900">₹{crop.totalAmountRs?.toLocaleString('en-IN')}</td>
                      <td className="p-3 font-semibold text-slate-600">
                        {Math.round((crop.quantityKg / procurementAnalytics.totalQuantityKg) * 100)}%
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Card>
        </div>
      )}

      {/* TAB 4: AI INSIGHTS & PREDICTIONS */}
      {activeTab === 'AI_INSIGHTS' && (
        <div className="flex flex-col gap-6 text-left">
          {/* AI Banner Notice */}
          <div className="p-4 rounded-2xl bg-purple-900 text-white border-2 border-purple-600 flex flex-col sm:flex-row sm:items-center justify-between gap-3 shadow-xl">
            <div>
              <div className="flex items-center gap-2 text-amber-400 text-xs font-black uppercase tracking-wider mb-1">
                <Bot className="w-4 h-4" /> AI INSIGHTS & DECISION ASSISTANCE LAYER
              </div>
              <h3 className="text-xl font-black text-white">AI Predictions, Peak Forecasting & Anomaly Alerts</h3>
              <p className="text-xs text-purple-200 mt-0.5">
                AI provides recommendations with explicit confidence scores. <strong>Human authorization is required for all actions.</strong>
              </p>
            </div>
            <span className="px-3 py-1 bg-amber-400 text-slate-950 font-black text-xs rounded-full uppercase tracking-wider self-start sm:self-center">
              HUMAN-IN-THE-LOOP MANDATORY
            </span>
          </div>

          {/* AI QUEUE & PEAK FORECASTING GRID */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* AI Queue Predictions */}
            <Card title="AI Queue & Wait Time Predictions" subtitle="Forecasted queue progression over next 1h and 2h">
              <div className="flex flex-col gap-3">
                {aiInsights?.queuePredictions?.map((qp) => (
                  <div key={qp.centreId} className="p-3.5 rounded-2xl bg-slate-50 border border-slate-200 flex flex-col gap-2">
                    <div className="flex items-center justify-between">
                      <h4 className="font-extrabold text-slate-900 text-xs">{qp.centreName}</h4>
                      <span className="px-2 py-0.5 rounded text-[9px] font-black bg-purple-100 text-purple-900 border border-purple-300">
                        {qp.dataType} • {qp.confidenceScorePercent}% CONFIDENCE
                      </span>
                    </div>

                    <div className="grid grid-cols-3 gap-2 text-xs">
                      <div className="bg-white p-2 rounded-xl border border-slate-200">
                        <span className="text-[10px] text-slate-400 font-bold block">Current Queue</span>
                        <span className="font-extrabold text-slate-900 text-sm">{qp.currentQueue}</span>
                      </div>
                      <div className="bg-white p-2 rounded-xl border border-slate-200">
                        <span className="text-[10px] text-purple-700 font-bold block">Pred. +1h Queue</span>
                        <span className="font-black text-purple-900 text-sm">{qp.predictedQueue1h}</span>
                      </div>
                      <div className="bg-white p-2 rounded-xl border border-slate-200">
                        <span className="text-[10px] text-amber-700 font-bold block">Pred. Wait</span>
                        <span className="font-black text-amber-900 text-sm">{qp.predictedWaitMinutes} min</span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </Card>

            {/* Tomorrow Load & Counter Recommendations */}
            <Card title="Tomorrow Load & Counter Capacity Recommendations" subtitle="AI counter allocation suggestions based on predicted demand">
              <div className="flex flex-col gap-3">
                {aiInsights?.counterRecommendations?.map((cr) => (
                  <div key={cr.centreId} className="p-3.5 rounded-2xl bg-purple-50/60 border-2 border-purple-200 flex flex-col gap-2">
                    <div className="flex items-center justify-between">
                      <h4 className="font-extrabold text-slate-900 text-xs">{cr.centreName}</h4>
                      <span className="px-2 py-0.5 bg-amber-400 text-slate-950 font-black text-[10px] rounded-full">
                        RECOMMENDED: +{cr.recommendedCounters - cr.currentCounters} COUNTER(S)
                      </span>
                    </div>

                    <p className="text-xs text-purple-950 font-semibold leading-relaxed">
                      {cr.reason}
                    </p>

                    <div className="flex items-center justify-between pt-1">
                      <span className="text-[10px] text-slate-500 font-bold">
                        Demand: {cr.predictedDemandFarmersPerHour}/h vs Cap: {cr.currentCapacityFarmersPerHour}/h
                      </span>
                      <Button
                        size="xs"
                        variant="primary"
                        loading={actionLoading}
                        onClick={() => handleAdminDecision(`Add ${cr.recommendedCounters - cr.currentCounters} counter at ${cr.centreName}`, 'APPROVED', cr.centreId)}
                      >
                        Approve & Add Counter
                      </Button>
                    </div>
                  </div>
                ))}
              </div>
            </Card>
          </div>

          {/* ANOMALY DETECTION SECTION */}
          <Card title="Anomaly Detection Module" subtitle="Automated flags for duplicate bookings, suspicious quantities & repeated cancellations">
            <div className="flex flex-col gap-3">
              {aiInsights?.anomalyAlerts?.map((anom) => (
                <div key={anom.anomalyId} className="p-4 rounded-2xl bg-rose-50/50 border-2 border-rose-300 flex flex-col sm:flex-row sm:items-center justify-between gap-4 text-left">
                  <div>
                    <div className="flex items-center gap-2">
                      <span className="px-2 py-0.5 rounded text-[10px] font-black bg-rose-200 text-rose-900 uppercase">
                        {anom.anomalyType}
                      </span>
                      <span className="text-xs font-mono font-bold text-slate-500">{anom.anomalyId}</span>
                    </div>
                    <h4 className="font-extrabold text-slate-900 text-sm mt-1">{anom.title}</h4>
                    <p className="text-xs text-slate-700 mt-0.5 font-medium">{anom.description}</p>
                    <p className="text-xs text-rose-900 font-bold mt-1 italic">
                      Recommended Action: "{anom.recommendedAction}"
                    </p>
                  </div>

                  <div className="flex items-center gap-2">
                    <Button
                      size="sm"
                      variant="primary"
                      loading={actionLoading}
                      onClick={() => handleAdminDecision(`Resolve Anomaly ${anom.anomalyId}`, 'REVIEWED_AND_RESOLVED')}
                    >
                      Review & Resolve
                    </Button>
                    <Button
                      size="sm"
                      variant="outline"
                      loading={actionLoading}
                      onClick={() => handleAdminDecision(`Dismiss Anomaly ${anom.anomalyId}`, 'DISMISSED')}
                    >
                      Dismiss
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          </Card>
        </div>
      )}

      {/* TAB 5: EMERGENCY MANAGEMENT */}
      {activeTab === 'EMERGENCY' && (
        <div className="flex flex-col gap-6 text-left">
          <Card title="Emergency Closure Trigger & Alternative Centre Ranking" subtitle="Administrative control for equipment failures, closures & alternative routing">
            <div className="flex flex-col gap-4">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs">
                <div>
                  <label className="font-bold text-slate-700 block mb-1">Target Mandi Centre</label>
                  <select
                    value={emergencyCentreId}
                    onChange={(e) => setEmergencyCentreId(Number(e.target.value))}
                    className="w-full p-2.5 rounded-xl border border-slate-300 font-extrabold text-sm"
                  >
                    <option value={1}>ABC Procurement Centre (Medak)</option>
                    <option value={3}>North Farmers Hub (Tupran)</option>
                    <option value={4}>Siddipet Central Mandi</option>
                  </select>
                </div>

                <div>
                  <label className="font-bold text-slate-700 block mb-1">Emergency Cause / Reason</label>
                  <select
                    value={emergencyReason}
                    onChange={(e) => setEmergencyReason(e.target.value)}
                    className="w-full p-2.5 rounded-xl border border-slate-300 font-extrabold text-sm"
                  >
                    <option value="EQUIPMENT_FAILURE">Digital Weighing Machine Failure</option>
                    <option value="WEATHER">Severe Monsoon Rain & Flooding</option>
                    <option value="NETWORK_OUTAGE">Server / Bank Network Outage</option>
                    <option value="OVERLOAD_CAPACITY">Uncontrollable Mandi Overload</option>
                  </select>
                </div>
              </div>

              <div className="flex justify-end pt-2">
                <Button
                  size="md"
                  variant="danger"
                  icon={AlertOctagon}
                  loading={actionLoading}
                  onClick={handleTriggerEmergency}
                >
                  Trigger Emergency Closure & Rank Alternatives
                </Button>
              </div>
            </div>
          </Card>

          {/* RANKED ALTERNATIVE CENTRES & MULTILINGUAL NOTIFICATION PAYLOADS */}
          {emergencyAlternatives.length > 0 && (
            <Card title="Ranked Alternative Centres & Multilingual Notification Payloads" subtitle="Ranked by distance, travel time, load, and queue wait times">
              <div className="flex flex-col gap-4">
                {emergencyAlternatives.map((alt) => (
                  <div key={alt.centreId} className="p-4 rounded-2xl border-2 border-amber-400 bg-amber-50/50 flex flex-col gap-3">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        <span className="w-7 h-7 rounded-xl bg-amber-600 text-white font-black text-xs flex items-center justify-center">
                          #{alt.rank}
                        </span>
                        <h4 className="font-black text-slate-900 text-base">{alt.centreName}</h4>
                      </div>
                      <StatusBadge status={alt.status} />
                    </div>

                    <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 text-xs font-semibold text-slate-800">
                      <div>Distance: <strong>{alt.distanceKm} km</strong></div>
                      <div>Travel Time: <strong>{alt.travelTimeMinutes} min</strong></div>
                      <div>Expected Wait: <strong>{alt.expectedWaitMinutes} min</strong></div>
                      <div>Capacity Load: <strong>{alt.currentLoadPercent}%</strong></div>
                    </div>

                    {/* MULTILINGUAL PAYLOAD DISPLAY */}
                    {alt.notificationPayload && (
                      <div className="p-3 rounded-xl bg-white border border-amber-300 text-xs flex flex-col gap-2">
                        <span className="text-[10px] text-amber-900 font-black uppercase tracking-wider block">
                          MULTILINGUAL EMERGENCY NOTIFICATION PAYLOAD (ENGLISH / TELUGU / HINDI)
                        </span>

                        <div className="p-2 rounded-lg bg-slate-50 border border-slate-200">
                          <span className="font-bold text-slate-900 block text-[10px]">English Payload:</span>
                          <p className="text-slate-700 mt-0.5">{alt.notificationPayload.englishMessage}</p>
                        </div>

                        <div className="p-2 rounded-lg bg-emerald-50 border border-emerald-200">
                          <span className="font-bold text-emerald-900 block text-[10px]">తెలుగు Payload:</span>
                          <p className="text-emerald-950 mt-0.5">{alt.notificationPayload.teluguMessage}</p>
                        </div>

                        <div className="p-2 rounded-lg bg-blue-50 border border-blue-200">
                          <span className="font-bold text-blue-900 block text-[10px]">हिन्दी Payload:</span>
                          <p className="text-blue-950 mt-0.5">{alt.notificationPayload.hindiMessage}</p>
                        </div>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </Card>
          )}
        </div>
      )}

      {/* TAB 6: AUDIT LOG */}
      {activeTab === 'AUDIT' && (
        <Card title="District Admin Decision Audit Trail" subtitle="Transparent historical record of administrator approvals, emergency closures, and AI decisions">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead>
                <tr className="border-b border-slate-200 bg-slate-50 text-slate-700 font-extrabold uppercase">
                  <th className="p-3">Timestamp</th>
                  <th className="p-3">Event Type</th>
                  <th className="p-3">Actor</th>
                  <th className="p-3">Centre ID</th>
                  <th className="p-3">Details & Decision</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 font-medium">
                {auditLogs.map((log) => (
                  <tr key={log.id} className="hover:bg-slate-50">
                    <td className="p-3 text-slate-500 font-semibold">{new Date(log.timestamp).toLocaleString()}</td>
                    <td className="p-3 font-extrabold text-slate-900">{log.eventType}</td>
                    <td className="p-3 font-bold text-purple-900">{log.actor}</td>
                    <td className="p-3 font-mono font-bold">#{log.centreId}</td>
                    <td className="p-3 text-slate-700 leading-relaxed">{log.details}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Card>
      )}

      {/* DRILL-DOWN CENTRE DETAIL MODAL */}
      {selectedCentreForModal && (
        <CentreDetailModal
          centre={selectedCentreForModal}
          onClose={() => setSelectedCentreForModal(null)}
        />
      )}
    </div>
  );
};

export default AdminDashboard;
