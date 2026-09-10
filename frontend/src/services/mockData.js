// Realistic Indian Agriculture & Procurement Mock Data

export const mockFarmer = {
  farmerId: 1,
  name: "Ramesh Kumar",
  phone: "9876543210",
  village: "Kondapur Village",
  district: "Medak District",
  state: "Telangana",
  language: "English",
  latitude: 17.3850,
  longitude: 78.4867,
  createdAt: "2026-09-01T10:00:00"
};

export const mockCrops = [
  {
    cropId: 1,
    cropType: "Paddy (Kharif)",
    variety: "BPT 5204 (Samba Mahsuri)",
    quantity: 500,
    unit: "kg",
    expectedHarvestDate: "2026-09-20",
    farmerId: 1
  },
  {
    cropId: 2,
    cropType: "Maize",
    variety: "Hybrid Yellow",
    quantity: 300,
    unit: "kg",
    expectedHarvestDate: "2026-10-05",
    farmerId: 1
  }
];

export const mockCentres = [
  {
    centreId: 1,
    name: "ABC Procurement Centre",
    location: "Kondapur Main Road, Medak",
    distanceKm: 12.4,
    travelTimeMinutes: 42,
    currentLoadPercent: 68,
    totalCapacityKg: 1000,
    currentLoadKg: 680,
    availableSlotsCount: 14,
    status: "AVAILABLE", // AVAILABLE, BUSY, NEAR_CAPACITY, OVERLOADED, CLOSED
    reason: "Normal operations. Good capacity available.",
    latitude: 17.3912,
    longitude: 78.4920,
    contactNumber: "+91 98490 12345"
  },
  {
    centreId: 2,
    name: "Centre B (Regional Grain Mandi)",
    location: "Sangareddy Highway, Medak",
    distanceKm: 15.2,
    travelTimeMinutes: 48,
    currentLoadPercent: 35,
    totalCapacityKg: 2000,
    currentLoadKg: 700,
    availableSlotsCount: 28,
    status: "AVAILABLE",
    reason: "Lower queue and high available procurement capacity.",
    latitude: 17.4100,
    longitude: 78.5100,
    contactNumber: "+91 98490 67890"
  },
  {
    centreId: 3,
    name: "Centre C (North Farmers Hub)",
    location: "Tupran Bypass, Medak",
    distanceKm: 18.1,
    travelTimeMinutes: 52,
    currentLoadPercent: 92,
    totalCapacityKg: 1000,
    currentLoadKg: 920,
    availableSlotsCount: 2,
    status: "OVERLOADED",
    reason: "Centre is currently overloaded with 12 farmers waiting.",
    latitude: 17.4350,
    longitude: 78.5300,
    contactNumber: "+91 98490 99999"
  }
];

export const mockSlots = [
  {
    slotId: "S1",
    date: "2026-09-20",
    timeWindow: "09:00 AM – 10:00 AM",
    status: "AVAILABLE",
    remainingCapacityKg: 300,
    farmersBooked: 4,
    estimatedWaitMinutes: 15
  },
  {
    slotId: "S2",
    date: "2026-09-20",
    timeWindow: "10:00 AM – 11:00 AM",
    status: "AVAILABLE",
    remainingCapacityKg: 200,
    farmersBooked: 6,
    estimatedWaitMinutes: 25
  },
  {
    slotId: "S3",
    date: "2026-09-20",
    timeWindow: "11:00 AM – 12:00 PM",
    status: "NEAR_CAPACITY",
    remainingCapacityKg: 50,
    farmersBooked: 9,
    estimatedWaitMinutes: 45
  },
  {
    slotId: "S4",
    date: "2026-09-20",
    timeWindow: "12:00 PM – 01:00 PM",
    status: "FULL",
    remainingCapacityKg: 0,
    farmersBooked: 12,
    estimatedWaitMinutes: 60
  }
];

