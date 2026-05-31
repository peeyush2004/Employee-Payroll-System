package com.payroll.dao;

import com.payroll.model.LeaveRequest;
import com.payroll.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LeaveRequestDAO {

    // =====================================
    // 🔹 ADD NEW LEAVE REQUEST
    // =====================================
    public boolean addLeaveRequest(LeaveRequest leaveRequest) {
        String sql = "INSERT INTO leave_requests (emp_id, leave_type, start_date, end_date, reason, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, leaveRequest.getEmpId());
            ps.setString(2, leaveRequest.getLeaveType());
            ps.setDate(3, Date.valueOf(leaveRequest.getStartDate()));
            ps.setDate(4, Date.valueOf(leaveRequest.getEndDate()));
            ps.setString(5, leaveRequest.getReason());
            ps.setString(6, leaveRequest.getStatus()); // Should be "Pending" by default from model

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // =====================================
    // 🔹 GET LEAVE REQUESTS BY EMPLOYEE ID
    // =====================================
    public List<LeaveRequest> getLeaveRequestsByEmpId(int empId) {
        List<LeaveRequest> leaveRequests = new ArrayList<>();
        String sql = "SELECT * FROM leave_requests WHERE emp_id = ? ORDER BY applied_date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, empId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                leaveRequests.add(mapResultSetToLeaveRequest(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return leaveRequests;
    }

    // =====================================
    // 🔹 GET ALL LEAVE REQUESTS (FOR ADMIN)
    // =====================================
    public List<LeaveRequest> getAllLeaveRequests() {
        List<LeaveRequest> leaveRequests = new ArrayList<>();
        String sql = "SELECT * FROM leave_requests ORDER BY applied_date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                leaveRequests.add(mapResultSetToLeaveRequest(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return leaveRequests;
    }

    // =====================================
    // 🔹 UPDATE LEAVE REQUEST STATUS (FOR ADMIN)
    // =====================================
    public boolean updateLeaveRequestStatus(int leaveId, String status) {
        String sql = "UPDATE leave_requests SET status = ? WHERE leave_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, leaveId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to map ResultSet to LeaveRequest object
    private LeaveRequest mapResultSetToLeaveRequest(ResultSet rs) throws SQLException {
        return new LeaveRequest(
                rs.getInt("leave_id"),
                rs.getInt("emp_id"),
                rs.getString("leave_type"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate(),
                rs.getString("reason"),
                rs.getString("status"),
                rs.getTimestamp("applied_date")
        );
    }
    
    // =====================================
    // 🔹 GET PENDING LEAVE REQUESTS COUNT
    // =====================================
    public int getPendingLeaveRequestCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM leave_requests WHERE status = 'Pending'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public int getLeaveRequestCountByStatusForCurrentMonth(String status) {
        int count = 0;
        String sql = """
            SELECT COUNT(*)
            FROM leave_requests
            WHERE status = ?
            AND YEAR(applied_date) = YEAR(CURDATE())
            AND MONTH(applied_date) = MONTH(CURDATE())
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    // =====================================
    // 🔹 GET APPROVED LEAVE DAYS FOR PAYSLIP CALCULATION
    // =====================================
    public int getApprovedLeaveDaysInMonth(int empId, java.time.YearMonth yearMonth) {
        int approvedDays = 0;
        String sql = """
            SELECT SUM(DATEDIFF(LEAST(end_date, ?) , GREATEST(start_date, ?)) + 1) AS leave_days
            FROM leave_requests
            WHERE emp_id = ?
            AND status = 'Approved'
            AND start_date <= ?
            AND end_date >= ?
        """;

        java.time.LocalDate monthStart = yearMonth.atDay(1);
        java.time.LocalDate monthEnd = yearMonth.atEndOfMonth();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(monthEnd));
            ps.setDate(2, Date.valueOf(monthStart));
            ps.setInt(3, empId);
            ps.setDate(4, Date.valueOf(monthEnd));
            ps.setDate(5, Date.valueOf(monthStart));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                approvedDays = rs.getInt("leave_days");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return approvedDays;
    }
}
