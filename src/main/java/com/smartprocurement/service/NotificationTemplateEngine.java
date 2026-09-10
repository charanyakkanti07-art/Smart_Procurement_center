package com.smartprocurement.service;

import com.smartprocurement.entity.NotificationEventType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class NotificationTemplateEngine {

    public static class TemplateResult {
        private final String title;
        private final String message;

        public TemplateResult(String title, String message) {
            this.title = title;
            this.message = message;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }
    }

    private boolean isTelugu(String lang) {
        if (lang == null) return false;
        String l = lang.trim();
        return "Telugu".equalsIgnoreCase(l) || "te".equalsIgnoreCase(l);
    }

    private boolean isHindi(String lang) {
        if (lang == null) return false;
        String l = lang.trim();
        return "Hindi".equalsIgnoreCase(l) || "hi".equalsIgnoreCase(l);
    }

    public TemplateResult render(NotificationEventType eventType, String language, Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        String lang = language != null ? language.trim() : "te";

        switch (eventType) {
            case BOOKING_CONFIRMATION:
                return renderBookingConfirmation(lang, params);
            case SLOT_REMINDER:
                return renderSlotReminder(lang, params);
            case QUEUE_APPROACHING:
                return renderQueueApproaching(lang, params);
            case START_TRAVELLING:
                return renderStartTravelling(lang, params);
            case QUEUE_CHANGED:
                return renderQueueChanged(lang, params);
            case CENTRE_CHANGED:
                return renderCentreChanged(lang, params);
            case RESCHEDULE_REQUEST:
                return renderRescheduleRequest(lang, params);
            case CANCELLATION_REQUEST:
                return renderCancellationRequest(lang, params);
            case OWNER_APPROVAL:
                return renderOwnerApproval(lang, params);
            case PROCUREMENT_COMPLETED:
                return renderProcurementCompleted(lang, params);
            case PAYMENT_COMPLETED:
                return renderPaymentCompleted(lang, params);
            default:
                return new TemplateResult("Notification", "State update for event: " + eventType);
        }
    }

    private TemplateResult renderBookingConfirmation(String lang, Map<String, Object> params) {
        String centre = str(params, "centreName", "Procurement Centre");
        String date = str(params, "bookingDate", "Today");
        String slot = str(params, "slot", "Morning Slot");

        if (isTelugu(lang)) {
            return new TemplateResult(
                    "బుకింగ్ ధృవీకరించబడింది",
                    String.format("మీ బుకింగ్ %s వద్ద %s తేదీన (%s) ధృవీకరించబడింది.", centre, date, slot)
            );
        } else if (isHindi(lang)) {
            return new TemplateResult(
                    "बुकिंग की पुष्टि",
                    String.format("आपकी बुकिंग %s पर %s (%s) के लिए कन्फर्म हो गई है।", centre, date, slot)
            );
        } else {
            return new TemplateResult(
                    "Booking Confirmed",
                    String.format("Your booking at %s for %s (%s) has been confirmed.", centre, date, slot)
            );
        }
    }

    private TemplateResult renderSlotReminder(String lang, Map<String, Object> params) {
        String centre = str(params, "centreName", "Procurement Centre");
        String slot = str(params, "slot", "Upcoming Slot");

        if (isTelugu(lang)) {
            return new TemplateResult(
                    "స్లాట్ జ్ఞాపిక",
                    String.format("గుర్తుచేసేది: %s వద్ద మీ స్లాట్ %s త్వరలో ప్రారంభం కానుంది.", centre, slot)
            );
        } else if (isHindi(lang)) {
            return new TemplateResult(
                    "स्लॉट अनुस्मारक",
                    String.format("रिमाइंडर: %s पर आपका स्लॉट %s जल्द ही शुरू होने वाला है।", centre, slot)
            );
        } else {
            return new TemplateResult(
                    "Upcoming Slot Reminder",
                    String.format("Reminder: Your slot %s at %s is coming up shortly.", slot, centre)
            );
        }
    }

    private TemplateResult renderQueueApproaching(String lang, Map<String, Object> params) {
        int position = integer(params, "queuePosition", 2);

        if (isTelugu(lang)) {
            return new TemplateResult(
                    "క్యూ సమాచారం",
                    String.format("మీ క్యూ స్థానం #%d కు చేరుకుంది. దయచేసి సిద్ధంగా ఉండండి.", position)
            );
        } else if (isHindi(lang)) {
            return new TemplateResult(
                    "कतार सूचना",
                    String.format("आपकी कतार स्थिति #%d तक पहुँच गई है। कृपया तैयार रहें।", position)
            );
        } else {
            return new TemplateResult(
                    "Queue Approaching",
                    String.format("Your queue position is approaching #%d. Please get ready.", position)
            );
        }
    }

    private TemplateResult renderStartTravelling(String lang, Map<String, Object> params) {
        String centre = str(params, "centreName", "Procurement Centre");
        int waitMins = integer(params, "estimatedWaitMinutes", 30);

        if (isTelugu(lang)) {
            return new TemplateResult(
                    "ప్రయాణం ప్రారంభించండి",
                    String.format("సిఫార్సు: %s కు ప్రయాణాన్ని ప్రారంభించండి. అంచనా వేచి ఉండే సమయం: %d నిమిషాలు.", centre, waitMins)
            );
        } else if (isHindi(lang)) {
            return new TemplateResult(
                    "यात्रा शुरू करें",
                    String.format("अनुशंसा: %s के लिए यात्रा शुरू करें। अनुमानित प्रतीक्षा समय: %d मिनट।", centre, waitMins)
            );
        } else {
            return new TemplateResult(
                    "Start Travelling",
                    String.format("Recommendation: Start travelling to %s. Estimated waiting time: %d minutes.", centre, waitMins)
            );
        }
    }

    private TemplateResult renderQueueChanged(String lang, Map<String, Object> params) {
        int oldPos = integer(params, "oldPosition", 8);
        int newPos = integer(params, "newPosition", 5);
        int waitMins = integer(params, "estimatedWaitMinutes", 25);

        if (isTelugu(lang)) {
            return new TemplateResult(
                    "క్యూ స్థానం మారింది",
                    String.format("మీ క్యూ స్థానం %d నుండి %d కి మారింది. అంచనా వేచి ఉండే సమయం: %d నిమిషాలు.", oldPos, newPos, waitMins)
            );
        } else if (isHindi(lang)) {
            return new TemplateResult(
                    "कतार की स्थिति बदली",
                    String.format("आपकी कतार स्थिति %d से बदलकर %d हो गई है। अनुमानित प्रतीक्षा समय: %d मिनट।", oldPos, newPos, waitMins)
            );
        } else {
            return new TemplateResult(
                    "Queue Position Changed",
                    String.format("Your queue position changed from %d to %d. Estimated waiting time: %d minutes.", oldPos, newPos, waitMins)
            );
        }
    }

    private TemplateResult renderCentreChanged(String lang, Map<String, Object> params) {
        String oldCentre = str(params, "oldCentre", "Centre A");
        String newCentre = str(params, "newCentre", "Centre B");

        if (isTelugu(lang)) {
            return new TemplateResult(
                    "కొనుగోలు కేంద్రం మారింది",
                    String.format("మీ కొనుగోలు కేంద్రం %s నుండి %s కి మార్చబడింది.", oldCentre, newCentre)
            );
        } else if (isHindi(lang)) {
            return new TemplateResult(
                    "खरीद केंद्र बदला गया",
                    String.format("आपका खरीद केंद्र %s से बदलकर %s कर दिया गया है।", oldCentre, newCentre)
            );
        } else {
            return new TemplateResult(
                    "Procurement Centre Changed",
                    String.format("Your procurement centre changed from %s to %s.", oldCentre, newCentre)
            );
        }
    }

    private TemplateResult renderRescheduleRequest(String lang, Map<String, Object> params) {
        String slot = str(params, "slot", "New Slot");
        String date = str(params, "bookingDate", "Date");

        if (isTelugu(lang)) {
            return new TemplateResult(
                    "రీషెడ్యూల్ అభ్యర్థన సబ్మిట్ చేయబడింది",
                    String.format("తేదీ %s (%s) కొరకు రీషెడ్యూల్ అభ్యర్థన స్వీకరించబడింది.", date, slot)
            );
        } else if (isHindi(lang)) {
            return new TemplateResult(
                    "रीशेड्यूल अनुरोध प्रस्तुत",
                    String.format("तिथि %s (%s) के लिए रीशेड्यूल अनुरोध प्राप्त हुआ है।", date, slot)
            );
        } else {
            return new TemplateResult(
                    "Reschedule Request Submitted",
                    String.format("Reschedule request received for %s (%s).", date, slot)
            );
        }
    }

    private TemplateResult renderCancellationRequest(String lang, Map<String, Object> params) {
        if (isTelugu(lang)) {
            return new TemplateResult(
                    "రద్దు అభ్యర్థన స్వీకరించబడింది",
                    "మీ రద్దు అభ్యర్థన స్వీకరించబడింది మరియు పరిశీలనలో ఉంది."
            );
        } else if (isHindi(lang)) {
            return new TemplateResult(
                    "रद्दीकरण अनुरोध प्राप्त",
                    "आपका रद्दीकरण अनुरोध प्राप्त हो गया है और समीक्षाधीन है।"
            );
        } else {
            return new TemplateResult(
                    "Cancellation Request Submitted",
                    "Your cancellation request has been received and is under review."
            );
        }
    }

    private TemplateResult renderOwnerApproval(String lang, Map<String, Object> params) {
        String action = str(params, "action", "approved");

        if (isTelugu(lang)) {
            return new TemplateResult(
                    "కేంద్ర అధికారి ఆమోదం",
                    String.format("మీ అభ్యర్థన కేంద్ర అధికారి ద్వారా %s చేయబడింది.", action)
            );
        } else if (isHindi(lang)) {
            return new TemplateResult(
                    "केंद्र अधिकारी की स्वीकृति",
                    String.format("आपका अनुरोध केंद्र अधिकारी द्वारा %s कर दिया गया है।", action)
            );
        } else {
            return new TemplateResult(
                    "Owner Approval Completed",
                    String.format("Your request has been %s by the centre official.", action)
            );
        }
    }

    private TemplateResult renderProcurementCompleted(String lang, Map<String, Object> params) {
        double quantity = doubleVal(params, "quantity", 0.0);
        String crop = str(params, "cropType", "Crop");

        if (isTelugu(lang)) {
            return new TemplateResult(
                    "సేకరణ పూర్తయింది",
                    String.format("మీ %.1f కిలోల %s సేకరణ ప్రక్రియ విజయవంతంగా పూర్తయింది.", quantity, crop)
            );
        } else if (isHindi(lang)) {
            return new TemplateResult(
                    "खरीद प्रक्रिया पूरी हुई",
                    String.format("आपकी %.1f किग्रा %s की खरीद प्रक्रिया सफलतापूर्वक पूरी हो गई है।", quantity, crop)
            );
        } else {
            return new TemplateResult(
                    "Procurement Completed",
                    String.format("Procurement completed successfully for %.1f kg of %s.", quantity, crop)
            );
        }
    }

    private TemplateResult renderPaymentCompleted(String lang, Map<String, Object> params) {
        double amount = doubleVal(params, "amount", 0.0);
        String ref = str(params, "paymentRef", "TXN-1001");

        if (isTelugu(lang)) {
            return new TemplateResult(
                    "చెల్లింపు పూర్తయింది",
                    String.format("రూ. %.2f చెల్లింపు విజయవంతంగా పూర్తయింది. రెఫరెన్స్: %s.", amount, ref)
            );
        } else if (isHindi(lang)) {
            return new TemplateResult(
                    "भुगतान पूरा हुआ",
                    String.format("रु %.2f का भुगतान सफलतापूर्वक संसाधित किया गया। संदर्भ: %s।", amount, ref)
            );
        } else {
            return new TemplateResult(
                    "Payment Completed",
                    String.format("Payment of Rs. %.2f has been successfully processed. Ref: %s.", amount, ref)
            );
        }
    }

    private String str(Map<String, Object> map, String key, String defaultValue) {
        Object val = map.get(key);
        return val != null ? val.toString() : defaultValue;
    }

    private int integer(Map<String, Object> map, String key, int defaultValue) {
        Object val = map.get(key);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        } else if (val != null) {
            try {
                return Integer.parseInt(val.toString());
            } catch (Exception ignored) {
            }
        }
        return defaultValue;
    }

    private double doubleVal(Map<String, Object> map, String key, double defaultValue) {
        Object val = map.get(key);
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        } else if (val != null) {
            try {
                return Double.parseDouble(val.toString());
            } catch (Exception ignored) {
            }
        }
        return defaultValue;
    }
}
