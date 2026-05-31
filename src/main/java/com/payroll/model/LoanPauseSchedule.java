package com.payroll.model;

import java.time.YearMonth;

public class LoanPauseSchedule {

    private int id;
    private int loanId;
    private YearMonth pauseMonth;
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public YearMonth getPauseMonth() {
        return pauseMonth;
    }

    public void setPauseMonth(YearMonth pauseMonth) {
        this.pauseMonth = pauseMonth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
