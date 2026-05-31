package com.payroll.controller;


import jakarta.servlet.ServletException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import com.payroll.dao.EmployeeDAO;
import com.payroll.model.Employee;

/**
 * Servlet implementation class ViewEmployeeServlet
 */
@WebServlet("/viewEmployees")
public class ViewEmployeeServlet extends HttpServlet {
	

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		EmployeeDAO dao = new EmployeeDAO();
		
		List<Employee> list = dao.getAllEmployees();
		
		String keyword = request.getParameter("keyword");
		
		if(keyword != null && !keyword.trim().isEmpty()) {
			list = dao.searchEmployees(keyword);
		}else {
			list = dao.getAllEmployees(); 	
		}
		
		request.setAttribute("employeeList", list);
		
		request.getRequestDispatcher("viewEmployees.jsp")
		.forward(request, response);
	}

}
