<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.payroll.model.Employee" %>
<jsp:include page="includes/header.jsp" />

<%
    String errorMessage = (String) request.getAttribute("errorMessage");
    String successMessage = (String) request.getAttribute("successMessage");
    Employee employee = (Employee) request.getAttribute("employee");
%>

<div class="page-header d-flex justify-content-between align-items-center">
    <div>
        <h4 class="mb-1">Apply for Leave</h4>
        <p class="text-muted mb-0 small">Submit your leave request for administrator approval.</p>
    </div>
    <a href="dashboard" class="btn btn-outline-secondary btn-sm">
        <i class="bi bi-arrow-left me-1"></i>Back to Dashboard
    </a>
</div>

<% if (errorMessage != null) { %>
<div class="alert alert-danger alert-dismissible fade show" role="alert">
    <i class="bi bi-exclamation-circle me-2"></i><%= errorMessage %>
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
</div>
<% } %>
<% if (successMessage != null) { %>
<div class="alert alert-success alert-dismissible fade show" role="alert">
    <i class="bi bi-check-circle me-2"></i><%= successMessage %>
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
</div>
<% } %>

<div class="row g-3">
    <!-- Form -->
    <div class="col-12 col-xl-8">
        <div class="card">
            <div class="card-header"><i class="bi bi-calendar-plus me-2"></i>Leave Request Form</div>
            <div class="card-body">
                <form action="applyLeave" method="post">
                    <div class="row g-3">
                        <div class="col-12">
                            <label class="form-label">Employee Name</label>
                            <input type="text" class="form-control" readonly disabled
                                   value="<%= employee != null ? employee.getFirstName() + " " + employee.getLastName() : "" %>">
                        </div>
                        <div class="col-12">
                            <label class="form-label">Leave Type <span class="text-danger">*</span></label>
                            <select class="form-select" name="leaveType" required>
                                <option value="">Select Leave Type</option>
                                <option value="Casual Leave">Casual Leave</option>
                                <option value="Sick Leave">Sick Leave</option>
                                <option value="Annual Leave">Annual Leave</option>
                                <option value="Maternity Leave">Maternity Leave</option>
                                <option value="Paternity Leave">Paternity Leave</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Start Date <span class="text-danger">*</span></label>
                            <input type="date" class="form-control" name="startDate" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">End Date <span class="text-danger">*</span></label>
                            <input type="date" class="form-control" name="endDate" required>
                        </div>
                        <div class="col-12">
                            <label class="form-label">Reason <span class="text-danger">*</span></label>
                            <textarea class="form-control" name="reason" rows="4"
                                      placeholder="Briefly explain the reason..." required></textarea>
                        </div>
                        <div class="col-12 d-flex gap-2">
                            <button class="btn btn-primary" type="submit">
                                <i class="bi bi-send me-1"></i>Submit Request
                            </button>
                            <a href="dashboard" class="btn btn-outline-secondary">Cancel</a>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Leave Policy -->
    <div class="col-12 col-xl-4">
        <div class="card">
            <div class="card-header"><i class="bi bi-info-circle me-2"></i>Leave Policy</div>
            <div class="list-group list-group-flush">
                <div class="list-group-item d-flex justify-content-between align-items-center">
                    <div>
                        <div class="fw-semibold">Casual Leave</div>
                        <div class="text-muted small">For personal emergencies</div>
                    </div>
                    <span class="badge bg-primary rounded-pill">12 days</span>
                </div>
                <div class="list-group-item d-flex justify-content-between align-items-center">
                    <div>
                        <div class="fw-semibold">Sick Leave</div>
                        <div class="text-muted small">Medical reasons</div>
                    </div>
                    <span class="badge bg-primary rounded-pill">10 days</span>
                </div>
                <div class="list-group-item d-flex justify-content-between align-items-center">
                    <div>
                        <div class="fw-semibold">Annual Leave</div>
                        <div class="text-muted small">Planned vacation</div>
                    </div>
                    <span class="badge bg-primary rounded-pill">15 days</span>
                </div>
                <div class="list-group-item d-flex justify-content-between align-items-center">
                    <div>
                        <div class="fw-semibold">Maternity Leave</div>
                        <div class="text-muted small">As per policy</div>
                    </div>
                    <span class="badge bg-secondary rounded-pill">As per law</span>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="includes/footer.jsp" />
