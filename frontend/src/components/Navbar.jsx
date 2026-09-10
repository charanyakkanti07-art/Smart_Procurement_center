import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Sprout, Bell, Globe, User, LogOut, ChevronDown } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useLanguage, SUPPORTED_LANGUAGES } from '../context/LanguageContext';
import { notificationService } from '../services/api';

export const Navbar = () => {
  const { farmer, userRole, logout, isAuthenticated } = useAuth();
  const { language, setLanguage, t } = useLanguage();
  const [unreadCount, setUnreadCount] = useState(0);
  const [showLangDropdown, setShowLangDropdown] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (isAuthenticated) {
      fetchUnreadCount();
      const interval = setInterval(fetchUnreadCount, 15000);
      return () => clearInterval(interval);
    }
  }, [isAuthenticated, farmer?.farmerId]);

  const fetchUnreadCount = async () => {
    try {
      const count = await notificationService.getUnreadCount(farmer?.farmerId || 1);
      setUnreadCount(count);
    } catch (err) {
      console.error(err);
    }
  };

  const currentLangObj = SUPPORTED_LANGUAGES.find((l) => l.code === language) || SUPPORTED_LANGUAGES[1];

  const handleSelectLanguage = (code) => {
    setLanguage(code);
    setShowLangDropdown(false);
  };

  return (
    <header className="sticky top-0 z-40 bg-emerald-950 text-white shadow-md border-b border-emerald-900">
      <div className="max-w-5xl mx-auto px-4 py-3 flex items-center justify-between gap-2">
        {/* Brand Header */}
        <Link to="/" className="flex items-center gap-2.5 group text-left">
          <div className="w-10 h-10 rounded-xl bg-emerald-700 flex items-center justify-center text-white shadow-inner group-hover:scale-105 transition-transform">
            <Sprout className="w-6 h-6 text-emerald-200" />
          </div>
          <div>
            <h1 className="font-extrabold text-base sm:text-lg tracking-tight text-white leading-tight">
              {t('common.appName')}
            </h1>
            <p className="text-[10px] sm:text-xs text-emerald-300 font-medium tracking-wide">
              {t('onboarding.welcome')}
            </p>
          </div>
        </Link>

        {/* Header Actions */}
        <div className="flex items-center gap-2">
          {/* Global Language Selector Dropdown */}
          <div className="relative">
            <button
              onClick={() => setShowLangDropdown(!showLangDropdown)}
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-emerald-900/80 hover:bg-emerald-900 border border-emerald-700/60 text-xs font-semibold text-emerald-100 transition-all cursor-pointer shadow-xs"
              title="Select Language / భాషను ఎంచుకోండి"
            >
              <Globe className="w-4 h-4 text-emerald-400" />
              <span className="font-bold">{currentLangObj.native}</span>
              <ChevronDown className="w-3.5 h-3.5 text-emerald-300" />
            </button>

            {showLangDropdown && (
              <div className="absolute right-0 mt-2 w-44 rounded-2xl bg-white text-slate-900 shadow-xl border border-slate-200 py-1.5 z-50 animate-in fade-in slide-in-from-top-2">
                <div className="px-3 py-1.5 border-b border-slate-100 font-bold text-[11px] uppercase tracking-wider text-slate-500 flex items-center gap-1.5">
                  <Globe className="w-3.5 h-3.5 text-emerald-600" />
                  {t('common.chooseLanguage')}
                </div>
                {SUPPORTED_LANGUAGES.map((lang) => {
                  const isSelected = language === lang.code;
                  return (
                    <button
                      key={lang.code}
                      onClick={() => handleSelectLanguage(lang.code)}
                      className={`w-full px-3 py-2 text-left text-xs flex items-center justify-between transition-colors cursor-pointer ${
                        isSelected
                          ? 'bg-emerald-50 text-emerald-800 font-extrabold border-l-4 border-emerald-600'
                          : 'hover:bg-slate-50 text-slate-700 font-medium'
                      }`}
                    >
                      <span className="text-sm font-bold">{lang.native}</span>
                      <span className="text-[10px] text-slate-400 font-medium">{lang.name}</span>
                    </button>
                  );
                })}
              </div>
            )}
          </div>

          {/* Notifications Icon */}
          {isAuthenticated && (
            <Link
              to="/notifications"
              className="relative p-2 rounded-xl bg-emerald-900/80 hover:bg-emerald-900 border border-emerald-700/60 text-emerald-200 hover:text-white transition-all"
              title={t('nav.notifications')}
            >
              <Bell className="w-4 h-4" />
              {unreadCount > 0 && (
                <span className="absolute -top-1 -right-1 px-1.5 py-0.5 min-w-[18px] text-[10px] font-extrabold bg-amber-500 text-white rounded-full flex items-center justify-center ring-2 ring-emerald-950">
                  {unreadCount}
                </span>
              )}
            </Link>
          )}

          {/* District Admin Quick Link */}
          <Link
            to="/admin/dashboard"
            className="hidden md:flex items-center gap-1 px-2.5 py-1.5 rounded-xl bg-slate-900 hover:bg-slate-800 border border-slate-700 text-xs font-bold text-amber-400 transition-all cursor-pointer"
            title="District Admin Portal"
          >
            Admin
          </Link>

          {/* Profile / Role Badge / Logout */}
          {isAuthenticated ? (
            <div className="flex items-center gap-2">
              <span className="hidden sm:inline-flex items-center px-2 py-0.5 rounded-md text-[10px] font-extrabold uppercase bg-emerald-800 text-emerald-200 border border-emerald-700">
                {userRole || 'FARMER'}
              </span>
              <button
                onClick={() => {
                  logout();
                  navigate('/login');
                }}
                className="flex items-center gap-1 px-2.5 py-1.5 rounded-xl bg-rose-900/40 hover:bg-rose-900/70 border border-rose-700/60 text-xs font-bold text-rose-200 transition-all cursor-pointer"
                title="Logout"
              >
                <LogOut className="w-3.5 h-3.5" />
                <span className="hidden sm:inline">Logout</span>
              </button>
            </div>
          ) : (
            <Link
              to="/login"
              className="px-3.5 py-1.5 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-xs font-extrabold text-white shadow-xs transition-all"
            >
              {t('onboarding.loginTitle')}
            </Link>
          )}
        </div>
      </div>
    </header>
  );
};

export default Navbar;
