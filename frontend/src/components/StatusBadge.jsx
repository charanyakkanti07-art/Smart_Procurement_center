import React from 'react';
import { CheckCircle2, AlertTriangle, AlertCircle, Clock, XCircle } from 'lucide-react';

export const StatusBadge = ({ status = 'AVAILABLE', label, className = '' }) => {
  const normalized = status.toUpperCase();

  const configs = {
    AVAILABLE: {
      bg: 'bg-emerald-50 text-emerald-800 border-emerald-200',
      icon: CheckCircle2,
      text: label || 'AVAILABLE'
    },
    BUSY: {
      bg: 'bg-amber-50 text-amber-800 border-amber-200',
      icon: Clock,
      text: label || 'BUSY'
    },
    NEAR_CAPACITY: {
      bg: 'bg-orange-50 text-orange-800 border-orange-200',
      icon: AlertTriangle,
      text: label || 'NEAR CAPACITY'
    },
    OVERLOADED: {
      bg: 'bg-rose-50 text-rose-800 border-rose-200',
      icon: AlertCircle,
      text: label || 'OVERLOADED'
    },
    CLOSED: {
      bg: 'bg-slate-100 text-slate-700 border-slate-200',
      icon: XCircle,
      text: label || 'CLOSED'
    },
    CONFIRMED: {
      bg: 'bg-emerald-100 text-emerald-900 border-emerald-300 font-bold',
      icon: CheckCircle2,
      text: label || 'CONFIRMED'
    },
    WAITING: {
      bg: 'bg-blue-50 text-blue-800 border-blue-200',
      icon: Clock,
      text: label || 'WAITING'
    },
    PROCESSING: {
      bg: 'bg-amber-50 text-amber-800 border-amber-200',
      icon: Clock,
      text: label || 'PROCESSING'
    },
    COMPLETED: {
      bg: 'bg-emerald-100 text-emerald-900 border-emerald-300',
      icon: CheckCircle2,
      text: label || 'COMPLETED'
    }
  };

  const config = configs[normalized] || configs.AVAILABLE;
  const Icon = config.icon;

  return (
    <span
      className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold border ${config.bg} ${className}`}
    >
      <Icon className="w-3.5 h-3.5 shrink-0" />
      <span>{config.text}</span>
    </span>
  );
};

export default StatusBadge;
