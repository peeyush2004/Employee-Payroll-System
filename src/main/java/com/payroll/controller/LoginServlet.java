package com.payroll.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

import com.payroll.dao.UserDAO;
import com.payroll.model.LoginResult;
import com.payroll.model.User;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String loginRole = request.getParameter("loginRole");

        UserDAO dao = new UserDAO();
        LoginResult loginResult = dao.authenticate(username, password, loginRole);
        User user = loginResult.getUser();

        if (user != null) {

            HttpSession session = request.getSession();
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());
            session.setAttribute("userId", user.getUserId());

            response.sendRedirect("dashboard");

        } else {
            request.setAttribute("error", loginResult.getErrorMessage());
            request.setAttribute("selectedRole", loginRole);
            request.setAttribute("enteredUsername", username);
            request.getRequestDispatcher("login.jsp")
                    .forward(request, response);
        }
    }
}
