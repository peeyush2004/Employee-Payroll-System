<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.payroll.model.LeaveRequest" %>
<%@ page import="com.payroll.model.Employee" %>
<%@ page import="com.payroll.dao.EmployeeDAO" %>
<jsp:include page="includes/header.jsp" />

<div class="page-header">
    <h4 class="mb-1">Manage Leave Requests</h4>
    <p class="text-muted mb-0 small">Review and approve or reject employee leave applications.</p>
</div>

<%
    List<LeaveRequest> leaveRequests = (List<LeaveRequest>) request.getAttribute("leaveRequests");
    EmployeeDAO employeeDAO = new EmployeeDAO();
%>

<div class="card">
    <div class="card-body p-0">
        <% if (leaveRequests == null || leaveRequests.isEmpty()) { %>
        <div class="text-center py-5 text-muted">
            <i class="bi bi-calendar-x fs-1 d-block mb-2"></i>
            No leave requests found.
        </div>
        <% } else { %>
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-dark">
                    <tr>
                        <th class="ps-3">ID</th>
                        <th>Employee</th>
                        <th>Type</th>
                        <th>Start Date</th>
                        <th>End Date</th>
                        <th>Reason</th>
                        <th>Applied Date</th>
                        <th class="text-center">Status</th>
                        <th class="text-center">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (LeaveRequest lr : leaveRequests) {
                        Employee emp = employeeDAO.getEmployeeById(lr.getEmpId());
                        String employeeName = (emp != null) ? emp.getFirstName() + " " + emp.getLastName() : "N/A";
                        String statusClass = "bg-warning text-dark";
                        if ("Approved".equals(lr.getStatus())) statusClass = "bg-success";
                        else if ("Rejected".equals(lr.getStatus())) statusClass = "bg-danger";
                    %>
                    <tr>
                        <td class="ps-3"><%= lr.getLeaveId() %></td>
                        <td class="fw-semibold"><%= employeeName %></td>
                        <td><%= lr.getLeaveType() %></td>
                        <td><%= lr.getStartDate() %></td>
                        <td><%= lr.getEndDate() %></td>
                        <td class="text-muted small" style="max-width:140px;"><%= lr.getReason() %></td>
                        <td class="text-muted small"><%= lr.getAppliedDate() %></td>
                        <td class="text-center">
                            <span class="badge <%= statusClass %>"><%= lr.getStatus() %></span>
                        </td>
                        <td class="text-center">
                            <% if ("Pending".equals(lr.getStatus())) { %>
                            <div class="d-flex gap-1 justify-content-center">
                                <a href="updateLeaveStatus?leaveId=<%= lr.getLeaveId() %>&status=Approved"
                                   class="btn btn-sm btn-success"
                                   onclick="return confirm('Approve this leave request?')">
                                    <i class="bi bi-check-lg"></i> Approve
                                </a>
                                <a href="updateLeaveStatus?leaveId=<%= lr.getLeaveId() %>&status=Rejected"
                                   class="btn btn-sm btn-danger"
                                   onclick="return confirm('Reject this leave request?')">
                                    <i class="bi bi-x-lg"></i> Reject
                                </a>
                            </div>
                            <% } else { %>
                            <button class="btn btn-sm btn-outline-secondary" disabled>No Action</button>
                            <% } %>
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
