import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';
import BottomNavigation from './components/BottomNavigation';

// Farmer Pages
import LanguageSelection from './pages/LanguageSelection';
import Login from './pages/Login';
import Registration from './pages/Registration';
import Home from './pages/Home';
import FarmerProfile from './pages/FarmerProfile';
import CropDetails from './pages/CropDetails';
import FindCentres from './pages/FindCentres';
import CentreRecommendation from './pages/CentreRecommendation';
import SlotSelection from './pages/SlotSelection';
import BookingConfirmation from './pages/BookingConfirmation';
import DigitalToken from './pages/DigitalToken';
import QueueTracking from './pages/QueueTracking';
import ProcurementStatus from './pages/ProcurementStatus';
import PaymentStatus from './pages/PaymentStatus';
import Notifications from './pages/Notifications';

// Owner / Operator Pages
import OwnerLanding from './pages/owner/OwnerLanding';
import OwnerLogin from './pages/owner/OwnerLogin';
import OwnerDashboard from './pages/owner/OwnerDashboard';

// District Admin Pages
import AdminLanding from './pages/admin/AdminLanding';
import AdminLogin from './pages/admin/AdminLogin';
import AdminDashboard from './pages/admin/AdminDashboard';

import { LanguageProvider } from './context/LanguageContext';

// Protected Route Component with Role Authorization
const ProtectedRoute = ({ children, allowedRoles, fallbackLogin = "/login" }) => {
  const { isAuthenticated, userRole } = useAuth();
  
  if (!isAuthenticated) {
    return <Navigate to={fallbackLogin} replace />;
  }

  if (allowedRoles && !allowedRoles.includes(userRole)) {
    console.warn(`Unauthorized role access: ${userRole} attempted to access protected route.`);
    if (allowedRoles.includes('OWNER') || allowedRoles.includes('ADMIN')) {
      return <Navigate to={fallbackLogin} replace />;
    }
    return <Navigate to="/" replace />;
  }

  return children;
};

// Port-aware Root Route to support multi-host localhost dev servers
const RootRoute = () => {
  const port = window.location.port;
  const { isAuthenticated, userRole } = useAuth();

  if (port === '5174') {
    if (isAuthenticated && (userRole === 'OWNER' || userRole === 'ADMIN')) {
      return <Navigate to="/owner/dashboard" replace />;
    }
    return <OwnerLanding />;
  }

  if (port === '5175') {
    if (isAuthenticated && userRole === 'ADMIN') {
      return <Navigate to="/admin/dashboard" replace />;
    }
    return <AdminLanding />;
  }

  return <Home />;
};

export const AppContent = () => {
  return (
    <div className="min-h-screen flex flex-col bg-slate-50 pb-20 md:pb-6">
      <Navbar />
      <main className="flex-1 w-full max-w-5xl mx-auto">
        <Routes>
          {/* Public Common Auth Routes */}
          <Route path="/language" element={<LanguageSelection />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Registration />} />
          
          {/* Dedicated Owner & Admin Portals & Landing Pages */}
          <Route path="/owner" element={<OwnerLanding />} />
          <Route path="/owner/login" element={<OwnerLogin />} />
          
          <Route path="/admin" element={<AdminLanding />} />
          <Route path="/admin/login" element={<AdminLogin />} />

          {/* Farmer Portal Routes */}
          <Route path="/" element={<RootRoute />} />
          <Route path="/farmer" element={<Home />} />
          <Route path="/profile" element={<ProtectedRoute allowedRoles={['FARMER', 'OWNER', 'ADMIN']}><FarmerProfile /></ProtectedRoute>} />
          <Route path="/crop-details" element={<ProtectedRoute allowedRoles={['FARMER', 'OWNER', 'ADMIN']}><CropDetails /></ProtectedRoute>} />
          <Route path="/find-centres" element={<FindCentres />} />
          <Route path="/recommendation" element={<CentreRecommendation />} />
          <Route path="/slot-selection" element={<SlotSelection />} />
          <Route path="/booking-confirmation" element={<ProtectedRoute allowedRoles={['FARMER', 'OWNER', 'ADMIN']}><BookingConfirmation /></ProtectedRoute>} />
          <Route path="/token" element={<ProtectedRoute allowedRoles={['FARMER', 'OWNER', 'ADMIN']}><DigitalToken /></ProtectedRoute>} />
          <Route path="/queue" element={<ProtectedRoute allowedRoles={['FARMER', 'OWNER', 'ADMIN']}><QueueTracking /></ProtectedRoute>} />
          <Route path="/procurement-status" element={<ProtectedRoute allowedRoles={['FARMER', 'OWNER', 'ADMIN']}><ProcurementStatus /></ProtectedRoute>} />
          <Route path="/payment" element={<ProtectedRoute allowedRoles={['FARMER', 'OWNER', 'ADMIN']}><PaymentStatus /></ProtectedRoute>} />
          <Route path="/notifications" element={<ProtectedRoute allowedRoles={['FARMER', 'OWNER', 'ADMIN']}><Notifications /></ProtectedRoute>} />

          {/* Owner / Operator Portal Route */}
          <Route path="/owner/dashboard" element={<ProtectedRoute allowedRoles={['OWNER', 'ADMIN']} fallbackLogin="/owner/login"><OwnerDashboard /></ProtectedRoute>} />

          {/* District Admin Console Route */}
          <Route path="/admin/dashboard" element={<ProtectedRoute allowedRoles={['ADMIN']} fallbackLogin="/admin/login"><AdminDashboard /></ProtectedRoute>} />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
      <BottomNavigation />
    </div>
  );
};

export const App = () => {
  return (
    <LanguageProvider>
      <AuthProvider>
        <BrowserRouter>
          <AppContent />
        </BrowserRouter>
      </AuthProvider>
    </LanguageProvider>
  );
};

export default App;
