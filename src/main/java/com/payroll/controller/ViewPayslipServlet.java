package com.payroll.controller;

import java.io.IOException;
import java.time.YearMonth;

import com.payroll.dao.EmployeeDAO;
import com.payroll.model.Employee;
import com.payroll.model.PayrollCalculation;
import com.payroll.service.PayrollService;
import com.payroll.util.Branding;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/viewPayslip")
public class ViewPayslipServlet extends HttpServlet {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final PayrollService payrollService = new PayrollService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Employee employee = resolveEmployee(request, session);
        if (employee == null) {
            response.sendRedirect("dashboard");
            return;
        }

        String monthParam = request.getParameter("month");
        YearMonth payrollMonth = (monthParam == null || monthParam.isBlank())
                ? YearMonth.now()
                : YearMonth.parse(monthParam);

        PayrollCalculation payroll = payrollService.calculatePayroll(employee, payrollMonth, false);
        request.setAttribute("employee", employee);
        request.setAttribute("payroll", payroll);
        request.setAttribute("selectedMonth", payrollMonth);
        request.setAttribute("companyName", Branding.COMPANY_NAME);
        request.getRequestDispatcher("payslip.jsp").forward(request, response);
    }

    private Employee resolveEmployee(HttpServletRequest request, HttpSession session) {
        String role = (String) session.getAttribute("role");
        if ("ADMIN".equals(role)) {
            String empParam = request.getParameter("empId");
            if (empParam == null || empParam.isBlank()) {
                return null;
            }
            return employeeDAO.getEmployeeById(Integer.parseInt(empParam));
        }

        if ("EMPLOYEE".equals(role)) {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                return null;
            }
            return employeeDAO.getEmployeeByUserId(userId);
        }

        return null;
    }
}
