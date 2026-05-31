<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.payroll.util.Branding" %>
<%
    String currentUser = (String) session.getAttribute("username");
    String currentRole = (String) session.getAttribute("role");
    String companyName = Branding.COMPANY_NAME;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= companyName %> Payroll</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <style>
        :root {
            --app-bg: #eef2f7;
            --app-surface: #ffffff;
            --app-surface-soft: #f7f9fc;
            --app-text: #18212f;
            --app-muted: #657386;
            --app-border: #dfe6ef;
            --app-primary: #2563eb;
            --app-primary-dark: #1d4ed8;
            --app-success: #15803d;
            --app-warning: #b7791f;
            --app-danger: #dc2626;
            --app-sidebar: #101828;
            --app-sidebar-2: #172033;
            --app-shadow: 0 14px 36px rgba(15, 23, 42, 0.08);
        }

        * { letter-spacing: 0; }

        body {
            min-height: 100vh;
            background:
                radial-gradient(circle at top left, rgba(37, 99, 235, 0.12), transparent 30rem),
                linear-gradient(180deg, #f8fafc 0%, var(--app-bg) 100%);
            color: var(--app-text);
            font-size: 0.95rem;
        }

        .sidebar {
            min-height: 100vh;
            background: linear-gradient(180deg, var(--app-sidebar) 0%, var(--app-sidebar-2) 100%);
            width: 260px;
            position: fixed;
            top: 0;
            left: 0;
            z-index: 100;
            padding-top: 0;
            box-shadow: 10px 0 30px rgba(15, 23, 42, 0.18);
        }

        .sidebar-brand {
            padding: 1.25rem 1.35rem;
            border-bottom: 1px solid rgba(255, 255, 255, 0.08);
        }

        .sidebar-brand h5 {
            color: #fff;
            margin: 0;
            font-weight: 700;
            font-size: 1rem;
            line-height: 1.3;
        }

        .sidebar-brand i {
            width: 2.1rem;
            height: 2.1rem;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            border-radius: 0.5rem;
            background: rgba(37, 99, 235, 0.18);
            color: #93c5fd;
        }

        .sidebar nav {
            padding: 0.65rem 0.65rem 1rem;
        }

        .sidebar .nav-link {
            color: #b9c2d0;
            padding: 0.72rem 0.85rem;
            margin: 0.12rem 0;
            font-size: 0.9rem;
            display: flex;
            align-items: center;
            gap: 0.65rem;
            border-radius: 0.5rem;
            border-left: 0;
            transition: color 0.18s ease, background-color 0.18s ease, transform 0.18s ease;
        }

        .sidebar .nav-link:hover,
        .sidebar .nav-link.active {
            color: #fff;
            background: rgba(37, 99, 235, 0.18);
        }

        .sidebar .nav-link:hover {
            transform: translateX(2px);
        }

        .sidebar .nav-link.active {
            box-shadow: inset 3px 0 0 #60a5fa;
        }

        .sidebar .nav-link.logout-link {
            color: #fecaca;
        }

        .sidebar .nav-link.logout-link:hover,
        .sidebar .nav-link.logout-link:focus {
            color: #fff;
            background: rgba(220, 38, 38, 0.22);
            box-shadow: inset 3px 0 0 #ef4444;
        }

        .sidebar .nav-link i {
            font-size: 1rem;
            width: 1.25rem;
            text-align: center;
        }

        .sidebar-divider {
            border-top: 1px solid rgba(255, 255, 255, 0.08);
            margin: 0.65rem 0.2rem;
        }

        .sidebar-section-label {
            color: #7b8798;
            font-size: 0.68rem;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0;
            padding: 0.75rem 0.85rem 0.25rem;
        }

        .main-content {
            margin-left: 260px;
            min-height: 100vh;
            padding: 0;
        }

        .topbar {
            background: rgba(255, 255, 255, 0.88);
            border-bottom: 1px solid rgba(223, 230, 239, 0.9);
            padding: 0.85rem 1.5rem;
            display: flex;
            align-items: center;
            justify-content: flex-end;
            position: sticky;
            top: 0;
            z-index: 99;
            backdrop-filter: blur(14px);
        }

        .topbar .user-info {
            display: flex;
            align-items: center;
            gap: 0.65rem;
            color: #475569;
            font-size: 0.9rem;
        }

        .topbar .logout-btn {
            color: var(--app-danger);
            border-color: #fecaca;
            background-color: #fff;
        }

        .topbar .logout-btn:hover,
        .topbar .logout-btn:focus {
            color: #fff;
            background-color: var(--app-danger);
            border-color: var(--app-danger);
        }

        .topbar .user-avatar {
            width: 38px;
            height: 38px;
            border-radius: 50%;
            background: linear-gradient(135deg, var(--app-primary), #0891b2);
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
            font-size: 0.9rem;
            box-shadow: 0 8px 18px rgba(37, 99, 235, 0.22);
        }

        .page-content {
            padding: 1.65rem;
        }

        .page-header {
            margin-bottom: 1.35rem;
            padding: 1rem 0 1.25rem;
            border-bottom: 1px solid var(--app-border);
        }

        .page-header h4 {
            color: var(--app-text);
            font-weight: 750;
        }

        .text-muted {
            color: var(--app-muted) !important;
        }

        .card {
            border: 1px solid rgba(223, 230, 239, 0.95);
            border-radius: 0.5rem;
            box-shadow: var(--app-shadow);
            overflow: hidden;
        }

        .card-header {
            background: linear-gradient(180deg, #ffffff 0%, var(--app-surface-soft) 100%);
            border-bottom: 1px solid var(--app-border);
            font-weight: 700;
            color: #233044;
            padding: 0.9rem 1rem;
        }

        .card-footer {
            background-color: var(--app-surface-soft);
            border-top: 1px solid var(--app-border);
        }

        .stat-card {
            border-left: 0;
            position: relative;
        }

        .stat-card::before {
            content: "";
            position: absolute;
            inset: 0 auto 0 0;
            width: 4px;
            background: var(--app-primary);
        }

        .stat-card.success::before { background: var(--app-success); }
        .stat-card.warning::before { background: #f59e0b; }
        .stat-card.danger::before  { background: var(--app-danger); }

        .stat-card .stat-value {
            font-size: 1.75rem;
            font-weight: 800;
            line-height: 1.1;
            color: #111827;
        }

        .stat-card .stat-label {
            font-size: 0.74rem;
            color: var(--app-muted);
            text-transform: uppercase;
            font-weight: 700;
            letter-spacing: 0;
        }

        .stat-card .stat-icon {
            font-size: 2.2rem;
            opacity: 0.18;
        }

        .btn {
            border-radius: 0.45rem;
            font-weight: 650;
        }

        .btn-primary {
            background-color: var(--app-primary);
            border-color: var(--app-primary);
            box-shadow: 0 8px 18px rgba(37, 99, 235, 0.18);
        }

        .btn-primary:hover,
        .btn-primary:focus {
            background-color: var(--app-primary-dark);
            border-color: var(--app-primary-dark);
        }

        .form-control,
        .form-select {
            border-color: #d8e0eb;
            border-radius: 0.45rem;
        }

        .form-control:focus,
        .form-select:focus {
            border-color: #93c5fd;
            box-shadow: 0 0 0 0.2rem rgba(37, 99, 235, 0.14);
        }

        .table {
            color: #243244;
        }

        .table > :not(caption) > * > * {
            padding: 0.85rem 0.8rem;
            border-bottom-color: #edf1f6;
        }

        .table thead th {
            background-color: #f1f5f9 !important;
            color: #475569 !important;
            border-bottom: 1px solid var(--app-border);
            font-size: 0.76rem;
            font-weight: 800;
            text-transform: uppercase;
            letter-spacing: 0;
            white-space: nowrap;
        }

        .table-hover tbody tr:hover {
            background-color: #f8fbff;
        }

        .badge {
            border-radius: 999px;
            font-weight: 700;
            padding: 0.42em 0.65em;
        }

        .alert {
            border-radius: 0.5rem;
            border-width: 1px;
        }

        footer {
            background: rgba(255, 255, 255, 0.58);
        }

        @media (max-width: 992px) {
            .sidebar {
                width: 100%;
                min-height: auto;
                position: relative;
                box-shadow: none;
            }

            .sidebar nav {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
                gap: 0.15rem;
            }

            .sidebar-divider,
            .sidebar-section-label {
                display: none;
            }

            .main-content {
                margin-left: 0;
            }

            .topbar {
                position: relative;
                justify-content: flex-start;
            }

            .page-content {
                padding: 1rem;
            }

            .page-header {
                align-items: flex-start !important;
                gap: 0.75rem;
                flex-direction: column;
            }
        }

        @media (max-width: 576px) {
            .topbar .user-info {
                width: 100%;
                flex-wrap: wrap;
            }

            .topbar .logout-btn {
                margin-left: 0 !important;
            }

            .card-body {
                padding: 1rem;
            }

            .btn {
                white-space: normal;
            }
        }
    </style>
</head>
<body>

<!-- SIDEBAR -->
<div class="sidebar d-flex flex-column">
    <div class="sidebar-brand">
        <h5><i class="bi bi-building me-2"></i><%= companyName %></h5>
    </div>

    <nav class="flex-column mt-2">
        <div class="sidebar-section-label">Main</div>
        <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">
            <i class="bi bi-speedometer2"></i> Dashboard
        </a>

        <% if ("ADMIN".equals(currentRole)) { %>
        <div class="sidebar-divider"></div>
        <div class="sidebar-section-label">Employees</div>
        <a href="${pageContext.request.contextPath}/viewEmployees" class="nav-link">
            <i class="bi bi-people"></i> View Employees
        </a>
        <a href="${pageContext.request.contextPath}/addEmployee" class="nav-link">
            <i class="bi bi-person-plus"></i> Add Employee
        </a>

        <div class="sidebar-divider"></div>
        <div class="sidebar-section-label">Leave</div>
        <a href="${pageContext.request.contextPath}/viewLeaveRequests" class="nav-link">
            <i class="bi bi-calendar-check"></i> Manage Leaves
        </a>

        <div class="sidebar-divider"></div>
        <div class="sidebar-section-label">Loans</div>
        <a href="${pageContext.request.contextPath}/manageLoans" class="nav-link">
            <i class="bi bi-cash-coin"></i> Manage Loans
        </a>
        <% } else { %>
        <div class="sidebar-divider"></div>
        <div class="sidebar-section-label">Leave</div>
        <a href="${pageContext.request.contextPath}/applyLeave" class="nav-link">
            <i class="bi bi-calendar-plus"></i> Apply Leave
        </a>
        <a href="${pageContext.request.contextPath}/myLeaveHistory" class="nav-link">
            <i class="bi bi-clock-history"></i> My Leave History
        </a>

        <div class="sidebar-divider"></div>
        <div class="sidebar-section-label">Loans</div>
        <a href="${pageContext.request.contextPath}/employeeLoans" class="nav-link">
            <i class="bi bi-wallet2"></i> My Loans
        </a>
        <% } %>

        <div class="sidebar-divider"></div>
        <a href="${pageContext.request.contextPath}/logout" class="nav-link logout-link">
            <i class="bi bi-box-arrow-left"></i> Logout
        </a>
    </nav>
</div>

<!-- MAIN CONTENT WRAPPER -->
<div class="main-content">
    <!-- TOPBAR -->
    <div class="topbar">
        <div class="user-info">
            <div class="user-avatar">
                <%= currentUser != null ? String.valueOf(currentUser.charAt(0)).toUpperCase() : "U" %>
            </div>
            <div>
                <div class="small text-muted"><%= companyName %></div>
                <span class="fw-semibold"><%= currentUser != null ? currentUser : "User" %></span>
                <span class="badge bg-secondary ms-1" style="font-size:0.65rem;">
                    <%= currentRole != null ? currentRole : "" %>
                </span>
            </div>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-outline-danger logout-btn ms-3">
                <i class="bi bi-box-arrow-left"></i> Logout
            </a>
        </div>
    </div>

    <!-- PAGE CONTENT -->
    <div class="page-content">
