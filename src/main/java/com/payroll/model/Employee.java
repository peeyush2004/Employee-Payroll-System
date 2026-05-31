package com.payroll.model;

public class Employee {

    private int empId;
    private int userId;
    private int deptId;

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String hireDate;
    private String departmentName;

    private double basicSalary;
    private double hra;
    private double da;
    private double allowances;
    private double deductions;
    private String photo;
    // ✅ REQUIRED EMPTY CONSTRUCTOR
    public Employee() {
    }

    // ✅ FULL CONSTRUCTOR (WITH ADDRESS + USERNAME)
    public Employee(int empId, String username,
                    String firstName, String lastName,
                    String email, String departmentName,
                    String address,
                    double basicSalary, double hra,
                    double da, double allowances,
                    double deductions,
                    String photo
                    ) {

        this.empId = empId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.departmentName = departmentName;
        this.address = address;
        this.basicSalary = basicSalary;
        this.hra = hra;
        this.da = da;
        this.allowances = allowances;
        this.deductions = deductions;
        this.photo=photo;
        
    }

    // ================= GETTERS & SETTERS =================

    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getDeptId() { return deptId; }
    public void setDeptId(int deptId) { this.deptId = deptId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getHireDate() { return hireDate; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }

    public double getHra() { return hra; }
    public void setHra(double hra) { this.hra = hra; }

    public double getDa() { return da; }
    public void setDa(double da) { this.da = da; }

    public double getAllowances() { return allowances; }
    public void setAllowances(double allowances) { this.allowances = allowances; }

    public double getDeductions() { return deductions; }
    public void setDeductions(double deductions) { this.deductions = deductions; }
    
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
}