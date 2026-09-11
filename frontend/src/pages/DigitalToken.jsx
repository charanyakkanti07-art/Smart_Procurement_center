import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Ticket, Clock, Navigation, CheckCircle2, QrCode, ArrowRight, ShieldCheck, AlertCircle } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useLanguage } from '../context/LanguageContext';
import Card from '../components/Card';
import Button from '../components/Button';
import StatusBadge from '../components/StatusBadge';
import FarmerQRCode from '../components/FarmerQRCode';
import { handleStartTravelling } from '../services/navigationService';

export const DigitalToken = () => {
  const { activeBooking } = useAuth();
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

  const token = activeBooking || {
    tokenNumber: "#103",
    centreName: "ABC Procurement Centre",
    centreLocation: "Kondapur Main Road, Medak",
    cropType: "Paddy",
    quantityKg: 500,
    farmersAhead: 4,
    estimatedServiceTime: "11:35 AM",
    travelTimeMinutes: 42,
    recommendedDepartureTime: "10:45 AM",
    status: "WAITING",
    bookingDate: "2026-09-20",
    slotWindow: "10:00 AM – 11:00 AM",
    geofenceStatus: "NEAR_CENTRE",
    geofenceDistanceMeters: 450
  };

  return (
    <div className="max-w-md mx-auto px-4 py-6 text-left flex flex-col gap-6">
      {/* Title */}
      <div className="text-center">
        <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-100 text-emerald-900 text-xs font-bold mb-2 border border-emerald-300">
          <CheckCircle2 className="w-4 h-4 text-emerald-700" /> {t('booking.bookingConfirmed')}
        </span>
        <h2 className="text-2xl font-black text-slate-900 tracking-tight">
          {t('booking.tokenNumber')}
        </h2>
        <p className="text-xs text-slate-600">{t('booking.bookingConfirmedMsg')}</p>
      </div>

      {/* PROMINENT DIGITAL TOKEN CARD */}
      <Card className="border-4 border-emerald-700 bg-gradient-to-b from-emerald-900 via-emerald-950 to-slate-950 text-white shadow-2xl p-0 overflow-hidden relative">
        {/* Top Decorative Mandi Header */}
        <div className="bg-emerald-800/80 px-4 py-2.5 flex items-center justify-between border-b border-emerald-700/60 text-xs font-bold">
          <span className="flex items-center gap-1.5 text-emerald-200">
            <ShieldCheck className="w-4 h-4 text-emerald-400" /> {t('common.appName')}
          </span>
          <StatusBadge status={token.status || 'WAITING'} />
        </div>

        <div className="p-6 flex flex-col items-center text-center">
          <span className="text-xs font-bold text-emerald-300 uppercase tracking-widest">{t('booking.tokenNumber')}</span>

          {/* LARGE TOKEN DISPLAY */}
          <div className="my-2 py-2 px-8 rounded-2xl bg-gradient-to-r from-emerald-600 via-teal-500 to-emerald-600 text-white shadow-lg border border-emerald-400/40">
            <span className="text-5xl font-black tracking-tight">{token.tokenNumber || '#103'}</span>
          </div>

          <p className="text-sm font-semibold text-emerald-100 mt-1">
            {token.cropType} • {token.quantityKg} {t('farmer.unit')}
          </p>

          {/* Dynamic Valid QR Code Component */}
          <FarmerQRCode booking={token} size={160} className="my-4" />

          {/* Token Specs Table */}
          <div className="w-full grid grid-cols-2 gap-2 text-left bg-slate-900/90 p-3.5 rounded-2xl border border-emerald-900/60 text-xs">
            <div>
              <span className="text-[10px] text-emerald-400 font-semibold uppercase block">{t('booking.centre')}</span>
              <span className="font-bold text-white block truncate">{token.centreName}</span>
            </div>

            <div>
              <span className="text-[10px] text-emerald-400 font-semibold uppercase block">{t('queue.yourPosition')}</span>
              <span className="font-extrabold text-amber-400 text-sm block">{t('queue.farmersAhead', { count: token.farmersAhead })}</span>
            </div>

            <div>
              <span className="text-[10px] text-emerald-400 font-semibold uppercase block">{t('queue.estimatedWait', { minutes: '' }).split(':')[0]}</span>
              <span className="font-bold text-white block">{token.estimatedServiceTime}</span>
            </div>

            <div>
              <span className="text-[10px] text-emerald-400 font-semibold uppercase block">{t('booking.timeSlot')}</span>
              <span className="font-semibold text-emerald-200 block text-[11px]">{token.slotWindow}</span>
            </div>
          </div>
        </div>

        {/* SMART DEPARTURE RECOMMENDATION WIDGET */}
        <div className="bg-amber-500/20 border-t border-amber-500/40 p-4 text-left flex flex-col gap-3">
          {navError && (
            <div className="p-2.5 bg-rose-900/90 border border-rose-500 text-rose-100 text-xs font-semibold rounded-xl flex items-center gap-2">
              <AlertCircle className="w-4 h-4 text-rose-300 shrink-0" />
              <span>{navError}</span>
            </div>
          )}

          <div className="flex items-center justify-between gap-3">
            <div className="flex items-start gap-3">
              <div className="w-8 h-8 rounded-xl bg-amber-500 text-slate-950 flex items-center justify-center shrink-0 mt-0.5 shadow-sm font-bold">
                <Navigation className="w-4 h-4" />
              </div>
              <div>
                <h4 className="font-extrabold text-amber-300 text-xs uppercase tracking-wide">
                  {t('booking.startTravelling')}
                </h4>
                <p className="text-xs text-amber-100 font-medium mt-0.5 leading-relaxed">
                  {t('centre.travelTime', { travelTime: token.travelTimeMinutes })}
                </p>
              </div>
            </div>

            <Button
              size="sm"
              icon={Navigation}
              loading={navLoading}
              onClick={onNavClick}
              className="bg-amber-500 hover:bg-amber-400 text-slate-950 font-black text-xs shrink-0 cursor-pointer shadow-md"
            >
              {t('booking.startTravelling')}
            </Button>
          </div>
        </div>
      </Card>

      {/* Action Buttons */}
      <div className="flex flex-col gap-3">
        <Link to="/queue">
          <Button size="lg" fullWidth icon={Clock}>
            {t('nav.queue')}
          </Button>
        </Link>
        <Link to="/procurement-status">
          <Button size="md" variant="outline" fullWidth icon={ArrowRight}>
            {t('booking.procurementCompleted')}
          </Button>
        </Link>
      </div>
    </div>
  );
};

export default DigitalToken;
