package com.payroll.model;

import java.time.YearMonth;

public class LoanPayrollDeduction {

    private int loanId;
    private String loanType;
    private double emi;
    private double remainingBalance;
    private YearMonth payrollMonth;
    private String status;

    public LoanPayrollDeduction() {
    }

    public LoanPayrollDeduction(int loanId, String loanType, double emi, double remainingBalance) {
        this.loanId = loanId;
        this.loanType = loanType;
        this.emi = emi;
        this.remainingBalance = remainingBalance;
    }

    public LoanPayrollDeduction(int loanId, String loanType, double emi, double remainingBalance,
                                YearMonth payrollMonth, String status) {
        this(loanId, loanType, emi, remainingBalance);
        this.payrollMonth = payrollMonth;
        this.status = status;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public double getEmi() {
        return emi;
    }

    public void setEmi(double emi) {
        this.emi = emi;
    }

    public double getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(double remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public YearMonth getPayrollMonth() {
        return payrollMonth;
    }

    public void setPayrollMonth(YearMonth payrollMonth) {
        this.payrollMonth = payrollMonth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
