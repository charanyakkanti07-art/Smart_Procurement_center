import React, { useState } from 'react';
import { User, Phone, MapPin, Globe, Save, CheckCircle2, Edit2 } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useLanguage, SUPPORTED_LANGUAGES, getVoiceLanguage } from '../context/LanguageContext';
import { farmerService } from '../services/api';
import Card from '../components/Card';
import Input from '../components/Input';
import Button from '../components/Button';

export const FarmerProfile = () => {
  const { farmer, setFarmer } = useAuth();
  const { language, setLanguage, t } = useLanguage();
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(false);
  const [successMsg, setSuccessMsg] = useState('');

  const [formData, setFormData] = useState({
    name: farmer?.name || 'Ramesh Kumar',
    phone: farmer?.phone || '9876543210',
    village: farmer?.village || 'Kondapur Village',
    district: farmer?.district || 'Medak District',
    state: farmer?.state || 'Telangana',
    language: language || farmer?.language || 'te'
  });

  const handleChange = (field, value) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setLoading(true);
    setSuccessMsg('');

    try {
      setLanguage(formData.language);
      const updated = await farmerService.updateProfile(farmer?.farmerId || 1, formData);
      setFarmer(updated);
      setSuccessMsg(t('common.save') + '!');
      setIsEditing(false);
    } catch (err) {
      setLanguage(formData.language);
      setFarmer(formData);
      setIsEditing(false);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-xl mx-auto px-4 py-6 text-left">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-2xl font-extrabold text-slate-900 tracking-tight">
            {t('farmer.profileTitle')}
          </h2>
          <p className="text-xs text-slate-600">{t('farmer.personalDetails')}</p>
        </div>
        {!isEditing && (
          <Button size="sm" variant="outline" icon={Edit2} onClick={() => setIsEditing(true)}>
            {t('common.edit')}
          </Button>
        )}
      </div>

      {successMsg && (
        <div className="mb-4 p-3 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-900 text-xs font-semibold flex items-center gap-2">
          <CheckCircle2 className="w-4 h-4 text-emerald-700" />
          <span>{successMsg}</span>
        </div>
      )}

      <Card>
        {isEditing ? (
          <form onSubmit={handleSave} className="flex flex-col gap-4">
            <Input
              label={t('onboarding.name')}
              icon={User}
              value={formData.name}
              onChange={(e) => handleChange('name', e.target.value)}
            />
            <Input
              label={t('onboarding.phone')}
              icon={Phone}
              disabled
              value={formData.phone}
            />
            <div className="w-full flex flex-col gap-1.5">
              <label className="text-sm font-semibold text-slate-700 flex items-center gap-1">
                <Globe className="w-4 h-4 text-emerald-600" />
                {t('farmer.preferredLanguage')}
              </label>
              <select
                value={formData.language}
                onChange={(e) => handleChange('language', e.target.value)}
                className="w-full rounded-xl border border-slate-300 bg-white py-3 px-4 text-slate-900 text-sm font-bold focus:border-emerald-600 focus:outline-none"
              >
                {SUPPORTED_LANGUAGES.map((l) => (
                  <option key={l.code} value={l.code}>
                    {l.native} ({l.name})
                  </option>
                ))}
              </select>
            </div>
            <Input
              label={t('onboarding.village')}
              icon={MapPin}
              value={formData.village}
              onChange={(e) => handleChange('village', e.target.value)}
            />
            <div className="grid grid-cols-2 gap-4">
              <Input
                label={t('onboarding.district')}
                icon={MapPin}
                value={formData.district}
                onChange={(e) => handleChange('district', e.target.value)}
              />
              <Input
                label={t('onboarding.state')}
                icon={MapPin}
                value={formData.state}
                onChange={(e) => handleChange('state', e.target.value)}
              />
            </div>
            <div className="flex gap-3 pt-2">
              <Button type="submit" loading={loading} icon={Save} fullWidth>
                {t('common.save')}
              </Button>
              <Button type="button" variant="secondary" onClick={() => setIsEditing(false)} fullWidth>
                {t('common.cancel')}
              </Button>
            </div>
          </form>
        ) : (
          <div className="flex flex-col gap-4">
            <div className="flex items-center gap-4 pb-4 border-b border-slate-100">
              <div className="w-14 h-14 rounded-2xl bg-emerald-800 text-white font-extrabold text-xl flex items-center justify-center shadow-sm">
                {farmer?.name?.charAt(0) || 'R'}
              </div>
              <div>
                <h3 className="font-extrabold text-lg text-slate-900">{farmer?.name || 'Ramesh Kumar'}</h3>
                <p className="text-xs text-slate-500 font-medium">Farmer ID: #FRM-2026-9842</p>
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-sm pt-1">
              <div className="bg-slate-50 p-3 rounded-xl border border-slate-100">
                <span className="text-xs text-slate-500 font-medium block">{t('onboarding.phone')}</span>
                <span className="font-bold text-slate-900 flex items-center gap-1.5 mt-0.5">
                  <Phone className="w-4 h-4 text-emerald-700" />
                  {farmer?.phone || '9876543210'}
                </span>
              </div>

              <div className="bg-slate-50 p-3 rounded-xl border border-slate-100">
                <span className="text-xs text-slate-500 font-medium block">{t('farmer.preferredLanguage')}</span>
                <span className="font-bold text-slate-900 flex items-center gap-1.5 mt-0.5">
                  <Globe className="w-4 h-4 text-emerald-700" />
                  {SUPPORTED_LANGUAGES.find(l => l.code === language)?.native || getVoiceLanguage(language)}
                </span>
              </div>

              <div className="bg-slate-50 p-3 rounded-xl border border-slate-100 sm:col-span-2">
                <span className="text-xs text-slate-500 font-medium block">{t('farmer.location')}</span>
                <span className="font-bold text-slate-900 flex items-center gap-1.5 mt-0.5">
                  <MapPin className="w-4 h-4 text-emerald-700 shrink-0" />
                  {farmer?.village || 'Kondapur Village'}, {farmer?.district || 'Medak District'}, {farmer?.state || 'Telangana'}
                </span>
              </div>
            </div>
          </div>
        )}
      </Card>
    </div>
  );
};

export default FarmerProfile;
