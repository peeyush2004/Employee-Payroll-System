<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.payroll.model.Employee" %>
<%
    String username = (String) session.getAttribute("username");
    String role = (String) session.getAttribute("role");
    if (username == null || role == null || !"ADMIN".equals(role)) {
        response.sendRedirect("login.jsp");
        return;
    }
    List<Employee> list = (List<Employee>) request.getAttribute("employeeList");
    String keyword = request.getParameter("keyword");
    if (keyword == null) keyword = "";
%>
<jsp:include page="includes/header.jsp" />

<div class="page-header d-flex justify-content-between align-items-center">
    <div>
        <h4 class="mb-1">All Employees</h4>
        <p class="text-muted mb-0 small">Manage and view all registered employees.</p>
    </div>
    <a href="addEmployee" class="btn btn-primary btn-sm">
        <i class="bi bi-person-plus me-1"></i>Add Employee
    </a>
</div>

<!-- Search -->
<div class="card mb-4">
    <div class="card-body py-2">
        <form method="get" action="viewEmployees" class="d-flex gap-2 align-items-center">
            <input type="text" name="keyword" class="form-control form-control-sm" style="max-width:280px;"
                   placeholder="Search by name..." value="<%= keyword %>">
            <button type="submit" class="btn btn-primary btn-sm">
                <i class="bi bi-search me-1"></i>Search
            </button>
            <a href="viewEmployees" class="btn btn-outline-secondary btn-sm">
                <i class="bi bi-arrow-counterclockwise me-1"></i>Reset
            </a>
        </form>
    </div>
</div>

<!-- Table -->
<div class="card">
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-dark">
                    <tr>
                        <th class="ps-3">ID</th>
                        <th>Username</th>
                        <th>Full Name</th>
                        <th>Email</th>
                        <th>Department</th>
                        <th>Address</th>
                        <th>Net Salary</th>
                        <th class="text-center">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (list != null && !list.isEmpty()) {
                        for (Employee emp : list) {
                            double netSalary = emp.getBasicSalary() + emp.getHra() + emp.getDa()
                                             + emp.getAllowances() - emp.getDeductions();
                    %>
                    <tr>
                        <td class="ps-3"><%= emp.getEmpId() %></td>
                        <td><%= emp.getUsername() %></td>
                        <td class="fw-semibold"><%= emp.getFirstName() %> <%= emp.getLastName() %></td>
                        <td class="text-muted small"><%= emp.getEmail() %></td>
                        <td><span class="badge bg-secondary"><%= emp.getDepartmentName() %></span></td>
                        <td class="text-muted small"><%= emp.getAddress() != null ? emp.getAddress() : "-" %></td>
                        <td class="fw-bold text-success">INR <%= String.format("%.2f", netSalary) %></td>
                        <td class="text-center">
                            <div class="d-flex gap-1 justify-content-center flex-wrap">
                                <a href="viewPayslip?empId=<%= emp.getEmpId() %>"
                                   class="btn btn-sm btn-outline-primary" title="View Payslip">
                                    <i class="bi bi-eye"></i>
                                </a>
                                <a href="generatePayslip?empId=<%= emp.getEmpId() %>"
                                   class="btn btn-sm btn-outline-secondary" title="Download Payslip">
                                    <i class="bi bi-file-earmark-pdf"></i>
                                </a>
                                <a href="editEmployee?id=<%= emp.getEmpId() %>"
                                   class="btn btn-sm btn-primary" title="Edit">
                                    <i class="bi bi-pencil"></i>
                                </a>
                                <a href="deleteEmployee?id=<%= emp.getEmpId() %>"
                                   class="btn btn-sm btn-danger" title="Delete"
                                   onclick="return confirm('Are you sure you want to delete this employee?')">
                                    <i class="bi bi-trash"></i>
                                </a>
                            </div>
                        </td>
                    </tr>
                    <% } } else { %>
                    <tr>
                        <td colspan="8" class="text-center text-muted py-4">
                            <i class="bi bi-people fs-3 d-block mb-2"></i>No employees found.
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="includes/footer.jsp" />
