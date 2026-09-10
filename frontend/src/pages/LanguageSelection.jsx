import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Globe, Check, ArrowRight, Sprout } from 'lucide-react';
import { useLanguage, SUPPORTED_LANGUAGES } from '../context/LanguageContext';
import { useAuth } from '../context/AuthContext';
import Button from '../components/Button';

export const LanguageSelection = () => {
  const { language, setLanguage, t } = useLanguage();
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const handleSelect = (langCode) => {
    setLanguage(langCode);
  };

  const handleContinue = () => {
    if (isAuthenticated) {
      navigate('/');
    } else {
      navigate('/login');
    }
  };

  return (
    <div className="min-h-[85vh] flex flex-col justify-center max-w-md mx-auto px-4 py-8 text-center">
      {/* Header Badge */}
      <div className="w-16 h-16 rounded-2xl bg-emerald-700 text-white flex items-center justify-center mx-auto mb-4 shadow-lg shadow-emerald-900/20">
        <Sprout className="w-10 h-10 text-emerald-200" />
      </div>

      <h2 className="text-2xl sm:text-3xl font-extrabold text-slate-900 tracking-tight">
        {t('onboarding.selectLanguage')}
      </h2>
      <p className="text-sm text-slate-600 mt-1 mb-8">
        {t('onboarding.selectLanguageDesc')}
      </p>

      {/* Language Options Cards */}
      <div className="flex flex-col gap-3.5 mb-8">
        {SUPPORTED_LANGUAGES.map((lang) => {
          const isSelected = language === lang.code;
          return (
            <button
              key={lang.code}
              onClick={() => handleSelect(lang.code)}
              className={`
                w-full p-4.5 rounded-2xl border-2 text-left flex items-center justify-between transition-all cursor-pointer min-h-[64px]
                ${isSelected
                  ? 'border-emerald-700 bg-emerald-50/90 shadow-md ring-2 ring-emerald-600/30 scale-[1.01]'
                  : 'border-slate-200 bg-white hover:border-slate-300 hover:bg-slate-50/80'
                }
              `}
            >
              <div className="flex items-center gap-4">
                <div className={`w-12 h-12 rounded-xl flex items-center justify-center font-bold text-base ${isSelected ? 'bg-emerald-700 text-white' : 'bg-slate-100 text-slate-600'}`}>
                  <Globe className="w-6 h-6" />
                </div>
                <div>
                  <h3 className="font-black text-lg text-slate-900 tracking-tight">{lang.native}</h3>
                  <p className="text-xs text-slate-500 font-semibold">{lang.name} • {t(`onboarding.${lang.code}Desc`, { defaultValue: lang.desc })}</p>
                </div>
              </div>

              {isSelected && (
                <div className="w-8 h-8 rounded-full bg-emerald-700 text-white flex items-center justify-center shadow-xs shrink-0">
                  <Check className="w-5 h-5" />
                </div>
              )}
            </button>
          );
        })}
      </div>

      {/* Continue Button */}
      <Button
        size="lg"
        fullWidth
        icon={ArrowRight}
        onClick={handleContinue}
      >
        {t('common.continue')}
      </Button>
    </div>
  );
};

export default LanguageSelection;