export const mockActiveBooking = {
  bookingId: 101,
  tokenNumber: "#103",
  farmerId: 1,
  farmerName: "Ramesh Kumar",
  cropId: 1,
  cropType: "Paddy (Kharif)",
  quantityKg: 500,
  centreId: 1,
  centreName: "ABC Procurement Centre",
  centreLocation: "Kondapur Main Road, Medak",
  bookingDate: "2026-09-20",
  slotWindow: "10:00 AM – 11:00 AM",
  status: "CONFIRMED", // CONFIRMED, WAITING, ARRIVED, COMPLETED, CANCELLED
  farmersAhead: 4,
  currentServingToken: "#99",
  estimatedServiceTime: "11:35 AM",
  travelTimeMinutes: 42,
  recommendedDepartureTime: "10:45 AM",
  geofenceStatus: "NEAR_CENTRE", // OUTSIDE, NEAR_CENTRE, AT_CENTRE
  geofenceDistanceMeters: 450,
  qrCodeData: "SPP-TOKEN-103-FARMER-1-CENTRE-1-QTY-500",
  createdAt: "2026-09-10T08:30:00"
};

export const mockQueueTracker = {
  tokenNumber: "#103",
  currentServing: "#99",
  farmersAhead: 4,
  estimatedWaitMinutes: 55,
  estimatedProcurementTime: "11:35 AM",
  travelTimeMinutes: 42,
  recommendedDeparture: "10:45 AM",
  queueItems: [
    { token: "#99", status: "SERVING", label: "Currently Serving" },
    { token: "#100", status: "WAITING", label: "In Line" },
    { token: "#101", status: "WAITING", label: "In Line" },
    { token: "#102", status: "WAITING", label: "In Line" },
    { token: "#103", status: "YOUR_TOKEN", label: "You are here" }
  ]
};

export const mockProcurementTimeline = [
  { stage: "BOOKED", label: "Booking Confirmed", completed: true, timestamp: "08:30 AM" },
  { stage: "ARRIVED", label: "Farmer Arrived at Centre", completed: true, timestamp: "10:42 AM" },
  { stage: "QUALITY_CHECK", label: "Quality Check & Moisture Testing", completed: true, timestamp: "11:05 AM" },
  { stage: "WEIGHING", label: "Weighing & Verification", completed: false, current: true, timestamp: "In Progress" },
  { stage: "COMPLETED", label: "Procurement Completed", completed: false, timestamp: "Pending" },
  { stage: "PAYMENT_PROCESSING", label: "Payment Processing", completed: false, timestamp: "Pending" },
  { stage: "PAYMENT_COMPLETED", label: "Payment Transferred to Bank", completed: false, timestamp: "Pending" }
];

export const mockPaymentRecord = {
  transactionId: "TXN984210385",
  bookingId: 101,
  farmerName: "Ramesh Kumar",
  cropType: "Paddy (Grade A)",
  quantityKg: 500,
  mspRatePerQuintal: 2500,
  totalAmountRs: 12500,
  bankName: "State Bank of India",
  accountLastFour: "4821",
  ifscCode: "SBIN0004521",
  procurementStatus: "COMPLETED",
  paymentStatus: "PROCESSING", // PENDING, PROCESSING, COMPLETED, FAILED
  initiatedAt: "2026-09-10T11:45:00",
  estimatedCreditTime: "Within 24 Hours"
};

export const mockNotifications = [
  {
    id: 1,
    title: "Digital Token Generated",
    message: "Your procurement token #103 for Paddy (500 kg) at ABC Procurement Centre has been issued.",
    time: "10 mins ago",
    unread: true,
    type: "TOKEN"
  },
  {
    id: 2,
    title: "Smart Departure Alert",
    message: "Based on current queue (4 farmers ahead) and 42 min travel time, please leave by 10:45 AM.",
    time: "25 mins ago",
    unread: true,
    type: "DEPARTURE"
  },
  {
    id: 3,
    title: "Centre Recommendation Update",
    message: "Centre B currently has lower waiting time (35% load vs 68%). Consider Centre B for faster processing.",
    time: "1 hour ago",
    unread: false,
    type: "RECOMMENDATION"
  },
  {
    id: 4,
    title: "Procurement & Payment Status",
    message: "Payment of ₹12,500 for Paddy procurement #101 has been initiated to SBI A/C ending in 4821.",
    time: "2 hours ago",
    unread: false,
    type: "PAYMENT"
  }
];

