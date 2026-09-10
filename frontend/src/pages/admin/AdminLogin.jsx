import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ShieldCheck, Phone, Lock, Building2 } from 'lucide-react';
import adminService from '../../services/adminApi';
import Card from '../../components/Card';
import Button from '../../components/Button';

export const AdminLogin = () => {
  const [phone, setPhone] = useState('9999999999');
  const [password, setPassword] = useState('admin123');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await adminService.login(phone, password);
      navigate('/admin/dashboard');
    } catch (err) {
      setError('Invalid admin credentials. Access restricted to district authority.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-md mx-auto px-4 py-12 text-left">
      <Card className="border-2 border-slate-800 shadow-2xl">
        <div className="flex flex-col items-center text-center pb-4 border-b border-slate-100 mb-6">
          <div className="w-14 h-14 rounded-2xl bg-slate-900 text-white flex items-center justify-center font-bold shadow-md mb-3">
            <ShieldCheck className="w-8 h-8 text-amber-400" />
          </div>
          <span className="text-[10px] text-amber-600 font-extrabold uppercase tracking-wider">GOVERNMENT / DISTRICT PORTAL</span>
          <h2 className="text-2xl font-black text-slate-900 mt-1">District Administrator Login</h2>
          <p className="text-xs text-slate-500 mt-1">Authorized Official Access for Procurement Supervision</p>
        </div>

        {error && (
          <div className="p-3 rounded-xl bg-rose-50 border border-rose-300 text-rose-900 text-xs font-bold mb-4">
            {error}
          </div>
        )}

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
