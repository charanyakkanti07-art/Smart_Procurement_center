import React, { useState } from 'react';
import { useNavigate, Link, useLocation } from 'react-router-dom';
import { ShieldCheck, Phone, Lock, LogIn, Building2, User, ArrowLeft, Clock, AlertTriangle, CheckCircle2, UserPlus } from 'lucide-react';
import { authService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { useLanguage } from '../../context/LanguageContext';
import Input from '../../components/Input';
import Button from '../../components/Button';
import ErrorMessage from '../../components/ErrorMessage';

export const OwnerLogin = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();
  const { t } = useLanguage();

  const [activeTab, setActiveTab] = useState('LOGIN'); // LOGIN or REGISTER
  
  // Login Form State
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');
  
  // Register Form State
  const [regName, setRegName] = useState('');
  const [regPhone, setRegPhone] = useState('');
  const [regPassword, setRegPassword] = useState('');
  const [regCentreName, setRegCentreName] = useState('');

  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');
  const [statusNotice, setStatusNotice] = useState(
    location.state?.pendingMessage
      ? { type: 'SUCCESS', message: location.state.pendingMessage }
      : null
  );

  const handleLoginSubmit = async (e) => {
    if (e) e.preventDefault();
    setLoading(true);
    setErrorMsg('');
    setStatusNotice(null);

    try {
      // Single call through AuthContext — reads role/status from real backend response
      const authRes = await login(phone, password, 'OWNER');
      if (authRes.success) {
        navigate('/owner/dashboard');
      } else if (authRes.status === 'PENDING') {
        setStatusNotice({
          type: 'PENDING',
          message: authRes.message || 'Your Mandi Owner account is PENDING approval by the District Administrator.'
        });
      } else if (authRes.status === 'REJECTED') {
        setStatusNotice({
          type: 'REJECTED',
          message: authRes.message || 'Your Mandi Owner registration request was REJECTED.'
        });
      } else {
        setErrorMsg(authRes.message || 'Invalid operator phone or password');
      }
    } catch (err) {
      setErrorMsg(err.response?.data?.message || err.message || 'Invalid operator phone or password');
    } finally {
      setLoading(false);
    }
  };

  const handleRegisterSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErrorMsg('');
    setStatusNotice(null);

    try {
      const regPayload = {
        name: regName,
        phone: regPhone,
        password: regPassword,
        role: 'OWNER',
        centreName: regCentreName || 'New Procurement Centre'
      };

      await authService.register(regPayload);


      setStatusNotice({
        type: 'SUCCESS',
        message: `Mandi Owner registration submitted successfully! Your account status is PENDING. Access to the Owner Dashboard will be granted once approved by the District Administrator.`
      });

      // Clear fields and switch to login view with notice
      setRegName('');
      setRegPhone('');
      setRegPassword('');
      setRegCentreName('');
      setActiveTab('LOGIN');
      setPhone(regPhone);
    } catch (err) {
      setErrorMsg(err.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[85vh] flex flex-col justify-center max-w-md mx-auto px-4 py-8 text-left">
      <div className="mb-4">
        <Link to="/owner" className="inline-flex items-center gap-1.5 text-xs font-bold text-slate-500 hover:text-slate-900 transition-colors">
          <ArrowLeft className="w-4 h-4" />
          <span>{t('owner.backToLanding', { defaultValue: 'Back to Mandi Owner Landing' })}</span>
        </Link>
      </div>

      <div className="text-center mb-6">
        <div className="w-16 h-16 rounded-2xl bg-slate-900 text-amber-400 flex items-center justify-center mx-auto mb-3 shadow-lg border border-slate-800">
          <Building2 className="w-9 h-9" />
        </div>
        <h2 className="text-2xl font-black text-slate-900 tracking-tight">
          {t('owner.portalTitle', { defaultValue: 'Mandi Owner & Operator Portal' })}
        </h2>
        <p className="text-xs text-slate-600 mt-1">
          {t('owner.portalSubtitle', { defaultValue: 'Government Procurement Centre Management' })}
        </p>
      </div>

      {/* Tab Selector: Login vs Register */}
      <div className="grid grid-cols-2 p-1 bg-slate-200 rounded-2xl mb-6 text-xs font-black">
        <button
          type="button"
          onClick={() => { setActiveTab('LOGIN'); setStatusNotice(null); setErrorMsg(''); }}
          className={`py-2.5 rounded-xl transition-all cursor-pointer ${activeTab === 'LOGIN' ? 'bg-slate-900 text-amber-400 shadow-sm' : 'text-slate-600 hover:text-slate-900'}`}
        >
          {t('owner.loginTab', { defaultValue: 'Mandi Owner Login' })}
        </button>
        <button
          type="button"
          onClick={() => { setActiveTab('REGISTER'); setStatusNotice(null); setErrorMsg(''); }}
          className={`py-2.5 rounded-xl transition-all cursor-pointer ${activeTab === 'REGISTER' ? 'bg-slate-900 text-amber-400 shadow-sm' : 'text-slate-600 hover:text-slate-900'}`}
        >
          {t('owner.registerTab', { defaultValue: 'Register New Owner' })}
        </button>
      </div>

      {errorMsg && <ErrorMessage message={errorMsg} className="mb-4" />}

      {/* Status Notice Alerts */}
      {statusNotice && (
        <div className={`mb-6 p-4 rounded-2xl border-2 text-xs font-extrabold flex items-start gap-3 shadow-sm ${
          statusNotice.type === 'PENDING'
            ? 'bg-amber-50 border-amber-400 text-amber-950'
            : statusNotice.type === 'REJECTED'
            ? 'bg-rose-50 border-rose-400 text-rose-950'
            : 'bg-emerald-50 border-emerald-400 text-emerald-950'
        }`}>
          {statusNotice.type === 'PENDING' && <Clock className="w-5 h-5 text-amber-600 shrink-0 mt-0.5" />}
          {statusNotice.type === 'REJECTED' && <AlertTriangle className="w-5 h-5 text-rose-600 shrink-0 mt-0.5" />}
          {statusNotice.type === 'SUCCESS' && <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0 mt-0.5" />}
          <div>
            <span className="block font-black text-sm uppercase mb-0.5">
              {statusNotice.type === 'PENDING' ? 'Registration Pending Approval' : statusNotice.type === 'REJECTED' ? 'Registration Rejected' : 'Request Submitted'}
            </span>
            <span className="font-medium leading-relaxed">{statusNotice.message}</span>
          </div>
        </div>
      )}

      {/* TAB 1: LOGIN FORM */}
      {activeTab === 'LOGIN' && (
        <>
          <form onSubmit={handleLoginSubmit} className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col gap-4">
            <Input
              label={t('owner.operatorMobile', { defaultValue: 'Operator Mobile Number' })}
              type="tel"
              maxLength={10}
              icon={Phone}
              value={phone}
              onChange={(e) => setPhone(e.target.value.replace(/\D/g, ''))}
              required
            />

            <Input
              label={t('owner.password', { defaultValue: 'Password' })}
              type="password"
              icon={Lock}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />

            <Button type="submit" size="lg" fullWidth loading={loading} icon={LogIn} className="mt-2 bg-slate-900 hover:bg-slate-800 text-amber-300 font-extrabold cursor-pointer">
              {t('owner.accessDashboard', { defaultValue: 'Access Operator Dashboard' })}
            </Button>
          </form>
        </>
      )}

      {/* TAB 2: REGISTER FORM */}
      {activeTab === 'REGISTER' && (
        <form onSubmit={handleRegisterSubmit} className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col gap-4">
          <div className="p-3 rounded-xl bg-amber-50 border border-amber-200 text-amber-900 text-xs font-semibold">
            🛡️ Mandi Owner registration requires <strong>District Admin Approval</strong>. Once submitted, your request will be reviewed by district authorities.
          </div>

          <Input
            label={t('owner.ownerName', { defaultValue: 'Full Name of Mandi Owner / Manager' })}
            type="text"
            icon={User}
            value={regName}
            onChange={(e) => setRegName(e.target.value)}
            placeholder="e.g. Suresh Reddy"
            required
          />

          <Input
            label={t('owner.operatorMobile', { defaultValue: 'Mobile Number (Used for Login)' })}
            type="tel"
            maxLength={10}
            icon={Phone}
            value={regPhone}
            onChange={(e) => setRegPhone(e.target.value.replace(/\D/g, ''))}
            placeholder="e.g. 9849055555"
            required
          />

          <Input
            label={t('owner.mandiName', { defaultValue: 'Procurement Centre / Mandi Name' })}
            type="text"
            icon={Building2}
            value={regCentreName}
            onChange={(e) => setRegCentreName(e.target.value)}
            placeholder="e.g. Kondapur Grain Hub"
            required
          />

          <Input
            label={t('owner.password', { defaultValue: 'Password' })}
            type="password"
            icon={Lock}
            value={regPassword}
            onChange={(e) => setRegPassword(e.target.value)}
            placeholder="Create password"
            required
          />

          <Button type="submit" size="lg" fullWidth loading={loading} icon={UserPlus} className="mt-2 bg-amber-500 hover:bg-amber-600 text-slate-950 font-black cursor-pointer shadow-md">
            {t('owner.submitRegister', { defaultValue: 'Submit Registration Request' })}
          </Button>
        </form>
      )}

      <div className="mt-6 p-3 rounded-xl bg-slate-100 border border-slate-200 text-xs text-slate-600 font-medium text-center flex items-center justify-center gap-2">
        <ShieldCheck className="w-4 h-4 text-emerald-700 shrink-0" />
        <span>Admin Approval Enforced for Mandi Operators</span>
      </div>
    </div>
  );
};

export default OwnerLogin;
