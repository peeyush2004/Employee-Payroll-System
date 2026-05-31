# EmployeePayrollSystem

## Overview

**EmployeePayrollSystem** is a Java EE web application for managing employee records, payroll calculations, leave requests, and loan processing. It provides admin and employee workflows, integrates with a relational database via JDBC, and renders UI pages using JSP.

## Key Features

- Employee management
  - Add, edit, view, and remove employees
  - Store salary structure and profile data
- Payroll processing
  - Compute gross salary and net pay
  - Deduct approved leave and loan EMIs
  - Generate payslip reports
- Leave management
  - Submit leave requests
  - Approve or reject leave from admin
  - Calculate leave deductions for payroll
- Loan management
  - Employee loan application and tracking
  - Admin approval, rejection, pause, and close actions
  - Loan EMI scheduling and payroll deduction integration
- Authentication & role separation
  - Admin and employee roles
  - Role-based access to pages and actions

## Tech Stack

- Java SE / Java EE
- JSP / Servlets
- JDBC
- MySQL (or compatible relational database)
- Servlet container: Apache Tomcat, Jetty, etc.
- PDF generation libraries included in `WEB-INF/lib`

## Repository Structure

- `src/main/java/com/payroll/controller/`
  - Servlet controllers
- `src/main/java/com/payroll/dao/`
  - Data access layer
- `src/main/java/com/payroll/service/`
  - Business logic layer
- `src/main/java/com/payroll/model/`
  - Domain model classes
- `src/main/java/com/payroll/util/`
  - Utility and helper classes
- `src/main/webapp/`
  - JSP views, static resources, and deployment descriptors
- `src/main/webapp/WEB-INF/lib/`
  - Third-party jar dependencies

## Prerequisites

- Java 11+ JDK
- Apache Tomcat 9+ or compatible servlet container
- MySQL 8+ or equivalent RDBMS
- JDBC driver present in `WEB-INF/lib/mysql-connector-j-8.3.0.jar`

## Setup

### 1. Clone or copy project
Place the project in your workspace and open it in Eclipse or another Java IDE.

### 2. Configure database
Update database connection settings in:

- `src/main/java/com/payroll/util/DBConnection.java`

Set:
- `jdbcURL`
- `jdbcUsername`
- `jdbcPassword`

### 3. Initialize schema
Create the required tables and initial data. If available, use the provided SQL guidance files in the workspace.

Typical tables required:
- `users`
- `employees`
- `salary_structure`
- `leave_requests`
- `loan_requests`
- `loan_transactions`

### 4. Refresh and build
- Refresh the project in Eclipse
- Run: `Project > Clean...`
- Ensure `Build Automatically` is enabled

## Deployment

Deploy the project as a WAR file to your servlet container. Example URL:

- `http://localhost:8080/EmployeePayrollSystem/`

## Usage

- Open the app URL in a browser
- Log in with admin or employee credentials
- Admin users can manage employees, approve leaves, and manage loans
- Employees can view payslips, submit leave requests, and request loans

## Security Notes

- New employee passwords are hashed before storage
- Existing plain-text passwords remain compatible during login
- Future improvement: migrate to a centralized authentication service and add CSRF protection

## Recommended Improvements

- Convert to Maven/Gradle for dependency and build management
- Add automated tests for service and DAO layers
- Add API documentation and REST endpoints
- Implement working-day leave deduction support
- Add database migration tooling (Flyway / Liquibase)
- Introduce centralized logging and audit trails

## Contact & Maintenance

- Keep `DBConnection.java` updated with current DB credentials
- Do not commit generated output directories like `build/classes/`
- Use `Refresh` and `Clean` in Eclipse after manual source changes
