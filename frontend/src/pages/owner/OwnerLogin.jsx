import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ShieldCheck, Phone, Lock, LogIn, Building2 } from 'lucide-react';
import ownerService from '../../services/ownerApi';
import Input from '../../components/Input';
import Button from '../../components/Button';
import ErrorMessage from '../../components/ErrorMessage';

export const OwnerLogin = () => {
  const navigate = useNavigate();
  const [phone, setPhone] = useState('9876543210');
  const [password, setPassword] = useState('123456');
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErrorMsg('');

    try {
      const res = await ownerService.login(phone, password);
      navigate('/owner/dashboard');
    } catch (err) {
      setErrorMsg('Invalid operator phone or password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[85vh] flex flex-col justify-center max-w-md mx-auto px-4 py-8 text-left">
      <div className="text-center mb-6">
        <div className="w-16 h-16 rounded-2xl bg-slate-900 text-amber-400 flex items-center justify-center mx-auto mb-3 shadow-lg border border-slate-800">
          <Building2 className="w-9 h-9" />
        </div>
        <h2 className="text-2xl font-black text-slate-900 tracking-tight">
          Operator & Owner Portal
        </h2>
        <p className="text-xs text-slate-600 mt-1">
          Government Procurement Centre Management Dashboard
        </p>
      </div>

      {errorMsg && <ErrorMessage message={errorMsg} className="mb-4" />}

      <form onSubmit={handleSubmit} className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col gap-4">
        <Input
          label="Operator Mobile Number"
          type="tel"
          maxLength={10}
          icon={Phone}
          value={phone}
          onChange={(e) => setPhone(e.target.value.replace(/\D/g, ''))}
        />

        <Input
          label="Password"
          type="password"
          icon={Lock}
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <Button type="submit" size="lg" fullWidth loading={loading} icon={LogIn} className="mt-2 bg-slate-900 hover:bg-slate-800 text-amber-300 font-extrabold">
          Access Operator Dashboard
        </Button>
      </form>

      <div className="mt-6 p-3 rounded-xl bg-slate-100 border border-slate-200 text-xs text-slate-600 font-medium text-center flex items-center justify-center gap-2">
        <ShieldCheck className="w-4 h-4 text-emerald-700 shrink-0" />
        <span>Authorized Mandi Operators Only</span>
      </div>
    </div>
  );
};

export default OwnerLogin;
