package com.payroll.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/employeePhoto")
public class EmployeePhotoServlet extends HttpServlet {
    private static final String EXTERNAL_UPLOAD_BASE_DIR = "D:\\payroll_uploads";
    private static final String APP_UPLOAD_SUBDIR = "EmployeePayrollSystem" + File.separator + "uploads";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fileName = request.getParameter("file");
        if (fileName == null || fileName.isBlank() || fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        File photoFile = resolvePhotoFile(request, fileName);
        if (photoFile == null || !photoFile.exists() || !photoFile.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String contentType = getServletContext().getMimeType(photoFile.getName());
        response.setContentType(contentType != null ? contentType : "application/octet-stream");
        response.setContentLengthLong(photoFile.length());
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        try (InputStream in = new FileInputStream(photoFile);
             OutputStream out = response.getOutputStream()) {
            in.transferTo(out);
        }
    }

    private File resolvePhotoFile(HttpServletRequest request, String fileName) {
        File externalFile = new File(EXTERNAL_UPLOAD_BASE_DIR + File.separator + APP_UPLOAD_SUBDIR, fileName);
        if (externalFile.exists()) {
            return externalFile;
        }

        String webAppUploads = request.getServletContext().getRealPath("/uploads");
        if (webAppUploads != null) {
            File webAppFile = new File(webAppUploads, fileName);
            if (webAppFile.exists()) {
                return webAppFile;
            }
        }

        File sourceUploadsFile = new File("src" + File.separator + "main" + File.separator + "webapp" + File.separator + "uploads", fileName);
        if (sourceUploadsFile.exists()) {
            return sourceUploadsFile;
        }

        return null;
    }
}
