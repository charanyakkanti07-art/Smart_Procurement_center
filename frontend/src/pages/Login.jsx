import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Phone, Lock, LogIn, Sprout, ShieldCheck } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useLanguage } from '../context/LanguageContext';
import Input from '../components/Input';
import Button from '../components/Button';
import ErrorMessage from '../components/ErrorMessage';

export const Login = () => {
  const { login, loading } = useAuth();
  const { t } = useLanguage();
  const navigate = useNavigate();

  const [phone, setPhone] = useState('9876543210');
  const [password, setPassword] = useState('123456');
  const [errors, setErrors] = useState({});
  const [apiError, setApiError] = useState('');

  const handleDemoFill = () => {
    setPhone('9876543210');
    setPassword('123456');
  };

  const validate = () => {
    const errs = {};
    if (!phone || phone.trim().length !== 10) {
      errs.phone = t('errors.selectQuantity', { defaultValue: 'Please enter a valid 10-digit mobile number' });
    }
    if (!password) {
      errs.password = t('errors.generic', { defaultValue: 'Password is required' });
    }
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setApiError('');
    if (!validate()) return;

    const result = await login(phone, password);
    if (result.success) {
      const role = result.role || localStorage.getItem('user_role');
      if (role === 'ADMIN') {
        navigate('/admin/dashboard');
      } else if (role === 'OWNER') {
        navigate('/owner/dashboard');
      } else {
        navigate('/');
      }
    } else {
      setApiError(result.message || t('errors.generic', { defaultValue: 'Invalid phone number or password' }));
    }
  };

  return (
    <div className="min-h-[85vh] flex flex-col justify-center max-w-md mx-auto px-4 py-8">
      {/* Header Visual */}
      <div className="text-center mb-6">
        <div className="w-14 h-14 rounded-2xl bg-emerald-800 text-emerald-200 flex items-center justify-center mx-auto mb-3 shadow-md">
          <Sprout className="w-8 h-8" />
        </div>
        <h2 className="text-2xl font-extrabold text-slate-900 tracking-tight">
          {t('onboarding.loginTitle')}
        </h2>
        <p className="text-xs text-slate-600 mt-1">
          {t('onboarding.loginSubtitle')}
        </p>
      </div>

      {apiError && <ErrorMessage message={apiError} className="mb-4" />}

      {/* 1-Click Farmer Access Helper */}
      <div className="mb-4 p-3 rounded-2xl bg-emerald-900 text-emerald-100 border border-emerald-800 flex items-center justify-between gap-3 text-xs shadow-sm">
        <div>
          <span className="font-extrabold block text-emerald-300">Farmer Quick Access</span>
          <span className="text-[11px] text-emerald-200">Phone: 9876543210 | Pass: 123456</span>
        </div>
        <button
          type="button"
          onClick={handleDemoFill}
          className="px-3 py-1.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-black text-xs shrink-0 cursor-pointer shadow-xs"
        >
          1-Click Fill
        </button>
      </div>

      {/* Form */}
      <form onSubmit={handleSubmit} className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col gap-4">
        <Input
          label={t('onboarding.phone')}
          type="tel"
          maxLength={10}
          placeholder="10-digit phone number"
          icon={Phone}
          value={phone}
          onChange={(e) => setPhone(e.target.value.replace(/\D/g, ''))}
          error={errors.phone}
        />

        <Input
          label={t('onboarding.password')}
          type="password"
          placeholder="Enter password"
          icon={Lock}
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          error={errors.password}
        />

        <Button
          type="submit"
          size="lg"
          fullWidth
          loading={loading}
          icon={LogIn}
          className="mt-2"
        >
          {t('onboarding.loginTitle')}
        </Button>



        <Link to="/register" className="w-full">
          <Button variant="outline" fullWidth>
            {t('onboarding.registerTitle')}
          </Button>
        </Link>

        <div className="pt-2 flex flex-col sm:flex-row gap-2 text-center text-xs">
          <Link to="/owner/login" className="flex-1 py-2 px-3 rounded-xl bg-amber-50 hover:bg-amber-100 border border-amber-200 text-amber-900 font-extrabold transition-colors">
            🏬 Dedicated Owner Login →
          </Link>
          <Link to="/admin/login" className="flex-1 py-2 px-3 rounded-xl bg-slate-100 hover:bg-slate-200 border border-slate-300 text-slate-900 font-extrabold transition-colors">
            🏛️ Dedicated Admin Login →
          </Link>
        </div>
      </form>

      {/* Govt Trust Badge Footer */}
      <div className="mt-6 p-3 rounded-xl bg-emerald-50 border border-emerald-200/60 flex items-center justify-center gap-2 text-emerald-900 text-xs font-semibold text-center">
        <ShieldCheck className="w-4 h-4 text-emerald-700 shrink-0" />
        <span>{t('common.appName')}</span>
      </div>
    </div>
  );
};

export default Login;
