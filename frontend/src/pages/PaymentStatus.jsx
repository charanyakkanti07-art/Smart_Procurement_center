import React, { useState, useEffect } from 'react';
import { CreditCard, Clock, Landmark, FileText, CheckCircle2, Download, Printer, X, Scale, ShieldCheck } from 'lucide-react';
import { paymentService } from '../services/api';
import { useLanguage } from '../context/LanguageContext';
import Card from '../components/Card';
import Button from '../components/Button';
import StatusBadge from '../components/StatusBadge';
import Loading from '../components/Loading';

export const PaymentStatus = () => {
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedReceipt, setSelectedReceipt] = useState(null);
  const { t } = useLanguage();

  useEffect(() => {
    fetchPayments();
  }, []);

  const fetchPayments = async () => {
    setLoading(true);
    try {
      const data = await paymentService.getFarmerPayments(1);
      setPayments(data || []);
    } catch (err) {
      console.error("Failed to fetch farmer payments", err);
      setPayments([]);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <Loading message={t('common.loading')} />;

  const latest = payments[0];

  return (
    <div className="max-w-2xl mx-auto px-4 py-6 text-left flex flex-col gap-6">
      {/* Title */}
      <div>
        <h2 className="text-2xl font-black text-slate-900 tracking-tight">
          {t('nav.payments')} & {t('procurement.receipt') || 'Receipts'}
        </h2>
        <p className="text-xs text-slate-600 mt-0.5">{t('payment.directBenefitTransfer') || 'Direct Benefit Transfer (DBT) Payment Status & Receipts'}</p>
      </div>

      {/* Prominent Latest Payment Card */}
      <Card className="border-2 border-emerald-700 bg-gradient-to-br from-emerald-900 via-teal-950 to-slate-950 text-white shadow-xl">
        <div className="flex items-center justify-between border-b border-emerald-800/80 pb-3 mb-4">
          <div>
            <span className="text-[10px] text-emerald-300 font-bold uppercase block">{t('payment.latestProcurement') || 'LATEST PROCUREMENT PAYMENT'}</span>
            <div className="text-4xl font-black text-white mt-1">₹{latest.amount?.toLocaleString('en-IN') || '12,500'}</div>
          </div>
          <StatusBadge status={latest.status || 'COMPLETED'} />
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-3 gap-3 text-xs">
          <div className="bg-slate-900/80 p-2.5 rounded-xl border border-emerald-800/60">
            <span className="text-[10px] text-emerald-400 font-semibold block uppercase">MSP Rate</span>
            <span className="font-bold text-white mt-0.5 block">₹{latest.ratePerUnit || 25}/kg</span>
          </div>

          <div className="bg-slate-900/80 p-2.5 rounded-xl border border-emerald-800/60">
            <span className="text-[10px] text-emerald-400 font-semibold block uppercase">{t('farmer.quantity')}</span>
            <span className="font-bold text-white mt-0.5 block">{latest.netQuantity || 500} {t('farmer.unit')}</span>
          </div>

          <div className="bg-slate-900/80 p-2.5 rounded-xl border border-emerald-800/60 col-span-2 sm:col-span-1">
            <span className="text-[10px] text-emerald-400 font-semibold block uppercase">Grade & Crop</span>
            <span className="font-bold text-white mt-0.5 block">{latest.cropType} ({latest.qualityGrade || 'Grade A'})</span>
          </div>
        </div>

        <div className="mt-4 pt-3 border-t border-emerald-800/60 flex items-center justify-between">
          <span className="text-xs text-emerald-200">
            Bank: <strong>{latest.bankName || 'State Bank of India'} (A/C **{latest.accountLastFour || '4821'})</strong>
          </span>
          <Button
            size="xs"
            variant="outline"
            icon={FileText}
            onClick={() => setSelectedReceipt(latest)}
            className="border-emerald-400 text-emerald-300 hover:bg-emerald-800"
          >
            {t('procurement.viewReceipt') || 'View Digital Receipt'}
          </Button>
        </div>
      </Card>

      {/* FARMER PAYMENT HISTORY LEDGER */}
      <Card title={t('payment.paymentHistory') || 'Payment History & Receipts'}>
        <div className="flex flex-col gap-3">
          {payments.map((p) => (
            <div
              key={p.paymentId || p.procurementCode}
              className="p-4 rounded-2xl border border-slate-200 bg-white hover:border-emerald-300 transition-all flex flex-col sm:flex-row sm:items-center justify-between gap-3 text-left"
            >
              <div>
                <div className="flex items-center gap-2">
                  <span className="font-mono font-bold text-xs text-slate-900">{p.procurementCode || 'PR-10001'}</span>
                  <StatusBadge status={p.status} />
                </div>
                <h4 className="font-extrabold text-slate-900 text-sm mt-1">{p.cropType} • {p.netQuantity} kg</h4>
                <p className="text-xs text-slate-500 mt-0.5">
                  Bank: {p.bankName || 'SBI'} (A/C **{p.accountLastFour || '4821'}) • {p.completedAt ? new Date(p.completedAt).toLocaleDateString() : 'Processing'}
                </p>
                {p.failureReason && (
                  <p className="text-xs text-rose-600 font-semibold mt-1">Reason: {p.failureReason}</p>
                )}
              </div>

              <div className="flex items-center justify-between sm:justify-end gap-3">
                <div className="text-right">
                  <span className="text-xs text-slate-400 block font-bold">Total Amount</span>
                  <span className="text-base font-black text-emerald-800">₹{p.amount?.toLocaleString('en-IN')}</span>
                </div>
                <Button
                  size="sm"
                  variant="outline"
                  icon={FileText}
                  onClick={() => setSelectedReceipt(p)}
                >
                  Receipt
                </Button>
              </div>
            </div>
          ))}
        </div>
      </Card>

      {/* DIGITAL RECEIPT MODAL */}
      {selectedReceipt && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-3xl max-w-lg w-full p-6 shadow-2xl border border-slate-200 relative text-left flex flex-col gap-4">
            <button
              onClick={() => setSelectedReceipt(null)}
              className="absolute top-5 right-5 text-slate-400 hover:text-slate-700"
            >
              <X className="w-6 h-6" />
            </button>

            {/* Official Receipt Header */}
            <div className="border-b border-slate-200 pb-4">
              <div className="flex items-center gap-2 text-emerald-800 text-xs font-black uppercase tracking-wider">
                <Landmark className="w-4 h-4 text-emerald-600" /> GOVERNMENT AGRICULTURAL PROCUREMENT CELL
              </div>
              <h3 className="text-xl font-black text-slate-900 mt-1">Official Digital Procurement Receipt</h3>
              <p className="text-xs text-slate-500 mt-0.5">National MSP Agriculture Procurement Portal</p>
            </div>

            {/* Receipt Main Details */}
            <div className="grid grid-cols-2 gap-3 text-xs">
              <div className="bg-slate-50 p-3 rounded-xl border border-slate-200">
                <span className="text-slate-400 font-bold block">Procurement Code</span>
                <span className="font-mono font-black text-slate-900 text-sm block mt-0.5">{selectedReceipt.procurementCode || 'PR-10001'}</span>
              </div>
              <div className="bg-slate-50 p-3 rounded-xl border border-slate-200">
                <span className="text-slate-400 font-bold block">Payment Reference</span>
                <span className="font-mono font-black text-slate-900 text-sm block mt-0.5">{selectedReceipt.paymentCode || 'PAY-98421'}</span>
              </div>
            </div>

            <div className="flex flex-col gap-2 text-xs text-slate-700 bg-slate-50 p-3 rounded-xl border border-slate-200">
              <div className="flex justify-between">
                <span className="text-slate-500 font-medium">Farmer Name:</span>
                <span className="font-extrabold text-slate-900">{selectedReceipt.farmerName || 'Ramesh Kumar'}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-500 font-medium">Crop & Variety:</span>
                <span className="font-bold text-slate-900">{selectedReceipt.cropType}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-500 font-medium">Net Weight Quantity:</span>
                <span className="font-bold text-emerald-800">{selectedReceipt.netQuantity} kg</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-500 font-medium">Quality Grade:</span>
                <span className="font-bold text-blue-800">{selectedReceipt.qualityGrade || 'Grade A'} (ACCEPTED)</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-500 font-medium">Applicable MSP Rate:</span>
                <span className="font-bold text-slate-900">₹{selectedReceipt.ratePerUnit || 25}/kg</span>
              </div>
            </div>

            {/* Itemized Calculation Breakdown */}
            <div className="flex flex-col gap-1.5 text-xs text-slate-700 bg-emerald-50/80 p-3 rounded-xl border border-emerald-200">
              <div className="flex justify-between">
                <span className="text-slate-600 font-medium">Procurement Value ({selectedReceipt.netQuantity || 500} kg @ ₹{selectedReceipt.ratePerUnit || 25}/kg):</span>
                <span className="font-bold text-slate-900">₹{(selectedReceipt.amount || 12500).toLocaleString('en-IN')}</span>
              </div>
              <div className="flex justify-between text-emerald-800 font-bold">
                <span>Booking Security Deposit Refund:</span>
                <span className="text-emerald-950">+ ₹300.00</span>
              </div>
              <hr className="border-emerald-300 my-0.5" />
              <div className="flex justify-between font-black text-slate-900 text-xs">
                <span>Total Net Payable:</span>
                <span className="text-emerald-900 text-sm font-black">₹{((selectedReceipt.amount || 12500) + 300).toLocaleString('en-IN')}</span>
              </div>
            </div>

            {/* Total Amount Disbursed Highlight */}
            <div className="bg-gradient-to-r from-emerald-800 to-teal-900 text-white p-4 rounded-2xl flex items-center justify-between">
              <div>
                <span className="text-[10px] text-emerald-200 font-extrabold uppercase block">TOTAL DISBURSED AMOUNT (INCL. DEPOSIT REFUND)</span>
                <div className="text-2xl font-black text-amber-300 mt-0.5">₹{((selectedReceipt.amount || 12500) + 300).toLocaleString('en-IN')}</div>
              </div>
              <StatusBadge status={selectedReceipt.status} />
            </div>

            {/* Bank Transfer Details */}
            <div className="text-xs text-slate-600 bg-slate-50 p-3 rounded-xl border border-slate-200 flex flex-col gap-1">
              <p><strong>Bank Account:</strong> {selectedReceipt.bankName || 'State Bank of India'} (A/C **{selectedReceipt.accountLastFour || '4821'})</p>
              <p><strong>IFSC Code:</strong> {selectedReceipt.ifscCode || 'SBIN0004521'}</p>
              <p><strong>Transaction UTR / Ref:</strong> {selectedReceipt.transactionId || 'TXN-8F92A71'}</p>
            </div>

            <div className="flex gap-3 pt-2">
              <Button size="md" variant="primary" fullWidth icon={Printer} onClick={() => window.print()}>
                Print / Save Receipt PDF
              </Button>
              <Button size="md" variant="secondary" fullWidth onClick={() => setSelectedReceipt(null)}>
                Close
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PaymentStatus;

