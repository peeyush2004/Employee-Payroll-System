package com.payroll.model;

public class LoanSchedulePreview {

    private double monthlyEmi;
    private double totalRepaymentAmount;

    public LoanSchedulePreview() {
    }

    public LoanSchedulePreview(double monthlyEmi, double totalRepaymentAmount) {
        this.monthlyEmi = monthlyEmi;
        this.totalRepaymentAmount = totalRepaymentAmount;
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
}
