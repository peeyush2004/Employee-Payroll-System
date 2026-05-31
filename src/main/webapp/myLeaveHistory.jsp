<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.payroll.model.LeaveRequest" %>
<jsp:include page="includes/header.jsp" />

<%
    List<LeaveRequest> myLeaveRequests = (List<LeaveRequest>) request.getAttribute("myLeaveRequests");
%>

<div class="page-header d-flex justify-content-between align-items-center">
    <div>
        <h4 class="mb-1">My Leave History</h4>
        <p class="text-muted mb-0 small">Track the status and history of your leave applications.</p>
    </div>
    <a href="applyLeave" class="btn btn-primary btn-sm">
        <i class="bi bi-calendar-plus me-1"></i>Apply New Leave
    </a>
</div>

<div class="card">
    <div class="card-body p-0">
        <% if (myLeaveRequests == null || myLeaveRequests.isEmpty()) { %>
        <div class="text-center py-5 text-muted">
            <i class="bi bi-calendar-x fs-1 d-block mb-2"></i>
            <h6>No Leave History Found</h6>
            <p class="small">You haven't submitted any leave requests yet.</p>
            <a href="applyLeave" class="btn btn-primary btn-sm">Apply Now</a>
        </div>
        <% } else { %>
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-dark">
                    <tr>
                        <th class="ps-3">ID</th>
                        <th>Type</th>
                        <th>Duration</th>
                        <th>Reason</th>
                        <th class="text-center">Applied Date</th>
                        <th class="text-center">Status</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (LeaveRequest lr : myLeaveRequests) {
                        String badgeClass = "bg-warning text-dark";
                        if ("Approved".equals(lr.getStatus())) badgeClass = "bg-success";
                        else if ("Rejected".equals(lr.getStatus())) badgeClass = "bg-danger";
                    %>
                    <tr>
                        <td class="ps-3"><%= lr.getLeaveId() %></td>
                        <td><%= lr.getLeaveType() %></td>
                        <td>
                            <span class="small d-block"><%= lr.getStartDate() %></span>
                            <span class="small text-muted">to <%= lr.getEndDate() %></span>
                        </td>
                        <td class="text-muted small" style="max-width:180px;"><%= lr.getReason() %></td>
                        <td class="text-center small"><%= lr.getAppliedDate() %></td>
                        <td class="text-center">
                            <span class="badge <%= badgeClass %> px-3 py-1"><%= lr.getStatus() %></span>
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
