<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.payroll.model.Employee" %>
<%
    Employee emp = (Employee) request.getAttribute("employee");
%>
<jsp:include page="includes/header.jsp" />

<div class="page-header d-flex justify-content-between align-items-center">
    <div>
        <h4 class="mb-1">Edit Employee</h4>
        <p class="text-muted mb-0 small">Modify employee details and salary structure.</p>
    </div>
    <a href="viewEmployees" class="btn btn-outline-secondary btn-sm">
        <i class="bi bi-arrow-left me-1"></i>Back to List
    </a>
</div>

<div class="card">
    <div class="card-body">
        <form action="updateEmployee" method="post" enctype="multipart/form-data">
            <input type="hidden" name="id" value="<%= emp.getEmpId() %>">

            <!-- Personal Info -->
            <h6 class="text-uppercase text-muted fw-semibold mb-3 border-bottom pb-2">
                <i class="bi bi-person me-1"></i>Personal Information
            </h6>
            <div class="row g-3 mb-4">
                <div class="col-md-6">
                    <label class="form-label">First Name <span class="text-danger">*</span></label>
                    <input class="form-control" name="firstName" type="text"
                           value="<%= emp.getFirstName() %>" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Last Name <span class="text-danger">*</span></label>
                    <input class="form-control" name="lastName" type="text"
                           value="<%= emp.getLastName() %>" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Email <span class="text-danger">*</span></label>
                    <input class="form-control" name="email" type="email"
                           value="<%= emp.getEmail() %>" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Update Profile Photo <span class="text-muted small">(optional)</span></label>
                    <input class="form-control" name="photo" type="file" accept="image/*">
                    <% if (emp.getPhoto() != null && !emp.getPhoto().isEmpty()) { %>
                    <div class="form-text">Current: <%= emp.getPhoto() %></div>
                    <% } %>
                </div>
                <div class="col-12">
                    <label class="form-label">Address <span class="text-danger">*</span></label>
                    <textarea class="form-control" name="address" rows="2" required><%= emp.getAddress() %></textarea>
                </div>
            </div>

            <!-- Salary Structure -->
            <h6 class="text-uppercase text-muted fw-semibold mb-3 border-bottom pb-2">
                <i class="bi bi-wallet2 me-1"></i>Salary Structure
            </h6>
            <div class="row g-3 mb-4">
                <div class="col-md-4">
                    <label class="form-label">Basic Salary <span class="text-danger">*</span></label>
                    <div class="input-group">
                        <span class="input-group-text">₹</span>
                        <input class="form-control" name="basicSalary" type="number" step="0.01"
                               value="<%= emp.getBasicSalary() %>" required>
                    </div>
                </div>
                <div class="col-md-4">
                    <label class="form-label">HRA</label>
                    <div class="input-group">
                        <span class="input-group-text">₹</span>
                        <input class="form-control" name="hra" type="number" step="0.01"
                               value="<%= emp.getHra() %>">
                    </div>
                </div>
                <div class="col-md-4">
                    <label class="form-label">DA</label>
                    <div class="input-group">
                        <span class="input-group-text">₹</span>
                        <input class="form-control" name="da" type="number" step="0.01"
                               value="<%= emp.getDa() %>">
                    </div>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Allowances</label>
                    <div class="input-group">
                        <span class="input-group-text">₹</span>
                        <input class="form-control" name="allowances" type="number" step="0.01"
                               value="<%= emp.getAllowances() %>">
                    </div>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Deductions</label>
                    <div class="input-group">
                        <span class="input-group-text">₹</span>
                        <input class="form-control" name="deductions" type="number" step="0.01"
                               value="<%= emp.getDeductions() %>">
                    </div>
                </div>
            </div>

            <div class="d-flex gap-2">
                <button class="btn btn-primary" type="submit">
                    <i class="bi bi-check-lg me-1"></i>Update Employee
                </button>
                <a href="viewEmployees" class="btn btn-outline-secondary">
                    <i class="bi bi-x-lg me-1"></i>Cancel
                </a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="includes/footer.jsp" />
