package com.payroll.controller;

import java.io.File;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.payroll.dao.EmployeeDAO;
import com.payroll.model.Employee;

@WebServlet("/updateEmployee")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,  // 1MB
        maxFileSize = 1024 * 1024 * 5,    // 5MB
        maxRequestSize = 1024 * 1024 * 10 // 10MB
)
public class UpdateEmployeeServlet extends HttpServlet {
    private static final String EXTERNAL_UPLOAD_BASE_DIR = "D:\\payroll_uploads";
    private static final String APP_UPLOAD_SUBDIR = "EmployeePayrollSystem" + File.separator + "uploads";

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        int id = Integer.parseInt(request.getParameter("id"));

        EmployeeDAO dao = new EmployeeDAO();
        Employee emp = dao.getEmployeeById(id);

        if (emp == null) {
            response.sendRedirect("viewEmployees"); // Employee not found
            return;
        }

        request.setAttribute("employee", emp);
        request.getRequestDispatcher("editEmployee.jsp")
               .forward(request, response);
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        int id = Integer.parseInt(request.getParameter("id"));

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String address = request.getParameter("address");

        double basicSalary = parseDoubleOrDefault(request.getParameter("basicSalary"), 0.0);
        double hra = parseDoubleOrDefault(request.getParameter("hra"), 0.0);
        double da = parseDoubleOrDefault(request.getParameter("da"), 0.0);
        double allowances = parseDoubleOrDefault(request.getParameter("allowances"), 0.0);
        double deductions = parseDoubleOrDefault(request.getParameter("deductions"), 0.0);

        EmployeeDAO dao = new EmployeeDAO();
        Employee existingEmp = dao.getEmployeeById(id); // Get existing employee to retain unchanged data

        if (existingEmp == null) {
            response.sendRedirect("viewEmployees"); // Employee not found
            return;
        }

        String photoName = existingEmp.getPhoto(); // Default: keep existing photo name

        // --- PHOTO UPLOAD HANDLING ---
        try {
            Part photoPart = request.getPart("photo");
            String submittedFileName = photoPart.getSubmittedFileName();

            System.out.println("DEBUG Update: Original submitted filename: " + submittedFileName);

            if (submittedFileName != null && !submittedFileName.isEmpty()) {
                String fileExtension = "";
                int dotIndex = submittedFileName.lastIndexOf(".");
                if (dotIndex > 0 && dotIndex < submittedFileName.length() - 1) {
                    fileExtension = submittedFileName.substring(dotIndex);
                }

                String newFileName = firstName + "_" + System.currentTimeMillis() + fileExtension;
                String uploadPath = getUploadDirectoryPath(request);

                System.out.println("DEBUG Update: Resolved Upload Path: " + uploadPath);

                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    boolean created = uploadDir.mkdirs(); // Use mkdirs() for robustness
                    System.out.println("DEBUG Update: 'uploads' directory created: " + created);
                }

                String filePath = uploadPath + File.separator + newFileName;
                photoPart.write(filePath);
                System.out.println("DEBUG Update: File '" + newFileName + "' saved successfully to: " + filePath);
                photoName = newFileName; // Update photo name only if a new file was successfully uploaded
            }
        } catch (IOException e) {
            System.err.println("ERROR Update: Failed to save uploaded file: " + e.getMessage());
            e.printStackTrace();
            // Optionally, set an error message for the user
        } catch (ServletException e) {
            // This can happen if no file was selected but getPart("photo") is called
            // Or if the request is not multipart/form-data (should be handled by enctype)
            System.out.println("DEBUG Update: No new photo submitted or multipart error: " + e.getMessage());
            // Keep existing photoName
        }
        // --- END PHOTO UPLOAD HANDLING ---

        // Create an Employee object with updated details
        // ✅ CORRECTED CONSTRUCTOR CALL TO MATCH Employee.java
        Employee updatedEmp = new Employee(
                id,
                existingEmp.getUsername(), // Retain existing username
                firstName,
                lastName,
                email,
                existingEmp.getDepartmentName(), // Use existing departmentName (String)
                address,
                basicSalary,
                hra,
                da,
                allowances,
                deductions,
                photoName // Use the new photoName (or old if no new upload)
        );

        // Update employee in the database
        boolean success = dao.updateEmployee(updatedEmp);

        if (success) {
            response.sendRedirect("viewEmployees?message=Employee updated successfully!");
        } else {
            request.setAttribute("errorMessage", "Failed to update employee. Please try again.");
            request.setAttribute("employee", existingEmp); // Pass existing employee back to form
            request.getRequestDispatcher("editEmployee.jsp").forward(request, response);
        }
    }

    private String getUploadDirectoryPath(HttpServletRequest request) {
        return EXTERNAL_UPLOAD_BASE_DIR + File.separator + APP_UPLOAD_SUBDIR;
    }

    private double parseDoubleOrDefault(String param, double defaultValue) {
        if (param != null && !param.trim().isEmpty()) {
            try {
                return Double.parseDouble(param);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}
