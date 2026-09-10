import React, { useState, useEffect } from 'react';
import { Clock, Navigation, MapPin, CheckCircle2, RefreshCw, AlertCircle, XCircle } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useLanguage } from '../context/LanguageContext';
import { queueService } from '../services/api';
import { subscribeToQueueUpdates } from '../services/socket';
import Card from '../components/Card';
import Button from '../components/Button';
import Loading from '../components/Loading';

export const QueueTracking = () => {
  const { activeBooking } = useAuth();
  const { t } = useLanguage();
  const [queueData, setQueueData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [cancelLoading, setCancelLoading] = useState(false);
  const [cancelMessage, setCancelMessage] = useState('');

  const bookingId = activeBooking?.bookingId || activeBooking?.id || 1;

  useEffect(() => {
    fetchQueue();
    // Real-time Socket.IO subscription
    const unsubscribe = subscribeToQueueUpdates(1, (eventData) => {
      console.log('Live socket queue update received in QueueTracking:', eventData);
      fetchQueue();
    });
    // Fallback periodic poll
    const interval = setInterval(fetchQueue, 10000);
    return () => {
      unsubscribe();
      clearInterval(interval);
    };
  }, [bookingId]);

  const fetchQueue = async () => {
    try {
      const data = await queueService.getQueueStatus(bookingId);
      setQueueData(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleRequestCancel = async () => {
    if (!window.confirm(t('booking.confirmCancel'))) return;
    setCancelLoading(true);
    setCancelMessage('');
    try {
      await queueService.requestCancellation(bookingId);
      setCancelMessage(t('booking.cancellationSubmitted'));
      await fetchQueue();
    } catch (err) {
      setCancelMessage(err.message || t('errors.generic'));
    } finally {
      setCancelLoading(false);
    }
  };

  if (loading) return <Loading message={t('common.loading')} />;

  const tokenNum = queueData?.tokenNumber || (activeBooking?.tokenNumber || '#103');
  const status = queueData?.status || 'WAITING';
  const queuePos = queueData?.queuePosition ?? 2;
  const farmersAhead = queueData?.farmersAhead ?? 1;
  const currentServing = queueData?.currentlyProcessingToken || 'None (#101)';
  const estWait = status === 'PROCESSING' 
    ? t('queue.processing') 
    : (queueData?.estimatedWaitFormatted || t('queue.estimatedWait', { minutes: queueData?.estimatedWaitMinutes ?? 15 }));

  const getStatusLabel = (st) => {
    switch (st) {
      case 'PROCESSING': return t('queue.processing');
      case 'COMPLETED': return t('queue.completed');
      case 'CANCELLED': return t('queue.cancelled');
      case 'CANCEL_REQUESTED': return t('booking.cancellationSubmitted');
      case 'WAITING': default: return t('queue.waiting');
    }
  };

  return (
    <div className="max-w-md mx-auto px-4 py-6 text-left flex flex-col gap-6">
      {/* Page Title */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-black text-slate-900 tracking-tight">
            {t('queue.title')}
          </h2>
          <p className="text-xs text-slate-600 mt-0.5">{t('queue.yourPosition')}</p>
        </div>
        <Button size="sm" variant="outline" icon={RefreshCw} onClick={fetchQueue}>
          {t('common.refresh')}
        </Button>
      </div>

      {cancelMessage && (
        <div className="p-3 bg-amber-50 border border-amber-200 text-amber-900 text-xs rounded-xl flex items-center gap-2">
          <AlertCircle className="w-4 h-4 text-amber-600 shrink-0" />
          <span>{cancelMessage}</span>
        </div>
      )}

      {/* Primary Metrics Grid Card */}
      <Card className="border-2 border-emerald-700 bg-gradient-to-br from-emerald-900 to-slate-950 text-white shadow-lg">
        <div className="flex items-center justify-between border-b border-emerald-800 pb-3 mb-4">
          <div>
            <span className="text-[10px] text-emerald-300 font-bold uppercase block">{t('booking.tokenNumber')}</span>
            <span className="text-3xl font-black text-white">{tokenNum}</span>
          </div>
          <div className="text-right">
            <span className="text-[10px] text-emerald-300 font-bold uppercase block">{t('common.status')}</span>
            <span className={`inline-block px-3 py-1 text-xs font-black rounded-full uppercase mt-1 ${
              status === 'PROCESSING' ? 'bg-amber-400 text-slate-950 animate-pulse' :
              status === 'COMPLETED' ? 'bg-emerald-500 text-white' :
              status === 'CANCELLED' ? 'bg-rose-500 text-white' :
              status === 'CANCEL_REQUESTED' ? 'bg-orange-400 text-slate-950' : 'bg-emerald-800 text-emerald-100'
            }`}>
              {getStatusLabel(status)}
            </span>
          </div>
        </div>

        <div className="grid grid-cols-4 gap-2 text-center text-xs">
          <div className="bg-slate-900/80 p-2 rounded-xl border border-emerald-800">
            <span className="text-[9px] text-emerald-400 font-semibold block uppercase">{t('queue.yourPosition')}</span>
            <span className="font-black text-lg text-amber-400 mt-0.5 block">#{queuePos}</span>
          </div>

          <div className="bg-slate-900/80 p-2 rounded-xl border border-emerald-800">
            <span className="text-[9px] text-emerald-400 font-semibold block uppercase">{t('queue.farmersAhead', { count: '' }).replace(/:/g, '')}</span>
            <span className="font-black text-lg text-amber-400 mt-0.5 block">{farmersAhead}</span>
          </div>

          <div className="bg-slate-900/80 p-2 rounded-xl border border-emerald-800">
            <span className="text-[9px] text-emerald-400 font-semibold block uppercase">{t('queue.processing')}</span>
            <span className="font-black text-xs text-white mt-1 block truncate">{currentServing}</span>
          </div>

          <div className="bg-slate-900/80 p-2 rounded-xl border border-emerald-800">
            <span className="text-[9px] text-emerald-400 font-semibold block uppercase">{t('queue.estimatedWait', { minutes: '' }).replace(/:/g, '')}</span>
            <span className="font-black text-xs text-emerald-300 mt-1 block">{estWait}</span>
          </div>
        </div>
      </Card>

      {/* CANCELLATION REQUEST BUTTON */}
      {status !== 'CANCELLED' && status !== 'COMPLETED' && (
        <div className="flex justify-end">
          <button
            onClick={handleRequestCancel}
            disabled={cancelLoading || status === 'CANCEL_REQUESTED'}
            className="px-4 py-2 text-xs font-bold rounded-xl border border-rose-300 text-rose-700 bg-rose-50 hover:bg-rose-100 transition-colors flex items-center gap-1.5 disabled:opacity-50 cursor-pointer"
          >
            <XCircle className="w-4 h-4 text-rose-600" />
            {status === 'CANCEL_REQUESTED' ? t('booking.cancellationSubmitted') : t('booking.cancelBooking')}
          </button>
        </div>
      )}

      {/* SMART DEPARTURE RECOMMENDATION */}
      <Card className="bg-gradient-to-r from-amber-50 to-orange-50 border-2 border-amber-300">
        <div className="flex items-start gap-3">
          <div className="w-10 h-10 rounded-2xl bg-amber-500 text-white flex items-center justify-center shrink-0 mt-0.5 shadow-sm">
            <Navigation className="w-5 h-5" />
          </div>
          <div>
            <h3 className="font-extrabold text-amber-950 text-sm">{t('booking.startTravelling')}</h3>
            <p className="text-xs text-amber-900 font-medium mt-1 leading-relaxed">
              {t('queue.estimatedWait', { minutes: 15 })}: <strong>{estWait}</strong>
            </p>
            <div className="mt-2.5 p-2.5 rounded-xl bg-white border border-amber-300 text-xs font-bold text-amber-950 flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-emerald-700 shrink-0" />
              <span>{t('notifications.queueApproachingBody', { position: queuePos })}</span>
            </div>
          </div>
        </div>
      </Card>

      {/* GEOFENCING STATUS WIDGET */}
      <Card className="bg-slate-50 border-slate-200">
        <h4 className="font-bold text-slate-800 text-xs uppercase tracking-wider mb-2 flex items-center gap-1.5">
          <MapPin className="w-4 h-4 text-emerald-700" /> {t('centre.withinRange', { distance: 500 })}
        </h4>
        <div className="flex items-center gap-2.5 text-xs text-slate-700 font-medium bg-white p-3 rounded-xl border border-slate-200">
          <CheckCircle2 className="w-4 h-4 text-emerald-700 shrink-0" />
          <span>✓ {t('queue.queueUpdated')}</span>
        </div>
      </Card>
    </div>
  );
};

export default QueueTracking;
