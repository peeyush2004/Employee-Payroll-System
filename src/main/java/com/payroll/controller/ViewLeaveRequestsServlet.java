package com.payroll.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

import com.payroll.dao.LeaveRequestDAO;
import com.payroll.model.LeaveRequest;

@WebServlet("/viewLeaveRequests")
public class ViewLeaveRequestsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // 🔐 Security Check: Only ADMIN can view all leave requests
        if (session == null || session.getAttribute("userId") == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();
        List<LeaveRequest> leaveRequests = leaveRequestDAO.getAllLeaveRequests();

        request.setAttribute("leaveRequests", leaveRequests);
        request.getRequestDispatcher("viewLeaveRequests.jsp").forward(request, response);
    }
}