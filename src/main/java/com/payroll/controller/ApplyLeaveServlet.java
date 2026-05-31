package com.payroll.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.payroll.dao.EmployeeDAO;
import com.payroll.dao.LeaveRequestDAO;
import com.payroll.model.Employee;
import com.payroll.model.LeaveRequest;

@WebServlet("/applyLeave")
public class ApplyLeaveServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null || !"EMPLOYEE".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Fetch employee details to display on the form (optional, but good for context)
        int userId = (int) session.getAttribute("userId");
        EmployeeDAO employeeDAO = new EmployeeDAO();
        Employee employee = employeeDAO.getEmployeeByUserId(userId);

        request.setAttribute("employee", employee);
        request.getRequestDispatcher("applyLeave.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null || !"EMPLOYEE".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        EmployeeDAO employeeDAO = new EmployeeDAO();
        Employee employee = employeeDAO.getEmployeeByUserId(userId);

        if (employee == null) {
            request.setAttribute("errorMessage", "Employee not found.");
            request.getRequestDispatcher("applyLeave.jsp").forward(request, response);
            return;
        }

        int empId = employee.getEmpId();
        String leaveType = request.getParameter("leaveType");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String reason = request.getParameter("reason");

        // Basic validation
        if (leaveType == null || leaveType.isEmpty() ||
            startDateStr == null || startDateStr.isEmpty() ||
            endDateStr == null || endDateStr.isEmpty() ||
            reason == null || reason.isEmpty()) {
            request.setAttribute("errorMessage", "All fields are required.");
            request.setAttribute("employee", employee); // Keep employee data for form pre-fill
            request.getRequestDispatcher("applyLeave.jsp").forward(request, response);
            return;
        }

        LocalDate startDate;
        LocalDate endDate;
        try {
            startDate = LocalDate.parse(startDateStr);
            endDate = LocalDate.parse(endDateStr);
        } catch (DateTimeParseException e) {
            request.setAttribute("errorMessage", "Invalid date format. Please use YYYY-MM-DD.");
            request.setAttribute("employee", employee);
            request.getRequestDispatcher("applyLeave.jsp").forward(request, response);
            return;
        }

        if (startDate.isAfter(endDate)) {
            request.setAttribute("errorMessage", "Start date cannot be after end date.");
            request.setAttribute("employee", employee);
            request.getRequestDispatcher("applyLeave.jsp").forward(request, response);
            return;
        }
        
        if (startDate.isBefore(LocalDate.now())) {
            request.setAttribute("errorMessage", "Leave start date cannot be in the past.");
            request.setAttribute("employee", employee);
            request.getRequestDispatcher("applyLeave.jsp").forward(request, response);
            return;
        }


        LeaveRequest leaveRequest = new LeaveRequest(empId, leaveType, startDate, endDate, reason);
        LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();

        if (leaveRequestDAO.addLeaveRequest(leaveRequest)) {
            request.setAttribute("successMessage", "Leave request submitted successfully!");
            // Optionally redirect to a "My Leaves" page or dashboard
            response.sendRedirect("dashboard"); // Redirect to dashboard for now
        } else {
            request.setAttribute("errorMessage", "Failed to submit leave request. Please try again.");
            request.setAttribute("employee", employee);
            request.getRequestDispatcher("applyLeave.jsp").forward(request, response);
        }
    }
}