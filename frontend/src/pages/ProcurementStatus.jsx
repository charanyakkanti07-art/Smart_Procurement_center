import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { CheckCircle2, Scale, CreditCard } from 'lucide-react';
import { queueService } from '../services/api';
import { useLanguage } from '../context/LanguageContext';
import Card from '../components/Card';
import Button from '../components/Button';
import Loading from '../components/Loading';

export const ProcurementStatus = () => {
  const [timeline, setTimeline] = useState([]);
  const [loading, setLoading] = useState(true);
  const { t } = useLanguage();

  useEffect(() => {
    fetchStatus();
  }, []);

  const fetchStatus = async () => {
    setLoading(true);
    try {
      const data = await queueService.getProcurementTimeline(101);
      setTimeline(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <Loading message={t('common.loading')} />;

  return (
    <div className="max-w-md mx-auto px-4 py-6 text-left flex flex-col gap-6">
      {/* Title */}
      <div>
        <h2 className="text-2xl font-black text-slate-900 tracking-tight">
          {t('booking.procurementCompleted')}
        </h2>
        <p className="text-xs text-slate-600 mt-0.5">{t('common.status')}</p>
      </div>

      {/* Overview Card */}
      <Card className="bg-gradient-to-r from-emerald-950 to-teal-950 text-white border-0 shadow-md">
        <div className="flex items-center justify-between">
          <div>
            <span className="text-[10px] text-emerald-300 font-bold uppercase block">{t('booking.tokenNumber')}</span>
            <h3 className="font-extrabold text-lg text-white mt-0.5">Paddy • 500 {t('farmer.unit')}</h3>
            <p className="text-xs text-emerald-200 mt-1">ABC Procurement Centre • Token #103</p>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-emerald-800 text-emerald-200 flex items-center justify-center font-bold">
            <Scale className="w-6 h-6" />
          </div>
        </div>
      </Card>

      {/* TIMELINE LIST */}
      <Card title={t('common.status')}>
        <div className="relative pl-6 flex flex-col gap-6 py-2">
          {/* Vertical Connecting Line */}
          <div className="absolute left-[15px] top-4 bottom-4 w-0.5 bg-slate-200"></div>

          {timeline.map((step) => {
            const isCompleted = step.completed;
            const isCurrent = step.current;

            return (
              <div key={step.stage} className="relative flex items-start gap-4">
                {/* Node Circle */}
                <div
                  className={`
                    w-7 h-7 rounded-full flex items-center justify-center shrink-0 z-10 font-bold text-xs transition-all -ml-6
                    ${isCompleted
                      ? 'bg-emerald-700 text-white shadow-xs'
                      : isCurrent
                      ? 'bg-amber-500 text-white ring-4 ring-amber-400/30 animate-pulse'
                      : 'bg-slate-200 text-slate-400 border border-slate-300'
                    }
                  `}
                >
                  {isCompleted ? <CheckCircle2 className="w-4 h-4" /> : isCurrent ? '●' : '○'}
                </div>

                {/* Content */}
                <div className="flex-1">
                  <div className="flex items-center justify-between gap-2">
                    <h4 className={`font-extrabold text-sm ${isCompleted ? 'text-emerald-900' : isCurrent ? 'text-amber-900 font-black' : 'text-slate-500'}`}>
                      {step.label}
                    </h4>
                    <span className="text-[11px] font-semibold text-slate-500">{step.timestamp}</span>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </Card>

      {/* Next Action Button */}
      <Link to="/payment">
        <Button size="lg" fullWidth icon={CreditCard}>
          {t('nav.payments')}
        </Button>
      </Link>
    </div>
  );
};

export default ProcurementStatus;
