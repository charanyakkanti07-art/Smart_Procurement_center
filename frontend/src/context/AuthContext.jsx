import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService, farmerService } from '../services/api';
import { useLanguage } from './LanguageContext';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const { language, setLanguage: setLangContext } = useLanguage();
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [userRole, setUserRole] = useState(localStorage.getItem('user_role') || 'FARMER');
  const [farmer, setFarmer] = useState(() => {
    const saved = localStorage.getItem('farmer');
    return saved ? JSON.parse(saved) : null;
  });
  const [activeBooking, setActiveBooking] = useState(() => {
    const saved = localStorage.getItem('active_booking');
    return saved ? JSON.parse(saved) : null;
  });
  const [loading, setLoading] = useState(false);

  // Sync language to LanguageContext & localStorage & backend if farmer is logged in
  const handleSetLanguage = async (lang) => {
    setLangContext(lang);
    if (farmer && farmer.farmerId) {
      const updatedFarmer = { ...farmer, language: lang, preferredLanguage: lang };
      setFarmer(updatedFarmer);
      localStorage.setItem('farmer', JSON.stringify(updatedFarmer));
      try {
        await farmerService.updateLanguage?.(farmer.farmerId, lang);
      } catch (e) {
        // Soft fallback for offline/mock
      }
    }
  };

  // Unified Login handler
  const login = async (phone, password, forcedRole = null) => {
    setLoading(true);
    try {
      const res = await authService.login({ phone, password });
      const authToken = res.token || 'demo-token-' + Date.now();
      setToken(authToken);
      localStorage.setItem('token', authToken);
      
      const role = forcedRole || res.role || (phone === '9999999999' ? 'ADMIN' : (phone === '9876543211' || phone === '9849012345' ? 'OWNER' : 'FARMER'));
      setUserRole(role);
      localStorage.setItem('user_role', role);

      if (role === 'OWNER') {
        localStorage.setItem('owner_token', authToken);
        localStorage.setItem('owner_phone', phone);
        localStorage.setItem('owner_info', JSON.stringify(res));
      } else if (role === 'ADMIN') {
        localStorage.setItem('admin_token', authToken);
        localStorage.setItem('admin_phone', phone);
        localStorage.setItem('admin_info', JSON.stringify(res));
      } else if (role === 'FARMER') {
        try {
          const profile = await farmerService.getProfile(res.farmerId || 1);
          setFarmer(profile);
          localStorage.setItem('farmer', JSON.stringify(profile));
        } catch (e) {
          const fallbackFarmer = {
            farmerId: res.farmerId || 1,
            name: res.name || 'Ramesh Kumar',
            phone: phone,
            village: 'Kondapur Village',
            district: 'Medak',
            state: 'Telangana',
            language: language || 'te'
          };
          setFarmer(fallbackFarmer);
          localStorage.setItem('farmer', JSON.stringify(fallbackFarmer));
        }
      }

      return { success: true, res, role };
    } catch (err) {
      return { success: false, message: err.message || 'Login failed' };
    } finally {
      setLoading(false);
    }
  };

  // Register handler
  const register = async (farmerData) => {
    setLoading(true);
    try {
      const res = await authService.register(farmerData);
      setToken(res.token);
      localStorage.setItem('token', res.token);
      
      const role = res.role || farmerData.role || 'FARMER';
      setUserRole(role);
      localStorage.setItem('user_role', role);

      if (role === 'FARMER') {
        const profile = {
          farmerId: res.farmerId || Date.now(),
          name: farmerData.name,
          phone: farmerData.phone,
          village: farmerData.village || 'Demo Village',
          district: farmerData.district || 'Demo District',
          state: farmerData.state || 'Telangana',
          language: farmerData.language || language,
          latitude: farmerData.latitude || 17.385,
          longitude: farmerData.longitude || 78.4867
        };
        setFarmer(profile);
        localStorage.setItem('farmer', JSON.stringify(profile));
      }
      return { success: true, res, role };
    } catch (err) {
      return { success: false, message: err.message || 'Registration failed' };
    } finally {
      setLoading(false);
    }
  };

  // Logout handler
  const logout = () => {
    setToken(null);
    setUserRole(null);
    setFarmer(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user_role');
    localStorage.removeItem('farmer');
    localStorage.removeItem('active_booking');
  };

  // Save/Update Booking
  const saveBooking = (bookingData) => {
    setActiveBooking(bookingData);
    if (bookingData) {
      localStorage.setItem('active_booking', JSON.stringify(bookingData));
    } else {
      localStorage.removeItem('active_booking');
    }
  };

  // Role view switcher for portal selector
  const switchRole = (newRole) => {
    setUserRole(newRole);
    localStorage.setItem('user_role', newRole);
  };

  return (
    <AuthContext.Provider
      value={{
        token,
        userRole,
        farmer,
        language,
        activeBooking,
        loading,
        login,
        register,
        logout,
        switchRole,
        setLanguage: handleSetLanguage,
        setFarmer,
        saveBooking,
        isAuthenticated: !!token
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
