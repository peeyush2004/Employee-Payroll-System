package com.payroll.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import com.payroll.dao.EmployeeDAO;
import com.payroll.model.Employee;

@WebServlet("/editEmployee")
public class EditEmployeeServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));

        EmployeeDAO dao = new EmployeeDAO();
        Employee emp = dao.getEmployeeById(id);

        request.setAttribute("employee", emp);
        request.getRequestDispatcher("editEmployee.jsp")
               .forward(request, response);
    }
}