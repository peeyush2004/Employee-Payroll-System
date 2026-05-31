<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.payroll.util.Branding" %>
<%
String selectedRole = request.getAttribute("selectedRole") != null
        ? String.valueOf(request.getAttribute("selectedRole"))
        : "ADMIN";
String enteredUsername = request.getAttribute("enteredUsername") != null
        ? String.valueOf(request.getAttribute("enteredUsername"))
        : "";
String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login - <%= Branding.COMPANY_NAME %></title>

<!-- Bootstrap CSS -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">

<style>
    :root {
        --login-bg: #eef2f7;
        --login-primary: #2563eb;
        --login-primary-dark: #1d4ed8;
        --login-text: #18212f;
        --login-muted: #657386;
        --login-border: #dfe6ef;
    }

    * {
        letter-spacing: 0;
    }

    body {
        background:
            radial-gradient(circle at 18% 18%, rgba(37, 99, 235, 0.18), transparent 24rem),
            radial-gradient(circle at 82% 78%, rgba(20, 184, 166, 0.14), transparent 22rem),
            linear-gradient(135deg, #f8fafc 0%, var(--login-bg) 100%);
        min-height: 100vh;
        display: flex;
        justify-content: center;
        align-items: center;
        color: var(--login-text);
        padding: 1.25rem;
    }

    .login-card {
        width: min(100%, 430px);
        border-radius: 0.75rem;
        border: 1px solid rgba(223, 230, 239, 0.9);
        box-shadow: 0 24px 60px rgba(15, 23, 42, 0.14);
        overflow: hidden;
    }

    .login-card::before {
        content: "";
        display: block;
        height: 5px;
        background: linear-gradient(90deg, var(--login-primary), #0891b2, #16a34a);
    }

    .brand-mark {
        width: 62px;
        height: 62px;
        border-radius: 0.75rem;
        background: rgba(37, 99, 235, 0.1);
        color: var(--login-primary);
        display: inline-flex;
        align-items: center;
        justify-content: center;
        font-size: 1.6rem;
        margin-bottom: 1rem;
        box-shadow: inset 0 0 0 1px rgba(37, 99, 235, 0.1);
    }

    h3 {
        font-weight: 800;
    }

    .role-switch {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 0.65rem;
        margin-bottom: 1.15rem;
    }

    .role-option input {
        display: none;
    }

    .role-option label {
        display: flex;
        align-items: center;
        justify-content: center;
        min-height: 48px;
        padding: 0.75rem 0.85rem;
        border: 1px solid var(--login-border);
        border-radius: 0.5rem;
        background: #f8fafc;
        cursor: pointer;
        text-align: center;
        font-weight: 700;
        color: #475569;
        transition: all 0.2s ease;
    }

    .role-option label:hover {
        border-color: #93c5fd;
        color: var(--login-primary);
    }

    .role-option input:checked + label {
        border-color: var(--login-primary);
        background: #eff6ff;
        color: var(--login-primary);
        box-shadow: 0 0 0 0.2rem rgba(37, 99, 235, 0.1);
    }

    .form-label {
        color: #334155;
        font-weight: 700;
    }

    .form-control {
        border-color: #d8e0eb;
        border-radius: 0.5rem;
        min-height: 44px;
    }

    .form-control:focus {
        border-color: #93c5fd;
        box-shadow: 0 0 0 0.2rem rgba(37, 99, 235, 0.14);
    }

    .btn {
        border-radius: 0.5rem;
        font-weight: 750;
        min-height: 44px;
    }

    .btn-primary {
        background-color: var(--login-primary);
        border-color: var(--login-primary);
        box-shadow: 0 12px 22px rgba(37, 99, 235, 0.22);
    }

    .btn-primary:hover,
    .btn-primary:focus {
        background-color: var(--login-primary-dark);
        border-color: var(--login-primary-dark);
    }

    .text-muted {
        color: var(--login-muted) !important;
    }

    .alert {
        border-radius: 0.5rem;
    }

    @media (max-width: 420px) {
        .role-switch {
            grid-template-columns: 1fr;
        }
    }
</style>

</head>
<body>

<div class="card login-card shadow-lg">
    <div class="card-body p-4">

        <div class="text-center mb-4">
            <div class="brand-mark">
                <i class="bi bi-building"></i>
            </div>
            <h3 class="mb-1"><%= Branding.COMPANY_NAME %></h3>
            <div class="text-muted">Employee Payroll Portal</div>
        </div>

        <form action="login" method="post">
            <div class="role-switch">
                <div class="role-option">
                    <input type="radio" id="adminRole" name="loginRole" value="ADMIN"
                           <%= "ADMIN".equalsIgnoreCase(selectedRole) ? "checked" : "" %>>
                    <label for="adminRole">Admin Login</label>
                </div>
                <div class="role-option">
                    <input type="radio" id="employeeRole" name="loginRole" value="EMPLOYEE"
                           <%= "EMPLOYEE".equalsIgnoreCase(selectedRole) ? "checked" : "" %>>
                    <label for="employeeRole">Employee Login</label>
                </div>
            </div>

            <div class="mb-3">
                <label class="form-label">Username</label>
                <input type="text" name="username" class="form-control" value="<%= enteredUsername %>" required>
            </div>

            <div class="mb-3">
                <label class="form-label">Password</label>
                <input type="password" name="password" class="form-control" required>
            </div>

            <div class="d-grid">
                <button type="submit" class="btn btn-primary" id="loginButton">
                    Login as Admin
                </button>
            </div>

        </form>

        <% if(error != null){ %>
            <div class="alert alert-danger mt-3 text-center">
                <%= error %>
            </div>
        <% } %>

    </div>
</div>

<script>
    (function () {
        const adminRadio = document.getElementById('adminRole');
        const employeeRadio = document.getElementById('employeeRole');
        const loginButton = document.getElementById('loginButton');

        function syncButtonLabel() {
            loginButton.textContent = employeeRadio.checked ? 'Login as Employee' : 'Login as Admin';
        }

        adminRadio.addEventListener('change', syncButtonLabel);
        employeeRadio.addEventListener('change', syncButtonLabel);
        syncButtonLabel();
    })();
</script>

</body>
</html>
