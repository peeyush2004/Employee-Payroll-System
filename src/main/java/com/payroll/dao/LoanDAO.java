package com.payroll.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com.payroll.model.LoanPayrollDeduction;
import com.payroll.model.LoanRequest;
import com.payroll.model.LoanTransaction;
import com.payroll.util.DBConnection;

public class LoanDAO {

    public boolean createLoanRequest(LoanRequest loanRequest, Integer createdByUserId) {
        String sql = """
            INSERT INTO loan_requests (
                emp_id, loan_type, loan_amount, reason, duration_months,
                interest_rate, monthly_emi, total_repayment_amount,
                outstanding_balance, status
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            con.setAutoCommit(false);

            ps.setInt(1, loanRequest.getEmpId());
            ps.setString(2, loanRequest.getLoanType());
            ps.setDouble(3, loanRequest.getLoanAmount());
            ps.setString(4, loanRequest.getReason());
            ps.setInt(5, loanRequest.getDurationMonths());
            ps.setDouble(6, loanRequest.getInterestRate());
            ps.setDouble(7, loanRequest.getMonthlyEmi());
            ps.setDouble(8, loanRequest.getTotalRepaymentAmount());
            ps.setDouble(9, loanRequest.getOutstandingBalance());
            ps.setString(10, loanRequest.getStatus());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        loanRequest.setLoanId(rs.getInt(1));
                    }
                }
                insertAuditTransaction(con, loanRequest.getLoanId(), "REQUESTED", 0, 0,
                        loanRequest.getOutstandingBalance(), null, loanRequest.getReason(), createdByUserId);
                con.commit();
            }
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<LoanRequest> getLoanRequestsByEmployee(int empId) {
        List<LoanRequest> loans = new ArrayList<>();
        String sql = """
            SELECT lr.*,
                   CONCAT(e.first_name, ' ', e.last_name) AS employee_name,
                   d.dept_name,
                   (s.basic_salary + s.hra + s.da + s.allowances) AS gross_salary
            FROM loan_requests lr
            JOIN employees e ON lr.emp_id = e.emp_id
            JOIN departments d ON e.dept_id = d.dept_id
            JOIN salary_structure s ON s.emp_id = e.emp_id
            WHERE lr.emp_id = ?
            ORDER BY lr.requested_at DESC
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, empId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapLoanRequest(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    public List<LoanRequest> getPendingLoanRequests() {
        List<LoanRequest> loans = new ArrayList<>();
        String sql = """
            SELECT lr.*,
                   CONCAT(e.first_name, ' ', e.last_name) AS employee_name,
                   d.dept_name,
                   (s.basic_salary + s.hra + s.da + s.allowances) AS gross_salary
            FROM loan_requests lr
            JOIN employees e ON lr.emp_id = e.emp_id
            JOIN departments d ON e.dept_id = d.dept_id
            JOIN salary_structure s ON s.emp_id = e.emp_id
            WHERE lr.status = 'PENDING'
            ORDER BY lr.requested_at ASC
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                loans.add(mapLoanRequest(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    public List<LoanRequest> getActiveLoans() {
        List<LoanRequest> loans = new ArrayList<>();
        String sql = """
            SELECT lr.*,
                   CONCAT(e.first_name, ' ', e.last_name) AS employee_name,
                   d.dept_name,
                   (s.basic_salary + s.hra + s.da + s.allowances) AS gross_salary
            FROM loan_requests lr
            JOIN employees e ON lr.emp_id = e.emp_id
            JOIN departments d ON e.dept_id = d.dept_id
            JOIN salary_structure s ON s.emp_id = e.emp_id
            WHERE lr.status = 'ACTIVE'
            ORDER BY lr.emi_start_month ASC, lr.loan_id DESC
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                loans.add(mapLoanRequest(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    public LoanRequest getLoanById(int loanId) {
        String sql = """
            SELECT lr.*,
                   CONCAT(e.first_name, ' ', e.last_name) AS employee_name,
                   d.dept_name,
                   (s.basic_salary + s.hra + s.da + s.allowances) AS gross_salary
            FROM loan_requests lr
            JOIN employees e ON lr.emp_id = e.emp_id
            JOIN departments d ON e.dept_id = d.dept_id
            JOIN salary_structure s ON s.emp_id = e.emp_id
            WHERE lr.loan_id = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, loanId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapLoanRequest(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean approveLoan(int loanId, double interestRate, YearMonth emiStartMonth, String remark, int adminUserId) {
        String sql = """
            UPDATE loan_requests
            SET status = 'ACTIVE',
                interest_rate = ?,
                emi_start_month = ?,
                admin_remark = ?,
                reviewed_at = CURRENT_TIMESTAMP,
                reviewed_by = ?
            WHERE loan_id = ? AND status = 'PENDING'
        """;

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            LoanRequest loan = getLoanByIdForUpdate(con, loanId);
            if (loan == null || !"PENDING".equalsIgnoreCase(loan.getStatus())) {
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setDouble(1, interestRate);
                ps.setString(2, emiStartMonth.toString());
                ps.setString(3, remark);
                ps.setInt(4, adminUserId);
                ps.setInt(5, loanId);

                int updated = ps.executeUpdate();
                if (updated > 0) {
                    insertAuditTransaction(con, loanId, "APPROVED", 0, loan.getOutstandingBalance(),
                            loan.getOutstandingBalance(), emiStartMonth, remark, adminUserId);
                    con.commit();
                    return true;
                }
            }

            con.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean rejectLoan(int loanId, String remark, int adminUserId) {
        String sql = """
            UPDATE loan_requests
            SET status = 'REJECTED',
                admin_remark = ?,
                reviewed_at = CURRENT_TIMESTAMP,
                reviewed_by = ?
            WHERE loan_id = ? AND status = 'PENDING'
        """;

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            LoanRequest loan = getLoanByIdForUpdate(con, loanId);
            if (loan == null || !"PENDING".equalsIgnoreCase(loan.getStatus())) {
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, remark);
                ps.setInt(2, adminUserId);
                ps.setInt(3, loanId);

                int updated = ps.executeUpdate();
                if (updated > 0) {
                    insertAuditTransaction(con, loanId, "REJECTED", 0, loan.getOutstandingBalance(),
                            loan.getOutstandingBalance(), null, remark, adminUserId);
                    con.commit();
                    return true;
                }
            }

            con.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean pauseLoanForMonth(int loanId, YearMonth payrollMonth, String remark, int adminUserId) {
        String sql = """
            INSERT INTO loan_pause_schedule (loan_id, pause_month, status)
            VALUES (?, ?, 'PAUSED')
        """;

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            LoanRequest loan = getLoanByIdForUpdate(con, loanId);
            if (loan == null || !"ACTIVE".equalsIgnoreCase(loan.getStatus())) {
                return false;
            }
            if (loan.getEmiStartMonth() != null && payrollMonth.isBefore(loan.getEmiStartMonth())) {
                return false;
            }

            if (isPauseScheduled(con, loanId, payrollMonth)) {
                return true;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, loanId);
                ps.setDate(2, Date.valueOf(payrollMonth.atDay(1)));
                ps.executeUpdate();
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<YearMonth> getPausedMonths(int loanId) {
        List<YearMonth> pausedMonths = new ArrayList<>();
        String sql = """
            SELECT pause_month
            FROM loan_pause_schedule
            WHERE loan_id = ? AND status = 'PAUSED'
            ORDER BY pause_month
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, loanId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date pauseDate = rs.getDate("pause_month");
                    if (pauseDate != null) {
                        pausedMonths.add(YearMonth.from(pauseDate.toLocalDate()));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pausedMonths;
    }

    public boolean closeLoan(int loanId, String remark, int adminUserId) {
        String sql = """
            UPDATE loan_requests
            SET status = 'COMPLETED',
                outstanding_balance = 0,
                closed_month = ?,
                admin_remark = ?,
                reviewed_at = CURRENT_TIMESTAMP,
                reviewed_by = ?
            WHERE loan_id = ? AND status IN ('ACTIVE', 'PENDING')
        """;

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            LoanRequest loan = getLoanByIdForUpdate(con, loanId);
            if (loan == null || "COMPLETED".equalsIgnoreCase(loan.getStatus())) {
                return false;
            }

            YearMonth closedMonth = YearMonth.now();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, closedMonth.toString());
                ps.setString(2, remark);
                ps.setInt(3, adminUserId);
                ps.setInt(4, loanId);

                int updated = ps.executeUpdate();
                if (updated > 0) {
                    insertAuditTransaction(con, loanId, "CLOSED", 0, loan.getOutstandingBalance(),
                            0, closedMonth, remark, adminUserId);
                    con.commit();
                    return true;
                }
            }
            con.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<LoanTransaction> getTransactionsByLoanId(int loanId) {
        List<LoanTransaction> transactions = new ArrayList<>();
        String sql = """
            SELECT lt.*, lr.loan_type
            FROM loan_transactions lt
            JOIN loan_requests lr ON lr.loan_id = lt.loan_id
            WHERE lt.loan_id = ?
            ORDER BY lt.created_at DESC, lt.transaction_id DESC
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, loanId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapLoanTransaction(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public List<LoanTransaction> getTransactionsByEmployee(int empId) {
        List<LoanTransaction> transactions = new ArrayList<>();
        String sql = """
            SELECT lt.*, lr.loan_type
            FROM loan_transactions lt
            JOIN loan_requests lr ON lr.loan_id = lt.loan_id
            WHERE lr.emp_id = ?
            ORDER BY lt.created_at DESC, lt.transaction_id DESC
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, empId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapLoanTransaction(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public List<LoanPayrollDeduction> processPayrollDeductions(int empId, YearMonth payrollMonth, boolean persistChanges) {
        List<LoanPayrollDeduction> deductions = new ArrayList<>();
        String activeLoansSql = """
            SELECT *
            FROM loan_requests
            WHERE emp_id = ?
              AND status = 'ACTIVE'
              AND emi_start_month IS NOT NULL
              AND emi_start_month <= ?
            ORDER BY loan_id
        """;

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(activeLoansSql)) {
                ps.setInt(1, empId);
                ps.setString(2, payrollMonth.toString());

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        LoanRequest loan = mapLoanRequest(rs);
                        if (isPauseScheduled(con, loan.getLoanId(), payrollMonth)) {
                            if (persistChanges && !hasTransactionTypeForMonth(con, loan.getLoanId(), "EMI_PAUSED", payrollMonth)) {
                                insertAuditTransaction(con, loan.getLoanId(), "EMI_PAUSED", 0,
                                        loan.getOutstandingBalance(), loan.getOutstandingBalance(),
                                        payrollMonth, "Scheduled pause for " + payrollMonth, null);
                            }
                            deductions.add(new LoanPayrollDeduction(
                                    loan.getLoanId(),
                                    loan.getLoanType(),
                                    0,
                                    loan.getOutstandingBalance(),
                                    payrollMonth,
                                    "PAUSED"));
                            continue;
                        }

                        if (hasTransactionTypeForMonth(con, loan.getLoanId(), "EMI_DEDUCTION", payrollMonth)) {
                            deductions.add(fetchExistingDeduction(con, loan.getLoanId(), payrollMonth, loan.getLoanType()));
                            continue;
                        }

                        double balanceBefore = loan.getOutstandingBalance();
                        double deductionAmount = Math.min(loan.getMonthlyEmi(), balanceBefore);
                        double balanceAfter = Math.max(0, round(balanceBefore - deductionAmount));

                        if (persistChanges) {
                            insertAuditTransaction(con, loan.getLoanId(), "EMI_DEDUCTION", deductionAmount,
                                    balanceBefore, balanceAfter, payrollMonth,
                                    "Auto deduction for " + payrollMonth, null);

                            updateLoanBalance(con, loan.getLoanId(), balanceAfter, balanceAfter == 0 ? payrollMonth : null);
                        }
                        deductions.add(new LoanPayrollDeduction(
                                loan.getLoanId(),
                                loan.getLoanType(),
                                deductionAmount,
                                balanceAfter,
                                payrollMonth,
                                "PAID"));
                    }
                }
            }

            if (persistChanges) {
                con.commit();
            } else {
                con.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return deductions;
    }

    public int getPendingLoanCount() {
        return getLoanCountByStatus("PENDING");
    }

    public int getActiveLoanCount() {
        return getLoanCountByStatus("ACTIVE");
    }

    public int getEmployeeActiveLoanCount(int empId) {
        String sql = "SELECT COUNT(*) FROM loan_requests WHERE emp_id = ? AND status = 'ACTIVE'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, empId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getLoanCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM loan_requests WHERE status = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private LoanPayrollDeduction fetchExistingDeduction(Connection con, int loanId, YearMonth payrollMonth, String loanType)
            throws SQLException {
        String sql = """
            SELECT amount, balance_after
            FROM loan_transactions
            WHERE loan_id = ? AND transaction_type = 'EMI_DEDUCTION' AND payroll_month = ?
            ORDER BY transaction_id DESC
            LIMIT 1
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, loanId);
            ps.setString(2, payrollMonth.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new LoanPayrollDeduction(
                            loanId,
                            loanType,
                            rs.getDouble("amount"),
                            rs.getDouble("balance_after"),
                            payrollMonth,
                            "PAID");
                }
            }
        }
        return new LoanPayrollDeduction(loanId, loanType, 0, 0, payrollMonth, "PAID");
    }

    private void updateLoanBalance(Connection con, int loanId, double balanceAfter, YearMonth closedMonth) throws SQLException {
        String sql = """
            UPDATE loan_requests
            SET outstanding_balance = ?,
                paid_amount = total_repayment_amount - ?,
                status = CASE WHEN ? = 0 THEN 'COMPLETED' ELSE status END,
                closed_month = CASE WHEN ? = 0 THEN ? ELSE closed_month END
            WHERE loan_id = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, balanceAfter);
            ps.setDouble(2, balanceAfter);
            ps.setDouble(3, balanceAfter);
            ps.setDouble(4, balanceAfter);
            ps.setString(5, closedMonth != null ? closedMonth.toString() : null);
            ps.setInt(6, loanId);
            ps.executeUpdate();
        }
    }

    private boolean hasTransactionTypeForMonth(Connection con, int loanId, String transactionType, YearMonth payrollMonth)
            throws SQLException {
        String sql = """
            SELECT COUNT(*)
            FROM loan_transactions
            WHERE loan_id = ? AND transaction_type = ? AND payroll_month = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, loanId);
            ps.setString(2, transactionType);
            ps.setString(3, payrollMonth.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private boolean isPauseScheduled(Connection con, int loanId, YearMonth payrollMonth) throws SQLException {
        String sql = """
            SELECT COUNT(*)
            FROM loan_pause_schedule
            WHERE loan_id = ? AND status = 'PAUSED' AND pause_month = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, loanId);
            ps.setDate(2, Date.valueOf(payrollMonth.atDay(1)));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private void insertAuditTransaction(Connection con, int loanId, String transactionType, double amount,
                                        double balanceBefore, double balanceAfter, YearMonth payrollMonth,
                                        String note, Integer createdBy) throws SQLException {
        String sql = """
            INSERT INTO loan_transactions (
                loan_id, transaction_type, amount, balance_before, balance_after,
                payroll_month, note, created_by
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, loanId);
            ps.setString(2, transactionType);
            ps.setDouble(3, amount);
            ps.setDouble(4, balanceBefore);
            ps.setDouble(5, balanceAfter);
            if (payrollMonth != null) {
                ps.setString(6, payrollMonth.toString());
            } else {
                ps.setNull(6, java.sql.Types.VARCHAR);
            }
            ps.setString(7, note);
            if (createdBy != null) {
                ps.setInt(8, createdBy);
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }
            ps.executeUpdate();
        }
    }

    private LoanRequest getLoanByIdForUpdate(Connection con, int loanId) throws SQLException {
        String sql = "SELECT * FROM loan_requests WHERE loan_id = ? FOR UPDATE";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, loanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapLoanRequest(rs);
                }
            }
        }
        return null;
    }

    private LoanRequest mapLoanRequest(ResultSet rs) throws SQLException {
        LoanRequest loan = new LoanRequest();
        loan.setLoanId(rs.getInt("loan_id"));
        loan.setEmpId(rs.getInt("emp_id"));
        loan.setLoanType(rs.getString("loan_type"));
        loan.setLoanAmount(rs.getDouble("loan_amount"));
        loan.setReason(rs.getString("reason"));
        loan.setDurationMonths(rs.getInt("duration_months"));
        loan.setInterestRate(rs.getDouble("interest_rate"));
        loan.setMonthlyEmi(rs.getDouble("monthly_emi"));
        loan.setTotalRepaymentAmount(rs.getDouble("total_repayment_amount"));
        loan.setOutstandingBalance(rs.getDouble("outstanding_balance"));
        loan.setPaidAmount(rs.getDouble("paid_amount"));
        loan.setStatus(rs.getString("status"));
        loan.setAdminRemark(rs.getString("admin_remark"));
        loan.setRequestedAt(rs.getTimestamp("requested_at"));
        loan.setReviewedAt(rs.getTimestamp("reviewed_at"));

        int reviewedBy = rs.getInt("reviewed_by");
        loan.setReviewedBy(rs.wasNull() ? null : reviewedBy);

        String emiStartMonth = rs.getString("emi_start_month");
        if (emiStartMonth != null && !emiStartMonth.isBlank()) {
            loan.setEmiStartMonth(YearMonth.parse(emiStartMonth));
        }

        String closedMonth = rs.getString("closed_month");
        if (closedMonth != null && !closedMonth.isBlank()) {
            loan.setClosedMonth(YearMonth.parse(closedMonth));
        }

        trySetIfPresent(rs, "employee_name", loan::setEmployeeName);
        trySetIfPresent(rs, "dept_name", loan::setDepartmentName);
        trySetIfPresentDouble(rs, "gross_salary", loan::setEmployeeGrossSalary);
        return loan;
    }

    private LoanTransaction mapLoanTransaction(ResultSet rs) throws SQLException {
        LoanTransaction transaction = new LoanTransaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setLoanId(rs.getInt("loan_id"));
        transaction.setLoanType(rs.getString("loan_type"));
        transaction.setTransactionType(rs.getString("transaction_type"));
        transaction.setAmount(rs.getDouble("amount"));
        transaction.setBalanceBefore(rs.getDouble("balance_before"));
        transaction.setBalanceAfter(rs.getDouble("balance_after"));

        String payrollMonth = rs.getString("payroll_month");
        if (payrollMonth != null && !payrollMonth.isBlank()) {
            transaction.setPayrollMonth(YearMonth.parse(payrollMonth));
        }

        transaction.setNote(rs.getString("note"));
        transaction.setCreatedAt(rs.getTimestamp("created_at"));
        int createdBy = rs.getInt("created_by");
        transaction.setCreatedBy(rs.wasNull() ? null : createdBy);
        return transaction;
    }

    private void trySetIfPresent(ResultSet rs, String column, java.util.function.Consumer<String> consumer) {
        try {
            consumer.accept(rs.getString(column));
        } catch (SQLException ignored) {
        }
    }

    private void trySetIfPresentDouble(ResultSet rs, String column, java.util.function.DoubleConsumer consumer) {
        try {
            consumer.accept(rs.getDouble(column));
        } catch (SQLException ignored) {
        }
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
