# Loan Module Manual DB Setup

Run the SQL in [loan_module_sql.sql](/c:/Users/peeyu/eclipse-workspace/EmployeePayrollSystem/loan_module_sql.sql) manually in MySQL Workbench.

Use the same database/schema that already contains:

- `employees`
- `users`
- `departments`
- `salary_structure`
- `leave_requests`

Order:

1. Open MySQL Workbench and select your payroll database.
2. Open [loan_module_sql.sql](/c:/Users/peeyu/eclipse-workspace/EmployeePayrollSystem/loan_module_sql.sql).
3. Execute the statements from top to bottom.

Relationships added:

- `loan_requests.emp_id -> employees.emp_id`
- `loan_requests.reviewed_by -> users.id`
- `loan_transactions.loan_id -> loan_requests.loan_id`
- `loan_transactions.created_by -> users.id`

Notes:

- `loan_requests` stores the loan application, approval state, EMI setup, and current balance.
- `loan_transactions` doubles as repayment history and audit log.
- Duplicate EMI deduction for the same payroll month is prevented in application logic by checking `loan_id + payroll_month + transaction_type`.
