package com.payroll.filter;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        HttpSession session = req.getSession(false);

        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        // Allow login resources
        if (uri.contains("login") ||
            uri.contains("css") ||
            uri.contains("js")) {

            chain.doFilter(request, response);
            return;
        }

        // If not logged in
        if (session == null || session.getAttribute("username") == null) {
            res.sendRedirect("login.jsp");
            return;
        }

        String role = (String) session.getAttribute("role");

        // Admin-only pages
        if (uri.contains("viewEmployees") ||
            uri.contains("addEmployee") ||
            uri.contains("editEmployee") ||
            uri.contains("deleteEmployee")) {

            if (!"ADMIN".equals(role)) {
                res.sendRedirect("accessDenied.jsp");
                return;
            }
        }

        // Employee-only pages
        if (uri.contains("myProfile") ||
            uri.contains("mySalary")) {

            if (!"EMPLOYEE".equals(role)) {
                res.sendRedirect("accessDenied.jsp");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
