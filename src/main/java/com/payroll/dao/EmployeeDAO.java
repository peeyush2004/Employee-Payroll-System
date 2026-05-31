package com.payroll.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.payroll.model.Employee;
import com.payroll.util.DBConnection;

public class EmployeeDAO {

    // =====================================
    // 🔹 GET ALL EMPLOYEES (ADMIN VIEW)
    // =====================================
    public List<Employee> getAllEmployees() {

        List<Employee> list = new ArrayList<>();

        String sql = """
            SELECT e.emp_id, u.username,
                   e.first_name, e.last_name,
                   e.email, e.address, e.hire_date, e.photo,
                   d.dept_name,
                   s.basic_salary, s.hra, s.da,
                   s.allowances, s.deductions
            FROM employees e
            JOIN users u ON e.user_id = u.id
            JOIN departments d ON e.dept_id = d.dept_id
            JOIN salary_structure s ON e.emp_id = s.emp_id
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Employee emp = new Employee(	
                        rs.getInt("emp_id"),
                        rs.getString("username"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("dept_name"),
                        rs.getString("address"),
                        rs.getDouble("basic_salary"),
                        rs.getDouble("hra"),
                        rs.getDouble("da"),
                        rs.getDouble("allowances"),
                        rs.getDouble("deductions"),
                        rs.getString("photo")
                );
                emp.setHireDate(rs.getString("hire_date"));

                list.add(emp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =====================================
    // 🔹 SEARCH EMPLOYEES
    // =====================================
    public List<Employee> searchEmployees(String keyword) {

        List<Employee> list = new ArrayList<>();

        String sql = """
            SELECT e.emp_id, u.username,
                   e.first_name, e.last_name,
                   e.email, e.address, e.hire_date, e.photo,
                   d.dept_name,
                   s.basic_salary, s.hra, s.da,
                   s.allowances, s.deductions
            FROM employees e
            JOIN users u ON e.user_id = u.id
            JOIN departments d ON e.dept_id = d.dept_id
            JOIN salary_structure s ON e.emp_id = s.emp_id
            WHERE e.first_name LIKE ? OR e.last_name LIKE ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Employee emp = new Employee(
                        rs.getInt("emp_id"),
                        rs.getString("username"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("dept_name"),
                        rs.getString("address"),
                        rs.getDouble("basic_salary"),
                        rs.getDouble("hra"),
                        rs.getDouble("da"),
                        rs.getDouble("allowances"),
                        rs.getDouble("deductions"),
                        rs.getString("photo")
                );
                emp.setHireDate(rs.getString("hire_date"));

                list.add(emp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =====================================
    // 🔹 GET EMPLOYEE BY EMP_ID
    // =====================================
    public Employee getEmployeeById(int id) {

        Employee emp = null;

        String sql = """
            SELECT e.emp_id, u.username,
                   e.first_name, e.last_name,
                   e.email, e.address, e.hire_date, e.photo,
                   d.dept_name,
                   s.basic_salary, s.hra, s.da,
                   s.allowances, s.deductions
            FROM employees e
            JOIN users u ON e.user_id = u.id
            JOIN departments d ON e.dept_id = d.dept_id
            JOIN salary_structure s ON e.emp_id = s.emp_id
            WHERE e.emp_id = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                emp = new Employee(
                        rs.getInt("emp_id"),
                        rs.getString("username"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("dept_name"),
                        rs.getString("address"),
                        rs.getDouble("basic_salary"),
                        rs.getDouble("hra"),
                        rs.getDouble("da"),
                        rs.getDouble("allowances"),
                        rs.getDouble("deductions"),
                        rs.getString("photo")
                );
                emp.setHireDate(rs.getString("hire_date"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return emp;
    }

    // =====================================
    // 🔹 GET EMPLOYEE BY USER_ID (EMP DASHBOARD)
    // =====================================
    public Employee getEmployeeByUserId(int userId) {

        Employee emp = null;

        String sql = """
            SELECT e.emp_id, u.username,
                   e.first_name, e.last_name,
                   e.email, e.address, e.hire_date, e.photo,
                   d.dept_name,
                   s.basic_salary, s.hra, s.da,
                   s.allowances, s.deductions
            FROM employees e
            JOIN users u ON e.user_id = u.id
            JOIN departments d ON e.dept_id = d.dept_id
            JOIN salary_structure s ON e.emp_id = s.emp_id
            WHERE e.user_id = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                emp = new Employee(
                        rs.getInt("emp_id"),
                        rs.getString("username"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("dept_name"),
                        rs.getString("address"),
                        rs.getDouble("basic_salary"),
                        rs.getDouble("hra"),
                        rs.getDouble("da"),
                        rs.getDouble("allowances"),
                        rs.getDouble("deductions"),
                        rs.getString("photo")
                );
                emp.setHireDate(rs.getString("hire_date"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return emp;
    }

    // =====================================
    // 🔹 DASHBOARD STATS (ADMIN)
    // =====================================
    public int getEmployeeCount() {

        int count = 0;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM employees");
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) count = rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    public double getTotalSalary() {

        double total = 0;

        String sql = """
            SELECT SUM(basic_salary + hra + da + allowances - deductions)
            FROM salary_structure
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) total = rs.getDouble(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return total;
    }

    public double getAverageSalary() {

        double avg = 0;

        String sql = """
            SELECT AVG(basic_salary + hra + da + allowances - deductions)
            FROM salary_structure
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) avg = rs.getDouble(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return avg;
    }

    // =====================================
    // 🔹 GET TOTAL EMPLOYEES COUNT
    // =====================================
    public int getTotalEmployeesCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM employees";
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

    // =====================================
    // 🔹 GET EMPLOYEES COUNT BY DEPARTMENT
    // =====================================
    public java.util.Map<String, Integer> getEmployeesCountByDepartment() {
        java.util.Map<String, Integer> departmentCounts = new java.util.HashMap<>();
        String sql = "SELECT d.dept_name, COUNT(e.emp_id) AS employee_count FROM employees e JOIN departments d ON e.dept_id = d.dept_id GROUP BY d.dept_name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                departmentCounts.put(rs.getString("dept_name"), rs.getInt("employee_count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departmentCounts;
    }

    // =====================================
    // 🔹 GET NEW HIRES COUNT LAST MONTH
    // =====================================
    public int getNewHiresCountLastMonth() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM employees WHERE hire_date >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)";
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

    // =====================================
    // 🔹 DELETE EMPLOYEE
    // =====================================
    public void deleteEmployee(int empId) {

        String getUserSQL = "SELECT user_id FROM employees WHERE emp_id=?";
        String deleteSalarySQL = "DELETE FROM salary_structure WHERE emp_id=?";
        String deleteLeaveSQL = "DELETE FROM leave_requests WHERE emp_id=?";
        String deleteEmployeeSQL = "DELETE FROM employees WHERE emp_id=?";
        String deleteUserSQL = "DELETE FROM users WHERE id=?";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            PreparedStatement ps1 = con.prepareStatement(getUserSQL);
            ps1.setInt(1, empId);
            ResultSet rs = ps1.executeQuery();

            int userId = 0;
            if (rs.next()) userId = rs.getInt("user_id");

            PreparedStatement ps2 = con.prepareStatement(deleteSalarySQL);
            ps2.setInt(1, empId);
            ps2.executeUpdate();

            PreparedStatement ps3 = con.prepareStatement(deleteLeaveSQL);
            ps3.setInt(1, empId);
            ps3.executeUpdate();

            PreparedStatement ps4 = con.prepareStatement(deleteEmployeeSQL);
            ps4.setInt(1, empId);
            ps4.executeUpdate();

            PreparedStatement ps5 = con.prepareStatement(deleteUserSQL);
            ps5.setInt(1, userId);
            ps5.executeUpdate();

            con.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 // =====================================
 // 🔹 UPDATE EMPLOYEE
 // =====================================
    // =====================================
    // 🔹 UPDATE EMPLOYEE
    // =====================================
    public boolean updateEmployee(Employee emp) { // Changed return type to boolean
        Connection con = null;
        boolean success = false;

        String updateEmployeeSQL = """
            UPDATE employees
            SET first_name=?, last_name=?, email=?, address=?, photo=?
            WHERE emp_id=?
        """;

        String updateSalarySQL = """
            UPDATE salary_structure
            SET basic_salary=?, hra=?, da=?, allowances=?, deductions=?
            WHERE emp_id=?
        """;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // Start transaction

            // Update employees table
            try (PreparedStatement ps1 = con.prepareStatement(updateEmployeeSQL)) {
                ps1.setString(1, emp.getFirstName());
                ps1.setString(2, emp.getLastName());
                ps1.setString(3, emp.getEmail());
                ps1.setString(4, emp.getAddress());
                ps1.setString(5, emp.getPhoto()); // Correctly set photo as 5th parameter
                ps1.setInt(6, emp.getEmpId());   // Correctly set emp_id as 6th parameter (for WHERE clause)
                ps1.executeUpdate();
            }

            // Update salary_structure table
            try (PreparedStatement ps2 = con.prepareStatement(updateSalarySQL)) {
                ps2.setDouble(1, emp.getBasicSalary());
                ps2.setDouble(2, emp.getHra());
                ps2.setDouble(3, emp.getDa());
                ps2.setDouble(4, emp.getAllowances());
                ps2.setDouble(5, emp.getDeductions());
                ps2.setInt(6, emp.getEmpId());
                ps2.executeUpdate();
            }

            con.commit(); // Commit transaction if both updates succeed
            success = true;

        } catch (Exception e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return success; // Return true on success, false on failure
    }
}
