import React from 'react';

export const Card = ({
  children,
  title,
  subtitle,
  action,
  className = '',
  headerClassName = '',
  bodyClassName = '',
  ...props
}) => {
  return (
    <div
      className={`bg-white rounded-2xl border border-slate-200/80 shadow-xs overflow-hidden transition-all hover:shadow-md ${className}`}
      {...props}
    >
      {(title || subtitle || action) && (
        <div className={`p-4 sm:p-5 border-b border-slate-100 flex items-center justify-between gap-3 ${headerClassName}`}>
          <div>
            {title && <h3 className="font-bold text-slate-800 text-base sm:text-lg">{title}</h3>}
            {subtitle && <p className="text-xs sm:text-sm text-slate-500 mt-0.5">{subtitle}</p>}
          </div>
          {action && <div>{action}</div>}
        </div>
      )}
      <div className={`p-4 sm:p-5 ${bodyClassName}`}>{children}</div>
    </div>
  );
};

export default Card;
