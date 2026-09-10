import React, { useState, useEffect } from 'react';
import { Bell, Ticket, Navigation, Sparkles, CreditCard, CheckCheck, Settings, Smartphone, MessageSquare, PhoneCall, Globe, Check } from 'lucide-react';
import { notificationService } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { useLanguage, SUPPORTED_LANGUAGES, normalizeLanguageCode } from '../context/LanguageContext';
import Card from '../components/Card';
import Button from '../components/Button';
import Loading from '../components/Loading';

export const Notifications = () => {
  const { farmer } = useAuth();
  const { language, setLanguage, t } = useLanguage();
  const farmerId = farmer?.farmerId || 1;

  const [activeTab, setActiveTab] = useState('notifications'); // 'notifications' | 'preferences'
  const [notifications, setNotifications] = useState([]);
  const [preferences, setPreferences] = useState({
    appEnabled: true,
    smsEnabled: true,
    voiceEnabled: true,
    language: language || 'te'
  });
  const [loading, setLoading] = useState(true);
  const [savingPref, setSavingPref] = useState(false);
  const [prefMessage, setPrefMessage] = useState('');

  useEffect(() => {
    fetchData();
  }, [farmerId]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [notifsData, prefsData] = await Promise.all([
        notificationService.getNotifications(farmerId),
        notificationService.getPreferences(farmerId)
      ]);
      setNotifications(notifsData);
      setPreferences(prefsData);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const markRead = async (id) => {
    try {
      await notificationService.markAsRead(id, farmerId);
      setNotifications(prev => prev.map(n => n.id === id ? { ...n, unread: false, status: 'READ' } : n));
    } catch (err) {
      console.error(err);
    }
  };

  const markAllRead = async () => {
    try {
      await notificationService.markAllAsRead(farmerId);
      setNotifications(prev => prev.map(n => ({ ...n, unread: false, status: 'READ' })));
    } catch (err) {
      console.error(err);
    }
  };

  const handlePreferenceToggle = async (key) => {
    const updated = { ...preferences, [key]: !preferences[key] };
    setPreferences(updated);
    savePreferences(updated);
  };

  const handleLanguageChange = async (langCode) => {
    const norm = normalizeLanguageCode(langCode);
    setLanguage(norm);
    const updated = { ...preferences, language: norm };
    setPreferences(updated);
    savePreferences(updated);
  };

  const savePreferences = async (prefData) => {
    setSavingPref(true);
    setPrefMessage('');
    try {
      await notificationService.updatePreferences(prefData, farmerId);
      setPrefMessage(t('common.save') + '!');
      setTimeout(() => setPrefMessage(''), 3000);
    } catch (err) {
      setPrefMessage(t('errors.generic'));
    } finally {
      setSavingPref(false);
    }
  };

  const getIcon = (type) => {
    switch (type) {
      case 'BOOKING_CONFIRMATION':
      case 'SLOT_REMINDER':
      case 'TOKEN': return Ticket;
      case 'START_TRAVELLING':
      case 'DEPARTURE': return Navigation;
      case 'QUEUE_APPROACHING':
      case 'QUEUE_CHANGED':
      case 'RECOMMENDATION': return Sparkles;
      case 'PAYMENT_COMPLETED':
      case 'PAYMENT': return CreditCard;
      default: return Bell;
    }
  };

  if (loading) return <Loading message={t('common.loading')} />;

  const unreadCount = notifications.filter(n => n.unread).length;

  return (
    <div className="max-w-md mx-auto px-4 py-6 text-left flex flex-col gap-6">
      {/* Title & Tabs */}
      <div className="flex flex-col gap-3">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-2xl font-black text-slate-900 tracking-tight flex items-center gap-2">
              {t('notifications.title')} {unreadCount > 0 && <span className="text-xs bg-amber-500 text-white font-bold px-2 py-0.5 rounded-full">{unreadCount} New</span>}
            </h2>
            <p className="text-xs text-slate-600 mt-0.5">{t('onboarding.subtitle')}</p>
          </div>
          {activeTab === 'notifications' && unreadCount > 0 && (
            <Button size="sm" variant="outline" icon={CheckCheck} onClick={markAllRead}>
              {t('notifications.markAllRead')}
            </Button>
          )}
        </div>

        {/* Tab Navigation */}
        <div className="flex bg-slate-100 p-1 rounded-xl border border-slate-200">
          <button
            onClick={() => setActiveTab('notifications')}
            className={`flex-1 py-2 rounded-lg text-xs font-extrabold transition-all flex items-center justify-center gap-1.5 cursor-pointer ${activeTab === 'notifications' ? 'bg-white text-emerald-800 shadow-xs' : 'text-slate-500 hover:text-slate-900'}`}
          >
            <Bell className="w-3.5 h-3.5" />
            {t('notifications.title')}
          </button>
          <button
            onClick={() => setActiveTab('preferences')}
            className={`flex-1 py-2 rounded-lg text-xs font-extrabold transition-all flex items-center justify-center gap-1.5 cursor-pointer ${activeTab === 'preferences' ? 'bg-white text-emerald-800 shadow-xs' : 'text-slate-500 hover:text-slate-900'}`}
          >
            <Settings className="w-3.5 h-3.5" />
            {t('nav.settings')}
          </button>
        </div>
      </div>

      {activeTab === 'notifications' ? (
        /* Notifications List */
        <div className="flex flex-col gap-3">
          {notifications.length === 0 ? (
            <Card className="text-center py-10">
              <Bell className="w-10 h-10 text-slate-300 mx-auto mb-2" />
              <p className="font-bold text-slate-700 text-sm">{t('notifications.empty')}</p>
            </Card>
          ) : (
            notifications.map((item) => {
              const Icon = getIcon(item.type);
              return (
                <Card
                  key={item.id}
                  onClick={() => item.unread && markRead(item.id)}
                  className={`transition-all cursor-pointer ${item.unread ? 'border-l-4 border-l-emerald-700 bg-emerald-50/40 font-semibold shadow-xs' : 'border-l-4 border-l-slate-200'}`}
                >
                  <div className="flex items-start gap-3">
                    <div className={`w-9 h-9 rounded-xl flex items-center justify-center shrink-0 mt-0.5 ${item.unread ? 'bg-emerald-700 text-white' : 'bg-slate-100 text-slate-500'}`}>
                      <Icon className="w-5 h-5" />
                    </div>
                    <div className="flex-1">
                      <div className="flex items-center justify-between gap-2">
                        <div className="flex items-center gap-2">
                          <h4 className="font-extrabold text-slate-900 text-sm">{item.title}</h4>
                          {item.channel && (
                            <span className="text-[9px] font-bold px-1.5 py-0.2 bg-slate-100 text-slate-600 rounded">
                              {item.channel}
                            </span>
                          )}
                        </div>
                        <span className="text-[10px] text-slate-400 font-medium shrink-0">{item.time}</span>
                      </div>
                      <p className="text-xs text-slate-600 font-medium mt-1 leading-relaxed">{item.message}</p>
                    </div>
                  </div>
                </Card>
              );
            })
          )}
        </div>
      ) : (
        /* Channel & Language Preferences */
        <div className="flex flex-col gap-4">
          <Card title={t('nav.settings')} subtitle={t('onboarding.subtitle')}>
            <div className="flex flex-col gap-3 mt-2">
              {/* App Channel */}
              <div className="flex items-center justify-between p-3 rounded-xl bg-slate-50 border border-slate-200">
                <div className="flex items-center gap-3">
                  <div className="w-8 h-8 rounded-lg bg-emerald-100 text-emerald-800 flex items-center justify-center">
                    <Smartphone className="w-4 h-4" />
                  </div>
                  <div>
                    <h5 className="font-extrabold text-xs text-slate-900">App Notifications</h5>
                    <p className="text-[10px] text-slate-500">In-app notifications</p>
                  </div>
                </div>
                <button
                  onClick={() => handlePreferenceToggle('appEnabled')}
                  className={`w-11 h-6 rounded-full p-1 transition-colors ${preferences.appEnabled ? 'bg-emerald-600' : 'bg-slate-300'}`}
                >
                  <div className={`w-4 h-4 rounded-full bg-white transition-transform ${preferences.appEnabled ? 'translate-x-5' : 'translate-x-0'}`} />
                </button>
              </div>

              {/* SMS Channel */}
              <div className="flex items-center justify-between p-3 rounded-xl bg-slate-50 border border-slate-200">
                <div className="flex items-center gap-3">
                  <div className="w-8 h-8 rounded-lg bg-blue-100 text-blue-800 flex items-center justify-center">
                    <MessageSquare className="w-4 h-4" />
                  </div>
                  <div>
                    <h5 className="font-extrabold text-xs text-slate-900">SMS Alerts</h5>
                    <p className="text-[10px] text-slate-500">SMS notifications</p>
                  </div>
                </div>
                <button
                  onClick={() => handlePreferenceToggle('smsEnabled')}
                  className={`w-11 h-6 rounded-full p-1 transition-colors ${preferences.smsEnabled ? 'bg-emerald-600' : 'bg-slate-300'}`}
                >
                  <div className={`w-4 h-4 rounded-full bg-white transition-transform ${preferences.smsEnabled ? 'translate-x-5' : 'translate-x-0'}`} />
                </button>
              </div>

              {/* Voice Channel */}
              <div className="flex items-center justify-between p-3 rounded-xl bg-slate-50 border border-slate-200">
                <div className="flex items-center gap-3">
                  <div className="w-8 h-8 rounded-lg bg-purple-100 text-purple-800 flex items-center justify-center">
                    <PhoneCall className="w-4 h-4" />
                  </div>
                  <div>
                    <h5 className="font-extrabold text-xs text-slate-900">Automated Voice Calls</h5>
                    <p className="text-[10px] text-slate-500">AI Voice calls</p>
                  </div>
                </div>
                <button
                  onClick={() => handlePreferenceToggle('voiceEnabled')}
                  className={`w-11 h-6 rounded-full p-1 transition-colors ${preferences.voiceEnabled ? 'bg-emerald-600' : 'bg-slate-300'}`}
                >
                  <div className={`w-4 h-4 rounded-full bg-white transition-transform ${preferences.voiceEnabled ? 'translate-x-5' : 'translate-x-0'}`} />
                </button>
              </div>
            </div>
          </Card>

          {/* Preferred Language */}
          <Card title={t('farmer.preferredLanguage')} subtitle={t('common.chooseLanguage')}>
            <div className="grid grid-cols-3 gap-2 mt-2">
              {SUPPORTED_LANGUAGES.map((l) => (
                <button
                  key={l.code}
                  onClick={() => handleLanguageChange(l.code)}
                  className={`p-3 rounded-xl border text-xs font-extrabold transition-all flex flex-col items-center gap-1 cursor-pointer ${normalizeLanguageCode(preferences.language) === l.code ? 'border-emerald-700 bg-emerald-50 text-emerald-900 shadow-xs' : 'border-slate-200 bg-white text-slate-600 hover:bg-slate-50'}`}
                >
                  <Globe className="w-4 h-4 text-emerald-700" />
                  <span>{l.native}</span>
                  {normalizeLanguageCode(preferences.language) === l.code && <Check className="w-3 h-3 text-emerald-700 mt-1" />}
                </button>
              ))}
            </div>
          </Card>

          {prefMessage && (
            <div className="p-3 bg-emerald-100 text-emerald-900 rounded-xl text-xs font-bold text-center border border-emerald-300">
              {prefMessage}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default Notifications;
