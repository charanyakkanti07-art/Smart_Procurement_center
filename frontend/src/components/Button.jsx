import React from 'react';
import { Loader2 } from 'lucide-react';

export const Button = ({
  children,
  variant = 'primary', // primary, secondary, outline, danger, success
  size = 'md', // sm, md, lg
  fullWidth = false,
  loading = false,
  disabled = false,
  icon: Icon,
  className = '',
  ...props
}) => {
  const baseStyles = 'inline-flex items-center justify-center font-semibold rounded-xl transition-all duration-150 focus:outline-none focus:ring-2 focus:ring-offset-2 active:scale-[0.98] disabled:opacity-50 disabled:pointer-events-none cursor-pointer';

  const variants = {
    primary: 'bg-emerald-700 hover:bg-emerald-800 text-white shadow-md shadow-emerald-900/10 focus:ring-emerald-600',
    secondary: 'bg-emerald-100 hover:bg-emerald-200 text-emerald-900 focus:ring-emerald-500',
    outline: 'border-2 border-emerald-700 text-emerald-800 hover:bg-emerald-50 focus:ring-emerald-600',
    danger: 'bg-rose-600 hover:bg-rose-700 text-white shadow-md focus:ring-rose-500',
    success: 'bg-green-600 hover:bg-green-700 text-white shadow-md focus:ring-green-500'
  };

  const sizes = {
    sm: 'py-2 px-3 text-xs gap-1.5',
    md: 'py-3 px-5 text-sm gap-2 min-h-[44px]',
    lg: 'py-3.5 px-6 text-base gap-2.5 min-h-[50px] text-base font-bold'
  };

  return (
    <button
      disabled={disabled || loading}
      className={`
        ${baseStyles}
        ${variants[variant] || variants.primary}
        ${sizes[size] || sizes.md}
        ${fullWidth ? 'w-full' : ''}
        ${className}
      `}
      {...props}
    >
      {loading ? (
        <Loader2 className="w-5 h-5 animate-spin" />
      ) : (
        <>
          {Icon && <Icon className="w-5 h-5 shrink-0" />}
          <span>{children}</span>
        </>
      )}
    </button>
  );
};

export default Button;
