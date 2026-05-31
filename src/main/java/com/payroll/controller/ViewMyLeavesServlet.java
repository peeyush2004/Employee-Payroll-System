package com.payroll.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

import com.payroll.dao.EmployeeDAO;
import com.payroll.dao.LeaveRequestDAO;
import com.payroll.model.Employee;
import com.payroll.model.LeaveRequest;

@WebServlet("/myLeaveHistory")
public class ViewMyLeavesServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // 🔐 Security Check: Only logged-in EMPLOYEE can view their own leave history
        if (session == null || session.getAttribute("userId") == null || !"EMPLOYEE".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        EmployeeDAO employeeDAO = new EmployeeDAO();
        Employee employee = employeeDAO.getEmployeeByUserId(userId);

        if (employee == null) {
            // Should not happen if user is logged in, but good to handle
            response.sendRedirect("dashboard");
            return;
        }

        LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();
        List<LeaveRequest> myLeaveRequests = leaveRequestDAO.getLeaveRequestsByEmpId(employee.getEmpId());

        request.setAttribute("myLeaveRequests", myLeaveRequests);
        request.getRequestDispatcher("myLeaveHistory.jsp").forward(request, response);
    }
}