import React from 'react';
import { NavLink } from 'react-router-dom';
import { Home, Calendar, Clock, CreditCard, User } from 'lucide-react';
import { useLanguage } from '../context/LanguageContext';

export const BottomNavigation = () => {
  const { t } = useLanguage();

  const navItems = [
    { to: '/', label: t('nav.home'), icon: Home },
    { to: '/find-centres', label: t('nav.myBookings'), icon: Calendar },
    { to: '/queue', label: t('nav.queue'), icon: Clock },
    { to: '/payment', label: t('nav.payments'), icon: CreditCard },
    { to: '/profile', label: t('nav.profile'), icon: User },
  ];

  return (
    <nav className="md:hidden fixed bottom-0 left-0 right-0 z-40 bg-white border-t border-slate-200 shadow-lg px-2 py-1.5">
      <div className="flex items-center justify-around max-w-md mx-auto">
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) => `
                flex flex-col items-center justify-center py-1 px-2 min-w-[56px] min-h-[48px] rounded-xl text-[11px] font-medium transition-all
                ${isActive
                  ? 'text-emerald-800 font-bold bg-emerald-50 scale-105'
                  : 'text-slate-500 hover:text-slate-800 hover:bg-slate-50'
                }
              `}
            >
              <Icon className="w-5 h-5 mb-0.5 shrink-0" />
              <span className="truncate max-w-[64px] text-center">{item.label}</span>
            </NavLink>
          );
        })}
      </div>
    </nav>
  );
};

export default BottomNavigation;
