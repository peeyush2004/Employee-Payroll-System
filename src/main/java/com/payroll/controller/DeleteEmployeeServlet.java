package com.payroll.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.payroll.dao.EmployeeDAO;

/**
 * Servlet implementation class DeleteEmployeeServlet
 */
@WebServlet("/deleteEmployee")
public class DeleteEmployeeServlet extends HttpServlet {
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		int id = Integer.parseInt(request.getParameter("id"));
		
		EmployeeDAO dao = new EmployeeDAO();
		dao.deleteEmployee(id);
		
		response.sendRedirect("viewEmployees");
	}

}
