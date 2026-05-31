<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="includes/header.jsp" />

<%
    String errorMessage = (String) request.getAttribute("errorMessage");
%>

<div class="page-header d-flex justify-content-between align-items-center">
    <div>
        <h4 class="mb-1">Add New Employee</h4>
        <p class="text-muted mb-0 small">Fill in the details below to register a new employee.</p>
    </div>
    <a href="viewEmployees" class="btn btn-outline-secondary btn-sm">
        <i class="bi bi-arrow-left me-1"></i>Back to List
    </a>
</div>

<% if (errorMessage != null) { %>
<div class="alert alert-danger alert-dismissible fade show" role="alert">
    <i class="bi bi-exclamation-circle me-2"></i><%= errorMessage %>
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
</div>
<% } %>

<div class="card">
    <div class="card-body">
        <form action="addEmployee" method="post" enctype="multipart/form-data">

            <!-- Login Info -->
            <h6 class="text-uppercase text-muted fw-semibold mb-3 border-bottom pb-2">
                <i class="bi bi-lock me-1"></i>Login Information
            </h6>
            <div class="row g-3 mb-4">
                <div class="col-md-6">
                    <label class="form-label">Username <span class="text-danger">*</span></label>
                    <input class="form-control" name="username" type="text" placeholder="Enter username" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Password <span class="text-danger">*</span></label>
                    <input class="form-control" name="password" type="password" placeholder="Enter password" required>
                </div>
            </div>

            <!-- Personal Info -->
            <h6 class="text-uppercase text-muted fw-semibold mb-3 border-bottom pb-2">
                <i class="bi bi-person me-1"></i>Personal Information
            </h6>
            <div class="row g-3 mb-4">
                <div class="col-md-6">
                    <label class="form-label">First Name <span class="text-danger">*</span></label>
                    <input class="form-control" name="firstName" type="text" placeholder="First Name" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Last Name <span class="text-danger">*</span></label>
                    <input class="form-control" name="lastName" type="text" placeholder="Last Name" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Email <span class="text-danger">*</span></label>
                    <input class="form-control" name="email" type="email" placeholder="name@company.com" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Hire Date <span class="text-danger">*</span></label>
                    <input class="form-control" name="hireDate" type="date" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Department <span class="text-danger">*</span></label>
                    <select class="form-select" name="deptId" required>
                        <option value="">Select Department</option>
                        <option value="1">HR</option>
                        <option value="2">IT</option>
                        <option value="3">Finance</option>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Profile Photo <span class="text-danger">*</span></label>
                    <input class="form-control" name="photo" type="file" accept="image/*" required>
                </div>
                <div class="col-12">
                    <label class="form-label">Address <span class="text-danger">*</span></label>
                    <textarea class="form-control" name="address" rows="2" placeholder="Enter full address" required></textarea>
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
                        <input class="form-control" name="basicSalary" type="number" step="0.01" placeholder="0.00" required>
                    </div>
                </div>
                <div class="col-md-4">
                    <label class="form-label">HRA</label>
                    <div class="input-group">
                        <span class="input-group-text">₹</span>
                        <input class="form-control" name="hra" type="number" step="0.01" placeholder="0.00">
                    </div>
                </div>
                <div class="col-md-4">
                    <label class="form-label">DA</label>
                    <div class="input-group">
                        <span class="input-group-text">₹</span>
                        <input class="form-control" name="da" type="number" step="0.01" placeholder="0.00">
                    </div>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Allowances</label>
                    <div class="input-group">
                        <span class="input-group-text">₹</span>
                        <input class="form-control" name="allowances" type="number" step="0.01" placeholder="0.00">
                    </div>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Deductions</label>
                    <div class="input-group">
                        <span class="input-group-text">₹</span>
                        <input class="form-control" name="deductions" type="number" step="0.01" placeholder="0.00">
                    </div>
                </div>
            </div>

            <div class="d-flex gap-2">
                <button class="btn btn-primary" type="submit">
                    <i class="bi bi-person-check me-1"></i>Add Employee
                </button>
                <button class="btn btn-outline-secondary" type="reset">
                    <i class="bi bi-arrow-counterclockwise me-1"></i>Reset
                </button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="includes/footer.jsp" />
