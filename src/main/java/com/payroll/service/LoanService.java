package com.payroll.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.payroll.dao.LeaveRequestDAO;
import com.payroll.dao.LoanDAO;
import com.payroll.model.LoanPayrollDeduction;
import com.payroll.model.LoanRequest;
import com.payroll.model.LoanSchedulePreview;
import com.payroll.model.LoanTransaction;

public class LoanService {

    private static final List<String> SUPPORTED_LOAN_TYPES = Arrays.asList(
            "Personal Loan",
            "Emergency Loan",
            "Medical Loan",
            "Festival Advance",
            "Housing Loan"
    );

    private final LoanDAO loanDAO = new LoanDAO();
    private final LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();
    private final EMICalculationService emiCalculationService = new EMICalculationService();

    public List<String> getSupportedLoanTypes() {
        return SUPPORTED_LOAN_TYPES;
    }

    public LoanSchedulePreview previewLoan(double amount, int durationMonths) {
        return emiCalculationService.calculatePreview(amount, durationMonths);
    }

    public String validateRequest(String loanType, double amount, String reason, int durationMonths) {
        if (!SUPPORTED_LOAN_TYPES.contains(loanType)) {
            return "Please select a valid loan type.";
        }
        if (amount <= 0) {
            return "Loan amount must be greater than zero.";
        }
        if (reason == null || reason.isBlank()) {
            return "Reason is required.";
        }
        if (durationMonths <= 0 || durationMonths > 360) {
            return "Repayment duration must be between 1 and 360 months.";
        }
        return null;
    }

    public boolean submitLoanRequest(int empId, int createdByUserId, String loanType, double amount, String reason, int durationMonths) {
        LoanSchedulePreview preview = previewLoan(amount, durationMonths);

        LoanRequest request = new LoanRequest();
        request.setEmpId(empId);
        request.setLoanType(loanType);
        request.setLoanAmount(amount);
        request.setReason(reason);
        request.setDurationMonths(durationMonths);
        request.setInterestRate(EMICalculationService.DEFAULT_ANNUAL_INTEREST_RATE);
        request.setMonthlyEmi(preview.getMonthlyEmi());
        request.setTotalRepaymentAmount(preview.getTotalRepaymentAmount());
        request.setOutstandingBalance(preview.getTotalRepaymentAmount());
        request.setPaidAmount(0);
        request.setStatus("PENDING");

        return loanDAO.createLoanRequest(request, createdByUserId);
    }

    public List<LoanRequest> getEmployeeLoans(int empId) {
        return loanDAO.getLoanRequestsByEmployee(empId);
    }

    public List<LoanRequest> getEmployeeLoansForDisplay(int empId) {
        List<LoanRequest> loans = loanDAO.getLoanRequestsByEmployee(empId);
        LocalDate today = LocalDate.now();
        for (LoanRequest loan : loans) {
            enrichLoanForDisplay(loan, today);
        }
        return loans;
    }

    public List<LoanTransaction> getEmployeeTransactions(int empId) {
        return loanDAO.getTransactionsByEmployee(empId);
    }

    public List<LoanRequest> getPendingLoansWithLeaveData() {
        List<LoanRequest> loans = loanDAO.getPendingLoanRequests();
        YearMonth currentMonth = YearMonth.now();
        for (LoanRequest loan : loans) {
            loan.setApprovedLeaveDaysThisMonth(
                    leaveRequestDAO.getApprovedLeaveDaysInMonth(loan.getEmpId(), currentMonth));
            enrichLoanForDisplay(loan, LocalDate.now());
        }
        return loans;
    }

    public List<LoanRequest> getActiveLoans() {
        List<LoanRequest> loans = loanDAO.getActiveLoans();
        LocalDate today = LocalDate.now();
        for (LoanRequest loan : loans) {
            enrichLoanForDisplay(loan, today);
        }
        return loans;
    }

    public boolean approveLoan(int loanId, String emiStartMonth, String remark, Integer adminUserId) {
        YearMonth startMonth = YearMonth.parse(emiStartMonth);
        return loanDAO.approveLoan(
                loanId,
                EMICalculationService.DEFAULT_ANNUAL_INTEREST_RATE,
                startMonth,
                remark,
                adminUserId != null ? adminUserId : 0
        );
    }

    public boolean rejectLoan(int loanId, String remark, Integer adminUserId) {
        return loanDAO.rejectLoan(loanId, remark, adminUserId != null ? adminUserId : 0);
    }

    public boolean pauseLoan(int loanId, String payrollMonth, String remark, Integer adminUserId) {
        YearMonth pauseMonth = YearMonth.parse(payrollMonth);
        if (!pauseMonth.isAfter(YearMonth.now())) {
            return false;
        }
        return loanDAO.pauseLoanForMonth(loanId, pauseMonth, remark, adminUserId != null ? adminUserId : 0);
    }

    public boolean closeLoan(int loanId, String remark, Integer adminUserId) {
        return loanDAO.closeLoan(loanId, remark, adminUserId != null ? adminUserId : 0);
    }

    public List<LoanPayrollDeduction> processPayrollDeductions(int empId, YearMonth payrollMonth) {
        return loanDAO.processPayrollDeductions(empId, payrollMonth, true);
    }

    public List<LoanPayrollDeduction> previewPayrollDeductions(int empId, YearMonth payrollMonth) {
        return loanDAO.processPayrollDeductions(empId, payrollMonth, false);
    }

    public String getLoanStatus(int loanId, LocalDate currentDate) {
        LoanRequest loan = loanDAO.getLoanById(loanId);
        if (loan == null) {
            return "UNKNOWN";
        }
        return deriveLoanStatus(loan, currentDate);
    }

    public List<YearMonth> getPausedMonths(int loanId) {
        return loanDAO.getPausedMonths(loanId);
    }

    public int getPendingLoanCount() {
        return loanDAO.getPendingLoanCount();
    }

    public int getActiveLoanCount() {
        return loanDAO.getActiveLoanCount();
    }

    public int getEmployeeActiveLoanCount(int empId) {
        return loanDAO.getEmployeeActiveLoanCount(empId);
    }

    private void enrichLoanForDisplay(LoanRequest loan, LocalDate currentDate) {
        loan.setDisplayStatus(deriveLoanStatus(loan, currentDate));
        loan.setPausedMonthsSummary(formatPausedMonths(loan.getLoanId(), currentDate));
    }

    private String deriveLoanStatus(LoanRequest loan, LocalDate currentDate) {
        YearMonth currentMonth = YearMonth.from(currentDate);
        if ("REJECTED".equalsIgnoreCase(loan.getStatus())) {
            return "REJECTED";
        }
        if ("COMPLETED".equalsIgnoreCase(loan.getStatus())
                || loan.getClosedMonth() != null
                || loan.getOutstandingBalance() <= 0) {
            return "COMPLETED";
        }
        if (loan.getEmiStartMonth() == null || currentMonth.isBefore(loan.getEmiStartMonth())) {
            return "PENDING";
        }
        if (getPausedMonths(loan.getLoanId()).contains(currentMonth)) {
            return "PAUSED";
        }
        return "ACTIVE";
    }

    private String formatPausedMonths(int loanId, LocalDate currentDate) {
        List<YearMonth> upcomingPausedMonths = getPausedMonths(loanId).stream()
                .filter(month -> !month.isBefore(YearMonth.from(currentDate)))
                .toList();
        if (upcomingPausedMonths.isEmpty()) {
            return "No upcoming paused months";
        }
        return upcomingPausedMonths.stream()
                .map(month -> month.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + month.getYear())
                .collect(Collectors.joining(", "));
    }
}
