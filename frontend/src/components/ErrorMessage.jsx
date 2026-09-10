import React from 'react';
import { AlertCircle, RefreshCw } from 'lucide-react';
import Button from './Button';

export const ErrorMessage = ({
  title = 'Something went wrong',
  message = 'We could not complete your request. Please check your network connection and try again.',
  onRetry,
  className = ''
}) => {
  return (
    <div className={`p-4 rounded-2xl bg-rose-50 border border-rose-200 text-rose-900 flex flex-col gap-3 ${className}`}>
      <div className="flex items-start gap-3">
        <AlertCircle className="w-5 h-5 text-rose-600 shrink-0 mt-0.5" />
        <div className="flex-1 text-left">
          <h4 className="font-bold text-sm text-rose-900">{title}</h4>
          <p className="text-xs text-rose-700 mt-1 leading-relaxed">{message}</p>
        </div>
      </div>
      {onRetry && (
        <div className="flex justify-end">
          <Button variant="danger" size="sm" icon={RefreshCw} onClick={onRetry}>
            Try Again
          </Button>
        </div>
      )}
    </div>
  );
};

export default ErrorMessage;
