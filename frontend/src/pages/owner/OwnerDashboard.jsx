import React, { useState, useEffect } from 'react';
import {
  Building2, Users, Clock, CheckCircle2, XCircle, RefreshCw, AlertTriangle, Play,
  SkipForward, UserCheck, CheckSquare, XSquare, Calendar, Scale, ShieldCheck, Zap,
  Bot, PhoneCall, Check, X, AlertCircle, FileText, Send, Phone, DollarSign, CreditCard,
  ArrowRight, ChevronRight, CheckCircle, AlertOctagon, RotateCcw
} from 'lucide-react';
import ownerService from '../../services/ownerApi';
import Card from '../../components/Card';
import Button from '../../components/Button';
import StatusBadge from '../../components/StatusBadge';
import Loading from '../../components/Loading';
import { subscribeToQueueUpdates } from '../../services/socket';

export const OwnerDashboard = () => {
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [successMsg, setSuccessMsg] = useState('');
  const [errorMsg, setErrorMsg] = useState('');
  const [activeTab, setActiveTab] = useState('PROCUREMENT'); // PROCUREMENT, QUEUE, APPROVALS, ACTION_REQUIRED, SIMULATOR, AUDIT

  // Phase 11 States
  const [pendingApprovals, setPendingApprovals] = useState([]);
  const [actionAlerts, setActionAlerts] = useState([]);
  const [auditLogs, setAuditLogs] = useState([]);

  // Procurement & Payment Workflow States
  const [ownerOverview, setOwnerOverview] = useState({
    todaysFarmersCount: 0,
    completedProcurementsCount: 0,
    pendingProcurementsCount: 0,
    paymentsCompletedCount: 0,
    paymentsPendingCount: 0,
    paymentFailuresCount: 0,
    totalDisbursedAmountRs: 0.0,
    transactions: []
  });

  const [procBookingId, setProcBookingId] = useState(101);
  const [procStep, setProcStep] = useState(1); // 1: Verify, 2: Weighing, 3: Quality, 4: Summary, 5: Confirm, 6: Payment
  const [grossWeight, setGrossWeight] = useState(520);
  const [tareWeight, setTareWeight] = useState(20);
  const [qualityGrade, setQualityGrade] = useState('Grade A');
  const [qualityStatus, setQualityStatus] = useState('ACCEPTED');
  const [moisturePct, setMoisturePct] = useState(12.5);
  const [operatorName, setOperatorName] = useState('Operator Ravi');

  const [activeProcurement, setActiveProcurement] = useState(null);
  const [activePayment, setActivePayment] = useState(null);

  // Simulator State
  const [simBookingId, setSimBookingId] = useState('');
  const [simReason, setSimReason] = useState('Cannot attend due to transport delay');
  const [simDate, setSimDate] = useState('2026-09-21');
  const [simSlot, setSimSlot] = useState('14:00 PM – 15:00 PM');
  const [simResultMsg, setSimResultMsg] = useState('');

  // Skip Modal State
  const [skipModalBookingId, setSkipModalBookingId] = useState(null);
  const [skipReason, setSkipReason] = useState('');

  // Assign Slot Modal State
  const [assignModalBookingId, setAssignModalBookingId] = useState(null);
  const [newDate, setNewDate] = useState('2026-09-20');
  const [newSlot, setNewSlot] = useState('10:00 AM – 11:00 AM');

  // QR Code Verification State
  const [qrInput, setQrInput] = useState('');
  const [scannedPayload, setScannedPayload] = useState(null);

  const handleScanQrCode = (rawText) => {
    try {
      const parsed = JSON.parse(rawText);
      if (parsed && parsed.bookingId) {
        setScannedPayload(parsed);
        setProcBookingId(parsed.bookingId);
        showSuccess(`Valid Farmer QR Code Scanned! Token #${parsed.bookingId} (${parsed.farmer?.name || 'Farmer'}) Verified.`);
      } else {
        showError("Invalid QR Code payload. Expected official Mandi verification JSON.");
      }
    } catch (e) {
      showError("Invalid QR payload format. Must be official JSON string.");
    }
  };

  useEffect(() => {
    fetchDashboard();
    fetchPhase11Data();
    fetchPhase14Data();

    const unsubscribe = subscribeToQueueUpdates(1, () => {
      fetchDashboard();
      fetchPhase11Data();
      fetchPhase14Data();
    });

    const interval = setInterval(() => {
      fetchDashboard();
      fetchPhase11Data();
      fetchPhase14Data();
    }, 10000);

    return () => {
      unsubscribe();
      clearInterval(interval);
    };
  }, []);

  const fetchDashboard = async () => {
    try {
      const data = await ownerService.getDashboard(localStorage.getItem('owner_phone') || '9876543210');
      if (data) {
        setDashboard(data);
        if (!simBookingId && data.queueList && data.queueList.length > 0) {
          setSimBookingId(data.queueList[0].bookingId);
        }
      }
    } catch (err) {
      console.error("Dashboard fetch error:", err);
    } finally {
      setLoading(false);
    }
  };

  const fetchPhase11Data = async () => {
    try {
      const phone = localStorage.getItem('owner_phone') || '9876543210';
      const [approvals, alerts, logs] = await Promise.all([
        ownerService.getPendingApprovalRequests(phone),
        ownerService.getActionRequiredAlerts(phone),
        ownerService.getAuditLogs(1)
      ]);
      setPendingApprovals(approvals);
      setActionAlerts(alerts);
      setAuditLogs(logs);
    } catch (err) {
      console.error("Phase 11 data fetch error:", err);
    }
  };

  const fetchPhase14Data = async () => {
    try {
      const overview = await ownerService.getOwnerPaymentOverview(1);
      if (overview) {
        setOwnerOverview(overview);
      }
    } catch (err) {
      console.error("Phase 14 data fetch error:", err);
    }
  };

  const showSuccess = (msg) => {
    setSuccessMsg(msg);
    setErrorMsg('');
    setTimeout(() => setSuccessMsg(''), 5000);
    fetchDashboard();
    fetchPhase11Data();
    fetchPhase14Data();
  };

  const showError = (msg) => {
    setErrorMsg(msg);
    setSuccessMsg('');
    setTimeout(() => setErrorMsg(''), 6000);
  };

  // Phase 14 Procurement Stepper & Gateway Simulation Handlers
  const handleVerifyProcurement = async () => {
    setActionLoading(true);
    try {
      const res = await ownerService.verifyFarmer(procBookingId, operatorName);
      setActiveProcurement(res);
      setProcStep(2);
      showSuccess(`Farmer Identity Verified for Booking #${procBookingId}`);
    } catch (err) {
      showError(err.message || 'Verification failed');
    } finally {
      setActionLoading(false);
    }
  };

  const handleRecordWeighing = async () => {
    setActionLoading(true);
    try {
      const data = { grossWeight: Number(grossWeight), tareWeight: Number(tareWeight) };
      const res = await ownerService.recordWeighing(procBookingId, data);
      setActiveProcurement(res);
      setProcStep(3);
      showSuccess(`Weighing recorded: Gross ${grossWeight}kg, Tare ${tareWeight}kg, Net ${grossWeight - tareWeight}kg`);
    } catch (err) {
      showError(err.message || 'Weighing failed');
    } finally {
      setActionLoading(false);
    }
  };

  const handleRecordQuality = async () => {
    setActionLoading(true);
    try {
      const data = {
        qualityGrade,
        qualityStatus,
        moisturePercentage: Number(moisturePct),
        foreignMatterPercentage: 1.0
      };
      const res = await ownerService.recordQualityCheck(procBookingId, data);
      setActiveProcurement(res);
      setProcStep(4);
      showSuccess(`Quality check recorded: ${qualityGrade} (${qualityStatus})`);
    } catch (err) {
      showError(err.message || 'Quality check failed');
    } finally {
      setActionLoading(false);
    }
  };

  const handleConfirmProcurementCode = async () => {
    setActionLoading(true);
    try {
      const res = await ownerService.confirmProcurement(procBookingId, operatorName);
      setActiveProcurement(res);
      setProcStep(6);
      showSuccess(`Procurement Confirmed! Code: ${res.procurementCode || 'PR-' + (10000 + Number(procBookingId))}`);
      fetchPhase14Data();
    } catch (err) {
      showError(err.message || 'Procurement confirmation failed');
    } finally {
      setActionLoading(false);
    }
  };

  const handleInitiatePayment = async () => {
    const pId = activeProcurement?.procurementId || Date.now();
    setActionLoading(true);
    try {
      const res = await ownerService.initiatePayment(pId, "DIRECT_BENEFIT_TRANSFER");
      setActivePayment(res);
      showSuccess(`DBT Payment Initiated: ${res.paymentCode || 'PAY-' + (res.paymentId || 98421)} (₹${res.amount || activeProcurement?.totalAmount || 12500})`);
      fetchPhase14Data();
    } catch (err) {
      showError(err.message || 'Payment initiation failed');
    } finally {
      setActionLoading(false);
    }
  };

  const handleSimulatePaymentSuccess = async (pId) => {
    const payId = pId || activePayment?.paymentId || 1;
    setActionLoading(true);
    try {
      const res = await ownerService.simulatePaymentSuccess(payId);
      setActivePayment(res);
      showSuccess(`Payment SUCCESS! Transaction ID: ${res.transactionId || 'TXN-8F92A71'}. Farmer notified via App/SMS.`);
      fetchPhase14Data();
    } catch (err) {
      showError(err.message || 'Simulation failed');
    } finally {
      setActionLoading(false);
    }
  };

  const handleSimulatePaymentFailure = async (pId) => {
    const payId = pId || activePayment?.paymentId || 1;
    setActionLoading(true);
    try {
      const res = await ownerService.simulatePaymentFailure(payId, "Bank network timeout during DBT processing.");
      setActivePayment(res);
      showError(`Payment FAILED: ${res.failureReason || 'Bank network timeout'}. Retry available.`);
      fetchPhase14Data();
    } catch (err) {
      showError(err.message || 'Simulation failed');
    } finally {
      setActionLoading(false);
    }
  };

  const handleRetryPayment = async (pId) => {
    const payId = pId || activePayment?.paymentId || 1;
    setActionLoading(true);
    try {
      const res = await ownerService.retryPayment(payId);
      setActivePayment(res);
      showSuccess(`Payment Retried. Status reset to PENDING.`);
      fetchPhase14Data();
    } catch (err) {
      showError(err.message || 'Retry failed');
    } finally {
      setActionLoading(false);
    }
  };

  // Queue Action Handlers
  const handleCallNext = async () => {
    setActionLoading(true);
    try {
      const res = await ownerService.callNextFarmer();
      showSuccess(`Called Next Farmer: Token #${res.bookingId} (${res.farmerName})`);
    } catch (err) {
      showError(err.response?.data?.message || err.message || 'No waiting farmers in queue');
    } finally {
      setActionLoading(false);
    }
  };

  const handleMarkArrived = async (bookingId) => {
    setActionLoading(true);
    try {
      const res = await ownerService.markArrived(bookingId);
      showSuccess(`Farmer Marked ARRIVED for Token #${res.bookingId} (${res.farmerName})`);
    } catch (err) {
      showError(err.response?.data?.message || err.message);
    } finally {
      setActionLoading(false);
    }
  };

  const handleStartProcurement = async (bookingId) => {
    setActionLoading(true);
    try {
      const res = await ownerService.startProcurement(bookingId);
      showSuccess(`Started Procurement for Token #${res.bookingId} (${res.farmerName})`);
    } catch (err) {
      showError(err.response?.data?.message || err.message);
    } finally {
      setActionLoading(false);
    }
  };

  const handleCompleteProcurement = async (bookingId) => {
    setActionLoading(true);
    try {
      const res = await ownerService.completeProcurement(bookingId);
      showSuccess(`Procurement COMPLETED for Token #${res.bookingId} (${res.farmerName})`);
    } catch (err) {
      showError(err.response?.data?.message || err.message);
    } finally {
      setActionLoading(false);
    }
  };

  const handleConfirmSkip = async () => {
    if (!skipModalBookingId) return;
    setActionLoading(true);
    try {
      await ownerService.skipFarmer(skipModalBookingId, skipReason);
      showSuccess(`Skipped Farmer for Booking #${skipModalBookingId}`);
      setSkipModalBookingId(null);
      setSkipReason('');
    } catch (err) {
      showError(err.response?.data?.message || err.message);
    } finally {
      setActionLoading(false);
    }
  };

  // Phase 11 Approval Handlers
  const handleApproveApprovalRequest = async (requestId) => {
    setActionLoading(true);
    try {
      await ownerService.approveApprovalRequest(requestId);
      showSuccess(`Approval Request #${requestId} APPROVED successfully`);
    } catch (err) {
      showError(err.response?.data?.message || err.message);
    } finally {
      setActionLoading(false);
    }
  };

  const handleRejectApprovalRequest = async (requestId) => {
    setActionLoading(true);
    try {
      await ownerService.rejectApprovalRequest(requestId);
      showSuccess(`Approval Request #${requestId} REJECTED. Booking remains unchanged.`);
    } catch (err) {
      showError(err.response?.data?.message || err.message);
    } finally {
      setActionLoading(false);
    }
  };

  // Action Required Resolution Handler
  const handleResolveAction = async (bookingId, action, body = {}) => {
    setActionLoading(true);
    try {
      await ownerService.resolveActionRequired(bookingId, action, body);
      showSuccess(`Action Required resolved with action: ${action}`);
    } catch (err) {
      showError(err.response?.data?.message || err.message);
    } finally {
      setActionLoading(false);
    }
  };

  // AI Voice Agent Simulator Handler
  const handleSimulateVoiceCall = async (eventType) => {
    if (!simBookingId) {
      showError("Please enter or select a valid Booking ID");
      return;
    }
    setActionLoading(true);
    setSimResultMsg('');
    try {
      const payload = {
        bookingId: Number(simBookingId),
        event: eventType,
        reason: simReason,
        requestedDate: simDate,
        requestedTime: simSlot,
        source: 'SIMULATOR'
      };
      const res = await ownerService.simulateVoiceCall(payload);
      setSimResultMsg(`AI Call Simulated: ${res.message} (Journey Status: ${res.journeyStatus})`);
      showSuccess(`Simulated AI Voice Call Event: ${eventType}`);
    } catch (err) {
      showError(err.response?.data?.message || err.message);
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) return <Loading message="Loading procurement centre operator dashboard..." />;

  const stats = dashboard || {
    centreName: "ABC Procurement Centre",
    centreLocation: "Kondapur Main Road, Medak",
    totalCapacity: 1000,
    currentLoad: 300,
    availableCapacity: 700,
    status: "ACTIVE",
    isOverloaded: false,
    overloadReason: "Operating normally",
    todaysFarmersCount: 5,
    waitingCount: 3,
    calledCount: 1,
    arrivedCount: 1,
    processingCount: 1,
    completedCount: 2,
    cancelledCount: 0,
    reschedulingCount: 1,
    pendingApprovalsCount: pendingApprovals.length
  };

  const currentProcessing = dashboard?.currentProcessing;

  return (
    <div className="max-w-6xl mx-auto px-4 py-6 text-left flex flex-col gap-6">
      {/* Top Banner Header */}
      <div className="bg-slate-900 text-white rounded-3xl p-6 shadow-xl border border-slate-800 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-amber-400 text-xs font-extrabold uppercase tracking-wider mb-1">
            <Building2 className="w-4 h-4" /> PROCUREMENT CENTRE OPERATOR CONSOLE
          </div>
          <h2 className="text-2xl sm:text-3xl font-black text-white">{stats.centreName}</h2>
          <p className="text-xs text-slate-300 mt-1 flex items-center gap-1 font-medium">
            Location: {stats.centreLocation} • Capacity: <strong>{stats.totalCapacity} kg</strong> | Load: <strong>{stats.currentLoad} kg ({Math.round((stats.currentLoad / stats.totalCapacity) * 100)}%)</strong>
          </p>
        </div>

        <div className="flex items-center gap-2">
          <Button size="sm" variant="outline" icon={RefreshCw} onClick={() => { fetchDashboard(); fetchPhase11Data(); }} className="border-slate-700 text-slate-200 hover:bg-slate-800">
            Live Refresh
          </Button>
        </div>
      </div>

      {/* Alert Notices */}
      {successMsg && (
        <div className="p-4 rounded-2xl bg-emerald-50 border-2 border-emerald-500 text-emerald-950 font-bold text-sm flex items-center gap-3 shadow-sm animate-pulse-subtle">
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

      {/* OVERLOAD WARNING WIDGET */}
      {stats.isOverloaded && (
        <Card className="border-2 border-rose-500 bg-rose-50/50">
          <div className="flex items-start gap-3">
            <AlertTriangle className="w-6 h-6 text-rose-600 shrink-0 mt-0.5" />
            <div>
              <h4 className="font-extrabold text-rose-950 text-sm uppercase">CENTRE OVERLOAD WARNING</h4>
              <p className="text-xs text-rose-900 font-medium mt-0.5">
                {stats.overloadReason || "Centre capacity exceeded. Waiting list active."}
              </p>
            </div>
          </div>
        </Card>
      )}

      {/* TOP STATISTICS COUNTERS GRID */}
      <div className="grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-7 gap-3">
        <div className="bg-white p-3.5 rounded-2xl border border-slate-200 shadow-xs">
          <span className="text-[10px] text-slate-400 font-extrabold uppercase block">Today's Total</span>
          <span className="text-2xl font-black text-slate-900 block mt-1">{stats.todaysFarmersCount}</span>
        </div>

        <div className="bg-white p-3.5 rounded-2xl border border-slate-200 shadow-xs">
          <span className="text-[10px] text-slate-400 font-extrabold uppercase block">Waiting</span>
          <span className="text-2xl font-black text-blue-700 block mt-1">{stats.waitingCount}</span>
        </div>

        <div className="bg-white p-3.5 rounded-2xl border border-slate-200 shadow-xs">
          <span className="text-[10px] text-slate-400 font-extrabold uppercase block">Called / Arrived</span>
          <span className="text-2xl font-black text-amber-700 block mt-1">{stats.calledCount + stats.arrivedCount}</span>
        </div>

        <div className="bg-white p-3.5 rounded-2xl border border-emerald-300 bg-emerald-50/40 shadow-xs">
          <span className="text-[10px] text-emerald-800 font-extrabold uppercase block">Processing</span>
          <span className="text-2xl font-black text-emerald-800 block mt-1">{stats.processingCount}</span>
        </div>

        <div className="bg-white p-3.5 rounded-2xl border border-slate-200 shadow-xs">
          <span className="text-[10px] text-slate-400 font-extrabold uppercase block">Completed</span>
          <span className="text-2xl font-black text-green-700 block mt-1">{stats.completedCount}</span>
        </div>

        <div className="bg-white p-3.5 rounded-2xl border border-amber-300 bg-amber-50/40 shadow-xs">
          <span className="text-[10px] text-amber-900 font-extrabold uppercase block">Pending Approvals</span>
          <span className="text-2xl font-black text-amber-800 block mt-1">{pendingApprovals.length}</span>
        </div>

        <div className="bg-white p-3.5 rounded-2xl border border-rose-300 bg-rose-50/40 shadow-xs">
          <span className="text-[10px] text-rose-900 font-extrabold uppercase block">Action Required</span>
          <span className="text-2xl font-black text-rose-700 block mt-1">{actionAlerts.length}</span>
        </div>
      </div>

      {/* CURRENT PROCESSING SECTION */}
      <Card className="border-4 border-emerald-600 bg-gradient-to-r from-emerald-900 to-teal-950 text-white shadow-xl">
        <div className="flex items-center justify-between border-b border-emerald-800 pb-3 mb-4">
          <div className="flex items-center gap-2">
            <Scale className="w-6 h-6 text-amber-400 animate-pulse" />
            <h3 className="font-black text-lg text-white">CURRENTLY PROCESSING AT SCALE</h3>
          </div>
          <span className="px-3 py-1 rounded-full bg-amber-400 text-slate-950 font-black text-xs uppercase tracking-wider">
            LIVE WEIGHING & QUALITY CHECK
          </span>
        </div>

        {currentProcessing ? (
          <div className="flex flex-col md:flex-row items-stretch justify-between gap-6 p-2">
            <div>
              <span className="text-xs text-emerald-300 font-bold uppercase">TOKEN #{currentProcessing.bookingId}</span>
              <h4 className="text-2xl font-black text-white mt-0.5">{currentProcessing.farmerName}</h4>
              <p className="text-xs text-emerald-200 mt-1">
                Crop: <strong>{currentProcessing.cropType}</strong> • Quantity: <strong>{currentProcessing.quantity} kg</strong>
              </p>
            </div>

            <div className="flex items-center">
              <Button
                size="lg"
                variant="success"
                icon={CheckCircle2}
                loading={actionLoading}
                onClick={() => handleCompleteProcurement(currentProcessing.bookingId)}
                className="bg-gradient-to-r from-emerald-500 to-green-600 hover:from-emerald-600 hover:to-green-700 text-white shadow-lg text-base py-4 px-8 font-black cursor-pointer"
              >
                Complete Procurement
              </Button>
            </div>
          </div>
        ) : (
          <div className="py-6 text-center text-slate-300">
            <p className="font-semibold text-sm">No farmer is currently at the processing scale.</p>
            <p className="text-xs text-emerald-300 mt-1">Click <strong>"CALL NEXT FARMER"</strong> in the queue section below to start processing.</p>
          </div>
        )}
      </Card>

      {/* NAVIGATION TABS FOR SECTIONS */}
      <div className="flex flex-wrap items-center gap-2 border-b border-slate-200 pb-2">
        <button
          onClick={() => setActiveTab('PROCUREMENT')}
          className={`px-4 py-2.5 rounded-xl text-xs font-extrabold transition-all cursor-pointer flex items-center gap-1.5 ${activeTab === 'PROCUREMENT' ? 'bg-emerald-700 text-white shadow-xs' : 'bg-emerald-50 text-emerald-900 border border-emerald-200 hover:bg-emerald-100'}`}
        >
          <CreditCard className="w-3.5 h-3.5" />
          Procurement & Payment Workflow
        </button>

        <button
          onClick={() => setActiveTab('QUEUE')}
          className={`px-4 py-2.5 rounded-xl text-xs font-extrabold transition-all cursor-pointer ${activeTab === 'QUEUE' ? 'bg-slate-900 text-white shadow-xs' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'}`}
        >
          Queue Management ({dashboard?.queueList?.length || 0})
        </button>

        <button
          onClick={() => setActiveTab('APPROVALS')}
          className={`px-4 py-2.5 rounded-xl text-xs font-extrabold transition-all cursor-pointer flex items-center gap-1.5 ${activeTab === 'APPROVALS' ? 'bg-amber-600 text-white shadow-xs' : 'bg-amber-50 text-amber-900 border border-amber-200 hover:bg-amber-100'}`}
        >
          <CheckSquare className="w-3.5 h-3.5" />
          Pending Approvals ({pendingApprovals.length})
        </button>

        <button
          onClick={() => setActiveTab('ACTION_REQUIRED')}
          className={`px-4 py-2.5 rounded-xl text-xs font-extrabold transition-all cursor-pointer flex items-center gap-1.5 ${activeTab === 'ACTION_REQUIRED' ? 'bg-rose-700 text-white shadow-xs' : 'bg-rose-50 text-rose-900 border border-rose-200 hover:bg-rose-100'}`}
        >
          <AlertCircle className="w-3.5 h-3.5" />
          Action Required ({actionAlerts.length})
        </button>

        <button
          onClick={() => setActiveTab('SIMULATOR')}
          className={`px-4 py-2.5 rounded-xl text-xs font-extrabold transition-all cursor-pointer flex items-center gap-1.5 ${activeTab === 'SIMULATOR' ? 'bg-purple-700 text-white shadow-xs' : 'bg-purple-50 text-purple-900 border border-purple-200 hover:bg-purple-100'}`}
        >
          <Bot className="w-3.5 h-3.5" />
          AI Call Simulator (Dev)
        </button>

        <button
          onClick={() => setActiveTab('AUDIT')}
          className={`px-4 py-2.5 rounded-xl text-xs font-extrabold transition-all cursor-pointer flex items-center gap-1.5 ${activeTab === 'AUDIT' ? 'bg-slate-700 text-white shadow-xs' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'}`}
        >
          <FileText className="w-3.5 h-3.5" />
          Audit Trail
        </button>
      </div>

      {/* SECTION 0: PROCUREMENT & PAYMENT WORKFLOW */}
      {activeTab === 'PROCUREMENT' && (
        <div className="flex flex-col gap-6 text-left">
          {/* KPI Overview Cards */}
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4">
            <Card className="bg-gradient-to-br from-emerald-900 to-teal-950 text-white border-2 border-emerald-600">
              <span className="text-[10px] text-emerald-300 font-extrabold uppercase block">Total Disbursed Today</span>
              <div className="text-3xl font-black text-white mt-1">₹{(ownerOverview.totalDisbursedAmountRs || 0).toLocaleString('en-IN')}</div>
              <span className="text-xs text-emerald-200 mt-1 block">Direct Benefit Transfer (DBT)</span>
            </Card>

            <Card className="bg-white border border-slate-200">
              <span className="text-[10px] text-slate-400 font-extrabold uppercase block">Completed Procurements</span>
              <div className="text-2xl font-black text-emerald-700 mt-1">{ownerOverview.completedProcurementsCount || 0}</div>
              <span className="text-xs text-slate-500 mt-1 block">Tokens Processed & Confirmed</span>
            </Card>

            <Card className="bg-white border border-slate-200">
              <span className="text-[10px] text-slate-400 font-extrabold uppercase block">Completed Payments</span>
              <div className="text-2xl font-black text-blue-700 mt-1">{ownerOverview.paymentsCompletedCount || 0}</div>
              <span className="text-xs text-slate-500 mt-1 block">Successful Bank Transfers</span>
            </Card>

            <Card className="bg-white border border-slate-200">
              <span className="text-[10px] text-slate-400 font-extrabold uppercase block">Pending / Failed Payments</span>
              <div className="flex items-center gap-2 mt-1">
                <span className="text-2xl font-black text-amber-600">{ownerOverview.paymentsPendingCount || 0} Pending</span>
                <span className="text-[10px] px-2 py-0.5 rounded bg-rose-100 text-rose-800 font-bold">{ownerOverview.paymentFailuresCount || 0} Failed</span>
              </div>
              <span className="text-xs text-slate-500 mt-1 block">Awaiting / Retry Available</span>
            </Card>
          </div>

          {/* OPERATOR STEPPER MODAL / PANEL */}
          <Card title="Mandi Operator Stepper — Verified Procurement & Payment Flow" subtitle="Step-by-step verification, net weight calculation, MSP quality pricing, and DBT payment simulation">
            {/* Step Bar Indicator */}
            <div className="grid grid-cols-3 sm:grid-cols-6 gap-2 mb-6 text-center text-xs font-extrabold">
              <div className={`p-2.5 rounded-xl border ${procStep >= 1 ? 'bg-emerald-600 text-white border-emerald-600' : 'bg-slate-100 text-slate-400 border-slate-200'}`}>
                1. Verify
              </div>
              <div className={`p-2.5 rounded-xl border ${procStep >= 2 ? 'bg-emerald-600 text-white border-emerald-600' : 'bg-slate-100 text-slate-400 border-slate-200'}`}>
                2. Weight
              </div>
              <div className={`p-2.5 rounded-xl border ${procStep >= 3 ? 'bg-emerald-600 text-white border-emerald-600' : 'bg-slate-100 text-slate-400 border-slate-200'}`}>
                3. Quality
              </div>
              <div className={`p-2.5 rounded-xl border ${procStep >= 4 ? 'bg-emerald-600 text-white border-emerald-600' : 'bg-slate-100 text-slate-400 border-slate-200'}`}>
                4. Summary
              </div>
              <div className={`p-2.5 rounded-xl border ${procStep >= 5 ? 'bg-emerald-600 text-white border-emerald-600' : 'bg-slate-100 text-slate-400 border-slate-200'}`}>
                5. Confirm
              </div>
              <div className={`p-2.5 rounded-xl border ${procStep >= 6 ? 'bg-emerald-600 text-white border-emerald-600' : 'bg-slate-100 text-slate-400 border-slate-200'}`}>
                6. Payment
              </div>
            </div>

            {/* STEP 1: VERIFICATION */}
            {procStep === 1 && (
              <div className="flex flex-col gap-4 p-4 rounded-2xl bg-slate-50 border border-slate-200">
                <div className="flex items-center justify-between">
                  <h4 className="font-extrabold text-slate-900 text-base">Step 1: Farmer Arrival & Identity Verification</h4>
                  <span className="text-xs bg-blue-100 text-blue-800 px-2.5 py-1 rounded-full font-bold">Booking Token #{procBookingId}</span>
                </div>

                {/* QR Code Scanner / Verification Box */}
                <div className="p-3 bg-emerald-950 text-white rounded-xl border border-emerald-800 flex flex-col gap-2 text-xs">
                  <div className="flex items-center justify-between">
                    <span className="font-extrabold text-amber-400 flex items-center gap-1.5">
                      <ShieldCheck className="w-4 h-4 text-emerald-400" /> Digital QR Code Scanner / Verifier
                    </span>
                    <span className="text-[10px] text-emerald-300 font-semibold">Official Mandi Gate Scanner</span>
                  </div>
                  <div className="flex gap-2">
                    <input
                      type="text"
                      placeholder="Scan or paste Farmer QR Payload (JSON)..."
                      value={qrInput}
                      onChange={(e) => setQrInput(e.target.value)}
                      className="flex-1 px-3 py-1.5 rounded-lg bg-slate-900 border border-emerald-700 text-emerald-200 text-xs font-mono"
                    />
                    <button
                      type="button"
                      onClick={() => { handleScanQrCode(qrInput); setQrInput(''); }}
                      className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-500 text-white font-extrabold text-xs rounded-lg transition-colors cursor-pointer shrink-0"
                    >
                      Verify QR
                    </button>
                  </div>

                  {scannedPayload && (
                    <div className="p-2.5 rounded-lg bg-emerald-900/80 border border-emerald-500 text-[11px] flex flex-col gap-1 text-emerald-100">
                      <div className="flex items-center justify-between font-bold text-emerald-300">
                        <span>✅ QR VERIFIED: Token #{scannedPayload.bookingId}</span>
                        <span>{scannedPayload.crop?.type || 'Paddy'} • {scannedPayload.crop?.quantityKg || 500} kg</span>
                      </div>
                      <div>Farmer: <strong>{scannedPayload.farmer?.name}</strong> ({scannedPayload.farmer?.phone})</div>
                      <div className="text-[10px] text-emerald-300">Hash: {scannedPayload.securityHash}</div>
                    </div>
                  )}
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs">
                  <div>
                    <label className="text-slate-500 font-bold block mb-1">Target Booking ID</label>
                    <input
                      type="number"
                      value={procBookingId}
                      onChange={(e) => setProcBookingId(Number(e.target.value))}
                      className="w-full p-2.5 rounded-xl border border-slate-300 font-extrabold text-sm"
                    />
                  </div>
                  <div>
                    <label className="text-slate-500 font-bold block mb-1">Operator Name</label>
                    <input
                      type="text"
                      value={operatorName}
                      onChange={(e) => setOperatorName(e.target.value)}
                      className="w-full p-2.5 rounded-xl border border-slate-300 text-sm font-semibold"
                    />
                  </div>
                </div>
                <p className="text-xs text-slate-600 leading-relaxed">
                  Verify farmer's arrival at mandi gate against booking token #{procBookingId}. Verify Aadhaar/Identity card details prior to weighing.
                </p>
                <div className="flex justify-end gap-3 pt-2">
                  <Button variant="primary" icon={UserCheck} loading={actionLoading} onClick={handleVerifyProcurement}>
                    Verify Farmer & Proceed to Weighing
                  </Button>
                </div>
              </div>
            )}

            {/* STEP 2: WEIGHING */}
            {procStep === 2 && (
              <div className="flex flex-col gap-4 p-4 rounded-2xl bg-slate-50 border border-slate-200">
                <div className="flex items-center justify-between">
                  <h4 className="font-extrabold text-slate-900 text-base">Step 2: Digital Scale Weighing & Net Weight Calculation</h4>
                  <span className="text-xs bg-emerald-100 text-emerald-800 px-2.5 py-1 rounded-full font-bold">Verified: Ramesh Kumar</span>
                </div>
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 text-xs">
                  <div>
                    <label className="text-slate-500 font-bold block mb-1">Gross Weight (kg)</label>
                    <input
                      type="number"
                      value={grossWeight}
                      onChange={(e) => setGrossWeight(Number(e.target.value))}
                      className="w-full p-2.5 rounded-xl border border-slate-300 font-extrabold text-sm text-slate-900"
                    />
                  </div>
                  <div>
                    <label className="text-slate-500 font-bold block mb-1">Tare Weight (Bags/Gunny) (kg)</label>
                    <input
                      type="number"
                      value={tareWeight}
                      onChange={(e) => setTareWeight(Number(e.target.value))}
                      className="w-full p-2.5 rounded-xl border border-slate-300 font-extrabold text-sm text-slate-900"
                    />
                  </div>
                  <div className="bg-emerald-100 p-3 rounded-xl border border-emerald-300 flex flex-col justify-center">
                    <span className="text-[10px] text-emerald-800 font-extrabold uppercase">Calculated Net Quantity</span>
                    <span className="text-xl font-black text-emerald-950 mt-0.5">{grossWeight - tareWeight} kg</span>
                  </div>
                </div>
                <div className="flex justify-between items-center pt-2">
                  <Button variant="outline" size="sm" onClick={() => setProcStep(1)}>Back</Button>
                  <Button variant="primary" icon={Scale} loading={actionLoading} onClick={handleRecordWeighing}>
                    Record Weighing & Next
                  </Button>
                </div>
              </div>
            )}

            {/* STEP 3: QUALITY CHECK */}
            {procStep === 3 && (
              <div className="flex flex-col gap-4 p-4 rounded-2xl bg-slate-50 border border-slate-200">
                <div className="flex items-center justify-between">
                  <h4 className="font-extrabold text-slate-900 text-base">Step 3: Quality Check & MSP Rate Determination</h4>
                  <span className="text-xs bg-emerald-100 text-emerald-800 px-2.5 py-1 rounded-full font-bold">Net Quantity: {grossWeight - tareWeight} kg</span>
                </div>
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 text-xs">
                  <div>
                    <label className="text-slate-500 font-bold block mb-1">Quality Grade</label>
                    <select
                      value={qualityGrade}
                      onChange={(e) => setQualityGrade(e.target.value)}
                      className="w-full p-2.5 rounded-xl border border-slate-300 font-extrabold text-sm"
                    >
                      <option value="Grade A">Grade A (Premium MSP ₹25/kg)</option>
                      <option value="Grade B">Grade B (Standard MSP ₹23/kg)</option>
                      <option value="Grade C">Grade C (Fair MSP ₹20/kg)</option>
                    </select>
                  </div>
                  <div>
                    <label className="text-slate-500 font-bold block mb-1">Quality Inspection Status</label>
                    <select
                      value={qualityStatus}
                      onChange={(e) => setQualityStatus(e.target.value)}
                      className="w-full p-2.5 rounded-xl border border-slate-300 font-extrabold text-sm"
                    >
                      <option value="ACCEPTED">ACCEPTED (Passes Standard Specs)</option>
                      <option value="NEEDS_REVIEW">NEEDS REVIEW (Moisture High)</option>
                      <option value="REJECTED">REJECTED (Fails Specs)</option>
                    </select>
                  </div>
                  <div>
                    <label className="text-slate-500 font-bold block mb-1">Moisture Level (%)</label>
                    <input
                      type="number"
                      step="0.1"
                      value={moisturePct}
                      onChange={(e) => setMoisturePct(Number(e.target.value))}
                      className="w-full p-2.5 rounded-xl border border-slate-300 text-sm font-bold"
                    />
                  </div>
                </div>
                <div className="flex justify-between items-center pt-2">
                  <Button variant="outline" size="sm" onClick={() => setProcStep(2)}>Back</Button>
                  <Button variant="primary" icon={ShieldCheck} loading={actionLoading} onClick={handleRecordQuality}>
                    Record Quality Check & Summary
                  </Button>
                </div>
              </div>
            )}

            {/* STEP 4 & 5: SUMMARY & CONFIRMATION */}
            {(procStep === 4 || procStep === 5) && (
              <div className="flex flex-col gap-4 p-4 rounded-2xl bg-emerald-50/50 border-2 border-emerald-300">
                <div className="flex items-center justify-between border-b border-emerald-200 pb-3">
                  <div>
                    <span className="text-[10px] text-emerald-800 font-extrabold uppercase block">PROCUREMENT SUMMARY & RATE CALCULATION</span>
                    <h4 className="text-xl font-black text-slate-900 mt-0.5">Booking #{procBookingId} • Ramesh Kumar</h4>
                  </div>
                  <span className="px-3 py-1 bg-emerald-700 text-white font-extrabold text-xs rounded-full">
                    {qualityGrade} ({qualityStatus})
                  </span>
                </div>

                <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-xs">
                  <div className="bg-white p-3 rounded-xl border border-emerald-200">
                    <span className="text-slate-400 font-bold block">Gross Weight</span>
                    <span className="text-slate-900 font-black text-base">{grossWeight} kg</span>
                  </div>
                  <div className="bg-white p-3 rounded-xl border border-emerald-200">
                    <span className="text-slate-400 font-bold block">Tare Weight</span>
                    <span className="text-slate-900 font-black text-base">{tareWeight} kg</span>
                  </div>
                  <div className="bg-white p-3 rounded-xl border border-emerald-200">
                    <span className="text-slate-400 font-bold block">Net Payable Quantity</span>
                    <span className="text-emerald-800 font-black text-base">{grossWeight - tareWeight} kg</span>
                  </div>
                  <div className="bg-emerald-800 text-white p-3 rounded-xl shadow-xs">
                    <span className="text-emerald-200 font-bold block">Total Amount Payable</span>
                    <span className="text-amber-300 font-black text-xl">₹{((grossWeight - tareWeight) * (qualityGrade === 'Grade A' ? 25 : 23)).toLocaleString('en-IN')}</span>
                  </div>
                </div>

                <div className="flex justify-between items-center pt-2">
                  <Button variant="outline" size="sm" onClick={() => setProcStep(3)}>Back</Button>
                  <Button variant="success" icon={CheckCircle2} loading={actionLoading} onClick={handleConfirmProcurementCode}>
                    Confirm Procurement & Generate Code PR-10001
                  </Button>
                </div>
              </div>
            )}

            {/* STEP 6: PAYMENT GATEWAY SIMULATION */}
            {procStep === 6 && (
              <div className="flex flex-col gap-4 p-4 rounded-2xl bg-gradient-to-r from-slate-900 to-slate-950 text-white border-2 border-amber-500 shadow-xl">
                <div className="flex items-center justify-between border-b border-slate-800 pb-3">
                  <div>
                    <div className="flex items-center gap-2">
                      <CreditCard className="w-5 h-5 text-amber-400" />
                      <h4 className="text-lg font-black text-white">Direct Benefit Transfer (DBT) Payment Adapter</h4>
                    </div>
                    <p className="text-xs text-slate-300 mt-0.5">
                      Procurement Code: <strong>{activeProcurement?.procurementCode || 'PR-10001'}</strong> | Total Amount: <strong>₹{activeProcurement?.totalAmount || (grossWeight - tareWeight) * 25}</strong>
                    </p>
                  </div>
                  <StatusBadge status={activePayment?.status || 'NOT_INITIATED'} />
                </div>

                {!activePayment ? (
                  <div className="flex flex-col items-center py-4 gap-3">
                    <p className="text-xs text-slate-300">Click below to initiate DBT bank account transfer to Ramesh Kumar (SBI A/C **4821)</p>
                    <Button size="lg" variant="success" icon={Zap} loading={actionLoading} onClick={handleInitiatePayment}>
                      Initiate Payment (DBT)
                    </Button>
                  </div>
                ) : (
                  <div className="flex flex-col gap-4">
                    <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 text-xs">
                      <div className="bg-slate-800/80 p-3 rounded-xl border border-slate-700">
                        <span className="text-slate-400 font-bold block">Payment Code</span>
                        <span className="text-white font-mono font-bold text-sm">{activePayment.paymentCode || 'PAY-98421'}</span>
                      </div>
                      <div className="bg-slate-800/80 p-3 rounded-xl border border-slate-700">
                        <span className="text-slate-400 font-bold block">Transaction ID / UTR</span>
                        <span className="text-amber-400 font-mono font-bold text-sm">{activePayment.transactionId || 'PENDING'}</span>
                      </div>
                      <div className="bg-slate-800/80 p-3 rounded-xl border border-slate-700">
                        <span className="text-slate-400 font-bold block">Payment Status</span>
                        <span className="font-extrabold text-sm text-emerald-400">{activePayment.status}</span>
                      </div>
                    </div>

                    {/* GATEWAY SIMULATION ACTION BUTTONS */}
                    <div className="p-3 rounded-xl bg-slate-800 border border-slate-700">
                      <span className="text-[10px] text-amber-400 font-black uppercase tracking-wider block mb-2">
                        DBT PAYMENT GATEWAY ACTIONS
                      </span>
                      <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
                        <Button size="sm" variant="success" icon={CheckCircle} loading={actionLoading} onClick={() => handleSimulatePaymentSuccess(activePayment.paymentId)}>
                          [Simulate Success]
                        </Button>
                        <Button size="sm" variant="secondary" icon={Clock} loading={actionLoading} onClick={() => setActivePayment({...activePayment, status: 'PENDING'})}>
                          [Simulate Pending]
                        </Button>
                        <Button size="sm" variant="danger" icon={XCircle} loading={actionLoading} onClick={() => handleSimulatePaymentFailure(activePayment.paymentId)}>
                          [Simulate Failure]
                        </Button>
                        <Button size="sm" variant="outline" icon={RotateCcw} loading={actionLoading} onClick={() => handleRetryPayment(activePayment.paymentId)} className="border-amber-400 text-amber-300">
                          [Retry Payment]
                        </Button>
                      </div>
                    </div>
                  </div>
                )}
              </div>
            )}
          </Card>

          {/* MANDI PROCUREMENTS & PAYMENTS HISTORY TABLE */}
          <Card title="Recent Mandi Procurements & Payment Ledger" subtitle="Complete record of procurements, weighment, quality grades, MSP values, and payment status">
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead>
                  <tr className="border-b border-slate-200 bg-slate-50 text-slate-700 font-extrabold uppercase">
                    <th className="p-3">Procurement Code</th>
                    <th className="p-3">Farmer</th>
                    <th className="p-3">Crop</th>
                    <th className="p-3">Net Weight</th>
                    <th className="p-3">Grade</th>
                    <th className="p-3">Rate/kg</th>
                    <th className="p-3">Total Amount</th>
                    <th className="p-3">Payment Status</th>
                    <th className="p-3">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 font-medium">
                  <tr>
                    <td colSpan={9} className="p-8 text-center text-slate-400 text-sm">
                      No procurement records found. Records will appear here after farmers complete their procurement.
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </Card>
        </div>
      )}

      {/* SECTION 1: QUEUE MANAGEMENT */}
      {activeTab === 'QUEUE' && (
        <Card
          title="Current Mandi Queue & Controls"
          action={
            <Button
              size="sm"
              variant="primary"
              icon={Zap}
              loading={actionLoading}
              onClick={handleCallNext}
            >
              CALL NEXT FARMER
            </Button>
          }
        >
          {dashboard?.queueList?.length === 0 ? (
            <div className="py-8 text-center text-slate-500 text-sm">
              No active waiting farmers in the queue right now.
            </div>
          ) : (
            <div className="flex flex-col gap-3">
              {dashboard?.queueList?.map((b) => (
                <div
                  key={b.bookingId}
                  className="p-4 rounded-2xl border border-slate-200 bg-white flex flex-col sm:flex-row sm:items-center justify-between gap-4 hover:border-slate-300 transition-all text-left"
                >
                  <div className="flex items-center gap-3.5">
                    <div className="w-10 h-10 rounded-xl bg-slate-100 text-slate-800 flex items-center justify-center font-black text-sm shrink-0">
                      #{b.bookingId}
                    </div>
                    <div>
                      <div className="flex items-center gap-2">
                        <h4 className="font-extrabold text-slate-900 text-base">{b.farmerName}</h4>
                        <StatusBadge status={b.status} />
                      </div>
                      <p className="text-xs text-slate-500 mt-0.5">
                        Crop: <strong>{b.cropType}</strong> ({b.quantity} kg) • Slot: <strong>{b.slot}</strong>
                      </p>
                    </div>
                  </div>

                  <div className="flex flex-wrap items-center gap-2">
                    {b.status === 'CALLED' && (
                      <Button size="sm" variant="secondary" icon={UserCheck} onClick={() => handleMarkArrived(b.bookingId)}>
                        Mark Arrived
                      </Button>
                    )}
                    {b.status === 'ARRIVED' && (
                      <Button size="sm" variant="primary" icon={Play} onClick={() => handleStartProcurement(b.bookingId)}>
                        Start Procurement
                      </Button>
                    )}
                    {b.status === 'PROCESSING' && (
                      <Button size="sm" variant="success" icon={CheckCircle2} onClick={() => handleCompleteProcurement(b.bookingId)}>
                        Complete Procurement
                      </Button>
                    )}
                    <Button size="sm" variant="outline" icon={SkipForward} onClick={() => setSkipModalBookingId(b.bookingId)}>
                      Skip
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>
      )}

      {/* SECTION 2: PENDING APPROVALS */}
      {activeTab === 'APPROVALS' && (
        <Card title="Pending Approvals (Owner Decision Required)" subtitle="AI Agent & Farmer Cancellation/Rescheduling Requests">
          {pendingApprovals.length === 0 ? (
            <div className="py-8 text-center text-slate-500 text-sm">
              No pending approval requests requiring owner decision.
            </div>
          ) : (
            <div className="flex flex-col gap-3">
              {pendingApprovals.map((req) => (
                <div
                  key={req.id}
                  className={`p-4 rounded-2xl border flex flex-col sm:flex-row sm:items-center justify-between gap-4 text-left ${req.type === 'CANCELLATION' ? 'border-rose-300 bg-rose-50/40' : 'border-amber-300 bg-amber-50/40'}`}
                >
                  <div>
                    <div className="flex items-center gap-2">
                      <span className={`px-2 py-0.5 rounded text-[10px] font-black uppercase ${req.type === 'CANCELLATION' ? 'bg-rose-200 text-rose-900' : 'bg-amber-200 text-amber-900'}`}>
                        {req.type} REQUEST
                      </span>
                      <span className="text-[10px] font-bold px-2 py-0.5 bg-purple-100 text-purple-900 rounded">
                        Created by: {req.createdBy}
                      </span>
                    </div>

                    <h4 className="font-extrabold text-slate-900 text-base mt-1">
                      {req.farmerName} (Booking #{req.bookingId})
                    </h4>
                    <p className="text-xs text-slate-600 mt-0.5">
                      Phone: <strong>{req.farmerPhone}</strong> | Current Slot: <strong>{req.currentSlot} ({req.currentDate})</strong>
                    </p>

                    {req.type === 'RESCHEDULE' && (
                      <p className="text-xs text-amber-950 font-bold mt-1">
                        Requested Slot: <strong>{req.requestedDate} ({req.requestedSlot})</strong>
                      </p>
                    )}

                    <p className="text-xs font-semibold text-slate-700 mt-1 italic">
                      Reason: "{req.reason || 'No reason provided'}"
                    </p>
                  </div>

                  <div className="flex items-center gap-2">
                    <Button
                      size="sm"
                      variant="success"
                      icon={Check}
                      loading={actionLoading}
                      onClick={() => handleApproveApprovalRequest(req.id)}
                    >
                      Approve
                    </Button>

                    <Button
                      size="sm"
                      variant="danger"
                      icon={X}
                      loading={actionLoading}
                      onClick={() => handleRejectApprovalRequest(req.id)}
                    >
                      Reject
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>
      )}

      {/* SECTION 3: ACTION REQUIRED ALERTS (MISSED ARRIVALS & UNREACHABLE FARMERS) */}
      {activeTab === 'ACTION_REQUIRED' && (
        <Card title="Action Required Alerts" subtitle="Missed expected arrivals & unreachable farmers (No automatic cancellation)">
          {actionAlerts.length === 0 ? (
            <div className="py-8 text-center text-slate-500 text-sm">
              No active alerts requiring operator intervention.
            </div>
          ) : (
            <div className="flex flex-col gap-3">
              {actionAlerts.map((alert) => (
                <div key={alert.bookingId} className="p-4 rounded-2xl border-2 border-rose-400 bg-rose-50/50 flex flex-col sm:flex-row sm:items-center justify-between gap-4 text-left">
                  <div>
                    <div className="flex items-center gap-2">
                      <AlertCircle className="w-5 h-5 text-rose-600" />
                      <h4 className="font-extrabold text-rose-950 text-base">{alert.issueType}</h4>
                    </div>
                    <p className="text-xs text-slate-900 font-bold mt-1">
                      {alert.farmerName} (Booking #{alert.bookingId}) • Phone: {alert.farmerPhone}
                    </p>
                    <p className="text-xs text-rose-900 font-medium mt-1 leading-relaxed">
                      {alert.issueDescription}
                    </p>
                  </div>

                  <div className="flex flex-wrap items-center gap-2">
                    <Button
                      size="sm"
                      variant="outline"
                      icon={Phone}
                      onClick={() => handleResolveAction(alert.bookingId, 'CONTACT_FARMER')}
                    >
                      Contact Farmer
                    </Button>

                    <Button
                      size="sm"
                      variant="secondary"
                      onClick={() => handleResolveAction(alert.bookingId, 'KEEP_BOOKING')}
                    >
                      Keep Booking
                    </Button>

                    <Button
                      size="sm"
                      variant="danger"
                      onClick={() => handleResolveAction(alert.bookingId, 'CANCEL')}
                    >
                      Cancel Booking
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>
      )}

      {/* SECTION 4: AI VOICE AGENT SIMULATOR */}
      {activeTab === 'SIMULATOR' && (
        <Card title="AI Voice Call Advisory" subtitle="Initiate automated multilingual voice call advisory for farmer queue dispatch">
          <div className="flex flex-col gap-4 text-left">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="text-xs font-bold text-slate-700 block mb-1">Target Booking ID</label>
                <input
                  type="text"
                  placeholder="e.g. 1"
                  value={simBookingId}
                  onChange={(e) => setSimBookingId(e.target.value)}
                  className="w-full rounded-xl border border-slate-300 p-2.5 text-sm font-semibold"
                />
              </div>

              <div>
                <label className="text-xs font-bold text-slate-700 block mb-1">Call Outcome Reason</label>
                <input
                  type="text"
                  value={simReason}
                  onChange={(e) => setSimReason(e.target.value)}
                  className="w-full rounded-xl border border-slate-300 p-2.5 text-sm"
                />
              </div>
            </div>

            {/* Simulation Triggers Grid */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3 pt-2">
              <Button
                variant="success"
                icon={Send}
                loading={actionLoading}
                onClick={() => handleSimulateVoiceCall('FARMER_READY_TO_TRAVEL')}
              >
                [Farmer Ready]
              </Button>

              <Button
                variant="danger"
                icon={XSquare}
                loading={actionLoading}
                onClick={() => handleSimulateVoiceCall('CANCELLATION_REQUESTED')}
              >
                [Cannot Come]
              </Button>

              <Button
                variant="primary"
                icon={Calendar}
                loading={actionLoading}
                onClick={() => handleSimulateVoiceCall('RESCHEDULE_REQUESTED')}
              >
                [Request Reschedule]
              </Button>

              <Button
                variant="outline"
                icon={PhoneCall}
                loading={actionLoading}
                onClick={() => handleSimulateVoiceCall('NO_ANSWER')}
              >
                [No Answer]
              </Button>
            </div>

            {simResultMsg && (
              <div className="p-3 rounded-xl bg-purple-50 border border-purple-300 text-purple-900 text-xs font-bold mt-2">
                {simResultMsg}
              </div>
            )}
          </div>
        </Card>
      )}

      {/* SECTION 5: AUDIT TRAIL LOGS */}
      {activeTab === 'AUDIT' && (
        <Card title="System Audit Logs" subtitle="Chronological record of all journey, AI events, and owner decisions">
          {auditLogs.length === 0 ? (
            <div className="py-8 text-center text-slate-500 text-sm">
              No audit logs recorded yet.
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead>
                  <tr className="border-b border-slate-200 bg-slate-50 text-slate-700 font-extrabold uppercase">
                    <th className="p-3">Time</th>
                    <th className="p-3">Event Type</th>
                    <th className="p-3">Booking ID</th>
                    <th className="p-3">Actor</th>
                    <th className="p-3">Details</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {auditLogs.map((log) => (
                    <tr key={log.id} className="hover:bg-slate-50">
                      <td className="p-3 text-slate-500 shrink-0 font-medium">
                        {log.timestamp ? new Date(log.timestamp).toLocaleTimeString() : 'N/A'}
                      </td>
                      <td className="p-3 font-extrabold text-slate-900">{log.eventType}</td>
                      <td className="p-3 font-bold text-slate-700">#{log.bookingId}</td>
                      <td className="p-3">
                        <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-slate-100 text-slate-800">
                          {log.actor}
                        </span>
                      </td>
                      <td className="p-3 text-slate-600 font-medium">{log.details}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      )}

      {/* SKIP FARMER REASON MODAL */}
      {skipModalBookingId && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4 z-50">
          <Card title={`Skip Farmer for Token #${skipModalBookingId}`} className="max-w-md w-full">
            <div className="flex flex-col gap-4 text-left">
              <p className="text-xs text-slate-600">
                Provide an optional reason for skipping this farmer's turn in the mandi queue:
              </p>
              <input
                type="text"
                placeholder="e.g. Farmer not present at gate when called"
                value={skipReason}
                onChange={(e) => setSkipReason(e.target.value)}
                className="w-full rounded-xl border border-slate-300 p-3 text-sm"
              />
              <div className="flex gap-3 pt-2">
                <Button size="md" variant="danger" fullWidth onClick={handleConfirmSkip}>
                  Confirm Skip
                </Button>
                <Button size="md" variant="secondary" fullWidth onClick={() => setSkipModalBookingId(null)}>
                  Cancel
                </Button>
              </div>
            </div>
          </Card>
        </div>
      )}
    </div>
  );
};

export default OwnerDashboard;
