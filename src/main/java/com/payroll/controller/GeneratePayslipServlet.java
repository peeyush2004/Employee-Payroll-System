package com.payroll.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import com.payroll.dao.EmployeeDAO;
import com.payroll.model.Employee;
import com.payroll.model.LoanPayrollDeduction;
import com.payroll.model.PayrollCalculation;
import com.payroll.service.PayrollService;
import com.payroll.util.Branding;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

@WebServlet("/generatePayslip")
public class GeneratePayslipServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String role = (String) session.getAttribute("role");
        EmployeeDAO dao = new EmployeeDAO();
        Employee emp = null;

        if ("ADMIN".equals(role)) {
            String empParam = request.getParameter("empId");
            if (empParam == null || empParam.isEmpty()) {
                response.sendRedirect("viewEmployees");
                return;
            }
            int empId = Integer.parseInt(empParam);
            emp = dao.getEmployeeById(empId);
        } else if ("EMPLOYEE".equals(role)) {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            emp = dao.getEmployeeByUserId(userId);
        } else {
            response.sendRedirect("login.jsp");
            return;
        }

        if (emp == null) {
            response.sendRedirect("dashboard");
            return;
        }

        String monthParam = request.getParameter("month");
        YearMonth yearMonth;
        if (monthParam == null || monthParam.isEmpty()) {
            yearMonth = YearMonth.now();
        } else {
            yearMonth = YearMonth.parse(monthParam);
        }

        PayrollService payrollService = new PayrollService();
        PayrollCalculation payroll = payrollService.calculatePayroll(emp, yearMonth, false);

        PDDocument doc = new PDDocument();
        try {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            float margin = 50;
            float yStart = page.getMediaBox().getHeight() - margin;

            PDPageContentStream cs = new PDPageContentStream(doc, page);
            try {
                float titleX = page.getMediaBox().getWidth() / 2 - 110;
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 18);
                cs.newLineAtOffset(titleX, yStart);
                cs.showText(safeText(Branding.COMPANY_NAME));
                cs.endText();

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 10);
                cs.newLineAtOffset(margin, yStart - 22);
                cs.showText(safeText("Payslip for: " + emp.getFirstName() + " " + emp.getLastName()));
                cs.endText();

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 10);
                cs.newLineAtOffset(margin, yStart - 38);
                cs.showText(safeText("Generated: " + LocalDate.now() + " | Payroll Month: " + yearMonth));
                cs.endText();

                float y = yStart - 80;

                cs.beginText();
                cs.setLeading(16f);
                cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
                cs.newLineAtOffset(margin, y);
                cs.showText(safeText("Employee Details"));
                cs.newLine();
                cs.setFont(PDType1Font.HELVETICA, 11);
                cs.showText(safeText("Employee ID: " + emp.getEmpId()));
                cs.newLine();
                cs.showText(safeText("Name: " + emp.getFirstName() + " " + emp.getLastName()));
                cs.newLine();
                cs.showText(safeText("Username: " + emp.getUsername()));
                cs.newLine();
                cs.showText(safeText("Department: " + emp.getDepartmentName()));
                cs.newLine();
                cs.showText(safeText("Email: " + emp.getEmail()));
                cs.newLine();
                cs.showText(safeText("Address: " + (emp.getAddress() != null ? emp.getAddress() : "-")));
                cs.endText();

                y -= 16f * 8;

                cs.beginText();
                cs.setLeading(16f);
                cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
                cs.newLineAtOffset(margin, y);
                cs.showText(safeText("Leave Deductions"));
                cs.newLine();
                cs.setFont(PDType1Font.HELVETICA, 11);
                cs.showText(safeText("Approved Leave Days: " + payroll.getApprovedLeaveDays()));
                cs.newLine();
                cs.showText(safeText("Leave Deduction: INR " + String.format("%.2f", payroll.getLeaveDeduction())));
                cs.endText();

                y -= 40;

                cs.beginText();
                cs.setLeading(16f);
                cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
                cs.newLineAtOffset(margin, y);
                cs.showText(safeText("Earnings"));
                cs.newLine();
                cs.setFont(PDType1Font.HELVETICA, 11);
                cs.showText(safeText("Basic Salary: INR " + String.format("%.2f", emp.getBasicSalary())));
                cs.newLine();
                cs.showText(safeText("HRA: INR " + String.format("%.2f", emp.getHra())));
                cs.newLine();
                cs.showText(safeText("DA: INR " + String.format("%.2f", emp.getDa())));
                cs.newLine();
                cs.showText(safeText("Allowances: INR " + String.format("%.2f", emp.getAllowances())));
                cs.newLine();
                cs.showText(safeText("Gross Salary: INR " + String.format("%.2f", payroll.getGrossSalary())));
                cs.endText();

                y -= 16f * 8;

                cs.beginText();
                cs.setLeading(16f);
                cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
                cs.newLineAtOffset(margin, y);
                cs.showText(safeText("Loan Deductions"));
                cs.newLine();
                cs.setFont(PDType1Font.HELVETICA, 11);
                List<LoanPayrollDeduction> loanDeductions = payroll.getLoanDeductions();
                if (loanDeductions.isEmpty()) {
                    cs.showText(safeText("No active loan deduction for this month."));
                } else {
                    cs.showText(safeText(String.format("%-18s %-10s %-10s", "Loan Type", "EMI", "Status")));
                    for (LoanPayrollDeduction deduction : loanDeductions) {
                        cs.newLine();
                        cs.showText(safeText(String.format("%-18s %-10s %-10s",
                                trimToLength(deduction.getLoanType(), 20),
                                "INR " + String.format("%.2f", deduction.getEmi()),
                                deduction.getStatus())));
                    }
                }
                cs.endText();

                y -= 16f * (loanDeductions.isEmpty() ? 3 : loanDeductions.size() + 4);

                cs.beginText();
                cs.setLeading(16f);
                cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
                cs.newLineAtOffset(margin, y);
                cs.showText(safeText("Final Summary"));
                cs.newLine();
                cs.setFont(PDType1Font.HELVETICA, 11);
                cs.showText(safeText("Gross Salary: INR " + String.format("%.2f", payroll.getGrossSalary())));
                cs.newLine();
                cs.showText(safeText("Total Deductions: INR " + String.format("%.2f",
                        payroll.getStandardDeduction() + payroll.getLeaveDeduction() + payroll.getLoanDeduction())));
                cs.newLine();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 13);
                cs.showText(safeText("Net Pay: INR " + String.format("%.2f", payroll.getNetSalary())));
                cs.endText();
            } finally {
                cs.close();
            }

            response.setContentType("application/pdf");
            String fileName = "Payslip_" + emp.getEmpId() + "_" + yearMonth.toString() + ".pdf";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            doc.save(response.getOutputStream());
        } finally {
            doc.close();
        }
    }

    private String trimToLength(String value, int maxLength) {
        value = safeText(value);
        if (value == null) {
            return "-";
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength - 1);
    }

    private String safeText(String value) {
        if (value == null) {
            return "-";
        }
        return value
                .replace('\r', ' ')
                .replace('\n', ' ')
                .replace('\t', ' ')
                .trim();
    }
}
