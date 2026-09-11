import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  Sprout, Building2, ShieldCheck, ArrowRight, MapPin, Ticket,
  Clock, Calendar, Bell, BarChart3, Smartphone, Users, CheckCircle2,
  Zap, Bot, Navigation, ChevronRight, Leaf, Star, TrendingUp,
  Phone, Globe, Award, ChevronDown
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';

/* ─────────────────────────── helpers ─────────────────────────── */
const FEATURES = [
  {
    icon: MapPin,
    color: 'emerald',
    title: 'Smart Centre Recommendation',
    desc: 'AI-powered routing recommends the best procurement centre based on distance, queue length, and real-time traffic—so you spend less time waiting.'
  },
  {
    icon: TrendingUp,
    color: 'blue',
    title: 'Multi-Centre Load Balancing',
    desc: 'Live load monitoring across all mandis. Farmers are guided toward centres with available capacity, preventing dangerous overcrowding.'
  },
  {
    icon: Calendar,
    color: 'violet',
    title: 'Advance Slot Booking',
    desc: 'Reserve a time slot days in advance. Arrive at your scheduled time—no more sleeping overnight at the mandi gate.'
  },
  {
    icon: Ticket,
    color: 'amber',
    title: 'Digital Token Generation',
    desc: 'Every booking generates a secure, scannable QR-based digital token that replaces paper chits and eliminates forgery.'
  },
  {
    icon: Clock,
    color: 'rose',
    title: 'Real-Time Queue Tracking',
    desc: 'See your live queue position and exact estimated wait time from your phone—no need to stand in line and guess.'
  },
  {
    icon: Navigation,
    color: 'cyan',
    title: 'Smart Departure Alerts',
    desc: 'The system calculates your optimal departure time based on queue position and travel time, so you arrive just in time.'
  },
  {
    icon: Bot,
    color: 'indigo',
    title: 'AI Voice Notifications',
    desc: 'Automated voice calls in Telugu, Hindi, and English keep farmers informed—even those without smartphones.'
  },
  {
    icon: Building2,
    color: 'teal',
    title: 'Centre Management Console',
    desc: 'Procurement centre owners get a full dashboard: queue control, weighment, quality grading, and payment initiation—all in one place.'
  },
  {
    icon: BarChart3,
    color: 'orange',
    title: 'Analytics & AI Monitoring',
    desc: 'District administrators see live heatmaps, queue analytics, anomaly detection, AI predictions, and emergency closure management.'
  }
];

const STEPS = [
  { step: '01', label: 'Register as a Farmer', sub: 'Create your account with your mobile number and village details' },
  { step: '02', label: 'Add Your Crop Details', sub: 'Enter your crop type, variety, and expected harvest quantity' },
  { step: '03', label: 'Find Procurement Centres', sub: 'See all nearby mandis with live capacity and queue information' },
  { step: '04', label: 'Get Smart Recommendation', sub: 'Receive an AI-powered suggestion for the best centre near you' },
  { step: '05', label: 'Book Your Slot', sub: 'Choose a date and time window that works for you' },
  { step: '06', label: 'Receive Digital Token', sub: 'Get your QR-coded procurement token instantly on your phone' },
  { step: '07', label: 'Track Your Queue', sub: 'Monitor your live position and get smart departure alerts' },
  { step: '08', label: 'Complete Procurement & Payment', sub: 'Arrive at the right time, complete weighment, and receive your MSP payment via DBT' }
];

const STEP_COLORS = [
  'bg-emerald-600', 'bg-emerald-600',
  'bg-teal-600', 'bg-teal-600',
  'bg-blue-600', 'bg-blue-600',
  'bg-violet-600', 'bg-violet-600'
];

