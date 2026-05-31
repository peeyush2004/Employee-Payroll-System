package com.payroll.model;

import java.util.ArrayList;
import java.util.List;
import java.time.YearMonth;

public class PayrollCalculation {

    private YearMonth payrollMonth;
    private int approvedLeaveDays;
    private double grossSalary;
    private double standardDeduction;
    private double leaveDeduction;
    private double loanDeduction;
    private double netSalary;
    private List<LoanPayrollDeduction> loanDeductions = new ArrayList<>();

    public YearMonth getPayrollMonth() {
        return payrollMonth;
    }

    public void setPayrollMonth(YearMonth payrollMonth) {
        this.payrollMonth = payrollMonth;
    }

    public int getApprovedLeaveDays() {
        return approvedLeaveDays;
    }

    public void setApprovedLeaveDays(int approvedLeaveDays) {
        this.approvedLeaveDays = approvedLeaveDays;
    }

    public double getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(double grossSalary) {
        this.grossSalary = grossSalary;
    }

    public double getStandardDeduction() {
        return standardDeduction;
    }

    public void setStandardDeduction(double standardDeduction) {
        this.standardDeduction = standardDeduction;
    }

    public double getLeaveDeduction() {
        return leaveDeduction;
    }

    public void setLeaveDeduction(double leaveDeduction) {
        this.leaveDeduction = leaveDeduction;
    }

    public double getLoanDeduction() {
        return loanDeduction;
    }

    public void setLoanDeduction(double loanDeduction) {
        this.loanDeduction = loanDeduction;
    }

    public double getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(double netSalary) {
        this.netSalary = netSalary;
    }

    public List<LoanPayrollDeduction> getLoanDeductions() {
        return loanDeductions;
    }

    public void setLoanDeductions(List<LoanPayrollDeduction> loanDeductions) {
        this.loanDeductions = loanDeductions;
    }
}
