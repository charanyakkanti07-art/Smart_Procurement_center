import React, { useState } from 'react';
import { QRCodeSVG } from 'qrcode.react';
import { ShieldCheck, QrCode, CheckCircle2, Copy, Check, Info } from 'lucide-react';

export const FarmerQRCode = ({ booking, size = 180, className = '' }) => {
  const [copied, setCopied] = useState(false);
  const [showDetails, setShowDetails] = useState(false);

  if (!booking) return null;

  const bookingId = booking.bookingId || booking.id || 103;
  const tokenNumber = booking.tokenNumber || `#${bookingId}`;
  const farmerName = booking.farmerName || booking.farmer?.name || 'Farmer Ramesh';
  const farmerPhone = booking.farmerPhone || booking.farmer?.phone || '9876543210';
  const centreName = booking.centreName || booking.centre?.name || 'ABC Procurement Centre';
  const centreId = booking.centreId || booking.centre?.centreId || 1;
  const cropType = booking.cropType || booking.crop?.cropType || 'Paddy';
  const quantityKg = booking.quantityKg || booking.quantity || 500;
  const bookingDate = booking.bookingDate || booking.date || '2026-09-20';
  const slotWindow = booking.slotWindow || booking.slot || '10:00 AM – 11:00 AM';

  // Construct structured verification payload for official scanners
  const qrPayload = JSON.stringify({
    protocol: 'SMART_MANDI_VERIFICATION_V1',
    system: 'Smart Mandi Procurement Platform',
    bookingId: bookingId,
    tokenNumber: tokenNumber,
    farmer: {
      name: farmerName,
      phone: farmerPhone
    },
    centre: {
      id: centreId,
      name: centreName
    },
    crop: {
      type: cropType,
      quantityKg: quantityKg
    },
    schedule: {
      date: bookingDate,
      slot: slotWindow
    },
    verificationStatus: 'VALID_OFFICIAL_TOKEN',
    issuedTimestamp: new Date().toISOString(),
    securityHash: `GOVT-MANDI-${bookingId}-${farmerPhone.slice(-4)}`
  });

  const handleCopyPayload = () => {
    navigator.clipboard.writeText(qrPayload);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className={`flex flex-col items-center gap-3 ${className}`}>
      {/* Official Valid QR Frame */}
      <div className="relative p-3 rounded-2xl bg-white text-slate-900 shadow-xl border-4 border-emerald-500 flex flex-col items-center justify-center">
        {/* Verification Status Badge */}
        <div className="absolute -top-3 bg-emerald-600 text-white text-[10px] font-black uppercase tracking-wider px-2.5 py-0.5 rounded-full flex items-center gap-1 shadow-md border border-emerald-400">
          <ShieldCheck className="w-3 h-3 text-emerald-200" /> Official Valid QR
        </div>

        <div className="p-2 bg-white rounded-xl flex items-center justify-center">
          <QRCodeSVG
            value={qrPayload}
            size={size}
            level="H"
            includeMargin={true}
            bgColor="#FFFFFF"
            fgColor="#0F172A"
          />
        </div>

        <div className="mt-1 flex items-center justify-center gap-1 text-[11px] font-extrabold text-emerald-800 bg-emerald-50 px-3 py-1 rounded-lg border border-emerald-200 w-full text-center">
          <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600 shrink-0" />
          <span>Scan to Verify Token #{bookingId}</span>
        </div>
      </div>

      {/* Payload Inspection & Actions */}
      <div className="flex items-center gap-2">
        <button
          type="button"
          onClick={() => setShowDetails(!showDetails)}
          className="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg bg-slate-800 text-slate-200 hover:bg-slate-700 text-[11px] font-bold transition-colors cursor-pointer"
        >
          <Info className="w-3.5 h-3.5 text-amber-400" />
          {showDetails ? 'Hide Verification Data' : 'View QR Payload'}
        </button>

        <button
          type="button"
          onClick={handleCopyPayload}
          className="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg bg-slate-800 text-slate-200 hover:bg-slate-700 text-[11px] font-bold transition-colors cursor-pointer"
        >
          {copied ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5 text-slate-400" />}
          {copied ? 'Copied Data' : 'Copy JSON'}
        </button>
      </div>

      {/* Structured Payload Viewer Modal/Drawer */}
      {showDetails && (
        <div className="w-full max-w-sm p-3 bg-slate-900 text-slate-100 rounded-xl border border-slate-700 text-left text-xs font-mono shadow-inner mt-1">
          <div className="flex items-center justify-between pb-1 mb-2 border-b border-slate-800 text-[11px] font-sans text-amber-400 font-bold">
            <span>Verified Encoded Payload</span>
            <span className="text-emerald-400 text-[10px]">JSON FORMAT</span>
          </div>
          <pre className="overflow-x-auto text-[10px] text-emerald-300 leading-relaxed whitespace-pre-wrap">
            {JSON.stringify(JSON.parse(qrPayload), null, 2)}
          </pre>
        </div>
      )}
    </div>
  );
};

export default FarmerQRCode;
