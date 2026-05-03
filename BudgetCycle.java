package com.example.demo7;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BudgetCycle {
    private double totalAllowance;
    private LocalDate startDate;
    private LocalDate endDate;
    public double remainingBalance;



    public BudgetCycle(double totalAllowance, LocalDate startDate, LocalDate endDate) {
        this.totalAllowance = totalAllowance;
        this.remainingBalance = totalAllowance;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public double calculateDailyLimit() {
        long daysLeft= ChronoUnit.DAYS.between(LocalDate.now(), endDate);

        if (daysLeft <= 0) {
            return remainingBalance;
        }
        return remainingBalance / daysLeft;
    }

    public void updateBalance(double amount) {
        remainingBalance -= amount;
    }

    public double getRemainingBalance(){
        return remainingBalance;
    }
    public double getTotalAllowance(){
        return totalAllowance;
    }

}
