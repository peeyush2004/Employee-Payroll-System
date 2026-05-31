<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.payroll.model.Employee" %>
<%@ page import="com.payroll.model.LoanRequest" %>
<%@ page import="com.payroll.model.LoanSchedulePreview" %>
<jsp:include page="includes/header.jsp" />

<%
    Employee employee = (Employee) request.getAttribute("employee");
    List<String> loanTypes = (List<String>) request.getAttribute("loanTypes");
    List<LoanRequest> loanRequests = (List<LoanRequest>) request.getAttribute("loanRequests");
    LoanSchedulePreview loanPreview = (LoanSchedulePreview) request.getAttribute("loanPreview");
    Double interestRate = (Double) request.getAttribute("interestRate");
    String errorMessage = (String) request.getAttribute("errorMessage");
    String successMessage = (String) request.getAttribute("successMessage");
%>

<div class="page-header d-flex justify-content-between align-items-center">
    <div>
        <h4 class="mb-1">Loan Request</h4>
        <p class="text-muted mb-0 small">Request a company loan and preview EMI before submitting.</p>
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

<div class="row g-3">
    <div class="col-12 col-xl-5">
        <div class="card">
            <div class="card-header"><i class="bi bi-cash-stack me-2"></i>New Loan Request</div>
            <div class="card-body">
                <form method="post" action="employeeLoans">
                    <div class="mb-3">
                        <label class="form-label">Employee</label>
                        <input type="text" class="form-control" readonly
                               value="<%= employee != null ? employee.getFirstName() + " " + employee.getLastName() : "" %>">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Loan Type</label>
                        <select class="form-select" name="loanType" required>
                            <option value="">Select Loan Type</option>
                            <% if (loanTypes != null) { for (String loanType : loanTypes) { %>
                            <option value="<%= loanType %>"><%= loanType %></option>
                            <% } } %>
                        </select>
                    </div>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label">Loan Amount</label>
                            <input type="number" min="1" step="0.01" class="form-control" name="loanAmount" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Duration (Months)</label>
                            <input type="number" min="1" max="360" class="form-control" name="durationMonths" required>
                        </div>
                    </div>
                    <div class="mt-3">
                        <label class="form-label">Reason</label>
                        <textarea class="form-control" name="reason" rows="4" required></textarea>
                    </div>
                    <div class="alert alert-light border mt-3 mb-3">
                        Fixed Interest Rate: <strong><%= interestRate %>% p.a.</strong>
                    </div>
                    <div class="row g-2 small mb-3">
                        <div class="col-sm-6">
                            <div class="border rounded p-2">
                                <div class="text-muted">Live EMI Preview</div>
                                <div class="fw-semibold" id="liveEmi">INR 0.00</div>
                            </div>
                        </div>
                        <div class="col-sm-6">
                            <div class="border rounded p-2">
                                <div class="text-muted">Live Total Repayment</div>
                                <div class="fw-semibold" id="liveTotal">INR 0.00</div>
                            </div>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-send me-1"></i>Submit Loan Request
                    </button>
                </form>
            </div>
        </div>

        <div class="card mt-3">
            <div class="card-header"><i class="bi bi-calculator me-2"></i>Installment Preview</div>
            <div class="card-body">
                <form method="get" action="employeeLoans" class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label">Loan Amount</label>
                        <input type="number" min="1" step="0.01" class="form-control" name="previewAmount" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Duration (Months)</label>
                        <input type="number" min="1" max="360" class="form-control" name="previewDuration" required>
                    </div>
                    <div class="col-12">
                        <button class="btn btn-outline-primary" type="submit">Preview EMI</button>
                    </div>
                </form>

                <% if (loanPreview != null) { %>
                <hr>
                <div class="d-flex justify-content-between py-1">
                    <span class="text-muted">Monthly EMI</span>
                    <strong>INR <%= String.format("%.2f", loanPreview.getMonthlyEmi()) %></strong>
                </div>
                <div class="d-flex justify-content-between py-1">
                    <span class="text-muted">Total Repayment</span>
                    <strong>INR <%= String.format("%.2f", loanPreview.getTotalRepaymentAmount()) %></strong>
                </div>
                <% } %>
            </div>
        </div>
    </div>

    <div class="col-12 col-xl-7">
        <div class="card mb-3">
            <div class="card-header"><i class="bi bi-list-check me-2"></i>Submitted Loan Requests</div>
            <div class="card-body p-0">
                <% if (loanRequests == null || loanRequests.isEmpty()) { %>
                <div class="text-center py-5 text-muted">No loan requests submitted yet.</div>
                <% } else { %>
                <div class="table-responsive">
                    <table class="table table-hover mb-0 align-middle">
                        <thead class="table-light">
                            <tr>
                                <th class="ps-3">Type</th>
                                <th>Amount</th>
                                <th>EMI</th>
                                <th>Total</th>
                                <th>Upcoming Paused Months</th>
                                <th>Status</th>
                                <th>Start</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (LoanRequest loan : loanRequests) {
                                String badgeClass = "bg-warning text-dark";
                                if ("ACTIVE".equalsIgnoreCase(loan.getDisplayStatus())) badgeClass = "bg-success";
                                else if ("PAUSED".equalsIgnoreCase(loan.getDisplayStatus())) badgeClass = "bg-secondary";
                                else if ("REJECTED".equalsIgnoreCase(loan.getDisplayStatus())) badgeClass = "bg-danger";
                                else if ("COMPLETED".equalsIgnoreCase(loan.getDisplayStatus())) badgeClass = "bg-primary";
                            %>
                            <tr>
                                <td class="ps-3"><%= loan.getLoanType() %></td>
                                <td>INR <%= String.format("%.2f", loan.getLoanAmount()) %></td>
                                <td>INR <%= String.format("%.2f", loan.getMonthlyEmi()) %></td>
                                <td>INR <%= String.format("%.2f", loan.getTotalRepaymentAmount()) %></td>
                                <td class="small text-muted"><%= loan.getPausedMonthsSummary() %></td>
                                <td><span class="badge <%= badgeClass %>"><%= loan.getDisplayStatus() %></span></td>
                                <td><%= loan.getEmiStartMonth() != null ? loan.getEmiStartMonth() : "-" %></td>
                            </tr>
                            <% if (loan.getAdminRemark() != null && !loan.getAdminRemark().isBlank()) { %>
                            <tr>
                                <td></td>
                                <td colspan="6" class="small text-muted">Admin Note: <%= loan.getAdminRemark() %></td>
                            </tr>
                            <% } %>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                <% } %>
            </div>
        </div>

    </div>
</div>

<jsp:include page="includes/footer.jsp" />
<script>
    (function () {
        const amountInput = document.querySelector('input[name="loanAmount"]');
        const durationInput = document.querySelector('input[name="durationMonths"]');
        const emiTarget = document.getElementById('liveEmi');
        const totalTarget = document.getElementById('liveTotal');
        const annualRate = <%= interestRate != null ? interestRate : 5.0 %>;
        let lastPreviewKey = '';

        function roundCurrency(value) {
            return Math.round((value + Number.EPSILON) * 100) / 100;
        }

        function calculateEmi(principal, annualRateValue, months) {
            if (!Number.isFinite(principal) || !Number.isFinite(annualRateValue) || !Number.isInteger(months)
                    || principal <= 0 || annualRateValue < 0 || months <= 0) {
                return 0;
            }

            const monthlyRate = annualRateValue / (12 * 100);
            if (monthlyRate === 0) {
                return roundCurrency(principal / months);
            }

            const factor = Math.pow(1 + monthlyRate, months);
            const emi = (principal * monthlyRate * factor) / (factor - 1);
            return roundCurrency(emi);
        }

        function updatePreview() {
            const principal = parseFloat(amountInput.value || '0');
            const months = parseInt(durationInput.value || '0', 10);
            const previewKey = principal + '|' + months + '|' + annualRate;

            if (!principal || !months) {
                lastPreviewKey = '';
                emiTarget.textContent = 'INR 0.00';
                totalTarget.textContent = 'INR 0.00';
                return;
            }

            if (previewKey === lastPreviewKey) {
                return;
            }

            lastPreviewKey = previewKey;
            const emi = calculateEmi(principal, annualRate, months);
            const total = roundCurrency(emi * months);
            emiTarget.textContent = 'INR ' + emi.toFixed(2);
            totalTarget.textContent = 'INR ' + total.toFixed(2);
        }

        amountInput.addEventListener('input', updatePreview);
        durationInput.addEventListener('input', updatePreview);
        updatePreview();
    })();
</script>
