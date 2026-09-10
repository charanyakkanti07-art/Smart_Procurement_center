import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import enTranslations from '../locales/en.json';
import teTranslations from '../locales/te.json';
import hiTranslations from '../locales/hi.json';

const translations = {
  en: enTranslations,
  te: teTranslations,
  hi: hiTranslations,
};

export const SUPPORTED_LANGUAGES = [
  { code: 'te', name: 'Telugu', native: 'తెలుగు', desc: 'తెలంగాణ & ఆంధ్రప్రదేశ్' },
  { code: 'en', name: 'English', native: 'English', desc: 'Default Language' },
  { code: 'hi', name: 'Hindi', native: 'हिन्दी', desc: 'राजभाषा हिन्दी' },
];

// Helper to normalize language input ('Telugu' -> 'te', 'te' -> 'te', etc.)
export const normalizeLanguageCode = (code) => {
  if (!code) return 'en';
  const lower = String(code).toLowerCase().trim();
  if (lower === 'te' || lower === 'telugu') return 'te';
  if (lower === 'hi' || lower === 'hindi') return 'hi';
  if (lower === 'en' || lower === 'english') return 'en';
  return 'en';
};

// Helper for Voice Agent compatibility
export const getVoiceLanguage = (code) => {
  const normalized = normalizeLanguageCode(code);
  switch (normalized) {
    case 'te': return 'Telugu';
    case 'hi': return 'Hindi';
    case 'en': default: return 'English';
  }
};

// Internal nested key resolver
const resolveKey = (dict, keyPath) => {
  if (!dict || !keyPath) return null;
  const parts = keyPath.split('.');
  let current = dict;
  for (const part of parts) {
    if (current && typeof current === 'object' && part in current) {
      current = current[part];
    } else {
      return null;
    }
  }
  return typeof current === 'string' ? current : null;
};

// Interpolates {paramName} in template string
const interpolate = (template, params) => {
  if (!template) return '';
  if (!params || typeof params !== 'object') return template;

  let result = template;
  Object.keys(params).forEach((paramKey) => {
    const value = params[paramKey];
    // Replace {paramKey}
    result = result.replace(new RegExp(`\\{${paramKey}\\}`, 'g'), value !== undefined && value !== null ? value : '');
  });
  return result;
};

// Global translation lookup function with English fallback
export const getTranslation = (key, langCode = 'en', params = {}) => {
  const normLang = normalizeLanguageCode(langCode);
  const primaryDict = translations[normLang];
  
  let template = resolveKey(primaryDict, key);

  // Fallback to English if missing in primary language
  if (!template && normLang !== 'en') {
    if (process.env.NODE_ENV !== 'production') {
      console.warn(`[i18n] Missing translation for key "${key}" in language "${normLang}". Falling back to English.`);
    }
    template = resolveKey(translations.en, key);
  }

  // Fallback if missing in English as well
  if (!template) {
    if (process.env.NODE_ENV !== 'production') {
      console.warn(`[i18n] Missing translation for key "${key}" in English fallback.`);
    }
    // Clean humanized key representation
    const lastPart = key.split('.').pop() || key;
    template = lastPart.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase());
  }

  return interpolate(template, params);
};

const LanguageContext = createContext();

export const LanguageProvider = ({ children }) => {
  const [language, setLanguageState] = useState(() => {
    const saved = localStorage.getItem('preferred_language') || localStorage.getItem('app_language');
    return normalizeLanguageCode(saved);
  });

  const setLanguage = useCallback((newLangCode) => {
    const normalized = normalizeLanguageCode(newLangCode);
    setLanguageState(normalized);
    localStorage.setItem('preferred_language', normalized);
    localStorage.setItem('app_language', normalized);
  }, []);

  const t = useCallback((key, params) => {
    return getTranslation(key, language, params);
  }, [language]);

  return (
    <LanguageContext.Provider
      value={{
        language,
        languageName: getVoiceLanguage(language),
        setLanguage,
        t,
        getVoiceLanguage,
        getTranslation,
        supportedLanguages: SUPPORTED_LANGUAGES,
      }}
    >
      {children}
    </LanguageContext.Provider>
  );
};

export const useLanguage = () => {
  const context = useContext(LanguageContext);
  if (!context) {
    throw new Error('useLanguage must be used within a LanguageProvider');
  }
  return context;
};

export default LanguageContext;
