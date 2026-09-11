import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import {
  Sprout,
  Ticket,
  MapPin,
  Clock,
  Calendar,
  CreditCard,
  Bell,
  TrendingUp,
  PlusCircle,
  Navigation,
  AlertCircle
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useLanguage } from '../context/LanguageContext';
import Card from '../components/Card';
import Button from '../components/Button';
import StatusBadge from '../components/StatusBadge';
import { handleStartTravelling } from '../services/navigationService';

export const Home = () => {
  const { farmer, activeBooking } = useAuth();
  const { t } = useLanguage();
  const [navLoading, setNavLoading] = useState(false);
  const [navError, setNavError] = useState('');

  const onNavClick = () => {
    setNavError('');
    handleStartTravelling({
      booking: activeBooking,
      onStartLoading: () => setNavLoading(true),
      onEndLoading: () => setNavLoading(false),
      onError: (msg) => setNavError(msg)
    });
  };

  return (
    <div className="max-w-4xl mx-auto px-4 py-6 flex flex-col gap-6">
      {/* Welcome Banner */}
      <div className="bg-gradient-to-r from-emerald-900 via-emerald-800 to-teal-900 text-white rounded-3xl p-6 shadow-md relative overflow-hidden text-left">
        <div className="relative z-10">
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-700/80 text-emerald-100 text-xs font-semibold mb-3 border border-emerald-500/40">
            <Sprout className="w-3.5 h-3.5" /> {t('common.appName')}
          </span>
          <h2 className="text-2xl sm:text-3xl font-extrabold tracking-tight">
            {t('onboarding.welcome')}, {farmer?.name || 'Ramesh Kumar'}
          </h2>
          <p className="text-xs sm:text-sm text-emerald-200 mt-1 flex items-center gap-1 font-medium">
            <MapPin className="w-4 h-4 text-emerald-400 shrink-0" />
            {farmer?.village || 'Kondapur Village'}, {farmer?.district || 'Medak'}, {farmer?.state || 'Telangana'}
          </p>
        </div>
      </div>

      {/* Active Token / Booking Card */}
      {activeBooking ? (
        <Card
          title={t('booking.bookingConfirmed')}
          subtitle={t('booking.tokenNumber')}
          action={
            <StatusBadge status={activeBooking.status || 'CONFIRMED'} />
          }
          className="border-2 border-emerald-600/40 shadow-sm text-left"
        >
          <div className="flex flex-col md:flex-row items-stretch gap-6">
            {/* Token Number Box */}
            <div className="bg-gradient-to-br from-emerald-800 to-emerald-950 text-white rounded-2xl p-5 flex flex-col items-center justify-center min-w-[150px] shadow-sm text-center">
              <span className="text-xs text-emerald-300 font-bold uppercase tracking-wider">{t('booking.tokenNumber')}</span>
              <span className="text-4xl font-black text-white my-1 tracking-tight">{activeBooking.tokenNumber || '#103'}</span>
              <span className="text-[11px] text-emerald-200 font-medium">Paddy (500 {t('farmer.unit')})</span>
            </div>

            {/* Token Specs Grid */}
            <div className="flex-1 grid grid-cols-2 sm:grid-cols-3 gap-4 text-left py-1">
              <div>
                <span className="text-xs font-medium text-slate-500 block">{t('booking.centre')}</span>
                <span className="font-bold text-slate-900 text-sm block truncate">
                  {activeBooking.centreName || 'ABC Procurement Centre'}
                </span>
              </div>

              <div>
                <span className="text-xs font-medium text-slate-500 block">{t('queue.yourPosition')}</span>
                <span className="font-extrabold text-amber-700 text-base block">
                  {t('queue.farmersAhead', { count: activeBooking.farmersAhead ?? 4 })}
                </span>
              </div>

              <div>
                <span className="text-xs font-medium text-slate-500 block">{t('queue.estimatedWait')}</span>
                <span className="font-bold text-slate-900 text-sm block">
                  {activeBooking.estimatedServiceTime || '11:35 AM'}
                </span>
              </div>

              <div>
                <span className="text-xs font-medium text-slate-500 block">{t('booking.timeSlot')}</span>
                <span className="font-semibold text-slate-800 text-xs block">
                  {activeBooking.slotWindow || '10:00 AM – 11:00 AM'}
                </span>
              </div>

              <div>
                <span className="text-xs font-medium text-slate-500 block">{t('centre.travelTime', { travelTime: activeBooking.travelTimeMinutes || 42 })}</span>
                <span className="font-semibold text-slate-800 text-xs block">
                  {activeBooking.travelTimeMinutes || 42} min
                </span>
              </div>

              <div>
                <span className="text-xs font-medium text-slate-500 block">{t('booking.startTravelling')}</span>
                <span className="font-bold text-emerald-800 text-xs block">
                  ~ {activeBooking.recommendedDepartureTime || '10:45 AM'}
                </span>
              </div>
            </div>
          </div>

          {navError && (
            <div className="mt-4 p-3 bg-rose-50 border border-rose-300 text-rose-900 text-xs font-semibold rounded-xl flex items-center gap-2">
              <AlertCircle className="w-4 h-4 text-rose-600 shrink-0" />
              <span>{navError}</span>
            </div>
          )}

          {/* Quick Action Bar for Token */}
          <div className="mt-5 pt-4 border-t border-slate-100 flex flex-wrap items-center justify-between gap-3">
            <Button
              size="sm"
              variant="outline"
              icon={Navigation}
              loading={navLoading}
              onClick={onNavClick}
              className="border-emerald-600 text-emerald-800 hover:bg-emerald-50 font-bold"
            >
              {t('booking.startTravelling')}
            </Button>
            <div className="flex items-center gap-2 w-full sm:w-auto">
              <Link to="/token" className="flex-1 sm:flex-initial">
                <Button size="sm" variant="secondary" icon={Ticket}>
                  {t('booking.tokenNumber')}
                </Button>
              </Link>
              <Link to="/queue" className="flex-1 sm:flex-initial">
                <Button size="sm" variant="primary" icon={Clock}>
                  {t('nav.queue')}
                </Button>
              </Link>
            </div>
          </div>
        </Card>
      ) : (
        <Card className="text-center py-8 bg-emerald-50/50 border-dashed border-2 border-emerald-300">
          <div className="w-12 h-12 rounded-full bg-emerald-100 text-emerald-800 flex items-center justify-center mx-auto mb-3">
            <Calendar className="w-6 h-6" />
          </div>
          <h3 className="font-extrabold text-slate-900 text-lg">{t('booking.bookSlotTitle')}</h3>
          <p className="text-xs text-slate-600 max-w-sm mx-auto mt-1 mb-5">
            {t('onboarding.subtitle')}
          </p>
          <Link to="/find-centres">
            <Button size="lg" icon={PlusCircle}>
              {t('booking.bookSlotTitle')}
            </Button>
          </Link>
        </Card>
      )}

      {/* Quick Action Grid */}
      <h3 className="font-bold text-slate-800 text-base sm:text-lg text-left mt-2">
        {t('nav.dashboard')}
      </h3>

      <div className="grid grid-cols-2 sm:grid-cols-3 gap-3.5 sm:gap-4">
        <Link to="/crop-details">
          <Card className="hover:border-emerald-500 hover:bg-emerald-50/30 transition-all cursor-pointer h-full text-left">
            <div className="w-10 h-10 rounded-xl bg-amber-100 text-amber-800 flex items-center justify-center mb-3">
              <Sprout className="w-5 h-5" />
            </div>
            <h4 className="font-bold text-slate-900 text-sm">{t('farmer.cropDetails')}</h4>
            <p className="text-xs text-slate-500 mt-1">{t('farmer.quantity')}</p>
          </Card>
        </Link>

        <Link to="/find-centres">
          <Card className="border-2 border-emerald-500 bg-emerald-50/20 hover:border-emerald-600 transition-all cursor-pointer h-full text-left">
            <div className="w-10 h-10 rounded-xl bg-emerald-100 text-emerald-800 flex items-center justify-center mb-3">
              <MapPin className="w-5 h-5" />
            </div>
            <h4 className="font-bold text-slate-900 text-sm">{t('booking.selectCentre')}</h4>
            <p className="text-xs text-slate-500 mt-1">{t('centre.recommendationTitle')}</p>
          </Card>
        </Link>

        <Link to="/queue">
          <Card className="hover:border-emerald-500 hover:bg-emerald-50/30 transition-all cursor-pointer h-full text-left">
            <div className="w-10 h-10 rounded-xl bg-blue-100 text-blue-800 flex items-center justify-center mb-3">
              <Clock className="w-5 h-5" />
            </div>
            <h4 className="font-bold text-slate-900 text-sm">{t('queue.title')}</h4>
            <p className="text-xs text-slate-500 mt-1">{t('queue.yourPosition')}</p>
          </Card>
        </Link>

        <Link to="/procurement-status">
          <Card className="hover:border-emerald-500 hover:bg-emerald-50/30 transition-all cursor-pointer h-full text-left">
            <div className="w-10 h-10 rounded-xl bg-teal-100 text-teal-800 flex items-center justify-center mb-3">
              <TrendingUp className="w-5 h-5" />
            </div>
            <h4 className="font-bold text-slate-900 text-sm">{t('booking.procurementCompleted')}</h4>
            <p className="text-xs text-slate-500 mt-1">{t('common.status')}</p>
          </Card>
        </Link>

        <Link to="/payment">
          <Card className="hover:border-emerald-500 hover:bg-emerald-50/30 transition-all cursor-pointer h-full text-left">
            <div className="w-10 h-10 rounded-xl bg-purple-100 text-purple-800 flex items-center justify-center mb-3">
              <CreditCard className="w-5 h-5" />
            </div>
            <h4 className="font-bold text-slate-900 text-sm">{t('nav.payments')}</h4>
            <p className="text-xs text-slate-500 mt-1">{t('booking.paymentCompleted')}</p>
          </Card>
        </Link>

        <Link to="/notifications">
          <Card className="hover:border-emerald-500 hover:bg-emerald-50/30 transition-all cursor-pointer h-full text-left">
            <div className="w-10 h-10 rounded-xl bg-rose-100 text-rose-800 flex items-center justify-center mb-3">
              <Bell className="w-5 h-5" />
            </div>
            <h4 className="font-bold text-slate-900 text-sm">{t('nav.notifications')}</h4>
            <p className="text-xs text-slate-500 mt-1">{t('notifications.title')}</p>
          </Card>
        </Link>
      </div>

      {/* Interconnected Portals Section */}
      <div className="mt-4 p-5 rounded-2xl bg-gradient-to-r from-slate-900 via-slate-800 to-slate-900 text-white shadow-lg text-left border border-slate-800 flex flex-col sm:flex-row items-center justify-between gap-4">
        <div>
          <span className="text-[10px] font-black uppercase tracking-wider text-amber-400 block">SYSTEM ROLE GATEWAYS</span>
          <h4 className="font-black text-white text-base mt-0.5">Switch to Other System Portals</h4>
          <p className="text-xs text-slate-300 mt-0.5">Access Mandi Operator control room or District Authority console.</p>
        </div>
        <div className="flex items-center gap-2 w-full sm:w-auto">
          <Link to="/owner" className="flex-1 sm:flex-initial px-3.5 py-2 rounded-xl bg-amber-500 hover:bg-amber-400 text-slate-950 font-black text-xs flex items-center justify-center gap-1.5 shadow-xs">
            🏬 Mandi Owner
          </Link>
          <Link to="/admin" className="flex-1 sm:flex-initial px-3.5 py-2 rounded-xl bg-slate-950 hover:bg-slate-900 border border-slate-700 text-amber-300 font-black text-xs flex items-center justify-center gap-1.5 shadow-xs">
            🛡️ District Admin
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Home;
