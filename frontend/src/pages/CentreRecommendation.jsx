import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Sparkles, CheckCircle2, AlertTriangle, Navigation, Clock, Users, ArrowRight, MapPin } from 'lucide-react';
import { centreService } from '../services/api';
import { useLanguage } from '../context/LanguageContext';
import Card from '../components/Card';
import Button from '../components/Button';
import StatusBadge from '../components/StatusBadge';
import Loading from '../components/Loading';

export const CentreRecommendation = () => {
  const [recommendationData, setRecommendationData] = useState(null);
  const [loading, setLoading] = useState(true);
  const { t } = useLanguage();
  const navigate = useNavigate();

  useEffect(() => {
    fetchRecommendations();
  }, []);

  const fetchRecommendations = async () => {
    setLoading(true);
    try {
      const data = await centreService.getRecommendations(17.3850, 78.4867, 'Paddy', 500);
      setRecommendationData(data);
    } catch (err) {
      console.error("Error fetching centre recommendations:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleProceedToSlot = (centre) => {
    localStorage.setItem('selected_centre', JSON.stringify(centre));
    navigate('/slot-selection');
  };

  if (loading) return <Loading message={t('common.loading')} />;

  const recommended = recommendationData?.recommendedCentre;
  const allCentres = recommendationData?.centres || [];
  const summary = recommendationData?.recommendationSummary;

  return (
    <div className="max-w-4xl mx-auto px-4 py-6 text-left flex flex-col gap-6">
      {/* Header */}
      <div className="bg-gradient-to-r from-emerald-950 via-emerald-900 to-teal-950 text-white rounded-3xl p-6 shadow-xl border border-emerald-800">
        <div className="flex items-center gap-2 text-emerald-300 text-xs font-bold uppercase tracking-wider mb-2">
          <Sparkles className="w-4 h-4 text-amber-400 animate-pulse" />
          <span>{t('centre.loadBalanced')}</span>
        </div>
        <h2 className="text-2xl sm:text-3xl font-black text-white tracking-tight">
          {t('centre.recommendationTitle')}
        </h2>
        <p className="text-xs sm:text-sm text-emerald-200 mt-1 max-w-2xl">
          {t('centre.shorterWaitReason')}
        </p>
      </div>

      {/* REASONING SUMMARY BANNER */}
      {summary && (
        <div className="p-4 rounded-2xl bg-amber-500/10 border border-amber-500/30 text-amber-950 text-xs sm:text-sm font-semibold flex items-start gap-3 shadow-xs">
          <Sparkles className="w-5 h-5 text-amber-600 shrink-0 mt-0.5" />
          <div>
            <span className="font-extrabold text-amber-900 block mb-0.5">{t('centre.recommendationTitle')}:</span>
            {summary}
          </div>
        </div>
      )}

      {/* RECOMMENDED CENTRE CARD */}
      {recommended && (
        <Card className="border-2 border-emerald-600 shadow-lg bg-gradient-to-b from-emerald-50/50 via-white to-white relative overflow-hidden">
          <div className="absolute top-0 right-0 bg-emerald-600 text-white px-4 py-1 rounded-bl-2xl text-[11px] font-extrabold tracking-wide uppercase shadow-sm flex items-center gap-1">
            <Sparkles className="w-3.5 h-3.5 text-amber-300" /> {t('centre.recommendedBadge')}
          </div>

          <div className="flex items-center gap-2 pb-3 border-b border-emerald-100">
            <StatusBadge status={recommended.operatingStatus || 'ACTIVE'} />
            <span className="text-xs font-extrabold text-emerald-800 bg-emerald-100 px-2.5 py-0.5 rounded-full">
              Score: {recommended.score} / 100
            </span>
          </div>

          <div className="py-4">
            <h3 className="text-xl sm:text-2xl font-black text-slate-900">{recommended.name}</h3>
            <p className="text-xs text-slate-600 mt-0.5 flex items-center gap-1">
              <MapPin className="w-3.5 h-3.5 text-slate-400" /> {recommended.location}
            </p>

            {/* Metrics Row */}
            <div className="grid grid-cols-2 sm:grid-cols-5 gap-2.5 my-4">
              <div className="bg-white p-2.5 rounded-xl border border-emerald-200 shadow-2xs">
                <span className="text-[10px] text-slate-400 font-bold block uppercase">{t('centre.distance', { distance: recommended.distanceKm }).split(':')[0]}</span>
                <span className="font-extrabold text-slate-900 text-sm flex items-center gap-1 mt-0.5">
                  <Navigation className="w-3.5 h-3.5 text-emerald-700" /> {recommended.distanceKm} km
                </span>
              </div>

              <div className="bg-white p-2.5 rounded-xl border border-emerald-200 shadow-2xs">
                <span className="text-[10px] text-slate-400 font-bold block uppercase">
                  {t('centre.travelTime', { travelTime: recommended.travelTimeMinutes }).split(':')[0]}
                </span>
                <span className="font-extrabold text-slate-900 text-sm flex items-center gap-1 mt-0.5">
                  <Clock className="w-3.5 h-3.5 text-emerald-700" /> {recommended.travelTimeMinutes} mins
                </span>
              </div>

              <div className="bg-white p-2.5 rounded-xl border border-emerald-200 shadow-2xs">
                <span className="text-[10px] text-slate-400 font-bold block uppercase">{t('queue.farmersAhead', { count: '' }).split(':')[0]}</span>
                <span className="font-extrabold text-slate-900 text-sm flex items-center gap-1 mt-0.5">
                  <Users className="w-3.5 h-3.5 text-emerald-700" /> {recommended.queueLength} Farmers
                </span>
              </div>

              <div className="bg-white p-2.5 rounded-xl border border-emerald-200 shadow-2xs">
                <span className="text-[10px] text-slate-400 font-bold block uppercase">{t('queue.estimatedWait', { minutes: '' }).split(':')[0]}</span>
                <span className="font-extrabold text-slate-900 text-sm flex items-center gap-1 mt-0.5">
                  <Clock className="w-3.5 h-3.5 text-amber-600" /> {recommended.estimatedWaitMinutes} mins
                </span>
              </div>

              <div className="bg-emerald-900 text-white p-2.5 rounded-xl border border-emerald-800 shadow-2xs col-span-2 sm:col-span-1">
                <span className="text-[10px] text-emerald-300 font-bold block uppercase">{t('queue.estimatedWait', { minutes: '' }).split(':')[0]}</span>
                <span className="font-black text-amber-300 text-base block mt-0.5">
                  {recommended.estimatedTotalMinutes} mins
                </span>
              </div>
            </div>

            {/* Why Centre Explanation List */}
            <div className="bg-emerald-100/70 rounded-2xl p-4 border border-emerald-300 text-xs font-semibold text-emerald-950 flex flex-col gap-2">
              <span className="text-emerald-900 font-extrabold uppercase tracking-wide text-[11px] flex items-center gap-1.5">
                <CheckCircle2 className="w-4 h-4 text-emerald-700" /> {recommended.name}
              </span>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                {recommended.reasons?.map((reason, idx) => (
                  <span key={idx} className="flex items-center gap-1.5 text-emerald-950">
                    <CheckCircle2 className="w-3.5 h-3.5 text-emerald-700 shrink-0" />
                    {reason}
                  </span>
                ))}
              </div>
            </div>
          </div>

          <div className="pt-2">
            <Button
              size="lg"
              variant="primary"
              fullWidth
              icon={ArrowRight}
              onClick={() => handleProceedToSlot(recommended)}
              className="bg-emerald-800 hover:bg-emerald-900 shadow-md"
            >
              {t('booking.selectSlot')} ({recommended.name})
            </Button>
          </div>
        </Card>
      )}

      {/* COMPARISON MATRIX TABLE */}
      <div>
        <h3 className="font-extrabold text-slate-900 text-lg mb-3 flex items-center justify-between">
          <span>{t('booking.selectCentre')}</span>
          <span className="text-xs text-slate-500 font-normal">{t('centre.shorterWaitReason')}</span>
        </h3>

        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden text-xs">
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-slate-100 text-slate-700 border-b border-slate-200 font-extrabold uppercase text-[10px] tracking-wider">
                  <th className="p-3">{t('booking.centre')}</th>
                  <th className="p-3">{t('centre.distance', { distance: '' }).split(':')[0]}</th>
                  <th className="p-3">{t('centre.travelTime', { travelTime: '' }).split(':')[0]}</th>
                  <th className="p-3">{t('nav.queue')}</th>
                  <th className="p-3">{t('queue.estimatedWait', { minutes: '' }).split(':')[0]}</th>
                  <th className="p-3">{t('common.status')}</th>
                  <th className="p-3 text-right">{t('common.continue')}</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {allCentres.map((centre) => {
                  const isRec = centre.centreId === recommended?.centreId;
                  const isClosed = centre.operatingStatus === 'CLOSED';

                  return (
                    <tr
                      key={centre.centreId}
                      className={isRec ? 'bg-emerald-50/70 font-semibold' : 'hover:bg-slate-50'}
                    >
                      <td className="p-3">
                        <span className="font-extrabold text-slate-900 block">{centre.name}</span>
                        <span className="text-[10px] text-slate-500">{centre.location}</span>
                        {isRec && (
                          <span className="inline-block mt-0.5 text-[9px] bg-emerald-700 text-white font-extrabold px-1.5 py-0.5 rounded">
                            {t('centre.recommendedBadge')}
                          </span>
                        )}
                      </td>
                      <td className="p-3 font-bold text-slate-800">{centre.distanceKm} km</td>
                      <td className="p-3 font-bold text-slate-800">
                        {centre.travelTimeMinutes} mins
                      </td>
                      <td className="p-3 font-bold text-slate-800">{centre.queueLength} Farmers</td>
                      <td className="p-3 font-bold text-slate-800">
                        {isClosed ? '—' : `${centre.estimatedWaitMinutes} mins`}
                      </td>
                      <td className="p-3">
                        <StatusBadge status={centre.operatingStatus || 'ACTIVE'} />
                      </td>
                      <td className="p-3 text-right">
                        <Button
                          size="sm"
                          variant={isRec ? 'primary' : isClosed ? 'outline' : 'secondary'}
                          disabled={isClosed}
                          onClick={() => handleProceedToSlot(centre)}
                        >
                          {isRec ? t('booking.selectSlot') : isClosed ? t('common.close') : t('booking.selectCentre')}
                        </Button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* WHY NOT OTHER CENTRES (DETAILED BREAKDOWN CARDS) */}
      <div className="flex flex-col gap-3">
        <h3 className="font-extrabold text-slate-900 text-base">{t('centre.centreOverloaded', { centreName: '' })}</h3>

        {allCentres
          .filter(c => c.centreId !== recommended?.centreId)
          .map((centre) => {
            const isClosed = centre.operatingStatus === 'CLOSED';
            const isOverloaded = centre.operatingStatus === 'OVERLOADED';

            return (
              <Card key={centre.centreId} className={`p-4 border-l-4 ${isClosed ? 'border-l-slate-400 bg-slate-50' : isOverloaded ? 'border-l-rose-500 bg-rose-50/30' : 'border-l-amber-500 bg-amber-50/20'}`}>
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-2">
                  <div>
                    <h4 className="font-extrabold text-slate-900 text-sm flex items-center gap-2">
                      {centre.name}
                      <StatusBadge status={centre.operatingStatus || 'ACTIVE'} />
                    </h4>
                    <p className="text-xs text-slate-500">
                      {centre.distanceKm} km • {centre.travelTimeMinutes} mins travel • {centre.queueLength} waiting
                    </p>
                  </div>
                  <Button
                    size="sm"
                    variant="outline"
                    disabled={isClosed}
                    onClick={() => handleProceedToSlot(centre)}
                  >
                    {isClosed ? t('common.close') : t('booking.selectSlot')}
                  </Button>
                </div>

                {/* Why Not Explanation Box */}
                {isOverloaded && (
                  <div className="p-3 rounded-xl bg-white border border-rose-200 text-xs flex flex-col gap-1 mt-2">
                    <span className="font-extrabold text-rose-800 uppercase text-[10px] flex items-center gap-1">
                      <AlertTriangle className="w-3.5 h-3.5 text-rose-600" />
                      {t('centre.centreOverloaded', { centreName: centre.name })}
                    </span>
                    <p className="font-semibold text-rose-900">
                      {t('centre.overloadedMsg', { centreName: centre.name, waitingTime: centre.estimatedWaitMinutes })}
                    </p>
                  </div>
                )}
              </Card>
            );
          })}
      </div>
    </div>
  );
};

export default CentreRecommendation;
