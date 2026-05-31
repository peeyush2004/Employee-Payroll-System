package com.payroll.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.payroll.model.LoginResult;
import com.payroll.model.User;
import com.payroll.util.DBConnection;
import com.payroll.util.PasswordUtil;

public class UserDAO {

    public User login(String username, String password) {
        LoginResult result = authenticate(username, password, null);
        return result.getUser();
    }

    public LoginResult authenticate(String username, String password, String role) {
        LoginResult result = new LoginResult();

        String lookupSql = """
            SELECT u.id, u.username, u.password, u.role,
                   CASE WHEN e.user_id IS NOT NULL THEN 1 ELSE 0 END AS employee_present
            FROM users u
            LEFT JOIN employees e ON e.user_id = u.id
            WHERE u.username = ?
              AND (? IS NULL OR u.role = ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(lookupSql)) {

            ps.setString(1, username);
            ps.setString(2, role);
            ps.setString(3, role);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                result.setErrorMessage("EMPLOYEE".equalsIgnoreCase(role)
                        ? "Employee not present in the system."
                        : "Incorrect username.");
                return result;
            }

            if ("EMPLOYEE".equalsIgnoreCase(role) && rs.getInt("employee_present") == 0) {
                result.setErrorMessage("Employee not present in the system.");
                return result;
            }

            String storedPassword = rs.getString("password");
            if (storedPassword == null || !PasswordUtil.verifyPassword(password, storedPassword)) {
                result.setErrorMessage("Incorrect password.");
                return result;
            }

            result.setUser(new User(
                rs.getInt("id"),
                rs.getString("username"),
                storedPassword,
                rs.getString("role")
            ));

        } catch (Exception e) {
            e.printStackTrace();
            result.setErrorMessage("Unable to login right now.");
        }

        return result;
    }
}
