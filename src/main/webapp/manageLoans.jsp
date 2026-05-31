<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.time.YearMonth" %>
<%@ page import="java.util.List" %>
<%@ page import="com.payroll.model.LoanRequest" %>
<jsp:include page="includes/header.jsp" />

<%
    List<LoanRequest> pendingLoanRequests = (List<LoanRequest>) request.getAttribute("pendingLoanRequests");
    List<LoanRequest> activeLoans = (List<LoanRequest>) request.getAttribute("activeLoans");
    Double defaultInterestRate = (Double) request.getAttribute("defaultInterestRate");
    String errorMessage = (String) request.getAttribute("errorMessage");
    String successMessage = (String) request.getAttribute("successMessage");
    YearMonth currentMonth = YearMonth.now();
    YearMonth nextMonth = currentMonth.plusMonths(1);
%>

<div class="page-header d-flex justify-content-between align-items-center">
    <div>
        <h4 class="mb-1">Loan Management</h4>
        <p class="text-muted mb-0 small">Approve requests, monitor active loans, and manage EMI actions.</p>
    </div>
    <a href="dashboard" class="btn btn-outline-secondary btn-sm">
        <i class="bi bi-arrow-left me-1"></i>Back to Dashboard
    </a>
</div>

<% if (errorMessage != null) { %>
<div class="alert alert-danger"><%= errorMessage %></div>
<% } %>
<% if (successMessage != null) { %>
<div class="alert alert-success"><%= successMessage %></div>
<% } %>

<div class="card mb-4">
    <div class="card-header"><i class="bi bi-hourglass-split me-2"></i>Pending Loan Request Queue</div>
    <div class="card-body p-0">
        <% if (pendingLoanRequests == null || pendingLoanRequests.isEmpty()) { %>
        <div class="text-center py-5 text-muted">No pending loan requests.</div>
        <% } else { %>
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-dark">
                    <tr>
                        <th class="ps-3">Employee</th>
                        <th>Department</th>
                        <th>Salary</th>
                        <th>Leave Record</th>
                        <th>Loan</th>
                        <th>EMI Preview</th>
                        <th>Reason</th>
                        <th>Decision</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (LoanRequest loan : pendingLoanRequests) { %>
                    <tr>
                        <td class="ps-3">
                            <div class="fw-semibold"><%= loan.getEmployeeName() %></div>
                            <div class="small text-muted">Emp ID: <%= loan.getEmpId() %></div>
                        </td>
                        <td><%= loan.getDepartmentName() %></td>
                        <td>INR <%= String.format("%.2f", loan.getEmployeeGrossSalary()) %></td>
                        <td><%= loan.getApprovedLeaveDaysThisMonth() %> approved day(s) this month</td>
                        <td>
                            <div class="fw-semibold"><%= loan.getLoanType() %></div>
                            <div class="small text-muted">INR <%= String.format("%.2f", loan.getLoanAmount()) %> for <%= loan.getDurationMonths() %> months</div>
                        </td>
                        <td>
                            <div>EMI: INR <%= String.format("%.2f", loan.getMonthlyEmi()) %></div>
                            <div class="small text-muted">Total: INR <%= String.format("%.2f", loan.getTotalRepaymentAmount()) %></div>
                        </td>
                        <td class="small text-muted"><%= loan.getReason() %></td>
                        <td style="min-width: 290px;">
                            <form action="loanAction" method="post" class="mb-2">
                                <input type="hidden" name="loanId" value="<%= loan.getLoanId() %>">
                                <input type="hidden" name="action" value="approve">
                                <div class="row g-2">
                                    <div class="col-md-6">
                                        <label class="form-label small mb-1">Interest Rate</label>
                                        <input type="number" step="0.01" class="form-control form-control-sm"
                                               name="interestRate" value="<%= defaultInterestRate %>" readonly>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label small mb-1">EMI Start Month</label>
                                        <input type="month" class="form-control form-control-sm" name="emiStartMonth"
                                               value="<%= currentMonth %>" required>
                                    </div>
                                    <div class="col-12">
                                        <input type="text" class="form-control form-control-sm" name="adminRemark"
                                               placeholder="Approval note (optional)">
                                    </div>
                                    <div class="col-12">
                                        <button class="btn btn-sm btn-success" type="submit">Approve</button>
                                    </div>
                                </div>
                            </form>

                            <form action="loanAction" method="post">
                                <input type="hidden" name="loanId" value="<%= loan.getLoanId() %>">
                                <input type="hidden" name="action" value="reject">
                                <div class="input-group input-group-sm">
                                    <input type="text" class="form-control" name="adminRemark"
                                           placeholder="Rejection reason" required>
                                    <button class="btn btn-danger" type="submit">Reject</button>
                                </div>
                            </form>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
        <% } %>
    </div>
</div>

<div class="card">
    <div class="card-header"><i class="bi bi-wallet me-2"></i>Active Loan Management</div>
    <div class="card-body p-0">
        <% if (activeLoans == null || activeLoans.isEmpty()) { %>
        <div class="text-center py-5 text-muted">No active loans found.</div>
        <% } else { %>
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-dark">
                    <tr>
                        <th class="ps-3">Employee</th>
                        <th>Loan Type</th>
                        <th>EMI</th>
                        <th>Current Status</th>
                        <th>Paused Months</th>
                        <th>Start Month</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (LoanRequest loan : activeLoans) { %>
                    <tr>
                        <td class="ps-3">
                            <div class="fw-semibold"><%= loan.getEmployeeName() %></div>
                            <div class="small text-muted"><%= loan.getDepartmentName() %></div>
                        </td>
                        <td><%= loan.getLoanType() %></td>
                        <td>INR <%= String.format("%.2f", loan.getMonthlyEmi()) %></td>
                        <td>
                            <span class="badge <%= "PAUSED".equalsIgnoreCase(loan.getDisplayStatus()) ? "bg-secondary" : ("COMPLETED".equalsIgnoreCase(loan.getDisplayStatus()) ? "bg-primary" : "bg-success") %>">
                                <%= loan.getDisplayStatus() %>
                            </span>
                        </td>
                        <td class="small text-muted"><%= loan.getPausedMonthsSummary() %></td>
                        <td><%= loan.getEmiStartMonth() != null ? loan.getEmiStartMonth() : "-" %></td>
                        <td style="min-width: 280px;">
                            <form action="loanAction" method="post" class="mb-2">
                                <input type="hidden" name="loanId" value="<%= loan.getLoanId() %>">
                                <input type="hidden" name="action" value="pause">
                                <div class="row g-2">
                                    <div class="col-md-5">
                                        <input type="month" class="form-control form-control-sm" name="payrollMonth"
                                               value="<%= nextMonth %>" min="<%= nextMonth %>" required>
                                    </div>
                                    <div class="col-md-7">
                                        <div class="input-group input-group-sm">
                                            <input type="text" class="form-control" name="adminRemark"
                                                   placeholder="Pause reason">
                                            <button class="btn btn-outline-warning" type="submit">Schedule Pause</button>
                                        </div>
                                    </div>
                                </div>
                            </form>

                            <form action="loanAction" method="post">
                                <input type="hidden" name="loanId" value="<%= loan.getLoanId() %>">
                                <input type="hidden" name="action" value="close">
                                <div class="input-group input-group-sm">
                                    <input type="text" class="form-control" name="adminRemark"
                                           placeholder="Closure note" required>
                                    <button class="btn btn-outline-danger" type="submit">Close Loan</button>
                                </div>
                            </form>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
        <% } %>
    </div>
</div>

<jsp:include page="includes/footer.jsp" />
