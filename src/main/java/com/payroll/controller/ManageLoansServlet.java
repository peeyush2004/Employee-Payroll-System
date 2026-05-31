package com.payroll.controller;

import java.io.IOException;

import com.payroll.service.LoanService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/manageLoans")
public class ManageLoansServlet extends HttpServlet {

    private final LoanService loanService = new LoanService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        request.setAttribute("pendingLoanRequests", loanService.getPendingLoansWithLeaveData());
        request.setAttribute("activeLoans", loanService.getActiveLoans());
        request.setAttribute("defaultInterestRate", com.payroll.service.EMICalculationService.DEFAULT_ANNUAL_INTEREST_RATE);

        String success = request.getParameter("success");
        String error = request.getParameter("error");
        if (success != null && !success.isBlank()) {
            request.setAttribute("successMessage", success);
        }
        if (error != null && !error.isBlank()) {
            request.setAttribute("errorMessage", error);
        }

        request.getRequestDispatcher("manageLoans.jsp").forward(request, response);
    }
}
