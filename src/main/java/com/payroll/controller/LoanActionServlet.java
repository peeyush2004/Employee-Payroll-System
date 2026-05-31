package com.payroll.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.payroll.service.LoanService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/loanAction")
public class LoanActionServlet extends HttpServlet {

    private final LoanService loanService = new LoanService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        Integer adminUserId = (Integer) session.getAttribute("userId");
        String action = request.getParameter("action");
        int loanId = Integer.parseInt(request.getParameter("loanId"));

        boolean success = false;
        String redirectMessage;

        switch (action) {
            case "approve":
                success = loanService.approveLoan(
                        loanId,
                        request.getParameter("emiStartMonth"),
                        request.getParameter("adminRemark"),
                        adminUserId
                );
                redirectMessage = success ? "Loan approved successfully." : "Unable to approve the loan.";
                break;
            case "reject":
                success = loanService.rejectLoan(loanId, request.getParameter("adminRemark"), adminUserId);
                redirectMessage = success ? "Loan rejected successfully." : "Unable to reject the loan.";
                break;
            case "pause":
                success = loanService.pauseLoan(
                        loanId,
                        request.getParameter("payrollMonth"),
                        request.getParameter("adminRemark"),
                        adminUserId
                );
                redirectMessage = success ? "Loan EMI paused for the selected month." : "Unable to pause the EMI.";
                break;
            case "close":
                success = loanService.closeLoan(loanId, request.getParameter("adminRemark"), adminUserId);
                redirectMessage = success ? "Loan closed successfully." : "Unable to close the loan.";
                break;
            default:
                response.sendRedirect("manageLoans?error=Unsupported loan action.");
                return;
        }

        if (success) {
            response.sendRedirect("manageLoans?success=" + encode(redirectMessage));
        } else {
            response.sendRedirect("manageLoans?error=" + encode(redirectMessage));
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
