import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useLanguage, SUPPORTED_LANGUAGES } from '../context/LanguageContext';
import { notificationService } from '../services/api';

export const Navbar = () => {
  const { farmer, userRole, logout, isAuthenticated, switchRole, login } = useAuth();
  const { language, setLanguage, t } = useLanguage();
  const [unreadCount, setUnreadCount] = useState(0);
  const [showLangDropdown, setShowLangDropdown] = useState(false);
  const navigate = useNavigate();

  const handlePortalSwitch = async (portal) => {
    if (portal === 'FARMER') {
      switchRole('FARMER');
      navigate(isAuthenticated ? '/' : '/login');
    } else if (portal === 'OWNER') {
      switchRole('OWNER');
      navigate('/owner/login');
    } else if (portal === 'ADMIN') {
      switchRole('ADMIN');
      navigate('/admin/login');
    }
  };

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
      <div className="max-w-5xl mx-auto px-4 py-2.5 flex items-center justify-between gap-2">
        {/* Brand Header */}
        <Link to="/" className="flex items-center gap-2 text-left">
          <div>
            <h1 className="font-extrabold text-sm sm:text-base tracking-tight text-white leading-tight">
              {t('common.appName')}
            </h1>
            <p className="text-[10px] sm:text-[11px] text-emerald-300 font-medium tracking-wide">
              Smart Mandi Procurement System
            </p>
          </div>
        </Link>

        {/* Header Actions */}
        <div className="flex items-center gap-2">
          {/* Interconnected System Role Portal Switcher */}
          <div className="flex items-center gap-1 p-0.5 bg-emerald-900/90 rounded-xl border border-emerald-800 text-[10px] sm:text-[11px] font-extrabold">
            <button
              onClick={() => handlePortalSwitch('FARMER')}
              className={`px-2 py-1 sm:px-2.5 rounded-lg transition-colors cursor-pointer ${userRole === 'FARMER' ? 'bg-emerald-600 text-white shadow-xs' : 'text-emerald-200 hover:text-white'}`}
            >
              🌾 Farmer
            </button>
            <button
              onClick={() => handlePortalSwitch('OWNER')}
              className={`px-2 py-1 sm:px-2.5 rounded-lg transition-colors cursor-pointer ${userRole === 'OWNER' ? 'bg-amber-500 text-slate-950 shadow-xs' : 'text-emerald-200 hover:text-white'}`}
            >
              🏬 Owner
            </button>
            <button
              onClick={() => handlePortalSwitch('ADMIN')}
              className={`px-2 py-1 sm:px-2.5 rounded-lg transition-colors cursor-pointer ${userRole === 'ADMIN' ? 'bg-slate-900 text-amber-300 shadow-xs' : 'text-emerald-200 hover:text-white'}`}
            >
              🛡️ Admin
            </button>
          </div>

          {/* Global Language Selector Dropdown */}
          <div className="relative">
            <button
              onClick={() => setShowLangDropdown(!showLangDropdown)}
              className="flex items-center px-3 py-1.5 rounded-xl bg-emerald-900/80 hover:bg-emerald-900 border border-emerald-700/60 text-xs font-semibold text-emerald-100 transition-all cursor-pointer shadow-xs"
              title="Select Language"
            >
              <span className="font-bold">{currentLangObj.native}</span>
            </button>

            {showLangDropdown && (
              <div className="absolute right-0 mt-2 w-44 rounded-2xl bg-white text-slate-900 shadow-xl border border-slate-200 py-1.5 z-50 animate-in fade-in slide-in-from-top-2">
                <div className="px-3 py-1.5 border-b border-slate-100 font-bold text-[11px] uppercase tracking-wider text-slate-500">
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

          {/* Notifications Link */}
          {isAuthenticated && (
            <Link
              to="/notifications"
              className="relative px-3 py-1.5 rounded-xl bg-emerald-900/80 hover:bg-emerald-900 border border-emerald-700/60 text-xs font-bold text-emerald-200 hover:text-white transition-all flex items-center gap-1"
              title={t('nav.notifications')}
            >
              <span>Notifications</span>
              {unreadCount > 0 && (
                <span className="ml-1 px-1.5 py-0.5 text-[10px] font-extrabold bg-amber-500 text-white rounded-full">
                  {unreadCount}
                </span>
              )}
            </Link>
          )}

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
                className="flex items-center px-3 py-1.5 rounded-xl bg-rose-900/40 hover:bg-rose-900/70 border border-rose-700/60 text-xs font-bold text-rose-200 transition-all cursor-pointer"
                title="Logout"
              >
                Logout
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

