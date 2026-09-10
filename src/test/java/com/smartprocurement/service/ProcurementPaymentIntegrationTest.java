package com.smartprocurement.service;

import com.smartprocurement.dto.*;
import com.smartprocurement.entity.*;
import com.smartprocurement.exception.BadRequestException;
import com.smartprocurement.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProcurementPaymentIntegrationTest {

    @Autowired
    private ProcurementService procurementService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private CentreRepository centreRepository;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ProcurementRepository procurementRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private Farmer testFarmer;
    private ProcurementCentre testCentre;
    private Crop testCrop;
    private Booking testBooking;

    @BeforeEach
    @Transactional
    public void setup() {
        paymentRepository.deleteAll();
        procurementRepository.deleteAll();
        bookingRepository.deleteAll();
        cropRepository.deleteAll();
        centreRepository.deleteAll();
        farmerRepository.deleteAll();

        testFarmer = farmerRepository.save(Farmer.builder()
                .name("Ramesh Kumar")
                .phone("9876543210")
                .language("te")
                .build());

        testCentre = centreRepository.save(ProcurementCentre.builder()
                .name("Kondapur Mandi")
                .location("Medak")
                .totalCapacity(50.0)
                .currentLoad(0.0)
                .status(CentreStatus.ACTIVE)
                .build());

        testCrop = cropRepository.save(Crop.builder()
                .cropType("Paddy")
                .quantity(500.0)
                .farmer(testFarmer)
                .build());

        testBooking = bookingRepository.save(Booking.builder()
                .farmer(testFarmer)
                .centre(testCentre)
                .crop(testCrop)
                .quantity(500.0)
                .bookingDate(LocalDate.now())
                .slot("10:00 AM - 11:00 AM")
                .status(BookingStatus.CONFIRMED)
                .build());
    }

    @Test
    @Transactional
    public void testCompleteProcurementAndPaymentWorkflow() {
        Long bookingId = testBooking.getBookingId();

        // Step 1: Farmer Arrival
        ProcurementDTO arrived = procurementService.markArrival(bookingId);
        assertNotNull(arrived);
        assertEquals(ProcurementStatus.ARRIVED, arrived.getStatus());

        // Step 2: Verification
        ProcurementDTO verified = procurementService.verifyFarmer(bookingId, "Operator Ramesh");
        assertEquals(ProcurementStatus.VERIFIED, verified.getStatus());
        assertEquals("Operator Ramesh", verified.getVerifiedBy());

        // Step 3: Weighing (Gross = 520 kg, Tare = 20 kg -> Net = 500 kg)
        ProcurementRequestDTO weighReq = new ProcurementRequestDTO();
        weighReq.setGrossWeight(520.0);
        weighReq.setTareWeight(20.0);
        weighReq.setUnit("kg");
        weighReq.setOperatorName("Weighing Scale #1");

        ProcurementDTO weighed = procurementService.recordWeighing(bookingId, weighReq);
        assertEquals(ProcurementStatus.WEIGHING, weighed.getStatus());
        assertEquals(520.0, weighed.getGrossWeight());
        assertEquals(20.0, weighed.getTareWeight());
        assertEquals(500.0, weighed.getNetQuantity());

        // Step 4: Quality Check (Grade A -> Rate = Rs.25/kg -> Amount = Rs.12,500)
        ProcurementRequestDTO qualityReq = new ProcurementRequestDTO();
        qualityReq.setQualityGrade("Grade A");
        qualityReq.setQualityParameters("Moisture: 13.5%, Foreign Matter: <1%");
        qualityReq.setQualityStatus(QualityStatus.ACCEPTED);
        qualityReq.setOperatorName("Quality Inspector Suresh");

        ProcurementDTO qualityDone = procurementService.recordQualityCheck(bookingId, qualityReq);
        assertEquals(ProcurementStatus.QUALITY_CHECK, qualityDone.getStatus());
        assertEquals(25.0, qualityDone.getRatePerUnit());
        assertEquals(12500.0, qualityDone.getTotalAmount());

        // Step 5: Procurement Confirmation (Generates PR-XXXXX & triggers payment initiation)
        ProcurementDTO confirmed = procurementService.confirmProcurement(bookingId, "Operator Ramesh");
        assertEquals(ProcurementStatus.COMPLETED, confirmed.getStatus());
        assertNotNull(confirmed.getProcurementCode());
        assertTrue(confirmed.getProcurementCode().startsWith("PR-"));

        // Verify Payment Initiated / Pending
        var payments = paymentRepository.findByProcurementProcurementId(confirmed.getProcurementId());
        assertFalse(payments.isEmpty());
        Payment initPayment = payments.get(0);
        assertEquals(PaymentStatus.PENDING, initPayment.getStatus());
        assertEquals(12500.0, initPayment.getAmount());

        // Step 6: Simulate Payment Success
        PaymentDTO successPayment = paymentService.simulatePaymentSuccess(initPayment.getPaymentId());
        assertEquals(PaymentStatus.COMPLETED, successPayment.getStatus());
        assertNotNull(successPayment.getTransactionId());
        assertTrue(successPayment.getTransactionId().startsWith("TXN-"));

        // Verify Farmer Payment History
        var history = paymentService.getFarmerPaymentHistory(testFarmer.getFarmerId());
        assertEquals(1, history.size());
        assertEquals(PaymentStatus.COMPLETED, history.get(0).getStatus());
        assertEquals(12500.0, history.get(0).getAmount());
    }

    @Test
    @Transactional
    public void testPaymentFailureAndRetryWorkflow() {
        Long bookingId = testBooking.getBookingId();
        procurementService.markArrival(bookingId);
        procurementService.verifyFarmer(bookingId, "Op");

        ProcurementRequestDTO weigh = new ProcurementRequestDTO();
        weigh.setGrossWeight(420.0);
        weigh.setTareWeight(20.0);
        procurementService.recordWeighing(bookingId, weigh);

        ProcurementRequestDTO q = new ProcurementRequestDTO();
        q.setQualityGrade("Grade A");
        q.setQualityStatus(QualityStatus.ACCEPTED);
        procurementService.recordQualityCheck(bookingId, q);

        ProcurementDTO confirmed = procurementService.confirmProcurement(bookingId, "Op");
        Long paymentId = paymentRepository.findByProcurementProcurementId(confirmed.getProcurementId()).get(0).getPaymentId();

        // Simulate Payment Failure
        PaymentDTO failed = paymentService.simulatePaymentFailure(paymentId, "Bank gateway network timeout.");
        assertEquals(PaymentStatus.FAILED, failed.getStatus());
        assertEquals("Bank gateway network timeout.", failed.getFailureReason());

        // Retry Payment
        PaymentDTO retried = paymentService.retryPayment(paymentId);
        assertEquals(PaymentStatus.PENDING, retried.getStatus());
        assertNull(retried.getFailureReason());

        // Complete retried payment
        PaymentDTO completed = paymentService.simulatePaymentSuccess(paymentId);
        assertEquals(PaymentStatus.COMPLETED, completed.getStatus());
    }

    @Test
    @Transactional
    public void testDuplicatePaymentPreventionAndIdempotency() {
        Long bookingId = testBooking.getBookingId();
        procurementService.markArrival(bookingId);
        procurementService.verifyFarmer(bookingId, "Op");

        ProcurementRequestDTO weigh = new ProcurementRequestDTO();
        weigh.setGrossWeight(520.0);
        weigh.setTareWeight(20.0);
        procurementService.recordWeighing(bookingId, weigh);

        ProcurementRequestDTO q = new ProcurementRequestDTO();
        q.setQualityGrade("Grade A");
        q.setQualityStatus(QualityStatus.ACCEPTED);
        procurementService.recordQualityCheck(bookingId, q);

        ProcurementDTO confirmed = procurementService.confirmProcurement(bookingId, "Op");
        Long paymentId = paymentRepository.findByProcurementProcurementId(confirmed.getProcurementId()).get(0).getPaymentId();

        PaymentDTO firstSuccess = paymentService.simulatePaymentSuccess(paymentId);
        assertEquals(PaymentStatus.COMPLETED, firstSuccess.getStatus());

        // Attempt second payment initiation for same procurement
        PaymentDTO secondInit = paymentService.initiatePayment(confirmed.getProcurementId(), PaymentMethod.DIRECT_BENEFIT_TRANSFER);
        assertEquals(firstSuccess.getPaymentId(), secondInit.getPaymentId());
        assertEquals(PaymentStatus.COMPLETED, secondInit.getStatus());

        // Ensure only 1 payment record exists in repository
        assertEquals(1, paymentRepository.findByProcurementProcurementId(confirmed.getProcurementId()).size());
    }

    @Test
    @Transactional
    public void testIncompleteProcurementValidation() {
        Long bookingId = testBooking.getBookingId();
        procurementService.markArrival(bookingId);
        procurementService.verifyFarmer(bookingId, "Op");

        // Attempting confirmation without weight and quality check should throw BadRequestException
        assertThrows(BadRequestException.class, () -> {
            procurementService.confirmProcurement(bookingId, "Op");
        });
    }
}
