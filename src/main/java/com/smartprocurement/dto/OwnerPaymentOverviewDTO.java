package com.smartprocurement.dto;

import java.util.List;

public class OwnerPaymentOverviewDTO {
    private int todaysFarmersCount;
    private int completedProcurementsCount;
    private int pendingProcurementsCount;
    private int paymentsCompletedCount;
    private int paymentsPendingCount;
    private int paymentFailuresCount;
    private double totalDisbursedAmountRs;

    private List<PaymentDTO> transactions;

    public OwnerPaymentOverviewDTO() {
    }

    public int getTodaysFarmersCount() { return todaysFarmersCount; }
    public void setTodaysFarmersCount(int todaysFarmersCount) { this.todaysFarmersCount = todaysFarmersCount; }

    public int getCompletedProcurementsCount() { return completedProcurementsCount; }
    public void setCompletedProcurementsCount(int completedProcurementsCount) { this.completedProcurementsCount = completedProcurementsCount; }

    public int getPendingProcurementsCount() { return pendingProcurementsCount; }
    public void setPendingProcurementsCount(int pendingProcurementsCount) { this.pendingProcurementsCount = pendingProcurementsCount; }

    public int getPaymentsCompletedCount() { return paymentsCompletedCount; }
    public void setPaymentsCompletedCount(int paymentsCompletedCount) { this.paymentsCompletedCount = paymentsCompletedCount; }

    public int getPaymentsPendingCount() { return paymentsPendingCount; }
    public void setPaymentsPendingCount(int paymentsPendingCount) { this.paymentsPendingCount = paymentsPendingCount; }

    public int getPaymentFailuresCount() { return paymentFailuresCount; }
    public void setPaymentFailuresCount(int paymentFailuresCount) { this.paymentFailuresCount = paymentFailuresCount; }

    public double getTotalDisbursedAmountRs() { return totalDisbursedAmountRs; }
    public void setTotalDisbursedAmountRs(double totalDisbursedAmountRs) { this.totalDisbursedAmountRs = totalDisbursedAmountRs; }

    public List<PaymentDTO> getTransactions() { return transactions; }
    public void setTransactions(List<PaymentDTO> transactions) { this.transactions = transactions; }
}
