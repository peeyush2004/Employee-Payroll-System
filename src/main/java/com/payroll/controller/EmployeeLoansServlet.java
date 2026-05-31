package com.payroll.controller;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.payroll.dao.EmployeeDAO;
import com.payroll.model.Employee;
import com.payroll.model.LoanRequest;
import com.payroll.model.LoanSchedulePreview;
import com.payroll.service.EMICalculationService;
import com.payroll.service.LoanService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/employeeLoans")
public class EmployeeLoansServlet extends HttpServlet {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final LoanService loanService = new LoanService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"EMPLOYEE".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        Employee employee = employeeDAO.getEmployeeByUserId(userId);
        if (employee == null) {
            response.sendRedirect("dashboard");
            return;
        }

        bindLoanPageData(request, employee);
        request.getRequestDispatcher("employeeLoans.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"EMPLOYEE".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        Employee employee = employeeDAO.getEmployeeByUserId(userId);
        if (employee == null) {
            response.sendRedirect("dashboard");
            return;
        }

        String loanType = request.getParameter("loanType");
        String amountParam = request.getParameter("loanAmount");
        String reason = request.getParameter("reason");
        String durationParam = request.getParameter("durationMonths");

        double amount = 0;
        int durationMonths = 0;
        try {
            amount = Double.parseDouble(amountParam);
            durationMonths = Integer.parseInt(durationParam);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Loan amount and duration must be valid numbers.");
            bindLoanPageData(request, employee);
            request.getRequestDispatcher("employeeLoans.jsp").forward(request, response);
            return;
        }

        String validationError = loanService.validateRequest(loanType, amount, reason, durationMonths);
        if (validationError != null) {
            request.setAttribute("errorMessage", validationError);
            bindLoanPageData(request, employee);
            request.getRequestDispatcher("employeeLoans.jsp").forward(request, response);
            return;
        }

        if (loanService.submitLoanRequest(employee.getEmpId(), userId, loanType, amount, reason, durationMonths)) {
            response.sendRedirect("employeeLoans?success=Loan request submitted successfully.");
            return;
        }

        request.setAttribute("errorMessage", "Unable to submit the loan request right now.");
        bindLoanPageData(request, employee);
        request.getRequestDispatcher("employeeLoans.jsp").forward(request, response);
    }

    private void bindLoanPageData(HttpServletRequest request, Employee employee) {
        request.setAttribute("employee", employee);
        request.setAttribute("loanTypes", loanService.getSupportedLoanTypes());
        request.setAttribute("loanRequests", loanService.getEmployeeLoansForDisplay(employee.getEmpId()));
        request.setAttribute("interestRate", EMICalculationService.DEFAULT_ANNUAL_INTEREST_RATE);

        String success = request.getParameter("success");
        if (success != null && !success.isBlank()) {
            request.setAttribute("successMessage", success);
        }

        try {
            String previewAmount = request.getParameter("previewAmount");
            String previewDuration = request.getParameter("previewDuration");
            if (previewAmount != null && previewDuration != null) {
                double amount = Double.parseDouble(previewAmount);
                int duration = Integer.parseInt(previewDuration);
                LoanSchedulePreview preview = loanService.previewLoan(amount, duration);
                request.setAttribute("loanPreview", preview);
            }
        } catch (NumberFormatException ignored) {
        }

        String previewStartMonth = request.getParameter("previewStartMonth");
        if (previewStartMonth != null) {
            try {
                request.setAttribute("selectedPreviewMonth", YearMonth.parse(previewStartMonth));
            } catch (DateTimeParseException ignored) {
            }
        }
    }
}
