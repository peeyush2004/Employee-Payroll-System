<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.payroll.model.Employee" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.payroll.model.LeaveRequest" %>
<%@ page import="com.payroll.model.LoanRequest" %>
<%@ page import="com.payroll.util.Branding" %>
<jsp:include page="includes/header.jsp" />

<%
String username = (String) session.getAttribute("username");
String role = (String) session.getAttribute("role");
%>

<!-- Page Header -->
<div class="page-header d-flex justify-content-between align-items-center">
    <div>
        <h4 class="mb-1"><%= Branding.COMPANY_NAME %> Dashboard</h4>
        <p class="text-muted mb-0 small">Welcome back, <strong><%= username %></strong>! Here is your payroll and loan summary.</p>
    </div>
</div>

<% if ("ADMIN".equals(role)) { %>

<!-- Admin Stats Cards -->
<div class="row g-3 mb-4">
    <div class="col-12 col-sm-6 col-xl-4">
        <div class="card stat-card h-100">
            <div class="card-body d-flex justify-content-between align-items-center">
                <div>
                    <div class="stat-label">Total Employees</div>
                    <div class="stat-value mt-1"><%= request.getAttribute("totalEmployees") %></div>
                </div>
                <i class="bi bi-people stat-icon text-primary"></i>
            </div>
        </div>
    </div>
    <div class="col-12 col-sm-6 col-xl-4">
        <div class="card stat-card success h-100">
            <div class="card-body d-flex justify-content-between align-items-center">
                <div>
                    <div class="stat-label">Total Salary Paid</div>
                    <div class="stat-value mt-1">INR <%= request.getAttribute("totalSalary") %></div>
                </div>
                <i class="bi bi-cash-stack stat-icon text-success"></i>
            </div>
        </div>
    </div>
    <div class="col-12 col-sm-6 col-xl-4">
        <div class="card stat-card warning h-100">
            <div class="card-body d-flex justify-content-between align-items-center">
                <div>
                    <div class="stat-label">Average Salary</div>
                    <div class="stat-value mt-1">INR <%= request.getAttribute("avgSalary") %></div>
                </div>
                <i class="bi bi-bar-chart stat-icon text-warning"></i>
            </div>
        </div>
    </div>
    <div class="col-12 col-sm-6 col-xl-6">
        <div class="card stat-card success h-100">
            <div class="card-body d-flex justify-content-between align-items-center">
                <div>
                    <div class="stat-label">Pending Loan Requests</div>
                    <div class="stat-value mt-1"><%= request.getAttribute("pendingLoanCount") %></div>
                </div>
                <i class="bi bi-hourglass-split stat-icon text-success"></i>
            </div>
        </div>
    </div>
    <div class="col-12 col-sm-6 col-xl-6">
        <div class="card stat-card danger h-100">
            <div class="card-body d-flex justify-content-between align-items-center">
                <div>
                    <div class="stat-label">Active Loans</div>
                    <div class="stat-value mt-1"><%= request.getAttribute("activeLoanCount") %></div>
                </div>
                <i class="bi bi-cash-coin stat-icon text-danger"></i>
            </div>
        </div>
    </div>
</div>

<!-- Pending Leave Alert -->
<%
    Integer pendingLeaveCount = (Integer) request.getAttribute("pendingLeaveCount");
    if (pendingLeaveCount != null && pendingLeaveCount > 0) {
%>
<div class="alert alert-warning d-flex align-items-center justify-content-between mb-4" role="alert">
    <div>
        <i class="bi bi-exclamation-triangle-fill me-2"></i>
        You have <strong><%= pendingLeaveCount %></strong> pending leave request(s) awaiting your review.
    </div>
    <a href="viewLeaveRequests" class="btn btn-sm btn-warning">Review Now</a>
</div>
<% } %>

<div class="row g-3">
    <!-- Employees by Department -->
    <div class="col-12 col-xl-8">
        <div class="card h-100">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-diagram-3 me-2"></i>Employees by Department</span>
            </div>
            <div class="card-body p-0">
                <table class="table table-hover mb-0">
                    <thead class="table-light">
                        <tr>
                            <th class="ps-3">Department</th>
                            <th>Employee Count</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            Map<String, Integer> employeesByDepartment = (Map<String, Integer>) request.getAttribute("employeesByDepartment");
                            if (employeesByDepartment != null && !employeesByDepartment.isEmpty()) {
                                for (Map.Entry<String, Integer> entry : employeesByDepartment.entrySet()) {
                        %>
                        <tr>
                            <td class="ps-3"><%= entry.getKey() %></td>
                            <td>
                                <span class="badge bg-primary rounded-pill"><%= entry.getValue() %></span>
                            </td>
                        </tr>
                        <%      }
                            } else { %>
                        <tr>
                            <td colspan="2" class="text-center text-muted py-3">No department data available.</td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Monthly Leave Stats -->
    <div class="col-12 col-xl-4">
        <div class="card h-100">
            <div class="card-header">
                <i class="bi bi-calendar-month me-2"></i>Monthly Leave Stats
            </div>
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-center py-2 border-bottom">
                    <span class="text-muted">Approved</span>
                    <span class="badge bg-success fs-6"><%= request.getAttribute("approvedLeavesThisMonth") %></span>
                </div>
                <div class="d-flex justify-content-between align-items-center py-2">
                    <span class="text-muted">Rejected</span>
                    <span class="badge bg-danger fs-6"><%= request.getAttribute("rejectedLeavesThisMonth") %></span>
                </div>
            </div>
            <div class="card-footer">
                <a href="viewLeaveRequests" class="btn btn-sm btn-outline-primary w-100">
                    <i class="bi bi-list-check me-1"></i>View All Leaves
                </a>
            </div>
        </div>
    </div>
</div>

<div class="row g-3 mt-1">
    <div class="col-12">
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-cash-stack me-2"></i>Loan Request Queue</span>
                <a href="manageLoans" class="btn btn-sm btn-outline-primary">Open Loan Center</a>
            </div>
            <div class="card-body p-0">
                <%
                    List<LoanRequest> pendingLoanRequests = (List<LoanRequest>) request.getAttribute("pendingLoanRequests");
                    if (pendingLoanRequests == null || pendingLoanRequests.isEmpty()) {
                %>
                <div class="text-center py-4 text-muted">No pending loan requests.</div>
                <% } else { %>
                <div class="table-responsive">
                    <table class="table table-hover mb-0 align-middle">
                        <thead class="table-light">
                            <tr>
                                <th class="ps-3">Employee</th>
                                <th>Loan</th>
                                <th>Salary</th>
                                <th>Leave Record</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (LoanRequest loan : pendingLoanRequests) { %>
                            <tr>
                                <td class="ps-3">
                                    <div class="fw-semibold"><%= loan.getEmployeeName() %></div>
                                    <div class="small text-muted"><%= loan.getDepartmentName() %></div>
                                </td>
                                <td>
                                    <div><%= loan.getLoanType() %></div>
                                    <div class="small text-muted">INR <%= String.format("%.2f", loan.getLoanAmount()) %></div>
                                </td>
                                <td>INR <%= String.format("%.2f", loan.getEmployeeGrossSalary()) %></td>
                                <td><%= loan.getApprovedLeaveDaysThisMonth() %> approved day(s)</td>
                                <td><span class="badge bg-warning text-dark"><%= loan.getStatus() %></span></td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                <% } %>
            </div>
        </div>
    </div>
</div>

<% } else if ("EMPLOYEE".equals(role)) {
    Employee emp = (Employee) request.getAttribute("employee");
    String employeePhotoDataUri = (String) request.getAttribute("employeePhotoDataUri");
    if (emp != null) {
%>

<div class="row g-3">
    <!-- Profile Card -->
    <div class="col-12 col-xl-4">
        <div class="card text-center">
            <div class="card-body py-4">
                <div class="mb-3">
                    <img src="<%= employeePhotoDataUri != null ? employeePhotoDataUri : "" %>"
                         class="rounded-circle border"
                         width="90" height="90"
                         style="object-fit:cover;"
                         alt="Profile Photo"
                         onerror="this.src='https://ui-avatars.com/api/?name=<%= emp.getFirstName() %>+<%= emp.getLastName() %>&background=0d6efd&color=fff&size=90'">
                </div>
                <h5 class="mb-1"><%= emp.getFirstName() %> <%= emp.getLastName() %></h5>
                <p class="text-muted mb-1 small"><%= emp.getDepartmentName() %></p>
                <p class="text-muted mb-3 small"><%= emp.getEmail() %></p>
                <div class="d-flex justify-content-center gap-2">
                    <a href="viewPayslip" class="btn btn-outline-primary btn-sm">
                        <i class="bi bi-eye me-1"></i>View Payslip
                    </a>
                    <a href="generatePayslip" class="btn btn-primary btn-sm">
                        <i class="bi bi-file-earmark-pdf me-1"></i>Download Payslip
                    </a>
                </div>
            </div>
            <div class="card-footer text-start">
                <div class="row text-muted small">
                    <div class="col-5 fw-semibold">Address</div>
                    <div class="col-7"><%= emp.getAddress() %></div>
                    <div class="col-5 fw-semibold mt-2">Hire Date</div>
                    <div class="col-7 mt-2"><%= emp.getHireDate() != null ? emp.getHireDate() : "-" %></div>
                </div>
            </div>
        </div>
    </div>

    <!-- Salary & Leave -->
    <div class="col-12 col-xl-8">
        <!-- Salary Details -->
        <div class="card mb-3">
            <div class="card-header"><i class="bi bi-wallet2 me-2"></i>Salary Details</div>
            <div class="card-body">
                <div class="row g-2">
                    <div class="col-6 col-md-3 text-center">
                        <div class="small text-muted">Basic</div>
                        <div class="fw-bold">INR <%= emp.getBasicSalary() %></div>
                    </div>
                    <div class="col-6 col-md-3 text-center">
                        <div class="small text-muted">HRA</div>
                        <div class="fw-bold">INR <%= emp.getHra() %></div>
                    </div>
                    <div class="col-6 col-md-3 text-center">
                        <div class="small text-muted">DA</div>
                        <div class="fw-bold">INR <%= emp.getDa() %></div>
                    </div>
                    <div class="col-6 col-md-3 text-center">
                        <div class="small text-muted">Allowances</div>
                        <div class="fw-bold">INR <%= emp.getAllowances() %></div>
                    </div>
                </div>
                <hr>
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <span class="text-danger small">Deductions: <strong>INR <%= emp.getDeductions() %></strong></span>
                    </div>
                    <div class="bg-success bg-opacity-10 px-3 py-2 rounded">
                        <span class="text-success fw-bold fs-5">
                            Net: INR <%= emp.getBasicSalary() + emp.getHra() + emp.getDa() + emp.getAllowances() - emp.getDeductions() %>
                        </span>
                    </div>
                </div>
            </div>
        </div>

        <!-- Leave Notifications -->
        <%
            List<LeaveRequest> employeeLeaveRequests = (List<LeaveRequest>) request.getAttribute("employeeLeaveRequests");
            if (employeeLeaveRequests != null && !employeeLeaveRequests.isEmpty()) {
                boolean hasNotifications = false;
                for (LeaveRequest lr : employeeLeaveRequests) {
                    if (!"Pending".equals(lr.getStatus())) { hasNotifications = true; break; }
                }
                if (hasNotifications) {
        %>
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-bell me-2"></i>Leave Status Updates</span>
                <a href="myLeaveHistory" class="btn btn-sm btn-outline-primary">View All</a>
            </div>
            <div class="card-body">
                <% for (LeaveRequest lr : employeeLeaveRequests) {
                    if (!"Pending".equals(lr.getStatus())) {
                        String alertClass = "alert-info";
                        String icon = "bi-info-circle";
                        if ("Approved".equals(lr.getStatus())) { alertClass = "alert-success"; icon = "bi-check-circle"; }
                        else if ("Rejected".equals(lr.getStatus())) { alertClass = "alert-danger"; icon = "bi-x-circle"; }
                %>
                <div class="alert <%= alertClass %> py-2 mb-2 small" role="alert">
                    <i class="bi <%= icon %> me-1"></i>
                    Your <strong><%= lr.getLeaveType() %></strong> leave (<%= lr.getStartDate() %>) was
                    <strong><%= lr.getStatus() %></strong>.
                </div>
                <% } } %>
            </div>
        </div>
        <% } } %>

        <%
            List<LoanRequest> loanRequests = (List<LoanRequest>) request.getAttribute("loanRequests");
            Integer employeeActiveLoanCount = (Integer) request.getAttribute("employeeActiveLoanCount");
        %>
        <div class="card mt-3">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-cash-stack me-2"></i>Loan Request Section</span>
                <a href="employeeLoans" class="btn btn-sm btn-outline-primary">Open Loan Request Page</a>
            </div>
            <div class="card-body">
                <div class="row g-3 mb-3">
                    <div class="col-sm-6 col-lg-3">
                        <div class="border rounded p-3 h-100">
                            <div class="small text-muted">Active Loans</div>
                            <div class="fs-4 fw-bold"><%= employeeActiveLoanCount != null ? employeeActiveLoanCount : 0 %></div>
                        </div>
                    </div>
                    <div class="col-sm-6 col-lg-3">
                        <div class="border rounded p-3 h-100">
                            <div class="small text-muted">Interest Rate</div>
                            <div class="fs-4 fw-bold"><%= request.getAttribute("loanInterestRate") %>%</div>
                        </div>
                    </div>
                    <div class="col-sm-6 col-lg-6">
                        <div class="border rounded p-3 h-100 d-flex justify-content-between align-items-center">
                            <div>
                                <div class="fw-semibold">Need a salary advance or long-term loan?</div>
                                <div class="text-muted small">Preview EMI and submit the request from the employee portal.</div>
                            </div>
                            <a href="employeeLoans" class="btn btn-primary btn-sm">Request Loan</a>
                        </div>
                    </div>
                </div>

                <% if (loanRequests != null && !loanRequests.isEmpty()) { %>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>Loan Type</th>
                                <th>Amount</th>
                                <th>EMI</th>
                                <th>Upcoming Paused Months</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (LoanRequest loan : loanRequests) {
                                String loanBadgeClass = "bg-warning text-dark";
                                if ("ACTIVE".equalsIgnoreCase(loan.getDisplayStatus())) loanBadgeClass = "bg-success";
                                else if ("PAUSED".equalsIgnoreCase(loan.getDisplayStatus())) loanBadgeClass = "bg-secondary";
                                else if ("REJECTED".equalsIgnoreCase(loan.getDisplayStatus())) loanBadgeClass = "bg-danger";
                                else if ("COMPLETED".equalsIgnoreCase(loan.getDisplayStatus())) loanBadgeClass = "bg-primary";
                            %>
                            <tr>
                                <td><%= loan.getLoanType() %></td>
                                <td>INR <%= String.format("%.2f", loan.getLoanAmount()) %></td>
                                <td>INR <%= String.format("%.2f", loan.getMonthlyEmi()) %></td>
                                <td><%= loan.getPausedMonthsSummary() %></td>
                                <td><span class="badge <%= loanBadgeClass %>"><%= loan.getDisplayStatus() %></span></td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                <% } else { %>
                <div class="text-muted">No loan requests submitted yet.</div>
                <% } %>
            </div>
        </div>
    </div>
</div>
<% } } %>

<jsp:include page="includes/footer.jsp" />
