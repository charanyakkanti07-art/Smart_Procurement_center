package com.smartprocurement.dto;

import com.smartprocurement.entity.CentreStatus;
import java.util.List;

public class OwnerDashboardDTO {
    private Long centreId;
    private String centreName;
    private String centreLocation;
    private Double totalCapacity;
    private Double currentLoad;
    private Double availableCapacity;
    private CentreStatus status;
    private boolean isOverloaded;
    private String overloadReason;

    // Statistics Counter
    private long todaysFarmersCount;
    private long waitingCount;
    private long calledCount;
    private long arrivedCount;
    private long processingCount;
    private long completedCount;
    private long cancelledCount;
    private long reschedulingCount;
    private long pendingApprovalsCount;

    // Main Sections Data
    private BookingResponse currentProcessing;
    private List<BookingResponse> queueList;
    private List<BookingResponse> todaysFarmers;
    private List<BookingResponse> cancellationRequests;
    private List<BookingResponse> reschedulingRequests;
    private List<BookingResponse> pendingApprovals;

    public OwnerDashboardDTO() {
    }

    public Long getCentreId() { return centreId; }
    public void setCentreId(Long centreId) { this.centreId = centreId; }

    public String getCentreName() { return centreName; }
    public void setCentreName(String centreName) { this.centreName = centreName; }

    public String getCentreLocation() { return centreLocation; }
    public void setCentreLocation(String centreLocation) { this.centreLocation = centreLocation; }

    public Double getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(Double totalCapacity) { this.totalCapacity = totalCapacity; }

    public Double getCurrentLoad() { return currentLoad; }
    public void setCurrentLoad(Double currentLoad) { this.currentLoad = currentLoad; }

    public Double getAvailableCapacity() { return availableCapacity; }
    public void setAvailableCapacity(Double availableCapacity) { this.availableCapacity = availableCapacity; }

    public CentreStatus getStatus() { return status; }
    public void setStatus(CentreStatus status) { this.status = status; }

    public boolean isOverloaded() { return isOverloaded; }
    public void setOverloaded(boolean overloaded) { isOverloaded = overloaded; }

    public String getOverloadReason() { return overloadReason; }
    public void setOverloadReason(String overloadReason) { this.overloadReason = overloadReason; }

    public long getTodaysFarmersCount() { return todaysFarmersCount; }
    public void setTodaysFarmersCount(long todaysFarmersCount) { this.todaysFarmersCount = todaysFarmersCount; }

    public long getWaitingCount() { return waitingCount; }
    public void setWaitingCount(long waitingCount) { this.waitingCount = waitingCount; }

    public long getCalledCount() { return calledCount; }
    public void setCalledCount(long calledCount) { this.calledCount = calledCount; }

    public long getArrivedCount() { return arrivedCount; }
    public void setArrivedCount(long arrivedCount) { this.arrivedCount = arrivedCount; }

    public long getProcessingCount() { return processingCount; }
    public void setProcessingCount(long processingCount) { this.processingCount = processingCount; }

    public long getCompletedCount() { return completedCount; }
    public void setCompletedCount(long completedCount) { this.completedCount = completedCount; }

    public long getCancelledCount() { return cancelledCount; }
    public void setCancelledCount(long cancelledCount) { this.cancelledCount = cancelledCount; }

    public long getReschedulingCount() { return reschedulingCount; }
    public void setReschedulingCount(long reschedulingCount) { this.reschedulingCount = reschedulingCount; }

    public long getPendingApprovalsCount() { return pendingApprovalsCount; }
    public void setPendingApprovalsCount(long pendingApprovalsCount) { this.pendingApprovalsCount = pendingApprovalsCount; }

    public BookingResponse getCurrentProcessing() { return currentProcessing; }
    public void setCurrentProcessing(BookingResponse currentProcessing) { this.currentProcessing = currentProcessing; }

    public List<BookingResponse> getQueueList() { return queueList; }
    public void setQueueList(List<BookingResponse> queueList) { this.queueList = queueList; }

    public List<BookingResponse> getTodaysFarmers() { return todaysFarmers; }
    public void setTodaysFarmers(List<BookingResponse> todaysFarmers) { this.todaysFarmers = todaysFarmers; }

    public List<BookingResponse> getCancellationRequests() { return cancellationRequests; }
    public void setCancellationRequests(List<BookingResponse> cancellationRequests) { this.cancellationRequests = cancellationRequests; }

    public List<BookingResponse> getReschedulingRequests() { return reschedulingRequests; }
    public void setReschedulingRequests(List<BookingResponse> reschedulingRequests) { this.reschedulingRequests = reschedulingRequests; }

    public List<BookingResponse> getPendingApprovals() { return pendingApprovals; }
    public void setPendingApprovals(List<BookingResponse> pendingApprovals) { this.pendingApprovals = pendingApprovals; }
}
