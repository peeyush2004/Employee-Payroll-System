package com.payroll.model;

import java.sql.Timestamp;
import java.time.YearMonth;

public class LoanRequest {

    private int loanId;
    private int empId;
    private String employeeName;
    private String departmentName;
    private double employeeGrossSalary;
    private int approvedLeaveDaysThisMonth;

    private String loanType;
    private double loanAmount;
    private String reason;
    private int durationMonths;
    private double interestRate;
    private double monthlyEmi;
    private double totalRepaymentAmount;
    private double outstandingBalance;
    private double paidAmount;
    private String status;
    private String displayStatus;
    private String pausedMonthsSummary;
    private String adminRemark;
    private YearMonth emiStartMonth;
    private YearMonth closedMonth;
    private Timestamp requestedAt;
    private Timestamp reviewedAt;
    private Integer reviewedBy;

    public LoanRequest() {
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public double getEmployeeGrossSalary() {
        return employeeGrossSalary;
    }

    public void setEmployeeGrossSalary(double employeeGrossSalary) {
        this.employeeGrossSalary = employeeGrossSalary;
    }

    public int getApprovedLeaveDaysThisMonth() {
        return approvedLeaveDaysThisMonth;
    }

    public void setApprovedLeaveDaysThisMonth(int approvedLeaveDaysThisMonth) {
        this.approvedLeaveDaysThisMonth = approvedLeaveDaysThisMonth;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public double getMonthlyEmi() {
        return monthlyEmi;
    }

    public void setMonthlyEmi(double monthlyEmi) {
        this.monthlyEmi = monthlyEmi;
    }

    public double getTotalRepaymentAmount() {
        return totalRepaymentAmount;
    }

    public void setTotalRepaymentAmount(double totalRepaymentAmount) {
        this.totalRepaymentAmount = totalRepaymentAmount;
    }

    public double getOutstandingBalance() {
        return outstandingBalance;
    }

    public void setOutstandingBalance(double outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDisplayStatus() {
        return displayStatus != null && !displayStatus.isBlank() ? displayStatus : status;
    }

    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }

    public String getPausedMonthsSummary() {
        return pausedMonthsSummary;
    }

    public void setPausedMonthsSummary(String pausedMonthsSummary) {
        this.pausedMonthsSummary = pausedMonthsSummary;
    }

    public String getAdminRemark() {
        return adminRemark;
    }

    public void setAdminRemark(String adminRemark) {
        this.adminRemark = adminRemark;
    }

    public YearMonth getEmiStartMonth() {
        return emiStartMonth;
    }

    public void setEmiStartMonth(YearMonth emiStartMonth) {
        this.emiStartMonth = emiStartMonth;
    }

    public YearMonth getClosedMonth() {
        return closedMonth;
    }

    public void setClosedMonth(YearMonth closedMonth) {
        this.closedMonth = closedMonth;
    }

    public Timestamp getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Timestamp requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Timestamp getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Timestamp reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public Integer getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Integer reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
}
