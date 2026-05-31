package com.payroll.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.payroll.model.LoanSchedulePreview;

public class EMICalculationService {

    public static final double DEFAULT_ANNUAL_INTEREST_RATE = 5.0;
    private static final int CURRENCY_SCALE = 2;

    public LoanSchedulePreview calculatePreview(double principal, int months) {
        return calculatePreview(principal, months, DEFAULT_ANNUAL_INTEREST_RATE);
    }

    public LoanSchedulePreview calculatePreview(double principal, int months, double annualInterestRate) {
        if (!isValidInput(principal, annualInterestRate, months)) {
            return new LoanSchedulePreview(0, 0);
        }

        double monthlyEmi = calculateEMI(principal, annualInterestRate, months);
        double totalRepayment = calculateTotalRepayment(monthlyEmi, months);

        return new LoanSchedulePreview(monthlyEmi, totalRepayment);
    }

    public double calculateEMI(double principal, double annualInterestRate, int months) {
        if (!isValidInput(principal, annualInterestRate, months)) {
            return 0;
        }

        double monthlyRate = annualInterestRate / (12.0 * 100.0);
        if (monthlyRate == 0) {
            return roundCurrency(principal / months);
        }

        double factor = Math.pow(1 + monthlyRate, months);
        double emi = (principal * monthlyRate * factor) / (factor - 1);
        return roundCurrency(emi);
    }

    public double calculateTotalRepayment(double monthlyEmi, int months) {
        if (monthlyEmi <= 0 || months <= 0) {
            return 0;
        }
        return roundCurrency(BigDecimal.valueOf(monthlyEmi)
                .multiply(BigDecimal.valueOf(months))
                .doubleValue());
    }

    private boolean isValidInput(double principal, double annualInterestRate, int months) {
        return Double.isFinite(principal)
                && Double.isFinite(annualInterestRate)
                && principal > 0
                && annualInterestRate >= 0
                && months > 0;
    }

    public double roundCurrency(double value) {
        return BigDecimal.valueOf(value).setScale(CURRENCY_SCALE, RoundingMode.HALF_UP).doubleValue();
    }
}
