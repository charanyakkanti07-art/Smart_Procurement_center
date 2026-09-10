import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { MapPin, Navigation, Clock, Scale, Sparkles, AlertTriangle, ChevronRight } from 'lucide-react';
import { centreService } from '../services/api';
import { useLanguage } from '../context/LanguageContext';
import Card from '../components/Card';
import Button from '../components/Button';
import StatusBadge from '../components/StatusBadge';
import Loading from '../components/Loading';

export const FindCentres = () => {
  const [centres, setCentres] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('ALL');
  const { t } = useLanguage();
  const navigate = useNavigate();

  useEffect(() => {
    fetchCentres();
  }, []);

  const fetchCentres = async () => {
    setLoading(true);
    try {
      const data = await centreService.getAllCentres();
      setCentres(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSelectCentre = (centre) => {
    localStorage.setItem('selected_centre', JSON.stringify(centre));
    navigate('/recommendation');
  };

  const filteredCentres = centres.filter(c => {
    if (filter === 'AVAILABLE') return c.status === 'AVAILABLE';
    if (filter === 'OVERLOADED') return c.status === 'OVERLOADED';
    return true;
  });

  return (
    <div className="max-w-4xl mx-auto px-4 py-6 text-left flex flex-col gap-6">
      {/* Page Header & Smart Recommendation CTA */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-extrabold text-slate-900 tracking-tight">
            {t('booking.selectCentre')}
          </h2>
          <p className="text-xs text-slate-600 mt-0.5">{t('centre.recommendationTitle')}</p>
        </div>
        <Button
          size="md"
          variant="primary"
          icon={Sparkles}
          onClick={() => navigate('/recommendation')}
          className="bg-gradient-to-r from-emerald-800 to-teal-800 shadow-md"
        >
          {t('centre.recommendationTitle')}
        </Button>
      </div>

      {/* Filter Tabs */}
      <div className="flex items-center gap-2 border-b border-slate-200 pb-2">
        <button
          onClick={() => setFilter('ALL')}
          className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all ${filter === 'ALL' ? 'bg-emerald-800 text-white' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'}`}
        >
          {t('booking.selectCentre')} ({centres.length})
        </button>
        <button
          onClick={() => setFilter('AVAILABLE')}
          className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all ${filter === 'AVAILABLE' ? 'bg-emerald-800 text-white' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'}`}
        >
          {t('queue.waiting')}
        </button>
        <button
          onClick={() => setFilter('OVERLOADED')}
          className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all ${filter === 'OVERLOADED' ? 'bg-rose-700 text-white' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'}`}
        >
          {t('queue.centreOverloaded')}
        </button>
      </div>

      {/* Centre Cards Grid */}
      {loading ? (
        <Loading message={t('common.loading')} />
      ) : (
        <div className="flex flex-col gap-4">
          {filteredCentres.map((centre) => {
            const isOverloaded = centre.status === 'OVERLOADED';
            return (
              <Card
                key={centre.centreId}
                className={`transition-all hover:border-emerald-600 ${isOverloaded ? 'border-l-4 border-l-rose-500 bg-rose-50/20' : 'border-l-4 border-l-emerald-600'}`}
              >
                <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                  {/* Left Specs */}
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-1">
                      <h3 className="font-extrabold text-slate-900 text-base sm:text-lg">{centre.name}</h3>
                      <StatusBadge status={centre.status} />
                    </div>
                    <p className="text-xs text-slate-500 font-medium flex items-center gap-1 mb-3">
                      <MapPin className="w-3.5 h-3.5 text-slate-400 shrink-0" />
                      {centre.location}
                    </p>

                    {/* Specifications Chips */}
                    <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 text-xs">
                      <div className="bg-slate-50 p-2 rounded-xl border border-slate-100">
                        <span className="text-[10px] text-slate-400 uppercase font-semibold block">{t('centre.distance', { distance: '' }).split(':')[0]}</span>
                        <span className="font-bold text-slate-800 flex items-center gap-1 mt-0.5">
                          <Navigation className="w-3.5 h-3.5 text-emerald-700" />
                          {centre.distanceKm} km
                        </span>
                      </div>

                      <div className="bg-slate-50 p-2 rounded-xl border border-slate-100">
                        <span className="text-[10px] text-slate-400 uppercase font-semibold block">{t('centre.travelTime', { travelTime: '' }).split(':')[0]}</span>
                        <span className="font-bold text-slate-800 flex items-center gap-1 mt-0.5">
                          <Clock className="w-3.5 h-3.5 text-emerald-700" />
                          {centre.travelTimeMinutes} mins
                        </span>
                      </div>

                      <div className="bg-slate-50 p-2 rounded-xl border border-slate-100">
                        <span className="text-[10px] text-slate-400 uppercase font-semibold block">{t('farmer.quantity')}</span>
                        <span className={`font-bold flex items-center gap-1 mt-0.5 ${isOverloaded ? 'text-rose-700' : 'text-emerald-800'}`}>
                          <Scale className="w-3.5 h-3.5" />
                          {centre.currentLoadPercent}% ({centre.currentLoadKg} kg)
                        </span>
                      </div>

                      <div className="bg-slate-50 p-2 rounded-xl border border-slate-100">
                        <span className="text-[10px] text-slate-400 uppercase font-semibold block">{t('booking.selectSlot')}</span>
                        <span className="font-extrabold text-slate-900 block mt-0.5">
                          {centre.availableSlotsCount} Left
                        </span>
                      </div>
                    </div>

                    {isOverloaded && centre.reason && (
                      <div className="mt-3 p-2.5 rounded-xl bg-rose-100/80 border border-rose-200 text-rose-900 text-xs font-semibold flex items-center gap-2">
                        <AlertTriangle className="w-4 h-4 text-rose-700 shrink-0" />
                        <span>{t('centre.overloadedMsg', { centreName: centre.name, waitingTime: centre.estimatedWaitMinutes || 60 })}</span>
                      </div>
                    )}
                  </div>

                  {/* Action Button */}
                  <div className="shrink-0 flex items-center">
                    <Button
                      size="md"
                      variant={isOverloaded ? 'outline' : 'primary'}
                      icon={ChevronRight}
                      fullWidth
                      onClick={() => handleSelectCentre(centre)}
                    >
                      {isOverloaded ? t('centre.recommendationTitle') : t('booking.selectCentre')}
                    </Button>
                  </div>
                </div>
              </Card>
            );
          })}
        </div>
      )}
    </div>
  );
};

export default FindCentres;
