import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { 
  ShieldCheck, 
  BarChart3, 
  MapPin, 
  Sliders, 
  ArrowRight, 
  Building2, 
  Sprout, 
  Zap, 
  Layers, 
  LogIn,
  AlertTriangle,
  Activity
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

export const AdminLanding = () => {
  const navigate = useNavigate();
  const { login, switchRole } = useAuth();

  const handleQuickDemoAccess = async () => {
    switchRole('ADMIN');
    await login('9999999999', 'admin123', 'ADMIN');
    navigate('/admin/dashboard');
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans pb-12">
      {/* Hero Banner Section */}
      <section className="relative overflow-hidden pt-12 pb-16 px-4 border-b border-slate-800 bg-gradient-to-b from-slate-900 via-slate-950 to-slate-950">
        <div className="absolute top-0 left-1/3 w-96 h-96 bg-amber-500/10 rounded-full blur-3xl pointer-events-none" />
        <div className="absolute bottom-0 right-1/4 w-96 h-96 bg-blue-500/10 rounded-full blur-3xl pointer-events-none" />

        <div className="max-w-5xl mx-auto text-left relative z-10">
          <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-slate-800 border border-slate-700 text-amber-400 text-xs font-bold mb-6">
            <ShieldCheck className="w-4 h-4" />
            <span>DISTRICT COLLECTORATE SUPERVISION CONSOLE</span>
          </div>

          <h1 className="text-3xl sm:text-5xl font-black tracking-tight text-white leading-tight">
            District-Wide Command & <br className="hidden sm:inline" />
            <span className="bg-gradient-to-r from-amber-400 via-emerald-300 to-sky-300 bg-clip-text text-transparent">
              AI Load Balancing Platform
            </span>
          </h1>

          <p className="mt-4 text-base sm:text-lg text-slate-300 max-w-2xl font-normal leading-relaxed">
            Real-time oversight for agricultural procurement across all district mandis. Monitor live traffic delays, capacity congestion heatmaps, and AI-driven farmer load balancing.
          </p>

          {/* Quick Action CTA Box */}
          <div className="mt-8 flex flex-col sm:flex-row items-stretch sm:items-center gap-4">
            <Link
              to="/admin/login"
              className="px-6 py-3.5 rounded-2xl bg-amber-400 hover:bg-amber-300 text-slate-950 font-black text-sm flex items-center justify-center gap-2 shadow-lg shadow-amber-500/20 transition-all cursor-pointer"
            >
              <LogIn className="w-5 h-5" />
              <span>District Admin Login</span>
            </Link>

            <button
              onClick={handleQuickDemoAccess}
              className="px-6 py-3.5 rounded-2xl bg-slate-900 hover:bg-slate-800 border border-slate-700 text-amber-300 font-extrabold text-sm flex items-center justify-center gap-2 transition-all cursor-pointer"
            >
              <Zap className="w-5 h-5 text-amber-400" />
              <span>Instant Console Launch (District Official)</span>
            </button>
          </div>

          {/* District Supervision Stats Grid */}
          <div className="mt-10 grid grid-cols-2 md:grid-cols-4 gap-3">
            <div className="p-4 rounded-2xl bg-slate-900/80 border border-slate-800">
              <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Total Mandis</p>
              <p className="text-xl font-black text-white mt-1">4 Active Mandis</p>
            </div>
            <div className="p-4 rounded-2xl bg-slate-900/80 border border-slate-800">
              <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Daily Procurement</p>
              <p className="text-xl font-black text-amber-400 mt-1">119.5 MT Processed</p>
            </div>
            <div className="p-4 rounded-2xl bg-slate-900/80 border border-slate-800">
              <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Congestion Status</p>
              <p className="text-xl font-black text-emerald-400 mt-1">Low (AI Balanced)</p>
            </div>
            <div className="p-4 rounded-2xl bg-slate-900/80 border border-slate-800">
              <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">DBT Success Rate</p>
              <p className="text-xl font-black text-sky-400 mt-1">99.4% Verified</p>
            </div>
          </div>
        </div>
      </section>

      {/* Feature Capabilities Grid */}
      <section className="py-12 px-4 max-w-5xl mx-auto text-left w-full">
        <h2 className="text-xs font-black uppercase tracking-widest text-amber-400 mb-2">ADMINISTRATIVE POWERS</h2>
        <h3 className="text-2xl sm:text-3xl font-black text-white mb-8">District-Wide Governance & Operational Controls</h3>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 hover:border-amber-500/50 transition-all flex flex-col justify-between">
            <div>
              <div className="w-12 h-12 rounded-2xl bg-amber-500/10 border border-amber-500/20 text-amber-400 flex items-center justify-center mb-4">
                <BarChart3 className="w-6 h-6" />
              </div>
              <h4 className="text-lg font-extrabold text-white mb-2">District Mandi Heatmap</h4>
              <p className="text-xs text-slate-400 leading-relaxed">
                Live monitoring of all procurement centres, active vehicle queues, average waiting times, and daily intake tonnages across Medak district.
              </p>
            </div>
            <div className="mt-6 pt-4 border-t border-slate-800/80 flex items-center justify-between text-xs text-amber-400 font-bold">
              <span>Mandi Monitor</span>
              <ArrowRight className="w-4 h-4" />
            </div>
          </div>

          <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 hover:border-emerald-500/50 transition-all flex flex-col justify-between">
            <div>
              <div className="w-12 h-12 rounded-2xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 flex items-center justify-center mb-4">
                <Sliders className="w-6 h-6" />
              </div>
              <h4 className="text-lg font-extrabold text-white mb-2">AI Load Re-Balancing</h4>
              <p className="text-xs text-slate-400 leading-relaxed">
                Adjust capacity limits dynamically or auto-reroute farmer slot bookings away from overloaded mandis to nearby underutilized procurement hubs.
              </p>
            </div>
            <div className="mt-6 pt-4 border-t border-slate-800/80 flex items-center justify-between text-xs text-emerald-400 font-bold">
              <span>Dynamic Rerouting</span>
              <ArrowRight className="w-4 h-4" />
            </div>
          </div>

          <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 hover:border-sky-500/50 transition-all flex flex-col justify-between">
            <div>
              <div className="w-12 h-12 rounded-2xl bg-sky-500/10 border border-sky-500/20 text-sky-400 flex items-center justify-center mb-4">
                <Activity className="w-6 h-6" />
              </div>
              <h4 className="text-lg font-extrabold text-white mb-2">Audit Logs & Disbursal</h4>
              <p className="text-xs text-slate-400 leading-relaxed">
                Full transparency into every digital token issued, weighing record, crop quality report, and direct-benefit transfer payment status.
              </p>
            </div>
            <div className="mt-6 pt-4 border-t border-slate-800/80 flex items-center justify-between text-xs text-sky-400 font-bold">
              <span>Audit Console</span>
              <ArrowRight className="w-4 h-4" />
            </div>
          </div>
        </div>
      </section>

      {/* Interconnected Portals Gateway Section */}
      <section className="py-10 px-4 max-w-5xl mx-auto w-full text-left">
        <div className="p-8 rounded-3xl bg-gradient-to-r from-slate-900 via-slate-900 to-sky-950/30 border border-slate-800 flex flex-col md:flex-row items-center justify-between gap-6">
          <div>
            <div className="flex items-center gap-2 text-amber-400 font-bold text-xs uppercase tracking-wider mb-2">
              <Layers className="w-4 h-4" />
              <span>INTERCONNECTED SYSTEM PORTALS</span>
            </div>
            <h3 className="text-xl sm:text-2xl font-black text-white">Switch to Other System Roles</h3>
            <p className="text-xs text-slate-400 mt-1 max-w-xl">
              Navigate directly to the Farmer procurement interface or the Mandi Owner operator control room.
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
              to="/owner"
              className="flex-1 md:flex-none px-4 py-2.5 rounded-xl bg-slate-900 hover:bg-slate-800 border border-slate-700 text-amber-300 font-bold text-xs flex items-center justify-center gap-2"
            >
              <Building2 className="w-4 h-4" />
              <span>Mandi Owner</span>
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
};

export default AdminLanding;
