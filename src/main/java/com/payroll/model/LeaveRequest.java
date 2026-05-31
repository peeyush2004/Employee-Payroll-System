package com.payroll.model;

import java.sql.Timestamp;
import java.time.LocalDate;

public class LeaveRequest {
    private int leaveId;
    private int empId;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String status;
    private Timestamp appliedDate; // Use Timestamp for applied_date

    // Constructors
    public LeaveRequest() {
    }

    public LeaveRequest(int leaveId, int empId, String leaveType, LocalDate startDate, LocalDate endDate,
                        String reason, String status, Timestamp appliedDate) {
        this.leaveId = leaveId;
        this.empId = empId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
        this.appliedDate = appliedDate;
    }

    // Constructor without leaveId and appliedDate (for new requests)
    public LeaveRequest(int empId, String leaveType, LocalDate startDate, LocalDate endDate, String reason) {
        this.empId = empId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = "Pending"; // Default status for new requests
    }

    // Getters and Setters
    public int getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(int leaveId) {
        this.leaveId = leaveId;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(Timestamp appliedDate) {
        this.appliedDate = appliedDate;
    }

    @Override
    public String toString() {
        return "LeaveRequest{" +
               "leaveId=" + leaveId +
               ", empId=" + empId +
               ", leaveType='" + leaveType + '\'' +
               ", startDate=" + startDate +
               ", endDate=" + endDate +
               ", reason='" + reason + '\'' +
               ", status='" + status + '\'' +
               ", appliedDate=" + appliedDate +
               '}';
    }
}