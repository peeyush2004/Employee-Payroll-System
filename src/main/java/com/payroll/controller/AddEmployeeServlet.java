package com.payroll.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.File; // Import java.io.File

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.MultipartConfig;
import com.payroll.util.DBConnection;
import com.payroll.util.PasswordUtil;

@MultipartConfig(
	    fileSizeThreshold = 1024 * 1024,  // 1MB
	    maxFileSize = 1024 * 1024 * 5,    // 5MB
	    maxRequestSize = 1024 * 1024 * 10 // 10MB
	)
@WebServlet("/addEmployee")
public class AddEmployeeServlet extends HttpServlet {
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

        request.getRequestDispatcher("addEmployee.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String address = request.getParameter("address");
        String email = request.getParameter("email");
        int deptId = Integer.parseInt(request.getParameter("deptId"));
        String hireDate = request.getParameter("hireDate");

        // Safely parse double values, treating empty strings as 0.0
        double basic = parseDoubleOrDefault(request.getParameter("basicSalary"), 0.0);
        double hra = parseDoubleOrDefault(request.getParameter("hra"), 0.0);
        double da = parseDoubleOrDefault(request.getParameter("da"), 0.0);
        double allowances = parseDoubleOrDefault(request.getParameter("allowances"), 0.0);
        double deductions = parseDoubleOrDefault(request.getParameter("deductions"), 0.0);
        
        // --- PHOTO UPLOAD HANDLING ---
        String newFileName = "default.png"; // Default photo if no file is uploaded or upload fails

        try {
            Part photoPart = request.getPart("photo"); // Gets the file part from the request
            String fileName = photoPart.getSubmittedFileName(); // Original filename from client

            System.out.println("DEBUG Add: Original submitted filename: " + fileName); // Debugging line

            if (fileName != null && !fileName.isEmpty()) {
                String fileExtension = "";
                int dotIndex = fileName.lastIndexOf(".");
                if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
                    fileExtension = fileName.substring(dotIndex); // e.g., ".jpg", ".png"
                }
                
                // Generate a unique filename to prevent conflicts
                newFileName = username + "_" + System.currentTimeMillis() + fileExtension;

                String uploadPath = getUploadDirectoryPath(request);
                
                System.out.println("DEBUG Add: Resolved Upload Path: " + uploadPath);
                
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    boolean created = uploadDir.mkdirs();
                    System.out.println("DEBUG Add: 'uploads' directory created: " + created);
                }

                // Write the file to the server's filesystem
                String filePath = uploadPath + File.separator + newFileName;
                photoPart.write(filePath);
                System.out.println("DEBUG Add: File '" + newFileName + "' saved successfully to: " + filePath);
            }
        } catch (IOException e) {
            System.err.println("ERROR Add: Failed to save uploaded file: " + e.getMessage());
            e.printStackTrace();
            newFileName = "default.png"; // Fallback to default if saving fails
        } catch (ServletException e) {
            System.out.println("DEBUG Add: No photo submitted or multipart error: " + e.getMessage());
            // newFileName remains "default.png"
        }
        // --- END PHOTO UPLOAD HANDLING ---

        Connection con = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            // 1️⃣ Insert into users
            PreparedStatement userPs = con.prepareStatement(
                "INSERT INTO users (username, password, role) VALUES (?, ?, 'EMPLOYEE')",
                PreparedStatement.RETURN_GENERATED_KEYS);

            userPs.setString(1, username);
            userPs.setString(2, PasswordUtil.hashPassword(password));
            userPs.executeUpdate();

            ResultSet userRs = userPs.getGeneratedKeys();
            userRs.next();
            int userId = userRs.getInt(1);

            // 2️⃣ Insert into employees
            PreparedStatement empPs = con.prepareStatement(
                "INSERT INTO employees (user_id, dept_id, first_name, last_name, email, address, hire_date, photo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS);

            empPs.setInt(1, userId);
            empPs.setInt(2, deptId);
            empPs.setString(3, firstName);
            empPs.setString(4, lastName);
            empPs.setString(5, email);
            empPs.setString(6, address);
            empPs.setString(7, hireDate);
            empPs.setString(8, newFileName);
            empPs.executeUpdate();

            ResultSet empRs = empPs.getGeneratedKeys();
            empRs.next();
            int empId = empRs.getInt(1);

            // 3️⃣ Insert into salary_structure
            PreparedStatement salPs = con.prepareStatement(
                "INSERT INTO salary_structure (emp_id, basic_salary, hra, da, allowances, deductions) " +
                "VALUES (?, ?, ?, ?, ?, ?)");

            salPs.setInt(1, empId);
            salPs.setDouble(2, basic);
            salPs.setDouble(3, hra);
            salPs.setDouble(4, da);
            salPs.setDouble(5, allowances);
            salPs.setDouble(6, deductions);
            salPs.executeUpdate();

            con.commit();
            System.out.println("DEBUG Add: Employee and salary data committed to DB.");

        } catch (Exception e) {
            try {
                if (con != null) con.rollback();
            } catch (Exception ignored) {}
            System.err.println("ERROR Add: Database operation failed: " + e.getMessage());
            e.printStackTrace();
            // Set an error message for the user
            request.setAttribute("errorMessage", "Failed to add employee: " + e.getMessage()); // Show specific error
            request.getRequestDispatcher("addEmployee.jsp").forward(request, response);
            return; // Important to return after forwarding
        } finally {
            try {
                if (con != null) con.close();
            } catch (Exception ignored) {}
        }

        response.sendRedirect("dashboard");
    }

    private String getUploadDirectoryPath(HttpServletRequest request) {
        return EXTERNAL_UPLOAD_BASE_DIR + File.separator + APP_UPLOAD_SUBDIR;
    }

    // Helper method to safely parse double values
    private double parseDoubleOrDefault(String param, double defaultValue) {
        if (param != null && !param.trim().isEmpty()) {
            try {
                return Double.parseDouble(param);
            } catch (NumberFormatException e) {
                System.err.println("WARNING: Invalid number format for parameter: '" + param + "'. Using default value " + defaultValue);
                return defaultValue;
            }
        }
        return defaultValue;
    }
}
