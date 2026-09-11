import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { 
  Building2, 
  Scale, 
  Users, 
  ClipboardCheck, 
  ArrowRight, 
  ShieldCheck, 
  Clock, 
  TrendingUp, 
  Zap, 
  Layers, 
  Sprout, 
  LogIn
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

export const OwnerLanding = () => {
  const navigate = useNavigate();
  const { login, switchRole } = useAuth();

  const handleQuickDemoAccess = async () => {
    switchRole('OWNER');
    await login('9876543211', '123456', 'OWNER');
    navigate('/owner/dashboard');
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans pb-12">
      {/* Hero Banner Section */}
      <section className="relative overflow-hidden pt-12 pb-16 px-4 border-b border-slate-800 bg-gradient-to-b from-slate-900 via-slate-950 to-slate-950">
        <div className="absolute top-0 right-1/4 w-96 h-96 bg-amber-500/10 rounded-full blur-3xl pointer-events-none" />
        <div className="absolute bottom-0 left-1/4 w-96 h-96 bg-emerald-500/10 rounded-full blur-3xl pointer-events-none" />

        <div className="max-w-5xl mx-auto text-left relative z-10">
          <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-amber-500/10 border border-amber-500/30 text-amber-400 text-xs font-bold mb-6">
            <Building2 className="w-4 h-4" />
            <span>MANDI OPERATOR & CENTRE OWNER PORTAL</span>
          </div>

          <h1 className="text-3xl sm:text-5xl font-black tracking-tight text-white leading-tight">
            Digital Control Room for <br className="hidden sm:inline" />
            <span className="bg-gradient-to-r from-amber-300 via-emerald-300 to-teal-200 bg-clip-text text-transparent">
              Procurement Centre Operations
            </span>
          </h1>

          <p className="mt-4 text-base sm:text-lg text-slate-300 max-w-2xl font-normal leading-relaxed">
            Eliminate mandi bottlenecks with live token queue dispatching, automated weighbridge integration, quality grading, and real-time DBT payment approvals.
          </p>

          {/* Quick Action CTA Box */}
          <div className="mt-8 flex flex-col sm:flex-row items-stretch sm:items-center gap-4">
            <Link
              to="/owner/login"
              className="px-6 py-3.5 rounded-2xl bg-amber-400 hover:bg-amber-300 text-slate-950 font-black text-sm flex items-center justify-center gap-2 shadow-lg shadow-amber-500/20 transition-all cursor-pointer"
            >
              <LogIn className="w-5 h-5" />
              <span>Operator Portal Login</span>
            </Link>

            <button
              onClick={handleQuickDemoAccess}
              className="px-6 py-3.5 rounded-2xl bg-slate-800 hover:bg-slate-700 border border-slate-700 text-amber-300 font-extrabold text-sm flex items-center justify-center gap-2 transition-all cursor-pointer"
            >
              <Zap className="w-5 h-5 text-amber-400" />
              <span>Instant Demo Launch (Ravi Kumar)</span>
            </button>
          </div>

          {/* Live System Stats Pill Grid */}
          <div className="mt-10 grid grid-cols-2 md:grid-cols-4 gap-3">
            <div className="p-4 rounded-2xl bg-slate-900/80 border border-slate-800">
              <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Active Capacity</p>
              <p className="text-xl font-black text-amber-400 mt-1">50.0 MT / Day</p>
            </div>
            <div className="p-4 rounded-2xl bg-slate-900/80 border border-slate-800">
              <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Avg Processing Time</p>
              <p className="text-xl font-black text-emerald-400 mt-1">4.5 Mins / Farmer</p>
            </div>
            <div className="p-4 rounded-2xl bg-slate-900/80 border border-slate-800">
              <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Queue Throughput</p>
              <p className="text-xl font-black text-teal-400 mt-1">98.2% On-Time</p>
            </div>
            <div className="p-4 rounded-2xl bg-slate-900/80 border border-slate-800">
              <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">DBT Disbursal</p>
              <p className="text-xl font-black text-white mt-1">Direct Bank Sync</p>
            </div>
          </div>
        </div>
      </section>

      {/* Feature Capabilities Grid */}
      <section className="py-12 px-4 max-w-5xl mx-auto text-left w-full">
        <h2 className="text-xs font-black uppercase tracking-widest text-amber-400 mb-2">OPERATOR CAPABILITIES</h2>
        <h3 className="text-2xl sm:text-3xl font-black text-white mb-8">Everything Needed to Run a High-Volume Mandi</h3>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 hover:border-amber-500/50 transition-all flex flex-col justify-between">
            <div>
              <div className="w-12 h-12 rounded-2xl bg-amber-500/10 border border-amber-500/20 text-amber-400 flex items-center justify-center mb-4">
                <Users className="w-6 h-6" />
              </div>
              <h4 className="text-lg font-extrabold text-white mb-2">Smart Queue Dispatcher</h4>
              <p className="text-xs text-slate-400 leading-relaxed">
                Call next token with 1-click. Voice agent calls farmers when their slot is ready, keeping waiting bays smooth and congestion-free.
              </p>
            </div>
            <div className="mt-6 pt-4 border-t border-slate-800/80 flex items-center justify-between text-xs text-amber-400 font-bold">
              <span>Token Dispatcher</span>
              <ArrowRight className="w-4 h-4" />
            </div>
          </div>

          <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 hover:border-emerald-500/50 transition-all flex flex-col justify-between">
            <div>
              <div className="w-12 h-12 rounded-2xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 flex items-center justify-center mb-4">
                <Scale className="w-6 h-6" />
              </div>
              <h4 className="text-lg font-extrabold text-white mb-2">Weighbridge & Quality Grading</h4>
              <p className="text-xs text-slate-400 leading-relaxed">
                Record gross & tare weights directly, grade crop quality (Grade A / MSP standard), and auto-generate digital receipt for the farmer.
              </p>
            </div>
            <div className="mt-6 pt-4 border-t border-slate-800/80 flex items-center justify-between text-xs text-emerald-400 font-bold">
              <span>Digital Scales</span>
              <ArrowRight className="w-4 h-4" />
            </div>
          </div>

          <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 hover:border-teal-500/50 transition-all flex flex-col justify-between">
            <div>
              <div className="w-12 h-12 rounded-2xl bg-teal-500/10 border border-teal-500/20 text-teal-400 flex items-center justify-center mb-4">
                <ClipboardCheck className="w-6 h-6" />
              </div>
              <h4 className="text-lg font-extrabold text-white mb-2">Approvals & Cancellations</h4>
              <p className="text-xs text-slate-400 leading-relaxed">
                Review farmer reschedule and cancellation requests instantly. Overcrowding alerts notify you before capacity limits are breached.
              </p>
            </div>
            <div className="mt-6 pt-4 border-t border-slate-800/80 flex items-center justify-between text-xs text-teal-400 font-bold">
              <span>Approval Desk</span>
              <ArrowRight className="w-4 h-4" />
            </div>
          </div>
        </div>
      </section>

      {/* Interconnected Portals Gateway Section */}
      <section className="py-10 px-4 max-w-5xl mx-auto w-full text-left">
        <div className="p-8 rounded-3xl bg-gradient-to-r from-slate-900 via-slate-900 to-amber-950/30 border border-slate-800 flex flex-col md:flex-row items-center justify-between gap-6">
          <div>
            <div className="flex items-center gap-2 text-amber-400 font-bold text-xs uppercase tracking-wider mb-2">
              <Layers className="w-4 h-4" />
              <span>INTERCONNECTED SYSTEM PORTALS</span>
            </div>
            <h3 className="text-xl sm:text-2xl font-black text-white">Switch to Other System Roles</h3>
            <p className="text-xs text-slate-400 mt-1 max-w-xl">
              Access the Farmer procurement mobile interface or switch to District Administrator supervision.
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-3 w-full md:w-auto">
            <Link
              to="/"
              className="flex-1 md:flex-none px-4 py-2.5 rounded-xl bg-emerald-950 hover:bg-emerald-900 border border-emerald-700/60 text-emerald-300 font-bold text-xs flex items-center justify-center gap-2"
            >
              <Sprout className="w-4 h-4" />
              <span>Farmer Portal</span>
            </Link>

            <Link
              to="/admin"
              className="flex-1 md:flex-none px-4 py-2.5 rounded-xl bg-slate-900 hover:bg-slate-800 border border-slate-700 text-amber-300 font-bold text-xs flex items-center justify-center gap-2"
            >
              <ShieldCheck className="w-4 h-4" />
              <span>District Admin</span>
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
};

export default OwnerLanding;