export const mockProcurements = [
  {
    procurementId: 1,
    procurementCode: "PR-10001",
    bookingId: 101,
    farmerName: "Ramesh Kumar",
    farmerPhone: "9876543210",
    cropType: "Paddy (Kharif)",
    grossWeight: 520.0,
    tareWeight: 20.0,
    netQuantity: 500.0,
    qualityGrade: "Grade A",
    qualityStatus: "ACCEPTED",
    ratePerUnit: 25.0,
    totalAmount: 12500.0,
    status: "COMPLETED",
    createdAt: "2026-09-10T10:15:00",
    verifiedBy: "Operator Ravi"
  },
  {
    procurementId: 2,
    procurementCode: "PR-10002",
    bookingId: 102,
    farmerName: "Venkat Rao",
    farmerPhone: "9876543211",
    cropType: "Wheat",
    grossWeight: 825.0,
    tareWeight: 25.0,
    netQuantity: 800.0,
    qualityGrade: "Grade A",
    qualityStatus: "ACCEPTED",
    ratePerUnit: 23.0,
    totalAmount: 18400.0,
    status: "COMPLETED",
    createdAt: "2026-09-10T11:00:00",
    verifiedBy: "Operator Ravi"
  },
  {
    procurementId: 3,
    procurementCode: "PR-10003",
    bookingId: 103,
    farmerName: "Lakshmi Devi",
    farmerPhone: "9876543212",
    cropType: "Maize",
    grossWeight: 415.0,
    tareWeight: 15.0,
    netQuantity: 400.0,
    qualityGrade: "Grade B",
    qualityStatus: "ACCEPTED",
    ratePerUnit: 23.0,
    totalAmount: 9200.0,
    status: "COMPLETED",
    createdAt: "2026-09-10T11:30:00",
    verifiedBy: "Operator Ravi"
  },
  {
    procurementId: 4,
    procurementCode: "PR-10004",
    bookingId: 104,
    farmerName: "K. Satyanarayana",
    farmerPhone: "9876543213",
    cropType: "Cotton",
    grossWeight: 620.0,
    tareWeight: 20.0,
    netQuantity: 600.0,
    qualityGrade: "Grade A",
    qualityStatus: "ACCEPTED",
    ratePerUnit: 60.0,
    totalAmount: 36000.0,
    status: "QUALITY_CHECK",
    createdAt: "2026-09-10T12:00:00",
    verifiedBy: "Operator Ravi"
  }
];

export const mockPaymentsHistory = [
  {
    paymentId: 1,
    paymentCode: "PAY-98421",
    procurementId: 1,
    procurementCode: "PR-10001",
    farmerName: "Ramesh Kumar",
    cropType: "Paddy (Kharif)",
    netQuantity: 500.0,
    qualityGrade: "Grade A",
    ratePerUnit: 25.0,
    amount: 12500.0,
    paymentMethod: "DIRECT_BENEFIT_TRANSFER",
    status: "COMPLETED",
    transactionId: "TXN-8F92A71",
    bankName: "State Bank of India",
    accountLastFour: "4821",
    ifscCode: "SBIN0004521",
    createdAt: "2026-09-10T10:20:00",
    completedAt: "2026-09-10T10:22:15"
  },
  {
    paymentId: 2,
    paymentCode: "PAY-98422",
    procurementId: 2,
    procurementCode: "PR-10002",
    farmerName: "Venkat Rao",
    cropType: "Wheat",
    netQuantity: 800.0,
    qualityGrade: "Grade A",
    ratePerUnit: 23.0,
    amount: 18400.0,
    paymentMethod: "DIRECT_BENEFIT_TRANSFER",
    status: "PENDING",
    transactionId: null,
    bankName: "Andhra Pragathi Grameena Bank",
    accountLastFour: "9012",
    ifscCode: "APGB0002104",
    createdAt: "2026-09-10T11:05:00",
    completedAt: null
  },
  {
    paymentId: 3,
    paymentCode: "PAY-98423",
    procurementId: 3,
    procurementCode: "PR-10003",
    farmerName: "Lakshmi Devi",
    cropType: "Maize",
    netQuantity: 400.0,
    qualityGrade: "Grade B",
    ratePerUnit: 23.0,
    amount: 9200.0,
    paymentMethod: "DIRECT_BENEFIT_TRANSFER",
    status: "FAILED",
    failureReason: "Bank network timeout during DBT processing.",
    transactionId: null,
    bankName: "Union Bank of India",
    accountLastFour: "3156",
    ifscCode: "UBIN0534211",
    createdAt: "2026-09-10T11:35:00",
    completedAt: null
  }
];