/* ─────────────────────────── component ─────────────────────────── */
export const LandingPage = () => {
  const { isAuthenticated, userRole } = useAuth();
  const navigate = useNavigate();

  // If already authenticated, send to appropriate dashboard
  const handleGetStarted = () => {
    if (isAuthenticated) {
      if (userRole === 'ADMIN') navigate('/admin/dashboard');
      else if (userRole === 'OWNER') navigate('/owner/dashboard');
      else navigate('/farmer');
    } else {
      navigate('/login');
    }
  };

  return (
    <div className="min-h-screen bg-white text-slate-900">

      {/* ══════════════════════════ HERO ══════════════════════════ */}
      <section className="relative overflow-hidden bg-gradient-to-br from-emerald-950 via-emerald-900 to-teal-900 text-white">
        {/* Glow orbs */}
        <div className="absolute top-0 left-1/4 w-96 h-96 bg-emerald-500/10 rounded-full blur-3xl -translate-y-1/2 pointer-events-none" />
        <div className="absolute bottom-0 right-1/4 w-96 h-96 bg-teal-400/10 rounded-full blur-3xl translate-y-1/2 pointer-events-none" />

        <div className="relative max-w-6xl mx-auto px-5 pt-20 pb-28 flex flex-col items-center text-center gap-8">
          {/* Govt badge */}
          <div className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-white/10 border border-white/20 text-emerald-200 text-xs font-semibold tracking-wide">
            <Award className="w-3.5 h-3.5 text-amber-400" />
            National Agricultural Procurement Initiative
          </div>

          {/* Icon + Title */}
          <div className="flex flex-col items-center gap-4">
            <div className="w-16 h-16 rounded-3xl bg-emerald-600/60 border border-emerald-500/40 flex items-center justify-center shadow-2xl">
              <Sprout className="w-9 h-9 text-emerald-200" />
            </div>
            <h1 className="text-4xl sm:text-5xl lg:text-6xl font-black tracking-tight leading-tight">
              Smart Procurement
              <br />
              <span className="text-transparent bg-clip-text bg-gradient-to-r from-emerald-300 via-teal-300 to-cyan-300">
                Platform
              </span>
            </h1>
          </div>

          <p className="text-lg sm:text-xl text-emerald-100 max-w-2xl leading-relaxed font-light">
            Connecting farmers directly to government procurement centres—with intelligent slot booking, real-time queue tracking, and guaranteed MSP payments.
          </p>

          <p className="text-sm text-emerald-300/80 max-w-xl">
            No more overnight waits. No paper chits. No uncertainty. The entire procurement journey—from registration to payment—digitised and simplified.
          </p>

          {/* CTAs */}
          <div className="flex flex-wrap items-center justify-center gap-4 mt-2">
            <button
              onClick={handleGetStarted}
              className="inline-flex items-center gap-2 px-8 py-3.5 rounded-2xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-black text-sm shadow-lg transition-all duration-200 hover:-translate-y-0.5 cursor-pointer"
            >
              <Zap className="w-4 h-4" />
              Get Started
            </button>
            <a
              href="#access"
              className="inline-flex items-center gap-2 px-8 py-3.5 rounded-2xl border border-white/30 hover:border-white/60 bg-white/5 hover:bg-white/10 text-white font-bold text-sm transition-all duration-200"
            >
              <ChevronDown className="w-4 h-4" />
              Login Options
            </a>
          </div>

          {/* Stats */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-6 w-full max-w-3xl mt-4 pt-8 border-t border-white/10">
            {[
              { n: '9+', label: 'Platform Features' },
              { n: '3', label: 'User Portals' },
              { n: '3', label: 'Languages Supported' },
              { n: '24/7', label: 'AI-Powered Alerts' },
            ].map(s => (
              <div key={s.label} className="flex flex-col items-center gap-1">
                <span className="text-2xl sm:text-3xl font-black text-emerald-300">{s.n}</span>
                <span className="text-[11px] text-emerald-400/80 text-center">{s.label}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Wave */}
        <div className="absolute bottom-0 left-0 right-0">
          <svg viewBox="0 0 1440 60" className="w-full fill-white" preserveAspectRatio="none">
            <path d="M0,60 C360,0 1080,0 1440,60 L1440,60 L0,60 Z" />
          </svg>
        </div>
      </section>

      {/* ══════════════════════ USER ACCESS ══════════════════════ */}
      <section id="access" className="max-w-5xl mx-auto px-5 py-20">
        <div className="text-center mb-14">
          <span className="inline-block px-3 py-1 rounded-full bg-emerald-100 text-emerald-800 text-xs font-bold uppercase tracking-wider mb-4">
            Choose Your Portal
          </span>
          <h2 className="text-3xl sm:text-4xl font-black tracking-tight">
            One platform. Three portals.
          </h2>
          <p className="text-slate-500 mt-3 max-w-lg mx-auto text-sm leading-relaxed">
            Each user type has a dedicated access point tailored to their role and responsibilities.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">

          {/* Farmer */}
          <div className="group flex flex-col rounded-3xl border-2 border-emerald-200 bg-gradient-to-b from-emerald-50 to-white p-7 shadow-sm hover:shadow-xl hover:border-emerald-400 hover:-translate-y-1 transition-all duration-300 relative">
            <span className="absolute top-4 right-4 px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-700 text-[10px] font-black uppercase tracking-wider">
              Farmers
            </span>
            <div className="w-12 h-12 rounded-2xl bg-emerald-600 flex items-center justify-center mb-5 shadow-md">
              <Sprout className="w-6 h-6 text-white" />
            </div>
            <h3 className="text-xl font-black text-slate-900 mb-2">Farmer Portal</h3>
            <p className="text-slate-500 text-sm leading-relaxed mb-6 flex-1">
              Register, add your crops, discover centres, book slots, track your queue, and receive your MSP payment—all from your mobile.
            </p>
            <div className="flex flex-col gap-2.5">
              <Link to="/login" className="flex items-center justify-between px-5 py-3 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-sm transition-all">
                <span>Farmer Login</span>
                <ArrowRight className="w-4 h-4" />
              </Link>
              <Link to="/register" className="flex items-center justify-between px-5 py-3 rounded-xl border border-emerald-300 hover:bg-emerald-50 text-emerald-800 font-bold text-sm transition-all">
                <span>New Registration</span>
                <ChevronRight className="w-4 h-4" />
              </Link>
            </div>
          </div>

          {/* Owner */}
          <div className="group flex flex-col rounded-3xl border-2 border-amber-200 bg-gradient-to-b from-amber-50 to-white p-7 shadow-sm hover:shadow-xl hover:border-amber-400 hover:-translate-y-1 transition-all duration-300 relative">
            <span className="absolute top-4 right-4 px-2 py-0.5 rounded-full bg-amber-100 text-amber-700 text-[10px] font-black uppercase tracking-wider">
              Centre Owners
            </span>
            <div className="w-12 h-12 rounded-2xl bg-amber-500 flex items-center justify-center mb-5 shadow-md">
              <Building2 className="w-6 h-6 text-white" />
            </div>
            <h3 className="text-xl font-black text-slate-900 mb-2">Owner Portal</h3>
            <p className="text-slate-500 text-sm leading-relaxed mb-6 flex-1">
              Manage your procurement centre's queue, process farmer arrivals, record weighment and quality checks, and initiate DBT payments.
            </p>
            <div className="flex flex-col gap-2.5">
              <Link to="/owner/login" className="flex items-center justify-between px-5 py-3 rounded-xl bg-amber-500 hover:bg-amber-400 text-white font-bold text-sm transition-all">
                <span>Owner Login</span>
                <ArrowRight className="w-4 h-4" />
              </Link>
              <div className="px-5 py-2.5 rounded-xl border border-amber-200 bg-amber-50 text-amber-700 text-xs text-center font-medium">
                Access via authorised credentials
              </div>
            </div>
          </div>

          {/* Admin */}
          <div className="group flex flex-col rounded-3xl border-2 border-slate-200 bg-gradient-to-b from-slate-50 to-white p-7 shadow-sm hover:shadow-xl hover:border-slate-400 hover:-translate-y-1 transition-all duration-300 relative">
            <span className="absolute top-4 right-4 px-2 py-0.5 rounded-full bg-slate-100 text-slate-700 text-[10px] font-black uppercase tracking-wider">
              District Admin
            </span>
            <div className="w-12 h-12 rounded-2xl bg-slate-800 flex items-center justify-center mb-5 shadow-md">
              <ShieldCheck className="w-6 h-6 text-amber-300" />
            </div>
            <h3 className="text-xl font-black text-slate-900 mb-2">Administrator Portal</h3>
            <p className="text-slate-500 text-sm leading-relaxed mb-6 flex-1">
              District-level oversight: monitor all centres in real-time, review AI insights, manage emergencies, and approve procurement centre registrations.
            </p>
            <div className="flex flex-col gap-2.5">
              <Link to="/admin/login" className="flex items-center justify-between px-5 py-3 rounded-xl bg-slate-800 hover:bg-slate-700 text-amber-300 font-bold text-sm transition-all">
                <span>Admin Login</span>
                <ArrowRight className="w-4 h-4" />
              </Link>
              <div className="px-5 py-2.5 rounded-xl border border-slate-200 bg-slate-50 text-slate-500 text-xs text-center font-medium">
                Restricted to district officials
              </div>
            </div>
          </div>

        </div>
      </section>

      {/* ══════════════════════ FEATURES ══════════════════════ */}
      <section className="bg-slate-950 text-white py-24">
        <div className="max-w-6xl mx-auto px-5">
          <div className="text-center mb-14">
            <span className="inline-block px-3 py-1 rounded-full bg-emerald-900/60 text-emerald-400 text-xs font-bold uppercase tracking-wider border border-emerald-800 mb-4">
              Platform Capabilities
            </span>
            <h2 className="text-3xl sm:text-4xl font-black tracking-tight">
              Everything the procurement process needs
            </h2>
            <p className="text-slate-400 mt-3 max-w-xl mx-auto text-sm leading-relaxed">
              From a farmer's first registration to the final payment deposit—every step is covered by an intelligent, interconnected system.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
            {FEATURES.map((f) => {
              const Icon = f.icon;
              const iconColorClass = {
                emerald: 'text-emerald-400', blue: 'text-blue-400', violet: 'text-violet-400',
                amber: 'text-amber-400', rose: 'text-rose-400', cyan: 'text-cyan-400',
                indigo: 'text-indigo-400', teal: 'text-teal-400', orange: 'text-orange-400'
              }[f.color];
              return (
                <div key={f.title} className="flex flex-col gap-3 p-6 rounded-2xl bg-slate-900 border border-slate-800 hover:border-slate-600 hover:bg-slate-800/80 transition-all duration-200">
                  <div className="w-10 h-10 rounded-xl bg-slate-800 border border-slate-700 flex items-center justify-center">
                    <Icon className={`w-5 h-5 ${iconColorClass}`} />
                  </div>
                  <h3 className="font-bold text-white text-sm">{f.title}</h3>
                  <p className="text-slate-400 text-xs leading-relaxed">{f.desc}</p>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      {/* ══════════════════════ HOW IT WORKS ══════════════════════ */}
      <section className="max-w-5xl mx-auto px-5 py-24">
        <div className="text-center mb-14">
          <span className="inline-block px-3 py-1 rounded-full bg-blue-100 text-blue-800 text-xs font-bold uppercase tracking-wider mb-4">
            Farmer Journey
          </span>
          <h2 className="text-3xl sm:text-4xl font-black tracking-tight text-slate-900">
            From farm to payment in 8 steps
          </h2>
          <p className="text-slate-500 mt-3 max-w-lg mx-auto text-sm leading-relaxed">
            The entire procurement process made transparent, predictable, and stress-free.
          </p>
        </div>

        <div className="flex flex-col gap-4">
          {STEPS.map((s, i) => {
            const isLast = i === STEPS.length - 1;
            return (
              <div key={s.step} className="flex items-start gap-4 group">
                <div className={`flex-shrink-0 w-13 h-13 w-12 h-12 rounded-2xl flex items-center justify-center font-black text-base text-white shadow-md group-hover:scale-105 transition-transform duration-200 ${STEP_COLORS[i]}`}>
                  {s.step}
                </div>
                <div className={`flex-1 p-4 rounded-2xl border transition-all duration-200 ${isLast ? 'bg-emerald-50 border-emerald-200' : 'bg-white border-slate-200 hover:border-slate-300 hover:shadow-sm'}`}>
                  <div className="flex items-center gap-2 mb-0.5">
                    <h3 className="font-bold text-slate-900 text-sm">{s.label}</h3>
                    {isLast && <span className="px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-700 text-[10px] font-extrabold">✓ Final Step</span>}
                  </div>
                  <p className="text-slate-500 text-xs leading-relaxed">{s.sub}</p>
                </div>
              </div>
            );
          })}
        </div>

        <div className="mt-14 text-center">
          <p className="text-slate-600 text-sm mb-5 font-medium">Ready to simplify your procurement experience?</p>
          <div className="flex flex-wrap items-center justify-center gap-3">
            <Link
              to="/register"
              className="inline-flex items-center gap-2 px-7 py-3 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-sm shadow-md hover:-translate-y-0.5 transition-all"
            >
              <Sprout className="w-4 h-4" />
              Register as a Farmer
            </Link>
            <Link
              to="/login"
              className="inline-flex items-center gap-2 px-7 py-3 rounded-xl border border-slate-300 hover:border-slate-400 text-slate-700 font-bold text-sm hover:bg-slate-50 transition-all"
            >
              Already registered? Login
            </Link>
          </div>
        </div>
      </section>

      {/* ══════════════════════ TRUST ══════════════════════ */}
      <section className="bg-gradient-to-br from-emerald-50 to-teal-50 border-y border-emerald-100 py-16">
        <div className="max-w-5xl mx-auto px-5">
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-8 text-center">
            {[
              { icon: Globe, title: 'Multi-Language Support', desc: 'Available in Telugu, Hindi, and English—with AI voice calls for farmers without smartphones.' },
              { icon: ShieldCheck, title: 'Government Backed', desc: 'Built on MSP framework compliance. All payments processed via Direct Benefit Transfer (DBT).' },
              { icon: Smartphone, title: 'Works on Any Device', desc: 'Optimised for low-end smartphones with minimal data usage—designed for rural connectivity.' }
            ].map(item => {
              const Icon = item.icon;
              return (
                <div key={item.title} className="flex flex-col items-center gap-3 p-6">
                  <div className="w-12 h-12 rounded-2xl bg-emerald-100 flex items-center justify-center">
                    <Icon className="w-6 h-6 text-emerald-700" />
                  </div>
                  <h3 className="font-bold text-slate-900 text-sm">{item.title}</h3>
                  <p className="text-slate-500 text-xs leading-relaxed">{item.desc}</p>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      {/* ══════════════════════ FOOTER ══════════════════════ */}
      <footer className="bg-slate-950 text-slate-400 py-14">
        <div className="max-w-6xl mx-auto px-5">
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-10 mb-12">

            <div className="col-span-1 sm:col-span-2 lg:col-span-1 flex flex-col gap-3">
              <div className="flex items-center gap-2">
                <div className="w-9 h-9 rounded-xl bg-emerald-700 flex items-center justify-center">
                  <Sprout className="w-5 h-5 text-white" />
                </div>
                <div>
                  <div className="font-extrabold text-sm text-white leading-tight">Smart Procurement</div>
                  <div className="text-[11px] text-emerald-400">Platform</div>
                </div>
              </div>
              <p className="text-xs leading-relaxed text-slate-500">
                Digitising agricultural procurement for Indian farmers—from slot booking to MSP payment, entirely on your phone.
              </p>
            </div>

            <div>
              <h4 className="text-xs font-extrabold uppercase tracking-wider text-white mb-4">Farmer</h4>
              <ul className="flex flex-col gap-2.5 text-xs">
                <li><Link to="/login" className="hover:text-emerald-400 transition-colors">Farmer Login</Link></li>
                <li><Link to="/register" className="hover:text-emerald-400 transition-colors">New Registration</Link></li>
                <li><Link to="/find-centres" className="hover:text-emerald-400 transition-colors">Find Centres</Link></li>
              </ul>
            </div>

            <div>
              <h4 className="text-xs font-extrabold uppercase tracking-wider text-white mb-4">Procurement Centre</h4>
              <ul className="flex flex-col gap-2.5 text-xs">
                <li><Link to="/owner/login" className="hover:text-amber-400 transition-colors">Owner Login</Link></li>
                <li><Link to="/admin/login" className="hover:text-slate-200 transition-colors">District Administrator</Link></li>
              </ul>
            </div>

            <div>
              <h4 className="text-xs font-extrabold uppercase tracking-wider text-white mb-4">Support</h4>
              <ul className="flex flex-col gap-2.5 text-xs">
                <li className="flex items-center gap-2">
                  <Phone className="w-3.5 h-3.5 text-emerald-500" />
                  <span>Toll-Free: 1800-XXX-XXXX</span>
                </li>
                <li className="flex items-center gap-2">
                  <Globe className="w-3.5 h-3.5 text-emerald-500" />
                  <span>Mon–Sat, 8AM–8PM</span>
                </li>
                <li className="text-slate-600 text-[11px] mt-1">Telugu · Hindi · English</li>
              </ul>
            </div>
          </div>

          <div className="pt-6 border-t border-slate-800 flex flex-col sm:flex-row items-center justify-between gap-3 text-[11px] text-slate-600">
            <span>© {new Date().getFullYear()} Smart Procurement Platform. All rights reserved.</span>
            <span>National Agricultural Procurement Initiative · Government of India</span>
          </div>
        </div>
      </footer>

    </div>
  );
};

export default LandingPage;
