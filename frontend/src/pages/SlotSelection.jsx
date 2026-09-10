import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Calendar, Clock, MapPin, CheckCircle2, ArrowRight } from 'lucide-react';
import { centreService } from '../services/api';
import { useLanguage } from '../context/LanguageContext';
import Card from '../components/Card';
import Button from '../components/Button';
import StatusBadge from '../components/StatusBadge';
import Loading from '../components/Loading';

export const SlotSelection = () => {
  const [centre, setCentre] = useState(null);
  const [slots, setSlots] = useState([]);
  const [selectedDate, setSelectedDate] = useState('2026-09-20');
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [loading, setLoading] = useState(true);
  const { t } = useLanguage();
  const navigate = useNavigate();

  useEffect(() => {
    const savedCentre = localStorage.getItem('selected_centre');
    if (savedCentre) {
      setCentre(JSON.parse(savedCentre));
    }
    fetchSlots();
  }, []);

  const fetchSlots = async () => {
    setLoading(true);
    try {
      const data = await centreService.getSlots(centre?.centreId || 1);
      setSlots(data);
      // Preselect first available slot
      const firstAvail = data.find(s => s.status !== 'FULL');
      if (firstAvail) setSelectedSlot(firstAvail);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleProceedToConfirmation = () => {
    if (!selectedSlot) {
      alert(t('errors.generic'));
      return;
    }
    localStorage.setItem('selected_slot', JSON.stringify(selectedSlot));
    localStorage.setItem('selected_date', selectedDate);
    navigate('/booking-confirmation');
  };

  return (
    <div className="max-w-xl mx-auto px-4 py-6 text-left flex flex-col gap-6">
      {/* Selected Centre Header */}
      <Card className="bg-emerald-950 text-white border-0 shadow-md">
        <div className="flex items-start justify-between gap-3">
          <div>
            <span className="text-[10px] text-emerald-300 font-extrabold uppercase tracking-wider block">{t('booking.selectCentre')}</span>
            <h2 className="text-xl font-black text-white mt-0.5">{centre?.name || 'ABC Procurement Centre'}</h2>
            <p className="text-xs text-emerald-200 mt-1 flex items-center gap-1 font-medium">
              <MapPin className="w-3.5 h-3.5 text-emerald-400" /> {centre?.location || 'Kondapur Main Road, Medak'}
            </p>
          </div>
          <StatusBadge status={centre?.status || 'AVAILABLE'} />
        </div>
      </Card>

      {/* Date Picker Tabs */}
      <div>
        <h3 className="font-bold text-slate-800 text-sm mb-2.5 flex items-center gap-1.5">
          <Calendar className="w-4 h-4 text-emerald-700" /> {t('booking.selectDate')}
        </h3>
        <div className="grid grid-cols-3 gap-2">
          {['2026-09-20', '2026-09-21', '2026-09-22'].map((dateStr) => {
            const isSel = selectedDate === dateStr;
            return (
              <button
                key={dateStr}
                onClick={() => setSelectedDate(dateStr)}
                className={`py-3 px-2 rounded-xl text-xs font-bold border-2 transition-all cursor-pointer text-center ${
                  isSel
                    ? 'border-emerald-700 bg-emerald-700 text-white shadow-xs'
                    : 'border-slate-200 bg-white text-slate-700 hover:border-slate-300'
                }`}
              >
                <div>{dateStr}</div>
              </button>
            );
          })}
        </div>
      </div>

      {/* Time Slots Selection List */}
      <div>
        <h3 className="font-bold text-slate-800 text-sm mb-2.5 flex items-center gap-1.5">
          <Clock className="w-4 h-4 text-emerald-700" /> {t('booking.selectSlot')}
        </h3>

        {loading ? (
          <Loading message={t('common.loading')} />
        ) : (
          <div className="flex flex-col gap-3">
            {slots.map((slot) => {
              const isFull = slot.status === 'FULL';
              const isSelected = selectedSlot?.slotId === slot.slotId;

              return (
                <button
                  key={slot.slotId}
                  disabled={isFull}
                  onClick={() => setSelectedSlot(slot)}
                  className={`
                    w-full p-4 rounded-2xl border-2 text-left transition-all cursor-pointer flex flex-col sm:flex-row sm:items-center justify-between gap-3
                    ${isFull
                      ? 'border-slate-200 bg-slate-100/70 text-slate-400 opacity-60 cursor-not-allowed'
                      : isSelected
                      ? 'border-emerald-700 bg-emerald-50 shadow-md ring-2 ring-emerald-600/20'
                      : 'border-slate-200 bg-white hover:border-slate-300 hover:bg-slate-50'
                    }
                  `}
                >
                  {/* Left Slot info */}
                  <div className="flex items-center gap-3">
                    <div className={`w-8 h-8 rounded-full flex items-center justify-center font-bold text-xs ${isSelected ? 'bg-emerald-700 text-white' : 'bg-slate-200 text-slate-600'}`}>
                      {isSelected ? <CheckCircle2 className="w-5 h-5" /> : <Clock className="w-4 h-4" />}
                    </div>
                    <div>
                      <h4 className="font-extrabold text-slate-900 text-base">{slot.timeWindow}</h4>
                      <div className="flex items-center gap-2 text-xs text-slate-600 mt-1 font-medium">
                        <span>{t('queue.estimatedWait', { minutes: slot.estimatedWaitMinutes })}</span>
                      </div>
                    </div>
                  </div>

                  {/* Right Status pill */}
                  <div className="flex sm:flex-col items-center sm:items-end justify-between gap-1">
                    <StatusBadge status={slot.status} label={isFull ? 'FULL' : slot.status === 'NEAR_CAPACITY' ? 'ALMOST FULL' : 'AVAILABLE'} />
                  </div>
                </button>
              );
            })}
          </div>
        )}
      </div>

      {/* Continue CTA */}
      <Button
        size="lg"
        fullWidth
        icon={ArrowRight}
        disabled={!selectedSlot || selectedSlot.status === 'FULL'}
        onClick={handleProceedToConfirmation}
      >
        {t('common.continue')}
      </Button>
    </div>
  );
};

export default SlotSelection;
