package com.payroll.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import com.payroll.dao.LeaveRequestDAO;

@WebServlet("/updateLeaveStatus")
public class UpdateLeaveStatusServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // 🔐 Security Check: Only ADMIN can update leave status
        if (session == null || session.getAttribute("userId") == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        String leaveIdParam = request.getParameter("leaveId");
        String statusParam = request.getParameter("status"); // "Approved" or "Rejected"

        if (leaveIdParam == null || leaveIdParam.isEmpty() || statusParam == null || statusParam.isEmpty()) {
            request.setAttribute("errorMessage", "Invalid parameters for updating leave status.");
            request.getRequestDispatcher("viewLeaveRequests").forward(request, response);
            return;
        }

        try {
            int leaveId = Integer.parseInt(leaveIdParam);
            LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();

            if (leaveRequestDAO.updateLeaveRequestStatus(leaveId, statusParam)) {
                request.setAttribute("successMessage", "Leave request status updated successfully to " + statusParam + ".");
            } else {
                request.setAttribute("errorMessage", "Failed to update leave request status.");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid leave ID format.");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        response.sendRedirect("viewLeaveRequests"); // Redirect back to the list
    }
}