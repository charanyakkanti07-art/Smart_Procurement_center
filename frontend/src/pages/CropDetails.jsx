import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Sprout, Plus, Calendar, Scale, ArrowRight } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useLanguage } from '../context/LanguageContext';
import { farmerService } from '../services/api';
import Card from '../components/Card';
import Input from '../components/Input';
import Button from '../components/Button';
import Loading from '../components/Loading';

export const CropDetails = () => {
  const { farmer } = useAuth();
  const { t } = useLanguage();
  const navigate = useNavigate();

  const [crops, setCrops] = useState([]);
  const [loading, setLoading] = useState(true);
  const [adding, setAdding] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');
  const [showAddForm, setShowAddForm] = useState(false);

  const [newCrop, setNewCrop] = useState({
    cropType: 'Paddy',
    variety: 'BPT 5204 (Samba Mahsuri)',
    quantity: 500,
    unit: 'kg',
    expectedHarvestDate: '2026-09-20'
  });

  useEffect(() => {
    fetchCrops();
  }, []);

  const fetchCrops = async () => {
    setLoading(true);
    try {
      const data = await farmerService.getCrops(farmer?.farmerId || 1);
      setCrops(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleAddCrop = async (e) => {
    e.preventDefault();
    setAdding(true);
    setErrorMsg('');
    try {
      const added = await farmerService.addCrop(farmer?.farmerId || 1, newCrop);
      setCrops(prev => [...prev, added]);
      setShowAddForm(false);
    } catch (err) {
      setErrorMsg(err.message || t('errors.generic'));
    } finally {
      setAdding(false);
    }
  };

  const handleSelectCropForBooking = (crop) => {
    localStorage.setItem('selected_crop', JSON.stringify(crop));
    navigate('/find-centres');
  };

  return (
    <div className="max-w-xl mx-auto px-4 py-6 text-left">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-xl font-black text-slate-900">{t('crops.yourCrops')}</h2>
        <Button onClick={() => setShowAddForm(!showAddForm)} size="sm" icon={Plus}>
          {showAddForm ? 'Cancel' : t('crops.addCrop')}
        </Button>
      </div>

      {errorMsg && (
        <div className="mb-4">
          <ErrorMessage message={errorMsg} />
        </div>
      )}

      {/* Add Crop Form */}
      {showAddForm && (
        <Card title={t('farmer.cropDetails')} className="mb-6 border-2 border-emerald-600/30">
          <form onSubmit={handleAddCrop} className="flex flex-col gap-4">
            <div className="w-full flex flex-col gap-1.5">
              <label className="text-sm font-semibold text-slate-700">{t('farmer.cropType')}</label>
              <select
                value={newCrop.cropType}
                onChange={(e) => setNewCrop({ ...newCrop, cropType: e.target.value })}
                className="w-full rounded-xl border border-slate-300 bg-white py-3 px-4 text-slate-900 text-sm font-medium focus:border-emerald-600 focus:outline-none"
              >
                <option value="Paddy">Paddy</option>
                <option value="Maize">Maize</option>
                <option value="Cotton">Cotton</option>
                <option value="Wheat">Wheat</option>
                <option value="Red Gram">Red Gram</option>
              </select>
            </div>

            <Input
              label="Variety"
              placeholder="e.g. BPT 5204"
              value={newCrop.variety}
              onChange={(e) => setNewCrop({ ...newCrop, variety: e.target.value })}
            />

            <div className="grid grid-cols-2 gap-4">
              <Input
                label={t('farmer.quantity')}
                type="number"
                min="1"
                placeholder="500"
                icon={Scale}
                value={newCrop.quantity}
                onChange={(e) => setNewCrop({ ...newCrop, quantity: Number(e.target.value) })}
              />
              <div className="w-full flex flex-col gap-1.5">
                <label className="text-sm font-semibold text-slate-700">Unit</label>
                <select
                  value={newCrop.unit}
                  onChange={(e) => setNewCrop({ ...newCrop, unit: e.target.value })}
                  className="w-full rounded-xl border border-slate-300 bg-white py-3 px-4 text-slate-900 text-sm font-medium focus:border-emerald-600 focus:outline-none"
                >
                  <option value="kg">{t('farmer.unit')}</option>
                  <option value="Quintal">Quintal</option>
                </select>
              </div>
            </div>

            <Input
              label={t('booking.date')}
              type="date"
              icon={Calendar}
              value={newCrop.expectedHarvestDate}
              onChange={(e) => setNewCrop({ ...newCrop, expectedHarvestDate: e.target.value })}
            />

            <div className="flex gap-3 pt-2">
              <Button type="submit" loading={adding} fullWidth>
                {t('common.save')}
              </Button>
              <Button type="button" variant="secondary" onClick={() => setShowAddForm(false)} fullWidth>
                {t('common.cancel')}
              </Button>
            </div>
          </form>
        </Card>
      )}

      {/* Crops List */}
      {loading ? (
        <Loading message={t('common.loading')} />
      ) : crops.length === 0 ? (
        <Card className="text-center py-8">
          <Sprout className="w-10 h-10 text-slate-400 mx-auto mb-2" />
          <p className="font-semibold text-slate-700 text-sm">{t('farmer.cropDetails')}</p>
          <Button size="sm" className="mt-4" icon={Plus} onClick={() => setShowAddForm(true)}>
            {t('common.submit')}
          </Button>
        </Card>
      ) : (
        <div className="flex flex-col gap-3.5">
          {crops.map((crop) => (
            <Card key={crop.cropId} className="border-l-4 border-l-emerald-700">
              <div className="flex items-start justify-between gap-3">
                <div className="flex items-start gap-3">
                  <div className="w-10 h-10 rounded-xl bg-emerald-100 text-emerald-800 flex items-center justify-center shrink-0 mt-0.5">
                    <Sprout className="w-5 h-5" />
                  </div>
                  <div>
                    <h3 className="font-extrabold text-slate-900 text-base">{crop.cropType}</h3>
                    <p className="text-xs text-slate-500 font-medium">{crop.variety || 'Standard Variety'}</p>
                    <div className="flex items-center gap-3 text-xs text-slate-600 mt-2">
                      <span className="font-bold text-emerald-800 bg-emerald-50 px-2 py-0.5 rounded border border-emerald-200">
                        {crop.quantity} {t('farmer.unit')}
                      </span>
                    </div>
                  </div>
                </div>

                <Button
                  size="sm"
                  variant="primary"
                  icon={ArrowRight}
                  onClick={() => handleSelectCropForBooking(crop)}
                >
                  {t('booking.bookSlotTitle')}
                </Button>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};

export default CropDetails;
