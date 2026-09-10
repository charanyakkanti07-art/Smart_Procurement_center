import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ShieldCheck, User, Sprout, MapPin, Calendar, Clock, Navigation, CheckCircle2 } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useLanguage } from '../context/LanguageContext';
import { bookingService } from '../services/api';
import Card from '../components/Card';
import Button from '../components/Button';
import ErrorMessage from '../components/ErrorMessage';

export const BookingConfirmation = () => {
  const { farmer, saveBooking } = useAuth();
  const { t } = useLanguage();
  const navigate = useNavigate();

  const [centre, setCentre] = useState(null);
  const [slot, setSlot] = useState(null);
  const [date, setDate] = useState('2026-09-20');
  const [crop, setCrop] = useState(null);
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  useEffect(() => {
    const savedCentre = localStorage.getItem('selected_centre');
    const savedSlot = localStorage.getItem('selected_slot');
    const savedDate = localStorage.getItem('selected_date');
    const savedCrop = localStorage.getItem('selected_crop');

    if (savedCentre) setCentre(JSON.parse(savedCentre));
    if (savedSlot) setSlot(JSON.parse(savedSlot));
    if (savedDate) setDate(savedDate);
    if (savedCrop) setCrop(JSON.parse(savedCrop));
  }, []);

  const handleConfirmBooking = async () => {
    setLoading(true);
    setErrorMsg('');

    const bookingPayload = {
      farmerId: farmer?.farmerId || 1,
      centreId: centre?.centreId || 1,
      cropId: crop?.cropId || 1,
      quantity: crop?.quantity || 500,
      bookingDate: date || '2026-09-20',
      slot: slot?.timeWindow || '10:00 AM – 11:00 AM'
    };

    try {
      const res = await bookingService.createBooking(bookingPayload);
      saveBooking(res);
      navigate('/token');
    } catch (err) {
      setErrorMsg(err.message || t('errors.bookingFailed'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-lg mx-auto px-4 py-6 text-left flex flex-col gap-6">
      {/* Header */}
      <div>
        <h2 className="text-2xl font-extrabold text-slate-900 tracking-tight">
          {t('booking.confirmBooking')}
        </h2>
        <p className="text-xs text-slate-600 mt-0.5">{t('onboarding.subtitle')}</p>
      </div>

      {errorMsg && <ErrorMessage message={errorMsg} />}

      {/* Summary Review Card */}
      <Card className="border-2 border-emerald-600/40 shadow-md">
        <div className="flex items-center gap-2 pb-4 border-b border-slate-100 text-emerald-800 font-extrabold text-sm">
          <ShieldCheck className="w-5 h-5 text-emerald-700" />
          <span>{t('booking.bookingConfirmed')}</span>
        </div>

        <div className="py-4 flex flex-col gap-4">
          {/* Farmer Details */}
          <div className="flex items-start gap-3">
            <div className="w-9 h-9 rounded-xl bg-slate-100 text-slate-700 flex items-center justify-center shrink-0 mt-0.5">
              <User className="w-5 h-5" />
            </div>
            <div>
              <span className="text-[10px] text-slate-400 font-bold uppercase block">{t('nav.profile')}</span>
              <span className="font-extrabold text-slate-900 text-base block">{farmer?.name || 'Ramesh Kumar'}</span>
              <span className="text-xs text-slate-500 font-medium">{t('onboarding.phone')}: {farmer?.phone || '9876543210'}</span>
            </div>
          </div>

          <hr className="border-slate-100" />

          {/* Crop & Quantity */}
          <div className="flex items-start gap-3">
            <div className="w-9 h-9 rounded-xl bg-amber-100 text-amber-800 flex items-center justify-center shrink-0 mt-0.5">
              <Sprout className="w-5 h-5" />
            </div>
            <div>
              <span className="text-[10px] text-slate-400 font-bold uppercase block">{t('farmer.cropDetails')}</span>
              <span className="font-extrabold text-slate-900 text-base block">{crop?.cropType || 'Paddy'}</span>
              <span className="text-xs text-emerald-800 font-bold bg-emerald-50 px-2 py-0.5 rounded border border-emerald-200 inline-block mt-0.5">
                {t('farmer.quantity')}: {crop?.quantity || 500} {t('farmer.unit')}
              </span>
            </div>
          </div>

          <hr className="border-slate-100" />

          {/* Centre Details */}
          <div className="flex items-start gap-3">
            <div className="w-9 h-9 rounded-xl bg-blue-100 text-blue-800 flex items-center justify-center shrink-0 mt-0.5">
              <MapPin className="w-5 h-5" />
            </div>
            <div>
              <span className="text-[10px] text-slate-400 font-bold uppercase block">{t('booking.centre')}</span>
              <span className="font-extrabold text-slate-900 text-base block">{centre?.name || 'ABC Procurement Centre'}</span>
              <span className="text-xs text-slate-500 font-medium">{centre?.location || 'Kondapur Main Road, Medak'}</span>
            </div>
          </div>

          <hr className="border-slate-100" />

          {/* Date & Slot */}
          <div className="grid grid-cols-2 gap-4 bg-slate-50 p-3 rounded-2xl border border-slate-100">
            <div>
              <span className="text-[10px] text-slate-400 font-bold uppercase block">{t('booking.date')}</span>
              <span className="font-bold text-slate-900 text-sm flex items-center gap-1.5 mt-0.5">
                <Calendar className="w-4 h-4 text-emerald-700" /> {date}
              </span>
            </div>

            <div>
              <span className="text-[10px] text-slate-400 font-bold uppercase block">{t('booking.timeSlot')}</span>
              <span className="font-bold text-slate-900 text-sm flex items-center gap-1.5 mt-0.5">
                <Clock className="w-4 h-4 text-emerald-700" /> {slot?.timeWindow || '10:00 AM – 11:00 AM'}
              </span>
            </div>

            <div>
              <span className="text-[10px] text-slate-400 font-bold uppercase block">{t('centre.travelTime', { travelTime: '' }).split(':')[0]}</span>
              <span className="font-semibold text-slate-800 text-xs flex items-center gap-1 mt-0.5">
                <Navigation className="w-3.5 h-3.5 text-emerald-700" /> {centre?.travelTimeMinutes || 42} mins
              </span>
            </div>

            <div>
              <span className="text-[10px] text-slate-400 font-bold uppercase block">{t('queue.yourPosition')}</span>
              <span className="font-semibold text-amber-800 text-xs block mt-0.5">
                {t('queue.farmersAhead', { count: slot?.farmersBooked || 4 })}
              </span>
            </div>
          </div>
        </div>

        <Button
          size="lg"
          fullWidth
          loading={loading}
          icon={CheckCircle2}
          onClick={handleConfirmBooking}
          className="mt-2"
        >
          {t('booking.confirmBooking')}
        </Button>
      </Card>
    </div>
  );
};

export default BookingConfirmation;
