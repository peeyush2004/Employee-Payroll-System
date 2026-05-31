-- Run these statements manually in MySQL Workbench against the same database
-- used by the Employee Payroll System. Execute them in the order shown.

CREATE TABLE loan_requests (
    loan_id INT PRIMARY KEY AUTO_INCREMENT,
    emp_id INT NOT NULL,
    loan_type VARCHAR(50) NOT NULL,
    loan_amount DECIMAL(12,2) NOT NULL,
    reason VARCHAR(500) NOT NULL,
    duration_months INT NOT NULL,
    interest_rate DECIMAL(5,2) NOT NULL DEFAULT 5.00,
    monthly_emi DECIMAL(12,2) NOT NULL,
    total_repayment_amount DECIMAL(12,2) NOT NULL,
    outstanding_balance DECIMAL(12,2) NOT NULL,
    paid_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    admin_remark VARCHAR(500) NULL,
    emi_start_month VARCHAR(7) NULL,
    closed_month VARCHAR(7) NULL,
    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP NULL DEFAULT NULL,
    reviewed_by INT NULL,
    CONSTRAINT fk_loan_requests_employee
        FOREIGN KEY (emp_id) REFERENCES employees(emp_id),
    CONSTRAINT fk_loan_requests_reviewer
        FOREIGN KEY (reviewed_by) REFERENCES users(id),
    CONSTRAINT chk_loan_requests_type
        CHECK (loan_type IN ('Personal Loan', 'Emergency Loan', 'Medical Loan', 'Festival Advance', 'Housing Loan')),
    CONSTRAINT chk_loan_requests_status
        CHECK (status IN ('PENDING', 'ACTIVE', 'REJECTED', 'COMPLETED'))
);

CREATE TABLE loan_transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    loan_id INT NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    balance_before DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    balance_after DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    payroll_month VARCHAR(7) NULL,
    note VARCHAR(500) NULL,
    created_by INT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_loan_transactions_request
        FOREIGN KEY (loan_id) REFERENCES loan_requests(loan_id),
    CONSTRAINT fk_loan_transactions_user
        FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT chk_loan_transactions_type
        CHECK (transaction_type IN ('REQUESTED', 'APPROVED', 'REJECTED', 'EMI_DEDUCTION', 'EMI_PAUSED', 'CLOSED'))
);

CREATE INDEX idx_loan_requests_employee_status
    ON loan_requests (emp_id, status);

CREATE INDEX idx_loan_requests_status
    ON loan_requests (status);

CREATE INDEX idx_loan_transactions_loan_month_type
    ON loan_transactions (loan_id, payroll_month, transaction_type);
