import React, { createContext, useContext, useState } from 'react';
import { authService, farmerService } from '../services/api';
import { useLanguage } from './LanguageContext';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const { language, setLanguage: setLangContext } = useLanguage();
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [userRole, setUserRole] = useState(localStorage.getItem('user_role'));
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
        // Soft fallback for offline
      }
    }
  };

  // Unified Login handler — role comes ONLY from backend response
  const login = async (phone, password, forcedRole = null) => {
    setLoading(true);
    try {
      const res = await authService.login({ phone, password });

      // Backend must return a real JWT token
      const authToken = res.token;
      if (!authToken) {
        return { success: false, message: res.message || 'Login failed: no token received.' };
      }

      setToken(authToken);
      localStorage.setItem('token', authToken);

      // Role comes from the backend response
      const role = res.role || forcedRole;
      if (!role) {
        return { success: false, message: 'Login failed: role not returned from server.' };
      }
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
          const profile = await farmerService.getProfile(res.farmerId);
          setFarmer(profile);
          localStorage.setItem('farmer', JSON.stringify(profile));
        } catch (e) {
          const fallbackFarmer = {
            farmerId: res.farmerId,
            name: res.name || 'Farmer',
            phone: phone,
            language: language || 'te'
          };
          setFarmer(fallbackFarmer);
          localStorage.setItem('farmer', JSON.stringify(fallbackFarmer));
        }
      }

      return { success: true, res, role };
    } catch (err) {
      const serverMsg = err.response?.data?.message || err.message || 'Login failed. Please try again.';
      return { success: false, message: serverMsg };
    } finally {
      setLoading(false);
    }
  };

  // Register handler
  const register = async (farmerData) => {
    setLoading(true);
    try {
      const res = await authService.register(farmerData);

      const role = res.role || farmerData.role || 'FARMER';
      setUserRole(role);
      localStorage.setItem('user_role', role);

      // Only store token if one was returned (OWNER pending will have null token)
      if (res.token) {
        setToken(res.token);
        localStorage.setItem('token', res.token);
      }

      if (role === 'FARMER' && res.token) {
        const profile = {
          farmerId: res.farmerId,
          name: farmerData.name,
          phone: farmerData.phone,
          village: farmerData.village || '',
          district: farmerData.district || '',
          state: farmerData.state || 'Telangana',
          language: farmerData.language || language,
          latitude: farmerData.latitude || 17.385,
          longitude: farmerData.longitude || 78.4867
        };
        setFarmer(profile);
        localStorage.setItem('farmer', JSON.stringify(profile));
      }

      return { success: true, res, role, message: res.message };
    } catch (err) {
      const serverMsg = err.response?.data?.message || err.message || 'Registration failed. Please try again.';
      return { success: false, message: serverMsg };
    } finally {
      setLoading(false);
    }
  };

  // Logout handler
  const logout = () => {
    setToken(null);
    setUserRole(null);
    setFarmer(null);
    setActiveBooking(null);
    [
      'token', 'user_role', 'farmer', 'active_booking',
      'owner_token', 'owner_phone', 'owner_info',
      'admin_token', 'admin_phone', 'admin_info'
    ].forEach(key => localStorage.removeItem(key));
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

  // Role view switcher (for UI portal switching — not a security boundary)
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
