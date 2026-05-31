package com.payroll.controller;

import java.io.File;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Map;
import java.util.List; // Ensure this is imported

import com.payroll.dao.EmployeeDAO;
import com.payroll.dao.LeaveRequestDAO; // Ensure this is imported
import com.payroll.model.Employee;
import com.payroll.model.LeaveRequest; // Ensure this is imported
import com.payroll.service.LoanService;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String role = (String) session.getAttribute("role");
        Integer userId = (Integer) session.getAttribute("userId");

        if (role == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        EmployeeDAO dao = new EmployeeDAO();
        LoanService loanService = new LoanService();

        try { // <--- ADD THIS TRY BLOCK
            // ================= ADMIN =================
            if ("ADMIN".equals(role)) {

                int totalEmployees = dao.getEmployeeCount();
                double totalSalary = dao.getTotalSalary();
                double avgSalary = dao.getAverageSalary();
                Map<String, Integer> employeesByDepartment = dao.getEmployeesCountByDepartment();

                // Fetch pending leave requests count
                LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();
                int pendingLeaveCount = leaveRequestDAO.getPendingLeaveRequestCount();
                int approvedLeavesThisMonth = leaveRequestDAO.getLeaveRequestCountByStatusForCurrentMonth("Approved");
                int rejectedLeavesThisMonth = leaveRequestDAO.getLeaveRequestCountByStatusForCurrentMonth("Rejected");

                request.setAttribute("totalEmployees", totalEmployees);
                request.setAttribute("totalSalary", totalSalary);
                request.setAttribute("avgSalary", avgSalary);
                request.setAttribute("employeesByDepartment", employeesByDepartment);
                request.setAttribute("pendingLeaveCount", pendingLeaveCount);
                request.setAttribute("approvedLeavesThisMonth", approvedLeavesThisMonth);
                request.setAttribute("rejectedLeavesThisMonth", rejectedLeavesThisMonth);
                request.setAttribute("pendingLoanCount", loanService.getPendingLoanCount());
                request.setAttribute("activeLoanCount", loanService.getActiveLoanCount());
                request.setAttribute("pendingLoanRequests", loanService.getPendingLoansWithLeaveData());
                request.setAttribute("activeLoans", loanService.getActiveLoans());
            }
            // ================= EMPLOYEE =================
            else if ("EMPLOYEE".equals(role)) {

                if (userId != null) {
                    Employee emp = dao.getEmployeeByUserId(userId);
                    request.setAttribute("employee", emp);
                    request.setAttribute("employeePhotoDataUri", buildPhotoDataUri(request, emp));

                    // Fetch employee's leave requests for notifications
                    LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();
                    List<LeaveRequest> employeeLeaveRequests = leaveRequestDAO.getLeaveRequestsByEmpId(emp.getEmpId());
                    request.setAttribute("employeeLeaveRequests", employeeLeaveRequests);
                    request.setAttribute("loanTypes", loanService.getSupportedLoanTypes());
                    request.setAttribute("loanRequests", loanService.getEmployeeLoansForDisplay(emp.getEmpId()));
                    request.setAttribute("employeeActiveLoanCount", loanService.getEmployeeActiveLoanCount(emp.getEmpId()));
                    request.setAttribute("loanInterestRate", com.payroll.service.EMICalculationService.DEFAULT_ANNUAL_INTEREST_RATE);
                }
            }
        } catch (Exception e) { // <--- CATCH ANY EXCEPTION
            System.err.println("ERROR in DashboardServlet: " + e.getMessage());
            e.printStackTrace();
            // Set an error message and forward to a generic error page or back to login
            request.setAttribute("errorMessage", "An internal error occurred while loading the dashboard.");
            request.getRequestDispatcher("login.jsp").forward(request, response); // Or an error.jsp
            return; // Important to return after forwarding
        }


        request.getRequestDispatcher("dashboard.jsp")
               .forward(request, response);
    }

    private String buildPhotoDataUri(HttpServletRequest request, Employee emp) {
        if (emp == null || emp.getPhoto() == null || emp.getPhoto().isBlank()) {
            return null;
        }

        File photoFile = resolvePhotoFile(request, emp.getPhoto());
        if (photoFile == null || !photoFile.exists() || !photoFile.isFile()) {
            return null;
        }

        try {
            String mimeType = Files.probeContentType(photoFile.toPath());
            if (mimeType == null) {
                mimeType = "image/jpeg";
            }
            byte[] photoBytes = Files.readAllBytes(photoFile.toPath());
            return "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(photoBytes);
        } catch (IOException e) {
            return null;
        }
    }

    private File resolvePhotoFile(HttpServletRequest request, String fileName) {
        File externalFile = new File("D:\\payroll_uploads\\EmployeePayrollSystem\\uploads", fileName);
        if (externalFile.exists()) {
            return externalFile;
        }

        String webAppUploads = request.getServletContext().getRealPath("/uploads");
        if (webAppUploads != null) {
            File webAppFile = new File(webAppUploads, fileName);
            if (webAppFile.exists()) {
                return webAppFile;
            }
        }

        return null;
    }
}
