import React from 'react';
import { X, Building2, Users, Scale, Clock, ShieldCheck, AlertTriangle, Landmark, CheckCircle2 } from 'lucide-react';
import Card from './Card';
import Button from './Button';
import StatusBadge from './StatusBadge';

export const CentreDetailModal = ({ centre, onClose }) => {
  if (!centre) return null;

  return (
    <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4 z-50 overflow-y-auto">
      <div className="bg-white rounded-3xl max-w-2xl w-full p-6 shadow-2xl border border-slate-200 relative text-left flex flex-col gap-5 my-8">
        <button
          onClick={onClose}
          className="absolute top-5 right-5 text-slate-400 hover:text-slate-700 cursor-pointer"
        >
          <X className="w-6 h-6" />
        </button>

        {/* Modal Header */}
        <div className="border-b border-slate-200 pb-4">
          <div className="flex items-center gap-2 text-slate-500 text-xs font-black uppercase tracking-wider mb-1">
            <Building2 className="w-4 h-4 text-emerald-700" /> DISTRICT PROCUREMENT CENTRE DRILL-DOWN VIEW
          </div>
          <div className="flex items-center justify-between gap-3">
            <h3 className="text-2xl font-black text-slate-900">{centre.centreName}</h3>
            <StatusBadge status={centre.status || 'ACTIVE'} />
          </div>
          <p className="text-xs text-slate-600 mt-1 font-semibold">Location: {centre.location}</p>
        </div>

        {/* Overload Explanation Notice */}
        {centre.overloadReason && (
          <div className="p-4 rounded-2xl bg-amber-50 border-2 border-amber-300 text-amber-950 text-xs font-medium flex items-start gap-3">
            <AlertTriangle className="w-5 h-5 text-amber-700 shrink-0 mt-0.5" />
            <div>
              <span className="font-extrabold text-amber-900 uppercase block text-[10px]">WHY IS THIS CENTRE OVERLOADED / CRITICAL?</span>
              <p className="mt-0.5 text-slate-900 font-semibold leading-relaxed">{centre.overloadReason}</p>
            </div>
          </div>
        )}

        {/* Key Operational Metrics */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-xs">
          <div className="bg-slate-50 p-3 rounded-2xl border border-slate-200">
            <span className="text-slate-400 font-bold block">Current Capacity Load</span>
            <span className="text-xl font-black text-slate-900 mt-0.5 block">{centre.currentLoadPercent}%</span>
            <span className="text-[10px] text-slate-500">{centre.currentLoadKg} / {centre.capacityKg} kg</span>
          </div>

          <div className="bg-slate-50 p-3 rounded-2xl border border-slate-200">
            <span className="text-slate-400 font-bold block">Farmers Waiting</span>
            <span className="text-xl font-black text-blue-700 mt-0.5 block">{centre.waitingFarmersCount}</span>
            <span className="text-[10px] text-slate-500">Active Counters: {centre.activeCounters}</span>
          </div>

          <div className="bg-slate-50 p-3 rounded-2xl border border-slate-200">
            <span className="text-slate-400 font-bold block">Estimated Wait Time</span>
            <span className="text-xl font-black text-amber-700 mt-0.5 block">{centre.estimatedWaitMinutes} min</span>
            <span className="text-[10px] text-slate-500">Processing: {centre.avgProcessingTimeMinutes} min/farmer</span>
          </div>

          <div className="bg-emerald-50 p-3 rounded-2xl border border-emerald-200">
            <span className="text-emerald-800 font-bold block">Today's Procurement</span>
            <span className="text-xl font-black text-emerald-900 mt-0.5 block">{centre.todaysProcurementKg} kg</span>
            <span className="text-[10px] text-emerald-700">Farmers Today: {centre.todaysFarmersCount}</span>
          </div>
        </div>

        {/* Detailed Breakdown Specs */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs">
          <Card title="Queue & Cancellation Metrics" subtitle="Today's queue performance">
            <div className="flex flex-col gap-2 font-medium">
              <div className="flex justify-between py-1 border-b border-slate-100">
                <span className="text-slate-500">Farmers Currently Processing:</span>
                <span className="font-bold text-slate-900">{centre.processingFarmersCount || 3}</span>
              </div>
              <div className="flex justify-between py-1 border-b border-slate-100">
                <span className="text-slate-500">Cancellations Today:</span>
                <span className="font-bold text-rose-700">{centre.cancellationsCount}</span>
              </div>
              <div className="flex justify-between py-1">
                <span className="text-slate-500">No-Shows Today:</span>
                <span className="font-bold text-amber-800">{centre.noShowsCount}</span>
              </div>
            </div>
          </Card>

          <Card title="Financial & Payment Summary" subtitle="Pending payouts for this centre">
            <div className="flex flex-col gap-2 font-medium">
              <div className="flex justify-between py-1 border-b border-slate-100">
                <span className="text-slate-500">Payments Pending:</span>
                <span className="font-black text-rose-700">₹{centre.paymentPendingRs?.toLocaleString('en-IN')}</span>
              </div>
              <div className="flex justify-between py-1 border-b border-slate-100">
                <span className="text-slate-500">Payment Status:</span>
                <span className="font-bold text-emerald-800">DBT Active</span>
              </div>
              <div className="flex justify-between py-1">
                <span className="text-slate-500">Average Wait Metric:</span>
                <span className="font-bold text-slate-900">{centre.avgWaitTimeMinutes} min</span>
              </div>
            </div>
          </Card>
        </div>

        <div className="flex justify-end pt-2">
          <Button size="md" variant="secondary" onClick={onClose}>
            Close Drill-down View
          </Button>
        </div>
      </div>
    </div>
  );
};

export default CentreDetailModal;
