<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.time.YearMonth" %>
<%@ page import="com.payroll.model.Employee" %>
<%@ page import="com.payroll.model.PayrollCalculation" %>
<%@ page import="com.payroll.model.LoanPayrollDeduction" %>
<jsp:include page="includes/header.jsp" />

<%
    Employee employee = (Employee) request.getAttribute("employee");
    PayrollCalculation payroll = (PayrollCalculation) request.getAttribute("payroll");
    YearMonth selectedMonth = (YearMonth) request.getAttribute("selectedMonth");
    String companyName = (String) request.getAttribute("companyName");
    double totalDeductions = payroll.getStandardDeduction() + payroll.getLeaveDeduction() + payroll.getLoanDeduction();
%>

<div class="page-header d-flex justify-content-between align-items-center">
    <div>
        <h4 class="mb-1">Payslip for <%= employee.getFirstName() %> <%= employee.getLastName() %></h4>
        <p class="text-muted mb-0 small"><%= companyName %> payroll statement for <strong><%= selectedMonth %></strong>.</p>
    </div>
    <div class="d-flex gap-2">
        <form method="get" action="viewPayslip" class="d-flex gap-2">
            <% if ("ADMIN".equals(session.getAttribute("role")) && request.getParameter("empId") != null) { %>
            <input type="hidden" name="empId" value="<%= request.getParameter("empId") %>">
            <% } %>
            <input type="month" name="month" class="form-control form-control-sm" value="<%= selectedMonth %>">
            <button class="btn btn-outline-secondary btn-sm" type="submit">View Month</button>
        </form>
        <a href="generatePayslip?<%= "ADMIN".equals(session.getAttribute("role")) && request.getParameter("empId") != null ? "empId=" + request.getParameter("empId") + "&" : "" %>month=<%= selectedMonth %>"
           class="btn btn-primary btn-sm">
            <i class="bi bi-file-earmark-pdf me-1"></i>Download PDF
        </a>
    </div>
</div>

<div class="card shadow-sm border-0">
    <div class="card-body p-4">
        <div class="text-center mb-4">
            <div class="small text-uppercase text-muted">Payroll Statement</div>
            <h3 class="mb-1"><%= companyName %></h3>
            <div class="text-muted">Payslip for <strong><%= employee.getFirstName() %> <%= employee.getLastName() %></strong></div>
        </div>

        <div class="row g-4">
            <div class="col-12 col-xl-6">
                <div class="card h-100">
                    <div class="card-header">Employee Details</div>
                    <div class="card-body p-0">
                        <table class="table table-bordered mb-0">
                            <tbody>
                                <tr><th style="width: 40%;">Employee ID</th><td><%= employee.getEmpId() %></td></tr>
                                <tr><th>Name</th><td><%= employee.getFirstName() %> <%= employee.getLastName() %></td></tr>
                                <tr><th>Username</th><td><%= employee.getUsername() %></td></tr>
                                <tr><th>Department</th><td><%= employee.getDepartmentName() %></td></tr>
                                <tr><th>Month</th><td><%= selectedMonth %></td></tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <div class="col-12 col-xl-6">
                <div class="card h-100">
                    <div class="card-header">Earnings</div>
                    <div class="card-body p-0">
                        <table class="table table-bordered mb-0">
                            <tbody>
                                <tr><th>Basic Salary</th><td>INR <%= String.format("%.2f", employee.getBasicSalary()) %></td></tr>
                                <tr><th>HRA</th><td>INR <%= String.format("%.2f", employee.getHra()) %></td></tr>
                                <tr><th>DA</th><td>INR <%= String.format("%.2f", employee.getDa()) %></td></tr>
                                <tr><th>Allowances</th><td>INR <%= String.format("%.2f", employee.getAllowances()) %></td></tr>
                                <tr class="table-light fw-bold"><th>Gross Salary</th><td>INR <%= String.format("%.2f", payroll.getGrossSalary()) %></td></tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <div class="col-12 col-xl-6">
                <div class="card h-100">
                    <div class="card-header">Leave Deductions</div>
                    <div class="card-body p-0">
                        <table class="table table-bordered mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>Leave Days</th>
                                    <th>Deduction Amount</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><%= payroll.getApprovedLeaveDays() %></td>
                                    <td>INR <%= String.format("%.2f", payroll.getLeaveDeduction()) %></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <div class="col-12 col-xl-6">
                <div class="card h-100">
                    <div class="card-header">Loan Deductions</div>
                    <div class="card-body p-0">
                        <table class="table table-bordered mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>Loan Type</th>
                                    <th>Month</th>
                                    <th>EMI Amount</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (payroll.getLoanDeductions().isEmpty()) { %>
                                <tr>
                                    <td colspan="4" class="text-center text-muted">No loan deduction entries for this month.</td>
                                </tr>
                                <% } else {
                                    for (LoanPayrollDeduction deduction : payroll.getLoanDeductions()) { %>
                                <tr>
                                    <td><%= deduction.getLoanType() %></td>
                                    <td><%= deduction.getPayrollMonth() != null ? deduction.getPayrollMonth() : selectedMonth %></td>
                                    <td>INR <%= String.format("%.2f", deduction.getEmi()) %></td>
                                    <td><span class="badge <%= "PAUSED".equalsIgnoreCase(deduction.getStatus()) ? "bg-secondary" : "bg-success" %>"><%= deduction.getStatus() %></span></td>
                                </tr>
                                <% } } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <div class="col-12">
                <div class="card">
                    <div class="card-header">Final Summary</div>
                    <div class="card-body p-0">
                        <table class="table table-bordered mb-0">
                            <tbody>
                                <tr><th style="width: 40%;">Gross Salary</th><td>INR <%= String.format("%.2f", payroll.getGrossSalary()) %></td></tr>
                                <tr><th>Standard Deductions</th><td>INR <%= String.format("%.2f", payroll.getStandardDeduction()) %></td></tr>
                                <tr><th>Leave Deductions</th><td>INR <%= String.format("%.2f", payroll.getLeaveDeduction()) %></td></tr>
                                <tr><th>Loan Deductions</th><td>INR <%= String.format("%.2f", payroll.getLoanDeduction()) %></td></tr>
                                <tr class="table-light fw-bold"><th>Total Deductions</th><td>INR <%= String.format("%.2f", totalDeductions) %></td></tr>
                                <tr class="table-primary fw-bold"><th>Net Salary</th><td>INR <%= String.format("%.2f", payroll.getNetSalary()) %></td></tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="includes/footer.jsp" />
