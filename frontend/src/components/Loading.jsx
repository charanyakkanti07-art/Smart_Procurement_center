import React from 'react';
import { Loader2 } from 'lucide-react';

export const Loading = ({ message = 'Loading details...', fullScreen = false }) => {
  if (fullScreen) {
    return (
      <div className="fixed inset-0 bg-slate-900/20 backdrop-blur-xs flex flex-col items-center justify-center z-50 p-4">
        <div className="bg-white rounded-2xl p-6 shadow-xl flex flex-col items-center gap-3 max-w-xs text-center border border-slate-100">
          <Loader2 className="w-10 h-10 text-emerald-700 animate-spin" />
          <p className="font-semibold text-slate-800 text-sm">{message}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="py-12 flex flex-col items-center justify-center gap-3 text-center">
      <Loader2 className="w-8 h-8 text-emerald-700 animate-spin" />
      <p className="text-sm font-medium text-slate-600">{message}</p>
    </div>
  );
};

export default Loading;
