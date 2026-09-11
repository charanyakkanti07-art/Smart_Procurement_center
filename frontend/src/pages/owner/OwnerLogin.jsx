import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { ShieldCheck, Phone, Lock, LogIn, Building2, User, ArrowLeft, Clock, AlertTriangle, CheckCircle2, UserPlus } from 'lucide-react';
import ownerService from '../../services/ownerApi';
import { authService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { useLanguage } from '../../context/LanguageContext';
import Input from '../../components/Input';
import Button from '../../components/Button';
import ErrorMessage from '../../components/ErrorMessage';

export const OwnerLogin = () => {
  const navigate = useNavigate();
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
  const [statusNotice, setStatusNotice] = useState(null); // { type: 'PENDING' | 'REJECTED' | 'SUCCESS', message: string }

  const handleLoginSubmit = async (e) => {
    if (e) e.preventDefault();
    setLoading(true);
    setErrorMsg('');
    setStatusNotice(null);

    try {
      // Check if phone matches pending or rejected mock registration in local state
      const mockPending = JSON.parse(localStorage.getItem('mock_pending_owners') || '[]');
      const match = mockPending.find(o => o.phone === phone);
      
      if (match) {
        if (match.status === 'PENDING') {
          setStatusNotice({
            type: 'PENDING',
            message: `Registration Request PENDING: Account for ${match.name} is awaiting Admin Approval by the District Administrator.`
          });
          setLoading(false);
          return;
        } else if (match.status === 'REJECTED') {
          setStatusNotice({
            type: 'REJECTED',
            message: `Registration Request REJECTED: Registration for ${match.name} was rejected by the District Administrator.`
          });
          setLoading(false);
          return;
        }
      }

      await ownerService.login(phone, password);
      const authRes = await login(phone, password, 'OWNER');
      
      if (authRes.success) {
        if (authRes.res?.status === 'PENDING') {
          setStatusNotice({
            type: 'PENDING',
            message: 'Your Mandi Owner account is PENDING approval by the District Administrator.'
          });
        } else if (authRes.res?.status === 'REJECTED') {
          setStatusNotice({
            type: 'REJECTED',
            message: 'Your Mandi Owner registration request was REJECTED by the District Administrator.'
          });
        } else {
          navigate('/owner/dashboard');
        }
      } else {
        setErrorMsg(authRes.message || 'Invalid operator phone or password');
      }
    } catch (err) {
      setErrorMsg(err.message || 'Invalid operator phone or password');
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

      // Save to mock pending list for offline state tracking
      const mockPending = JSON.parse(localStorage.getItem('mock_pending_owners') || '[]');
      const newOwnerObj = {
        id: Date.now(),
        name: regName + (regCentreName ? ` (${regCentreName})` : ''),
        phone: regPhone,
        role: 'OWNER',
        status: 'PENDING',
        createdAt: new Date().toISOString()
      };
      mockPending.push(newOwnerObj);
      localStorage.setItem('mock_pending_owners', JSON.stringify(mockPending));

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

  const handleDemoFill = async () => {
    setPhone('9876543211');
    setPassword('123456');
    setStatusNotice(null);
    setErrorMsg('');
    setLoading(true);
    try {
      await ownerService.login('9876543211', '123456');
      const authRes = await login('9876543211', '123456', 'OWNER');
      if (authRes.success) {
        navigate('/owner/dashboard');
      }
    } catch (err) {
      setErrorMsg('Login failed');
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
          {/* 1-Click Owner Access Helper */}
          <div className="mb-4 p-3 rounded-2xl bg-slate-900 text-slate-100 border border-slate-800 flex items-center justify-between gap-3 text-xs">
            <div>
              <span className="font-extrabold block text-amber-400">{t('owner.operatorAccess', { defaultValue: 'Mandi Operator Access' })}</span>
              <span className="text-[11px] text-slate-300">Phone: 9876543211 | Pass: 123456</span>
            </div>
            <button
              type="button"
              onClick={handleDemoFill}
              className="px-3 py-1.5 rounded-xl bg-amber-400 hover:bg-amber-300 text-slate-950 font-black text-xs shrink-0 cursor-pointer shadow-xs"
            >
              {t('owner.oneClickLogin', { defaultValue: '1-Click Login' })}
            </button>
          </div>

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
