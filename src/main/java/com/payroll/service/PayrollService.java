package com.payroll.service;

import java.time.YearMonth;
import java.util.List;

import com.payroll.dao.LeaveRequestDAO;
import com.payroll.model.Employee;
import com.payroll.model.LoanPayrollDeduction;
import com.payroll.model.PayrollCalculation;

public class PayrollService {

    private final LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();
    private final LoanService loanService = new LoanService();

    public PayrollCalculation calculatePayroll(Employee emp, YearMonth payrollMonth, boolean persistLoanDeductions) {
        PayrollCalculation calculation = new PayrollCalculation();
        calculation.setPayrollMonth(payrollMonth);

        int approvedLeaveDays = leaveRequestDAO.getApprovedLeaveDaysInMonth(emp.getEmpId(), payrollMonth);
        double grossSalary = emp.getBasicSalary() + emp.getHra() + emp.getDa() + emp.getAllowances();
        double dailySalary = grossSalary / payrollMonth.lengthOfMonth();
        double leaveDeduction = approvedLeaveDays * dailySalary;

        List<LoanPayrollDeduction> loanDeductions = persistLoanDeductions
                ? loanService.processPayrollDeductions(emp.getEmpId(), payrollMonth)
                : loanService.previewPayrollDeductions(emp.getEmpId(), payrollMonth);

        double totalLoanDeduction = loanDeductions.stream()
                .mapToDouble(LoanPayrollDeduction::getEmi)
                .sum();

        calculation.setApprovedLeaveDays(approvedLeaveDays);
        calculation.setGrossSalary(grossSalary);
        calculation.setStandardDeduction(emp.getDeductions());
        calculation.setLeaveDeduction(round(leaveDeduction));
        calculation.setLoanDeductions(loanDeductions);
        calculation.setLoanDeduction(round(totalLoanDeduction));
        calculation.setNetSalary(round(grossSalary - emp.getDeductions() - leaveDeduction - totalLoanDeduction));
        return calculation;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
