import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { User, Phone, Lock, MapPin, Globe, UserPlus, Sprout } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useLanguage, SUPPORTED_LANGUAGES } from '../context/LanguageContext';
import Input from '../components/Input';
import Button from '../components/Button';
import ErrorMessage from '../components/ErrorMessage';

export const Registration = () => {
  const { register, loading } = useAuth();
  const { language, setLanguage, t } = useLanguage();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: '',
    phone: '',
    password: '',
    confirmPassword: '',
    role: 'FARMER',
    language: language || 'te',
    village: '',
    district: '',
    state: '',
    latitude: 17.3850,
    longitude: 78.4867
  });

  const [errors, setErrors] = useState({});
  const [apiError, setApiError] = useState('');

  const handleChange = (field, value) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    if (field === 'language') {
      setLanguage(value);
    }
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: '' }));
    }
  };

  const validate = () => {
    const errs = {};
    if (!formData.name.trim()) errs.name = t('errors.generic', { defaultValue: 'Full name is required' });
    if (!formData.phone || formData.phone.length !== 10) errs.phone = t('errors.selectQuantity', { defaultValue: 'Valid 10-digit mobile number required' });
    if (!formData.password) errs.password = t('errors.generic', { defaultValue: 'Password is required' });
    if (formData.password !== formData.confirmPassword) errs.confirmPassword = t('errors.generic', { defaultValue: 'Passwords do not match' });
    if (!formData.village.trim()) errs.village = t('errors.generic', { defaultValue: 'Village name is required' });

    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setApiError('');
    if (!validate()) return;

    const result = await register(formData);
    if (result.success) {
      const userRole = result.role || formData.role;
      if (userRole === 'OWNER') {
        navigate('/owner/dashboard');
      } else {
        navigate('/profile');
      }
    } else {
      setApiError(result.message || t('errors.generic'));
    }
  };

  return (
    <div className="min-h-[85vh] flex flex-col justify-center max-w-lg mx-auto px-4 py-8">
      {/* Header Visual */}
      <div className="text-center mb-6">
        <div className="w-14 h-14 rounded-2xl bg-emerald-800 text-emerald-200 flex items-center justify-center mx-auto mb-3 shadow-md">
          <Sprout className="w-8 h-8" />
        </div>
        <h2 className="text-2xl font-extrabold text-slate-900 tracking-tight">
          {t('onboarding.registerTitle')}
        </h2>
        <p className="text-xs text-slate-600 mt-1">
          {t('onboarding.registerSubtitle')}
        </p>
      </div>

      {apiError && <ErrorMessage message={apiError} className="mb-4" />}

      <form onSubmit={handleSubmit} className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col gap-4">
        {/* Full Name */}
        <Input
          label={t('onboarding.name')}
          placeholder="e.g. Ramesh Kumar"
          icon={User}
          value={formData.name}
          onChange={(e) => handleChange('name', e.target.value)}
          error={errors.name}
        />

        {/* Mobile Number */}
        <Input
          label={t('onboarding.phone')}
          type="tel"
          maxLength={10}
          placeholder="10-digit phone number"
          icon={Phone}
          value={formData.phone}
          onChange={(e) => handleChange('phone', e.target.value.replace(/\D/g, ''))}
          error={errors.phone}
        />

        {/* Password Fields */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <Input
            label={t('onboarding.password')}
            type="password"
            placeholder="Create password"
            icon={Lock}
            value={formData.password}
            onChange={(e) => handleChange('password', e.target.value)}
            error={errors.password}
          />
          <Input
            label={t('onboarding.password')}
            type="password"
            placeholder="Confirm password"
            icon={Lock}
            value={formData.confirmPassword}
            onChange={(e) => handleChange('confirmPassword', e.target.value)}
            error={errors.confirmPassword}
          />
        </div>

        {/* Account Type Selection */}
        <div className="w-full flex flex-col gap-1.5 text-left">
          <label className="text-sm font-semibold text-slate-700 flex items-center gap-1">
            <User className="w-4 h-4 text-emerald-600" />
            Account Type
          </label>
          <select
            value={formData.role}
            onChange={(e) => handleChange('role', e.target.value)}
            className="w-full rounded-xl border border-slate-300 bg-white py-3 px-4 text-slate-900 text-sm font-bold focus:border-emerald-600 focus:outline-none focus:ring-2 focus:ring-emerald-600/20"
          >
            <option value="FARMER">Farmer (వ్యవసాయదారుడు)</option>
            <option value="OWNER">Procurement Centre Owner / Operator</option>
          </select>
        </div>

        {/* Preferred Language Select */}
        <div className="w-full flex flex-col gap-1.5 text-left">
          <label className="text-sm font-semibold text-slate-700 flex items-center gap-1">
            <Globe className="w-4 h-4 text-emerald-600" />
            {t('farmer.preferredLanguage')}
          </label>
          <select
            value={formData.language}
            onChange={(e) => handleChange('language', e.target.value)}
            className="w-full rounded-xl border border-slate-300 bg-white py-3 px-4 text-slate-900 text-sm font-bold focus:border-emerald-600 focus:outline-none focus:ring-2 focus:ring-emerald-600/20"
          >
            {SUPPORTED_LANGUAGES.map((l) => (
              <option key={l.code} value={l.code}>
                {l.native} ({l.name})
              </option>
            ))}
          </select>
        </div>

        {/* Address Fields */}
        <Input
          label={t('onboarding.village')}
          placeholder="e.g. Kondapur Village"
          icon={MapPin}
          value={formData.village}
          onChange={(e) => handleChange('village', e.target.value)}
          error={errors.village}
        />

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <Input
            label={t('onboarding.district')}
            placeholder="e.g. Medak District"
            icon={MapPin}
            value={formData.district}
            onChange={(e) => handleChange('district', e.target.value)}
            error={errors.district}
          />
          <Input
            label={t('onboarding.state')}
            placeholder="e.g. Telangana"
            icon={MapPin}
            value={formData.state}
            onChange={(e) => handleChange('state', e.target.value)}
            error={errors.state}
          />
        </div>

        <Button
          type="submit"
          size="lg"
          fullWidth
          loading={loading}
          icon={UserPlus}
          className="mt-2"
        >
          {t('onboarding.registerTitle')}
        </Button>

        <p className="text-xs text-center text-slate-500 mt-2">
          {t('onboarding.haveAccount')}{' '}
          <Link to="/login" className="text-emerald-700 font-bold hover:underline">
            {t('onboarding.loginTitle')}
          </Link>
        </p>
      </form>
    </div>
  );
};

export default Registration;
