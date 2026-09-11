import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { ShieldCheck, Phone, Lock, Building2, ArrowLeft, Zap } from 'lucide-react';
import adminService from '../../services/adminApi';
import { useAuth } from '../../context/AuthContext';
import Card from '../../components/Card';
import Button from '../../components/Button';

export const AdminLogin = () => {
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { login, switchRole } = useAuth();

  const handleLogin = async (e) => {
    if (e) e.preventDefault();
    setLoading(true);
    setError('');

    try {
      switchRole('ADMIN');
      await adminService.login(phone, password);
      const authRes = await login(phone, password, 'ADMIN');
      if (authRes.success) {
        navigate('/admin/dashboard');
      } else {
        setError(authRes.message || 'Invalid admin credentials. Access restricted to district authority.');
      }
    } catch (err) {
      setError('Invalid admin credentials. Access restricted to district authority.');
    } finally {
      setLoading(false);
    }
  };

  const handleDemoFill = async () => {
    setPhone('9999999999');
    setPassword('admin123');
    switchRole('ADMIN');
    await login('9999999999', 'admin123', 'ADMIN');
    navigate('/admin/dashboard');
  };

  return (
    <div className="max-w-md mx-auto px-4 py-8 text-left">
      <div className="mb-4">
        <Link to="/admin" className="inline-flex items-center gap-1.5 text-xs font-bold text-slate-500 hover:text-slate-900 transition-colors">
          <ArrowLeft className="w-4 h-4" />
          <span>Back to District Admin Landing</span>
        </Link>
      </div>

      <Card className="border-2 border-slate-800 shadow-2xl">
        <div className="flex flex-col items-center text-center pb-4 border-b border-slate-100 mb-6">
          <div className="w-14 h-14 rounded-2xl bg-slate-900 text-white flex items-center justify-center font-bold shadow-md mb-3">
            <ShieldCheck className="w-8 h-8 text-amber-400" />
          </div>
          <span className="text-[10px] text-amber-600 font-extrabold uppercase tracking-wider">GOVERNMENT / DISTRICT PORTAL</span>
          <h2 className="text-2xl font-black text-slate-900 mt-1">District Administrator Login</h2>
          <p className="text-xs text-slate-500 mt-1">Authorized Official Access for Procurement Supervision</p>
        </div>

        {/* 1-Click Admin Access Helper */}
        <div className="mb-5 p-3 rounded-2xl bg-slate-900 text-slate-100 border border-slate-800 flex items-center justify-between gap-3 text-xs">
          <div>
            <span className="font-extrabold block text-amber-400">District Admin Access</span>
            <span className="text-[11px] text-slate-300">Phone: 9999999999 | Pass: admin123</span>
          </div>
          <button
            type="button"
            onClick={handleDemoFill}
            className="px-3 py-1.5 rounded-xl bg-amber-400 hover:bg-amber-300 text-slate-950 font-black text-xs shrink-0 cursor-pointer shadow-xs"
          >
            1-Click Login
          </button>
        </div>

        <form onSubmit={handleLogin} className="flex flex-col gap-4 text-xs">
          <div>
            <label className="font-bold text-slate-700 block mb-1">Official Mobile Number / ID</label>
            <div className="relative">
              <Phone className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
              <input
                type="text"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                placeholder="Enter 10-digit mobile"
                className="w-full pl-9 pr-3 py-2.5 rounded-xl border border-slate-300 text-sm font-semibold"
                required
              />
            </div>
          </div>

          <div>
            <label className="font-bold text-slate-700 block mb-1">Password</label>
            <div className="relative">
              <Lock className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter password"
                className="w-full pl-9 pr-3 py-2.5 rounded-xl border border-slate-300 text-sm"
                required
              />
            </div>
          </div>

          <div className="pt-2">
            <Button
              type="submit"
              size="lg"
              variant="primary"
              fullWidth
              loading={loading}
              className="bg-slate-900 hover:bg-slate-800 text-white font-black cursor-pointer shadow-lg"
            >
              Access District Admin Console
            </Button>
          </div>
        </form>

        <p className="text-[10px] text-slate-400 text-center mt-6">
          Smart Agricultural Procurement Management System • District Collectorate Portal
        </p>
      </Card>
    </div>
  );
};

export default AdminLogin;
