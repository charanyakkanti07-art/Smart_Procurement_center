import { bookingService } from './api';

/**
 * Handles Google Maps directions navigation flow for Farmers travelling to an assigned Procurement Centre.
 *
 * @param {Object} params
 * @param {Object} [params.booking] - Current booking object
 * @param {Object} [params.centre] - Assigned procurement centre object
 * @param {Function} [params.onStartLoading] - Callback when GPS acquisition starts
 * @param {Function} [params.onEndLoading] - Callback when navigation process ends
 * @param {Function} [params.onError] - Callback to report exact error message
 * @param {Function} [params.onSuccess] - Callback when navigation URL opens successfully
 */
export const handleStartTravelling = async ({
  booking,
  centre,
  onStartLoading,
  onEndLoading,
  onError,
  onSuccess
}) => {
  // Check Geolocation API availability
  if (!navigator.geolocation) {
    if (onError) onError("Location access is required to start navigation. Please enable location permission and try again.");
    return;
  }

  // Determine target procurement centre from props or localStorage
  let targetCentre = centre || booking?.centre || booking?.procurementCentre;
  if (!targetCentre) {
    try {
      const stored = localStorage.getItem('selected_centre');
      if (stored) targetCentre = JSON.parse(stored);
    } catch (e) {}
  }

  const destLat = targetCentre?.latitude || targetCentre?.lat;
  const destLng = targetCentre?.longitude || targetCentre?.lng;
  const destLocation = targetCentre?.location || targetCentre?.name || targetCentre?.centreName || booking?.centreName;

  let destinationParam = "";
  if (destLat && destLng && !isNaN(Number(destLat)) && !isNaN(Number(destLng))) {
    destinationParam = `${destLat},${destLng}`;
  } else if (destLocation && typeof destLocation === 'string' && destLocation.trim().length > 0) {
    destinationParam = encodeURIComponent(destLocation.trim());
  } else {
    if (onError) onError("Unable to find the procurement centre location. Please contact support.");
    return;
  }

  if (onStartLoading) onStartLoading();

  navigator.geolocation.getCurrentPosition(
    async (position) => {
      try {
        const latitude = position.coords.latitude;
        const longitude = position.coords.longitude;

        // 1. Construct Google Maps DRIVING direction URL using existing Google Maps URL API schema
        const originParam = `${latitude},${longitude}`;
        const googleMapsUrl = `https://www.google.com/maps/dir/?api=1&origin=${originParam}&destination=${destinationParam}&travelmode=driving`;

        // 2. Update travel status in backend database if bookingId exists
        const bookingId = booking?.bookingId || booking?.id || localStorage.getItem('active_booking_id');
        if (bookingId) {
          try {
            await bookingService.startTravelling(bookingId, latitude, longitude);
          } catch (backendErr) {
            console.warn("Travel status database update warning:", backendErr);
          }
        }

        // 3. Open Google Maps Navigation
        const navWindow = window.open(googleMapsUrl, '_blank', 'noopener,noreferrer');
        if (!navWindow) {
          window.location.href = googleMapsUrl;
        }

        if (onSuccess) onSuccess();
      } catch (err) {
        if (onError) onError("Unable to open navigation. Please try again.");
      } finally {
        if (onEndLoading) onEndLoading();
      }
    },
    (error) => {
      if (onEndLoading) onEndLoading();
      if (error.code === error.PERMISSION_DENIED) {
        if (onError) onError("Location access is required to start navigation. Please enable location permission and try again.");
      } else {
        if (onError) onError("Unable to find the procurement centre location. Please contact support.");
      }
    },
    {
      enableHighAccuracy: true,
      timeout: 10000,
      maximumAge: 0
    }
  );
};
